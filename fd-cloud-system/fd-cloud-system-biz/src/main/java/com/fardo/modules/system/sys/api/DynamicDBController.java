package com.fardo.modules.system.sys.api;

import com.fardo.common.util.dynamic.db.DynamicDBUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Description: 系统接口
 * @Author: suzc
 * @Date: 2020-12-11
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "api-动态数据源接口")
@RestController
@RequestMapping("/api/system/db")
public class DynamicDBController {

    @GetMapping("findList")
    public List<Map<String, Object>> findList(@RequestParam("dbKey")String dbKey, @RequestParam("sql")String sql, @RequestParam("param")Object... param){
        return DynamicDBUtil.findList(dbKey,sql,param);
    }

    @GetMapping("findOne")
    public Object findOne(@RequestParam("dbKey")String dbKey, @RequestParam("sql")String sql, @RequestParam("param")Object... param){
        return DynamicDBUtil.findOne(dbKey,sql,param);
    }
}
