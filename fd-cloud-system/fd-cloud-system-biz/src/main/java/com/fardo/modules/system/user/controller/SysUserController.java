package com.fardo.modules.system.user.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fardo.common.api.vo.Result;
import com.fardo.common.constant.CommonConstant;
import com.fardo.common.system.api.ISysBaseAPI;
import com.fardo.common.system.query.QueryGenerator;
import com.fardo.common.system.util.JwtUtil;
import com.fardo.common.system.vo.LoginUser;
import com.fardo.common.system.vo.SysUserCacheInfo;
import com.fardo.common.util.PasswordUtil;
import com.fardo.common.util.RedisUtil;
import com.fardo.common.util.StringUtil;
import com.fardo.common.util.oConvertUtils;
import com.fardo.modules.system.aspect.UrlMatchEnum;
import com.fardo.modules.system.config.service.ISysDictService;
import com.fardo.modules.system.constant.CacheKeyConstants;
import com.fardo.modules.system.depart.entity.SysDepartEntity;
import com.fardo.modules.system.depart.model.DepartIdModel;
import com.fardo.modules.system.depart.service.ISysDepartService;
import com.fardo.modules.system.permission.entity.SysPermissionEntity;
import com.fardo.modules.system.permission.service.ISysPermissionService;
import com.fardo.modules.system.role.model.SysDataPermissionModel;
import com.fardo.modules.system.role.service.ISysRoleService;
import com.fardo.modules.system.user.entity.SysUserEntity;
import com.fardo.modules.system.user.entity.SysUserRoleEntity;
import com.fardo.modules.system.user.model.SysUserModel;
import com.fardo.modules.system.user.model.SysUserSysDepartModel;
import com.fardo.modules.system.user.service.ISysUserDepartService;
import com.fardo.modules.system.user.service.ISysUserRoleService;
import com.fardo.modules.system.user.service.ISysUserService;
import com.fardo.modules.system.user.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Slf4j
@Api(tags = "用户列表")
@RestController
@RequestMapping("/sys/user")
public class SysUserController {
    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysDepartService sysDepartService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Autowired
    private ISysUserDepartService sysUserDepartService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${jeecg.path.upload}")
    private String upLoadPath;

    @Autowired
    private ISysPermissionService sysPermissionService;

    @Autowired
    private ISysDictService sysDictService;

    @ApiOperation("分页获取用户列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<SysUserModel>> queryPageList(SysUserParamVo sysUserParamVo, HttpServletRequest req) {
        Result<IPage<SysUserModel>> result = new Result<>();
        IPage<SysUserModel> modelIPage = sysUserService.getPageModelList(JwtUtil.getLoginUser().getId(),sysUserParamVo);
        result.setSuccess(true);
        result.setResult(modelIPage);
        return result;
    }

    @ApiOperation("查看用户信息")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public Result<SysUserVo> get(@ApiParam(name = "id", value = "用户id") @RequestParam("id") String id) {
        Result<SysUserVo> result = new Result<>();
        SysUserVo sysUserVo = new SysUserVo();
        SysUserEntity sysUserEntity = sysUserService.getById(id);
        BeanUtils.copyProperties(sysUserEntity, sysUserVo);
        result.setSuccess(true);
        result.setResult(sysUserVo);
        return result;
    }

    @ApiOperation("新增用户")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<?> add(@Valid @RequestBody SysUserVo sysUserVo) {
        SysUserEntity user = new SysUserEntity();
        BeanUtils.copyProperties(sysUserVo, user);
        Result<?> result = sysUserService.addUser(user, sysUserVo.getRoleIds());
        return result;
    }

    @ApiOperation("修改用户")
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public Result<?> update(@Valid @RequestBody SysUserUpdateVo sysUserVo) {
        Result<?> result = new Result<>();
        SysUserEntity sysUser = sysUserService.getById(sysUserVo.getId());
        if(sysUser==null) {
            result.error500("未找到对应实体");
        }else {
            SysUserEntity sysUserEntity = new SysUserEntity();
            BeanUtils.copyProperties(sysUserVo, sysUserEntity);
            sysUserService.updateUser(sysUserEntity, sysUserVo.getRoleIds());
        }
        return result;
    }

    @ApiOperation("删除用户")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<?> delete(@ApiParam(name = "id", value = "用户id") @RequestParam(name="id",required=true) String id) {
        sysBaseAPI.addLog("删除用户，id： " +id ,CommonConstant.LOG_TYPE_2, 3);
        this.sysUserService.logicDeleteUser(id);
        return Result.ok();
    }

    @ApiOperation("密码重置")
    @RequestMapping(value = "/resetPassword", method = RequestMethod.PUT)
    public Result<?> resetPassword(@Valid @RequestBody SysUserPasswordResetVo passwordResetVo) {
        SysUserEntity user = this.sysUserService.getById(passwordResetVo.getId());
        if(user==null) {
            return Result.error("用户不存在！");
        }
        return sysUserService.resetPassword(user.getUsername(),null,passwordResetVo.getPassword(),passwordResetVo.getPassword());
    }

    @ApiOperation("锁定&解解锁用户")
    @RequestMapping(value = "/lockBatch", method = RequestMethod.PUT)
    public Result<?> lockBatch(@Valid @RequestBody LockUserVo lockUserVo) {
        Result result = Result.ok();
        this.sysUserService.update(new SysUserEntity().setStatus(lockUserVo.getStatus()),
                new UpdateWrapper<SysUserEntity>().lambda().eq(SysUserEntity::getId,lockUserVo.getUserId()));
        return result;

    }

    @ApiOperation("修改密码")
    @RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
    public Result<?> changePassword(@Valid @RequestBody SysUserPasswordChangeVo passwordChangeVo) {
        SysUserEntity u = this.sysUserService.getById(passwordChangeVo.getId());
        if (u == null) {
            return Result.error("用户不存在！");
        }
        String oldPasswordEncode = PasswordUtil.encrypt(u.getUsername(), passwordChangeVo.getPassword(), PasswordUtil.getStaticSalt());
        if(!oldPasswordEncode.equals(u.getPassword())) {
            return Result.error("旧密码不正确！");
        }
        u.setPassword(passwordChangeVo.getNewPassword());
        return sysUserService.changePassword(u);
    }

    @ApiOperation("下载导入人员模板")
    @RequestMapping(value = "/downloadTemple", method = RequestMethod.GET)
    public void downloadTemple(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {

        response.setContentType("multipart/form-data");

        String userAgent = request.getHeader("User-Agent");
        String oraFileName = "导入用户模板.xls";
        String formFileName=oraFileName;

        // 针对IE或者以IE为内核的浏览器：
        if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            formFileName = java.net.URLEncoder.encode(formFileName, "UTF-8");
        } else {
            // 非IE浏览器的处理：
            formFileName = new String(formFileName.getBytes("UTF-8"), "ISO-8859-1");
        }
        response.setHeader("Content-disposition",
                String.format("attachment; filename=\"%s\"", formFileName));
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setCharacterEncoding("UTF-8");


        InputStream is = null;

        try {
            is = this.getClass().getResourceAsStream("/templates/"+"导入用户模板.xls");
            IOUtils.write(IOUtils.toByteArray(is), response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    //@RequiresPermissions("user:import")
    @ApiOperation("导入用户数据")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        if(redisUtil.hasKey(CacheKeyConstants.USER_IMP_TASK_START)) {
            return Result.error("已有其他用户导入任务正在进行，请稍后再试");
        }
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        Result result = Result.ok();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            try {
                List<SysUserExcelVo> listSysUsers = ExcelImportUtil.importExcel(file.getInputStream(), SysUserExcelVo.class, params);
                sysUserService.importExcel(listSysUsers);
            } catch (Exception e) {
                result.error500(e.getMessage());
                log.error(e.getMessage(), e);
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return result;
    }

    @ApiOperation("获取excel用户导入进度")
    @RequestMapping(value = "/getImportResult", method = RequestMethod.GET)
    public Result<TaskResultBaseVo> getImportResult() {
        Result<TaskResultBaseVo> result = new Result<>();
        TaskResultBaseVo taskResultBaseVo = sysUserService.getImportExcelResult();
        result.setResult(taskResultBaseVo);
        return result;
    }


    /**
     * 批量删除用户
     */
    //@RequiresRoles({"admin"})
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
        sysBaseAPI.addLog("批量删除用户， ids： " +ids ,CommonConstant.LOG_TYPE_2, 3);
        this.sysUserService.deleteBatchUsers(ids);
        return Result.ok("批量删除用户成功");
    }



    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
    public Result<SysUserEntity> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<SysUserEntity> result = new Result<SysUserEntity>();
        SysUserEntity sysUser = sysUserService.getById(id);
        if (sysUser == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(sysUser);
            result.setSuccess(true);
        }
        return result;
    }

    @RequestMapping(value = "/queryUserRole", method = RequestMethod.GET)
    public Result<List<String>> queryUserRole(@RequestParam(name = "userid", required = true) String userid) {
        Result<List<String>> result = new Result<>();
        List<String> list = new ArrayList<String>();
        List<SysUserRoleEntity> userRole = sysUserRoleService.list(new QueryWrapper<SysUserRoleEntity>().lambda().eq(SysUserRoleEntity::getUserId, userid));
        if (userRole == null || userRole.size() <= 0) {
            result.error500("未找到用户相关角色信息");
        } else {
            for (SysUserRoleEntity sysUserRole : userRole) {
                list.add(sysUserRole.getRoleId());
            }
            result.setSuccess(true);
            result.setResult(list);
        }
        return result;
    }


    /**
     *  校验用户账号是否唯一<br>
     *  可以校验其他 需要检验什么就传什么。。。
     *
     * @param sysUser
     * @return
     */
    @RequestMapping(value = "/checkOnlyUser", method = RequestMethod.GET)
    public Result<Boolean> checkOnlyUser(SysUserEntity sysUser) {
        Result<Boolean> result = new Result<>();
        //如果此参数为false则程序发生异常
        result.setResult(true);
        try {
            //通过传入信息查询新的用户信息
            SysUserEntity user = sysUserService.getOne(new QueryWrapper<SysUserEntity>(sysUser));
            if (user != null) {
                result.setSuccess(false);
                result.setMessage("用户账号已存在");
                return result;
            }

        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
        result.setSuccess(true);
        return result;
    }

    /**
     * 查询指定用户和部门关联的数据
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/userDepartList", method = RequestMethod.GET)
    public Result<List<DepartIdModel>> getUserDepartsList(@RequestParam(name = "userId", required = true) String userId) {
        Result<List<DepartIdModel>> result = new Result<>();
        try {
            List<DepartIdModel> depIdModelList = this.sysUserDepartService.queryDepartIdsOfUser(userId);
            if (depIdModelList != null && depIdModelList.size() > 0) {
                result.setSuccess(true);
                result.setMessage("查找成功");
                result.setResult(depIdModelList);
            } else {
                result.setSuccess(false);
                result.setMessage("查找失败");
            }
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("查找过程中出现了异常: " + e.getMessage());
            return result;
        }

    }

    /**
     * 生成在添加用户情况下没有主键的问题,返回给前端,根据该id绑定部门数据
     *
     * @return
     */
    @RequestMapping(value = "/generateUserId", method = RequestMethod.GET)
    public Result<String> generateUserId() {
        Result<String> result = new Result<>();
        System.out.println("我执行了,生成用户ID==============================");
        String userId = UUID.randomUUID().toString().replace("-", "");
        result.setSuccess(true);
        result.setResult(userId);
        return result;
    }

    /**
     * 根据部门id查询用户信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/queryUserByDepId", method = RequestMethod.GET)
    public Result<List<SysUserEntity>> queryUserByDepId(@RequestParam(name = "id", required = true) String id, @RequestParam(name="realname",required=false) String realname) {
        Result<List<SysUserEntity>> result = new Result<>();
        //List<SysUser> userList = sysUserDepartService.queryUserByDepId(id);
        SysDepartEntity sysDepart = sysDepartService.getById(id);
        List<SysUserEntity> userList = sysUserDepartService.queryUserByDepCode(sysDepart.getOrgCode(),realname);

        //批量查询用户的所属部门
        //step.1 先拿到全部的 useids
        //step.2 通过 useids，一次性查询用户的所属部门名字
//        List<String> userIds = userList.stream().map(SysUser::getId).collect(Collectors.toList());
//        if(userIds!=null && userIds.size()>0){
//            Map<String,String>  useDepNames = sysUserService.getDepNamesByUserIds(userIds);
//            userList.forEach(item->{
//                //TODO 临时借用这个字段用于页面展示
//                item.setOrgCode(useDepNames.get(item.getId()));
//            });
//        }

        try {
            result.setSuccess(true);
            result.setResult(userList);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            return result;
        }
    }

    /**
     * 导出excel
     *
     * @param request
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(SysUserEntity sysUser, HttpServletRequest request) {
        // Step.1 组装查询条件
        QueryWrapper<SysUserEntity> queryWrapper = QueryGenerator.initQueryWrapper(sysUser, request.getParameterMap());
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //update-begin--Author:kangxiaolin  Date:20180825 for：[03]用户导出，如果选择数据则只导出相关数据--------------------
        String selections = request.getParameter("selections");
        if(!oConvertUtils.isEmpty(selections)){
            queryWrapper.in("id",selections.split(","));
        }
        //update-end--Author:kangxiaolin  Date:20180825 for：[03]用户导出，如果选择数据则只导出相关数据----------------------
        List<SysUserEntity> pageList = sysUserService.list(queryWrapper);

        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "用户列表");
        mv.addObject(NormalExcelConstants.CLASS, SysUserEntity.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ExportParams exportParams = new ExportParams("用户列表数据", "导出人:"+user.getRealname(), "导出信息");
        exportParams.setImageBasePath(upLoadPath);
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * @功能：根据id 批量查询
     * @param userIds
     * @return
     */
    @RequestMapping(value = "/queryByIds", method = RequestMethod.GET)
    public Result<Collection<SysUserEntity>> queryByIds(@RequestParam String userIds) {
        Result<Collection<SysUserEntity>> result = new Result<>();
        String[] userId = userIds.split(",");
        Collection<String> idList = Arrays.asList(userId);
        Collection<SysUserEntity> userRole = sysUserService.listByIds(idList);
        result.setSuccess(true);
        result.setResult(userRole);
        return result;
    }

    @RequestMapping(value = "/userRoleList", method = RequestMethod.GET)
    public Result<IPage<SysUserEntity>> userRoleList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                     @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<SysUserEntity>> result = new Result<IPage<SysUserEntity>>();
        Page<SysUserEntity> page = new Page<SysUserEntity>(pageNo, pageSize);
        String roleId = req.getParameter("roleId");
        String username = req.getParameter("username");
        IPage<SysUserEntity> pageList = sysUserService.getUserByRoleId(page,roleId,username);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }
    /**
     *   删除指定角色的用户关系
     * @param
     * @return
     */
    //@RequiresRoles({"admin"})
    @RequestMapping(value = "/deleteUserRole", method = RequestMethod.DELETE)
    public Result<SysUserRoleEntity> deleteUserRole(@RequestParam(name="roleId") String roleId,
                                                    @RequestParam(name="userId",required=true) String userId
    ) {
        Result<SysUserRoleEntity> result = new Result<SysUserRoleEntity>();
        try {
            QueryWrapper<SysUserRoleEntity> queryWrapper = new QueryWrapper<SysUserRoleEntity>();
            queryWrapper.eq("role_id", roleId).eq("user_id",userId);
            sysUserRoleService.remove(queryWrapper);
            result.success("删除成功!");
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败！");
        }
        return result;
    }

    /**
     * 批量删除指定角色的用户关系
     *
     * @param
     * @return
     */
    //@RequiresRoles({"admin"})
    @RequestMapping(value = "/deleteUserRoleBatch", method = RequestMethod.DELETE)
    public Result<SysUserRoleEntity> deleteUserRoleBatch(
            @RequestParam(name="roleId") String roleId,
            @RequestParam(name="userIds",required=true) String userIds) {
        Result<SysUserRoleEntity> result = new Result<SysUserRoleEntity>();
        try {
            QueryWrapper<SysUserRoleEntity> queryWrapper = new QueryWrapper<SysUserRoleEntity>();
            queryWrapper.eq("role_id", roleId).in("user_id",Arrays.asList(userIds.split(",")));
            sysUserRoleService.remove(queryWrapper);
            result.success("删除成功!");
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败！");
        }
        return result;
    }

    /**
     * 部门用户列表
     */
    @RequestMapping(value = "/departUserList", method = RequestMethod.GET)
    public Result<IPage<SysUserEntity>> departUserList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                       @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<SysUserEntity>> result = new Result<IPage<SysUserEntity>>();
        Page<SysUserEntity> page = new Page<SysUserEntity>(pageNo, pageSize);
        String depId = req.getParameter("depId");
        String username = req.getParameter("username");
        //根据部门ID查询,当前和下级所有的部门IDS
        List<String> subDepids = new ArrayList<>();
        //部门id为空时，查询我的部门下所有用户
        if(oConvertUtils.isEmpty(depId)){
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            int userIdentity = user.getUserIdentity() != null?user.getUserIdentity():CommonConstant.USER_IDENTITY_1;
            if(oConvertUtils.isNotEmpty(userIdentity) && userIdentity == CommonConstant.USER_IDENTITY_2 ){
                subDepids = sysDepartService.getMySubDepIdsByDepId(user.getDepartIds());
            }
        }else{
            subDepids = sysDepartService.getSubDepIdsByDepId(depId);
        }
        if(subDepids != null && subDepids.size()>0){
            IPage<SysUserEntity> pageList = sysUserService.getUserByDepIds(page,subDepids,username);
            //批量查询用户的所属部门
            //step.1 先拿到全部的 useids
            //step.2 通过 useids，一次性查询用户的所属部门名字
//            List<String> userIds = pageList.getRecords().stream().map(SysUser::getId).collect(Collectors.toList());
//            if(userIds!=null && userIds.size()>0){
//                Map<String, String> useDepNames = sysUserService.getDepNamesByUserIds(userIds);
//                pageList.getRecords().forEach(item -> {
//                    //批量查询用户的所属部门
//                    item.setOrgCode(useDepNames.get(item.getId()));
//                });
//            }
            result.setSuccess(true);
            result.setResult(pageList);
        }else{
            result.setSuccess(true);
            result.setResult(null);
        }
        return result;
    }


    /**
     * 根据 orgCode 查询用户，包括子部门下的用户
     * 若某个用户包含多个部门，则会显示多条记录，可自行处理成单条记录
     */
    @GetMapping("/queryByOrgCode")
    public Result<?> queryByDepartId(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "orgCode") String orgCode,
            SysUserEntity userParams
    ) {
        IPage<SysUserSysDepartModel> pageList = sysUserService.queryUserByOrgCode(orgCode, userParams, new Page(pageNo, pageSize));
        return Result.ok(pageList);
    }

    /**
     * 根据 orgCode 查询用户，包括子部门下的用户
     * 针对通讯录模块做的接口，将多个部门的用户合并成一条记录，并转成对前端友好的格式
     */
    @GetMapping("/queryByOrgCodeForAddressList")
    public Result<?> queryByOrgCodeForAddressList(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "orgCode",required = false) String orgCode,
            SysUserEntity userParams
    ) {
        IPage page = new Page(pageNo, pageSize);
        IPage<SysUserSysDepartModel> pageList = sysUserService.queryUserByOrgCode(orgCode, userParams, page);
        List<SysUserSysDepartModel> list = pageList.getRecords();

        // 记录所有出现过的 user, key = userId
        Map<String, JSONObject> hasUser = new HashMap<>(list.size());

        JSONArray resultJson = new JSONArray(list.size());

        for (SysUserSysDepartModel item : list) {
            String userId = item.getId();
            // userId
            JSONObject getModel = hasUser.get(userId);
            // 之前已存在过该用户，直接合并数据
            if (getModel != null) {
                String departName = getModel.get("departName").toString();
                getModel.put("departName", (departName + " | " + item.getDepartName()));
            } else {
                // 将用户对象转换为json格式，并将部门信息合并到 json 中
                JSONObject json = JSON.parseObject(JSON.toJSONString(item));
                json.remove("id");
                json.put("userId", userId);
                json.put("departId", item.getDepartId());
                json.put("departName", item.getDepartName());
//                json.put("avatar", item.getSysUser().getAvatar());
                resultJson.add(json);
                hasUser.put(userId, json);
            }
        }

        IPage<JSONObject> result = new Page<>(pageNo, pageSize, pageList.getTotal());
        result.setRecords(resultJson.toJavaList(JSONObject.class));
        return Result.ok(result);
    }




    /**
     *  查询当前用户的所有部门/当前部门编码
     * @return
     */
    @RequestMapping(value = "/getCurrentUserDeparts", method = RequestMethod.GET)
    public Result<Map<String,Object>> getCurrentUserDeparts() {
        Result<Map<String,Object>> result = new Result<Map<String,Object>>();
        try {
            LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
            List<SysDepartEntity> list = this.sysDepartService.queryUserDeparts(sysUser.getId());
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("list", list);
            map.put("orgCode", sysUser.getOrgCode());
            result.setSuccess(true);
            result.setResult(map);
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.error500("查询失败！");
        }
        return result;
    }




    /**
     * 用户注册接口
     *
     * @param jsonObject
     * @param user
     * @return
     */
    @PostMapping("/register")
    public Result<JSONObject> userRegister(@RequestBody JSONObject jsonObject, SysUserEntity user) {
        Result<JSONObject> result = new Result<JSONObject>();
        String phone = jsonObject.getString("phone");
        String smscode = jsonObject.getString("smscode");
        Object code = redisUtil.get(phone);
        String username = jsonObject.getString("username");
        //未设置用户名，则用手机号作为用户名
        if(oConvertUtils.isEmpty(username)){
            username = phone;
        }
        //未设置密码，则随机生成一个密码
        String password = jsonObject.getString("password");
        if(oConvertUtils.isEmpty(password)){
            password = RandomUtil.randomString(8);
        }
        String email = jsonObject.getString("email");
        SysUserEntity sysUser1 = sysUserService.getUserByName(username);
        if (sysUser1 != null) {
            result.setMessage("用户名已注册");
            result.setSuccess(false);
            return result;
        }
        SysUserEntity sysUser2 = sysUserService.getUserByPhone(phone);
        if (sysUser2 != null) {
            result.setMessage("该手机号已注册");
            result.setSuccess(false);
            return result;
        }

        if(oConvertUtils.isNotEmpty(email)){
            SysUserEntity sysUser3 = sysUserService.getUserByEmail(email);
            if (sysUser3 != null) {
                result.setMessage("邮箱已被注册");
                result.setSuccess(false);
                return result;
            }
        }

        if (!smscode.equals(code)) {
            result.setMessage("手机验证码错误");
            result.setSuccess(false);
            return result;
        }

        try {
            String salt = oConvertUtils.randomGen(8);
            String passwordEncode = PasswordUtil.encrypt(username, password, salt);
            user.setSalt(salt);
            user.setUsername(username);
            user.setRealname(username);
            user.setPassword(passwordEncode);
            user.setEmail(email);
            user.setMobilephone(phone);
            user.setStatus(CommonConstant.USER_UNFREEZE);
            user.setDelFlag(CommonConstant.DEL_FLAG_0);
            sysUserService.addUserWithRole(user,"ee8626f80f7c2619917b6236f3a7f02b");//默认临时角色 test
            result.success("注册成功");
        } catch (Exception e) {
            result.error500("注册失败");
        }
        return result;
    }

    /**
     * 根据用户名或手机号查询用户信息
     * @param
     * @return
     */
    @GetMapping("/querySysUser")
    public Result<Map<String, Object>> querySysUser(SysUserEntity sysUser) {
        String phone = sysUser.getMobilephone();
        String username = sysUser.getUsername();
        Result<Map<String, Object>> result = new Result<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        if (oConvertUtils.isNotEmpty(phone)) {
            SysUserEntity user = sysUserService.getUserByPhone(phone);
            if(user!=null) {
                map.put("username",user.getUsername());
                map.put("phone",user.getMobilephone());
                result.setSuccess(true);
                result.setResult(map);
                return result;
            }
        }
        if (oConvertUtils.isNotEmpty(username)) {
            SysUserEntity user = sysUserService.getUserByName(username);
            if(user!=null) {
                map.put("username",user.getUsername());
                map.put("phone",user.getMobilephone());
                result.setSuccess(true);
                result.setResult(map);
                return result;
            }
        }
        result.setSuccess(false);
        result.setMessage("验证失败");
        return result;
    }

    /**
     * 用户手机号验证
     */
    @PostMapping("/phoneVerification")
    public Result<String> phoneVerification(@RequestBody JSONObject jsonObject) {
        Result<String> result = new Result<String>();
        String phone = jsonObject.getString("phone");
        String smscode = jsonObject.getString("smscode");
        Object code = redisUtil.get(phone);
        if (!smscode.equals(code)) {
            result.setMessage("手机验证码错误");
            result.setSuccess(false);
            return result;
        }
        redisUtil.set(phone, smscode);
        result.setResult(smscode);
        result.setSuccess(true);
        return result;
    }

    /**
     * 用户更改密码
     */
    @GetMapping("/passwordChange")
    public Result<SysUserEntity> passwordChange(@RequestParam(name="username")String username,
                                                @RequestParam(name="password")String password,
                                                @RequestParam(name="smscode")String smscode,
                                                @RequestParam(name="phone") String phone) {
        Result<SysUserEntity> result = new Result<SysUserEntity>();
        if(oConvertUtils.isEmpty(username) || oConvertUtils.isEmpty(password) || oConvertUtils.isEmpty(smscode)  || oConvertUtils.isEmpty(phone) ) {
            result.setMessage("重置密码失败！");
            result.setSuccess(false);
            return result;
        }

        SysUserEntity sysUser=new SysUserEntity();
        Object object= redisUtil.get(phone);
        if(null==object) {
            result.setMessage("短信验证码失效！");
            result.setSuccess(false);
            return result;
        }
        if(!smscode.equals(object)) {
            result.setMessage("短信验证码不匹配！");
            result.setSuccess(false);
            return result;
        }
        sysUser = this.sysUserService.getOne(new LambdaQueryWrapper<SysUserEntity>().eq(SysUserEntity::getUsername,username).eq(SysUserEntity::getMobilephone,phone));
        if (sysUser == null) {
            result.setMessage("未找到用户！");
            result.setSuccess(false);
            return result;
        } else {
            String salt = oConvertUtils.randomGen(8);
            sysUser.setSalt(salt);
            String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, salt);
            sysUser.setPassword(passwordEncode);
            this.sysUserService.updateById(sysUser);
            result.setSuccess(true);
            result.setMessage("密码重置完成！");
            return result;
        }
    }


    /**
     * 根据TOKEN获取用户的部分信息（返回的数据是可供表单设计器使用的数据）
     *
     * @return
     */
    @GetMapping("/getUserSectionInfoByToken")
    public Result<?> getUserSectionInfoByToken(HttpServletRequest request, @RequestParam(name = "token", required = false) String token) {
        try {
            String username = null;
            // 如果没有传递token，就从header中获取token并获取用户信息
            if (oConvertUtils.isEmpty(token)) {
                username = JwtUtil.getUserNameByToken(request);
            } else {
                username = JwtUtil.getUsername(token);
            }

            log.info(" ------ 通过令牌获取部分用户信息，当前用户： " + username);

            // 根据用户名查询用户信息
            SysUserEntity sysUser = sysUserService.getUserByName(username);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("sysUserId", sysUser.getId());
            map.put("sysUserCode", sysUser.getUsername()); // 当前登录用户登录账号
            map.put("sysUserName", sysUser.getRealname()); // 当前登录用户真实名称
//			map.put("sysOrgCode", sysUser.getOrgCode()); // 当前登录用户部门编号

            log.info(" ------ 通过令牌获取部分用户信息，已获取的用户信息： " + map);

            return Result.ok(map);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(500, "查询失败:" + e.getMessage());
        }
    }

    /**
     * 【APP端接口】获取用户列表  根据用户名和真实名 模糊匹配
     * @param keyword
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/appUserList")
    public Result<?> appUserList(@RequestParam(name = "keyword", required = false) String keyword,
                                 @RequestParam(name = "username", required = false) String username,
                                 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        try {
            //TODO 从查询效率上将不要用mp的封装的page分页查询 建议自己写分页语句
            LambdaQueryWrapper<SysUserEntity> query = new LambdaQueryWrapper<SysUserEntity>();
            query.eq(SysUserEntity::getDelFlag,"0");
            if(oConvertUtils.isNotEmpty(username)){
                query.eq(SysUserEntity::getUsername,username);
            }else{
                query.and(i -> i.like(SysUserEntity::getUsername, keyword).or().like(SysUserEntity::getRealname, keyword));
            }
            Page<SysUserEntity> page = new Page<>(pageNo, pageSize);
            IPage<SysUserEntity> res = this.sysUserService.page(page, query);
            return Result.ok(res);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(500, "查询失败:" + e.getMessage());
        }

    }

    /**
     * 获取被逻辑删除的用户列表，无分页
     *
     * @return logicDeletedUserList
     */
    @GetMapping("/recycleBin")
    public Result getRecycleBin() {
        List<SysUserEntity> logicDeletedUserList = sysUserService.queryLogicDeleted();
        if (logicDeletedUserList.size() > 0) {
            // 批量查询用户的所属部门
            // step.1 先拿到全部的 userIds
            List<String> userIds = logicDeletedUserList.stream().map(SysUserEntity::getId).collect(Collectors.toList());
            // step.2 通过 userIds，一次性查询用户的所属部门名字
            Map<String, String> useDepNames = sysUserService.getDepNamesByUserIds(userIds);
//            logicDeletedUserList.forEach(item -> item.setOrgCode(useDepNames.get(item.getId())));
        }
        return Result.ok(logicDeletedUserList);
    }

    /**
     * 还原被逻辑删除的用户
     *
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/putRecycleBin", method = RequestMethod.PUT)
    public Result putRecycleBin(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        String userIds = jsonObject.getString("userIds");
        if (StringUtils.isNotBlank(userIds)) {
            SysUserEntity updateUser = new SysUserEntity();
            sysUserService.revertLogicDeleted(Arrays.asList(userIds.split(",")), updateUser);
        }
        return Result.ok("还原成功");
    }

    /**
     * 彻底删除用户
     *
     * @param userIds 被删除的用户ID，多个id用半角逗号分割
     * @return
     */
    @RequestMapping(value = "/deleteRecycleBin", method = RequestMethod.DELETE)
    public Result deleteRecycleBin(@RequestParam("userIds") String userIds) {
        if (StringUtils.isNotBlank(userIds)) {
            sysUserService.removeLogicDeleted(Arrays.asList(userIds.split(",")));
        }
        return Result.ok("删除成功");
    }


    /**
     * 移动端修改用户信息
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/appEdit", method = RequestMethod.PUT)
    public Result<SysUserEntity> appEdit(@RequestBody JSONObject jsonObject) {
        Result<SysUserEntity> result = new Result<SysUserEntity>();
        try {
            SysUserEntity sysUser = sysUserService.getById(jsonObject.getString("id"));
            sysBaseAPI.addLog("移动端编辑用户，id： " +jsonObject.getString("id") ,CommonConstant.LOG_TYPE_2, 2);
            if(sysUser==null) {
                result.error500("未找到对应用户!");
            }else {
                SysUserEntity user = JSON.parseObject(jsonObject.toJSONString(), SysUserEntity.class);
                user.setPassword(sysUser.getPassword());
                sysUserService.updateById(user);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败!");
        }
        return result;
    }


    @GetMapping("/rolesSet/{username}")
    public Result<Object> getUserRolesSet(@PathVariable("username") String username){
        return Result.ok(sysUserService.getUserRolesSet(username));
    }

    @GetMapping("/permissionsSet/{username}")
    public Result<Object> getUserPermissionsSet(@PathVariable("username") String username){
        return Result.ok(sysUserService.getUserPermissionsSet(username));
    }

    @GetMapping(value = "/info/{username}")
    public Result<Object> info(@PathVariable("username") String username){
        SysUserEntity sysUser = sysUserService.getUserByName(username);
        LoginUser loginUser = new LoginUser();
        BeanUtils.copyProperties(sysUser,loginUser);
        return Result.ok(loginUser);
    }

    /**
     * 通过编码和存储的value查询字典text
     * @param code
     * @param key
     * @return
     */
    @GetMapping(value = "/queryDictTextByKey")
    public String queryDictTextByKey(@RequestParam(name="code") String code, @RequestParam(name="key") String key){
        return sysDictService.queryDictTextByKey(code, key);
    }

    /**
     * 通过编码和存储的value查询表字典的text
     * @param table 表名
     * @param text  表字段
     * @param code  表字段
     * @param key   表字段code的值
     * @return
     */
    @GetMapping(value = "/queryTableDictTextByKey")
    public String queryTableDictTextByKey(@RequestParam(name="table") String table,
                                          @RequestParam(name="text") String text,
                                          @RequestParam(name="code") String code,
                                          @RequestParam(name="key") String key){
        return sysDictService.queryTableDictTextByKey(table, text, code, key);
    }

    /**
     * 获取用户数据权限范围
     * @return
     */
    @GetMapping(value = "/getUserDataScope")
    public SysDataPermissionModel getUserDataScope(){
        return roleService.getUserDataScope();
    }

    /**
     * 通过请求地址查找菜单信息
     * @param method
     * @param path
     * @return
     */
    @GetMapping(value = "queryRequestPermission")
    public List<SysPermissionEntity> queryRequestPermission(@RequestParam(name="method") String method,
                                                            @RequestParam(name="path") String path){
        //1.直接通过前端请求地址查询菜单
        LambdaQueryWrapper<SysPermissionEntity> query = new LambdaQueryWrapper<SysPermissionEntity>();
        query.eq(SysPermissionEntity::getMenuType,2);
        query.eq(SysPermissionEntity::getDelFlag,0);
        query.eq(SysPermissionEntity::getUrl, path);
        List<SysPermissionEntity> currentSyspermission = sysPermissionService.list(query);
        //2.未找到 再通过自定义匹配URL 获取菜单
        if(currentSyspermission==null || currentSyspermission.size()==0) {
            //通过自定义URL匹配规则 获取菜单（实现通过菜单配置数据权限规则，实际上针对获取数据接口进行数据规则控制）
            String userMatchUrl = UrlMatchEnum.getMatchResultByUrl(path);
            LambdaQueryWrapper<SysPermissionEntity> queryQserMatch = new LambdaQueryWrapper<SysPermissionEntity>();
            queryQserMatch.eq(SysPermissionEntity::getMenuType, 1);
            queryQserMatch.eq(SysPermissionEntity::getDelFlag, 0);
            queryQserMatch.eq(SysPermissionEntity::getUrl, userMatchUrl);
            if(oConvertUtils.isNotEmpty(userMatchUrl)){
                currentSyspermission = sysPermissionService.list(queryQserMatch);
            }
        }
        //3.未找到 再通过正则匹配获取菜单
        if(currentSyspermission==null || currentSyspermission.size()==0) {
            //通过正则匹配权限配置
            String regUrl = getRegexpUrl(path);
            if(regUrl!=null) {
                currentSyspermission = sysPermissionService.list(new LambdaQueryWrapper<SysPermissionEntity>().eq(SysPermissionEntity::getMenuType,2).eq(SysPermissionEntity::getUrl, regUrl).eq(SysPermissionEntity::getDelFlag,0));
            }
        }
        return currentSyspermission;
    }

    /**
     * 根据username获取用户信息
     * @param username
     * @return
     */
    @GetMapping(value = "getCacheUser")
    public SysUserCacheInfo getCacheUser(@RequestParam(name="username") String username){
        return sysUserService.getCacheUser(username);
    }


    /**
     * 根据请求地址获取正则
     * @param url
     * @return
     */
    private String getRegexpUrl(String url) {
        List<String> list = sysPermissionService.queryPermissionUrlWithStar();
        if(list!=null && list.size()>0) {
            for (String p : list) {
                PathMatcher matcher = new AntPathMatcher();
                if(matcher.match(p, url)) {
                    return p;
                }
            }
        }
        return null;
    }

    /**
     * 查询用户id获取用户信息
     *
     * @param idList
     * @return
     */
    @RequestMapping(value = "/getUserList", method = RequestMethod.GET)
    public List<SysUserModel> getUserList(@RequestParam("idList") List<String> idList) {
        if(CollectionUtil.isNotEmpty(idList)){
            return sysUserService.getUserList(idList);
        }
        return null;

    }


    /**
     * 根据身份证查询用户
     */
    //@ApiOperation("根据身份证查询用户")
    @RequestMapping(value = "/getUserByIdcard", method = RequestMethod.GET)
    public SysUserModel getUserByIdcard(@RequestParam("idcard") String idcard) {
        if(StringUtil.isBlank(idcard)){
            log.error("身份证号参数无效");
            return null;
        }
        SysUserModel userByIdcard = sysUserService.getUserModelByIdcard(idcard);
        return userByIdcard;
    }

    @RequestMapping(value = "/getUserById", method = RequestMethod.GET)
    public SysUserModel getUserById(@RequestParam("id") String id) {
        if(StringUtil.isBlank(id)){
            log.error("身份证号参数无效");
            return null;
        }
        SysUserModel model = sysUserService.getUserModelById(id);
        return model;
    }

    @RequestMapping(value = "/getUserByZfzh", method = RequestMethod.GET)
    public SysUserModel getUserByZfzh(@RequestParam("zfzh") String zfzh) {
        if(StringUtil.isBlank(zfzh)){
            log.error("执法证号参数无效");
            return null;
        }
        SysUserModel model = sysUserService.getUserModelByZfzh(zfzh);
        return model;
    }


    /**
     * 查询用户下拉框数据接口
     *
     * @return
     */
    @ApiOperation("获取用户选择下拉框")
    @RequestMapping(value = "/getUserSelect", method = RequestMethod.GET)
    public List<SysUserModel> getUserSelect() {
        return sysUserService.getUserSelect();
    }

    /**
     * 获取用户数量
     * @return
     */
    @RequestMapping(value = "/getUserNum", method = RequestMethod.GET)
    public Integer getUserNum() {
        return sysUserService.count();
    }
}
