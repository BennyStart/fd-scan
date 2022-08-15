package com.fardo.modules.system.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fardo.common.util.oConvertUtils;
import com.fardo.modules.system.depart.entity.SysDepartEntity;
import com.fardo.modules.system.depart.model.DepartIdModel;
import com.fardo.modules.system.depart.service.ISysDepartService;
import com.fardo.modules.system.user.entity.SysUserDepartEntity;
import com.fardo.modules.system.user.entity.SysUserEntity;
import com.fardo.modules.system.user.mapper.SysUserDepartMapper;
import com.fardo.modules.system.user.mapper.SysUserMapper;
import com.fardo.modules.system.user.service.ISysUserDepartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <P>
 * 用户部门表实现类
 * <p/>
 * @Author ZhiLin
 *@since 2019-02-22
 */
@Service
public class SysUserDepartServiceImpl extends ServiceImpl<SysUserDepartMapper, SysUserDepartEntity> implements ISysUserDepartService {
	@Autowired
	private ISysDepartService sysDepartService;
	@Resource
	private SysUserMapper sysUserMapper;
	

	/**
	 * 根据用户id查询部门信息
	 */
	@Override
	public List<DepartIdModel> queryDepartIdsOfUser(String userId) {
        List<DepartIdModel> depIdModelList = new ArrayList<>();
        List<SysDepartEntity> depList = this.queryDepartOfUser(userId);
        if(depList != null && depList.size() > 0) {
            for(SysDepartEntity depart : depList) {
                depIdModelList.add(new DepartIdModel().convertByUserDepart(depart));
            }
        }
        return depIdModelList;
	}

    @Override
    public List<SysDepartEntity> queryDepartOfUser(String userId) {
        LambdaQueryWrapper<SysUserDepartEntity> queryUDep = new LambdaQueryWrapper<SysUserDepartEntity>();
        LambdaQueryWrapper<SysDepartEntity> queryDep = new LambdaQueryWrapper<SysDepartEntity>();
        try {
            queryUDep.eq(SysUserDepartEntity::getUserId, userId);
            List<String> depIdList = new ArrayList<>();
            List<SysUserDepartEntity> userDepList = this.list(queryUDep);
            if(userDepList != null && userDepList.size() > 0) {
                for(SysUserDepartEntity userDepart : userDepList) {
                    depIdList.add(userDepart.getDepId());
                }
                queryDep.in(SysDepartEntity::getId, depIdList);
                List<SysDepartEntity> depList = sysDepartService.list(queryDep);
                return depList;
            }
        }catch(Exception e) {
            e.fillInStackTrace();
        }
        return null;
    }

    /**
	 * 根据部门id查询用户信息
	 */
	@Override
	public List<SysUserEntity> queryUserByDepId(String depId) {
		LambdaQueryWrapper<SysUserDepartEntity> queryUDep = new LambdaQueryWrapper<SysUserDepartEntity>();
		queryUDep.eq(SysUserDepartEntity::getDepId, depId);
		List<String> userIdList = new ArrayList<>();
		List<SysUserDepartEntity> uDepList = this.list(queryUDep);
		if(uDepList != null && uDepList.size() > 0) {
			for(SysUserDepartEntity uDep : uDepList) {
				userIdList.add(uDep.getUserId());
			}
			List<SysUserEntity> userList = sysUserMapper.selectBatchIds(userIdList);
			//update-begin-author:taoyan date:201905047 for:接口调用查询返回结果不能返回密码相关信息
			for (SysUserEntity sysUser : userList) {
				sysUser.setSalt("");
				sysUser.setPassword("");
			}
			//update-end-author:taoyan date:201905047 for:接口调用查询返回结果不能返回密码相关信息
			return userList;
		}
		return new ArrayList<SysUserEntity>();
	}

	/**
	 * 根据部门code，查询当前部门和下级部门的 用户信息
	 */
	@Override
	public List<SysUserEntity> queryUserByDepCode(String depCode, String realname) {
		LambdaQueryWrapper<SysDepartEntity> queryByDepCode = new LambdaQueryWrapper<SysDepartEntity>();
		queryByDepCode.likeRight(SysDepartEntity::getOrgCode,depCode);
		List<SysDepartEntity> sysDepartList = sysDepartService.list(queryByDepCode);
		List<String> depIds = sysDepartList.stream().map(SysDepartEntity::getId).collect(Collectors.toList());

		LambdaQueryWrapper<SysUserDepartEntity> queryUDep = new LambdaQueryWrapper<SysUserDepartEntity>();
		queryUDep.in(SysUserDepartEntity::getDepId, depIds);
		List<String> userIdList = new ArrayList<>();
		List<SysUserDepartEntity> uDepList = this.list(queryUDep);
		if(uDepList != null && uDepList.size() > 0) {
			for(SysUserDepartEntity uDep : uDepList) {
				userIdList.add(uDep.getUserId());
			}
			LambdaQueryWrapper<SysUserEntity> queryUser = new LambdaQueryWrapper<SysUserEntity>();
			queryUser.in(SysUserEntity::getId,userIdList);
			if(oConvertUtils.isNotEmpty(realname)){
				queryUser.like(SysUserEntity::getRealname,realname.trim());
			}
			List<SysUserEntity> userList = sysUserMapper.selectList(queryUser);
			//update-begin-author:taoyan date:201905047 for:接口调用查询返回结果不能返回密码相关信息
			for (SysUserEntity sysUser : userList) {
				sysUser.setSalt("");
				sysUser.setPassword("");
			}
			//update-end-author:taoyan date:201905047 for:接口调用查询返回结果不能返回密码相关信息
			return userList;
		}
		return new ArrayList<SysUserEntity>();
	}

	@Override
	public List<SysUserDepartEntity> queryListByUserId(String userId) {
		LambdaQueryWrapper<SysUserDepartEntity> queryWrapper = new LambdaQueryWrapper<SysUserDepartEntity>();
		queryWrapper.eq(SysUserDepartEntity::getUserId,userId);
		return this.list(queryWrapper);
	}
}
