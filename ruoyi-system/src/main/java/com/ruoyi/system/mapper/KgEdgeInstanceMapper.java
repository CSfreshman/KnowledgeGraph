package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.KgEdgeInstance;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2024-03-17
 */
public interface KgEdgeInstanceMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public KgEdgeInstance selectKgEdgeInstanceById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param kgEdgeInstance 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<KgEdgeInstance> selectKgEdgeInstanceList(KgEdgeInstance kgEdgeInstance);

    /**
     * 新增【请填写功能名称】
     * 
     * @param kgEdgeInstance 【请填写功能名称】
     * @return 结果
     */
    public int insertKgEdgeInstance(KgEdgeInstance kgEdgeInstance);

    /**
     * 修改【请填写功能名称】
     * 
     * @param kgEdgeInstance 【请填写功能名称】
     * @return 结果
     */
    public int updateKgEdgeInstance(KgEdgeInstance kgEdgeInstance);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteKgEdgeInstanceById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteKgEdgeInstanceByIds(Long[] ids);
}
