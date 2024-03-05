package com.ruoyi.system.utils.neo4j;
import lombok.Data;
import org.neo4j.driver.types.Node;

import java.util.*;

@Data
public class Neo4jNode {

    private Object id;

    private List<String> labels = new ArrayList<>();

    private Map<String, Object> props = new HashMap<>();

    public Neo4jNode(Node node) {
        id = node.id();
        parseLabels(node);
        parseProp(node);
    }


    public void parseLabels(Node node){
        if (node.labels() != null){
            node.labels().forEach(labels::add);
        }
    }

    public void parseProp(Node node) {
        Map<String, Object> propsMap = node.asMap();
        Set<String> keys = propsMap.keySet();
        for (String key : keys){
            props.put(key, propsMap.get(key));
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Neo4jNode vertex = (Neo4jNode) o;
        return id.equals(vertex.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
