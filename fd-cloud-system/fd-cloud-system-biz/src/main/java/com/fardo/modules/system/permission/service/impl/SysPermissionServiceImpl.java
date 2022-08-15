package com.fardo.modules.system.permission.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fardo.common.constant.CacheConstant;
import com.fardo.common.constant.CommonConstant;
import com.fardo.common.exception.FdException;
import com.fardo.common.system.base.entity.BaseEntity;
import com.fardo.common.util.StringUtil;
import com.fardo.common.util.oConvertUtils;
import com.fardo.modules.shiro.authc.LoginUtil;
import com.fardo.modules.system.base.model.TreeModel;
import com.fardo.modules.system.constant.SysConstants;
import com.fardo.modules.system.permission.entity.SysPermissionEntity;
import com.fardo.modules.system.permission.enums.PermissionTypeEnum;
import com.fardo.modules.system.permission.mapper.SysPermissionMapper;
import com.fardo.modules.system.permission.model.SysPermissionTreeModel;
import com.fardo.modules.system.permission.service.ISysPermissionService;
import com.fardo.modules.system.role.mapper.SysRolePermissionMapper;
import com.fardo.modules.system.sys.vo.LoginUserVo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单权限表 服务实现类
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermissionEntity> implements ISysPermissionService {

	@Resource
	private SysPermissionMapper sysPermissionMapper;

	@Resource
	private SysRolePermissionMapper sysRolePermissionMapper;

	@Override
	public List<SysPermissionTreeModel> listAll(boolean withDel) {
		LambdaQueryWrapper<SysPermissionEntity> query = new LambdaQueryWrapper<SysPermissionEntity>();
		if(!withDel) {
			query.eq(SysPermissionEntity::getDelFlag, CommonConstant.DEL_FLAG_0);
			query.eq(SysPermissionEntity::getShowManage, CommonConstant.DEL_FLAG_1);
		}else{
			query.eq(SysPermissionEntity::getShowManage, CommonConstant.DEL_FLAG_1);
		}
		query.orderByAsc(SysPermissionEntity::getSortNo);
		List<SysPermissionEntity> list = this.list(query);
		List<SysPermissionTreeModel> treeList = new ArrayList<>();
		getTreeList(treeList, list, null);
		return treeList;
	}

	@Override
	public List<SysPermissionTreeModel> listForUser() {
		LoginUserVo loginUserVo = LoginUtil.getLoginUser();
		LambdaQueryWrapper<SysPermissionEntity> query = new LambdaQueryWrapper<>();
		query.eq(SysPermissionEntity::getShowManage, CommonConstant.DEL_FLAG_1);
		query.orderByAsc(SysPermissionEntity::getSortNo);
		List<SysPermissionTreeModel> treeList = new ArrayList<>();
		//如果用户是法度超级管理员，可以看到所有菜单，菜单不受功能启用和角色的控制
		if(SysConstants.USER_IDENTITY_SUPERADMIN.equals(loginUserVo.getUserIdentity())) {
			List<SysPermissionEntity> list = this.list(query);
			getTreeList(treeList, list, null);
			return treeList;
		}
		query.eq(SysPermissionEntity::getDelFlag, CommonConstant.DEL_FLAG_0);
		query.inSql(BaseEntity::getId, "select rp.permission_id from t_sys_role_permission rp where rp.role_id = '"+loginUserVo.getCurRoleId()+"'");
		List<SysPermissionEntity> list = this.list(query);
		//找出缺少的父级节点,菜单编码是xx xx xx xx 有规则的
		if(!CollectionUtils.isEmpty(list)) {
			//找出菜单类型
			List<String> codes = list.stream().filter(p-> !PermissionTypeEnum.BUTTON.getCode().equals(p.getMenuType().toString())).map(SysPermissionEntity::getPerms).collect(Collectors.toList());
			Set<String> seardCodes = new HashSet<>();
			for(String code : codes) {
				if(code.length()==6) {
					seardCodes.add(code.substring(0,2));
					seardCodes.add(code.substring(0,4));
				}else if(code.length()==4) {
					seardCodes.add(code.substring(0,2));
				}
				seardCodes.add(code);
			}
			codes = (List<String>) CollUtil.disjunction(codes,seardCodes);
			if(!CollectionUtils.isEmpty(codes)) {
				query = new LambdaQueryWrapper<>();
				query.eq(SysPermissionEntity::getDelFlag, CommonConstant.DEL_FLAG_0);
				query.in(SysPermissionEntity::getPerms, codes);
				query.orderByAsc(SysPermissionEntity::getSortNo);
				List<SysPermissionEntity> parentList = this.list(query);
				list.addAll(parentList);
			}
			//首页权限一定有，添加首页的权限
			if(list.stream().filter(p->p.getPerms().equals(SysConstants.PERMISSION_INDEX)).count() == 0) {
				list.add(0,sysPermissionMapper.getByPerms(SysConstants.PERMISSION_INDEX));
			}
		}
		getTreeList(treeList, list, null);
		return treeList;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void savePermissionEnable(List<String> ids) {
		if(!CollectionUtils.isEmpty(ids)) {
			List<SysPermissionEntity> list = this.list(null);
			Set<String> seardCodes = new HashSet<>();
			for(SysPermissionEntity po : list) {
				if(ids.contains(po.getId())) {
					if(po.getPerms().length()==6) {
						seardCodes.add(po.getPerms().substring(0,2));
						seardCodes.add(po.getPerms().substring(0,4));
					}else if(po.getPerms().length()==4) {
						seardCodes.add(po.getPerms().substring(0,2));
					}
					seardCodes.add(po.getPerms());
				}
			}
			for(SysPermissionEntity po : list) {
				if(seardCodes.contains(po.getPerms())) {
					po.setDelFlag(CommonConstant.DEL_FLAG_0);
				}else{
					po.setDelFlag(CommonConstant.DEL_FLAG_1);
				}
			}
			this.updateBatchById(list);
		}
	}

	@Override
	public List<TreeModel> queryListByParentId(String parentId) {
		return sysPermissionMapper.queryListByParentId(parentId);
	}

	/**
	  * 真实删除
	 */
	@Override
	@Transactional
	@CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE,allEntries=true)
	public void deletePermission(String id) throws FdException {
		SysPermissionEntity sysPermission = this.getById(id);
		if(sysPermission==null) {
			throw new FdException("未找到菜单信息");
		}
		String pid = sysPermission.getParentId();
		if(oConvertUtils.isNotEmpty(pid)) {
			int count = this.count(new QueryWrapper<SysPermissionEntity>().lambda().eq(SysPermissionEntity::getParentId, pid));
			if(count==1) {
				//若父节点无其他子节点，则该父节点是叶子节点
				this.sysPermissionMapper.setMenuLeaf(pid, 1);
			}
		}
		sysPermissionMapper.deleteById(id);
		// 该节点可能是子节点但也可能是其它节点的父节点,所以需要级联删除
		this.removeChildrenBy(sysPermission.getId());
		//关联删除
		Map map = new HashMap<>();
		map.put("permission_id",id);
		//删除角色授权表
		sysRolePermissionMapper.deleteByMap(map);
	}
	
	/**
	 * 根据父id删除其关联的子节点数据
	 * 
	 * @return
	 */
	public void removeChildrenBy(String parentId) {
		LambdaQueryWrapper<SysPermissionEntity> query = new LambdaQueryWrapper<>();
		// 封装查询条件parentId为主键,
		query.eq(SysPermissionEntity::getParentId, parentId);
		// 查出该主键下的所有子级
		List<SysPermissionEntity> permissionList = this.list(query);
		if (permissionList != null && permissionList.size() > 0) {
			String id = ""; // id
			int num = 0; // 查出的子级数量
			// 如果查出的集合不为空, 则先删除所有
			this.remove(query);
			// 再遍历刚才查出的集合, 根据每个对象,查找其是否仍有子级
			for (int i = 0, len = permissionList.size(); i < len; i++) {
				id = permissionList.get(i).getId();
				Map map = new HashMap<>();
				map.put("permission_id",id);
				//删除角色授权表
				sysRolePermissionMapper.deleteByMap(map);
				num = this.count(new LambdaQueryWrapper<SysPermissionEntity>().eq(SysPermissionEntity::getParentId, id));
				// 如果有, 则递归
				if (num > 0) {
					this.removeChildrenBy(id);
				}
			}
		}
	}
	
	/**
	  * 逻辑删除
	 */
	@Override
	@CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE,allEntries=true)
	//@CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE,allEntries=true,condition="#sysPermission.menuType==2")
	public void deletePermissionLogical(String id) throws FdException {
		SysPermissionEntity sysPermission = this.getById(id);
		if(sysPermission==null) {
			throw new FdException("未找到菜单信息");
		}
		String pid = sysPermission.getParentId();
		int count = this.count(new QueryWrapper<SysPermissionEntity>().lambda().eq(SysPermissionEntity::getParentId, pid));
		if(count==1) {
			//若父节点无其他子节点，则该父节点是叶子节点
			this.sysPermissionMapper.setMenuLeaf(pid, 1);
		}
		sysPermission.setDelFlag(1);
		this.updateById(sysPermission);
	}

	@Override
	@CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE,allEntries=true)
	public void addPermission(SysPermissionEntity sysPermission) throws FdException {
		//----------------------------------------------------------------------
		//判断是否是一级菜单，是的话清空父菜单
		if(CommonConstant.MENU_TYPE_0.equals(sysPermission.getMenuType())) {
			sysPermission.setParentId(null);
		}
		//----------------------------------------------------------------------
		String pid = sysPermission.getParentId();
		if(oConvertUtils.isNotEmpty(pid)) {
			//设置父节点不为叶子节点
			this.sysPermissionMapper.setMenuLeaf(pid, 0);
		}
		sysPermission.setDelFlag(0);
		sysPermission.setLeaf(true);
		this.save(sysPermission);
	}

	@Override
	@CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE,allEntries=true)
	public void editPermission(SysPermissionEntity sysPermission) throws FdException {
		SysPermissionEntity p = this.getById(sysPermission.getId());
		//TODO 该节点判断是否还有子节点
		if(p==null) {
			throw new FdException("未找到菜单信息");
		}else {
			//----------------------------------------------------------------------
			//Step1.判断是否是一级菜单，是的话清空父菜单ID
			if(CommonConstant.MENU_TYPE_0.equals(sysPermission.getMenuType())) {
				sysPermission.setParentId("");
			}
			//Step2.判断菜单下级是否有菜单，无则设置为叶子节点
			int count = this.count(new QueryWrapper<SysPermissionEntity>().lambda().eq(SysPermissionEntity::getParentId, sysPermission.getId()));
			if(count==0) {
				sysPermission.setLeaf(true);
			}
			//----------------------------------------------------------------------
			this.updateById(sysPermission);
			
			//如果当前菜单的父菜单变了，则需要修改新父菜单和老父菜单的，叶子节点状态
			String pid = sysPermission.getParentId();
			if((oConvertUtils.isNotEmpty(pid) && !pid.equals(p.getParentId())) || oConvertUtils.isEmpty(pid)&&oConvertUtils.isNotEmpty(p.getParentId())) {
				//a.设置新的父菜单不为叶子节点
				this.sysPermissionMapper.setMenuLeaf(pid, 0);
				//b.判断老的菜单下是否还有其他子菜单，没有的话则设置为叶子节点
				int cc = this.count(new QueryWrapper<SysPermissionEntity>().lambda().eq(SysPermissionEntity::getParentId, p.getParentId()));
				if(cc==0) {
					if(oConvertUtils.isNotEmpty(p.getParentId())) {
						this.sysPermissionMapper.setMenuLeaf(p.getParentId(), 1);
					}
				}
				
			}
		}
		
	}

	@Override
	public List<SysPermissionEntity> queryByUser(String username) {
		return this.sysPermissionMapper.queryByUser(username);
	}


	/**
	  *   获取模糊匹配规则的数据权限URL
	 */
	@Override
	@Cacheable(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE)
	public List<String> queryPermissionUrlWithStar() {
		return this.baseMapper.queryPermissionUrlWithStar();
	}

	@Override
	public boolean hasPermission(String username, SysPermissionEntity sysPermission) {
		int count = baseMapper.queryCountByUsername(username,sysPermission);
		if(count>0){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean hasPermission(String username, String url) {
		SysPermissionEntity sysPermission = new SysPermissionEntity();
		sysPermission.setUrl(url);
		int count = baseMapper.queryCountByUsername(username,sysPermission);
		if(count>0){
			return true;
		}else{
			return false;
		}
	}

	private void getTreeList(List<SysPermissionTreeModel> treeList, List<SysPermissionEntity> metaList, SysPermissionTreeModel temp) {
		for (SysPermissionEntity permission : metaList) {
			String tempPid = permission.getParentId();
			SysPermissionTreeModel tree = new SysPermissionTreeModel(permission);
			if (temp == null && oConvertUtils.isEmpty(tempPid)) {
				treeList.add(tree);
				if (!tree.getIsLeaf()) {
					getTreeList(treeList, metaList, tree);
				}
			} else if (temp != null && tempPid != null && tempPid.equals(temp.getId())) {
				temp.getChildren().add(tree);
				if (!tree.getIsLeaf()) {
					getTreeList(treeList, metaList, tree);
				}
			}

		}
	}

}
