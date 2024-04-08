package com.ruoyi.system.utils.dataBuilder;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.system.domain.KgNodeInstance;
import com.ruoyi.system.domain.KgNodeInstanceProperties;
import com.ruoyi.system.service.IKgNodeInstancePropertiesService;
import com.ruoyi.system.service.IKgNodeInstanceService;
import com.ruoyi.system.service.TestNeo4jService;
import com.ruoyi.system.service.impl.KgNodeInstanceServiceImpl;
import com.ruoyi.system.utils.neo4j.Neo4jNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Service
public class InsertIntoDataBase {
    @Autowired
    private static IKgNodeInstanceService kgNodeInstanceService;

    @Autowired
    private static IKgNodeInstancePropertiesService kgNodeInstancePropertiesService;

    @Autowired
    private static TestNeo4jService neo4jService;

    public static void main(String[] args) {
        InsertIntoDataBase test = new InsertIntoDataBase();
        Map<String,String> headers = new HashMap<>();
        headers.put("Authorization","Bearer eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX2tleSI6IjVmOTNiZmZhLTY4NzktNDAxOS1iNWJjLTIwODliZWNlMGIyMyJ9.nYS7OS_D78Ls4_LsfdFhOZIx_YsIRc3MJS_La8wKNOLe31T_yj1Sh6CLhUOqnaEUP8ujbkVlwhihFso8um_fsw");
        headers.put("Content-Type","application/json;charset=UTF-8");
        headers.put("Cookie","Hm_lvt_fe3b7a223fc08c795f0f4b6350703e6f=1698214174; username=admin; rememberMe=true; password=SEMS0lWZPvVASgb5zun/SI6Uozhj/hczIG3GAA4EKYl0zaGZHehHj9pySps/3yOx2gKhQnNJwv7kWg++fKTApw==; Admin-Token=eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX2tleSI6IjVmOTNiZmZhLTY4NzktNDAxOS1iNWJjLTIwODliZWNlMGIyMyJ9.nYS7OS_D78Ls4_LsfdFhOZIx_YsIRc3MJS_La8wKNOLe31T_yj1Sh6CLhUOqnaEUP8ujbkVlwhihFso8um_fsw; sidebarStatus=0");


        HttpResponse execute = HttpUtil.createPost("http://localhost/dev-api/mange/instance/edge")
                .addHeaders(headers)
                .body("{\"sourceId\":\"4\",\"source\":\"老年期抑郁症\",\"targetId\":\"10\",\"target\":\"测试症状\",\"label\":\"疾病症状\",\"classId\":\"1772132016339734528\",\"fromNodeNeo4jId\":\"4\",\"toNodeNeo4jId\":\"10\",\"fromNodeId\":\"1770334731922497536\",\"toNodeId\":\"1772131774349365248\"}").execute();
        System.out.println(execute);

    }

    public void addBingFaZheng(){
        List<String> nameList = new ArrayList<>();
        nameList.add("哮喘");
        nameList.add("精神分裂症");
        nameList.add("反复发作抑郁症");
        nameList.add("焦虑症");
        nameList.add("睡眠障碍");
        nameList.add("偏头痛");
        nameList.add("闭经");
        nameList.add("便秘");
        nameList.add("心血管和血栓栓塞综合征");
        nameList.add("抑郁症");
        nameList.add("儿童精神分裂症");
        nameList.add("单次发作抑郁症");
        nameList.add("慢性疲劳综合症");


        Map<String,String> props = new HashMap<>();
        props.put("key","color");
        props.put("value","rgb(38, 181, 13)");
        for (String s : nameList) {
            Map<String,Object> map = new HashMap<>();
            map.put("label","并发症");
            map.put("name",s);
            map.put("classId","1770336962667274240");
            map.put("props",Collections.singletonList(props));
            add(map);
        }
    }

    public static void add(Map<String,Object> req)
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
        Neo4jNode neo4jNode = neo4jService.insertNodeToNeo4j(node);
        System.out.println(neo4jNode);

        instance.setNeo4jId(Long.valueOf(neo4jNode.getId().toString()));
        int count1 = kgNodeInstanceService.insertKgNodeInstance(instance);



    }
}
