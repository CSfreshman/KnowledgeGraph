package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.KgNodeClassProperties;

/**
 * Mapper接口
 * 
 * @author ruoyi
 * @date 2024-03-04
 */
public interface KgNodeClassPropertiesMapper 
{
    /**
     * 查询
     * 
     * @param id 主键
     * @return 
     */
    public KgNodeClassProperties selectKgNodeClassPropertiesById(Long id);

    /**
     * 查询列表
     * 
     * @param kgNodeClassProperties 
     * @return 集合
     */
    public List<KgNodeClassProperties> selectKgNodeClassPropertiesList(KgNodeClassProperties kgNodeClassProperties);

    /**
     * 新增
     * 
     * @param kgNodeClassProperties 
     * @return 结果
     */
    public int insertKgNodeClassProperties(KgNodeClassProperties kgNodeClassProperties);

    /**
     * 修改
     * 
     * @param kgNodeClassProperties 
     * @return 结果
     */
    public int updateKgNodeClassProperties(KgNodeClassProperties kgNodeClassProperties);

    /**
     * 删除
     * 
     * @param id 主键
     * @return 结果
     */
    public int deleteKgNodeClassPropertiesById(Long id);

    /**
     * 批量删除
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteKgNodeClassPropertiesByIds(Long[] ids);
}
