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
 * 【请填写功能名称】对象 kg_history
 *
 * @author ruoyi
 * @date 2024-04-01
 */
@Data
public class KgHistory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /** 记录类型，1新增，2删除，3修改 */
    @Excel(name = "记录类型，1新增，2删除，3修改")
    private Integer type;

    /** 对应的主体的id */
    @Excel(name = "对应的主体的id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long targetId;

    /** 记录对应的主体类型，
1node_class
2node_class_properties
3node_instance
4node_instance_properties

5edge_class
6edge_class_properties
7edge_instance
8edge_instance_properties
 */
    @Excel(name = "")
    private Integer targetType;

    /** 对应主体的名称 */
    @Excel(name = "对应主体的名称")
    private String targetName;

    /** 修改前的值 */
    @Excel(name = "修改前的值")
    private String originValue;

    /** 修改后的值 */
    @Excel(name = "修改后的值")
    private String curValue;

    /** 记录生成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "记录生成时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date time;

    /** 生成该记录的用户id */
    @Excel(name = "生成该记录的用户id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

}
