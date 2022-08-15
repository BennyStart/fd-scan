package com.fardo.modules.api;

import com.fardo.common.api.vo.Result;
import com.fardo.common.constant.ServiceNameConstants;
import com.fardo.common.system.vo.SysUserCacheInfo;
import com.fardo.modules.api.factory.SysUserRemoteApiFallbackFactory;
import com.fardo.modules.api.fallback.SysUserRemoteApiFallbackImpl;
import com.fardo.modules.system.permission.entity.SysPermissionEntity;
import com.fardo.modules.system.user.model.SysUserModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

@Component
@FeignClient(contextId = "sysUserRemoteApi", value = ServiceNameConstants.SYSTEM_SERVICE, fallback = SysUserRemoteApiFallbackImpl.class,fallbackFactory = SysUserRemoteApiFallbackFactory.class)
public interface SysUserRemoteApi {
    /**
     * 通过用户名获取用户角色集合
     *
     * @param username 用户名
     * @return 角色集合
     */
    @GetMapping("/sys/user/rolesSet/{username}")
    Result<Set<String>> getUserRolesSet(@PathVariable("username") String username);

    /**
     * 通过用户名获取用户权限集合
     *
     * @param username 用户名
     * @return 权限集合
     */
    @GetMapping("/sys/user/permissionsSet/{username}")
    Result<Set<String>> getUserPermissionsSet(@PathVariable("username") String username);

    /**
     * 通过component查询菜单配置信息
     * @param component
     * @return
     */
    @GetMapping("/sys/user/queryComponentPermission")
    List<SysPermissionEntity> queryComponentPermission(@RequestParam("component") String component);

    /**
     * 通过请求地址查找菜单信息
     * @param method
     * @param path
     * @return
     */
    @GetMapping("/sys/user/queryRequestPermission")
    List<SysPermissionEntity> queryRequestPermission(@RequestParam("method") String method, @RequestParam("path") String path);

    /**
     * 根据用户名获取用户信息
     * @param username
     * @return
     */
    @GetMapping("/sys/user/getCacheUser")
    SysUserCacheInfo getCacheUser(@RequestParam("username") String username);
    /**
     * 根据id获取用户信息
     * @param idList
     * @return
     */
    @GetMapping("/sys/user/getUserList")
    List<SysUserModel> findUserByIds(@RequestParam("idList") List<String> idList);

    /**
     * 根据身份证获取用户信息
     * @param idcard
     * @return
     */
    @GetMapping("/sys/user/getUserByIdcard")
    SysUserModel findUserByIdcard(@RequestParam("idcard") String idcard);

    /**
     * 根据id获取用户信息
     * @param id
     * @return
     */
    @GetMapping("/sys/user/getUserById")
    SysUserModel getUserById(@RequestParam("id") String id);

    /**
     * 获取用户数量
     * @return
     */
    @GetMapping("/sys/user/getUserNum")
    Integer getUserNum();

    /**
     * 根据执法证号获取用户信息
     * @param zfzh
     * @return
     */
    @GetMapping("/sys/user/getUserByZfzh")
    SysUserModel getUserByZfzh(@RequestParam("zfzh") String zfzh);
}
