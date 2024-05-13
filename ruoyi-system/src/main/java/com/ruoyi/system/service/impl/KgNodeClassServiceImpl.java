package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.logging.Handler;
import java.util.stream.Collectors;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.mapper.KgNodeClassPropertiesMapper;
import com.ruoyi.system.myEnum.HistoryEnum;
import com.ruoyi.system.req.GraphReq;
import com.ruoyi.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.KgNodeClassMapper;

/**
 * Service业务层处理
 *
 * @author ruoyi
 * @date 2024-03-04
 */
@Slf4j
@Service
public class KgNodeClassServiceImpl implements IKgNodeClassService
{
    @Autowired
    private KgNodeClassMapper kgNodeClassMapper;
    @Autowired
    private KgNodeClassPropertiesMapper propertiesMapper;
    @Autowired
    private KgHistoryServiceImpl historyService;
    @Autowired
    private KgNodeClassPropertiesMapper kgNodeClassPropertiesMapper;
    @Autowired
    private IKgNodeClassPropertiesService kgNodeClassPropertiesService;
    @Autowired
    private IKgNodeInstanceService kgNodeInstanceService;
    @Autowired
    private TestNeo4jService neo4jService;
    @Autowired
    private IKgEdgeClassService kgEdgeClassService;

    /**
     * 查询
     *
     * @param id 主键
     * @return 
     */
    @Override
    public KgNodeClass selectKgNodeClassById(Long id)
    {
        return kgNodeClassMapper.selectKgNodeClassById(id);
    }

    /**
     * 查询列表
     *
     * @param kgNodeClass 
     * @return 
     */
    @Override
    public List<KgNodeClass> selectKgNodeClassList(KgNodeClass kgNodeClass)
    {
        List<KgNodeClass> kgNodeClasses = kgNodeClassMapper.selectKgNodeClassList(kgNodeClass);
        for (KgNodeClass nodeClass : kgNodeClasses) {
            KgNodeClassProperties req = new KgNodeClassProperties();
            req.setValid(1);
            req.setNodeId(nodeClass.getId());
            nodeClass.setProps(propertiesMapper.selectKgNodeClassPropertiesList(req));
        }
        return kgNodeClasses;
    }

    /**
     * 新增
     *
     * @param kgNodeClass 
     * @return 结果
     */
    @Override
    public int insertKgNodeClass(KgNodeClass kgNodeClass)
    {

        KgNodeClass test = new KgNodeClass();
        test.setName(kgNodeClass.getName());
        test.setValid(1l);
        List<KgNodeClass> kgNodeClasses = selectKgNodeClassList(test);
        if(ObjectUtil.isNotEmpty(kgNodeClasses)){
            throw new RuntimeException("名称重复");
        }


        log.info(kgNodeClass.toString());
        kgNodeClass.setCreateTime(DateUtils.getNowDate());
        kgNodeClass.setId(cn.hutool.core.util.IdUtil.getSnowflakeNextId());
        kgNodeClass.setValid(1L);
        kgNodeClass.setCreateUser(SecurityUtils.getUserId());

        KgHistory history = new KgHistory();
        // 新增
        history.setType(1);
        history.setTargetType(1);
        history.setTargetId(kgNodeClass.getId());
        history.setTargetName(kgNodeClass.getName());
        historyService.insertKgHistory(history);

        return kgNodeClassMapper.insertKgNodeClass(kgNodeClass);
    }

    /**
     * 修改
     *
     * @param kgNodeClass 
     * @return 结果
     */
    @Override
    public int updateKgNodeClass(KgNodeClass kgNodeClass)
    {
        return kgNodeClassMapper.updateKgNodeClass(kgNodeClass);
    }

    /**
     * 批量删除
     *
     * @param ids 需要删除的主键
     * @return 结果
     */
    @Override
    public int deleteKgNodeClassByIds(Long[] ids)
    {
        if(ObjectUtil.isEmpty(ids)){
            return 0;
        }
        int count = 0;
        for (Long id : ids) {
            count+=deleteKgNodeClassById(id);
        }
        return count;
    }

    /**
     * 删除信息
     *
     * @param id 主键
     * @return 结果
     */
    @Override
    public int deleteKgNodeClassById(Long id)
    {
        KgNodeClass nodeClass = selectKgNodeClassById(id);
        nodeClass.setValid(0l);
        updateKgNodeClass(nodeClass);

        // 历史记录
        KgHistory history = new KgHistory();
        history.setType(2);
        history.setTargetType(1);
        history.setTargetId(nodeClass.getId());
        history.setTargetName(nodeClass.getName());
        historyService.insertKgHistory(history);

        // 删除属性
        KgNodeClassProperties nodeClassProperties = new KgNodeClassProperties();
        nodeClassProperties.setNodeId(nodeClass.getId());
        nodeClassProperties.setValid(1);
        List<KgNodeClassProperties> kgNodeClassProperties = kgNodeClassPropertiesMapper.selectKgNodeClassPropertiesList(nodeClassProperties);
        List<Long> collectIds = kgNodeClassProperties.stream().map(it -> it.getId()).collect(Collectors.toList());
        kgNodeClassPropertiesService.deleteKgNodeClassPropertiesByIds(collectIds.toArray(new Long[0]));

        // 删除以该类型为起点或终点的关系类型
        KgEdgeClass edgeClass = new KgEdgeClass();
        KgEdgeClass edgeClass1 = new KgEdgeClass();
        edgeClass.setValid(1l);
        edgeClass1.setValid(1l);
        edgeClass.setFromNodeId(id);
        edgeClass1.setFromNodeId(id);
        List<KgEdgeClass> kgEdgeClasses = kgEdgeClassService.selectKgEdgeClassList(edgeClass);
        List<KgEdgeClass> kgEdgeClasses1 = kgEdgeClassService.selectKgEdgeClassList(edgeClass1);
        kgEdgeClasses.addAll(kgEdgeClasses1);
        Long[] longs = kgEdgeClasses.stream().map(it -> it.getId()).collect(Collectors.toList()).toArray(new Long[0]);
        kgEdgeClassService.deleteKgEdgeClassByIds(longs);

        // 删除该类型所有的实例
        KgNodeInstance instance = new KgNodeInstance();
        instance.setClassId(id);
        instance.setValid(1l);
        List<KgNodeInstance> kgNodeInstances = kgNodeInstanceService.selectKgNodeInstanceList(instance);
        if(ObjectUtil.isNotEmpty(kgNodeInstances)){
            for (KgNodeInstance kgNodeInstance : kgNodeInstances) {
                // 删除neo4j实例和mysql实例
                GraphReq req = new GraphReq();
                req.setNodeId(kgNodeInstance.getNeo4jId());
                neo4jService.deleteNodeByNeo4jId(req);
            }
        }

        return kgNodeClassMapper.deleteKgNodeClassById(id);
    }

    @Override
    public List<KgNodeClass> getAll(KgNodeClass kgNodeClass) {
        List<KgNodeClass> kgNodeClasses = kgNodeClassMapper.selectKgNodeClassList(kgNodeClass);
        for (KgNodeClass nodeClass : kgNodeClasses) {
            KgNodeClassProperties req = new KgNodeClassProperties();
            req.setValid(1);
            req.setNodeId(nodeClass.getId());
            nodeClass.setProps(propertiesMapper.selectKgNodeClassPropertiesList(req));
        }
        return kgNodeClasses;
    }
}
