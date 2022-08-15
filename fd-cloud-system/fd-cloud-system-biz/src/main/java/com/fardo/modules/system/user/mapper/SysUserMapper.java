package com.fardo.modules.system.user.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fardo.modules.system.user.entity.SysUserEntity;
import com.fardo.modules.system.user.model.SysUserModel;
import com.fardo.modules.system.user.model.SysUserSiteModel;
import com.fardo.modules.system.user.model.SysUserSysDepartModel;
import com.fardo.modules.system.user.vo.DataScopeVo;
import com.fardo.modules.system.user.vo.SysUserDepVo;
import com.fardo.modules.system.user.vo.SysUserLikeVo;
import com.fardo.modules.system.user.vo.SysUserParamVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
public interface SysUserMapper extends BaseMapper<SysUserEntity> {
	/**
	  * 通过用户账号查询用户信息
	 * @param username
	 * @return
	 */

	public SysUserEntity getUserByName(@Param("username") String username);

	/**
	 *  根据部门Id查询用户信息
	 * @param page
	 * @param departId
	 * @return
	 */
	IPage<SysUserEntity> getUserByDepId(Page page, @Param("departId") String departId, @Param("username") String username);

	/**
	 *  根据用户Ids,查询用户所属部门名称信息
	 * @param userIds
	 * @return
	 */
	List<SysUserDepVo> getDepNamesByUserIds(@Param("userIds")List<String> userIds);

	/**
	 *  根据部门Ids,查询部门下用户信息
	 * @param page
	 * @param departIds
	 * @return
	 */
	IPage<SysUserEntity> getUserByDepIds(Page page, @Param("departIds") List<String> departIds, @Param("username") String username);

	/**
	 * 根据角色Id查询用户信息
	 * @param page
	 * @param
	 * @return
	 */
	IPage<SysUserEntity> getUserByRoleId(Page page, @Param("roleId") String roleId, @Param("username") String username);

	/**
	 * 根据用户名设置部门ID
	 * @param username
	 * @param departId
	 */
	void updateUserDepart(@Param("username") String username,@Param("departId") String departId);

	/**
	 * 根据手机号查询用户信息
	 * @param phone
	 * @return
	 */
	public SysUserEntity getUserByPhone(@Param("phone") String phone);


	/**
	 * 根据邮箱查询用户信息
	 * @param email
	 * @return
	 */
	public SysUserEntity getUserByEmail(@Param("email")String email);

	/**
	 * 根据 orgCode 查询用户，包括子部门下的用户
	 *
	 * @param page 分页对象, xml中可以从里面进行取值,传递参数 Page 即自动分页,必须放在第一位(你可以继承Page实现自己的分页对象)
	 * @param orgCode
	 * @param userParams 用户查询条件，可为空
	 * @return
	 */
	List<SysUserSysDepartModel> getUserByOrgCode(IPage page, @Param("orgCode") String orgCode, @Param("userParams") SysUserEntity userParams);


    /**
     * 查询 getUserByOrgCode 的Total
     *
     * @param orgCode
     * @param userParams 用户查询条件，可为空
     * @return
     */
    Integer getUserByOrgCodeTotal(@Param("orgCode") String orgCode, @Param("userParams") SysUserEntity userParams);

    /**
     * @Author scott
     * @Date 2019/12/13 16:10
     * @Description: 批量删除角色与用户关系
     */
	void deleteBathRoleUserRelation(@Param("roleIdArray") String[] roleIdArray);

    /**
     * @Author scott
     * @Date 2019/12/13 16:10
     * @Description: 批量删除角色与权限关系
     */
	void deleteBathRolePermissionRelation(@Param("roleIdArray") String[] roleIdArray);

	/**
	 * 查询被逻辑删除的用户
	 */
	List<SysUserEntity> selectLogicDeleted(@Param(Constants.WRAPPER) Wrapper<SysUserEntity> wrapper);

	/**
	 * 还原被逻辑删除的用户
	 */
	int revertLogicDeleted(@Param("userIds") String userIds, @Param("entity") SysUserEntity entity);

	/**
	 * 彻底删除被逻辑删除的用户
	 */
	int deleteLogicDeleted(@Param("userIds") String userIds);

    /** 更新空字符串为null【此写法有sql注入风险，禁止随便用】 */
    int updateNullByEmptyString(@Param("fieldName") String fieldName);

	/**
	 *  根据部门Ids,查询部门下用户信息
	 * @param departIds
	 * @return
	 */
	List<SysUserEntity> queryByDepIds(@Param("departIds")List<String> departIds, @Param("username") String username);

    List<SysUserModel> getUserList(@Param("idList") List<String> idList);

	List<SysUserModel> getUserSelect();

	IPage<SysUserEntity> getPageList(Page page, @Param("sysUserParamVo")SysUserParamVo sysUserParamVo, @Param("dataScopeVo")DataScopeVo dataScopeVo);

    List<SysUserModel> queryUserForDepart(@Param("departId") String departId, @Param("vo") SysUserLikeVo vo, @Param("noUserId") String noUserId);

    List<SysUserSiteModel> getUserListForPath(@Param("path") String path);

    int countByColumn(@Param("column")String column);

    int countByDistinctColumn(@Param("column")String column);

	SysUserModel getSysUserInfoModel(@Param("zfzh") String zfzh);
}
