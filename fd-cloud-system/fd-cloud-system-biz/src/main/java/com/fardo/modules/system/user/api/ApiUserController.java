package com.fardo.modules.system.user.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fardo.common.api.vo.ResultCode;
import com.fardo.common.api.vo.ResultVo;
import com.fardo.common.aspect.annotation.RequestAop;
import com.fardo.common.enums.OperTypeEnum;
import com.fardo.common.util.FileUtil;
import com.fardo.common.util.RedisUtil;
import com.fardo.common.util.StringUtil;
import com.fardo.modules.api.SysBaseRemoteApi;
import com.fardo.modules.system.constant.CacheKeyConstants;
import com.fardo.modules.system.constant.SysConstants;
import com.fardo.modules.system.depart.vo.SysDepartIdParamVo;
import com.fardo.modules.system.role.model.SysDataPermissionModel;
import com.fardo.modules.system.role.vo.SysUserRolesIdVo;
import com.fardo.modules.system.sys.service.ApiLoginService;
import com.fardo.modules.system.sys.vo.LoginResult;
import com.fardo.modules.system.sys.vo.ParamVo;
import com.fardo.modules.system.user.model.SysDepartUserModel;
import com.fardo.modules.system.user.model.SysUserModel;
import com.fardo.modules.system.user.model.SysUserSiteModel;
import com.fardo.modules.system.user.service.ISysUserService;
import com.fardo.modules.system.user.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @(#)ApiUserController <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/5/11 19:26
 * 描　述：
 */
@Slf4j
@Api(tags = "api-用户管理接口")
@RestController
@RequestMapping("/api/system/user")
public class ApiUserController {

    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private SysBaseRemoteApi sysBaseRemoteApi;
    @Autowired
    private ApiLoginService apiLoginService;
    @Autowired
    private RedisUtil redisUtil;

    @RequestAop(value = "根据机构id获取用户及下级机构", clazz = SysDepartIdParamVo.class)
    @ApiOperation("根据机构id获取用户及下级机构")
    @PostMapping(value = "/departUserList")
    public ResultVo<List<SysDepartUserModel>> departUserList(ParamVo<SysDepartIdParamVo> paramVo) {
        ResultVo<List<SysDepartUserModel>> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        List<SysDepartUserModel> list = sysUserService.getDepartUserList(paramVo.currentLoginUser().getId(),paramVo.getData().getId());
        resultVo.setResults(list);
        return resultVo;
    }

    @RequestAop(value = "分页获取用户列表", clazz = SysUserParamVo.class)
    @ApiOperation("分页获取用户列表")
    @PostMapping(value = "/page")
    public ResultVo<IPage<SysUserModel>> page(ParamVo<SysUserParamVo> paramVo) {
        ResultVo<IPage<SysUserModel>> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        IPage<SysUserModel> modelIPage = sysUserService.getPageModelList(paramVo.currentLoginUser().getId(),paramVo.getData());
        resultVo.setResults(modelIPage);
        return resultVo;
    }

    @RequestAop(value = "查看用户信息", clazz = SysUserIdParamVo.class)
    @ApiOperation("查看用户信息")
    @PostMapping(value = "/get")
    public ResultVo<SysUserVo> get(ParamVo<SysUserIdParamVo> paramVo) {
        ResultVo<SysUserVo> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        SysUserVo sysUserVo = sysUserService.getUserDetail(paramVo.getData().getId());
        resultVo.setResults(sysUserVo);
        return resultVo;
    }

    @RequestAop(value = "新增用户", clazz = SysUserVo.class, operType = OperTypeEnum.XZYH)
    @ApiOperation("新增用户")
    @PostMapping(value = "/add")
    public ResultVo<SysUserIdParamVo> add(ParamVo<SysUserVo> paramVo, HttpServletRequest request) {
        SysUserVo sysUserVo = paramVo.getData();
        String operDesc =String.format("（用户名：%s）", sysUserVo.getUsername());
        request.setAttribute(SysConstants.LOG_OPER_DESC, operDesc);
        request.setAttribute(SysConstants.LOG_RESULT_DATA, operDesc);
        return sysUserService.saveUser(sysUserVo);
    }

    @RequestAop(value = "修改用户", clazz = SysUserUpdateVo.class, operType = OperTypeEnum.XGYH)
    @ApiOperation("修改用户")
    @PostMapping(value = "/edit")
    public ResultVo<SysUserIdParamVo> edit(ParamVo<SysUserUpdateVo> paramVo) {
        return sysUserService.updateUser(paramVo.getData());
    }

    @RequestAop(value = "删除用户", clazz = SysUserIdParamVo.class, operType = OperTypeEnum.SCYH)
    @ApiOperation("删除用户")
    @PostMapping(value = "/delete")
    public ResultVo delete(ParamVo<SysUserIdParamVo> paramVo) {
        return this.sysUserService.logicDeleteUser(paramVo.getData().getId());
    }

    @RequestAop(value = "密码重置", clazz = SysUserPasswordResetVo.class)
    @ApiOperation("密码重置")
    @PostMapping(value = "/resetPwd")
    public ResultVo<?> resetPwd(ParamVo<SysUserPasswordResetVo> paramVo) {
        sysUserService.resetPassword(paramVo.getData());
        return ResultVo.getResultVo(ResultCode.SUCCESS);
    }


    @RequestAop(value = "获取授权/移交选择用户列表", clazz = SysUserLikeVo.class)
    @ApiOperation(value = "获取授权/移交选择用户列表", notes = "获取授权/移交选择用户列表，当前登录用户不查询出来")
    @PostMapping(value = "/queryUserForDepart")
    public ResultVo<List<SysUserModel>> queryUserForDepart(ParamVo<SysUserLikeVo> paramVo) {
        ResultVo<List<SysUserModel>> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        List<SysUserModel> list = sysUserService.queryUserForDepartNoCurrentUser(paramVo.getData());
        resultVo.setResults(list);
        return resultVo;
    }

    @RequestAop(value = "切换用户角色", clazz = SysUserRolesIdVo.class)
    @ApiOperation(value = "切换用户角色", notes = "切换用户角色")
    @PostMapping(value = "/checkUserRole")
    public ResultVo<LoginResult> checkUserRole(ParamVo<SysUserRolesIdVo> paramVo) {
        if(StringUtil.isEmpty(paramVo.getData().getId())) {
            return ResultVo.getResultVo(ResultCode.INVALIDPARAMETER);
        }
        ResultVo<LoginResult> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        LoginResult loginResult = apiLoginService.checkRole(paramVo.getData().getId());
        resultVo.setResults(loginResult);
        return resultVo;
    }

    @RequestAop(value = "获取用户所在站点下的用户数据")
    @ApiOperation(value = "获取用户所在站点下的用户数据", notes = "获取用户所在站点下的用户数据")
    @PostMapping(value = "/loadUserListForUserSite")
    public ResultVo<List<SysUserSiteModel>> loadUserListForUserSite(ParamVo paramVo) {
        ResultVo<List<SysUserSiteModel>> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        List<SysUserSiteModel> list = sysUserService.loadUserListForUserSite();
        resultVo.setResults(list);
        return resultVo;
    }

    @RequestAop(value = "导入模板下载")
    @ApiOperation(value = "导入模板下载", notes = "导入模板下载")
    @PostMapping(value = "/downloadTpl")
    public void downloadTpl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("multipart/form-data");
        String userAgent = request.getHeader("User-Agent");
        String formFileName = "导入用户模板.xls";
        // 针对IE或者以IE为内核的浏览器：
        if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            formFileName = java.net.URLEncoder.encode(formFileName, StandardCharsets.UTF_8.name());
        } else {
            // 非IE浏览器的处理：
            formFileName = new String(formFileName.getBytes(StandardCharsets.UTF_8.name()),  StandardCharsets.ISO_8859_1.name());
        }
        response.setHeader("Content-disposition", String.format("attachment; filename=\"%s\"", formFileName));
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        InputStream is = this.getClass().getResourceAsStream("/templates/导入用户模板.xls");
        IOUtils.write(IOUtils.toByteArray(is), response.getOutputStream());
    }

    @RequestAop(value = "导入用户excel")
    @ApiOperation(value = "导入用户excel", notes = "导入用户excel")
    @PostMapping(value = "/importExcel")
    public ResultVo importExcel(ParamVo paramVo, HttpServletRequest request, HttpServletResponse response) {
        List<MultipartFile> files = FileUtil.getUploadFiles(request);
        if(CollectionUtils.isEmpty(files)) {
            return ResultVo.getResultVo("101","文件不能为空");
        }
        ResultVo resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        MultipartFile file = files.get(0);
        String ext = FileUtil.getExtend(file.getOriginalFilename());
        if(!"xlsx".contains(ext) ) {
            return ResultVo.getResultVo("101","请导入excel文件");
        }
        if(redisUtil.hasKey(CacheKeyConstants.USER_IMP_TASK_START)) {
            return ResultVo.getResultVo("102","已有其他用户导入任务正在进行，请稍后再试");
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(0);
        params.setHeadRows(1);
        try {
            List<SysUserExcelVo> listSysUsers = ExcelImportUtil.importExcel(file.getInputStream(), SysUserExcelVo.class, params);
            if(listSysUsers.size() > 10000) {
                return ResultVo.getResultVo("104","每次只能导入最多1万条记录");
            }
            sysUserService.importExcel(listSysUsers);
        } catch (Exception e) {
            if(e instanceof  RuntimeException) {
                resultVo = ResultVo.getResultVo("103",e.getMessage());
            }else{
                resultVo = ResultVo.getResultVo("103","导入失败");
            }
            log.error(e.getMessage(), e);
        } finally {
            try {
                file.getInputStream().close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return resultVo;
    }
}
