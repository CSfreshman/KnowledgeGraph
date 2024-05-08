package com.ruoyi.web.controller;


import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.dto.GraphDto;
import com.ruoyi.system.mapper.KgOperationMapper;
import com.ruoyi.system.req.GraphResp;
import com.ruoyi.system.service.IKgEdgeInstacePropertiesService;
import com.ruoyi.system.service.IKgNodeInstancePropertiesService;
import com.ruoyi.system.service.TestNeo4jService;
import com.ruoyi.system.utils.neo4j.Neo4jEdge;
import com.ruoyi.system.utils.neo4j.Neo4jGraph;
import com.ruoyi.system.req.GraphReq;
import com.ruoyi.system.utils.neo4j.Neo4jNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/graph")
public class GraphController {
    @Autowired
    private TestNeo4jService testNeo4jService;
    @Autowired
    private IKgNodeInstancePropertiesService nodeInstancePropertiesService;
    @Autowired
    private IKgEdgeInstacePropertiesService edgeInstancePropertiesService;
    @Resource
    private KgOperationMapper operationMapper;

    @GetMapping("/getAllGraph")
    public Neo4jGraph getAllGraph(){
        return testNeo4jService.getAllGraph();
    }

    @PostMapping("/getNodeDetail")
    public Neo4jGraph getNodeDetail(@RequestBody GraphReq req){

        Neo4jGraph graph = testNeo4jService.getNodeDetailByNodeId(req);
        return graph;
    }

    @PostMapping("/getSingleEdgeByEdgeId")
    public Neo4jGraph getSingleEdgeByEdgeId(@RequestBody GraphReq req){

        Neo4jGraph graph = testNeo4jService.getSingleEdgeByEdgeId(req.getEdgeId());
        return graph;
    }

    // 更新节点实例
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

    // 更新关系实例
    @PostMapping("/updateEdgeDetail")
    public AjaxResult updateEdgeDetail(@RequestBody Map<String,Object> map){
        System.out.println(map);
        GraphReq req = new GraphReq();
        req.setEdgeId(Long.valueOf(String.valueOf(map.get("edgeId"))));
        //req.setProps((Map<String, Object>) );
        Map<String, Object> reqMap = new HashMap<>();
        List props = (List) map.get("props");
        for (Object prop : props) {
            Map map1 = (Map)prop;
            reqMap.put(map1.get("key").toString(),map1.get("value"));
        }
        req.setProps(reqMap);
        System.out.println(req);
        int res = testNeo4jService.updateEdgeDetail(req);
        int res1 = edgeInstancePropertiesService.updateByEdgeNeo4jId(req);
        return AjaxResult.success();
    }

    @PostMapping("/deleteNode")
    public AjaxResult deleteNode(@RequestBody GraphReq req){
        System.out.println(req);
        int count = testNeo4jService.deleteNodeByNeo4jId(req);
        return AjaxResult.success();
    }

    @PostMapping("/deleteEdge")
    public AjaxResult deleteEdge(@RequestBody GraphReq req){
        System.out.println(req);
        int count = testNeo4jService.deleteEdgeByNeo4jId(req);
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

        int operationType = 0;
        // 设置该操作对应的实体名称列表
        Set<String> operationParam = new HashSet<>();

        if(req.getSelectIndex() == 1){
            operationType = 11;
            // 根据实体的名称查询
            if(ObjectUtil.isEmpty(req.getNodeName())){
                throw new RuntimeException("实体名称不能为空");
            }
            operationParam.add(req.getNodeName());
            neo4jGraph = testNeo4jService.getNodeByName(req.getNodeName());
        }else if(req.getSelectIndex() == 2){
            operationType = 12;
            operationParam.add(req.getFromNodeName());
            operationParam.add(req.getToNodeName());
            // 根据关系的名称查询
            neo4jGraph = testNeo4jService.getEdgeByFromOrToNodeName(req.getFromNodeName(),req.getToNodeName());
        }else {
            operationType = 13;
            if(ObjectUtil.isNotEmpty(req.getNodeClassList())){
                operationParam.addAll(req.getNodeClassList().stream().map(KgNodeClass::getName).collect(Collectors.toList()));
            }
            if(ObjectUtil.isNotEmpty(req.getEdgeClassList())){
                operationParam.addAll(req.getEdgeClassList().stream().map(KgEdgeClass::getLabel).collect(Collectors.toList()));
            }
            neo4jGraph = testNeo4jService.getGraphByNodeOrEdgeClass(req.getNodeClassList(),req.getEdgeClassList());
            if(ObjectUtil.isEmpty(req.getEdgeClassList())){
                // 如果关系类型为空，就查询出所有的节点类型，以及这些节点之间的关系
                Neo4jGraph byNodeClass = testNeo4jService.getByNodeClass(req.getNodeClassList());
                for (Neo4jNode node : byNodeClass.getNodes()) {
                    neo4jGraph.getNodes().add(node);
                }
            }

        }

        // 埋点日志 记录用户操作
        KgOperation operation = new KgOperation();
        operation.setId(IdUtil.getSnowflakeNextId());
        operation.setName("图谱检索");
        operation.setType(operationType);
        operation.setParam(JSONUtil.toJsonStr(operationParam));
        operation.setTime(DateUtils.getNowDate());
        // 多线程操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                operationMapper.insertKgOperation(operation);
            }
        }).start();


        return neo4jGraph;
    }

    // 路径分析
    @PostMapping("/analyse/path")
    public Neo4jGraph pathAnalyse(@RequestBody GraphReq req){
        System.out.println("pathAnalyse:req:" + req);
        Neo4jGraph neo4jGraph = testNeo4jService.pathAnalyse(req);

        Set<String> operationParam = new HashSet<>();
        operationParam.add(req.getFromNode().getName());
        operationParam.add(req.getToNode().getName());
        // 埋点日志 记录用户操作
        KgOperation operation = new KgOperation();
        operation.setId(IdUtil.getSnowflakeNextId());
        operation.setName("网络分析");
        operation.setType(21);
        operation.setParam(JSONUtil.toJsonStr(operationParam));
        operation.setTime(DateUtils.getNowDate());
        // 多线程操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                operationMapper.insertKgOperation(operation);
            }
        }).start();


        return neo4jGraph;
    }

    // 中心多度探寻
    @PostMapping("/analyse/centerMultiDegree")
    public Neo4jGraph centerMultiDegree(@RequestBody GraphReq req){
        System.out.println("centerMultiDegree:req:" + req);
        Neo4jGraph neo4jGraph = testNeo4jService.centerMultiDegree(req);

        Set<String> operationParam = new HashSet<>();
        operationParam.add(req.getAnalyseNode().getName());
        operationParam.addAll(req.getEdgeClassList().stream().map(it->it.getLabel()).collect(Collectors.toList()));
        // 埋点日志 记录用户操作
        KgOperation operation = new KgOperation();
        operation.setId(IdUtil.getSnowflakeNextId());
        operation.setName("中心多度探寻");
        operation.setType(22);
        operation.setParam(JSONUtil.toJsonStr(operationParam));
        operation.setTime(DateUtils.getNowDate());
        // 多线程操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                operationMapper.insertKgOperation(operation);
            }
        }).start();

        return neo4jGraph;

    }

    // 中心度计算
    @PostMapping("/calculation/centrality")
    public GraphResp centralityCalculation(@RequestBody GraphReq req){
        System.out.println("centralityCalculation:req" + req);
        Map<Object,Double> map = testNeo4jService.centralityCalculation(req);

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


        Set<String> operationParam = new HashSet<>();
        operationParam.addAll(req.getNodeClassList().stream().map(KgNodeClass::getName).collect(Collectors.toList()));
        // 埋点日志 记录用户操作
        KgOperation operation = new KgOperation();
        operation.setId(IdUtil.getSnowflakeNextId());
        operation.setName("中心度计算");
        operation.setType(31);
        operation.setParam(JSONUtil.toJsonStr(operationParam));
        operation.setTime(DateUtils.getNowDate());
        // 多线程操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                operationMapper.insertKgOperation(operation);
            }
        }).start();

        GraphResp resp = new GraphResp();

        resp.setGraph(neo4jGraph);
        resp.setCentrality(map);
        return resp;
    }

    // 相似度计算
    @PostMapping("/calculation/similarity")
    public GraphResp centralitySimilarity(@RequestBody GraphReq req){
        System.out.println("centralitySimilarity:req" + req);
        List<GraphDto> dtoList = testNeo4jService.centralitySimilarity(req);

        Set<String> operationParam = new HashSet<>();
        operationParam.add(req.getCalculateNode().getName());
        operationParam.addAll(req.getEdgeClassList().stream().map(it->it.getLabel()).collect(Collectors.toList()));
        // 埋点日志 记录用户操作
        KgOperation operation = new KgOperation();
        operation.setId(IdUtil.getSnowflakeNextId());
        operation.setName("相似度计算");
        operation.setType(32);
        operation.setParam(JSONUtil.toJsonStr(operationParam));
        operation.setTime(DateUtils.getNowDate());
        // 多线程操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                operationMapper.insertKgOperation(operation);
            }
        }).start();

        GraphResp resp = new GraphResp();
        resp.setDtoList(dtoList);
        return resp;
    }

    // 统计图谱中实体的种类与数量
    @PostMapping("/statistic")
    public List<Map<String,Integer>> statistic(){
        return testNeo4jService.statistic();
    }


    // 获得数据库中存在的全部节点类型
    @GetMapping("/getAllExistNodeClass")
    public List<String> getAllExistNodeClass(){
        return testNeo4jService.getAllExistNodeClass();
    }


    // 清理neo4j中存在的graphProject
    @PostMapping("/cleanGraphProject")
    public AjaxResult cleanGraphProject(){
        return testNeo4jService.cleanGraphProject();
    }
}
