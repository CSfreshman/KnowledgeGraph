package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.KgEdgeInstance;

/**
 * Service接口
 *
 * @author ruoyi
 * @date 2024-03-17
 */
public interface IKgEdgeInstanceService
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
     * 批量删除
     *
     * @param ids 需要删除的主键集合
     * @return 结果
     */
    public int deleteKgEdgeInstanceByIds(Long[] ids);

    /**
     * 删除信息
     *
     * @param id 主键
     * @return 结果
     */
    public int deleteKgEdgeInstanceById(Long id);

    Integer deleteEdgeByNodeNeo4jId(Long nodeId);

    Integer deleteEdgeByNeo4jId(Long edgeId);
}
