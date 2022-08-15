package com.fardo.modules.system.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fardo.common.api.vo.Result;
import com.fardo.common.system.vo.SysUserCacheInfo;
import com.fardo.modules.system.user.entity.SysUserEntity;
import com.fardo.modules.system.user.model.SysDepartUserModel;
import com.fardo.modules.system.user.model.SysUserModel;
import com.fardo.modules.system.user.model.SysUserSysDepartModel;
import com.fardo.modules.system.user.vo.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
public interface ISysUnitUserService extends IService<SysUserEntity> {

	/**
	 * 根据部门id获取用户列表及下级部门
	 * @param userId 用户id，用户权限控制
	 * @param departId 部门id，当部门id为空时，取用户最高级部门id
	 * @return
	 */
	List<SysDepartUserModel> getDepartUserList(String userId, String departId);

	/**
	 * 根据用户id获取用户详情
	 * @param userId 用户id
	 * @return
	 */
	SysUserVo getUserDetail(String userId);

	/**
	 * 保存用户信息
	 * @param sysUserVo
	 */
	void saveUser(SysUserVo sysUserVo);

	/**
	 * 更新用户信息
	 * @param sysUserVo
	 */
	void updateUser(SysUserUpdateVo sysUserVo);

	/**
	 * 重置用户密码
	 * @param userPasswordResetVo
	 */
	void resetPassword(SysUserPasswordResetVo userPasswordResetVo);


	IPage<SysUserModel> getPageModelList(String userId, SysUserParamVo sysUserParamVo);

	/**
	 * 添加用户
	 * @param sysUser
	 * @param roles
	 * @return
	 */
	Result<?> addUser(SysUserEntity sysUser, String roles);

	/**
	 * 修改用户
	 * @param sysUser
	 * @param roles
	 * @return
	 */
	Result<?> updateUser(SysUserEntity sysUser, String roles);
	/**
	 * 重置密码
	 *
	 * @param username
	 * @param oldpassword
	 * @param newpassword
	 * @param confirmpassword
	 * @return
	 */
	public Result<?> resetPassword(String username, String oldpassword, String newpassword, String confirmpassword);

	/**
	 * 修改密码
	 *
	 * @param sysUser
	 * @return
	 */
	public Result<?> changePassword(SysUserEntity sysUser);

	/**
	 * 删除用户
	 * @param userId
	 * @return
	 */
	public boolean logicDeleteUser(String userId);

	/**
	 * 批量删除用户
	 * @param userIds
	 * @return
	 */
	public boolean deleteBatchUsers(String userIds);

	public SysUserEntity getUserByName(String username);

	public SysUserEntity getUserByPoliceNo(String policeNo);

	public SysUserEntity getUserByIdcard(String idcard);

	/**
	 * 添加用户和用户角色关系
	 * @param user
	 * @param roles
	 */
	public void addUserWithRole(SysUserEntity user, String roles);


	/**
	 * 修改用户和用户角色关系
	 * @param user
	 * @param roles
	 */
	public void editUserWithRole(SysUserEntity user, String roles);

	/**
	 * 获取用户的授权角色
	 * @param username
	 * @return
	 */
	public List<String> getRole(String username);

	/**
	  * 查询用户信息包括 部门信息
	 * @param username
	 * @return
	 */
	public SysUserCacheInfo getCacheUser(String username);

	/**
	 * 根据部门Id查询
	 * @param
	 * @return
	 */
	public IPage<SysUserEntity> getUserByDepId(Page<SysUserEntity> page, String departId, String username);

	/**
	 * 根据部门Ids查询
	 * @param
	 * @return
	 */
	public IPage<SysUserEntity> getUserByDepIds(Page<SysUserEntity> page, List<String> departIds, String username);

	/**
	 * 根据 userIds查询，查询用户所属部门的名称（多个部门名逗号隔开）
	 * @param
	 * @return
	 */
	public Map<String,String> getDepNamesByUserIds(List<String> userIds);

    /**
     * 根据部门 Id 和 QueryWrapper 查询
     *
     * @param page
     * @param departId
     * @param queryWrapper
     * @return
     */
    public IPage<SysUserEntity> getUserByDepartIdAndQueryWrapper(Page<SysUserEntity> page, String departId, QueryWrapper<SysUserEntity> queryWrapper);

	/**
	 * 根据 orgCode 查询用户，包括子部门下的用户
	 *
	 * @param orgCode
	 * @param userParams 用户查询条件，可为空
	 * @param page 分页参数
	 * @return
	 */
	IPage<SysUserSysDepartModel> queryUserByOrgCode(String orgCode, SysUserEntity userParams, IPage page);

	/**
	 * 根据角色Id查询
	 * @param
	 * @return
	 */
	public IPage<SysUserEntity> getUserByRoleId(Page<SysUserEntity> page, String roleId, String username);

	/**
	 * 通过用户名获取用户角色集合
	 *
	 * @param username 用户名
	 * @return 角色集合
	 */
	Set<String> getUserRolesSet(String username);

	/**
	 * 通过用户名获取用户权限集合
	 *
	 * @param username 用户名
	 * @return 权限集合
	 */
	Set<String> getUserPermissionsSet(String username);

	/**
	 * 根据用户名设置部门ID
	 * @param username
	 * @param departId
	 */
	void updateUserDepart(String username,String departId);

	/**
	 * 根据手机号获取用户名和密码
	 */
	public SysUserEntity getUserByPhone(String phone);


	/**
	 * 根据邮箱获取用户
	 */
	public SysUserEntity getUserByEmail(String email);


	/**
	 * 添加用户和用户部门关系
	 * @param user
	 * @param selectedParts
	 */
	void addUserWithDepart(SysUserEntity user, String selectedParts);

	/**
	 * 编辑用户和用户部门关系
	 * @param user
	 * @param departs
	 */
	void editUserWithDepart(SysUserEntity user, String departs);

	/**
	   * 校验用户是否有效
	 * @param sysUser
	 * @return
	 */
	Result checkUserIsEffective(SysUserEntity sysUser);

	/**
	 * 查询被逻辑删除的用户
	 */
	List<SysUserEntity> queryLogicDeleted();

	/**
	 * 查询被逻辑删除的用户（可拼装查询条件）
	 */
	List<SysUserEntity> queryLogicDeleted(LambdaQueryWrapper<SysUserEntity> wrapper);

	/**
	 * 还原被逻辑删除的用户
	 */
	boolean revertLogicDeleted(List<String> userIds, SysUserEntity updateEntity);

	/**
	 * 彻底删除被逻辑删除的用户
	 */
	boolean removeLogicDeleted(List<String> userIds);

    /**
     * 更新手机号、邮箱空字符串为 null
     */
    @Transactional(rollbackFor = Exception.class)
    boolean updateNullPhoneEmail();

	/**
	 * 保存第三方用户信息
	 * @param sysUser
	 */
	void saveThirdUser(SysUserEntity sysUser);

	/**
	 * 根据部门Ids查询
	 * @param
	 * @return
	 */
	List<SysUserEntity> queryByDepIds(List<String> departIds, String username);

    List<SysUserModel> getUserList(List<String> idList);

    List<SysUserModel> getUserSelect();

    void importExcel(List<SysUserExcelVo> userExcelVos);

    TaskResultBaseVo getImportExcelResult();

    SysUserModel getUserModelByIdcard(String idcard);

    List<SysUserModel> queryUserForDepartNoCurrentUser(SysUserLikeVo vo);


}
