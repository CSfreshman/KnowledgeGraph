package com.ruoyi.system.service.impl;

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
        Session session = driver.session();
        Result result = null;
        try{
            result = session.run(cypher);
        }catch (Exception e){
            e.printStackTrace();
        }


        Neo4jGraph graph = Neo4jGraph.parse1(result);

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
}
