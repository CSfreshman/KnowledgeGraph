package com.ruoyi.system.service.impl;

import java.util.List;

import cn.hutool.core.util.IdUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.KgNodeClassProperties;
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
        kgEdgeClassProperties.setId(IdUtil.getSnowflakeNextId());
        kgEdgeClassProperties.setCreateUser(SecurityUtils.getUserId());
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
        return updateKgEdgeClassProperties(kgEdgeClassProperties);
    }
}
