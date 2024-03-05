package com.ruoyi.web.controller.system.neo4j;

import cn.hutool.json.JSONUtil;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.TestNeo4jService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/neo4j")
public class TestNeo4jController extends BaseController {
    @Autowired
    private TestNeo4jService testNeo4jService;

    @GetMapping("/test")
    public String test(){
        return JSONUtil.toJsonStr(testNeo4jService.doTestNeo4j());
    }

    @GetMapping("/test1")
    public String test1(){
        return JSONUtil.toJsonStr(testNeo4jService.doTestNeo4j1());
    }
}
