package com.ruoyi.system.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 kg_node_instance
 *
 * @author ruoyi
 * @date 2024-03-16
 */
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
    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return label;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("classId", getClassId())
            .append("label", getLabel())
            .append("name", getName())
            .append("createTime", getCreateTime())
            .append("createUser", getCreateUser())
            .append("valid", getValid())
            .toString();
    }
}
