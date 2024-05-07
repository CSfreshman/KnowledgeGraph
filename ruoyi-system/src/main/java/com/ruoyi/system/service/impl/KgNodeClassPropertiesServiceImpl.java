package com.ruoyi.system.service.impl;

import java.util.List;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.KgHistory;
import com.ruoyi.system.domain.KgNodeInstance;
import com.ruoyi.system.domain.KgNodeInstanceProperties;
import com.ruoyi.system.mapper.KgNodeInstanceMapper;
import com.ruoyi.system.mapper.KgNodeInstancePropertiesMapper;
import com.ruoyi.system.service.TestNeo4jService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.KgNodeClassPropertiesMapper;
import com.ruoyi.system.domain.KgNodeClassProperties;
import com.ruoyi.system.service.IKgNodeClassPropertiesService;

import javax.annotation.Resource;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author ruoyi
 * @date 2024-03-04
 */
@Service
public class KgNodeClassPropertiesServiceImpl implements IKgNodeClassPropertiesService
{
    @Autowired
    private KgNodeClassPropertiesMapper kgNodeClassPropertiesMapper;
    @Autowired
    private KgHistoryServiceImpl historyService;
    @Resource
    private KgNodeInstanceMapper nodeInstanceMapper;
    @Resource
    private KgNodeInstancePropertiesMapper nodeInstancePropertiesMapper;
    @Resource
    private TestNeo4jService neo4jService;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public KgNodeClassProperties selectKgNodeClassPropertiesById(Long id)
    {
        return kgNodeClassPropertiesMapper.selectKgNodeClassPropertiesById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param kgNodeClassProperties 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<KgNodeClassProperties> selectKgNodeClassPropertiesList(KgNodeClassProperties kgNodeClassProperties)
    {
        return kgNodeClassPropertiesMapper.selectKgNodeClassPropertiesList(kgNodeClassProperties);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param kgNodeClassProperties 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertKgNodeClassProperties(KgNodeClassProperties kgNodeClassProperties)
    {

        KgNodeClassProperties test = new KgNodeClassProperties();
        test.setNodeId(kgNodeClassProperties.getNodeId());
        test.setName(kgNodeClassProperties.getName());
        test.setValid(1);
        if(ObjectUtil.isNotEmpty(selectKgNodeClassPropertiesList(test))){
            throw new RuntimeException("属性名重复");
        }

        kgNodeClassProperties.setCreateTime(DateUtils.getNowDate());
        kgNodeClassProperties.setId(IdUtil.getSnowflakeNextId());
        kgNodeClassProperties.setCreateUser(SecurityUtils.getUserId());

        KgHistory history = new KgHistory();
        // 新增
        history.setType(1);
        history.setTargetType(2);
        history.setTargetId(kgNodeClassProperties.getId());
        history.setTargetName(kgNodeClassProperties.getName());
        historyService.insertKgHistory(history);

        // 类型新增属性，需要同时给该类型的全部实例新增该属性
        KgNodeInstance instance = new KgNodeInstance();
        instance.setClassId(kgNodeClassProperties.getNodeId());
        instance.setValid(1l);
        List<KgNodeInstance> kgNodeInstances = nodeInstanceMapper.selectKgNodeInstanceList(instance);
        for (KgNodeInstance kgNodeInstance : kgNodeInstances) {
            // 构造属性
            KgNodeInstanceProperties properties = new KgNodeInstanceProperties();
            properties.setValid(1l);
            properties.setId(IdUtil.getSnowflakeNextId());
            properties.setName(kgNodeClassProperties.getName());
            properties.setNodeId(kgNodeInstance.getId());
            // 获得默认值
            properties.setValue(kgNodeClassProperties.getDefaultValue());
            properties.setCreateTime(DateUtils.getNowDate());
            properties.setCreateUser(SecurityUtils.getUserId());
            // 插入mysql数据库
            nodeInstancePropertiesMapper.insertKgNodeInstanceProperties(properties);

            // 插入neo4j
            neo4jService.insertPropByNodeId(kgNodeInstance.getNeo4jId(),properties.getName(),properties.getValue());
        }

        return kgNodeClassPropertiesMapper.insertKgNodeClassProperties(kgNodeClassProperties);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param kgNodeClassProperties 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateKgNodeClassProperties(KgNodeClassProperties kgNodeClassProperties)
    {
        return kgNodeClassPropertiesMapper.updateKgNodeClassProperties(kgNodeClassProperties);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteKgNodeClassPropertiesByIds(Long[] ids)
    {
        int count = 0;
        for (Long id : ids) {
            count+=deleteKgNodeClassPropertiesById(id);
        }
        return count;
    }

    @Override
    public int deleteKgNodeClassPropertiesById(Long id)
    {
//        return kgNodeClassPropertiesMapper.deleteKgNodeClassPropertiesById(id);
        KgNodeClassProperties kgNodeClassProperties = selectKgNodeClassPropertiesById(id);
        // 置为失效,设置修改时间和修改人
        kgNodeClassProperties.setValid(0);
        kgNodeClassProperties.setModifyTime(DateUtils.getNowDate());
        kgNodeClassProperties.setModifyUser(SecurityUtils.getUserId());
        kgNodeClassProperties.setModifyType(0L);

        // 历史记录
        KgHistory history = new KgHistory();
        // 删除
        history.setType(2);
        history.setTargetType(2);
        history.setTargetId(kgNodeClassProperties.getId());
        history.setTargetName(kgNodeClassProperties.getName());
        historyService.insertKgHistory(history);

        // 同时删除该类型实例的全部该属性
        // 类型新增属性，需要同时给该类型的全部实例新增该属性
        KgNodeInstance instance = new KgNodeInstance();
        instance.setClassId(kgNodeClassProperties.getNodeId());
        instance.setValid(1l);
        List<KgNodeInstance> kgNodeInstances = nodeInstanceMapper.selectKgNodeInstanceList(instance);
        for (KgNodeInstance kgNodeInstance : kgNodeInstances) {
            KgNodeInstanceProperties properties = new KgNodeInstanceProperties();
            properties.setNodeId(kgNodeInstance.getId());
            properties.setName(kgNodeClassProperties.getName());
            properties.setValid(1l);
            List<KgNodeInstanceProperties> propertiesList = nodeInstancePropertiesMapper.selectKgNodeInstancePropertiesList(properties);
            // 更新数据库
            for (KgNodeInstanceProperties kgNodeInstanceProperties : propertiesList) {
                kgNodeInstanceProperties.setValid(0l);
                nodeInstancePropertiesMapper.updateKgNodeInstanceProperties(kgNodeInstanceProperties);
            }

             // 更新neo4j
            neo4jService.removePropByNodeId(kgNodeInstance.getNeo4jId(),properties.getName(),"");
        }
        // 更新数据
        return updateKgNodeClassProperties(kgNodeClassProperties);

    }
}
