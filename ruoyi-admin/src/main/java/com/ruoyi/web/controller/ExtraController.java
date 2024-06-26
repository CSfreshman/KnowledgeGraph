package com.ruoyi.web.controller;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.filter.ExtraProcessor;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.WordDictionary;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.tokenizers.ChineseWordTokenizer;
import com.kennycason.kumo.palette.LinearGradientColorPalette;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.KgOperation;
import com.ruoyi.system.domain.dto.ExtraStatisticDto;
import com.ruoyi.system.domain.vo.ExtraResp;
import com.ruoyi.system.mapper.KgOperationMapper;
import com.ruoyi.system.req.ExtraReq;
import com.ruoyi.system.service.TestNeo4jService;
import com.ruoyi.system.utils.neo4j.Neo4jGraph;
import com.ruoyi.system.utils.neo4j.Neo4jNode;
import com.ruoyi.system.utils.operationUtil.MyFunctionInterface;
import org.neo4j.driver.*;
import org.neo4j.driver.internal.types.TypeConstructor;
import org.neo4j.driver.types.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.awt.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
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

    private static Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "809434255wzw"));

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private TestNeo4jService neo4jService;

    @Resource
    private KgOperationMapper operationMapper;

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
        if(graph == null){
            return new Neo4jGraph();
        }
        ExtraResp resp = new ExtraResp();
        resp.setGraph(graph);
        return resp;
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
            // 如果是这两种疾病 同时性别为男性
            if(("产后抑郁症".equals(node.getLabel()) || "绝经与抑郁症".equals(node.getLabel())) && req.getSex() == 1){
                continue;
            }
            // 年龄
            if(("老年抑郁症".equals(node.getLabel()) || "老年期抑郁症".equals(node.getLabel())) && req.getAge() < 60){
                continue;
            }
            diseaseSymptomsMap.put(node,symptomsIdList);
        }

        System.out.println(diseaseSymptomsMap);

        // 计算jaccard相似度
        Map<Neo4jNode,JSONObject> jaccardMap = new HashMap<>();
        for (Map.Entry<Neo4jNode, List<Object>> entry : diseaseSymptomsMap.entrySet()) {
            Set<Long> set1 = new HashSet<>(entry.getValue().stream().map(it->(Long) it).collect(Collectors.toList()));
            Set<Long> set2 = neo4jNodeIdSet.stream().map(it->(Long) it).collect(Collectors.toSet());
            double jaccardSimilarity = calculateJaccardSimilarity(set1, set2);
//            double jaccardSimilarity = 0;
            JSONObject jsonObject = new JSONObject();
            // 相似度
            jsonObject.putIfAbsent("similarity",jaccardSimilarity);
            // 匹配的节点
            set1.retainAll(set2);
            Neo4jGraph nodesByIds = neo4jService.getNodesByIds(new ArrayList<>(set1));
            if(nodesByIds != null){
                jsonObject.putIfAbsent("matchNodes",nodesByIds.getNodes());
            }


            jaccardMap.put(entry.getKey(),jsonObject);
        }

        // 转成List 完成排序
        List<Map.Entry<Neo4jNode, JSONObject>> list = new ArrayList<Map.Entry<Neo4jNode, JSONObject>>(jaccardMap.entrySet());
        Collections.sort(list, (o1, o2) -> Double.compare((double)o2.getValue().get("similarity"),(double)o1.getValue().get("similarity")));

        for (Map.Entry<Neo4jNode, JSONObject> entry : list) {
            System.out.println(entry.getKey().getLabel() + " -- " + entry.getValue());
        }


        list = list.stream().filter(entry -> Double.compare((double) entry.getValue().get("similarity"), 0) != 0).collect(Collectors.toList());

        // 展示前10条
        if(list.size() > 10){
            list = list.subList(0,10);
        }


        System.out.println(list.size());

//        // 获得疾病的治疗方法
//        for (Map.Entry<Neo4jNode, Double> entry : list) {
//            System.out.println(getNodesByFromNoeIdAndToNodeLabel((Long) entry.getKey().getId(), "治疗方法"));
//        }


        // 埋点日志 记录用户操作
        KgOperation operation = new KgOperation();
        operation.setId(IdUtil.getSnowflakeNextId());
        operation.setName("辅助诊断");
        operation.setType(4);
        // 设置该操作对应的实体名称列表
        Set<String> nodeNameList = new HashSet<>();
        for (Neo4jNode neo4jNode : req.getSelectedNodeList()) {
            nodeNameList.add(neo4jNode.getLabel());
        }
        operation.setParam(JSONUtil.toJsonStr(nodeNameList));
        operation.setTime(DateUtils.getNowDate());
        // 多线程操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                operationMapper.insertKgOperation(operation);
            }
        }).start();

        ExtraResp resp = new ExtraResp();
        resp.setList(list);

        return resp;
    }

    private static Map<String,String> labelMap = new HashMap<>();

    static {
        labelMap.put("治疗方法","treatmentMethod");
        labelMap.put("预防方法","preventMethod");
    }

    @PostMapping("/getDiseaseInfo")
    public Object getDiseaseInfo(@RequestBody ExtraReq req){
        System.out.println(req);
        Long diseaseNodeId = req.getDiseaseNodeId();
        JSONObject jsonObject = new JSONObject();

        for (String s : labelMap.keySet()) {
            Neo4jGraph graph = neo4jService.getNodesByFromNoeIdAndToNodeLabel(diseaseNodeId, s);
            if(graph != null && graph.getNodes() != null)
            jsonObject.putIfAbsent(labelMap.get(s),graph.getNodes());
        }

        return jsonObject;
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
            return (double) intersection.size() / (double) union.size();
        }
    }

    // 生成词云
    @PostMapping("/wordCloud")
    public Object createWordCloudApi(){
        List<KgOperation> kgOperations = operationMapper.selectKgOperationList(new KgOperation());
        Set<String> paramSet = new HashSet<>();
        for (KgOperation kgOperation : kgOperations) {
            JSONArray jsonArray = JSONUtil.parseArray(kgOperation.getParam());
            List<String> list = jsonArray.toList(String.class);
            //System.out.println(list);

            paramSet.addAll(list);
        }
        System.out.println(paramSet);
        paramSet.remove("TEST_PATH");
        createWordCloud(paramSet);
        return null;
    }

    public static void createWordCloud(Set<String> strSet){
        final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        frequencyAnalyzer.setWordFrequenciesToReturn(600);
        frequencyAnalyzer.setMinWordLength(2);
        frequencyAnalyzer.setWordTokenizer(new ChineseWordTokenizer());
        // 可以直接从文件中读取
        //final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(getInputStream("text/chinese_language.txt"));
        final List<WordFrequency> wordFrequencies = new ArrayList<>();
        //加入分词并随机生成权重，每次生成得图片都不一样
        for (String s : strSet){
            wordFrequencies.add(new WordFrequency(s,new Random().nextInt(s.length())));
        }
        //此处不设置会出现中文乱码
        java.awt.Font font = new java.awt.Font("STSong-Light", 2, 18);
        final Dimension dimension = new Dimension(450, 450);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        wordCloud.setPadding(2);
        wordCloud.setBackground(new CircleBackground(255));
        wordCloud.setFontScalar(new SqrtFontScalar(12, 42));
        //设置词云显示的三种颜色，越靠前设置表示词频越高的词语的颜色
        wordCloud.setColorPalette(new LinearGradientColorPalette(Color.RED, Color.BLUE, Color.GREEN, 30, 30));
        wordCloud.setKumoFont(new KumoFont(font));
        wordCloud.setBackgroundColor(new Color(255, 255, 255));
        //因为我这边是生成一个圆形,这边设置圆的半径
        wordCloud.setBackground(new CircleBackground(180));
        wordCloud.build(wordFrequencies);
        wordCloud.writeToFile("d://3.png");
    }

    // 图谱分析计算数据统计
    @PostMapping("/statistic")
    public Object statistic(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Map<String,List<String>> resMap = new HashMap<>();
        List<String> dateList = new ArrayList<>();
        for (int i = 4; i >= 0; i--) {
            dateList.add(sdf.format(DateUtils.toDate(LocalDateTime.now().minusDays(i))));
        }

        System.out.println(dateList);

        List<ExtraStatisticDto> statisticList = operationMapper.statistic(DateUtils.toDate(LocalDateTime.now().minusDays(4)));

        System.out.println(statisticList);
        Map<Integer,List<ExtraStatisticDto>> map = new HashMap<>();
        Map<Integer,Map<String,Long>> dateMap = new HashMap<>();
        for (ExtraStatisticDto item : statisticList) {
            List<ExtraStatisticDto> orDefault = map.getOrDefault(item.getType(), new ArrayList<>());
            orDefault.add(item);
            map.put(item.getType(),orDefault);

            Map<String, Long> orDefaultMap = dateMap.getOrDefault(item.getType(), new HashMap<>());



            orDefaultMap.put(sdf.format(item.getDate()),item.getCount());
            dateMap.put(item.getType(),orDefaultMap);

        }

        // 对每种类型，每一天的数据都要补齐
        for (Map.Entry<Integer, Map<String, Long>> entry : dateMap.entrySet()) {
            Map<String, Long> value = entry.getValue();
            for (String s : dateList) {
                value.putIfAbsent(s,0l);
            }
        }
        // 把图谱检索的合并（1开头）
        Map<String,Long> tempMap = new HashMap<>();
        for (Map.Entry<Integer, Map<String, Long>> entry : dateMap.entrySet()) {
            if(entry.getKey().toString().charAt(0) == '1'){
                Map<String, Long> value = entry.getValue();
                for (Map.Entry<String, Long> entry1 : value.entrySet()) {
                    tempMap.put(entry1.getKey(),tempMap.getOrDefault(entry1.getKey(),0l) + entry1.getValue());
                }
            }
        }
        dateMap.remove(11);
        dateMap.remove(12);
        dateMap.remove(13);
        dateMap.put(1,tempMap);

        Map<Integer,String> nameMap = new HashMap<>();
        nameMap.put(1,"图谱检索");
        nameMap.put(21,"路径分析");
        nameMap.put(22,"中心多度探寻");
        nameMap.put(31,"中心度计算");
        nameMap.put(32,"相似度计算");
        nameMap.put(4,"辅助诊断");

        Map<String,List<Map.Entry<String,Long>>> resData = new HashMap<>();
        for (Map.Entry<Integer, Map<String, Long>> entry : dateMap.entrySet()) {
            String name = nameMap.get(entry.getKey());
            List<Map.Entry<String,Long>> list = new ArrayList<>(entry.getValue().entrySet());
            list.sort((o1, o2) -> o1.getKey().compareTo(o2.getKey()));
            resData.put(name,list);

        }

        JSONObject resJsonObject = new JSONObject();
        resJsonObject.putIfAbsent("name",nameMap);
        resJsonObject.putIfAbsent("data",resData);
        return resJsonObject;

    }


    // 热搜
    @PostMapping("/hotSearch")
    public Object hotSearch(){
        List<KgOperation> kgOperations = operationMapper.selectKgOperationList(new KgOperation());
        // type: paramName : count
        Map<Integer,Map<String,Integer>> map = new HashMap<>();
        map.put(1,new HashMap<>());
        map.put(2,new HashMap<>());
        map.put(3,new HashMap<>());
        map.put(4,new HashMap<>());
        for (KgOperation kgOperation : kgOperations) {
            String typeStr = kgOperation.getType().toString();
            int c = Integer.valueOf(String.valueOf(typeStr.charAt(0)));

            Map<String, Integer> map1 = map.get(c);

            JSONArray jsonArray = JSONUtil.parseArray(kgOperation.getParam());
            List<String> list = jsonArray.toList(String.class);
            for (String s : list) {
                map1.put(s,map1.getOrDefault(s,0) + 1);
            }

        }
        System.out.println(map);

        // 结果排序数量
        Map<Integer,List<Map.Entry<String, Integer>>> resMap = new HashMap<>();
        for (Map.Entry<Integer, Map<String, Integer>> entry : map.entrySet()) {
            Integer key = entry.getKey();
            Map<String, Integer> value = entry.getValue();
            List<Map.Entry<String, Integer>> list = new ArrayList(value.entrySet());
            list.sort((o1, o2) -> o2.getValue() - o1.getValue());
            resMap.put(key,list);

        }

        System.out.println(resMap);




        return resMap;
    }

    public static void main(String[] args) {

        List<String> list = new ArrayList<>();
        list.add("111");
        list.add("122");
        String jsonStr = JSONUtil.toJsonStr(list);
        System.out.println(jsonStr);

        JSONArray jsonArray = JSONUtil.parseArray(jsonStr);
        list = jsonArray.toList(String.class);
        System.out.println(list);

//        final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
//        frequencyAnalyzer.setWordFrequenciesToReturn(600);
//        frequencyAnalyzer.setMinWordLength(2);
//        frequencyAnalyzer.setWordTokenizer(new ChineseWordTokenizer());
//        // 可以直接从文件中读取
//        //final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(getInputStream("text/chinese_language.txt"));
//        final List<WordFrequency> wordFrequencies = new ArrayList<>();
//        // 用后端技术书籍来随机生成词云
//        String [] books = {"Spring实战","Spring源码深度解析","SpringBoot实战",
//                "SpringBoot2精髓","一步一步学SpringBoot2","Spring微服务实战",
//                "Head First Java","Java并发编程实战","深入理解Java 虚拟机",
//                "Head First Design","effective java","J2EE development without EJB",
//                "TCP/IP卷一"," 计算机网络：自顶向下","图解HTTP和图解TCP/IP",
//                "计算机网络","深入理解计算机系统","现代操作系统",
//                "Linux内核设计与实现","Unix网络编程","数据结构与算法",
//                "算法导论","数据结构与算法（Java版）","算法图解，啊哈算法",
//                "剑指offer","LeetCode"," Java编程思想",
//                "Java核心技术卷一","深入理解JVM虚拟机","Java并发编程实战",
//                " Java并发编程艺术","Java性能调优指南","Netty权威指南",
//                "深入JavaWeb技术内幕","How Tomcat Works","Tomcat架构解析",
//                "Spring实战","Spring源码深度解析","Spring MVC学习指南",
//                "Maven实战","sql必知必会","深入浅出MySQL",
//                "Spring cloud微服务实战","SpringBoot与Docker微服务实战","深入理解SpringBoot与微服务架构","抑郁症知识图谱"
//        };
//        //加入分词并随机生成权重，每次生成得图片都不一样
//        for (String book : books){
//            wordFrequencies.add(new WordFrequency(book,new Random().nextInt(books.length)));
//        }
//        //此处不设置会出现中文乱码
//        java.awt.Font font = new java.awt.Font("STSong-Light", 2, 18);
//        final Dimension dimension = new Dimension(900, 900);
//        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
//        wordCloud.setPadding(2);
//        wordCloud.setBackground(new CircleBackground(255));
//        wordCloud.setFontScalar(new SqrtFontScalar(12, 42));
//        //设置词云显示的三种颜色，越靠前设置表示词频越高的词语的颜色
//        wordCloud.setColorPalette(new LinearGradientColorPalette(Color.RED, Color.BLUE, Color.GREEN, 30, 30));
//        wordCloud.setKumoFont(new KumoFont(font));
//        wordCloud.setBackgroundColor(new Color(255, 255, 255));
//        //因为我这边是生成一个圆形,这边设置圆的半径
//        wordCloud.setBackground(new CircleBackground(255));
//        wordCloud.build(wordFrequencies);
//        wordCloud.writeToFile("d://3.png");

    }
}
