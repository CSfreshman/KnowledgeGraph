package com.ruoyi.system.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 kg_edge_class
 *
 * @author ruoyi
 * @date 2024-03-08
 */
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

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return label;
    }
    public void setFromNodeId(Long fromNodeId)
    {
        this.fromNodeId = fromNodeId;
    }

    public Long getFromNodeId()
    {
        return fromNodeId;
    }
    public void setToNodeId(Long toNodeId)
    {
        this.toNodeId = toNodeId;
    }

    public Long getToNodeId()
    {
        return toNodeId;
    }
    public void setCreateUser(Long createUser)
    {
        this.createUser = createUser;
    }

    public Long getCreateUser()
    {
        return createUser;
    }
    public void setValid(Long valid)
    {
        this.valid = valid;
    }

    public Long getValid()
    {
        return valid;
    }

    public String getFromNodeClassName() {
        return fromNodeClassName;
    }

    public void setFromNodeClassName(String fromNodeClassName) {
        this.fromNodeClassName = fromNodeClassName;
    }

    public String getToNodeClassName() {
        return toNodeClassName;
    }

    public void setToNodeClassName(String toNodeClassName) {
        this.toNodeClassName = toNodeClassName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("label", getLabel())
            .append("fromNodeId", getFromNodeId())
            .append("toNodeId", getToNodeId())
            .append("createUser", getCreateUser())
            .append("createTime", getCreateTime())
            .append("valid", getValid())
            .toString();
    }
}
