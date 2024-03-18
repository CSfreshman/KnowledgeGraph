package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 kg_edge_instace_properties
 *
 * @author ruoyi
 * @date 2024-03-18
 */
@Data
public class KgEdgeInstaceProperties extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 边id */
    @Excel(name = "边id")
    private Long edgeId;

    /** 属性名 */
    @Excel(name = "属性名")
    private String name;

    /** 属性值 */
    @Excel(name = "属性值")
    private String value;

    /** 是否有效，1有效，0无效 */
    @Excel(name = "是否有效，1有效，0无效")
    private Long valid;

    /** 创建人 */
    @Excel(name = "创建人")
    private Long createUser;

    /** 修改时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "修改时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date modityTime;

    /** 修改人 */
    @Excel(name = "修改人")
    private Long modityUser;

    /** 修改类型 */
    @Excel(name = "修改类型")
    private String modityType;
}
