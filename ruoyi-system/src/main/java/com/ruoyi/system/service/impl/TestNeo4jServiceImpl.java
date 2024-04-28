package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.dto.GraphDto;
import com.ruoyi.system.req.GraphReq;
import com.ruoyi.system.service.IKgEdgeInstanceService;
import com.ruoyi.system.service.IKgNodeInstanceService;
import com.ruoyi.system.service.TestNeo4jService;
import com.ruoyi.system.utils.neo4j.Neo4jEdge;
import com.ruoyi.system.utils.neo4j.Neo4jGraph;
import com.ruoyi.system.utils.neo4j.Neo4jNode;
import org.apache.poi.ss.formula.functions.T;
import org.neo4j.driver.*;
import org.neo4j.driver.internal.InternalResult;
import org.neo4j.driver.internal.types.TypeConstructor;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;


@Service
public class TestNeo4jServiceImpl implements TestNeo4jService {
    @Resource
    private Neo4jClient neo4jClient;

    @Resource
    private Neo4jTemplate neo4jTemplate;

    @Autowired
    private IKgNodeInstanceService nodeInstanceService;

    @Autowired
    private IKgEdgeInstanceService edgeInstanceService;

    private Driver driver = GraphDatabase.driver("bolt://8.130.96.64:7687", AuthTokens.basic("neo4j", "809434255wzw"));

    public Neo4jGraph doTestNeo4j(){
        String cypher = "MATCH (n) RETURN n";
        String cypher1 = "MATCH p=()-->() RETURN p";
        Driver driver = GraphDatabase.driver("bolt://8.130.96.64:7687", AuthTokens.basic("neo4j", "809434255wzw"));
        Session session = driver.session();

        Result result = session.run(cypher);
        Neo4jGraph graph = Neo4jGraph.parse(result);

        System.out.println("================");
        System.out.println(graph);
        System.out.println("================");

        return graph;
    }

    public Neo4jGraph doTestNeo4j1(){
        String cypher = "MATCH p=()-[r:`检查方法`]->() RETURN p";
        Driver driver = GraphDatabase.driver("bolt://8.130.96.64:7687", AuthTokens.basic("neo4j", "809434255wzw"));
        Session session = driver.session();

        Result result = session.run(cypher);
        Neo4jGraph graph = Neo4jGraph.parse(result);

        System.out.println("================");
        System.out.println(graph);
        System.out.println("================");

        return graph;
    }

    @Override
    public Neo4jGraph getAllGraph() {
        Neo4jGraph graph = new Neo4jGraph();
        graph.setNodes(getAllNode());
        graph.setEdges(getAllEdge());
        return graph;
    }

    @Override
    public Set<Neo4jNode> getAllNode() {
        // 查询全部节点
        String cypher = "MATCH (n) RETURN n";
        Session session = driver.session();
        Result result = session.run(cypher);
        Neo4jGraph graph = Neo4jGraph.parse(result);
        Set<Neo4jNode> nodes = graph.getNodes();
        System.out.println(nodes);
        return nodes;
    }

    @Override
    public Set<Neo4jEdge> getAllEdge() {
        // 查询全部节点
        String cypher = "MATCH p=()-->() RETURN p";
        Session session = driver.session();
        Result result = session.run(cypher);
        Neo4jGraph graph = Neo4jGraph.parse(result);
        Set<Neo4jEdge> edges = graph.getEdges();
        return edges;
    }

    // 查询n度子图
    @Override
    public Neo4jGraph getNodeDetailByNodeId(GraphReq req) {
        System.out.println("开始查询节点详情-以该节点为中心的N度网络");
        System.out.println("nodeId = " + req.getNodeId());
        System.out.println("degree = " + req.getDegree());

        String cypher = "MATCH path = (n) " +
                "WHERE id(n) = " + req.getNodeId() + " " +
                "WITH nodes(path) AS centerNodes " +
                "UNWIND centerNodes AS centerNode " +
                "MATCH network = (centerNode)-[*.."+req.getDegree()+"]-(related) " +
                "RETURN nodes(network) AS nodes, relationships(network) AS relationships";
        System.out.println(cypher);
        Session session = driver.session();
        Result result = null;
        result = session.run(cypher);
        Neo4jGraph graph = Neo4jGraph.parse1(result);
        if(ObjectUtil.isEmpty(graph.getNodes()) && ObjectUtil.isEmpty(graph.getEdges())){
            String cypher2 = "MATCH (n) WHERE id(n) = " + req.getNodeId() + " RETURN n";
            result = session.run(cypher2);
            graph = Neo4jGraph.parse(result);
        }
        return graph;
    }

    @Override
    public Integer updateNodeDetail(GraphReq req) {
        String cypher = "MATCH (n) WHERE id(n) = " + req.getNodeId() + " SET ";
        StringBuilder paramsStr = new StringBuilder();
        for (Map.Entry<String, Object> entry : req.getProps().entrySet()) {
            paramsStr.append("n.");
            paramsStr.append(entry.getKey());
            paramsStr.append(" = ").append("'");
            paramsStr.append(entry.getValue()).append("'");
            paramsStr.append(",");
        }
        if(paramsStr.length() > 0){
            paramsStr.deleteCharAt(paramsStr.length() - 1);
        }
        cypher+=paramsStr;
        System.out.println(cypher);
        Session session = driver.session();
        Result result = session.run(cypher);

        return 0;
    }

    @Override
    public Neo4jNode insertNodeToNeo4j(Neo4jNode node) {
        Session session = driver.session();
        String query = "CREATE (n:" + node.getLabels().get(0) + " {";
        StringBuilder builder = new StringBuilder();
        // 构造Cypher语句
        for (Map.Entry<String, Object> entry : node.getProps().entrySet()) {
         builder.append(entry.getKey() + ": ");
         builder.append("'" + entry.getValue() + "'");
         builder.append(",");
        }

        // 移除最后一个逗号
        if(builder.length() > 0){
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("}) RETURN n");
        query+=builder;

        System.out.println("Cypher   "+query);
        Result result = session.run(query);
        System.out.println("插入完成");

        return Neo4jGraph.parse(result).getNodes().toArray(new Neo4jNode[1])[0];
    }

    @Override
    public Neo4jGraph getEdgeInstanceGraph(KgEdgeInstance instance) {
        String cypher = "MATCH p=()-[r:`"+instance.getLabel()+"`]->() RETURN p";
        System.out.println("getEdgeInstanceGraph:查询neo4j:cypher:" + cypher);
        Session session = driver.session();
        Result res = session.run(cypher);
        Neo4jGraph resGraph = Neo4jGraph.parse(res);

        return resGraph;
    }

    // 新增边
    @Override
    public Neo4jEdge addEdge(KgEdgeInstance kgEdgeInstance) {
        String cypher = "MATCH (startNode) WHERE ID(startNode) = " + kgEdgeInstance.getFromNodeNeo4jId() + "\n"
                + "MATCH (endNode) WHERE ID(endNode) = " + kgEdgeInstance.getToNodeNeo4jId() + "\n"
                + "CREATE (startNode)-[r:" + kgEdgeInstance.getLabel() + " {";
        StringBuilder builder = new StringBuilder();
        if(!ObjectUtil.isNull(kgEdgeInstance.getProps())){
            for (KgEdgeInstaceProperties prop : kgEdgeInstance.getProps()) {
                builder.append(prop.getName());
                builder.append(": ");
                builder.append("'" + prop.getValue() + "'");
                builder.append(",");
            }
        }

        if(builder.length() > 0){
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("}]->(endNode) RETURN r");

        cypher+=builder;

        System.out.println("新建关系实例:cypher:" + cypher);
        Session session = driver.session();
        Result res = session.run(cypher);
        return Neo4jGraph.parse(res).getEdges().toArray(new Neo4jEdge[1])[0];
    }

    @Override
    public Neo4jGraph getByNodeClass(List<KgNodeClass> nodeClassList) {
        String cypher = "";
        int count = 0;
        StringBuilder builder = new StringBuilder();
        StringBuilder builder1 = new StringBuilder();
        for (KgNodeClass nodeClass : nodeClassList) {
            builder.append("MATCH (p")
                    .append(count)
                    .append(":`")
                    .append(nodeClass.getName())
                    .append("`) ")
                    .append("RETURN p" + count + " AS node")
                            .append(" UNION ");
            builder1.append(",p").append(count);
            count++;
        }

        builder.delete(builder.length() - 7, builder.length() - 1);

        builder1.deleteCharAt(0);
        cypher+=builder;
//        cypher+=" RETURN " + builder1;
        System.out.println("getByNodeClass:cypher:\n" + cypher);
        Session session = driver.session();
        Result res = session.run(cypher);
        Neo4jGraph parse = Neo4jGraph.parse(res);
        System.out.println(parse);
        return parse;
    }

    // 通过给定的节点类型查询其中的关系
    @Override
    public Neo4jGraph getEdgeByNodeClass(List<KgNodeClass> nodeClassList) {
        StringBuilder builder = new StringBuilder();
        for (KgNodeClass nodeClass : nodeClassList) {
            builder.append(",'").append(nodeClass.getName()).append("'");
        }
        builder.deleteCharAt(0);
        String cypher = "MATCH (n)-[r]->(m)\n" +
                "WHERE ANY(label IN labels(n) WHERE label IN ["
                + builder + "]) "
                + "AND ANY(label IN labels(m) WHERE label IN ["
                + builder + "]) "
                + "RETURN n, r, m";

        System.out.println("getEdgeByNodeClass:cypher:\n" + cypher);
        Session session = driver.session();
        Result res = session.run(cypher);
        Neo4jGraph parse = Neo4jGraph.parse(res);
        System.out.println(parse);
        return parse;
    }

    @Override
    public Neo4jGraph getGraphByNodeOrEdgeClass(List<KgNodeClass> nodeClassList, List<KgEdgeClass> edgeClassList){
        StringBuilder builder = new StringBuilder();
        StringBuilder builder1 = new StringBuilder();

        String cypher = "MATCH (n)-[r]->(m) WHERE 1=1\n";

        if(ObjectUtil.isNotEmpty(nodeClassList)){
            for (KgNodeClass nodeClass : nodeClassList) {
                builder.append(",'").append(nodeClass.getName()).append("'");
            }
            builder.deleteCharAt(0);
            cypher+="AND ANY(label IN labels(n) WHERE label IN ["
                    + builder + "]) "
                    + "AND ANY(label IN labels(m) WHERE label IN ["
                    + builder + "]) \n";
        }

        if(ObjectUtil.isNotEmpty(edgeClassList)){
            for (KgEdgeClass edgeClass : edgeClassList) {
                builder1.append(",'").append(edgeClass.getLabel()).append("'");
            }
            builder1.deleteCharAt(0);
            cypher+="AND type(r) IN ["+builder1+"]\n";
        }

        cypher+="RETURN n, r, m";

        System.out.println("getGraphByNodeOrEdgeClass:cypher:\n" + cypher);

        Result result = driver.session().run(cypher);
        Neo4jGraph parse = Neo4jGraph.parse(result);
        return parse;

    }


    // 图谱路径分析
    @Override
    public Neo4jGraph pathAnalyse(GraphReq req) {
        if(ObjectUtil.isEmpty(req.getFromNode()) || ObjectUtil.isEmpty(req.getToNode())
                || ObjectUtil.isEmpty(req.getFromNode().getName())
                ||ObjectUtil.isEmpty(req.getToNode().getName())){
            System.out.println("起点或终点为空");
            throw new RuntimeException("起点或终点为空");
        }

        // 最大值为5
        if(!req.getIsShortest() && req.getMaxDegree() >= 5){
            req.setMaxDegree(5);
        }

        String cypher = "";
        StringBuilder edgeClassListStr = new StringBuilder();

        if(ObjectUtil.isNotNull(req.getEdgeClassList())){
            for (KgEdgeClass edgeClass : req.getEdgeClassList()) {
                edgeClassListStr.append(",'")
                        .append(edgeClass.getLabel())
                        .append("'");
            }
            edgeClassListStr.deleteCharAt(0);
            edgeClassListStr.insert(0,'[');
            edgeClassListStr.append(']');
        }
        if(req.getIsShortest()){
            cypher+="MATCH (a), (b)\n" +
                    "WHERE id(a) = " + req.getFromNode().getNeo4jId() + " " +
                    "AND id(b) = " + req.getToNode().getNeo4jId() + " \n" +
                    "MATCH p=allShortestPaths((a)-[*]->(b))\n";
            if(ObjectUtil.isNotEmpty(req.getEdgeClassList())){
                cypher+="WHERE all(rel in relationships(p) WHERE type(rel) in " + edgeClassListStr + ") \n";
            }
            cypher+="RETURN p";

        }else{
            cypher+="MATCH p=(a)-[r*.." + req.getMaxDegree() + "]->(b)\n" +
                    "WHERE id(a) = " + req.getFromNode().getNeo4jId() + " AND id(b) = " + req.getToNode().getNeo4jId() + " \n";
            if(ObjectUtil.isNotEmpty(req.getEdgeClassList())){
                cypher+="AND all(rel in relationships(p) WHERE type(rel) IN " + edgeClassListStr + ") \n";

            }
            cypher+="RETURN p";
        }



        System.out.println("cypher = \n" + cypher);

        Session session = driver.session();
        Result run = session.run(cypher);
        Neo4jGraph parse = Neo4jGraph.parse(run);
        showNeo4j(parse);


        return parse;
    }

    // 中心多度探寻
    @Override
    public Neo4jGraph centerMultiDegree(GraphReq req) {

        StringBuilder builderEdge = new StringBuilder();
        StringBuilder builderNode = new StringBuilder();
        if(ObjectUtil.isNotEmpty(req.getEdgeClassList())){

            for (KgEdgeClass edgeClass : req.getEdgeClassList()) {
                builderEdge.append("|").append(edgeClass.getLabel());
            }
            builderEdge.deleteCharAt(0);
        }

        if(ObjectUtil.isNotEmpty(req.getNodeClassList())){
            for (KgNodeClass nodeClass : req.getNodeClassList()) {
                builderNode.append("|").append(nodeClass.getName());
            }
            builderNode.deleteCharAt(0);
        }

//        String cypher = "MATCH (startNode)\n" +
//                "WHERE id(startNode) = " + req.getAnalyseNode().getNeo4jId() +
//                "\n" +
//                "CALL apoc.path.subgraphNodes(startNode, {\n" +
//                "  relationshipFilter: '"+builderEdge+"',\n" +
//                "  labelFilter: '"+builderNode+"',\n" +
//                "  minLevel: 1,\n" +
//                "  maxLevel: "+req.getSelectedDegree()+"\n" +
//                "})\n" +
//                "YIELD node\n" +
//                "MATCH p = shortestPath((startNode)-[*]-(node))\n" +
//                "RETURN startNode, relationships(p) as relationships, node";
        // 向外发散的关系网络(如果不希望得到向外发散的网络，只需要将箭头'>'删除即可)
        String cypher =
                "MATCH path = " +
                        "(n)-[:"+builderEdge+"*.."+req.getSelectedDegree()+"]->(related:"+builderNode+") " +
                        "WHERE id(n) = "+req.getAnalyseNode().getNeo4jId()+" \n" +
                "RETURN nodes(path) AS nodes, relationships(path) AS relationships";

        System.out.println("centerMultiDegree:cypher:\n" + cypher);
        Session session = driver.session();
        Result run = session.run(cypher);
        Neo4jGraph parse = Neo4jGraph.parse1(run);
        showNeo4j(parse);
        return parse;
    }

    // 中心度计算
    /*
    MATCH (n:`疾病`|`根节点`)
    OPTIONAL MATCH (n)-[r]-()
    WITH n, COUNT(r) as degreeCentrality
    RETURN n, degreeCentrality
    ORDER BY degreeCentrality DESC
     */
//    @Override
//    public Map<Object,Integer> centralityCalculation(GraphReq req) {
//        String cypher = "MATCH (n";
//        StringBuilder builder = new StringBuilder();
//        if(ObjectUtil.isNotEmpty(req.getNodeClassList())){
//            for (KgNodeClass nodeClass : req.getNodeClassList()) {
//                builder.append("|")
//                        .append(nodeClass.getName());
//            }
//            // 删除第一个分隔符
//            builder.deleteCharAt(0);
//            cypher+=":";
//            cypher+=builder;
//        }
//
//        cypher += ")\n" +
//                "OPTIONAL MATCH (n)-[r]->()\n" +
//                "WITH n, COUNT(r) as degreeCentrality\n" +
//                "RETURN n, degreeCentrality\n" +
//                "ORDER BY degreeCentrality DESC";
//
//
//        System.out.println("centralityCalculation:cyher:\n" + cypher);
//        Session session = driver.session();
//        Result result = session.run(cypher);
//        Map<Object,Integer> result1 = Neo4jGraph.centralityCalculation(result);
//
//        System.out.println(result1);
//
//        return result1;
//    }

    // 中心度计算
    @Override
    public Map<Object,Double> centralityCalculation(GraphReq req) {
        String centralityName =
                createGraphProject("centrality",
                        createNodeClassStr(req.getNodeClassList()),
                        createEdgeClassStr(req.getEdgeClassList()));
        System.out.println(centralityName);
        String cypher = null;
        Integer model = req.getSelectedCenterDegreeModel();

        Map<Object,Double> map = new HashMap<>();

        if(model == 1){
            System.out.println("执行 度中心度 计算");
            // 度中心度
            cypher = "CALL gds.degree.stream('" + centralityName + "')\n" +
                    "YIELD nodeId, score\n" +
                    "RETURN gds.util.asNode(nodeId), score AS followers\n" +
                    "ORDER BY followers DESC";
        }else if(model == 2){
            System.out.println("执行 接近中心度 计算");
            // 接近中心度
            cypher = "CALL gds.closeness.stream('" + centralityName + "')\n" +
                    "YIELD nodeId, score\n" +
                    "RETURN gds.util.asNode(nodeId), score AS followers\n" +
                    "ORDER BY followers DESC";
        }else{
            System.out.println("执行 介中心度 计算");
            // 介中心度
            cypher = "CALL gds.betweenness.stream('" + centralityName + "')\n" +
                    "YIELD nodeId, score\n" +
                    "RETURN gds.util.asNode(nodeId), score AS followers\n" +
                    "ORDER BY followers DESC";
        }

        System.out.println("centralityCalculation:cypher:\n");
        System.out.println(cypher);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Result result = driver.session().run(cypher);
        List<Record> records = result.list();
        for (Record record : records) {
            List<Value> values = record.values();
            Neo4jNode node = null;
            Double score = null;
            for (Value value : values) {
                Type type = value.type();
                if (type.name().equals(TypeConstructor.NODE.name())) {
                    node = new Neo4jNode(value.asNode());
                }else{
                    score = value.asDouble();
                }
            }


            map.put(node,score);
        }

        System.out.println(map);

        dropGraphProject(centralityName);

        return map;

    }

    public String createNodeClassStr(List<KgNodeClass> list){
        if(ObjectUtil.isEmpty(list)){
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (KgNodeClass it : list) {
            builder.append(",")
                    .append("'")
                    .append(it.getName())
                    .append("'");
        }
        builder.deleteCharAt(0);
        return "[" + builder + "]";

    }

    public String createEdgeClassStr(List<KgEdgeClass> list){
        if(ObjectUtil.isEmpty(list)){
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (KgEdgeClass it : list) {
            builder.append(",")
                    .append("'")
                    .append(it.getLabel())
                    .append("'");
        }
        builder.deleteCharAt(0);
        return "[" + builder + "]";
    }

    // 用于创建neo4j的数据投影，用于进行中心度、相似度计算
    public String createGraphProject(String projectName,String nodeClassListStr,String edgeClassListStr){

        String name = projectName +"-"+ LocalDateTime.now();
        String cypher = "CALL gds.graph.project('" + name  + "'" +
                // 如果类型列表为空，就匹配全部的类型
                "," + (ObjectUtil.isEmpty(nodeClassListStr) ? "'*'" : nodeClassListStr) +
                "," + (ObjectUtil.isEmpty(edgeClassListStr) ? "'*'" : edgeClassListStr) +
                ") YIELD graphName";
        System.out.println("createGraphProject:cypher:\n");
        System.out.println(cypher);

        driver.session().run(cypher);

        return name;
    }

    public void dropGraphProject(String name){
        System.out.println("开始执行删除graphProject");
        String cypher = "CALL gds.graph.drop('" + name + "')";
        System.out.println("dropGraphProject:cypher:");
        System.out.println(cypher + "\n");
        driver.session().run(cypher);
    }

    // 根据节点名称进行模糊查询
    @Override
    public Neo4jGraph getNodeByName(String nodeName) {
        String cypher = "MATCH (p)\n" +
                "WHERE p.name CONTAINS '"+nodeName+"'\n" +
                "RETURN p";
        System.out.println("getNodeByName:cypher:\n" + cypher);
        Result result = driver.session().run(cypher);
        Neo4jGraph parse = Neo4jGraph.parse(result);
        return parse;
    }

    @Override
    public Neo4jGraph getEdgeByFromOrToNodeName(String fromNodeName, String toNodeName) {
        String cypher = "MATCH (start)-[r]->(end)\n" +
                "WHERE start.name CONTAINS '"+fromNodeName+"' " +
                "AND end.name CONTAINS '"+toNodeName+"'\n" +
                "RETURN start, r, end";
        System.out.println("getEdgeByFromOrToNodeName:cypher:\n" + cypher);
        Result result = driver.session().run(cypher);
        Neo4jGraph parse = Neo4jGraph.parse(result);
        return parse;
    }


    // 打印图谱内容
    public void showNeo4j(Neo4jGraph parse){
        System.out.println(parse);
        for (Neo4jNode node : parse.getNodes()) {
            System.out.println(node + "\n");
        }
        System.out.println(parse.getNodes().size());
        for (Neo4jEdge edge : parse.getEdges()) {
            System.out.println(edge + "\n");
        }
        System.out.println(parse.getEdges().size());
    }

    // 数据统计
    @Override
    public List<Map<String,Integer>> statistic(){

        List<Map<String,Integer>> resMapList = new ArrayList<>();

        Map<String,Integer> nodeMap = new HashMap<>();
        Map<String,Integer> edgeMap = new HashMap<>();

        // 节点的类型与数量
        Result nodeResult = driver.session().run("MATCH (n)\n" +
                "RETURN labels(n) AS Type, count(*) AS Count");

        List<Record> records = nodeResult.list();
        for (Record record : records) {
            String curKey = "";
            Integer curVal = 0;
            for (Value value : record.values()) {
                //System.out.println(value);
                if("LIST OF ANY?".equals(value.type().name())){
                    curKey = value.asList().get(0).toString();
                }else if("INTEGER".equals(value.type().name())){
                    curVal = Integer.valueOf(value.toString());
                }
            }
            nodeMap.put(curKey,curVal);
        }
        System.out.println(nodeMap);

        // 关系的类型与数量
        Result edgeResult = driver.session().run("MATCH ()-[r]->()\n" +
                "RETURN type(r) AS Type, count(*) AS Count");

        List<Record> records2 = edgeResult.list();
        for (Record record : records2) {
            String curKey = "";
            Integer curVal = 0;
            for (Value value : record.values()) {
                if("STRING".equals(value.type().name())){
                    curKey = value.asString();
                }else if("INTEGER".equals(value.type().name())){
                    curVal = value.asInt();
                }
            }
            edgeMap.put(curKey,curVal);
        }
        System.out.println(edgeMap);

        resMapList.add(nodeMap);
        resMapList.add(edgeMap);

        return resMapList;
    }

    // 根据neo4jId删除节点
    @Override
    public Integer deleteNodeByNeo4jId(GraphReq req){
        Long nodeId = req.getNodeId();
        if(ObjectUtil.isEmpty(nodeId)){
            System.out.println("nodeNeo4jId为空");
            return 0;
        }
        // 删除节点的时候会同时删除与之相连的关系
        String cypher = "MATCH (n)\n" +
                "WHERE id(n) = "+nodeId+"\n" +
                "DETACH DELETE n;";
        System.out.println("deleteNodeByNeo4jId:cypher:\n");
        System.out.println(cypher);
        driver.session().run(cypher);


        // 删除节点实例
        System.out.println("删除节点实例");
        Integer count1 = nodeInstanceService.deleteNodeInstanceByNeo4jId(nodeId);

        // 删除与之相连的关系
        System.out.println("删除与之相连的关系");
        Integer count2 = edgeInstanceService.deleteEdgeByNodeNeo4jId(nodeId);

        // 删除节点的属性
//        System.out.println("删除节点的属性");
        Integer count3 = 0;

        // 删除关系的属性
//        System.out.println("删除关系的属性");
        Integer count4 = 0;
        return count1+count2+count3+count4;
    }

    // 删除关系
    @Override
    public int deleteEdgeByNeo4jId(GraphReq req) {

//        edgeInstanceService.deleteEdgeByNodeNeo4jId(nodeId);

        Long edgeId = req.getEdgeId();
        if(ObjectUtil.isEmpty(edgeId)){
            System.out.println("edgeNeo4jId为空");
            return 0;
        }
        // 删除节点的时候会同时删除与之相连的关系
        String cypher = " MATCH ()-[r]-()\n" +
                "        WHERE id(r) = "+edgeId+"\n" +
                "        DELETE r";
        System.out.println("deleteEdgeByNeo4jId:cypher:\n");
        System.out.println(cypher);
        driver.session().run(cypher);

        Integer count2 = edgeInstanceService.deleteEdgeByNeo4jId(edgeId);

        return 0;
    }

    // 相似度计算
    @Override
    public List<GraphDto> centralitySimilarity(GraphReq req) {
        if(ObjectUtil.isEmpty(req.getEdgeClassList())){
            throw new RuntimeException("关系类型不能为空");
        }
        if(ObjectUtil.isEmpty(req.getCalculateNode())){
            throw new RuntimeException("相似度计算实体为空");
        }

        StringBuilder builder = new StringBuilder();
        for (KgEdgeClass edgeClass : req.getEdgeClassList()) {
            builder.append(",")
                    .append("'")
                    .append(edgeClass.getLabel())
                    .append("'");
        }
        builder.deleteCharAt(0);

        // 默认计算与选中主体同类型的实体
        String cypher = "MATCH (p1)-[r]-(to1) \n" +
                "WHERE id(p1) = " + req.getCalculateNode().getNeo4jId() +
                "\nAND type(r) in ["+builder+"]" +
                "\nWITH p1, collect(id(to1)) AS collect1\n" +
                "\n" +
                "MATCH (p2:`"+req.getCalculateNode().getLabel()+"`)-[r]-(to2) \n" +
                "WHERE p1 <> p2 AND type(r) in ["+builder+"]" +
                "\nWITH p1, collect1, p2, collect(id(to2)) AS collect2\n" +
                "\n" +
                "RETURN id(p1),id(p2),collect1,collect2,\n" +
                "                 gds.similarity.jaccard(collect1,collect2) AS jaccardSimilarity";


        System.out.println("centralitySimilarity:cypher:\n" + cypher);

        Result result = driver.session().run(cypher);

        List<GraphDto> dtoList = new ArrayList<>();

        List<Record> records = result.list();
        for (Record record : records) {
            GraphDto dto = new GraphDto();
            for (int i = 0; i < record.values().size(); i++) {
                Value value = record.values().get(i);
                switch (i){
                    case 0: dto.setMainNodeId(value.asLong()); break;
                    case 1: dto.setToNodeId(value.asLong()); break;
                    case 2: dto.setMainNodeRelIdList(value.asList(new Function<Value, Long>() {
                        @Override
                        public Long apply(Value it) {
                            return it.asLong();
                        }
                    })); break;
                    case 3: dto.setToNodeRelIdList(value.asList(it -> it.asLong())); break;
                    case 4: dto.setJaccardSimilarity(value.asDouble()); break;
                    default:
                        System.out.println(value + " 不在处理范围内");
                }

            }
            dtoList.add(dto);
        }

        for (GraphDto dto : dtoList) {
            System.out.println(dto);
        }

        return dtoList;
    }

    @Override
    public List<String> getAllExistNodeClass() {
        //        CALL db.labels()
        List<String> res = new ArrayList<>();
        Result result = driver.session().run("CALL db.labels()");
        List<Record> records = result.list();
        for (Record record : records) {
            for (Value value : record.values()) {
                res.add(value.asString());
            }
        }
        return res;
    }

    @Override
    public AjaxResult cleanGraphProject(){
        List<String> graphProjectName = new ArrayList<>();

        Result result = driver.session().run("call gds.graph.list()");
        for (Record record : result.list()) {
            graphProjectName.add(record.values().get(1).asString());

            dropGraphProject(record.values().get(1).asString());
        }

        System.out.println(graphProjectName);

        return AjaxResult.success();
    }

    @Override
    public Neo4jGraph getByNodeNameList(List<String> nodeNameList){
        if(ObjectUtil.isEmpty(nodeNameList)){
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (String s : nodeNameList) {
            builder.append(",")
                    .append("'")
                    .append(s)
                    .append("'");
        }
        builder.deleteCharAt(0);
        builder.insert(0,'[');
        builder.append("]");
        System.out.println("nodeNameList = " + builder);
        String cypher = "MATCH (n) where n.name in " + builder + " return n";
        System.out.println("getByNodeNameList:cypher:" + cypher);
        Result result = driver.session().run(cypher);

        Neo4jGraph parse = Neo4jGraph.parse(result);
        System.out.println(parse);
        return parse;
    }

    @Override
    public Neo4jGraph getNodesByFromNoeIdAndToNodeLabel(Long fromNodeId,String toNodeLabel){
        /*
        MATCH (f)-[r]-(t:`治疗方法`) WHERE id(f) = 1 RETURN t
         */
        String cypher = "MATCH (f)-[r]-(t:`" + toNodeLabel + "`) WHERE id(f) = " + fromNodeId + " RETURN f,r,t";
        System.out.println("getNodesByFromNoeIdAndToNodeLabel:cypher:");
        System.out.println(cypher);

        Result result = driver.session().run(cypher);

        Neo4jGraph parse = Neo4jGraph.parse(result);
        System.out.println(fromNodeId + " -- " + toNodeLabel + " -- " + parse.getNodes().size());
        return parse;
    }

    @Override
    public Neo4jGraph getNodesByIds(List<Long> ids) {
        // 根据id获得节点
        if(ObjectUtil.isEmpty(ids)){
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (Long id : ids) {
            builder.append(",")
                    .append(id);
        }
        builder.deleteCharAt(0);

        String cypher = "MATCH (n) WHERE id(n) in [" + builder + "] return n";
        System.out.println("getNodesByIds:cypher:");
        System.out.println(cypher);
        Result result = driver.session().run(cypher);
        Neo4jGraph parse = Neo4jGraph.parse(result);
        return parse;
    }

    @Override
    public Neo4jGraph insertPropByNodeId(Long id,String name,String value){
        String cypher = "MATCH (n) WHERE id(n) = " + id + " SET n." + name + " = '" + value + "'";
        System.out.println("insertPropByNodeId:cypher:");
        System.out.println(cypher);
        Result result = driver.session().run(cypher);
        return null;
    }

    @Override
    public Neo4jGraph removePropByNodeId(Long id,String name,String value){
        String cypher = "MATCH (n) WHERE id(n) = " + id + " REMOVE n." + name;
        System.out.println("removePropByNodeId:cypher:");
        System.out.println(cypher);
        Result result = driver.session().run(cypher);
        return null;
    }

    @Override
    public Neo4jGraph insertPropByEdgeId(Long id,String name,String value){
        String cypher = "MATCH ()-[n]->() WHERE id(n) = " + id + " SET n." + name + " = '" + value + "'";
        System.out.println("insertPropByEdgeId:cypher:");
        System.out.println(cypher);
        Result result = driver.session().run(cypher);
        return null;
    }

    @Override
    public Neo4jGraph removePropByEdgeId(Long id,String name,String value){
        String cypher = "MATCH ()-[n]->() WHERE id(n) = " + id + " REMOVE n." + name;
        System.out.println("removePropByEdgeId:cypher:");
        System.out.println(cypher);
        Result result = driver.session().run(cypher);
        return null;
    }

    @Override
    public Neo4jGraph getSingleEdgeByEdgeId(Long edgeId){
        String cypher = "MATCH p=()-[r]-() WHERE id(r) = " + edgeId + " RETURN p";
        System.out.println("getSingleEdgeByEdgeId:cypher:");
        System.out.println(cypher);
        Result result = driver.session().run(cypher);

        Neo4jGraph parse = Neo4jGraph.parse(result);
        return parse;
    }
}
