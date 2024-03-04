package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.KgNodeClassPropertiesMapper;
import com.ruoyi.system.domain.KgNodeClassProperties;
import com.ruoyi.system.service.IKgNodeClassPropertiesService;

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
        kgNodeClassProperties.setCreateTime(DateUtils.getNowDate());
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
        return kgNodeClassPropertiesMapper.deleteKgNodeClassPropertiesByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteKgNodeClassPropertiesById(Long id)
    {
        return kgNodeClassPropertiesMapper.deleteKgNodeClassPropertiesById(id);
    }
}
