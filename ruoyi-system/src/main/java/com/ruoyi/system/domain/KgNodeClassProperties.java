package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 kg_node_class_properties
 * 
 * @author ruoyi
 * @date 2024-03-04
 */
public class KgNodeClassProperties extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 属性名 */
    @Excel(name = "属性名")
    private String name;

    /** 属性类型 */
    @Excel(name = "属性类型")
    private String type;

    /** 创建人id */
    @Excel(name = "创建人id")
    private Long createUser;

    /** 外键 */
    @Excel(name = "外键")
    private Long nodeId;

    /** 是否有效，1有效，0无效 */
    @Excel(name = "是否有效，1有效，0无效")
    private Integer valid;

    /** 修改该记录的时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "修改该记录的时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date modifyTime;

    /** 修改该记录的修改人 */
    @Excel(name = "修改该记录的修改人")
    private Long modifyUser;

    /** 修改类型，1新增，0删除 */
    @Excel(name = "修改类型，1新增，0删除")
    private Long modifyType;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
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
    public void setNodeId(Long nodeId) 
    {
        this.nodeId = nodeId;
    }

    public Long getNodeId() 
    {
        return nodeId;
    }
    public void setValid(Integer valid) 
    {
        this.valid = valid;
    }

    public Integer getValid() 
    {
        return valid;
    }
    public void setModifyTime(Date modifyTime) 
    {
        this.modifyTime = modifyTime;
    }

    public Date getModifyTime() 
    {
        return modifyTime;
    }
    public void setModifyUser(Long modifyUser) 
    {
        this.modifyUser = modifyUser;
    }

    public Long getModifyUser() 
    {
        return modifyUser;
    }
    public void setModifyType(Long modifyType) 
    {
        this.modifyType = modifyType;
    }

    public Long getModifyType() 
    {
        return modifyType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("type", getType())
            .append("createTime", getCreateTime())
            .append("createUser", getCreateUser())
            .append("nodeId", getNodeId())
            .append("valid", getValid())
            .append("modifyTime", getModifyTime())
            .append("modifyUser", getModifyUser())
            .append("modifyType", getModifyType())
            .toString();
    }
}
