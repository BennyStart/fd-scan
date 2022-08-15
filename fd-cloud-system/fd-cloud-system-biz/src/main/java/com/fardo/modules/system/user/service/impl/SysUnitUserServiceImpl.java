package com.fardo.modules.system.user.service.impl;

import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.ReUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fardo.common.api.vo.Result;
import com.fardo.common.api.vo.ResultCode;
import com.fardo.common.aspect.annotation.RedisLock;
import com.fardo.common.constant.CacheConstant;
import com.fardo.common.constant.CommonConstant;
import com.fardo.common.enums.IndexUniqEnum;
import com.fardo.common.exception.ApiException;
import com.fardo.common.exception.FdException;
import com.fardo.common.system.api.ISysBaseAPI;
import com.fardo.common.system.base.entity.BaseEntity;
import com.fardo.common.system.vo.LoginUser;
import com.fardo.common.system.vo.SysUserCacheInfo;
import com.fardo.common.util.*;
import com.fardo.modules.shiro.authc.LoginUtil;
import com.fardo.modules.system.constant.CacheKeyConstants;
import com.fardo.modules.system.constant.SysConstants;
import com.fardo.modules.system.depart.entity.SysDepartEntity;
import com.fardo.modules.system.depart.mapper.SysDepartMapper;
import com.fardo.modules.system.depart.model.SysDepartModel;
import com.fardo.modules.system.depart.service.ISysDepartService;
import com.fardo.modules.system.permission.entity.SysPermissionEntity;
import com.fardo.modules.system.permission.mapper.SysPermissionMapper;
import com.fardo.modules.system.role.entity.SysRoleEntity;
import com.fardo.modules.system.role.mapper.SysRoleMapper;
import com.fardo.modules.system.user.entity.SysUserDepartEntity;
import com.fardo.modules.system.user.entity.SysUserDepartRolesEntity;
import com.fardo.modules.system.user.entity.SysUserEntity;
import com.fardo.modules.system.user.entity.SysUserRoleEntity;
import com.fardo.modules.system.user.enums.PermissionEnum;
import com.fardo.modules.system.user.enums.UserResultCodeEnum;
import com.fardo.modules.system.user.mapper.*;
import com.fardo.modules.system.user.model.SysDepartUserModel;
import com.fardo.modules.system.user.model.SysUserModel;
import com.fardo.modules.system.user.model.SysUserSysDepartModel;
import com.fardo.modules.system.user.service.ISysUnitUserService;
import com.fardo.modules.system.user.service.ISysUserDepartService;
import com.fardo.modules.system.user.service.ISysUserRoleService;
import com.fardo.modules.system.user.service.ISysUserService;
import com.fardo.modules.system.user.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @Author: scott
 * @Date: 2018-12-20
 */
@Service
@Slf4j
public class SysUnitUserServiceImpl extends ServiceImpl<SysUnitUserMapper, SysUserEntity> implements ISysUnitUserService {

	private static String[] XXZM = new String[]{"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};

	private static AtomicBoolean taskStatus = new AtomicBoolean(false);

	private static TaskResultBaseVo taskVo = new TaskResultBaseVo();

	@Resource
	private SysUnitUserMapper sysUnitUserMapper;
	@Resource
	private SysPermissionMapper sysPermissionMapper;
	@Resource
	private SysUserRoleMapper sysUserRoleMapper;
	@Resource
	private SysUserDepartMapper sysUserDepartMapper;
	@Autowired
	private ISysBaseAPI sysBaseAPI;
	@Resource
	private SysDepartMapper sysDepartMapper;
	@Resource
	private SysRoleMapper sysRoleMapper;
	@Autowired
	private ISysUserRoleService sysUserRoleService;
	@Resource
	private SysUserDepartRolesMapper sysUserDepartRolesMapper;
	@Autowired
	private ISysUserDepartService sysUserDepartService;
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private ISysDepartService departService;

	@Value("${user.init.password}")
	private String initPassword;

	@Override
	public List<SysDepartUserModel> getDepartUserList(String userId, String departId) {
		List<SysDepartUserModel> resultList = new ArrayList<>();
		List<SysDepartModel> departModels = departService.querySubDeptList(departId);
		if(!CollectionUtils.isEmpty(departModels)) {
			for(SysDepartModel depart : departModels) {
				SysDepartUserModel model = new SysDepartUserModel(depart.getId(),depart.getDepartName(),SysDepartUserModel.TYPE_BM);
				resultList.add(model);
			}
		}
		if(StringUtil.isNotEmpty(departId)) {
			LambdaQueryWrapper<SysUserEntity> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(SysUserEntity::getDepartId, departId);
			//查询单位管理员
			queryWrapper.eq(SysUserEntity::getType, CommonConstant.UNIT_SYSTEM);
			queryWrapper.eq(SysUserEntity::getDelFlag, CommonConstant.DEL_FLAG_0);
			List<SysUserEntity> userList = this.sysUnitUserMapper.selectList(queryWrapper);
			if(!CollectionUtils.isEmpty(userList)) {
				for(SysUserEntity user : userList) {
					SysDepartUserModel model = new SysDepartUserModel(user.getId(),user.getRealname(),SysDepartUserModel.TYPE_RY);
					resultList.add(model);
				}
			}
		}
		return resultList;
	}

	@Override
	public SysUserVo getUserDetail(String userId) {
		//获取用户信息
		QueryWrapper<SysUserEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("ID",userId);
		queryWrapper.eq("type",CommonConstant.UNIT_SYSTEM);
		SysUserEntity sysUser = sysUnitUserMapper.selectOne(queryWrapper);
		if(ObjectUtils.isEmpty(sysUser)) {
			throw new ApiException(ResultCode.INVALIDPARAMETER);
		}
		SysUserVo vo = new SysUserVo();
		BeanUtils.copyProperties(sysUser,vo);
		vo.setOtherPositionFlag(false);
		//获取用户-部门-角色信息
		List<UserDepartRoleDetailVo> userDepartRoles = sysUserDepartRolesMapper.selectByUserId(userId);
		if(!CollectionUtils.isEmpty(userDepartRoles)) {
			List<String> departIds = userDepartRoles.stream().map(UserDepartRoleDetailVo::getDepId).distinct().collect(Collectors.toList());
			List<SysUserPositionVo> list = new ArrayList<>();
			for(String departId : departIds) {
				if(sysUser.getDepartId().equals(departId)) {
					vo.setDepartName(userDepartRoles.stream().filter(dr -> dr.getDepId().equals(departId)).map(UserDepartRoleDetailVo::getDepartName).distinct().collect(Collectors.joining()));
					vo.setRoleIds(userDepartRoles.stream().filter(dr -> dr.getDepId().equals(departId)).map(UserDepartRoleDetailVo::getRoleId).collect(Collectors.joining(",")));
					vo.setRoleNames(userDepartRoles.stream().filter(dr -> dr.getDepId().equals(departId)).map(UserDepartRoleDetailVo::getRoleName).collect(Collectors.joining(",")));
				}else{
					SysUserPositionVo up = new SysUserPositionVo();
					up.setDepartId(departId);
					up.setDepartName(userDepartRoles.stream().filter(dr -> dr.getDepId().equals(departId)).map(UserDepartRoleDetailVo::getDepartName).distinct().collect(Collectors.joining()));
					up.setRoleIds(userDepartRoles.stream().filter(dr -> dr.getDepId().equals(departId)).map(UserDepartRoleDetailVo::getRoleId).collect(Collectors.joining(",")));
					up.setRoleNames(userDepartRoles.stream().filter(dr -> dr.getDepId().equals(departId)).map(UserDepartRoleDetailVo::getRoleName).collect(Collectors.joining(",")));
					list.add(up);
				}
			}
			if(!CollectionUtils.isEmpty(list)) {
				vo.setOtherPositionFlag(true);
				vo.setOtherPositions(list);
			}
		}
		return vo;
	}

	@Override
	@Transactional
	public void saveUser(SysUserVo sysUserVo) {
		SysUserEntity sysUser = SysUserVo.getSysUser(null ,sysUserVo);
		//用户名为空，自动生成用户名
		if(StringUtil.isEmpty(sysUser.getUsername())) {
			sysUser.setUsername(getUserName(sysUser.getRealname(), sysUser.getIdcard()));
		}else{
			//检验用户名是否已存在
			if(getUserByName(sysUser.getUsername())!=null) {
				throw new ApiException(UserResultCodeEnum.USERNAME_EXIST);
			}
		}
		if(getUserByIdcard(sysUser.getIdcard()) != null) {
			throw new ApiException(UserResultCodeEnum.IDCARD_EXIST);
		}
		String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), sysUser.getPassword(), PasswordUtil.getStaticSalt());
		//用户密码加密
		sysUser.setPassword(passwordEncode);
		sysUser.setStatus(CommonConstant.USER_UNFREEZE);
		sysUser.setDelFlag(CommonConstant.DEL_FLAG_0);
		//单位管理员标识
		sysUser.setType(CommonConstant.UNIT_SYSTEM);
		//保存用户信息
		this.save(sysUser);
		sysUserVo.setId(sysUser.getId());
		//保存用户角色关系
		this.addUserWithRole(sysUser, sysUserVo.getRoleIds());
		//保存用户部门关系
		this.addUserWithDepart(sysUser, sysUser.getDepartId());
		if(!CollectionUtils.isEmpty(sysUserVo.getOtherPositions())) {
			for(SysUserPositionVo vo : sysUserVo.getOtherPositions()) {
				this.addUserWithDepart(sysUser,vo.getDepartId());
			}
		}
		//保存用户部门角色关系
		this.addUserWithDepartRoles(sysUser.getId(),sysUser.getDepartId(),sysUserVo.getRoleIds(), sysUserVo.getOtherPositions());
	}

	@Override
	@Transactional
	public void updateUser(SysUserUpdateVo sysUserVo) {
		QueryWrapper<SysUserEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("type",CommonConstant.UNIT_SYSTEM);
		queryWrapper.eq("ID",sysUserVo.getId());
		SysUserEntity sysUser = sysUnitUserMapper.selectOne(queryWrapper);
		if(sysUser == null) {
			throw new ApiException(ResultCode.INVALIDPARAMETER);
		}
		sysUser = SysUserUpdateVo.getSysUser(sysUser, sysUserVo);
		SysUserEntity idcardUser = getUserByIdcard(sysUser.getIdcard());
		if(idcardUser!= null && !idcardUser.getId().equals(sysUser.getId())) {
			throw new ApiException(UserResultCodeEnum.IDCARD_EXIST);
		}
		sysUser.setType(CommonConstant.UNIT_SYSTEM);
		//更新用户信息
		this.sysUnitUserMapper.updateById(sysUser);
		//更新用户角色关系
		sysUserRoleMapper.deleteByUserId(sysUser.getId());
		this.addUserWithRole(sysUser,sysUserVo.getRoleIds());
		//更新用户部门关系
		sysUserDepartMapper.deleteByUserId(sysUser.getId());
		this.addUserWithDepart(sysUser, sysUser.getDepartId());
		if(!CollectionUtils.isEmpty(sysUserVo.getOtherPositions())) {
			for(SysUserPositionVo vo : sysUserVo.getOtherPositions()) {
				this.addUserWithDepart(sysUser,vo.getDepartId());
			}
		}
		//更新用户部门角色关系
		sysUserDepartRolesMapper.deleteByUserId(sysUser.getId());
		this.addUserWithDepartRoles(sysUser.getId(),sysUser.getDepartId(),sysUserVo.getRoleIds(), sysUserVo.getOtherPositions());
	}

	@Override
	@Transactional
	public void resetPassword(SysUserPasswordResetVo userPasswordResetVo) {
		SysUserEntity sysUser = this.sysUnitUserMapper.selectById(userPasswordResetVo.getId());
		if(sysUser == null) {
			throw new ApiException(ResultCode.INVALIDPARAMETER);
		}
		String password = userPasswordResetVo.getPassword();
		String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, PasswordUtil.getStaticSalt());
		sysUser.setPassword(passwordEncode);
		this.sysUnitUserMapper.updateById(sysUser);
	}

	@Override
	public IPage<SysUserModel> getPageModelList(String userId, SysUserParamVo sysUserParamVo) {
		IPage<SysUserModel> modelIPage = new Page<>();
		DataScopeVo dataScopeVo = sysUserRoleService.getDataScope(userId, PermissionEnum.userList);
		if(StringUtil.isNotEmpty(sysUserParamVo.getIdcard())) {
			sysUserParamVo.setIdcard(String.format(SysConstants.FORMAT_LIKE_ANYWHERE, sysUserParamVo.getIdcard()));
		}
		if(StringUtil.isNotEmpty(sysUserParamVo.getPoliceNo())) {
			sysUserParamVo.setPoliceNo(String.format(SysConstants.FORMAT_LIKE_ANYWHERE, sysUserParamVo.getPoliceNo()));
		}
		if(StringUtil.isNotEmpty(sysUserParamVo.getRealname())) {
			sysUserParamVo.setRealname(String.format(SysConstants.FORMAT_LIKE_ANYWHERE, sysUserParamVo.getRealname()));
		}
		if(StringUtil.isNotEmpty(sysUserParamVo.getUsername())) {
			sysUserParamVo.setUsername(String.format(SysConstants.FORMAT_LIKE_ANYWHERE, sysUserParamVo.getUsername()));
		}
		if(StringUtil.isNotEmpty(sysUserParamVo.getDepartName())) {
			sysUserParamVo.setDepartName(String.format(SysConstants.FORMAT_LIKE_ANYWHERE, sysUserParamVo.getDepartName()));
		}
		// TODO 权限
		IPage<SysUserEntity> pageList = this.sysUnitUserMapper.getPageList(new Page(sysUserParamVo.getPageNo(), sysUserParamVo.getPageSize()), sysUserParamVo, null);
		BeanUtils.copyProperties(pageList, modelIPage, "records");
		if(!CollectionUtils.isEmpty(pageList.getRecords())) {
			List<SysUserModel> list = new ArrayList<>(pageList.getRecords().size());
			HashSet<String> departIds = new HashSet<>();
			pageList.getRecords().forEach(m -> {
				SysUserModel model = new SysUserModel();
				BeanUtils.copyProperties(m, model);
				departIds.add(m.getDepartId());
				list.add(model);
			});
			//查找相关部门
			List<SysDepartEntity> departs = sysDepartMapper.selectBatchIds(departIds);
			list.forEach(m -> {
				m.setDepartName(departs.stream().filter(d -> d.getId().equals(m.getDepartId())).map(d -> d.getDepartName()).collect(Collectors.joining(",")));
			});
			modelIPage.setRecords(list);
		}
		return modelIPage;
	}

	@Override
	@Transactional
	@CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
	public Result<?> addUser(SysUserEntity sysUser, String roles) {
		checkData(sysUser);
		SysDepartEntity departEntity = sysDepartMapper.selectById(sysUser.getDepartId());
		if(departEntity == null) {
			return Result.error("部门信息有误");
		}
		sysUser.setUsername(getUserName(sysUser.getRealname(), sysUser.getIdcard()));
		String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), sysUser.getPassword(), PasswordUtil.getStaticSalt());
		sysUser.setPassword(passwordEncode);
		sysUser.setStatus(CommonConstant.USER_UNFREEZE);
		sysUser.setDelFlag(CommonConstant.DEL_FLAG_0);
		this.save(sysUser);
		this.addUserWithRole(sysUser, roles);
		this.addUserWithDepart(sysUser, sysUser.getDepartId());
		return Result.ok();
	}

	@Override
	@Transactional
	@CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
	public Result<?> updateUser(SysUserEntity sysUser, String roles) {
		checkData(sysUser);
		SysUserEntity sysUserEntity = this.getById(sysUser.getId());
		if(sysUserEntity == null) {
			return Result.error("未找到对应实体");
		}
		if(!sysUserEntity.getDepartId().equals(sysUser.getDepartId())) {
			SysDepartEntity departEntity = sysDepartMapper.selectById(sysUser.getDepartId());
			if(departEntity == null) {
				return Result.error("部门信息有误");
			}
		}
		this.sysUnitUserMapper.updateById(sysUser);
		this.editUserWithRole(sysUser,roles);
		this.editUserWithDepart(sysUser, sysUser.getDepartId());
		return Result.ok();
	}

	private void checkData(SysUserEntity sysUser) {
		if (!IdcardUtil.isValidCard(sysUser.getIdcard())) {
			throw new FdException("身份证号格式有误");
		}
		if (!ReUtil.isMatch(SysConstants.RE_POLICE_NO, sysUser.getPoliceNo())) {
			throw new FdException("警号号格式有误");
		}
	}

	@Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    public Result<?> resetPassword(String username, String oldpassword, String newpassword, String confirmpassword) {
        SysUserEntity user = sysUnitUserMapper.getUserByName(username);
        if(!StringUtils.isEmpty(oldpassword)) {
			String passwordEncode = PasswordUtil.encrypt(username, oldpassword, PasswordUtil.getStaticSalt());
			if (!user.getPassword().equals(passwordEncode)) {
				return Result.error("旧密码输入错误!");
			}
		}
        if (oConvertUtils.isEmpty(newpassword)) {
            return Result.error("新密码不允许为空!");
        }
        if (!newpassword.equals(confirmpassword)) {
            return Result.error("两次输入密码不一致!");
        }
        String password = PasswordUtil.encrypt(username, newpassword, PasswordUtil.getStaticSalt());
        this.sysUnitUserMapper.update(new SysUserEntity().setPassword(password), new LambdaQueryWrapper<SysUserEntity>().eq(SysUserEntity::getId, user.getId()));
        return Result.ok("密码重置成功!");
    }

    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    public Result<?> changePassword(SysUserEntity sysUser) {
        String password = sysUser.getPassword();
        String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, PasswordUtil.getStaticSalt());
        sysUser.setPassword(passwordEncode);
        this.sysUnitUserMapper.updateById(sysUser);
        return Result.ok("密码修改成功!");
    }

    @Override
    @CacheEvict(value={CacheConstant.SYS_USERS_CACHE}, allEntries=true)
	@Transactional(rollbackFor = Exception.class)
	public boolean logicDeleteUser(String userId) {
		//逻辑删除用户
		SysUserEntity user = new SysUserEntity();
		user.setId(userId);
		user.setDelFlag(CommonConstant.DEL_FLAG_1);
		this.sysUnitUserMapper.updateById(user);
		//删除用户角色关系
		this.sysUserRoleMapper.deleteByUserId(userId);
		//删除用户部门关系
		this.sysUserDepartMapper.deleteByUserId(userId);
		//删除用户部门角色关系
		this.sysUserDepartRolesMapper.deleteByUserId(userId);
		return true;
	}

	@Override
    @CacheEvict(value={CacheConstant.SYS_USERS_CACHE}, allEntries=true)
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteBatchUsers(String userIds) {
		//1.删除用户
		this.removeByIds(Arrays.asList(userIds.split(",")));
		return false;
	}

	@Override
	public SysUserEntity getUserByName(String username) {
		return sysUnitUserMapper.getUserByName(username);
	}

	@Override
	public SysUserEntity getUserByPoliceNo(String policeNo) {
		return sysUnitUserMapper.selectOne(new QueryWrapper<SysUserEntity>().lambda().eq(SysUserEntity::getPoliceNo, policeNo).eq(SysUserEntity::getDelFlag,CommonConstant.DEL_FLAG_0));
	}

	@Override
	public SysUserEntity getUserByIdcard(String idcard) {
		return sysUnitUserMapper.selectOne(new QueryWrapper<SysUserEntity>().lambda().eq(SysUserEntity::getIdcard, idcard).eq(SysUserEntity::getDelFlag,CommonConstant.DEL_FLAG_0));
	}

	@Override
	@Transactional
	public void addUserWithRole(SysUserEntity user, String roles) {
		if(oConvertUtils.isNotEmpty(roles)) {
			String[] arr = roles.split(",");
			for (String roleId : arr) {
				SysUserRoleEntity userRole = new SysUserRoleEntity(user.getId(), roleId);
				sysUserRoleMapper.insert(userRole);
			}
		}
	}

	@Override
	@CacheEvict(value= {CacheConstant.SYS_USERS_CACHE}, allEntries=true)
	@Transactional
	public void editUserWithRole(SysUserEntity user, String roles) {
		sysUserRoleMapper.delete(new QueryWrapper<SysUserRoleEntity>().lambda().eq(SysUserRoleEntity::getUserId, user.getId()));
		addUserWithRole(user, roles);
	}


	@Override
	public List<String> getRole(String username) {
		return sysUserRoleMapper.getRoleByUserName(username);
	}

	/**
	 * 通过用户名获取用户角色集合
	 * @param username 用户名
     * @return 角色集合
	 */
	@Override
	public Set<String> getUserRolesSet(String username) {
		// 查询用户拥有的角色集合
		List<String> roles = sysUserRoleMapper.getRoleByUserName(username);
		log.info("-------通过数据库读取用户拥有的角色Rules------username： " + username + ",Roles size: " + (roles == null ? 0 : roles.size()));
		return new HashSet<>(roles);
	}

	/**
	 * 通过用户名获取用户权限集合
	 *
	 * @param username 用户名
	 * @return 权限集合
	 */
	@Override
	public Set<String> getUserPermissionsSet(String username) {
		Set<String> permissionSet = new HashSet<>();
		List<SysPermissionEntity> permissionList = sysPermissionMapper.queryByUser(username);
		for (SysPermissionEntity po : permissionList) {
//			// TODO URL规则有问题？
//			if (oConvertUtils.isNotEmpty(po.getUrl())) {
//				permissionSet.add(po.getUrl());
//			}
			if (oConvertUtils.isNotEmpty(po.getPerms())) {
				permissionSet.add(po.getPerms());
			}
		}
		log.info("-------通过数据库读取用户拥有的权限Perms------username： "+ username+",Perms size: "+ (permissionSet==null?0:permissionSet.size()) );
		return permissionSet;
	}

	@Override
	public SysUserCacheInfo getCacheUser(String username) {
		SysUserCacheInfo info = new SysUserCacheInfo();
		info.setOneDepart(true);
//		SysUser user = sysUnitUserMapper.getUserByName(username);
//		info.setSysUserCode(user.getUsername());
//		info.setSysUserName(user.getRealname());


		LoginUser user = sysBaseAPI.getUserByName(username);
		if(user!=null) {
			info.setSysUserCode(user.getUsername());
			info.setSysUserName(user.getRealname());
			info.setSysOrgCode(user.getOrgCode());
		}

		//多部门支持in查询
		List<SysDepartEntity> list = sysDepartMapper.queryUserDeparts(user.getId());
		List<String> sysMultiOrgCode = new ArrayList<String>();
		if(list==null || list.size()==0) {
			//当前用户无部门
			//sysMultiOrgCode.add("0");
		}else if(list.size()==1) {
			sysMultiOrgCode.add(list.get(0).getOrgCode());
		}else {
			info.setOneDepart(false);
			for (SysDepartEntity dpt : list) {
				sysMultiOrgCode.add(dpt.getOrgCode());
			}
		}
		info.setSysMultiOrgCode(sysMultiOrgCode);

		return info;
	}

	// 根据部门Id查询
	@Override
	public IPage<SysUserEntity> getUserByDepId(Page<SysUserEntity> page, String departId, String username) {
		return sysUnitUserMapper.getUserByDepId(page, departId,username);
	}

	@Override
	public IPage<SysUserEntity> getUserByDepIds(Page<SysUserEntity> page, List<String> departIds, String username) {
		return sysUnitUserMapper.getUserByDepIds(page, departIds,username);
	}

	@Override
	public Map<String, String> getDepNamesByUserIds(List<String> userIds) {
		List<SysUserDepVo> list = this.baseMapper.getDepNamesByUserIds(userIds);

		Map<String, String> res = new HashMap<String, String>();
		list.forEach(item -> {
					if (res.get(item.getUserId()) == null) {
						res.put(item.getUserId(), item.getDepartName());
					} else {
						res.put(item.getUserId(), res.get(item.getUserId()) + "," + item.getDepartName());
					}
				}
		);
		return res;
	}

	@Override
	public IPage<SysUserEntity> getUserByDepartIdAndQueryWrapper(Page<SysUserEntity> page, String departId, QueryWrapper<SysUserEntity> queryWrapper) {
		LambdaQueryWrapper<SysUserEntity> lambdaQueryWrapper = queryWrapper.lambda();

		lambdaQueryWrapper.eq(SysUserEntity::getDelFlag, "0");
        lambdaQueryWrapper.inSql(SysUserEntity::getId, "SELECT user_id FROM sys_user_depart WHERE dep_id = '" + departId + "'");

        return sysUnitUserMapper.selectPage(page, lambdaQueryWrapper);
	}

	@Override
	public IPage<SysUserSysDepartModel> queryUserByOrgCode(String orgCode, SysUserEntity userParams, IPage page) {
		List<SysUserSysDepartModel> list = baseMapper.getUserByOrgCode(page, orgCode, userParams);
		Integer total = baseMapper.getUserByOrgCodeTotal(orgCode, userParams);

		IPage<SysUserSysDepartModel> result = new Page<>(page.getCurrent(), page.getSize(), total);
		result.setRecords(list);

		return result;
	}

	// 根据角色Id查询
	@Override
	public IPage<SysUserEntity> getUserByRoleId(Page<SysUserEntity> page, String roleId, String username) {
		return sysUnitUserMapper.getUserByRoleId(page,roleId,username);
	}


	@Override
	@CacheEvict(value= {CacheConstant.SYS_USERS_CACHE}, key="#username")
	public void updateUserDepart(String username,String departId) {
		baseMapper.updateUserDepart(username, departId);
	}


	@Override
	public SysUserEntity getUserByPhone(String phone) {
		return sysUnitUserMapper.getUserByPhone(phone);
	}


	@Override
	public SysUserEntity getUserByEmail(String email) {
		return sysUnitUserMapper.getUserByEmail(email);
	}

	@Override
	@Transactional
	public void addUserWithDepart(SysUserEntity user, String selectedParts) {
		if(oConvertUtils.isNotEmpty(selectedParts)) {
			String[] arr = selectedParts.split(",");
			for (String deaprtId : arr) {
				SysUserDepartEntity userDeaprt = new SysUserDepartEntity(user.getId(), deaprtId);
				sysUserDepartMapper.insert(userDeaprt);
			}
		}
	}

	private void addUserWithDepartRoles(String userId, String departId, String roles, List<SysUserPositionVo> userPositionVos){
		if(oConvertUtils.isNotEmpty(roles)) {
			String[] arr = roles.split(",");
			for (String roleId : arr) {
				SysUserDepartRolesEntity userDepartRoles = new SysUserDepartRolesEntity(userId,departId,roleId);
				sysUserDepartRolesMapper.insert(userDepartRoles);
			}
		}
		if(!CollectionUtils.isEmpty(userPositionVos)) {
			for(SysUserPositionVo vo : userPositionVos) {
				String[] arr = vo.getRoleIds().split(",");
				for (String roleId : arr) {
					SysUserDepartRolesEntity userDepartRoles = new SysUserDepartRolesEntity(userId,vo.getDepartId(),roleId);
					sysUserDepartRolesMapper.insert(userDepartRoles);
				}
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value={CacheConstant.SYS_USERS_CACHE}, allEntries=true)
	public void editUserWithDepart(SysUserEntity user, String departs) {
		String[] arr = {};
		if(oConvertUtils.isNotEmpty(departs)){
			arr = departs.split(",");
		}
		//先删后加
		sysUserDepartMapper.delete(new QueryWrapper<SysUserDepartEntity>().lambda().eq(SysUserDepartEntity::getUserId, user.getId()));
		if(oConvertUtils.isNotEmpty(departs)) {
			for (String departId : arr) {
				SysUserDepartEntity userDepart = new SysUserDepartEntity(user.getId(), departId);
				sysUserDepartMapper.insert(userDepart);
			}
		}
	}


	/**
	   * 校验用户是否有效
	 * @param sysUser
	 * @return
	 */
	@Override
	public Result<?> checkUserIsEffective(SysUserEntity sysUser) {
		Result<?> result = new Result<Object>();
		//情况1：根据用户信息查询，该用户不存在
		if (sysUser == null) {
			result.error500("账号输入错误，请重新输入");
			sysBaseAPI.addLog("用户登录失败，用户不存在！", CommonConstant.LOG_TYPE_1, null);
			return result;
		}
		//情况2：根据用户信息查询，该用户已注销
		if (CommonConstant.DEL_FLAG_1.toString().equals(sysUser.getDelFlag())) {
			sysBaseAPI.addLog("用户登录失败，用户名:" + sysUser.getUsername() + "已注销！", CommonConstant.LOG_TYPE_1, null);
			result.error500("该用户已注销");
			return result;
		}
		//情况3：根据用户信息查询，该用户已冻结
		if (CommonConstant.USER_FREEZE.equals(sysUser.getStatus())) {
			sysBaseAPI.addLog("用户登录失败，用户名:" + sysUser.getUsername() + "已冻结！", CommonConstant.LOG_TYPE_1, null);
			result.error500("该用户已冻结");
			return result;
		}
		return result;
	}

	@Override
	public List<SysUserEntity> queryLogicDeleted() {
		return this.queryLogicDeleted(null);
	}

	@Override
	public List<SysUserEntity> queryLogicDeleted(LambdaQueryWrapper<SysUserEntity> wrapper) {
		if (wrapper == null) {
			wrapper = new LambdaQueryWrapper<>();
		}
		wrapper.eq(SysUserEntity::getDelFlag, "1");
		return sysUnitUserMapper.selectLogicDeleted(wrapper);
	}

	@Override
	public boolean revertLogicDeleted(List<String> userIds, SysUserEntity updateEntity) {
		String ids = String.format("'%s'", String.join("','", userIds));
		return sysUnitUserMapper.revertLogicDeleted(ids, updateEntity) > 0;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean removeLogicDeleted(List<String> userIds) {
		String ids = String.format("'%s'", String.join("','", userIds));
		// 1. 删除用户
		int line = sysUnitUserMapper.deleteLogicDeleted(ids);
		// 2. 删除用户部门关系
		line += sysUserDepartMapper.delete(new LambdaQueryWrapper<SysUserDepartEntity>().in(SysUserDepartEntity::getUserId, userIds));
		//3. 删除用户角色关系
		line += sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRoleEntity>().in(SysUserRoleEntity::getUserId, userIds));
		return line != 0;
	}

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateNullPhoneEmail() {
        sysUnitUserMapper.updateNullByEmptyString("email");
        sysUnitUserMapper.updateNullByEmptyString("phone");
        return true;
    }

	@Override
	public void saveThirdUser(SysUserEntity sysUser) {
		//保存用户
		String userid = UUIDGenerator.generate();
		sysUser.setId(userid);
		baseMapper.insert(sysUser);
		//获取第三方角色
		SysRoleEntity sysRole = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRoleEntity>().eq(SysRoleEntity::getRoleCode, "third_role"));
		//保存用户角色
		SysUserRoleEntity userRole = new SysUserRoleEntity();
		userRole.setRoleId(sysRole.getId());
		userRole.setUserId(userid);
		sysUserRoleMapper.insert(userRole);
	}

	@Override
	public List<SysUserEntity> queryByDepIds(List<String> departIds, String username) {
		return sysUnitUserMapper.queryByDepIds(departIds,username);
	}

	@Override
	public List<SysUserModel> getUserList(List<String> idList) {
		return sysUnitUserMapper.getUserList(idList);
	}

	@Override
	public List<SysUserModel> getUserSelect() {
		return sysUnitUserMapper.getUserSelect();
	}

	@Async
	@Override
	@Transactional
	@RedisLock
	public void importExcel(List<SysUserExcelVo> userExcelVos) {
		if(taskStatus.compareAndSet(false, true)){
			long start = System.currentTimeMillis();
			redisUtil.set(CacheKeyConstants.USER_IMP_TASK_START, true);
			try {
				if(userExcelVos != null) {
					taskVo.init();
					taskVo.refresh(TaskResultBaseVo.DOING, "正在校验导入数据", 0L);
					taskVo.setTotalCount(userExcelVos.size());
					flushCacheTaskVo(taskVo);
					SysUserExcelVo userExcelVo;
					int line = 2;
					int emptyCount = 0;
					//基础校验
					for (int i = 0; i < userExcelVos.size(); i++) {
						userExcelVo = userExcelVos.get(i);
						if(FieldUtils.allFieldIsNull(userExcelVo)){
							emptyCount++;
							continue;
						}
						if(StringUtil.isEmpty(userExcelVo.getRealname())) {
							throw new RuntimeException("第" + (line+i) +"行真实姓名不能为空");
						}
						if(StringUtil.isEmpty(userExcelVo.getIdcard())) {
							throw new RuntimeException("第" + (line+i) +"行身份证号不能为空");
						}
						if(StringUtil.isEmpty(userExcelVo.getDepartCode())) {
							throw new RuntimeException("第" + (line+i) +"行单位编码不能为空");
						}
						if(StringUtil.isEmpty(userExcelVo.getRoleCode())) {
							throw new RuntimeException("第" + (line+i) +"行角色编码不能为空");
						}
						if(StringUtil.isEmpty(userExcelVo.getPoliceNo())) {
							throw new RuntimeException("第" + (line+i) +"行警号不能为空");
						}
						if(!IdcardUtil.isValidCard(userExcelVo.getIdcard())) {
							throw new RuntimeException("第" + line+i +"行身份证号校验不通过");
						}
						if(!ReUtil.isMatch(SysConstants.RE_POLICE_NO,userExcelVo.getPoliceNo())) {
							throw new RuntimeException("第" + (line+i) +"行警号校验不通过");
						}
					}
					if(userExcelVos.stream().map(SysUserExcelVo::getIdcard).distinct().count() < (userExcelVos.size() - emptyCount)){
						throw new RuntimeException("身份证号存在重复");
					}
					if(userExcelVos.stream().map(SysUserExcelVo::getPoliceNo).distinct().count() < (userExcelVos.size() - emptyCount)){
						throw new RuntimeException("警号存在重复");
					}
					List<SysRoleEntity> roles = sysRoleMapper.selectList(null);
					Set<String> departCodes = new HashSet<>(userExcelVos.size());
					for(SysUserExcelVo vo : userExcelVos) {
						if(roles.stream().filter(role -> role.getRoleCode().equals(vo.getRoleCode())).count() == 0) {
							throw new RuntimeException(vo.getRoleCode() +"角色编码不存在");
						}
						departCodes.addAll(Arrays.asList(vo.getDepartCode().split(",")));
					}
					List<SysDepartEntity> departs = sysDepartMapper.selectList(new LambdaQueryWrapper<SysDepartEntity>().eq(SysDepartEntity::getDelFlag, CommonConstant.DEL_FLAG_0));
					for (String departCode : departCodes) {
						if(StringUtil.isEmpty(departCode)) continue;
						if(departs.stream().filter(d -> departCode.equals(d.getOrgCode())).count() == 0) {
							throw new RuntimeException(departCode +"单位编码不存在");
						}
					}
					taskVo.refresh(TaskResultBaseVo.DOING, "正在导入用户数据", 0L);
					flushCacheTaskVo(taskVo);
					final int batchSize = 100;
					List<SysUserEntity> userList = new ArrayList<>(batchSize);
					List<SysUserRoleEntity> urList = new ArrayList<>(batchSize);
					List<SysUserDepartEntity> udList = new ArrayList<>(batchSize);
					for(SysUserExcelVo vo : userExcelVos) {
						SysUserEntity user = new SysUserEntity();
						user.setId(UUIDGenerator.generate());
						if(StringUtil.isEmpty(vo.getUsername())) {
							user.setUsername(getUserName(vo.getRealname(), vo.getIdcard()));
						}
						user.setIdcard(vo.getIdcard());
						user.setPoliceNo(vo.getPoliceNo());
						user.setRealname(vo.getRealname());
						user.setMobilephone(vo.getPhone());
						String passwordEncode = PasswordUtil.encrypt(user.getUsername(), initPassword, PasswordUtil.getStaticSalt());
						user.setPassword(passwordEncode);
						user.setStatus(CommonConstant.USER_UNFREEZE);
						user.setDelFlag(CommonConstant.DEL_FLAG_0);
						List<String> dcs = Arrays.asList(vo.getDepartCode().split(","));
						for(String dc : dcs) {
							if(StringUtil.isEmpty(dc)) continue;
							String departId = departs.stream().filter(d -> d.getOrgCode().equals(dc)).map(BaseEntity::getId).collect(Collectors.joining());
							if(StringUtil.isEmpty(user.getDepartId())) {
								user.setDepartId(departId);
							}
							SysUserDepartEntity sysUserDepartEntity = new SysUserDepartEntity(user.getId(), departId);
							udList.add(sysUserDepartEntity);
						}
						String roleId = roles.stream().filter(r -> r.getRoleCode().equals(vo.getRoleCode())).map(BaseEntity::getId).collect(Collectors.joining());
						SysUserRoleEntity sysUserRoleEntity = new SysUserRoleEntity(user.getId(),roleId);
						urList.add(sysUserRoleEntity);
						userList.add(user);
						if(userList.size()%batchSize == 0) {
							this.saveBatch(userList);
							sysUserDepartService.saveBatch(udList);
							sysUserRoleService.saveBatch(urList);
							userList.clear();
							urList.clear();
							udList.clear();
							taskVo.setDoneCount(taskVo.getDoneCount() + batchSize);
							flushCacheTaskVo(taskVo);
						}
					}
					if(userList.size() > 0) {
						this.saveBatch(userList);
						sysUserDepartService.saveBatch(udList);
						sysUserRoleService.saveBatch(urList);
						taskVo.setDoneCount(taskVo.getDoneCount() +userList.size());
					}
					taskVo.refresh(TaskResultBaseVo.SUCCESS,"用户导入成功", System.currentTimeMillis() - start);
				}
			} catch (Exception e) {
				taskVo.setFailCount(taskVo.getFailCount()+1);
				String msg = e.getMessage();
				for (IndexUniqEnum uniqEnum : IndexUniqEnum.values()) {
					if(e.getMessage().contains(uniqEnum.getUniqKey())) {
						String value = msg.substring(msg.lastIndexOf("Duplicate entry '") + "Duplicate entry '".length(),msg.lastIndexOf("' for key"));
						msg = uniqEnum.getUniqText() + value + "已存在";
					}
				}
				taskVo.refresh(TaskResultBaseVo.FAIL, msg, System.currentTimeMillis() - start);
				log.error(e.getMessage(),e);
				throw e;
			} finally {
				flushCacheTaskVo(taskVo);
				redisUtil.del(CacheKeyConstants.USER_IMP_TASK_START);
				taskStatus.set(false);
			}
		}
	}

	@Override
	public TaskResultBaseVo getImportExcelResult() {
		TaskResultBaseVo vo = new TaskResultBaseVo();
		if(redisUtil.hasKey(CacheKeyConstants.USER_IMP_TASK)) {
			vo = (TaskResultBaseVo)redisUtil.get(CacheKeyConstants.USER_IMP_TASK);
		}
		return vo;
	}

	@Override
	public SysUserModel getUserModelByIdcard(String idcard) {
		SysUserModel model = new SysUserModel();
		LambdaQueryWrapper<SysUserEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(SysUserEntity::getDelFlag,CommonConstant.DEL_FLAG_0)
				.eq(SysUserEntity::getIdcard,idcard);
		SysUserEntity one = this.getOne(wrapper);
		if(one!=null){
			BeanUtils.copyProperties(one,model);
		}
		return model;
	}

	private void flushCacheTaskVo(TaskResultBaseVo taskVo) {
		redisUtil.set(CacheKeyConstants.USER_IMP_TASK, taskVo);
	}

	/**
	 * 描述:  生成用户名
	 * 版本: 1.0
	 * 日期: 2021/3/23 16:11
	 * 作者: suzc
	 * @param realName
	 * @param idcard
	 * @return java.lang.String
	 */
	private String getUserName(String realName, String idcard) {
		//姓
		String lastName = PinyinUtil.getPingYin(realName.substring(0,1));
		//名
		String firstName = PinyinUtil.getPinYinHeadChar(realName.substring(1));
		String username = lastName + firstName + idcard.substring(idcard.length() - 4);
		String temp = username;
		int i = 0;
		while (getUserByName(temp) != null) {
			if(i == XXZM.length) {
				break;
			}
			temp = username + XXZM[i];
			i++;
		}
		return temp;
	}


    /**
     * 根据部门id查询该部门下用户，过滤调当前用户
     * @param vo
     * @return
     */
    @Override
    public List<SysUserModel> queryUserForDepartNoCurrentUser(SysUserLikeVo vo) {
        String departId = LoginUtil.getLoginUser().getDepartVo().getId();
        if(StringUtil.isNotEmpty(vo.getCondition())) {
            vo.setCondition(vo.getCondition() + "%");
        }
        return this.sysUnitUserMapper.queryUserForDepart(departId, vo, LoginUtil.getLoginUser().getId());
    }
}
