package com.ruoyi.system.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 kg_edge_instance
 *
 * @author ruoyi
 * @date 2024-03-17
 */
public class KgEdgeInstance extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /** 所属类型id */
    @Excel(name = "所属类型id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long classId;

    /** 对应在neo4j中的iid */
    @Excel(name = "对应在neo4j中的iid")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long neo4jId;

    /** 标签 */
    @Excel(name = "标签")
    private String label;

    /** 起点节点id */
    @Excel(name = "起点节点id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long fromNodeId;

    /** 重点节点id */
    @Excel(name = "重点节点id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long toNodeId;

    /** 是否有效，1有效，0无效 */
    @Excel(name = "是否有效，1有效，0无效")
    private Long valid;

    /** 创建人id */
    @Excel(name = "创建人id")
    private Long createUser;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setClassId(Long classId)
    {
        this.classId = classId;
    }

    public Long getClassId()
    {
        return classId;
    }
    public void setNeo4jId(Long neo4jId)
    {
        this.neo4jId = neo4jId;
    }

    public Long getNeo4jId()
    {
        return neo4jId;
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
    public void setValid(Long valid)
    {
        this.valid = valid;
    }

    public Long getValid()
    {
        return valid;
    }
    public void setCreateUser(Long createUser)
    {
        this.createUser = createUser;
    }

    public Long getCreateUser()
    {
        return createUser;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("classId", getClassId())
            .append("neo4jId", getNeo4jId())
            .append("label", getLabel())
            .append("fromNodeId", getFromNodeId())
            .append("toNodeId", getToNodeId())
            .append("valid", getValid())
            .append("createTime", getCreateTime())
            .append("createUser", getCreateUser())
            .toString();
    }
}
