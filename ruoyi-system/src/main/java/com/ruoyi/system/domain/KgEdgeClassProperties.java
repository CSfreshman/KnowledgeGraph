package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 kg_edge_class_properties
 *
 * @author ruoyi
 * @date 2024-03-08
 */
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

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setEdgeId(Long edgeId)
    {
        this.edgeId = edgeId;
    }

    public Long getEdgeId()
    {
        return edgeId;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    public void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
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
    public void setModityTime(Date modityTime)
    {
        this.modityTime = modityTime;
    }

    public Date getModityTime()
    {
        return modityTime;
    }
    public void setModityUser(Long modityUser)
    {
        this.modityUser = modityUser;
    }

    public Long getModityUser()
    {
        return modityUser;
    }
    public void setModityType(Long modityType)
    {
        this.modityType = modityType;
    }

    public Long getModityType()
    {
        return modityType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("edgeId", getEdgeId())
            .append("name", getName())
            .append("type", getType())
            .append("createTime", getCreateTime())
            .append("createUser", getCreateUser())
            .append("valid", getValid())
            .append("modityTime", getModityTime())
            .append("modityUser", getModityUser())
            .append("modityType", getModityType())
            .toString();
    }
}
