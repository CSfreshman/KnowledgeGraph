package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.KgEdgeClassProperties;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2024-03-08
 */
public interface KgEdgeClassPropertiesMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public KgEdgeClassProperties selectKgEdgeClassPropertiesById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param kgEdgeClassProperties 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<KgEdgeClassProperties> selectKgEdgeClassPropertiesList(KgEdgeClassProperties kgEdgeClassProperties);

    /**
     * 新增【请填写功能名称】
     * 
     * @param kgEdgeClassProperties 【请填写功能名称】
     * @return 结果
     */
    public int insertKgEdgeClassProperties(KgEdgeClassProperties kgEdgeClassProperties);

    /**
     * 修改【请填写功能名称】
     * 
     * @param kgEdgeClassProperties 【请填写功能名称】
     * @return 结果
     */
    public int updateKgEdgeClassProperties(KgEdgeClassProperties kgEdgeClassProperties);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteKgEdgeClassPropertiesById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteKgEdgeClassPropertiesByIds(Long[] ids);
}
