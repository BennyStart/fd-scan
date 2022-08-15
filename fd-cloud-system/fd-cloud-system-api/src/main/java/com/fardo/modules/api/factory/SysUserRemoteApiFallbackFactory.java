package com.fardo.modules.api.factory;

import com.fardo.modules.api.SysUserRemoteApi;
import com.fardo.modules.api.fallback.SysUserRemoteApiFallbackImpl;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author scott
 * @date 2020/05/22
 */
@Component
public class SysUserRemoteApiFallbackFactory implements FallbackFactory<SysUserRemoteApi> {

	@Override
	public SysUserRemoteApiFallbackImpl create(Throwable throwable) {
		SysUserRemoteApiFallbackImpl remoteUserServiceFallback = new SysUserRemoteApiFallbackImpl();
		remoteUserServiceFallback.setCause(throwable);
		return remoteUserServiceFallback;
	}
}
