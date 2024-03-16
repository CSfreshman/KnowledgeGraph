package com.ruoyi.system.utils.neo4j;
import cn.hutool.core.util.ObjectUtil;
import lombok.Data;
import org.neo4j.driver.types.Node;

import java.util.*;

@Data
public class Neo4jNode {

    private Object id;

    // 这里并不应该使用label，因为这个值并不是用来表示标签的意思，而是用来表示props中的name
    // 但是由于涉及的范围比较广，所以就将错就错，不改了
    // 真实的标签应该是labels属性中的数据
    private String label;

    // 一般来说只有一个，就是node属于的标签
    private List<String> labels = new ArrayList<>();

    private Map<String, Object> props = new HashMap<>();

    public Neo4jNode(Node node) {
        id = node.id();
        parseLabels(node);
        parseProp(node);

        if(ObjectUtil.isNotNull(props) && ObjectUtil.isNotNull(props.get("name"))){
            label = props.get("name").toString();
        }
    }

    public Neo4jNode() {

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
