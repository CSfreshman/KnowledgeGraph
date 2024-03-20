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
import com.ruoyi.system.domain.KgEdgeClass;
import com.ruoyi.system.service.IKgEdgeClassService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 【请填写功能名称】Controller
 *
 * @author ruoyi
 * @date 2024-03-08
 */
@RestController
@RequestMapping("/mange/class/edge")
public class KgEdgeClassController extends BaseController
{
    @Autowired
    private IKgEdgeClassService kgEdgeClassService;

    /**
     * 查询【请填写功能名称】列表
     */
    @PreAuthorize("@ss.hasPermi('system:class:list')")
    @GetMapping("/list")
    public TableDataInfo list(KgEdgeClass kgEdgeClass)
    {
        startPage();
        List<KgEdgeClass> list = kgEdgeClassService.selectKgEdgeClassList(kgEdgeClass);
        return getDataTable(list);
    }

    @GetMapping("/getAll")
    public List<KgEdgeClass> getAll(KgEdgeClass kgEdgeClass)
    {

        List<KgEdgeClass> list = kgEdgeClassService.selectKgEdgeClassList(kgEdgeClass);
        return list;
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @PreAuthorize("@ss.hasPermi('system:class:export')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, KgEdgeClass kgEdgeClass)
    {
        List<KgEdgeClass> list = kgEdgeClassService.selectKgEdgeClassList(kgEdgeClass);
        ExcelUtil<KgEdgeClass> util = new ExcelUtil<KgEdgeClass>(KgEdgeClass.class);
        util.exportExcel(response, list, "【请填写功能名称】数据");
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:class:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(kgEdgeClassService.selectKgEdgeClassById(id));
    }

    /**
     * 新增【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('system:class:add')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody KgEdgeClass kgEdgeClass)
    {
        return toAjax(kgEdgeClassService.insertKgEdgeClass(kgEdgeClass));
    }

    /**
     * 修改【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('system:class:edit')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody KgEdgeClass kgEdgeClass)
    {
        return toAjax(kgEdgeClassService.updateKgEdgeClass(kgEdgeClass));
    }

    /**
     * 删除【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('system:class:remove')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(kgEdgeClassService.deleteKgEdgeClassByIds(ids));
    }
}
