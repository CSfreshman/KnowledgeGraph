package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.KgEdgeClass;

/**
 * Mapper接口
 * 
 * @author ruoyi
 * @date 2024-03-08
 */
public interface KgEdgeClassMapper 
{
    /**
     * 查询
     * 
     * @param id 主键
     * @return 
     */
    public KgEdgeClass selectKgEdgeClassById(Long id);

    /**
     * 查询列表
     * 
     * @param kgEdgeClass 
     * @return 集合
     */
    public List<KgEdgeClass> selectKgEdgeClassList(KgEdgeClass kgEdgeClass);

    /**
     * 新增
     * 
     * @param kgEdgeClass 
     * @return 结果
     */
    public int insertKgEdgeClass(KgEdgeClass kgEdgeClass);

    /**
     * 修改
     * 
     * @param kgEdgeClass 
     * @return 结果
     */
    public int updateKgEdgeClass(KgEdgeClass kgEdgeClass);

    /**
     * 删除
     * 
     * @param id 主键
     * @return 结果
     */
    public int deleteKgEdgeClassById(Long id);

    /**
     * 批量删除
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteKgEdgeClassByIds(Long[] ids);
}
