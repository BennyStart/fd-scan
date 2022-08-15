package com.fardo.modules.system.permission.service;

import java.util.List;

import com.fardo.common.exception.FdException;
import com.fardo.modules.system.base.model.TreeModel;
import com.fardo.modules.system.permission.entity.SysPermissionEntity;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fardo.modules.system.permission.model.SysPermissionTreeModel;

/**
 * <p>
 * 菜单权限表 服务类
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
public interface ISysPermissionService extends IService<SysPermissionEntity> {

	/**
	 * 获取全部菜单
	 * @return
	 */
	List<SysPermissionTreeModel> listAll(boolean withDel);

	/**
	 * 获取用户菜单
	 * @return
	 */
	List<SysPermissionTreeModel> listForUser();

	void savePermissionEnable(List<String> ids);


	public List<TreeModel> queryListByParentId(String parentId);
	
	/**真实删除*/
	public void deletePermission(String id) throws FdException;
	/**逻辑删除*/
	public void deletePermissionLogical(String id) throws FdException;
	
	public void addPermission(SysPermissionEntity sysPermission) throws FdException;
	
	public void editPermission(SysPermissionEntity sysPermission) throws FdException;
	
	public List<SysPermissionEntity> queryByUser(String username);
	
	/**
	  * 查询出带有特殊符号的菜单地址的集合
	 * @return
	 */
	public List<String> queryPermissionUrlWithStar();

	/**
	 * 判断用户否拥有权限
	 * @param username
	 * @param sysPermission
	 * @return
	 */
	public boolean hasPermission(String username, SysPermissionEntity sysPermission);

	/**
	 * 根据用户和请求地址判断是否有此权限
	 * @param username
	 * @param url
	 * @return
	 */
	public boolean hasPermission(String username, String url);
}
