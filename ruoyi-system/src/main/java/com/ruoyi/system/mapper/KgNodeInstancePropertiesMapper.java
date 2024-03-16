package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.KgNodeInstanceProperties;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2024-03-16
 */
public interface KgNodeInstancePropertiesMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public KgNodeInstanceProperties selectKgNodeInstancePropertiesById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param kgNodeInstanceProperties 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<KgNodeInstanceProperties> selectKgNodeInstancePropertiesList(KgNodeInstanceProperties kgNodeInstanceProperties);

    /**
     * 新增【请填写功能名称】
     * 
     * @param kgNodeInstanceProperties 【请填写功能名称】
     * @return 结果
     */
    public int insertKgNodeInstanceProperties(KgNodeInstanceProperties kgNodeInstanceProperties);

    /**
     * 修改【请填写功能名称】
     * 
     * @param kgNodeInstanceProperties 【请填写功能名称】
     * @return 结果
     */
    public int updateKgNodeInstanceProperties(KgNodeInstanceProperties kgNodeInstanceProperties);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteKgNodeInstancePropertiesById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteKgNodeInstancePropertiesByIds(Long[] ids);
}
