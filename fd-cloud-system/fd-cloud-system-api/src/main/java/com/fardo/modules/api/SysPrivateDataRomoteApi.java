package com.fardo.modules.api;

import com.fardo.common.constant.ServiceNameConstants;
import com.fardo.modules.api.factory.SysPrivateDataRomoteApiFactory;
import com.fardo.modules.api.fallback.SysPrivateDataRomoteApiFallbackImpl;
import com.fardo.modules.system.config.entity.SysPrivateDataEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Component
@FeignClient(contextId = "sysPrivateDataRomoteApi", value = ServiceNameConstants.SYSTEM_SERVICE, fallback = SysPrivateDataRomoteApiFallbackImpl.class,fallbackFactory = SysPrivateDataRomoteApiFactory.class)
public interface SysPrivateDataRomoteApi {


    @GetMapping("/sys/privateData/getById/{id}")
    SysPrivateDataEntity getById(@PathVariable("id") String id);


    @GetMapping("/sys/privateData/save/{id}/{data}")
    void save(@PathVariable("id") String id, @PathVariable("data") String data);
}
