package com.fardo.modules.system.role.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fardo.common.util.StringUtil;
import com.fardo.common.util.oConvertUtils;
import com.fardo.modules.system.role.entity.SysRolePermissionEntity;
import com.fardo.modules.system.role.mapper.SysRolePermissionMapper;
import com.fardo.modules.system.role.service.ISysRolePermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * <p>
 * 角色权限表 服务实现类
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
@Service
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermissionEntity> implements ISysRolePermissionService {

	@Override
	@Transactional
	public void saveRolePermission(String roleId, String permissionIds) {
		if(StringUtil.isNotEmpty(permissionIds)) {
			LambdaQueryWrapper<SysRolePermissionEntity> query = new QueryWrapper<SysRolePermissionEntity>().lambda().eq(SysRolePermissionEntity::getRoleId, roleId);
			this.remove(query);
			List<SysRolePermissionEntity> list = new ArrayList<SysRolePermissionEntity>();
			String[] arr = permissionIds.split(",");
			for (String p : arr) {
				if(oConvertUtils.isNotEmpty(p)) {
					SysRolePermissionEntity rolepms = new SysRolePermissionEntity(roleId, p);
					list.add(rolepms);
				}
			}
			this.saveBatch(list);
		}
	}

	@Override
	@Transactional
	public void saveRolePermission(String roleId, String permissionIds, String lastPermissionIds) {
		List<String> add = getDiff(lastPermissionIds,permissionIds);
		if(add!=null && add.size()>0) {
			List<SysRolePermissionEntity> list = new ArrayList<SysRolePermissionEntity>();
			for (String p : add) {
				if(oConvertUtils.isNotEmpty(p)) {
					SysRolePermissionEntity rolepms = new SysRolePermissionEntity(roleId, p);
					list.add(rolepms);
				}
			}
			this.saveBatch(list);
		}
		
		List<String> delete = getDiff(permissionIds,lastPermissionIds);
		if(delete!=null && delete.size()>0) {
			for (String permissionId : delete) {
				this.remove(new QueryWrapper<SysRolePermissionEntity>().lambda().eq(SysRolePermissionEntity::getRoleId, roleId).eq(SysRolePermissionEntity::getPermissionId, permissionId));
			}
		}
	}
	
	/**
	 * 从diff中找出main中没有的元素
	 * @param main
	 * @param diff
	 * @return
	 */
	private List<String> getDiff(String main,String diff){
		if(oConvertUtils.isEmpty(diff)) {
			return null;
		}
		if(oConvertUtils.isEmpty(main)) {
			return Arrays.asList(diff.split(","));
		}
		
		String[] mainArr = main.split(",");
		String[] diffArr = diff.split(",");
		Map<String, Integer> map = new HashMap<>();
		for (String string : mainArr) {
			map.put(string, 1);
		}
		List<String> res = new ArrayList<String>();
		for (String key : diffArr) {
			if(oConvertUtils.isNotEmpty(key) && !map.containsKey(key)) {
				res.add(key);
			}
		}
		return res;
	}

}
