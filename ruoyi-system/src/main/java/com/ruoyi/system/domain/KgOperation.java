package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * kg_operation
 *
 * @author ruoyi
 * @date 2024-04-12
 */
@Data
public class KgOperation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 日志主键 */
    private Long id;

    /** 业务名称 */
    @Excel(name = "业务名称")
    private String name;

    /** 业务类型 */
    /*
    1.图谱检索
    1.1节点检索
    1.2关系检索
    1.3类型检索
    2.网络分析
    2.1路劲分析
    2.2中心多度探寻
    3.图谱计算
    3.1中心度计算
    3.2相似度计算
    4.辅助诊断
     */
    @Excel(name = "业务类型")
    private Integer type;

    /** 业务参数，json字符串 */
    @Excel(name = "业务参数，json字符串")
    private String param;

    /** 记录创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "记录创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date time;
}
