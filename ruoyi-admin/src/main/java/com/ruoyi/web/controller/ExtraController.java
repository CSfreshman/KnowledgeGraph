package com.ruoyi.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.WordDictionary;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.system.req.ExtraReq;
import com.ruoyi.system.service.TestNeo4jService;
import com.ruoyi.system.utils.neo4j.Neo4jGraph;
import com.ruoyi.system.utils.neo4j.Neo4jNode;
import org.neo4j.driver.*;
import org.neo4j.driver.internal.types.TypeConstructor;
import org.neo4j.driver.types.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/extra")
public class ExtraController {
    private static JiebaSegmenter segmenter = new JiebaSegmenter();
    private static final String SegmentWordKey = "segmentWord";
    private static Set<String> punctuationMarksSet = new HashSet<>();
    // 标点符号集合
    private static String[] punctuationMarks = {
            ",",
            ".",
            " ",
            "?",
            "!",
            "，",
            "。",
            "、",
    };
    @Autowired
    private RedisCache redisCache;

    @Autowired
    private TestNeo4jService neo4jService;

    // 静态代码块，加载资源
    static {
        WordDictionary.getInstance().init(Paths.get("E:\\data\\GraduateDesign\\RuoYi-Vue-master\\ruoyi-system\\conf"));

        for (String punctuationMark : punctuationMarks) {
            punctuationMarksSet.add(punctuationMark);
        }

    }

    @PostMapping("/participle")
    public Object participle(@RequestBody ExtraReq req){
        System.out.println(req);
        Map<String,String> segmentWordKey = redisCache.redisTemplate.opsForHash().entries(SegmentWordKey);
        List<String> strings = segmenter.sentenceProcess(req.getSymptomsDesc());

        Set<String> match = new HashSet<>();

        for (String str : strings) {
            // 跳过标点符号
            if(punctuationMarksSet.contains(str)){
                continue;
            }

            if(segmentWordKey.keySet().contains(str)){
                System.out.println(str + " ============= " + segmentWordKey.get(str));
                match.add(segmentWordKey.get(str));
            }else {
                System.out.println(str);
            }

        }

        // 匹配的症状
        System.out.println(match);
        Neo4jGraph graph = neo4jService.getByNodeNameList(match.stream().collect(Collectors.toList()));

        // 只返回匹配的节点
        return graph.getNodes();
    }

    // 执行分析动作
    @PostMapping("/executeDiagnose")
    public Object executeDiagnose(@RequestBody ExtraReq req){
        System.out.println(req);
        if(ObjectUtil.isEmpty(req.getSelectedNodeList())){
            return null;
        }
        // 得到选中的节点的id列表
        Set<Long> neo4jNodeIdSet = new HashSet<>();
        for (Neo4jNode neo4jNode : req.getSelectedNodeList()) {
            neo4jNodeIdSet.add(Long.valueOf(String.valueOf(neo4jNode.getId())));
        }

        System.out.println(neo4jNodeIdSet);

        // 获得所有疾病的症状的集合
        Driver driver = GraphDatabase.driver("bolt://8.130.96.64:7687", AuthTokens.basic("neo4j", "809434255wzw"));


        String cypher = "MATCH (n:`疾病`)-[r:`疾病症状`]->(t) RETURN n,collect(id(t))";
        Result result = driver.session().run(cypher);
        if(result == null) return null;
        Map<Neo4jNode,List<Object>> diseaseSymptomsMap = new HashMap<>();
        for (Record record : result.list()) {
            Neo4jNode node = null;
            List<Object> symptomsIdList = null;
            for (Value value : record.values()) {
                Type type = value.type();
                if (type.name().equals(TypeConstructor.NODE.name())) {
                    node = new Neo4jNode(value.asNode());
                }else{
                    symptomsIdList = value.asList();
                }
            }
            diseaseSymptomsMap.put(node,symptomsIdList);
        }

        System.out.println(diseaseSymptomsMap);

        // 计算jaccard相似度
        Map<Neo4jNode,Double> jaccardMap = new HashMap<>();
        for (Map.Entry<Neo4jNode, List<Object>> entry : diseaseSymptomsMap.entrySet()) {
            Set<Long> set1 = new HashSet<>(entry.getValue().stream().map(it->(Long) it).collect(Collectors.toList()));
            Set<Long> set2 = neo4jNodeIdSet.stream().map(it->(Long) it).collect(Collectors.toSet());
            double jaccardSimilarity = calculateJaccardSimilarity(set1, set2);
//            double jaccardSimilarity = 0;
            jaccardMap.put(entry.getKey(),jaccardSimilarity);
        }

        List<Map.Entry<Neo4jNode, Double>> list = new ArrayList<Map.Entry<Neo4jNode, Double>>(jaccardMap.entrySet());
        Collections.sort(list, (o1, o2) -> Double.compare(o2.getValue(),o1.getValue()));

        for (Map.Entry<Neo4jNode, Double> entry : list) {
            System.out.println(entry.getKey().getLabel() + " -- " + entry.getValue());
        }


        return list;
    }

    // 计算Jaccard相似度
    public static double calculateJaccardSimilarity(Set<Long> set1, Set<Long> set2) {
        // 交集
        Set<Long> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        // 并集
        Set<Long> union = new HashSet<>(set1);
        union.addAll(set2);

        if (union.size() == 0) {
            // 如果并集为空，则相似度定义为1（两个空集相似度为1）
            return 1.0;
        } else {
            return (double) intersection.size() / union.size();
        }
    }

    public static void main(String[] args) {
        Set<Long> set1 = new HashSet<>();
        set1.add(1l);
        set1.add(2l);
        set1.add(3l);
        Set<Long> set2 = new HashSet<>();
        set2.add(1l);
        set2.add(2l);
        set2.add(3l);
        set2.add(4l);
        set2.add(5l);

        System.out.println(calculateJaccardSimilarity(set1,set2));

    }
}
