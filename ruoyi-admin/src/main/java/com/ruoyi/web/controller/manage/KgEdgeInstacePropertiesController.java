package com.ruoyi.web.controller.manage;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.ruoyi.system.domain.KgEdgeInstaceProperties;
import com.ruoyi.system.service.IKgEdgeInstacePropertiesService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * Controller
 *
 * @author ruoyi
 * @date 2024-03-18
 */
@RestController
@RequestMapping("/system/properties")
public class KgEdgeInstacePropertiesController extends BaseController
{
    @Autowired
    private IKgEdgeInstacePropertiesService kgEdgeInstacePropertiesService;

    /**
     * 查询列表
     */
    @PreAuthorize("@ss.hasPermi('system:properties:list')")
    @GetMapping("/list")
    public TableDataInfo list(KgEdgeInstaceProperties kgEdgeInstaceProperties)
    {
        startPage();
        List<KgEdgeInstaceProperties> list = kgEdgeInstacePropertiesService.selectKgEdgeInstacePropertiesList(kgEdgeInstaceProperties);
        return getDataTable(list);
    }

    /**
     * 导出列表
     */
    @PreAuthorize("@ss.hasPermi('system:properties:export')")
    @Log(title = "", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, KgEdgeInstaceProperties kgEdgeInstaceProperties)
    {
        List<KgEdgeInstaceProperties> list = kgEdgeInstacePropertiesService.selectKgEdgeInstacePropertiesList(kgEdgeInstaceProperties);
        ExcelUtil<KgEdgeInstaceProperties> util = new ExcelUtil<KgEdgeInstaceProperties>(KgEdgeInstaceProperties.class);
        util.exportExcel(response, list, "数据");
    }

    /**
     * 获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:properties:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(kgEdgeInstacePropertiesService.selectKgEdgeInstacePropertiesById(id));
    }

    /**
     * 新增
     */
    @PreAuthorize("@ss.hasPermi('system:properties:add')")
    @Log(title = "", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody KgEdgeInstaceProperties kgEdgeInstaceProperties)
    {
        return toAjax(kgEdgeInstacePropertiesService.insertKgEdgeInstaceProperties(kgEdgeInstaceProperties));
    }

    /**
     * 修改
     */
    @PreAuthorize("@ss.hasPermi('system:properties:edit')")
    @Log(title = "", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody KgEdgeInstaceProperties kgEdgeInstaceProperties)
    {
        return toAjax(kgEdgeInstacePropertiesService.updateKgEdgeInstaceProperties(kgEdgeInstaceProperties));
    }

    /**
     * 删除
     */
    @PreAuthorize("@ss.hasPermi('system:properties:remove')")
    @Log(title = "", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(kgEdgeInstacePropertiesService.deleteKgEdgeInstacePropertiesByIds(ids));
    }
}
