package com.ruoyi.web.controller;


import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.KgEdgeInstance;
import com.ruoyi.system.domain.KgNodeClass;
import com.ruoyi.system.req.GraphResp;
import com.ruoyi.system.service.IKgNodeInstancePropertiesService;
import com.ruoyi.system.service.TestNeo4jService;
import com.ruoyi.system.utils.neo4j.Neo4jEdge;
import com.ruoyi.system.utils.neo4j.Neo4jGraph;
import com.ruoyi.system.req.GraphReq;
import com.ruoyi.system.utils.neo4j.Neo4jNode;
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
    @Autowired
    private IKgNodeInstancePropertiesService nodeInstancePropertiesService;

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
        int res1 = nodeInstancePropertiesService.updateByNodeNeo4jId(req);
        return AjaxResult.success();
    }

    @PostMapping("/deleteNode")
    public AjaxResult updateNodeDetail(@RequestBody GraphReq req){
        System.out.println(req);
        return AjaxResult.success();
    }

    @PostMapping("/getEdgeInstanceGraph")
    public Neo4jGraph getEdgeInstanceGraph(@RequestBody KgEdgeInstance instance){
        System.out.println("getEdgeInstanceGraph:params:" + instance);
        return testNeo4jService.getEdgeInstanceGraph(instance);
    }

//    图谱检索
    @PostMapping("/graphSelect")
    public Neo4jGraph graphSelect(@RequestBody GraphReq req){
        System.out.println("graphSelect:req:" + req);
        Neo4jGraph neo4jGraph = new Neo4jGraph();

        if(req.getSelectIndex() == 1){
            // 根据实体的名称查询
            if(ObjectUtil.isEmpty(req.getNodeName())){
                throw new RuntimeException("实体名称不能为空");
            }
            neo4jGraph = testNeo4jService.getNodeByName(req.getNodeName());
        }else if(req.getSelectIndex() == 2){
            // 根据关系的名称查询，
            neo4jGraph = testNeo4jService.getEdgeByFromOrToNodeName(req.getFromNodeName(),req.getToNodeName());
        }else {
            neo4jGraph = testNeo4jService.getGraphByNodeOrEdgeClass(req.getNodeClassList(),req.getEdgeClassList());
            if(ObjectUtil.isEmpty(req.getEdgeClassList())){
                // 如果关系类型为空，就查询出所有的节点类型，以及这些节点之间的关系
                Neo4jGraph byNodeClass = testNeo4jService.getByNodeClass(req.getNodeClassList());
                for (Neo4jNode node : byNodeClass.getNodes()) {
                    neo4jGraph.getNodes().add(node);
                }
            }

        }


        return neo4jGraph;
    }

    // 路径分析
    @PostMapping("/analyse/path")
    public Neo4jGraph pathAnalyse(@RequestBody GraphReq req){
        System.out.println("pathAnalyse:req:" + req);
        Neo4jGraph neo4jGraph = testNeo4jService.pathAnalyse(req);
        return neo4jGraph;
    }

    // 中心多度探寻
    @PostMapping("/analyse/centerMultiDegree")
    public Neo4jGraph centerMultiDegree(@RequestBody GraphReq req){
        System.out.println("centerMultiDegree:req:" + req);
        Neo4jGraph neo4jGraph = testNeo4jService.centerMultiDegree(req);
        return neo4jGraph;

    }

    // 中心度计算
    @PostMapping("/calculation/centrality")
    public  GraphResp centralityCalculation(@RequestBody GraphReq req){
        System.out.println("centralityCalculation:req" + req);
        Map<Object,Integer> map = testNeo4jService.centralityCalculation(req);

        Neo4jGraph neo4jGraph = new Neo4jGraph();
        // 根据节点类型查询
        if(ObjectUtil.isNotNull(req.getNodeClassList())){
            Neo4jGraph res1 = testNeo4jService.getByNodeClass(req.getNodeClassList());
            Neo4jGraph res2 = testNeo4jService.getEdgeByNodeClass(req.getNodeClassList());
            for (Neo4jNode node : res1.getNodes()) {
                neo4jGraph.addNeo4jNode(node);
            }
            for (Neo4jEdge edge : res2.getEdges()) {
                neo4jGraph.addNeo4jEdge(edge);
            }
        }

        GraphResp resp = new GraphResp();

        resp.setGraph(neo4jGraph);
        resp.setCentrality(map);
        return resp;
    }

    // 统计图谱中实体的种类与数量
    @PostMapping("/statistic")
    public List<Map<String,Integer>> statistic(){
        return testNeo4jService.statistic();
    }

}
