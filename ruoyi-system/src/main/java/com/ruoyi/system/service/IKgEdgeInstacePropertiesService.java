package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.KgEdgeInstaceProperties;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author ruoyi
 * @date 2024-03-18
 */
public interface IKgEdgeInstacePropertiesService 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public KgEdgeInstaceProperties selectKgEdgeInstacePropertiesById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param kgEdgeInstaceProperties 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<KgEdgeInstaceProperties> selectKgEdgeInstacePropertiesList(KgEdgeInstaceProperties kgEdgeInstaceProperties);

    /**
     * 新增【请填写功能名称】
     * 
     * @param kgEdgeInstaceProperties 【请填写功能名称】
     * @return 结果
     */
    public int insertKgEdgeInstaceProperties(KgEdgeInstaceProperties kgEdgeInstaceProperties);

    /**
     * 修改【请填写功能名称】
     * 
     * @param kgEdgeInstaceProperties 【请填写功能名称】
     * @return 结果
     */
    public int updateKgEdgeInstaceProperties(KgEdgeInstaceProperties kgEdgeInstaceProperties);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】主键集合
     * @return 结果
     */
    public int deleteKgEdgeInstacePropertiesByIds(Long[] ids);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteKgEdgeInstacePropertiesById(Long id);
}
