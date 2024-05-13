package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.KgNodeClass;

/**
 * Mapper接口
 * 
 * @author ruoyi
 * @date 2024-03-04
 */
public interface KgNodeClassMapper 
{
    /**
     * 查询
     * 
     * @param id 主键
     * @return 
     */
    public KgNodeClass selectKgNodeClassById(Long id);

    /**
     * 查询列表
     * 
     * @param kgNodeClass 
     * @return 集合
     */
    public List<KgNodeClass> selectKgNodeClassList(KgNodeClass kgNodeClass);

    /**
     * 新增
     * 
     * @param kgNodeClass 
     * @return 结果
     */
    public int insertKgNodeClass(KgNodeClass kgNodeClass);

    /**
     * 修改
     * 
     * @param kgNodeClass 
     * @return 结果
     */
    public int updateKgNodeClass(KgNodeClass kgNodeClass);

    /**
     * 删除
     * 
     * @param id 主键
     * @return 结果
     */
    public int deleteKgNodeClassById(Long id);

    /**
     * 批量删除
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteKgNodeClassByIds(Long[] ids);
}
