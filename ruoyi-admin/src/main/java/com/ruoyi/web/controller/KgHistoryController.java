package com.ruoyi.web.controller;

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
import com.ruoyi.system.domain.KgHistory;
import com.ruoyi.system.service.IKgHistoryService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * Controller
 *
 * @author ruoyi
 * @date 2024-04-01
 */
@RestController
@RequestMapping("/history")
public class KgHistoryController extends BaseController
{
    @Autowired
    private IKgHistoryService kgHistoryService;

    /**
     * 查询列表
     */

    @GetMapping("/list")
    public TableDataInfo list(KgHistory kgHistory)
    {
        startPage();
        List<KgHistory> list = kgHistoryService.selectKgHistoryList(kgHistory);
        return getDataTable(list);
    }

    /**
     * 导出列表
     */

    @Log(title = "", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, KgHistory kgHistory)
    {
        List<KgHistory> list = kgHistoryService.selectKgHistoryList(kgHistory);
        ExcelUtil<KgHistory> util = new ExcelUtil<KgHistory>(KgHistory.class);
        util.exportExcel(response, list, "数据");
    }

    /**
     * 获取详细信息
     */

    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(kgHistoryService.selectKgHistoryById(id));
    }

    /**
     * 新增
     */

    @Log(title = "", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody KgHistory kgHistory)
    {
        return toAjax(kgHistoryService.insertKgHistory(kgHistory));
    }

    /**
     * 修改
     */

    @Log(title = "", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody KgHistory kgHistory)
    {
        return toAjax(kgHistoryService.updateKgHistory(kgHistory));
    }

    /**
     * 删除
     */

    @Log(title = "", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(kgHistoryService.deleteKgHistoryByIds(ids));
    }
}
