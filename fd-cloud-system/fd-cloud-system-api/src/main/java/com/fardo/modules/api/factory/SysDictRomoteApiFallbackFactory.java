package com.fardo.modules.api.factory;

import com.fardo.modules.api.SysDictRomoteApi;
import com.fardo.modules.api.fallback.SysDictRomoteApiFallbackImpl;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class SysDictRomoteApiFallbackFactory implements FallbackFactory<SysDictRomoteApi> {

    @Override
    public SysDictRomoteApiFallbackImpl create(Throwable throwable) {
        SysDictRomoteApiFallbackImpl sysDepartRomoteApiFallback = new SysDictRomoteApiFallbackImpl();
        sysDepartRomoteApiFallback.setCause(throwable);
        return sysDepartRomoteApiFallback;
    }
}
