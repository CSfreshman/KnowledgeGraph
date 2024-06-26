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
import com.ruoyi.system.domain.KgNodeClass;
import com.ruoyi.system.service.IKgNodeClassService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * Controller
 *
 * @author ruoyi
 * @date 2024-03-04
 */
@RestController
@RequestMapping("/system/mange/class/node")
public class KgNodeClassController extends BaseController
{
    @Autowired
    private IKgNodeClassService kgNodeClassService;

    /**
     * 查询列表
     */
    @PreAuthorize("@ss.hasPermi('system:class:list')")
    @GetMapping("/list")
    public TableDataInfo list(KgNodeClass kgNodeClass)
    {
        startPage();
        List<KgNodeClass> list = kgNodeClassService.selectKgNodeClassList(kgNodeClass);
        return getDataTable(list);
    }

    @GetMapping("/getAll")
    public List<KgNodeClass> getAll(KgNodeClass kgNodeClass){
        return kgNodeClassService.getAll(kgNodeClass);
    }

    /**
     * 导出列表
     */
    @PreAuthorize("@ss.hasPermi('system:class:export')")
    @Log(title = "", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, KgNodeClass kgNodeClass)
    {
        List<KgNodeClass> list = kgNodeClassService.selectKgNodeClassList(kgNodeClass);
        ExcelUtil<KgNodeClass> util = new ExcelUtil<KgNodeClass>(KgNodeClass.class);
        util.exportExcel(response, list, "数据");
    }

    /**
     * 获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:class:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(kgNodeClassService.selectKgNodeClassById(id));
    }

    /**
     * 新增
     */
    @PreAuthorize("@ss.hasPermi('system:class:add')")
    @Log(title = "", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody KgNodeClass kgNodeClass)
    {
        return toAjax(kgNodeClassService.insertKgNodeClass(kgNodeClass));
    }

    /**
     * 修改
     */
    @PreAuthorize("@ss.hasPermi('system:class:edit')")
    @Log(title = "", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody KgNodeClass kgNodeClass)
    {
        return toAjax(kgNodeClassService.updateKgNodeClass(kgNodeClass));
    }

    /**
     * 删除
     */
    @PreAuthorize("@ss.hasPermi('system:class:remove')")
    @Log(title = "", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(kgNodeClassService.deleteKgNodeClassByIds(ids));
    }
}
