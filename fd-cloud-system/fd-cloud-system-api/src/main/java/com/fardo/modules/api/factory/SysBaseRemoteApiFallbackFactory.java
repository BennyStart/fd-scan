package com.fardo.modules.api.factory;

import com.fardo.modules.api.SysBaseRemoteApi;
import feign.hystrix.FallbackFactory;
import com.fardo.modules.api.fallback.SysBaseRemoteApiFallback;
import org.springframework.stereotype.Component;

/**
 * @author taoyan
 * @date 2020/05/22
 */
@Component
public class SysBaseRemoteApiFallbackFactory implements FallbackFactory<SysBaseRemoteApi> {

    @Override
    public SysBaseRemoteApi create(Throwable throwable) {
        SysBaseRemoteApiFallback fallback = new SysBaseRemoteApiFallback();
        fallback.setCause(throwable);
        return fallback;
    }

}
