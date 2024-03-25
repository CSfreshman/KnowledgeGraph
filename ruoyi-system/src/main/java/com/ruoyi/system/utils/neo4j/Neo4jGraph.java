package com.ruoyi.system.utils.neo4j;

import cn.hutool.core.collection.ConcurrentHashSet;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.types.TypeConstructor;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;
import org.neo4j.driver.types.Type;

import java.util.List;
import java.util.Set;

@Data
@Slf4j
public class Neo4jGraph {
    private Set<Neo4jNode> nodes = new ConcurrentHashSet<>();
    private Set<Neo4jEdge> edges = new ConcurrentHashSet<>();

    /**
     * 解析查询结果，目前仅解析节点（Node）、关系(Relationship)、路径(Path)三种查询结果类型
     * @param result 查询结果集
     * @return Neo4jGraph
     */
    public static Neo4jGraph parse(Result result) {
        if (result == null) return null;
        Neo4jGraph graph = new Neo4jGraph();
        List<Record> records = result.list();
        for (Record record:records){
            List<Value> values = record.values();
            for (Value value : values) {
                Type type = value.type();
                if (type.name().equals(TypeConstructor.NODE.name())) {
                    Node node = value.asNode();
                    graph.addNeo4jNode(new Neo4jNode(node));
                }else if (type.name().equals(TypeConstructor.PATH.name())) {
                    Path path = value.asPath();
                    path.nodes().forEach(node -> graph.addNeo4jNode(new Neo4jNode(node)));
                    path.relationships().forEach(relationship -> graph.addNeo4jEdge(new Neo4jEdge(relationship)));
                }else if (type.name().equals(TypeConstructor.RELATIONSHIP.name())) {
                    Relationship relationship = value.asRelationship();
                    graph.addNeo4jEdge(new Neo4jEdge(relationship));
                }else {
                    if("LIST OF ANY?".equals(type.name())){
                        for (Value value1 : value.values()) {
                            Type type1 = value1.type();
                            if (type1.name().equals(TypeConstructor.RELATIONSHIP.name())) {
                                Relationship relationship = value1.asRelationship();
                                graph.addNeo4jEdge(new Neo4jEdge(relationship));
                            }
                        }
                    }else{
                        log.error("目前不支持{}类型的查询数据解析。", type.name());
                    }

                }
            }
        }
        return graph;
    }

    public void addNeo4jNode(Neo4jNode neo4jNode) {
        nodes.add(neo4jNode);
    }

    public void addNeo4jEdge(Neo4jEdge neo4jEdge) {
        edges.add(neo4jEdge);
    }

    public static Neo4jGraph parse1(Result result) {
        if (result == null) return null;
        Neo4jGraph graph = new Neo4jGraph();
        List<Record> records = result.list();
        for (Record record:records){
            List<Value> values = record.values();
            for (Value value : values) {
                for (Value value1 : value.values()) {
                    Type type = value1.type();
                    if (type.name().equals(TypeConstructor.NODE.name())) {
                        Node node = value1.asNode();
                        graph.addNeo4jNode(new Neo4jNode(node));
                    }else if (type.name().equals(TypeConstructor.PATH.name())) {
                        Path path = value1.asPath();
                        path.nodes().forEach(node -> graph.addNeo4jNode(new Neo4jNode(node)));
                        path.relationships().forEach(relationship -> graph.addNeo4jEdge(new Neo4jEdge(relationship)));
                    }else if (type.name().equals(TypeConstructor.RELATIONSHIP.name())) {
                        Relationship relationship = value1.asRelationship();
                        graph.addNeo4jEdge(new Neo4jEdge(relationship));
                    }else {
                        log.error("目前不支持{}类型的查询数据解析。", type.name());
                    }
                }
            }
        }
        return graph;
    }
}
