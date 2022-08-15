package com.fardo.modules.system.role.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fardo.modules.system.role.entity.SysRoleEntity;
import com.fardo.common.api.vo.Result;
import com.fardo.modules.system.role.model.SysDataPermissionModel;
import com.fardo.modules.system.role.model.SysRoleModel;
import com.fardo.modules.system.role.vo.SysRoleParamVo;
import com.fardo.modules.system.sys.vo.LoginUserVo;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @Author scott
 * @since 2018-12-19
 */
public interface ISysRoleService extends IService<SysRoleEntity> {

    /**
     * 导入 excel ，检查 roleCode 的唯一性
     *
     * @param file
     * @param params
     * @return
     * @throws Exception
     */
    Result importExcelCheckRoleCode(MultipartFile file, ImportParams params) throws Exception;

    /**
     * 删除角色
     * @param roleid
     * @return
     */
    public boolean deleteRole(String roleid);

    /**
     * 批量删除角色
     * @param roleids
     * @return
     */
    public boolean deleteBatchRole(String[] roleids);

    /**
     * 获取角色详情（角色菜单权限，角色数据权限）
     * @param roleId
     * @return
     */
    SysRoleModel getRoleDetail(String roleId);

    /**
     * 更新角色数据权限
     * @param sysRoleModel
     * @return
     */
    boolean updateRoleAuth(SysRoleModel sysRoleModel);

    /**
     * 列表查询
     * @param roleVo
     * @return
     */
    IPage<SysRoleEntity> queryList(SysRoleParamVo roleVo);

    /**
     * 保存角色权限
     * @param sysRoleModel
     */
    SysRoleModel saveRoleAuth(SysRoleModel sysRoleModel);


    /**
     * 获取用户数据权限范围
     * @return
     */
    SysDataPermissionModel getUserDataScope();

    SysDataPermissionModel getUserDataScope(LoginUserVo loginUserVo);
}
