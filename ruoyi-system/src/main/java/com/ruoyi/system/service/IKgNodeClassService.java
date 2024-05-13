package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.KgNodeClass;

/**
 * Service接口
 * 
 * @author ruoyi
 * @date 2024-03-04
 */
public interface IKgNodeClassService 
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
     * 批量删除
     * 
     * @param ids 需要删除的主键集合
     * @return 结果
     */
    public int deleteKgNodeClassByIds(Long[] ids);

    /**
     * 删除信息
     * 
     * @param id 主键
     * @return 结果
     */
    public int deleteKgNodeClassById(Long id);

    List<KgNodeClass> getAll(KgNodeClass kgNodeClass);
}
