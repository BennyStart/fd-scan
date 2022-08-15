package com.fardo.modules.system.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fardo.common.constant.CommonConstant;
import com.fardo.common.system.base.entity.BaseEntity;
import com.fardo.common.util.StringUtil;
import com.fardo.modules.shiro.authc.LoginUtil;
import com.fardo.modules.system.permission.entity.SysPermissionEntity;
import com.fardo.modules.system.permission.mapper.SysPermissionMapper;
import com.fardo.modules.system.permission.model.SysPermissionModel;
import com.fardo.modules.system.sys.vo.LoginUserVo;
import com.fardo.modules.system.user.entity.SysUserPermissionEntity;
import com.fardo.modules.system.user.mapper.SysUserPermissionMapper;
import com.fardo.modules.system.user.model.UserFunctionModel;
import com.fardo.modules.system.user.service.ISysUserPermissionService;
import com.fardo.modules.system.user.vo.UserFunctionSaveVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户功能表 服务实现类
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
@Service
public class SysUserPermissionServiceImpl extends ServiceImpl<SysUserPermissionMapper, SysUserPermissionEntity> implements ISysUserPermissionService {

    @Resource
    private SysUserPermissionMapper userPermissionMapper;
    @Resource
    private SysPermissionMapper permissionMapper;

    @Override
    public UserFunctionModel getUserFunction() {
        LoginUserVo loginUserVo = LoginUtil.getLoginUser();
        //获取首页全部菜单
        List<SysPermissionEntity> permissionList = getIndexPermissionList(loginUserVo.getCurRoleId());
        //获取用户个人设置
        List<SysUserPermissionEntity> userPermissionList = getUserFunctionList(loginUserVo.getId());
        return getUserFunctionModel(permissionList,userPermissionList);
    }

    @Override
    @Transactional
    public void saveUserFunction(UserFunctionSaveVo vo) {
        LoginUserVo loginUserVo = LoginUtil.getLoginUser();
        //删除旧配置
        userPermissionMapper.deleteByUserId(loginUserVo.getId());
        //保存新配置
        List<SysUserPermissionEntity> list = new ArrayList<>();
        if(StringUtil.isNotEmpty(vo.getCommonIds())) {
            String[] ids = vo.getCommonIds().split(",");
            for (int i = 0; i < ids.length; i++) {
                SysUserPermissionEntity po = new SysUserPermissionEntity();
                po.setFuncType("common");
                po.setPermissionId(ids[i]);
                po.setSortNo((double) (i+1));
                po.setUserId(loginUserVo.getId());
                list.add(po);
            }
        }
        if(StringUtil.isNotEmpty(vo.getBizIds())) {
            String[] ids = vo.getBizIds().split(",");
            for (int i = 0; i < ids.length; i++) {
                SysUserPermissionEntity po = new SysUserPermissionEntity();
                po.setFuncType("biz");
                po.setPermissionId(ids[i]);
                po.setSortNo((double) (i+1));
                po.setUserId(loginUserVo.getId());
                list.add(po);
            }
        }
        if(CollectionUtils.isEmpty(list)) {
            SysUserPermissionEntity po = new SysUserPermissionEntity();
            po.setUserId(loginUserVo.getId());
            list.add(po);
        }
        this.saveBatch(list);
    }

    private UserFunctionModel getUserFunctionModel(List<SysPermissionEntity> permissionList, List<SysUserPermissionEntity> userPermissionList){
        UserFunctionModel model = new UserFunctionModel();
        List<SysPermissionModel> commonList = new ArrayList<>();
        List<SysPermissionModel> bizList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(userPermissionList)) {
            for(SysUserPermissionEntity up : userPermissionList) {
                for(SysPermissionEntity p : permissionList) {
                    if(!up.getPermissionId().equals(p.getId())) {
                        continue;
                    }
                    if("common".equals(up.getFuncType())) {
                        commonList.add(new SysPermissionModel(p));
                        break;
                    }
                    if("biz".equals(up.getFuncType())) {
                        bizList.add(new SysPermissionModel(p));
                        break;
                    }
                }
            }
        }
        List<SysPermissionModel> list = new ArrayList<>();
        List<String> userPermissionIds = commonList.stream().map(SysPermissionModel::getId).collect(Collectors.toList());
        userPermissionIds.addAll(bizList.stream().map(SysPermissionModel::getId).collect(Collectors.toList()));
        for(SysPermissionEntity p : permissionList) {
            if(CollectionUtils.isEmpty(userPermissionIds) || userPermissionIds.contains(p.getId())){
                continue;
            }
            list.add(new SysPermissionModel(p));
        }
        model.setCommonList(commonList);
        model.setBizList(bizList);
        model.setList(list);
        return model;
    }

    private List<SysUserPermissionEntity> getUserFunctionList(String userId){
        LambdaQueryWrapper<SysUserPermissionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserPermissionEntity::getUserId,userId);
        queryWrapper.orderByAsc(SysUserPermissionEntity::getSortNo);
        List<SysUserPermissionEntity> list = this.userPermissionMapper.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(list)) {
            return getDefalutFunctionList();
        }
        return list;
    }

    private List<SysUserPermissionEntity> getDefalutFunctionList(){
        LambdaQueryWrapper<SysUserPermissionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.isNull(SysUserPermissionEntity::getUserId);
        queryWrapper.orderByAsc(SysUserPermissionEntity::getSortNo);
        List<SysUserPermissionEntity> list = this.userPermissionMapper.selectList(queryWrapper);
        return list;
    }

    private List<SysPermissionEntity> getIndexPermissionList(String roleId){
        LambdaQueryWrapper<SysPermissionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.inSql(BaseEntity::getId, "select rp.permission_id from t_sys_role_permission rp where rp.role_id = '"+roleId+"'");
        //首页菜单标识
        queryWrapper.eq(SysPermissionEntity::getIndexFlag,1);
        //没有删除的
        queryWrapper.eq(SysPermissionEntity::getDelFlag, CommonConstant.DEL_FLAG_0);
        queryWrapper.orderByAsc(SysPermissionEntity::getSortNo);
        return this.permissionMapper.selectList(queryWrapper);
    }
}
