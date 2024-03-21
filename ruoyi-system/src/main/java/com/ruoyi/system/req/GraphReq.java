package com.ruoyi.system.req;

import com.ruoyi.system.domain.KgEdgeClass;
import com.ruoyi.system.domain.KgNodeClass;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GraphReq {
    private Long nodeId;

    private int degree;

    private Map<String, Object> props;

    private String nodeName;

    private String edgeName;

    private List<KgNodeClass> nodeClassList;

    private List<KgEdgeClass> edgeClassList;
}
