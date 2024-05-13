package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.KgEdgeInstance;

/**
 * Mapper接口
 *
 * @author ruoyi
 * @date 2024-03-17
 */
public interface KgEdgeInstanceMapper
{
    /**
     * 查询
     *
     * @param id 主键
     * @return 
     */
    public KgEdgeInstance selectKgEdgeInstanceById(Long id);

    /**
     * 查询列表
     *
     * @param kgEdgeInstance 
     * @return 集合
     */
    public List<KgEdgeInstance> selectKgEdgeInstanceList(KgEdgeInstance kgEdgeInstance);

    /**
     * 新增
     *
     * @param kgEdgeInstance 
     * @return 结果
     */
    public int insertKgEdgeInstance(KgEdgeInstance kgEdgeInstance);

    /**
     * 修改
     *
     * @param kgEdgeInstance 
     * @return 结果
     */
    public int updateKgEdgeInstance(KgEdgeInstance kgEdgeInstance);

    /**
     * 删除
     *
     * @param id 主键
     * @return 结果
     */
    public int deleteKgEdgeInstanceById(Long id);

    /**
     * 批量删除
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteKgEdgeInstanceByIds(Long[] ids);

    public int deleteEdgeByNodeNeo4jId(Long nodeId);
}
