package com.ruoyi.system.req;

import com.ruoyi.system.utils.neo4j.Neo4jNode;
import lombok.Data;

import java.util.List;

// 额外功能请求体
@Data
public class ExtraReq {
    private String symptomsDesc;

    private List<Neo4jNode> selectedNodeList;

    private Long diseaseNodeId;
}
