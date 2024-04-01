package com.ruoyi.system.service.impl;

import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.KgHistoryMapper;
import com.ruoyi.system.domain.KgHistory;
import com.ruoyi.system.service.IKgHistoryService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author ruoyi
 * @date 2024-04-01
 */
@Service
public class KgHistoryServiceImpl implements IKgHistoryService
{
    @Autowired
    private KgHistoryMapper kgHistoryMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public KgHistory selectKgHistoryById(Long id)
    {
        return kgHistoryMapper.selectKgHistoryById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param kgHistory 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<KgHistory> selectKgHistoryList(KgHistory kgHistory)
    {
        return kgHistoryMapper.selectKgHistoryList(kgHistory);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param kgHistory 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertKgHistory(KgHistory kgHistory)
    {
        kgHistory.setTime(DateUtils.getNowDate());
        kgHistory.setId(cn.hutool.core.util.IdUtil.getSnowflakeNextId());
        kgHistory.setUserId(SecurityUtils.getUserId());
        return kgHistoryMapper.insertKgHistory(kgHistory);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param kgHistory 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateKgHistory(KgHistory kgHistory)
    {
        return kgHistoryMapper.updateKgHistory(kgHistory);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteKgHistoryByIds(Long[] ids)
    {
        return kgHistoryMapper.deleteKgHistoryByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteKgHistoryById(Long id)
    {
        return kgHistoryMapper.deleteKgHistoryById(id);
    }
}
