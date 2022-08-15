package com.fardo.modules.api;

import com.fardo.common.constant.ServiceNameConstants;
import com.fardo.modules.api.factory.SysUserLogRomoteApiFactory;
import com.fardo.modules.api.fallback.SysUserLogRomoteApiFallbackImpl;
import com.fardo.modules.system.log.entity.SysUserLogEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Component
@FeignClient(contextId = "SysUserLogRomoteApi", value = ServiceNameConstants.SYSTEM_SERVICE, fallback = SysUserLogRomoteApiFallbackImpl.class,fallbackFactory = SysUserLogRomoteApiFactory.class)
public interface SysUserLogRomoteApi {


    @PostMapping("/sys/user/log/save")
    void save(@RequestBody SysUserLogEntity entity);

    @PostMapping("/sys/user/log/getUserDlcs")
    Integer getUserDlcs();

}
