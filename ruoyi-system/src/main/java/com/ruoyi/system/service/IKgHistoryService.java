package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.KgHistory;

/**
 * Service接口
 * 
 * @author ruoyi
 * @date 2024-04-01
 */
public interface IKgHistoryService 
{
    /**
     * 查询
     * 
     * @param id 主键
     * @return 
     */
    public KgHistory selectKgHistoryById(Long id);

    /**
     * 查询列表
     * 
     * @param kgHistory 
     * @return 集合
     */
    public List<KgHistory> selectKgHistoryList(KgHistory kgHistory);

    /**
     * 新增
     * 
     * @param kgHistory 
     * @return 结果
     */
    public int insertKgHistory(KgHistory kgHistory);

    /**
     * 修改
     * 
     * @param kgHistory 
     * @return 结果
     */
    public int updateKgHistory(KgHistory kgHistory);

    /**
     * 批量删除
     * 
     * @param ids 需要删除的主键集合
     * @return 结果
     */
    public int deleteKgHistoryByIds(Long[] ids);

    /**
     * 删除信息
     * 
     * @param id 主键
     * @return 结果
     */
    public int deleteKgHistoryById(Long id);
}
