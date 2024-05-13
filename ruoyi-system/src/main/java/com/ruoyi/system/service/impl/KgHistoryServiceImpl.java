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
 * Service业务层处理
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
     * 查询
     *
     * @param id 主键
     * @return 
     */
    @Override
    public KgHistory selectKgHistoryById(Long id)
    {
        return kgHistoryMapper.selectKgHistoryById(id);
    }

    /**
     * 查询列表
     *
     * @param kgHistory 
     * @return 
     */
    @Override
    public List<KgHistory> selectKgHistoryList(KgHistory kgHistory)
    {
        return kgHistoryMapper.selectKgHistoryList(kgHistory);
    }

    /**
     * 新增
     *
     * @param kgHistory 
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
     * 修改
     *
     * @param kgHistory 
     * @return 结果
     */
    @Override
    public int updateKgHistory(KgHistory kgHistory)
    {
        return kgHistoryMapper.updateKgHistory(kgHistory);
    }

    /**
     * 批量删除
     *
     * @param ids 需要删除的主键
     * @return 结果
     */
    @Override
    public int deleteKgHistoryByIds(Long[] ids)
    {
        return kgHistoryMapper.deleteKgHistoryByIds(ids);
    }

    /**
     * 删除信息
     *
     * @param id 主键
     * @return 结果
     */
    @Override
    public int deleteKgHistoryById(Long id)
    {
        return kgHistoryMapper.deleteKgHistoryById(id);
    }
}
