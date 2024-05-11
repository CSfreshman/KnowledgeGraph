package com.ruoyi.system.req;

import lombok.Data;

@Data
public class CacheReq {
    private Integer pageNum;

    private Integer pageSize;

    private String key;

    private String value;
}
