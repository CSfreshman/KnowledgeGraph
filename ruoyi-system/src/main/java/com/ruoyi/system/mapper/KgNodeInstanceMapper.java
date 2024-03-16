package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.KgNodeInstance;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2024-03-16
 */
public interface KgNodeInstanceMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public KgNodeInstance selectKgNodeInstanceById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param kgNodeInstance 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<KgNodeInstance> selectKgNodeInstanceList(KgNodeInstance kgNodeInstance);

    /**
     * 新增【请填写功能名称】
     * 
     * @param kgNodeInstance 【请填写功能名称】
     * @return 结果
     */
    public int insertKgNodeInstance(KgNodeInstance kgNodeInstance);

    /**
     * 修改【请填写功能名称】
     * 
     * @param kgNodeInstance 【请填写功能名称】
     * @return 结果
     */
    public int updateKgNodeInstance(KgNodeInstance kgNodeInstance);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteKgNodeInstanceById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteKgNodeInstanceByIds(Long[] ids);
}
