package com.ruoyi.system.domain.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ExtraStatisticDto {
    private Integer type;

    private Date date;

    private Long count;
}
