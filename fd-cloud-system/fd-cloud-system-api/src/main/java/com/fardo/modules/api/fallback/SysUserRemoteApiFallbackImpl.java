package com.fardo.modules.api.fallback;

import com.fardo.common.api.vo.Result;
import com.fardo.common.system.vo.SysUserCacheInfo;
import com.fardo.modules.api.SysUserRemoteApi;
import com.fardo.modules.system.permission.entity.SysPermissionEntity;
import com.fardo.modules.system.user.model.SysUserModel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @author scott
 * @date 2020/05/22
 */
@Slf4j
@Component
public class SysUserRemoteApiFallbackImpl implements SysUserRemoteApi {
	@Setter
	private Throwable cause;


	@Override
	public Result<Set<String>> getUserRolesSet(String username) {
		log.error("获取用户角色集合 {}",username, cause);
		return null;
	}

	@Override
	public Result<Set<String>> getUserPermissionsSet(String username) {
		log.error("获取用户权限集合 {}",username, cause);
		return null;
	}

	@Override
	public List<SysPermissionEntity> queryComponentPermission(String component) {
		return null;
	}

	@Override
	public List<SysPermissionEntity> queryRequestPermission(String method, String path) {
		return null;
	}

	@Override
	public SysUserCacheInfo getCacheUser(String username) {
		return null;
	}

	@Override
	public List<SysUserModel> findUserByIds(List<String> idList) {
		log.error("获取用户信息集合 {}",idList, cause);
		return null;
	}

    @Override
	public SysUserModel findUserByIdcard(String idcard) {
		log.error("根据身份证号获取人员信息失败：{}",idcard, cause);
		return null;
	}

	@Override
	public SysUserModel getUserById(String id) {
		log.error("根据id获取人员信息失败：{}",id, cause);
		return null;
	}

    @Override
    public Integer getUserNum() {
        log.error("获取用户数量失败：", cause);
        return null;
    }

	@Override
	public SysUserModel getUserByZfzh(String zfzh) {
		log.error("根据执法证号获取人员信息失败：{}",zfzh, cause);
		return null;
	}
}
