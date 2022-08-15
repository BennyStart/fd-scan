package com.fardo.modules.api.factory;

import com.fardo.modules.api.SysAreaRemoteApi;
import com.fardo.modules.api.fallback.SysAreaRemoteApiFallback;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author taoyan
 * @date 2020/05/22
 */
@Component
public class SysAreaRemoteApiFallbackFactory implements FallbackFactory<SysAreaRemoteApi> {

    @Override
    public SysAreaRemoteApi create(Throwable throwable) {
        SysAreaRemoteApiFallback fallback = new SysAreaRemoteApiFallback();
        fallback.setCause(throwable);
        return fallback;
    }

}
