package com.fardo.modules.system.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.fardo.modules.system.depart.entity.SysDepartEntity;
import com.fardo.modules.system.depart.model.DepartIdModel;
import com.fardo.modules.system.user.entity.SysUserDepartEntity;
import com.fardo.modules.system.user.entity.SysUserDepartRolesEntity;
import com.fardo.modules.system.user.entity.SysUserEntity;

import java.util.List;

/**
 * <p>
 * SysUserDpeart用户组织机构service
 * </p>
 * @Author ZhiLin
 *
 */
public interface ISysUserDepartService extends IService<SysUserDepartEntity> {
	

	/**
	 * 根据指定用户id查询部门信息
	 * @param userId
	 * @return
	 */
	List<DepartIdModel> queryDepartIdsOfUser(String userId);
	

	/**
	 * 根据部门id查询用户信息
	 * @param depId
	 * @return
	 */
	List<SysUserEntity> queryUserByDepId(String depId);
  	/**
	 * 根据部门code，查询当前部门和下级部门的用户信息
	 */
	public List<SysUserEntity> queryUserByDepCode(String depCode, String realname);

    /**
     * 根据指定用户id查询部门信息
     * @param userId
     * @return
     */
    List<SysDepartEntity> queryDepartOfUser(String userId);

    List<SysUserDepartEntity> queryListByUserId(String userId);
}
