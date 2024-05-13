package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.KgNodeInstanceProperties;
import com.ruoyi.system.req.GraphReq;

/**
 * Service接口
 *
 * @author ruoyi
 * @date 2024-03-16
 */
public interface IKgNodeInstancePropertiesService
{
    /**
     * 查询
     *
     * @param id 主键
     * @return 
     */
    public KgNodeInstanceProperties selectKgNodeInstancePropertiesById(Long id);

    /**
     * 查询列表
     *
     * @param kgNodeInstanceProperties 
     * @return 集合
     */
    public List<KgNodeInstanceProperties> selectKgNodeInstancePropertiesList(KgNodeInstanceProperties kgNodeInstanceProperties);

    /**
     * 新增
     *
     * @param kgNodeInstanceProperties 
     * @return 结果
     */
    public int insertKgNodeInstanceProperties(KgNodeInstanceProperties kgNodeInstanceProperties);

    /**
     * 修改
     *
     * @param kgNodeInstanceProperties 
     * @return 结果
     */
    public int updateKgNodeInstanceProperties(KgNodeInstanceProperties kgNodeInstanceProperties);

    /**
     * 批量删除
     *
     * @param ids 需要删除的主键集合
     * @return 结果
     */
    public int deleteKgNodeInstancePropertiesByIds(Long[] ids);

    /**
     * 删除信息
     *
     * @param id 主键
     * @return 结果
     */
    public int deleteKgNodeInstancePropertiesById(Long id);

    int updateByNodeNeo4jId(GraphReq req);

    int deleteByNodeId(Long id);
}
