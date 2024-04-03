package com.ruoyi.system.req;

import com.ruoyi.system.domain.dto.GraphDto;
import com.ruoyi.system.utils.neo4j.Neo4jGraph;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GraphResp {
    private Neo4jGraph graph;
    private Map<Object,Integer> centrality;
    private List<GraphDto> dtoList;
}
