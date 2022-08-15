package com.fardo.modules.api.factory;

import com.fardo.modules.api.SysDepartRomoteApi;
import com.fardo.modules.api.SysUserRemoteApi;
import com.fardo.modules.api.fallback.SysDepartRomoteApiFallbackImpl;
import com.fardo.modules.api.fallback.SysUserRemoteApiFallbackImpl;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class SysDepartRomoteApiFallbackFactory implements FallbackFactory<SysDepartRomoteApi> {

    @Override
    public SysDepartRomoteApiFallbackImpl create(Throwable throwable) {
        SysDepartRomoteApiFallbackImpl sysDepartRomoteApiFallback = new SysDepartRomoteApiFallbackImpl();
        sysDepartRomoteApiFallback.setCause(throwable);
        return sysDepartRomoteApiFallback;
    }
}
