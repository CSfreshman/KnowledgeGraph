package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.KgEdgeClass;
import com.ruoyi.system.domain.KgEdgeInstance;
import com.ruoyi.system.domain.KgNodeClass;
import com.ruoyi.system.domain.KgNodeInstance;
import com.ruoyi.system.domain.dto.GraphDto;
import com.ruoyi.system.req.GraphReq;
import com.ruoyi.system.utils.neo4j.Neo4jEdge;
import com.ruoyi.system.utils.neo4j.Neo4jGraph;
import com.ruoyi.system.utils.neo4j.Neo4jNode;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TestNeo4jService {
    Neo4jGraph doTestNeo4j();
    Neo4jGraph doTestNeo4j1();

    Neo4jGraph getAllGraph();

    Set<Neo4jNode> getAllNode();
    Set<Neo4jEdge> getAllEdge();

    Neo4jGraph getNodeDetailByNodeId(GraphReq valueOf);

    Integer updateNodeDetail(GraphReq req);

    // 插入一个节点到neo4j中
    Neo4jNode insertNodeToNeo4j(Neo4jNode node);

    Neo4jGraph getEdgeInstanceGraph(KgEdgeInstance instance);

    Neo4jEdge addEdge(KgEdgeInstance kgEdgeInstance);

    Neo4jGraph getByNodeClass(List<KgNodeClass> nodeClassList);

    Neo4jGraph getEdgeByNodeClass(List<KgNodeClass> nodeClassList);

    Neo4jGraph pathAnalyse(GraphReq req);

    Neo4jGraph centerMultiDegree(GraphReq req);

    Map<Object,Double> centralityCalculation(GraphReq req);

    Neo4jGraph getNodeByName(String nodeName);

    Neo4jGraph getEdgeByFromOrToNodeName(String fromNodeName, String toNodeName);

    Neo4jGraph getGraphByNodeOrEdgeClass(List<KgNodeClass> nodeClassList, List<KgEdgeClass> edgeClassList);

    List<Map<String,Integer>> statistic();

    Integer deleteNodeByNeo4jId(GraphReq req);

    int deleteEdgeByNeo4jId(GraphReq req);

    List<GraphDto> centralitySimilarity(GraphReq req);

    List<String> getAllExistNodeClass();

    AjaxResult cleanGraphProject();

    Neo4jGraph getByNodeNameList(List<String> nodeNameList);

    Neo4jGraph getNodesByFromNoeIdAndToNodeLabel(Long fromNodeId,String toNodeLabel);

    Neo4jGraph getNodesByIds(List<Long> ids);

    // 根据节点id新增属性
    Neo4jGraph insertPropByNodeId(Long id,String name,String value);

    // 根据节点id删除指定属性
    Neo4jGraph removePropByNodeId(Long id,String name,String value);

    // 根据节点id新增属性
    Neo4jGraph insertPropByEdgeId(Long id,String name,String value);

    // 根据节点id删除指定属性
    Neo4jGraph removePropByEdgeId(Long id,String name,String value);

    Neo4jGraph getSingleEdgeByEdgeId(Long edgeId);
}
