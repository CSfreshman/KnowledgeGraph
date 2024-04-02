package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.KgHistory;
import com.ruoyi.system.mapper.KgHistoryMapper;
import com.ruoyi.system.req.GraphReq;
import com.ruoyi.system.service.IKgHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.KgNodeInstancePropertiesMapper;
import com.ruoyi.system.domain.KgNodeInstanceProperties;
import com.ruoyi.system.service.IKgNodeInstancePropertiesService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author ruoyi
 * @date 2024-03-16
 */
@Service
public class KgNodeInstancePropertiesServiceImpl implements IKgNodeInstancePropertiesService
{
    @Autowired
    private KgNodeInstancePropertiesMapper kgNodeInstancePropertiesMapper;

    @Autowired
    private IKgHistoryService historyService;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public KgNodeInstanceProperties selectKgNodeInstancePropertiesById(Long id)
    {
        return kgNodeInstancePropertiesMapper.selectKgNodeInstancePropertiesById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param kgNodeInstanceProperties 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<KgNodeInstanceProperties> selectKgNodeInstancePropertiesList(KgNodeInstanceProperties kgNodeInstanceProperties)
    {
        return kgNodeInstancePropertiesMapper.selectKgNodeInstancePropertiesList(kgNodeInstanceProperties);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param kgNodeInstanceProperties 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertKgNodeInstanceProperties(KgNodeInstanceProperties kgNodeInstanceProperties)
    {
        kgNodeInstanceProperties.setCreateTime(DateUtils.getNowDate());
        kgNodeInstanceProperties.setCreateUser(SecurityUtils.getUserId());
        kgNodeInstanceProperties.setValid(1L);
        return kgNodeInstancePropertiesMapper.insertKgNodeInstanceProperties(kgNodeInstanceProperties);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param kgNodeInstanceProperties 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateKgNodeInstanceProperties(KgNodeInstanceProperties kgNodeInstanceProperties)
    {
        return kgNodeInstancePropertiesMapper.updateKgNodeInstanceProperties(kgNodeInstanceProperties);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteKgNodeInstancePropertiesByIds(Long[] ids)
    {
        return kgNodeInstancePropertiesMapper.deleteKgNodeInstancePropertiesByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteKgNodeInstancePropertiesById(Long id)
    {
        return kgNodeInstancePropertiesMapper.deleteKgNodeInstancePropertiesById(id);
    }

    // 根据节点的neo4jId修改节点属性
    @Override
    @Transactional
    public int updateByNodeNeo4jId(GraphReq req){
        List<KgNodeInstanceProperties> byNodeNeo4jId = kgNodeInstancePropertiesMapper.getByNodeNeo4jId(req.getNodeId());
        System.out.println(byNodeNeo4jId);

        // 所有需要修改的记录
        List<KgNodeInstanceProperties> updateList = byNodeNeo4jId.stream().filter(it -> req.getProps().keySet().contains(it.getName())).collect(Collectors.toList());
        System.out.println(updateList);

        int count = 0;
        for (KgNodeInstanceProperties properties : updateList) {
            // 历史记录
            KgHistory history = new KgHistory();
            history.setType(3);
            history.setTargetType(4);
            history.setTargetId(properties.getId());
            history.setTargetName(properties.getName());
            history.setOriginValue(properties.getValue());
            history.setCurValue(req.getProps().get(properties.getName()).toString());
            historyService.insertKgHistory(history);

            // 更新原始属性记录
            properties.setValid(0l);
            kgNodeInstancePropertiesMapper.updateKgNodeInstanceProperties(properties);

            // 新增一条新的记录
            properties.setValue(req.getProps().get(properties.getName()).toString());
            properties.setId(IdUtil.getSnowflakeNextId());
            insertKgNodeInstanceProperties(properties);
            count++;

        }
        return count;
    }


    @Override
    public int deleteByNodeId(Long nodeId){
        KgNodeInstanceProperties properties = new KgNodeInstanceProperties();
        properties.setNodeId(nodeId);

        List<KgNodeInstanceProperties> propertiesList = selectKgNodeInstancePropertiesList(properties);
        if(ObjectUtil.isEmpty(propertiesList)){
            return 0;
        }
        for (KgNodeInstanceProperties item : propertiesList) {
            item.setValid(0l);
            updateKgNodeInstanceProperties(item);

            KgHistory history = new KgHistory();
            history.setType(2);
            history.setTargetType(4);
            history.setTargetId(item.getId());
            history.setTargetName(item.getName());
            historyService.insertKgHistory(history);
        }

        return propertiesList.size();

    }
}
