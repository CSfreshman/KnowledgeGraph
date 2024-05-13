package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.KgNodeInstance;

/**
 * Mapper接口
 *
 * @author ruoyi
 * @date 2024-03-16
 */
public interface KgNodeInstanceMapper
{
    /**
     * 查询
     *
     * @param id 主键
     * @return 
     */
    public KgNodeInstance selectKgNodeInstanceById(Long id);

    /**
     * 查询列表
     *
     * @param kgNodeInstance 
     * @return 集合
     */
    public List<KgNodeInstance> selectKgNodeInstanceList(KgNodeInstance kgNodeInstance);

    /**
     * 新增
     *
     * @param kgNodeInstance 
     * @return 结果
     */
    public int insertKgNodeInstance(KgNodeInstance kgNodeInstance);

    /**
     * 修改
     *
     * @param kgNodeInstance 
     * @return 结果
     */
    public int updateKgNodeInstance(KgNodeInstance kgNodeInstance);

    /**
     * 删除
     *
     * @param id 主键
     * @return 结果
     */
    public int deleteKgNodeInstanceById(Long id);

    /**
     * 批量删除
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteKgNodeInstanceByIds(Long[] ids);

    public int deleteNodeInstanceByNeo4jId(Long nodeId);
}
