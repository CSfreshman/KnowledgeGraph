package com.ruoyi.system.service;

import com.ruoyi.system.utils.neo4j.Neo4jEdge;
import com.ruoyi.system.utils.neo4j.Neo4jGraph;
import com.ruoyi.system.utils.neo4j.Neo4jNode;

import java.util.Set;

public interface TestNeo4jService {
    Neo4jGraph doTestNeo4j();
    Neo4jGraph doTestNeo4j1();

    Neo4jGraph getAllGraph();

    Set<Neo4jNode> getAllNode();
    Set<Neo4jEdge> getAllEdge();
}
