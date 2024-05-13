package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 对象 kg_edge_class_properties
 *
 * @author ruoyi
 * @date 2024-03-08
 */
@Data
public class KgEdgeClassProperties extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /** 边id */
    @Excel(name = "边id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long edgeId;

    /** 属性名 */
    @Excel(name = "属性名")
    private String name;

    /** 属性类型 */
    @Excel(name = "属性类型")
    private String type;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long createUser;

    /** 是否有效，1有效，0无效 */
    @Excel(name = "是否有效，1有效，0无效")
    private Long valid;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date modityTime;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long modityUser;

    /** 修改类型，1新增，0删除 */
    @Excel(name = "修改类型，1新增，0删除")
    private Long modityType;

    private String defaultValue;

    private String originValue;
}
