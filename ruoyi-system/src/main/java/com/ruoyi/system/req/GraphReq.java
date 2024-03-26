package com.ruoyi.system.req;

import com.ruoyi.system.domain.KgEdgeClass;
import com.ruoyi.system.domain.KgNodeClass;
import com.ruoyi.system.domain.KgNodeInstance;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GraphReq {
    private Long nodeId;

    private int degree;

    private Map<String, Object> props;

    private String nodeName;

    private String edgeName;

    private List<KgNodeClass> nodeClassList;

    private List<KgEdgeClass> edgeClassList;

    // 路径分析
    // 参与分析的关系类型
    private List<KgEdgeClass> edgeList;

    // 起点
    private KgNodeInstance fromNode;

    // 终点
    private KgNodeInstance toNode;

    // 是否是最短路径
    private Boolean isShortest;

    // 最大度数
    private Integer maxDegree;

    // 中心多度探寻
    // 参与分析的关系类型
//    private List<KgEdgeClass> edgeList;

    // 参与分析的关系类型
    private List<KgNodeClass> nodeList;

    // 选择的度数
    private Integer selectedDegree;

    // 分析实体
    private KgNodeInstance analyseNode;

    // 中心度计算开始
    private Integer selectedCenterDegreeModel;

    // 中心度计算结束

}
