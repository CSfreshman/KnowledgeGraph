package com.ruoyi.system.mapper;

import java.util.Date;
import java.util.List;
import com.ruoyi.system.domain.KgOperation;
import com.ruoyi.system.domain.dto.ExtraStatisticDto;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper接口
 *
 * @author ruoyi
 * @date 2024-04-12
 */
@Mapper
public interface KgOperationMapper
{
    /**
     * 查询
     *
     * @param id 主键
     * @return 
     */
    public KgOperation selectKgOperationById(Long id);

    /**
     * 查询列表
     *
     * @param kgOperation 
     * @return 集合
     */
    public List<KgOperation> selectKgOperationList(KgOperation kgOperation);

    /**
     * 新增
     *
     * @param kgOperation 
     * @return 结果
     */
    public int insertKgOperation(KgOperation kgOperation);

    /**
     * 修改
     *
     * @param kgOperation 
     * @return 结果
     */
    public int updateKgOperation(KgOperation kgOperation);

    /**
     * 删除
     *
     * @param id 主键
     * @return 结果
     */
    public int deleteKgOperationById(Long id);

    /**
     * 批量删除
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteKgOperationByIds(Long[] ids);

    public List<ExtraStatisticDto> statistic(Date date);
}
