package com.fardo.modules.api.factory;

import com.fardo.modules.api.SysJobRomoteApi;
import com.fardo.modules.api.fallback.SysJobRomoteApiFallbackImpl;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class SysJobRomoteApiFactory implements FallbackFactory<SysJobRomoteApi> {

    @Override
    public SysJobRomoteApiFallbackImpl create(Throwable throwable) {
        SysJobRomoteApiFallbackImpl fallback = new SysJobRomoteApiFallbackImpl();
        fallback.setCause(throwable);
        return fallback;
    }
}
