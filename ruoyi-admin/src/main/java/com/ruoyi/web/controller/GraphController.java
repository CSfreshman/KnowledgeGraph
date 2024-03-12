package com.ruoyi.web.controller;

import com.ruoyi.system.service.TestNeo4jService;
import com.ruoyi.system.utils.neo4j.Neo4jGraph;
import com.ruoyi.system.req.GraphReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/graph")
public class GraphController {
    @Autowired
    private TestNeo4jService testNeo4jService;

    @GetMapping("/getAllGraph")
    public Neo4jGraph getAllGraph(){
        return testNeo4jService.getAllGraph();
    }

    @PostMapping("/getNodeDetail")
    public Neo4jGraph getNodeDetail(@RequestBody GraphReq req){

        Neo4jGraph graph = testNeo4jService.getNodeDetailByNodeId(req);
        return graph;
    }
}
