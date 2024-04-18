package com.ruoyi.web.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.KgEdgeClass;
import com.ruoyi.system.domain.KgEdgeInstance;
import com.ruoyi.system.domain.KgNodeInstance;
import com.ruoyi.system.domain.KgNodeInstanceProperties;
import com.ruoyi.system.service.*;
import com.ruoyi.system.service.impl.KgNodeClassServiceImpl;
import com.ruoyi.system.service.impl.KgNodeInstanceServiceImpl;
import com.ruoyi.system.utils.dataBuilder.InsertIntoDataBase;
import com.ruoyi.system.utils.neo4j.Neo4jEdge;
import com.ruoyi.system.utils.neo4j.Neo4jNode;
import com.ruoyi.web.controller.manage.KgEdgeInstanceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/import")
public class MyTestController extends BaseController {

    public static Map<String,Long> idMap = new HashMap<>();

    static {
        idMap.put("成功后抑郁症",1776861582966792192l);
        idMap.put("产后抑郁症",1770334657544904704l);
        idMap.put("反复发作抑郁症",1770334683528617984l);
        idMap.put("老年抑郁症",1770334706119139328l);
        idMap.put("老年期抑郁症",1770334731922497536l);
        idMap.put("绝经与抑郁症",1770334750234828800l);
        idMap.put("抑郁症",1770334804572037120l);
        idMap.put("微笑抑郁症",1770334827745566720l);
        idMap.put("单次发作抑郁症",1776861025610899456l);
        idMap.put("躁郁症",1770334768366804992l);
        idMap.put("季节性情绪抑郁",1770334450061074432l);
    }

    @Autowired
    IKgNodeClassService nodeClassService;
    @Autowired
    IKgNodeInstanceService nodeInstanceService;
    @Autowired
    IKgEdgeClassService edgeClassService;
    @Autowired
    IKgEdgeInstanceService edgeInstanceService;
    @Autowired
    TestNeo4jService testNeo4jService;

    @Autowired
    IKgNodeInstancePropertiesService kgNodeInstancePropertiesService;

    @Autowired
    KgEdgeInstanceController edgeInstanceController;

    @GetMapping("/instance/edge")
    public void importInstanceEdge() {
        String jsonStr = "{\"成功后抑郁症\":[\"抑郁症\",\"慢性疲劳综合症\"],\"产后抑郁症\":[\"抑郁症\"],\"反复发作抑郁症\":[\"便秘\",\"抑郁症\",\"反复发作抑郁症\",\"焦虑症\",\"睡眠障碍\",\"单次发作抑郁症\"],\"抑郁症\":[\"便秘\",\"焦虑症\",\"睡眠障碍\"],\"老年抑郁症\":[],\"微笑抑郁症\":[],\"老年期抑郁症\":[\"闭经\"],\"绝经与抑郁症\":[\"抑郁症\",\"焦虑症\"],\"单次发作抑郁症\":[\"便秘\",\"精神分裂症\",\"抑郁症\",\"反复发作抑郁症\",\"焦虑症\",\"睡眠障碍\",\"儿童精神分裂症\",\"单次发作抑郁症\"],\"躁郁症\":[\"哮喘\",\"心血管和血栓栓塞综合征\",\"偏头痛\"]}";
        Map map1 = JSONUtil.toBean(jsonStr, Map.class);
        Map<String,List<String>> map = (Map<String,List<String>>)map1;
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            String name = entry.getKey();
            KgNodeInstance instance1 = new KgNodeInstance();
            instance1.setName(name);
            List<KgNodeInstance> kgNodeInstances = nodeInstanceService.selectKgNodeInstanceList(instance1);
            // 得到起点实体
            KgNodeInstance start = null;
            if(ObjectUtil.isNotNull(kgNodeInstances)){
                start = kgNodeInstances.get(0);
            }

            for (String s : entry.getValue()) {
                // 得到终点实体
                KgNodeInstance end = null;
                KgNodeInstance instance2 = new KgNodeInstance();
                instance2.setName(s);
                if("抑郁症".equals(s) || "反复发作抑郁症".equals(s) || "单次发作抑郁症".equals(s)){
                    instance2.setLabel("并发症");
                }
                List<KgNodeInstance> kgNodeInstances1 = nodeInstanceService.selectKgNodeInstanceList(instance2);
                if(ObjectUtil.isNotNull(kgNodeInstances1)){
                    end = kgNodeInstances1.get(0);
                }
                System.out.println(start);
                System.out.println(end);
                // 查询关系类型
                KgEdgeClass edgeClasSReq = new KgEdgeClass();
                edgeClasSReq.setFromNodeId(start.getClassId());
                edgeClasSReq.setToNodeId(end.getClassId());
                List<KgEdgeClass> kgEdgeClasses = edgeClassService.selectKgEdgeClassList(edgeClasSReq);
                if(ObjectUtil.isNull(kgEdgeClasses)){
                    return;
                }
                KgEdgeClass edgeClass = kgEdgeClasses.get(0);

                // 创建关系实例
                KgEdgeInstance instance = new KgEdgeInstance();
                instance.setClassId(edgeClass.getId());
                instance.setFromNodeId(start.getId());
                instance.setFromNodeNeo4jId(start.getNeo4jId());
                instance.setToNodeId(end.getId());
                instance.setToNodeNeo4jId(end.getNeo4jId());
                instance.setLabel(edgeClass.getLabel());
                instance.setId(IdUtil.getSnowflakeNextId());
                instance.setCreateUser(SecurityUtils.getUserId());

                Neo4jEdge edge = testNeo4jService.addEdge(instance);

                instance.setNeo4jId(edge.getId());

                edgeInstanceService.insertKgEdgeInstance(instance);
            }
        }
    }

    @Transactional
    @PostMapping("/instance/node/zz")
    public void importNodeInstanceZz() {
        String jsonStr = "{\n" +
//                "\"抑郁症\": [\n" +
//                "\"焦虑\",\n" +
//                "\"绝望\",\n" +
//                "\"自杀企图\",\n" +
//                "\"身体功能减退\",\n" +
//                "\"心情压抑\",\n" +
//                "\"情绪低落\",\n" +
//                "\"精力不足\",\n" +
//                "\"自我评价过低\",\n" +
//                "\"兴趣丧失\",\n" +
//                "\"悲观失望\",\n" +
//                "\"幻觉妄想\"\n" +
//                "],\n" +
//                "\"产后抑郁症\": [\n" +
//                "\"情绪低落\",\n" +
//                "\"落泪\",\n" +
//                "\"易激惹\",\n" +
//                "\"焦虑\",\n" +
//                "\"害怕\",\n" +
//                "\"恐慌\",\n" +
//                "\"缺乏动力\",\n" +
//                "\"厌烦情绪\",\n" +
//                "\"食欲低下\",\n" +
//                "\"体重减轻\",\n" +
//                "\"疲倦\",\n" +
//                "\"乏力\",\n" +
//                "\"便秘\",\n" +
//                "\"注意力不集中\",\n" +
//                "\"健忘\",\n" +
//                "\"缺乏信心\",\n" +
//                "\"自尊心减低\",\n" +
//                "\"失望感\",\n" +
//                "\"自觉无用感\"\n" +
//                "],\n" +
                "\"老年抑郁症\": [\n" +
                "\"全身性酸痛乏力\",\n" +
                "\"自卑\",\n" +
                "\"情绪不稳易激动\",\n" +
                "\"反复往自我贬低与谴责\",\n" +
                "\"思维活动受限\"\n" +
                "],\n" +
                "\"微笑抑郁症\": [\n" +
                "\"强颜欢笑\",\n" +
                "\"情绪失调\",\n" +
                "\"自卑\",\n" +
                "\"怀疑自己能力\"\n" +
                "],\n" +
                "\"单次发作抑郁症\": [\n" +
                "\"精神活动减低\",\n" +
                "\"思维迟钝\",\n" +
                "\"反应缓慢\",\n" +
                "\"低声细语\",\n" +
                "\"意志活动抑制\",\n" +
                "\"周围环境漠不关心\",\n" +
                "\"终日忧心忡忡\",\n" +
                "\"缺乏愉快感\",\n" +
                "\"悲观厌世情绪\"\n" +
                "],\n" +
                "\"躁郁症\": [\n" +
                "\"抑郁发作\",\n" +
                "\"躁狂发作\",\n" +
                "\"混合发作\"\n" +
                "],\n" +
                "\"老年期抑郁症\": [\n" +
                "\"季节性情绪抑郁\",\n" +
                "\"老人性格孤僻\",\n" +
                "\"退离休综合症\",\n" +
                "\"被害妄想\"\n" +
                "],\n" +
//                "\"成功后抑郁症\": [\n" +
//                "\"情绪低落\",\n" +
//                "\"影响睡眠\"\n" +
//                "],\n" +
                "\"反复发作抑郁症\": [\n" +
                "\"心境低落\",\n" +
                "\"认知功能损害\",\n" +
                "\"思维迟缓\",\n" +
                "\"意志活动减退\",\n" +
                "\"躯体症状\",\n" +
                "\"妄想\"\n" +
                "],\n" +
                "\"绝经与抑郁症\": [\n" +
                "\"情绪问题\",\n" +
                "\"行为问题\",\n" +
                "\"躯体症状\",\n" +
                "\"精神症状\",\n" +
                "\"幻觉或妄想\"\n" +
                "]\n" +
                "}";

        Map<String,List<String>> map = JSONUtil.toBean(jsonStr, Map.class);
        Set<String> zhengZhuang = new HashSet<>();

        KgNodeInstance fromNode = new KgNodeInstance();
        KgNodeInstance toNode = new KgNodeInstance();

//        for (Map.Entry<String, List<String>> stringListEntry : map.entrySet()) {
//            System.out.println(stringListEntry.getKey());
//            fromNode = new KgNodeInstance();
//            fromNode.setName(stringListEntry.getKey());
//            fromNode.setValid(1l);
//
//            KgNodeInstance fromNodeInstance = nodeInstanceService.selectKgNodeInstanceList(fromNode).get(0);
//            System.out.println("fromNodeInstance" + fromNodeInstance);
//            for (String s : stringListEntry.getValue()) {
//                toNode = new KgNodeInstance();
//                toNode.setName(s);
//                toNode.setValid(1l);
//                KgNodeInstance toNodeInstance = nodeInstanceService.selectKgNodeInstanceList(toNode).get(0);
//                System.out.println("toNodeInstance" + toNodeInstance);
//
//                KgEdgeInstance instance = new KgEdgeInstance();
//                instance.setValid(1l);
//                instance.setLabel("疾病症状");
//                instance.setClassId(1772132016339734528l);
//                instance.setFromNodeId(fromNodeInstance.getId());
//                instance.setToNodeId(toNodeInstance.getId());
//                instance.setFromNodeNeo4jId(fromNodeInstance.getNeo4jId());
//                instance.setToNodeNeo4jId(toNodeInstance.getNeo4jId());
//
//
//                System.out.println("开始执行插入动作:" + fromNode.getName() + " == " + toNode.getName());
//
//                edgeInstanceController.add(instance);
//            }
//        }

        List<String> yyzZz = new ArrayList<>();
        yyzZz.add("易激惹");
        yyzZz.add("体重减轻");
        yyzZz.add("便秘");
        yyzZz.add("乏力");
        yyzZz.add("妄想");

        fromNode = new KgNodeInstance();
        fromNode.setValid(1l);

        KgNodeInstance fromNodeInstance = nodeInstanceService.selectKgNodeInstanceById(1770334657544904704L);
        System.out.println("fromNodeInstance" + fromNodeInstance);
        for (String s : yyzZz) {
            toNode = new KgNodeInstance();
            toNode.setName(s);
            toNode.setValid(1l);
            KgNodeInstance toNodeInstance = nodeInstanceService.selectKgNodeInstanceList(toNode).get(0);
            System.out.println("toNodeInstance" + toNodeInstance);

            KgEdgeInstance instance = new KgEdgeInstance();
            instance.setValid(1l);
            instance.setLabel("疾病症状");
            instance.setClassId(1772132016339734528l);
            instance.setFromNodeId(fromNodeInstance.getId());
            instance.setToNodeId(toNodeInstance.getId());
            instance.setFromNodeNeo4jId(fromNodeInstance.getNeo4jId());
            instance.setToNodeNeo4jId(toNodeInstance.getNeo4jId());


            System.out.println("开始执行插入动作:" + fromNode.getName() + " == " + toNode.getName());

            edgeInstanceController.add(instance);
        }

//        // 得到症状名称集合
//        System.out.println(zhengZhuang);
//        System.out.println(zhengZhuang.size());
//
//        Map<String,String> props = new HashMap<>();
//        props.put("key","color");
//        props.put("value","rgb(255, 229, 0)");
//        for (String s : zhengZhuang) {
//            Map<String,Object> map1 = new HashMap<>();
//            map1.put("label","症状");
//            map1.put("name",s);
//            map1.put("classId","1770365867855843328");
//            map1.put("props",Collections.singletonList(props));
//            add(map1);
//        }
    }

    // 病因
    @Transactional
    @PostMapping("/instance/node/by")
    public void importNodeInstanceBy() {
        String jsonStr = "{\n" +
                "    \"成功后抑郁症\": [\"压力过大\", \"缺乏自信\", \"自身期望过高\"],\n" +
                "    \"产后抑郁症\": [\"心理因素\", \"内分泌失调\", \"遗传因素\"],\n" +
                "    \"反复发作抑郁症\": [\"遗传因素\", \"内分泌失调\", \"增龄引起的脑退行性改变\", \"躯体疾病\", \"体质因素\", \"社会环境因素\", \"性格特质\"],\n" +
                "    \"抑郁症\": [\"遗传因素\", \"体质因素\", \"中枢神经介质的功能及代谢异常\"],\n" +
                "    \"老年抑郁症\": [\"体质因素\", \"社会环境因素\", \"心理因素\"],\n" +
                "    \"微笑抑郁症\": [\"心理负担\"],\n" +
//                "    \"季节性情绪抑郁\": [],\n" +
                "    \"老年期抑郁症\": [\"增龄引起的脑退行性改变\", \"社会环境因素\", \"心理因素\", \"体质因素\"],\n" +
//                "    \"绝经与抑郁症\": [],\n" +
                "    \"单次发作抑郁症\": [\"遗传因素\", \"心理因素\", \"体质因素\", \"躯体疾病\"],\n" +
                "    \"躁郁症\": [\"遗传因素\", \"心理因素\", \"社会环境因素\"]\n" +
                "}";
        Map<String,List<String>> map = JSONUtil.toBean(jsonStr, Map.class);

        Set<String> by = new HashSet<>();

        for (Map.Entry<String, List<String>> stringListEntry : map.entrySet()) {
            System.out.println(stringListEntry.getKey());
            System.out.println(stringListEntry.getValue());
            by.addAll(stringListEntry.getValue());
        }


        List<KgEdgeInstance> edgeInstances = new ArrayList<>();

        // 添加关系
        for (Map.Entry<String, List<String>> stringListEntry : map.entrySet()) {
            KgNodeInstance fromNode = new KgNodeInstance();

            KgNodeInstance fromInstance = nodeInstanceService.selectKgNodeInstanceById(idMap.get(stringListEntry.getKey()));
            System.out.println(fromInstance);
            for (String s : stringListEntry.getValue()) {

                KgNodeInstance toNode = new KgNodeInstance();
                toNode.setName(s);
                toNode.setValid(1l);
                KgNodeInstance toInstance = nodeInstanceService.selectKgNodeInstanceList(toNode).get(0);
                System.out.println(toInstance);

                KgEdgeInstance instance = new KgEdgeInstance();
                instance.setValid(1l);
                instance.setLabel("疾病病因");
                instance.setClassId(1776948605853626368l);
                instance.setFromNodeId(fromInstance.getId());
                instance.setToNodeId(toInstance.getId());
                instance.setFromNodeNeo4jId(fromInstance.getNeo4jId());
                instance.setToNodeNeo4jId(toInstance.getNeo4jId());
                edgeInstances.add(instance);

            }


        }

        System.out.println(edgeInstances);
        for (KgEdgeInstance edgeInstance : edgeInstances) {
            edgeInstanceController.add(edgeInstance);
        }

        // 添加实体
//        // 得到症状名称集合
//        System.out.println(by.size());
//        Map<String,String> props = new HashMap<>();
//        props.put("key","color");
//        props.put("value","rgb(176, 248, 112)");
//        for (String s : by) {
//            Map<String,Object> map1 = new HashMap<>();
//            map1.put("label","病因");
//            map1.put("name",s);
//            map1.put("classId","1770366208978587648");
//            map1.put("props",Collections.singletonList(props));
//            add(map1);
//        }

    }

    @Transactional
    @PostMapping("/instance/node/yf")
    public void importNodeInstanceYf() {
        String jsonStr = "{\n" +
                "\"成功后抑郁症\": [\"放松身心\", \"保持感情生活\", \"自我疏解\", \"消除负面情绪\", \"适当运动\"],\n" +
                "\"产后抑郁症\": [\"放松身心\", \"减少精神紧张\"],\n" +
                "\"反复发作抑郁症\": [\"培养良好的人格\", \"多交朋友\", \"保持好心情\", \"保持好奇心\", \"早发现早治疗\", \"多沟通\"],\n" +
                "\"抑郁症\": [\"放松身心\", \"减少精神紧张\", \"多交朋友\", \"适当运动\"],\n" +
                "\"老年抑郁症\": [\"适当运动\", \"补充维生素\", \"外出散心\", \"读书\"],\n" +
                "\"老年期抑郁症\": [\"早发现早治疗\", \"加强心理治疗与社会支持\", \"社区干预及家庭干预\"],\n" +
                "\"单次发作抑郁症\": [\"适当运动\", \"保持好心情\", \"多沟通\"],\n" +
                "\"躁郁症\": [\"扩大生活圈子\", \"外出散心\", \"不宜整日持续工作\"]\n" +
                "}";
        Map<String,List<String>> map = JSONUtil.toBean(jsonStr, Map.class);
        Set<String> yf = new HashSet<>();
        for (Map.Entry<String, List<String>> stringListEntry : map.entrySet()) {
            System.out.println(stringListEntry.getKey());
            System.out.println(stringListEntry.getValue());
            yf.addAll(stringListEntry.getValue());
        }

        // 添加关系
        List<KgEdgeInstance> edgeInstances = new ArrayList<>();

        // 添加关系
        for (Map.Entry<String, List<String>> stringListEntry : map.entrySet()) {
            KgNodeInstance fromNode = new KgNodeInstance();

            KgNodeInstance fromInstance = nodeInstanceService.selectKgNodeInstanceById(idMap.get(stringListEntry.getKey()));
            System.out.println(fromInstance);
            for (String s : stringListEntry.getValue()) {

                KgNodeInstance toNode = new KgNodeInstance();
                toNode.setName(s);
                toNode.setValid(1l);
                KgNodeInstance toInstance = nodeInstanceService.selectKgNodeInstanceList(toNode).get(0);
                System.out.println(toInstance);

                KgEdgeInstance instance = new KgEdgeInstance();
                instance.setValid(1l);
                instance.setLabel("疾病预防");
                instance.setClassId(1778043311140364288l);
                instance.setFromNodeId(fromInstance.getId());
                instance.setToNodeId(toInstance.getId());
                instance.setFromNodeNeo4jId(fromInstance.getNeo4jId());
                instance.setToNodeNeo4jId(toInstance.getNeo4jId());
                edgeInstances.add(instance);

            }


        }

        System.out.println(edgeInstances);
        for (KgEdgeInstance edgeInstance : edgeInstances) {
            edgeInstanceController.add(edgeInstance);
        }

        // 添加节点
//        System.out.println("待添加节点");
//        System.out.println(yf);
//
//        System.out.println(yf.size());
//        Map<String,String> props = new HashMap<>();
//        props.put("key","color");
//        props.put("value","rgb(255, 197, 197)");
//        for (String s : yf) {
//            Map<String,Object> map1 = new HashMap<>();
//            map1.put("label","预防方法");
//            map1.put("name",s);
//            map1.put("classId","1778043185009254400");
//            map1.put("props",Collections.singletonList(props));
//            add(map1);
//        }
    }


    @Transactional
    @PostMapping("/instance/node/hl")
    public void importNodeInstanceHl() {
        String jsonStr = "{\n" +
                "\"产后抑郁症\": [\"加强对孕妇的精神关怀\", \"多关心\", \"多爱护\"],\n" +
                "\"反复发作抑郁症\": [\"建立良好的治疗性人际关系\", \"给予心理支持\", \"加强对病房设施的安全检查\", \"严格执行整体护理管理制度\", \"完善居住环境\"],\n" +
                "\"抑郁症\": [\"建立良好的治疗性人际关系\", \"给予心理支持\", \"加强对病房设施的安全检查\", \"严格执行整体护理管理制度\", \"完善居住环境\"],\n" +
                "\"老年期抑郁症\": [\"预防意外\", \"转移注意\", \"饮食禁忌\", \"生活照顾\", \"坚持服药\", \"心灵沟通\"],\n" +
                "\"单次发作抑郁症\": [\"建立良好的治疗性人际关系\", \"给予心理支持\", \"加强对病房设施的安全检查\", \"严格执行整体护理管理制度\", \"完善居住环境\"],\n" +
                "\"躁郁症\": [\"完善居住环境\", \"加强生活护理\"]\n" +
                "}";
        Map<String,List<String>> map = JSONUtil.toBean(jsonStr, Map.class);
        Set<String> hl = new HashSet<>();
        for (Map.Entry<String, List<String>> stringListEntry : map.entrySet()) {
            System.out.println(stringListEntry.getKey());
            System.out.println(stringListEntry.getValue());
            hl.addAll(stringListEntry.getValue());
        }

        // 添加关系
        List<KgEdgeInstance> edgeInstances = new ArrayList<>();

        // 添加关系
        for (Map.Entry<String, List<String>> stringListEntry : map.entrySet()) {
            KgNodeInstance fromNode = new KgNodeInstance();

            KgNodeInstance fromInstance = nodeInstanceService.selectKgNodeInstanceById(idMap.get(stringListEntry.getKey()));
            System.out.println(fromInstance);
            for (String s : stringListEntry.getValue()) {

                KgNodeInstance toNode = new KgNodeInstance();
                toNode.setName(s);
                toNode.setValid(1l);
                KgNodeInstance toInstance = nodeInstanceService.selectKgNodeInstanceList(toNode).get(0);
                System.out.println(toInstance);

                KgEdgeInstance instance = new KgEdgeInstance();
                instance.setValid(1l);
                instance.setLabel("疾病护理");
                instance.setClassId(1780482196256460800l);
                instance.setFromNodeId(fromInstance.getId());
                instance.setToNodeId(toInstance.getId());
                instance.setFromNodeNeo4jId(fromInstance.getNeo4jId());
                instance.setToNodeNeo4jId(toInstance.getNeo4jId());
                edgeInstances.add(instance);

            }


        }

        System.out.println(edgeInstances);
        for (KgEdgeInstance edgeInstance : edgeInstances) {
            edgeInstanceController.add(edgeInstance);
        }

        // 添加节点
//        System.out.println("待添加节点");
//        System.out.println(hl);
//
//        System.out.println(hl.size());
//        Map<String,String> props = new HashMap<>();
//        props.put("key","color");
//        props.put("value","rgb(255, 148, 61)");
//        for (String s : hl) {
//            Map<String,Object> map1 = new HashMap<>();
//            map1.put("label","护理方法");
//            map1.put("name",s);
//            map1.put("classId","1780481606197694464");
//            map1.put("props",Collections.singletonList(props));
//            add(map1);
//        }
    }


    @Transactional
    @PostMapping("/instance/node/zl")
    public void importNodeInstanceZl() {
        String jsonStr = "{\n" +
                "    \"成功后抑郁症\": [\n" +
                "        \"药物治疗\",\n" +
                "        \"心理治疗\",\n" +
                "        \"物理治疗\"\n" +
                "    ],\n" +
                "    \"产后抑郁症\": [\n" +
                "        \"心理治疗\",\n" +
                "        \"药物治疗\"\n" +
                "    ],\n" +
                "    \"反复发作抑郁症\": [\n" +
                "        \"心理治疗\",\n" +
                "        \"药物治疗\",\n" +
                "        \"中医治疗\"\n" +
                "    ],\n" +
                "    \"抑郁症\": [\n" +
                "        \"心理治疗\",\n" +
                "        \"药物治疗\",\n" +
                "        \"运动疗法\",\n" +
                "        \"中医治疗\"\n" +
                "    ],\n" +
                "    \"老年抑郁症\": [\n" +
                "        \"BNT脑递质平衡修复疗法\"\n" +
                "    ],\n" +
                "    \"微笑抑郁症\": [\n" +
                "        \"心理治疗\"\n" +
                "    ],\n" +
                "    \"老年期抑郁症\": [\n" +
                "        \"心理治疗\",\n" +
                "        \"药物治疗\",\n" +
                "        \"电休克治疗\"\n" +
                "    ],\n" +
                "    \"绝经与抑郁症\": [\n" +
                "        \"药物治疗\",\n" +
                "        \"中医治疗\"\n" +
                "    ],\n" +
                "    \"单次发作抑郁症\": [\n" +
                "        \"药物治疗\",\n" +
                "        \"认知治疗\",\n" +
                "        \"中医治疗\"\n" +
                "    ],\n" +
                "    \"躁郁症\": [\n" +
                "        \"西医治疗\",\n" +
                "        \"中医治疗\"\n" +
                "    ]\n" +
                "}";
        Map<String,List<String>> map = JSONUtil.toBean(jsonStr, Map.class);
        Set<String> zl = new HashSet<>();
        for (Map.Entry<String, List<String>> stringListEntry : map.entrySet()) {
            System.out.println(stringListEntry.getKey());
            System.out.println(stringListEntry.getValue());
            zl.addAll(stringListEntry.getValue());
        }

        // 添加关系
        List<KgEdgeInstance> edgeInstances = new ArrayList<>();

        // 添加关系
        for (Map.Entry<String, List<String>> stringListEntry : map.entrySet()) {
            KgNodeInstance fromNode = new KgNodeInstance();

            KgNodeInstance fromInstance = nodeInstanceService.selectKgNodeInstanceById(idMap.get(stringListEntry.getKey()));
            System.out.println(fromInstance);
            for (String s : stringListEntry.getValue()) {

                KgNodeInstance toNode = new KgNodeInstance();
                toNode.setName(s);
                toNode.setValid(1l);
                KgNodeInstance toInstance = nodeInstanceService.selectKgNodeInstanceList(toNode).get(0);
                System.out.println(toInstance);

                KgEdgeInstance instance = new KgEdgeInstance();
                instance.setValid(1l);
                instance.setLabel("疾病治疗");
                instance.setClassId(1777955998817304576l);
                instance.setFromNodeId(fromInstance.getId());
                instance.setToNodeId(toInstance.getId());
                instance.setFromNodeNeo4jId(fromInstance.getNeo4jId());
                instance.setToNodeNeo4jId(toInstance.getNeo4jId());
                edgeInstances.add(instance);

            }


        }

        System.out.println(edgeInstances);
        for (KgEdgeInstance edgeInstance : edgeInstances) {
            edgeInstanceController.add(edgeInstance);
        }

        // 添加节点
//        System.out.println("待添加节点");
//        System.out.println(zl);
//
//        System.out.println(zl.size());
//        Map<String,String> props = new HashMap<>();
//        props.put("key","color");
//        props.put("value","rgb(137, 93, 188)");
//        for (String s : zl) {
//            Map<String,Object> map1 = new HashMap<>();
//            map1.put("label","治疗方法");
//            map1.put("name",s);
//            map1.put("classId","1770366404231827456");
//            map1.put("props",Collections.singletonList(props));
//            add(map1);
//        }
    }

    public void add(Map<String,Object> req)
    {
        System.out.println(req);
        KgNodeInstance instance = new KgNodeInstance();

        // 构造实体实例数据
        instance.setLabel((String)req.get("label"));
        instance.setName((String)req.get("name"));
        instance.setClassId(Long.valueOf((String)req.get("classId")));
        instance.setId(IdUtil.getSnowflakeNextId());
        List props = (List)req.get("props");
        List<KgNodeInstanceProperties> propertiesList = new ArrayList<>();
        int count2 = 0;

        Map<String,Object> nodePropsMap = new HashMap<>();

        // 构造实体实例属性数据，并进行保存
        for (Object prop : props) {
            KgNodeInstanceProperties properties = new KgNodeInstanceProperties();
            properties.setId(IdUtil.getSnowflakeNextId());
            properties.setNodeId(instance.getId());

            Map map = (Map)prop;
            properties.setName((String)map.get("key"));
            properties.setValue((String)map.get("value"));
            nodePropsMap.put((String)map.get("key"),map.get("value"));
            count2+=kgNodeInstancePropertiesService.insertKgNodeInstanceProperties(properties);
        }

        // 构造neo4j节点数据并执行插入动作
        Neo4jNode node = new Neo4jNode();
        node.setId(instance.getId());   // 这行没用，没法指定neo4j的id
        node.setLabel(instance.getName());  // 注意：这里label本意为name
        node.setLabels(Collections.singletonList(instance.getLabel()));
        nodePropsMap.put("name",instance.getName());
        node.setProps(nodePropsMap);
        Neo4jNode neo4jNode = testNeo4jService.insertNodeToNeo4j(node);
        System.out.println(neo4jNode);

        instance.setNeo4jId(Long.valueOf(neo4jNode.getId().toString()));
        int count1 = nodeInstanceService.insertKgNodeInstance(instance);



    }
}
