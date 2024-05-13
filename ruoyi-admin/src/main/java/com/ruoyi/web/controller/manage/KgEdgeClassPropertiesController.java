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
import com.ruoyi.system.domain.KgEdgeClassProperties;
import com.ruoyi.system.service.IKgEdgeClassPropertiesService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * Controller
 *
 * @author ruoyi
 * @date 2024-03-08
 */
@RestController
@RequestMapping("/mange/class/edgeProperties")
public class KgEdgeClassPropertiesController extends BaseController
{
    @Autowired
    private IKgEdgeClassPropertiesService kgEdgeClassPropertiesService;

    /**
     * 查询列表
     */
    @PreAuthorize("@ss.hasPermi('system:properties:list')")
    @GetMapping("/list")
    public TableDataInfo list(KgEdgeClassProperties kgEdgeClassProperties)
    {
        startPage();
        List<KgEdgeClassProperties> list = kgEdgeClassPropertiesService.selectKgEdgeClassPropertiesList(kgEdgeClassProperties);
        return getDataTable(list);
    }

    /**
     * 导出列表
     */
    @PreAuthorize("@ss.hasPermi('system:properties:export')")
    @Log(title = "", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, KgEdgeClassProperties kgEdgeClassProperties)
    {
        List<KgEdgeClassProperties> list = kgEdgeClassPropertiesService.selectKgEdgeClassPropertiesList(kgEdgeClassProperties);
        ExcelUtil<KgEdgeClassProperties> util = new ExcelUtil<KgEdgeClassProperties>(KgEdgeClassProperties.class);
        util.exportExcel(response, list, "数据");
    }

    /**
     * 获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:properties:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(kgEdgeClassPropertiesService.selectKgEdgeClassPropertiesById(id));
    }

    /**
     * 新增
     */
    @PreAuthorize("@ss.hasPermi('system:properties:add')")
    @Log(title = "", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody KgEdgeClassProperties kgEdgeClassProperties)
    {
        return toAjax(kgEdgeClassPropertiesService.insertKgEdgeClassProperties(kgEdgeClassProperties));
    }

    /**
     * 修改
     */
    @PreAuthorize("@ss.hasPermi('system:properties:edit')")
    @Log(title = "", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody KgEdgeClassProperties kgEdgeClassProperties)
    {
        return toAjax(kgEdgeClassPropertiesService.updateKgEdgeClassProperties(kgEdgeClassProperties));
    }

    /**
     * 删除
     */
    @PreAuthorize("@ss.hasPermi('system:properties:remove')")
    @Log(title = "", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(kgEdgeClassPropertiesService.deleteKgEdgeClassPropertiesByIds(ids));
    }
}
