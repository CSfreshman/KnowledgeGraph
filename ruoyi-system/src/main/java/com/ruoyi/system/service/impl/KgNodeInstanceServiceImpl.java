package com.ruoyi.system.service.impl;

import java.util.List;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.system.domain.KgHistory;
import com.ruoyi.system.domain.KgNodeInstanceProperties;
import com.ruoyi.system.mapper.KgEdgeInstanceMapper;
import com.ruoyi.system.mapper.KgNodeInstancePropertiesMapper;
import com.ruoyi.system.service.IKgHistoryService;
import com.ruoyi.system.service.IKgNodeInstancePropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.KgNodeInstanceMapper;
import com.ruoyi.system.domain.KgNodeInstance;
import com.ruoyi.system.service.IKgNodeInstanceService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author ruoyi
 * @date 2024-03-16
 */
@Service
public class KgNodeInstanceServiceImpl implements IKgNodeInstanceService
{
    @Autowired
    private KgNodeInstanceMapper kgNodeInstanceMapper;
    @Autowired
    private KgNodeInstancePropertiesMapper kgNodeInstancePropertiesMapper;
    @Autowired
    private KgEdgeInstanceMapper edgeInstanceMapper;
    @Autowired
    private IKgHistoryService historyService;
    @Autowired
    private IKgNodeInstancePropertiesService nodeInstancePropertiesService;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public KgNodeInstance selectKgNodeInstanceById(Long id)
    {
        return kgNodeInstanceMapper.selectKgNodeInstanceById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param kgNodeInstance 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<KgNodeInstance> selectKgNodeInstanceList(KgNodeInstance kgNodeInstance)
    {
        List<KgNodeInstance> kgNodeInstances = kgNodeInstanceMapper.selectKgNodeInstanceList(kgNodeInstance);
        for (KgNodeInstance instance : kgNodeInstances) {
            KgNodeInstanceProperties prop = new KgNodeInstanceProperties();
            prop.setNodeId(instance.getId());
            prop.setValid(1L);
            instance.setPropsList(kgNodeInstancePropertiesMapper.selectKgNodeInstancePropertiesList(prop));
        }
        return kgNodeInstances;
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param kgNodeInstance 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertKgNodeInstance(KgNodeInstance kgNodeInstance)
    {
        kgNodeInstance.setCreateUser(SecurityUtils.getUserId());
        kgNodeInstance.setCreateTime(DateUtils.getNowDate());
        if(ObjectUtil.isNull(kgNodeInstance.getId())){
            kgNodeInstance.setId(IdUtil.getSnowflakeNextId());
        }

        kgNodeInstance.setValid(1L);
        return kgNodeInstanceMapper.insertKgNodeInstance(kgNodeInstance);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param kgNodeInstance 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateKgNodeInstance(KgNodeInstance kgNodeInstance)
    {
        return kgNodeInstanceMapper.updateKgNodeInstance(kgNodeInstance);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteKgNodeInstanceByIds(Long[] ids)
    {
        return kgNodeInstanceMapper.deleteKgNodeInstanceByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteKgNodeInstanceById(Long id)
    {
        return kgNodeInstanceMapper.deleteKgNodeInstanceById(id);
    }

    // 根据neo4jId删除节点
    @Override
    public Integer deleteNodeInstanceByNeo4jId(Long nodeId) {
        KgNodeInstance instance = new KgNodeInstance();
        instance.setNeo4jId(nodeId);
        List<KgNodeInstance> kgNodeInstances = selectKgNodeInstanceList(instance);
        if(ObjectUtil.isEmpty(kgNodeInstances)){
            return 0;
        }
        KgNodeInstance instance1 = kgNodeInstances.get(0);
        // 逻辑删除
        instance1.setValid(0l);
        updateKgNodeInstance(instance1);

        KgHistory history = new KgHistory();
        history.setType(2);
        history.setTargetType(3);
        history.setTargetId(instance1.getId());
        history.setTargetName(instance1.getName());
        historyService.insertKgHistory(history);

        // 删除节点属性
        System.out.println("删除节点属性");
        int count1 = nodeInstancePropertiesService.deleteByNodeId(instance1.getId());
        return 1;
    }
}
