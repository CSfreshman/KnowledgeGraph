package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.KgHistory;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2024-04-01
 */
public interface KgHistoryMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public KgHistory selectKgHistoryById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param kgHistory 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<KgHistory> selectKgHistoryList(KgHistory kgHistory);

    /**
     * 新增【请填写功能名称】
     * 
     * @param kgHistory 【请填写功能名称】
     * @return 结果
     */
    public int insertKgHistory(KgHistory kgHistory);

    /**
     * 修改【请填写功能名称】
     * 
     * @param kgHistory 【请填写功能名称】
     * @return 结果
     */
    public int updateKgHistory(KgHistory kgHistory);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteKgHistoryById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteKgHistoryByIds(Long[] ids);
}
