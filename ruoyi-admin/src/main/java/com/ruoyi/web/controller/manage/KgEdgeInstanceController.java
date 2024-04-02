package com.ruoyi.web.controller.manage;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.KgEdgeInstaceProperties;
import com.ruoyi.system.domain.KgHistory;
import com.ruoyi.system.service.IKgEdgeInstacePropertiesService;
import com.ruoyi.system.service.IKgHistoryService;
import com.ruoyi.system.service.TestNeo4jService;
import com.ruoyi.system.utils.neo4j.Neo4jEdge;
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
import com.ruoyi.system.domain.KgEdgeInstance;
import com.ruoyi.system.service.IKgEdgeInstanceService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 【请填写功能名称】Controller
 *
 * @author ruoyi
 * @date 2024-03-17
 */
@RestController
@RequestMapping("/mange/instance/edge")
public class KgEdgeInstanceController extends BaseController
{
    @Autowired
    private IKgEdgeInstanceService kgEdgeInstanceService;

    @Autowired
    private IKgEdgeInstacePropertiesService kgEdgeInstacePropertiesService;

    @Autowired
    private TestNeo4jService neo4jService;
    @Autowired
    private IKgHistoryService historyService;

    /**
     * 查询【请填写功能名称】列表
     */
    @PreAuthorize("@ss.hasPermi('system:instance:list')")
    @GetMapping("/list")
    public TableDataInfo list(KgEdgeInstance kgEdgeInstance)
    {
        startPage();
        List<KgEdgeInstance> list = kgEdgeInstanceService.selectKgEdgeInstanceList(kgEdgeInstance);
        return getDataTable(list);
    }

    @GetMapping("/getAll")
    public List<KgEdgeInstance> getAll(KgEdgeInstance kgEdgeInstance)
    {
        List<KgEdgeInstance> list = kgEdgeInstanceService.selectKgEdgeInstanceList(kgEdgeInstance);
        return list;
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @PreAuthorize("@ss.hasPermi('system:instance:export')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, KgEdgeInstance kgEdgeInstance)
    {
        List<KgEdgeInstance> list = kgEdgeInstanceService.selectKgEdgeInstanceList(kgEdgeInstance);
        ExcelUtil<KgEdgeInstance> util = new ExcelUtil<KgEdgeInstance>(KgEdgeInstance.class);
        util.exportExcel(response, list, "【请填写功能名称】数据");
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:instance:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(kgEdgeInstanceService.selectKgEdgeInstanceById(id));
    }

    /**
     * 新增
     */
    @PreAuthorize("@ss.hasPermi('system:instance:add')")
    @Log(title = "新增关系实例", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody KgEdgeInstance kgEdgeInstance)
    {
        System.out.println("新增关系实例:req:" + kgEdgeInstance);
        // 首先添加到neo4j中
        Neo4jEdge edge = neo4jService.addEdge(kgEdgeInstance);
        System.out.println("neo4j中新建的关系"+edge);
        kgEdgeInstance.setNeo4jId(edge.getId());
        kgEdgeInstance.setId(IdUtil.getSnowflakeNextId());
        kgEdgeInstance.setValid(1L);
        kgEdgeInstance.setCreateUser(SecurityUtils.getUserId());
        kgEdgeInstance.setCreateTime(DateUtils.getNowDate());
        int count1 = kgEdgeInstanceService.insertKgEdgeInstance(kgEdgeInstance);

        KgHistory history = new KgHistory();
        history.setType(1);
        history.setTargetType(7);
        history.setTargetId(kgEdgeInstance.getId());
        history.setTargetName(kgEdgeInstance.getLabel());
        historyService.insertKgHistory(history);


        int count2 = 0;
        List<KgEdgeInstaceProperties> props = kgEdgeInstance.getProps();
        if(ObjectUtil.isNotNull(props)){
            for (KgEdgeInstaceProperties prop : props) {
                prop.setId(IdUtil.getSnowflakeNextId());
                prop.setEdgeId(edge.getId());
                prop.setCreateUser(SecurityUtils.getUserId());
                prop.setCreateTime(DateUtils.getNowDate());
                prop.setValid(1L);
                count2+=kgEdgeInstacePropertiesService.insertKgEdgeInstaceProperties(prop);
                KgHistory history1 = new KgHistory();
                history.setType(1);
                history.setTargetType(8);
                history.setTargetId(prop.getId());
                history.setTargetName(prop.getName());
                historyService.insertKgHistory(history);
            }
        }


        return toAjax(count1 + count2);
    }

    /**
     * 修改【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('system:instance:edit')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody KgEdgeInstance kgEdgeInstance)
    {
        return toAjax(kgEdgeInstanceService.updateKgEdgeInstance(kgEdgeInstance));
    }

    /**
     * 删除【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('system:instance:remove')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(kgEdgeInstanceService.deleteKgEdgeInstanceByIds(ids));
    }
}
