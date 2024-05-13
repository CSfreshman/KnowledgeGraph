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
 * 对象 kg_node_instance
 *
 * @author ruoyi
 * @date 2024-03-16
 */
@Data
public class KgNodeInstance extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /** 类型id */
    @Excel(name = "类型id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long classId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long neo4jId;

    /** 类型名称 */
    @Excel(name = "类型名称")
    private String label;

    /** 节点名称 */
    @Excel(name = "节点名称")
    private String name;

    /** 创建人id */
    @Excel(name = "创建人id")
    private Long createUser;

    /** 是否有效，1有效，0无效 */
    @Excel(name = "是否有效，1有效，0无效")
    private Long valid;

    private List<KgNodeInstanceProperties> propsList;
}
