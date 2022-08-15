package com.fardo.modules.api.factory;

import com.fardo.modules.api.SysPrivateDataRomoteApi;
import com.fardo.modules.api.fallback.SysPrivateDataRomoteApiFallbackImpl;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class SysPrivateDataRomoteApiFactory implements FallbackFactory<SysPrivateDataRomoteApi> {

    @Override
    public SysPrivateDataRomoteApiFallbackImpl create(Throwable throwable) {
        SysPrivateDataRomoteApiFallbackImpl sysPrivateDataRomoteApiFallback = new SysPrivateDataRomoteApiFallbackImpl();
        sysPrivateDataRomoteApiFallback.setCause(throwable);
        return sysPrivateDataRomoteApiFallback;
    }
}
