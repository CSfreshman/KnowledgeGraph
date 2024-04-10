package com.ruoyi.system.domain.vo;

import cn.hutool.json.JSONObject;
import com.ruoyi.system.utils.neo4j.Neo4jGraph;
import com.ruoyi.system.utils.neo4j.Neo4jNode;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ExtraResp {
    private Neo4jGraph graph;

    List<Map.Entry<Neo4jNode, JSONObject>> list;
}
