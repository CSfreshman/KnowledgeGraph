package com.ruoyi.web.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.TestNeo4jService;
import com.ruoyi.system.utils.neo4j.Neo4jGraph;
import com.ruoyi.system.req.GraphReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/updateNodeDetail")
    public AjaxResult updateNodeDetail(@RequestBody Map<String,Object> map){
        System.out.println(map);
        GraphReq req = new GraphReq();
        req.setNodeId(Long.valueOf(String.valueOf(map.get("nodeId"))));
        //req.setProps((Map<String, Object>) );
        Map<String, Object> reqMap = new HashMap<>();
        List props = (List) map.get("props");
        for (Object prop : props) {
            Map map1 = (Map)prop;
            reqMap.put(map1.get("key").toString(),map1.get("value"));
        }
        req.setProps(reqMap);
        System.out.println(req);
        int res = testNeo4jService.updateNodeDetail(req);
        return AjaxResult.success();
    }

    @PostMapping("/deleteNode")
    public AjaxResult updateNodeDetail(@RequestBody GraphReq req){
        System.out.println(req);
        return AjaxResult.success();
    }
}
