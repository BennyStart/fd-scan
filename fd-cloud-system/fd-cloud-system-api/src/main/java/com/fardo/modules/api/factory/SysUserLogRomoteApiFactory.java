package com.fardo.modules.api.factory;

import com.fardo.modules.api.SysUserLogRomoteApi;
import com.fardo.modules.api.fallback.SysUserLogRomoteApiFallbackImpl;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class SysUserLogRomoteApiFactory implements FallbackFactory<SysUserLogRomoteApi> {

    @Override
    public SysUserLogRomoteApiFallbackImpl create(Throwable throwable) {
        SysUserLogRomoteApiFallbackImpl sysUserLogRomoteApiFallback = new SysUserLogRomoteApiFallbackImpl();
        sysUserLogRomoteApiFallback.setCause(throwable);
        return sysUserLogRomoteApiFallback;
    }
}
