package com.ruoyi.system.utils.neo4j;

import lombok.Data;
import org.neo4j.driver.types.Relationship;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
public class Neo4jEdge {

    private long id;

    private Object from;

    private Object to;

    private String label;

    private Map<String, Object> props = new HashMap<>();

    public Neo4jEdge(Relationship relationship) {
        from = relationship.startNodeId();
        to = relationship.endNodeId();
        id = relationship.id();
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
        //if (!id.equals(that.id)) return false;
        if (!from.equals(that.from)) return false;
        if (!to.equals(that.to)) return false;
        return label.equals(that.label);
    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + to.hashCode();
        result = 31 * result + label.hashCode();
        result = 31 * result + (int) (id ^ (id >>> 32));
        return result;
    }
}
