package com.fardo.modules.api;

import com.fardo.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(contextId = "sysParameterRemoteApi", value = ServiceNameConstants.SYSTEM_SERVICE)
public interface SysParameterRemoteApi {

    @GetMapping("/api/system/parameterConfig/getSysParam")
    String getSysParam(@RequestParam("key") String key);

    @GetMapping("/api/system/parameterConfig/refreshClientConfig")
    void refreshClientConfig(@RequestParam("configId") String configId);

}
