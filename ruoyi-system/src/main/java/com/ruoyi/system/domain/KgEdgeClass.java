package com.ruoyi.system.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.List;

/**
 * 对象 kg_edge_class
 *
 * @author ruoyi
 * @date 2024-03-08
 */
@Data
public class KgEdgeClass extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /** 关系名（标签） */
    @Excel(name = "关系名", readConverterExp = "标=签")
    private String label;

    /** 起点nodeId */
    @Excel(name = "起点nodeId")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long fromNodeId;

    private String fromNodeClassName;

    /** 终点nodeId */
    @Excel(name = "终点nodeId")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long toNodeId;

    private String toNodeClassName;

    /** 创建人id */
    @Excel(name = "创建人id")
    private Long createUser;

    /** 是否有效，1有效，0无效 */
    @Excel(name = "是否有效，1有效，0无效")
    private Long valid;

    private List<KgEdgeClassProperties> props;
}
