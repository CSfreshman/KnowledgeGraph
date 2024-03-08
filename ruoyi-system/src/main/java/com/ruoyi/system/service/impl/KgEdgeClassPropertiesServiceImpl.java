package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.KgEdgeClassPropertiesMapper;
import com.ruoyi.system.domain.KgEdgeClassProperties;
import com.ruoyi.system.service.IKgEdgeClassPropertiesService;

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
        kgEdgeClassProperties.setCreateTime(DateUtils.getNowDate());
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
        return kgEdgeClassPropertiesMapper.deleteKgEdgeClassPropertiesByIds(ids);
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
        return kgEdgeClassPropertiesMapper.deleteKgEdgeClassPropertiesById(id);
    }
}
