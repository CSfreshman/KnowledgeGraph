package com.ruoyi.system.req;

import lombok.Data;

import java.util.Map;

@Data
public class GraphReq {
    private Long nodeId;

    private int degree;

    private Map<String, Object> props;
}
