package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.KgEdgeClassProperties;

/**
 * Mapper接口
 * 
 * @author ruoyi
 * @date 2024-03-08
 */
public interface KgEdgeClassPropertiesMapper 
{
    /**
     * 查询
     * 
     * @param id 主键
     * @return 
     */
    public KgEdgeClassProperties selectKgEdgeClassPropertiesById(Long id);

    /**
     * 查询列表
     * 
     * @param kgEdgeClassProperties 
     * @return 集合
     */
    public List<KgEdgeClassProperties> selectKgEdgeClassPropertiesList(KgEdgeClassProperties kgEdgeClassProperties);

    /**
     * 新增
     * 
     * @param kgEdgeClassProperties 
     * @return 结果
     */
    public int insertKgEdgeClassProperties(KgEdgeClassProperties kgEdgeClassProperties);

    /**
     * 修改
     * 
     * @param kgEdgeClassProperties 
     * @return 结果
     */
    public int updateKgEdgeClassProperties(KgEdgeClassProperties kgEdgeClassProperties);

    /**
     * 删除
     * 
     * @param id 主键
     * @return 结果
     */
    public int deleteKgEdgeClassPropertiesById(Long id);

    /**
     * 批量删除
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteKgEdgeClassPropertiesByIds(Long[] ids);
}
