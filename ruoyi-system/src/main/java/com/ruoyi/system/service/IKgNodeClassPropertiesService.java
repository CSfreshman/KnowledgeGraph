package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.KgNodeClassProperties;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author ruoyi
 * @date 2024-03-04
 */
public interface IKgNodeClassPropertiesService 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public KgNodeClassProperties selectKgNodeClassPropertiesById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param kgNodeClassProperties 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<KgNodeClassProperties> selectKgNodeClassPropertiesList(KgNodeClassProperties kgNodeClassProperties);

    /**
     * 新增【请填写功能名称】
     * 
     * @param kgNodeClassProperties 【请填写功能名称】
     * @return 结果
     */
    public int insertKgNodeClassProperties(KgNodeClassProperties kgNodeClassProperties);

    /**
     * 修改【请填写功能名称】
     * 
     * @param kgNodeClassProperties 【请填写功能名称】
     * @return 结果
     */
    public int updateKgNodeClassProperties(KgNodeClassProperties kgNodeClassProperties);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】主键集合
     * @return 结果
     */
    public int deleteKgNodeClassPropertiesByIds(Long[] ids);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteKgNodeClassPropertiesById(Long id);
}
