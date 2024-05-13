package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.KgEdgeInstaceProperties;
import com.ruoyi.system.req.GraphReq;

/**
 * Service接口
 *
 * @author ruoyi
 * @date 2024-03-18
 */
public interface IKgEdgeInstacePropertiesService
{
    /**
     * 查询
     *
     * @param id 主键
     * @return 
     */
    public KgEdgeInstaceProperties selectKgEdgeInstacePropertiesById(Long id);

    /**
     * 查询列表
     *
     * @param kgEdgeInstaceProperties 
     * @return 集合
     */
    public List<KgEdgeInstaceProperties> selectKgEdgeInstacePropertiesList(KgEdgeInstaceProperties kgEdgeInstaceProperties);

    /**
     * 新增
     *
     * @param kgEdgeInstaceProperties 
     * @return 结果
     */
    public int insertKgEdgeInstaceProperties(KgEdgeInstaceProperties kgEdgeInstaceProperties);

    /**
     * 修改
     *
     * @param kgEdgeInstaceProperties 
     * @return 结果
     */
    public int updateKgEdgeInstaceProperties(KgEdgeInstaceProperties kgEdgeInstaceProperties);

    /**
     * 批量删除
     *
     * @param ids 需要删除的主键集合
     * @return 结果
     */
    public int deleteKgEdgeInstacePropertiesByIds(Long[] ids);

    /**
     * 删除信息
     *
     * @param id 主键
     * @return 结果
     */
    public int deleteKgEdgeInstacePropertiesById(Long id);

    int deleteByEdgeId(Long id);

    int updateByEdgeNeo4jId(GraphReq req);

}
