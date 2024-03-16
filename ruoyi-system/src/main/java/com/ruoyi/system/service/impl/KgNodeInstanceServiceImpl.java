package com.ruoyi.system.service.impl;

import java.util.List;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.system.domain.KgNodeInstanceProperties;
import com.ruoyi.system.mapper.KgNodeInstancePropertiesMapper;
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
}
