package com.ruoyi.web.controller.manage;

import java.util.*;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.system.domain.KgHistory;
import com.ruoyi.system.domain.KgNodeInstanceProperties;
import com.ruoyi.system.service.IKgHistoryService;
import com.ruoyi.system.service.IKgNodeInstancePropertiesService;
import com.ruoyi.system.service.TestNeo4jService;
import com.ruoyi.system.utils.neo4j.Neo4jNode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.KgNodeInstance;
import com.ruoyi.system.service.IKgNodeInstanceService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * Controller
 *
 * @author ruoyi
 * @date 2024-03-16
 */
@RestController
@RequestMapping("/mange/instance/node")
public class KgNodeInstanceController extends BaseController
{
    @Autowired
    private IKgNodeInstanceService kgNodeInstanceService;

    @Autowired
    private IKgNodeInstancePropertiesService kgNodeInstancePropertiesService;

    @Autowired
    private TestNeo4jService neo4jService;

    @Autowired
    private IKgHistoryService historyService;

    /**
     * 查询列表
     */
    @PreAuthorize("@ss.hasPermi('system:instance:list')")
    @GetMapping("/list")
    public TableDataInfo list(KgNodeInstance kgNodeInstance)
    {
        startPage();
        List<KgNodeInstance> list = kgNodeInstanceService.selectKgNodeInstanceList(kgNodeInstance);
        return getDataTable(list);
    }

    @PostMapping("/getAll")
    public List<KgNodeInstance> getAll(@RequestBody KgNodeInstance kgNodeInstance){
        return kgNodeInstanceService.selectKgNodeInstanceList(kgNodeInstance);
    }

    @PostMapping("/getAllByClassId")
    public List<KgNodeInstance> getAllByClassId(@RequestBody Map<String,String> req)
    {
        System.out.println("getAllByClassId:请求参数:" + req);
        // 根据classId查询
        KgNodeInstance kgNodeInstance = new KgNodeInstance();
        kgNodeInstance.setClassId(Long.valueOf((String) req.get("nodeClassId")));
        // 查询有效的
        kgNodeInstance.setValid(1L);
        List<KgNodeInstance> list = kgNodeInstanceService.selectKgNodeInstanceList(kgNodeInstance);
        return list;
    }

    /**
     * 导出列表
     */
    @PreAuthorize("@ss.hasPermi('system:instance:export')")
    @Log(title = "", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, KgNodeInstance kgNodeInstance)
    {
        List<KgNodeInstance> list = kgNodeInstanceService.selectKgNodeInstanceList(kgNodeInstance);
        ExcelUtil<KgNodeInstance> util = new ExcelUtil<KgNodeInstance>(KgNodeInstance.class);
        util.exportExcel(response, list, "数据");
    }

    /**
     * 获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:instance:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(kgNodeInstanceService.selectKgNodeInstanceById(id));
    }


    @PreAuthorize("@ss.hasPermi('system:instance:add')")
    @Log(title = "", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Map<String,Object> req)
    {
        System.out.println(req);
        KgNodeInstance instance = new KgNodeInstance();

        // 去重处理，保证节点实体名称的唯一性
        instance.setName((String)req.get("name"));
        instance.setValid(1l);
        List<KgNodeInstance> kgNodeInstances = kgNodeInstanceService.selectKgNodeInstanceList(instance);
        if(ObjectUtil.isNotEmpty(kgNodeInstances)){
            // 如果存在重名实体，抛出名称重复异常
            System.out.println(kgNodeInstances);
            for (KgNodeInstance kgNodeInstance : kgNodeInstances) {
                System.out.println(kgNodeInstance);
                if(req.get("name").equals(kgNodeInstance.getName())){
                    throw new RuntimeException("实体名称重复");
                }
            }

        }

        instance = new KgNodeInstance();
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

            // 历史记录
            KgHistory history1 = new KgHistory();
            // 新增
            history1.setType(1);
            history1.setTargetType(4);
            history1.setTargetId(properties.getId());
            history1.setTargetName(properties.getName());
            historyService.insertKgHistory(history1);

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

        // 历史记录
        KgHistory history = new KgHistory();
        // 新增
        history.setType(1);
        history.setTargetType(3);
        history.setTargetId(instance.getId());
        history.setTargetName(instance.getName());
        historyService.insertKgHistory(history);

        int count1 = kgNodeInstanceService.insertKgNodeInstance(instance);

        return toAjax(count1 + count2);

    }

    /**
     * 修改
     */
    @PreAuthorize("@ss.hasPermi('system:instance:edit')")
    @Log(title = "", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody KgNodeInstance kgNodeInstance)
    {
        return toAjax(kgNodeInstanceService.updateKgNodeInstance(kgNodeInstance));
    }

    /**
     * 删除
     */
    @PreAuthorize("@ss.hasPermi('system:instance:remove')")
    @Log(title = "", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(kgNodeInstanceService.deleteKgNodeInstanceByIds(ids));
    }
}
