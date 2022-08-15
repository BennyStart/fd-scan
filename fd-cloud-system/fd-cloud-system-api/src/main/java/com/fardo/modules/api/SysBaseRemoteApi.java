package com.fardo.modules.api;

import com.alibaba.fastjson.JSONObject;
import com.fardo.common.api.vo.Result;
import com.fardo.common.constant.ServiceNameConstants;
import com.fardo.common.system.vo.LoginUser;
import com.fardo.modules.api.factory.SysBaseRemoteApiFallbackFactory;
import com.fardo.modules.system.personal.entity.BlQualitysetUserEntity;
import com.fardo.modules.system.role.model.SysDataPermissionModel;
import com.fardo.modules.system.security.entity.SysApiSecretEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2020年05月21日 14:32
 */
@Component
@FeignClient(contextId = "sysBaseRemoteApi", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = SysBaseRemoteApiFallbackFactory.class)
public interface SysBaseRemoteApi {

    @GetMapping("/sys/user/info/{username}")
    Result<LoginUser> getUserByName(@PathVariable("username") String username);

    /**
     * 保存日志
     * @param jsonObject
     */
    @PostMapping("/sys/log/save")
    void saveSysLog(@RequestBody JSONObject jsonObject);

    /**
     * 通过编码和存储的value查询字典text、
     * @return
     */
    @GetMapping("/sys/user/queryDictTextByKey")
    String queryDictTextByKey(@RequestParam("code") String code, @RequestParam("key") String key);

    /**
     * 通过编码和存储的value查询表字典的text
     * @param table 表名
     * @param text  表字段
     * @param code  表字段
     * @param key   表字段code的值
     * @return
     */
    @GetMapping("/sys/user/queryTableDictTextByKey")
    String queryTableDictTextByKey(@RequestParam("table") String table, @RequestParam("text") String text, @RequestParam("code") String code, @RequestParam("key") String key);

    /**
     * 获取用户数据权限范围
     * @return
     */
    @GetMapping("/sys/user/getUserDataScope")
    SysDataPermissionModel getUserDataScope();

    @GetMapping("/api/system/db/findList")
    List<Map<String, Object>> findList(@RequestParam("dbKey")String dbKey, @RequestParam("sql")String sql, @RequestParam("param")Object... param);

    @GetMapping("/api/system/db/findOne")
    Object findOne(@RequestParam("dbKey")String dbKey, @RequestParam("sql")String sql, @RequestParam("param")Object... param);

    @GetMapping("/api/system/secret/get")
    SysApiSecretEntity getApiSecretByKey(@RequestParam("apiKey") String apiKey);

    @GetMapping("/sys/blQualitysetUser/getByUserId")
    BlQualitysetUserEntity getByUserId(@RequestParam("id") String id);


}
