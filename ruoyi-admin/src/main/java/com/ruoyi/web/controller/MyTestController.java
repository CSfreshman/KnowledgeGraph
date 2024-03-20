package com.ruoyi.web.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.KgEdgeClass;
import com.ruoyi.system.domain.KgEdgeInstance;
import com.ruoyi.system.domain.KgNodeInstance;
import com.ruoyi.system.service.*;
import com.ruoyi.system.service.impl.KgNodeClassServiceImpl;
import com.ruoyi.system.service.impl.KgNodeInstanceServiceImpl;
import com.ruoyi.system.utils.neo4j.Neo4jEdge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/import")
public class MyTestController extends BaseController {

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
}
