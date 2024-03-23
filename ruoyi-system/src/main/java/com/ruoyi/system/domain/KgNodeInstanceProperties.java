package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 kg_node_instance_properties
 *
 * @author ruoyi
 * @date 2024-03-16
 */
@Data
public class KgNodeInstanceProperties extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 所属节点的id */
    @Excel(name = "所属节点的id")
    private Long nodeId;

    /** 属性名 */
    @Excel(name = "属性名")
    private String name;

    /** 属性值 */
    @Excel(name = "属性值")
    private String value;

    /** 创建人 */
    @Excel(name = "创建人")
    private Long createUser;

    /** 是否有效，1有效，0无效 */
    @Excel(name = "是否有效，1有效，0无效")
    private Long valid;

    /** 修改时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "修改时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date modityTime;

    /** 修改人id */
    @Excel(name = "修改人id")
    private Long modityUser;

    /** 修改类型，1新增，0删除 */
    @Excel(name = "修改类型，1新增，0删除")
    private Long modityType;
}
