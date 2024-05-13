package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.KgNodeInstance;
import com.ruoyi.system.utils.neo4j.Neo4jNode;

/**
 * Service接口
 *
 * @author ruoyi
 * @date 2024-03-16
 */
public interface IKgNodeInstanceService
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
     * 批量删除
     *
     * @param ids 需要删除的主键集合
     * @return 结果
     */
    public int deleteKgNodeInstanceByIds(Long[] ids);

    /**
     * 删除信息
     *
     * @param id 主键
     * @return 结果
     */
    public int deleteKgNodeInstanceById(Long id);

    Integer deleteNodeInstanceByNeo4jId(Long nodeId);
}
