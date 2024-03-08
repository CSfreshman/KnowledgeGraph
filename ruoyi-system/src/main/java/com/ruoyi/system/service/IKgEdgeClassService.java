package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.KgEdgeClass;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author ruoyi
 * @date 2024-03-08
 */
public interface IKgEdgeClassService 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public KgEdgeClass selectKgEdgeClassById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param kgEdgeClass 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<KgEdgeClass> selectKgEdgeClassList(KgEdgeClass kgEdgeClass);

    /**
     * 新增【请填写功能名称】
     * 
     * @param kgEdgeClass 【请填写功能名称】
     * @return 结果
     */
    public int insertKgEdgeClass(KgEdgeClass kgEdgeClass);

    /**
     * 修改【请填写功能名称】
     * 
     * @param kgEdgeClass 【请填写功能名称】
     * @return 结果
     */
    public int updateKgEdgeClass(KgEdgeClass kgEdgeClass);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】主键集合
     * @return 结果
     */
    public int deleteKgEdgeClassByIds(Long[] ids);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteKgEdgeClassById(Long id);
}
