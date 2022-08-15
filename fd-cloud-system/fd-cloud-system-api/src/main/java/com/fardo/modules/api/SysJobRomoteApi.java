package com.fardo.modules.api;

import com.fardo.common.constant.ServiceNameConstants;
import com.fardo.modules.api.factory.SysPrivateDataRomoteApiFactory;
import com.fardo.modules.api.fallback.SysJobRomoteApiFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Component
@FeignClient(contextId = "SysJobRomoteApi", value = ServiceNameConstants.SYSTEM_SERVICE, fallback = SysJobRomoteApiFallbackImpl.class,fallbackFactory = SysPrivateDataRomoteApiFactory.class)
public interface SysJobRomoteApi {


    @GetMapping("/sys/job/updateCron/{id}/{day}/{hour}")
    void updateCron(@PathVariable("id") Integer id, @PathVariable("day") String day, @PathVariable("hour") String hour);


    @GetMapping("/sys/job/stop/{id}")
    void stop(@PathVariable("id") Integer id);
}
