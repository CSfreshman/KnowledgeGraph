package com.ruoyi.system.service.impl;

import java.util.List;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.TestNeo4jService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.service.IKgEdgeClassPropertiesService;

import javax.annotation.Resource;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author ruoyi
 * @date 2024-03-08
 */
@Service
public class KgEdgeClassPropertiesServiceImpl implements IKgEdgeClassPropertiesService
{
    @Autowired
    private KgEdgeClassPropertiesMapper kgEdgeClassPropertiesMapper;
    @Autowired
    private KgHistoryServiceImpl historyService;
    @Resource
    private KgEdgeInstanceMapper edgeInstanceMapper;
    @Resource
    private KgEdgeInstacePropertiesMapper edgeInstacePropertiesMapper;
    @Resource
    private TestNeo4jService neo4jService;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public KgEdgeClassProperties selectKgEdgeClassPropertiesById(Long id)
    {
        return kgEdgeClassPropertiesMapper.selectKgEdgeClassPropertiesById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param kgEdgeClassProperties 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<KgEdgeClassProperties> selectKgEdgeClassPropertiesList(KgEdgeClassProperties kgEdgeClassProperties)
    {
        return kgEdgeClassPropertiesMapper.selectKgEdgeClassPropertiesList(kgEdgeClassProperties);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param kgEdgeClassProperties 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertKgEdgeClassProperties(KgEdgeClassProperties kgEdgeClassProperties)
    {

        KgEdgeClassProperties test = new KgEdgeClassProperties();
        test.setName(kgEdgeClassProperties.getName());
        test.setEdgeId(kgEdgeClassProperties.getEdgeId());
        test.setValid(1l);
        if(ObjectUtil.isNotEmpty(selectKgEdgeClassPropertiesList(test))){
            throw new RuntimeException("属性名称重复");
        }

        kgEdgeClassProperties.setCreateTime(DateUtils.getNowDate());
        kgEdgeClassProperties.setId(IdUtil.getSnowflakeNextId());
        kgEdgeClassProperties.setCreateUser(SecurityUtils.getUserId());

        // 历史记录
        KgHistory history = new KgHistory();
        // 新增
        history.setType(1);
        history.setTargetType(6);
        history.setTargetId(kgEdgeClassProperties.getId());
        history.setTargetName(kgEdgeClassProperties.getName());
        historyService.insertKgHistory(history);

        // 类型新增属性，需要同时给该类型的全部实例新增该属性
        KgEdgeInstance instance = new KgEdgeInstance();
        instance.setClassId(kgEdgeClassProperties.getEdgeId());
        instance.setValid(1l);
        List<KgEdgeInstance> edgeInstances = edgeInstanceMapper.selectKgEdgeInstanceList(instance);
        for (KgEdgeInstance edgeInstance : edgeInstances) {
            KgEdgeInstaceProperties properties = new KgEdgeInstaceProperties();
            properties.setValid(1l);
            properties.setId(IdUtil.getSnowflakeNextId());
            properties.setName(kgEdgeClassProperties.getName());
            properties.setEdgeId(edgeInstance.getId());
            properties.setValue("default");
            properties.setCreateTime(DateUtils.getNowDate());
            properties.setCreateUser(SecurityUtils.getUserId());
            edgeInstacePropertiesMapper.insertKgEdgeInstaceProperties(properties);

            neo4jService.insertPropByEdgeId(edgeInstance.getNeo4jId(),properties.getName(),properties.getValue());
//            neo4jService.removePropByNodeId(edgeInstance.getNeo4jId(),properties.getName(),"");
        }


        return kgEdgeClassPropertiesMapper.insertKgEdgeClassProperties(kgEdgeClassProperties);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param kgEdgeClassProperties 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateKgEdgeClassProperties(KgEdgeClassProperties kgEdgeClassProperties)
    {
        return kgEdgeClassPropertiesMapper.updateKgEdgeClassProperties(kgEdgeClassProperties);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteKgEdgeClassPropertiesByIds(Long[] ids)
    {
        int count = 0;
        for (Long id : ids) {
            count+=deleteKgEdgeClassPropertiesById(id);
        }
        return count;
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteKgEdgeClassPropertiesById(Long id)
    {
        KgEdgeClassProperties kgEdgeClassProperties = selectKgEdgeClassPropertiesById(id);
        // 置为失效,设置修改时间和修改人
        kgEdgeClassProperties.setValid(0L);
        kgEdgeClassProperties.setModityTime(DateUtils.getNowDate());
        kgEdgeClassProperties.setModityUser(SecurityUtils.getUserId());
        kgEdgeClassProperties.setModityType(0L);


        // 历史记录
        KgHistory history = new KgHistory();
        // 删除
        history.setType(2);
        history.setTargetType(6);
        history.setTargetId(kgEdgeClassProperties.getId());
        history.setTargetName(kgEdgeClassProperties.getName());
        historyService.insertKgHistory(history);

        // 类型新增属性，需要同时给该类型的全部实例新增该属性
        KgEdgeInstance instance = new KgEdgeInstance();
        instance.setClassId(kgEdgeClassProperties.getEdgeId());
        instance.setValid(1l);
        List<KgEdgeInstance> edgeInstances = edgeInstanceMapper.selectKgEdgeInstanceList(instance);
        for (KgEdgeInstance edgeInstance : edgeInstances) {
            KgEdgeInstaceProperties properties = new KgEdgeInstaceProperties();
            properties.setEdgeId(edgeInstance.getId());
            properties.setName(kgEdgeClassProperties.getName());
            properties.setValid(1l);
            List<KgEdgeInstaceProperties> kgEdgeInstaceProperties = edgeInstacePropertiesMapper.selectKgEdgeInstacePropertiesList(properties);
            for (KgEdgeInstaceProperties kgEdgeInstaceProperty : kgEdgeInstaceProperties) {
                kgEdgeInstaceProperty.setValid(0l);
                edgeInstacePropertiesMapper.updateKgEdgeInstaceProperties(kgEdgeInstaceProperty);

            }

            neo4jService.removePropByEdgeId(edgeInstance.getNeo4jId(),properties.getName(),"");
        }



        return updateKgEdgeClassProperties(kgEdgeClassProperties);
    }
}
