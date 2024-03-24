package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.system.domain.KgEdgeClass;
import com.ruoyi.system.domain.KgEdgeInstaceProperties;
import com.ruoyi.system.domain.KgEdgeInstance;
import com.ruoyi.system.domain.KgNodeClass;
import com.ruoyi.system.req.GraphReq;
import com.ruoyi.system.service.TestNeo4jService;
import com.ruoyi.system.utils.neo4j.Neo4jEdge;
import com.ruoyi.system.utils.neo4j.Neo4jGraph;
import com.ruoyi.system.utils.neo4j.Neo4jNode;
import org.neo4j.driver.*;
import org.neo4j.driver.internal.InternalResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Service
public class TestNeo4jServiceImpl implements TestNeo4jService {
    @Resource
    private Neo4jClient neo4jClient;

    @Resource
    private Neo4jTemplate neo4jTemplate;

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
            paramsStr.append(" = ");
            paramsStr.append(entry.getValue());
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
                    .append("`)")
                    .append("RETURN p" + count + " AS node")
                            .append(" UNION ");
            builder1.append(",p").append(count);
            count++;
        }
        // 删除逗号
        if(nodeClassList.size() > 1){
            builder.delete(builder.length() - 7, builder.length() - 1);
        }
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
                    "MATCH p=allShortestPaths((a)-[*]-(b))\n";
            if(ObjectUtil.isNotEmpty(req.getEdgeClassList())){
                cypher+="WHERE all(rel in relationships(p) WHERE type(rel) in " + edgeClassListStr + ") \n";
            }
            cypher+="RETURN p";

        }else{
            cypher+="MATCH p=(a)-[r*.." + req.getMaxDegree() + "]-(b)\n" +
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
        System.out.println(parse);
        System.out.println(parse.getNodes());
        System.out.println(parse.getNodes().size());
        System.out.println(parse.getEdges());
        System.out.println(parse.getEdges().size());


        return parse;
    }

    // 中心多度探寻
    @Override
    public Neo4jGraph centerMultiDegree(GraphReq req) {
        return null;
    }
}
