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
import com.ruoyi.system.domain.KgNodeClassProperties;
import com.ruoyi.system.service.IKgNodeClassPropertiesService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 【请填写功能名称】Controller
 *
 * @author ruoyi
 * @date 2024-03-04
 */
@RestController
@RequestMapping("/system/mange/class/nodeProperties")
public class KgNodeClassPropertiesController extends BaseController
{
    @Autowired
    private IKgNodeClassPropertiesService kgNodeClassPropertiesService;

    /**
     * 查询【请填写功能名称】列表
     */
    @PreAuthorize("@ss.hasPermi('system:properties:list')")
    @GetMapping("/list")
    public TableDataInfo list(KgNodeClassProperties kgNodeClassProperties)
    {
        startPage();
        List<KgNodeClassProperties> list = kgNodeClassPropertiesService.selectKgNodeClassPropertiesList(kgNodeClassProperties);
        return getDataTable(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @PreAuthorize("@ss.hasPermi('system:properties:export')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, KgNodeClassProperties kgNodeClassProperties)
    {
        List<KgNodeClassProperties> list = kgNodeClassPropertiesService.selectKgNodeClassPropertiesList(kgNodeClassProperties);
        ExcelUtil<KgNodeClassProperties> util = new ExcelUtil<KgNodeClassProperties>(KgNodeClassProperties.class);
        util.exportExcel(response, list, "【请填写功能名称】数据");
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:properties:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(kgNodeClassPropertiesService.selectKgNodeClassPropertiesById(id));
    }

    /**
     * 新增【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('system:properties:add')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody KgNodeClassProperties kgNodeClassProperties)
    {
        return toAjax(kgNodeClassPropertiesService.insertKgNodeClassProperties(kgNodeClassProperties));
    }

    /**
     * 修改【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('system:properties:edit')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody KgNodeClassProperties kgNodeClassProperties)
    {
        return toAjax(kgNodeClassPropertiesService.updateKgNodeClassProperties(kgNodeClassProperties));
    }

    /**
     * 删除【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('system:properties:remove')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(kgNodeClassPropertiesService.deleteKgNodeClassPropertiesByIds(ids));
    }
}
