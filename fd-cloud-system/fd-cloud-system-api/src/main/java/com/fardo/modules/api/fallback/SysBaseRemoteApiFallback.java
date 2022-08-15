package com.fardo.modules.api.fallback;

import com.alibaba.fastjson.JSONObject;
import com.fardo.common.api.vo.Result;
import com.fardo.common.system.vo.LoginUser;
import com.fardo.modules.api.SysBaseRemoteApi;
import com.fardo.modules.system.personal.entity.BlQualitysetUserEntity;
import com.fardo.modules.system.role.model.SysDataPermissionModel;
import com.fardo.modules.system.security.entity.SysApiSecretEntity;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class SysBaseRemoteApiFallback implements SysBaseRemoteApi {

    @Setter
    private Throwable cause;

    @Override
    public Result<LoginUser> getUserByName(String username) {
        log.info("--获取用户信息异常--username:"+username, cause);
        return null;
    }

    @Override
    public void saveSysLog(JSONObject jsonObject) {
        log.info("--包存日志信息异常", cause);
    }

    @Override
    public String queryDictTextByKey(String code, String key) {
        log.info("--查询字典信息异常, code:"+code+", key:"+key, cause);
        return null;
    }

    @Override
    public String queryTableDictTextByKey(String table, String text, String code, String key) {
        log.info("--查询表字典信息异常, table:"+table+", text:"+text+", code:"+code+", key:"+key, cause);
        return null;
    }

    @Override
    public SysDataPermissionModel getUserDataScope() {
        log.info("--获取用户数据权限异常", cause);
        return null;
    }

    @Override
    public List<Map<String, Object>> findList(String dbKey, String sql, Object... param) {
        log.info("--动态数据源获取数据异常", cause);
        return null;
    }

    @Override
    public Object findOne(String dbKey, String sql, Object... param) {
        log.info("--动态数据源获取数据异常", cause);
        return null;
    }

    @Override
    public SysApiSecretEntity getApiSecretByKey(String apiKey) {
        log.info("--获取秘钥异常", cause);
        return null;
    }

    @Override
    public BlQualitysetUserEntity getByUserId(String id) {
        log.info("--获取个人中心文书校验一次{}", id, cause);
        return null;
    }

}
