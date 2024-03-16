package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.KgNodeInstancePropertiesMapper;
import com.ruoyi.system.domain.KgNodeInstanceProperties;
import com.ruoyi.system.service.IKgNodeInstancePropertiesService;

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
}
