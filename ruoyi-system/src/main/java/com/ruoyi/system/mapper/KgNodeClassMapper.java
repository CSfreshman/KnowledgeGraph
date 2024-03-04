package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.KgNodeClass;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2024-03-04
 */
public interface KgNodeClassMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public KgNodeClass selectKgNodeClassById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param kgNodeClass 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<KgNodeClass> selectKgNodeClassList(KgNodeClass kgNodeClass);

    /**
     * 新增【请填写功能名称】
     * 
     * @param kgNodeClass 【请填写功能名称】
     * @return 结果
     */
    public int insertKgNodeClass(KgNodeClass kgNodeClass);

    /**
     * 修改【请填写功能名称】
     * 
     * @param kgNodeClass 【请填写功能名称】
     * @return 结果
     */
    public int updateKgNodeClass(KgNodeClass kgNodeClass);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteKgNodeClassById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteKgNodeClassByIds(Long[] ids);
}
