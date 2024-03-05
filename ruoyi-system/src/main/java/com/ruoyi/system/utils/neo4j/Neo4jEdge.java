package com.ruoyi.system.utils.neo4j;

import lombok.Data;
import org.neo4j.driver.types.Relationship;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
public class Neo4jEdge {

    private long edgeId;

    private Object srcId;

    private Object dstId;

    private String label;

    private Map<String, Object> props = new HashMap<>();

    public Neo4jEdge(Relationship relationship) {
        srcId = relationship.startNodeId();
        dstId = relationship.endNodeId();
        edgeId = relationship.id();
        label = relationship.type();
        parseProps(relationship);
    }

    private void parseProps(Relationship relationship) {
        Map<String, Object> data = relationship.asMap();
        Set<String> keys = data.keySet();
        for (String key : keys) {
            props.put(key, data.get(key));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Neo4jEdge that = (Neo4jEdge) o;
        //if (!edgeId.equals(that.edgeId)) return false;
        if (!srcId.equals(that.srcId)) return false;
        if (!dstId.equals(that.dstId)) return false;
        return label.equals(that.label);
    }

    @Override
    public int hashCode() {
        int result = srcId.hashCode();
        result = 31 * result + dstId.hashCode();
        result = 31 * result + label.hashCode();
        result = 31 * result + (int) (edgeId ^ (edgeId >>> 32));
        return result;
    }
}
