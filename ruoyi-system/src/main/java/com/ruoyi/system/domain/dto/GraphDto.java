package com.ruoyi.system.domain.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class GraphDto {
    // 相似度计算相关
    // 计算主体id
    private Long mainNodeId;

    // 被计算节点id
    private Long toNodeId;

    // 主体节点的关联节点集合
    private List<Long> mainNodeRelIdList;

    // 被计算节点的关联节点集合
    private List<Long> toNodeRelIdList;

    // jaccard相似度
    private Double jaccardSimilarity;

}
