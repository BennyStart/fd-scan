package com.fardo.modules.system.log.api;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fardo.common.api.vo.ResultCode;
import com.fardo.common.api.vo.ResultVo;
import com.fardo.common.aspect.annotation.RequestAop;
import com.fardo.common.enums.OperTypeEnum;
import com.fardo.common.exception.ApiException;
import com.fardo.common.util.DateUtils;
import com.fardo.common.util.StringUtil;
import com.fardo.modules.shiro.authc.LoginUtil;
import com.fardo.modules.system.log.entity.SysUserLogEntity;
import com.fardo.modules.system.log.model.*;
import com.fardo.modules.system.log.service.ISysUserLogService;
import com.fardo.modules.system.log.vo.LogIdVo;
import com.fardo.modules.system.log.vo.LogQueryVo;
import com.fardo.modules.system.log.vo.UserInfoLogVo;
import com.fardo.modules.system.log.vo.UserLogExportVo;
import com.fardo.modules.system.sys.vo.LoginUserVo;
import com.fardo.modules.system.sys.vo.ParamVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @(#)ApiUserLogController <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：wangbt
 * 时　间：2021/9/18 13:56
 * 描　述：
 */
@Slf4j
@Api(tags = "api-日志管理接口")
@RestController
@RequestMapping("/api/system/log")
public class ApiUserLogController {

    @Autowired
    private ISysUserLogService userLogService;

    @RequestAop(value = "分页获取日志列表", clazz = LogQueryVo.class)
    @ApiOperation("分页获取日志列表")
    @PostMapping(value = "/pageList")
    public ResultVo<IPage<LogGridModel>> pageList(ParamVo<LogQueryVo> paramVo) {
        ResultVo<IPage<LogGridModel>> resultVo = new ResultVo<>();
        resultVo.setResultCode(ResultCode.SUCCESS);
        IPage<LogGridModel> page = this.userLogService.pageList(paramVo.getData());
        resultVo.setResults(page);
        return resultVo;
    }

    @RequestAop(value = "获取日志详情", clazz = LogIdVo.class)
    @ApiOperation("获取日志详情")
    @PostMapping(value = "/detail")
    public ResultVo<LogDetailModel> detail(ParamVo<LogIdVo> paramVo) {
        ResultVo<LogDetailModel> resultVo = new ResultVo<>();
        resultVo.setResultCode(ResultCode.SUCCESS);
        LogDetailModel model = this.userLogService.detail(paramVo.getData());
        resultVo.setResults(model);
        return resultVo;
    }


    @RequestAop(value = "获取日志管理下拉框")
    @ApiOperation(value = "获取日志管理下拉框", notes = "获取日志管理下拉框")
    @PostMapping(value = "/getComboBox")
    public ResultVo<LogComboModel> queryPage(ParamVo paramVo) {
        ResultVo<LogComboModel> result = new ResultVo<>();
        result.setResultCode(ResultCode.SUCCESS);
        LogComboModel logComboModel = new LogComboModel();
        List<ComboBoxModel> operTypes = new ArrayList<>();
        List<ComboBoxModel> operModules = new ArrayList<>();
        List<String> modules = new ArrayList<>();
        for (OperTypeEnum value : OperTypeEnum.values()) {
            if(StringUtil.isBlank(value.getOperType())) {
                continue;
            }
            ComboBoxModel typeModel = new ComboBoxModel(value.getOperType(), value.getOperName());
            operTypes.add(typeModel);
            if(!modules.contains(value.getOperModule())) {
                ComboBoxModel moduleModel = new ComboBoxModel(value.getOperModule(), value.getOperModule());
                operModules.add(moduleModel);
                modules.add(value.getOperModule());
            }
        }
        logComboModel.setOperTypes(operTypes);
        logComboModel.setOperModule(operModules);
        result.setResults(logComboModel);
        return result;
    }


    @RequestAop(value = "导出日志", clazz = LogQueryVo.class)
    @ApiOperation("导出日志")
    @PostMapping(value = "/export")
    public ModelAndView export(ParamVo<LogQueryVo> paramVo, HttpServletRequest request) {
        LogQueryVo vo = paramVo.getData();
        if(StringUtil.isBlank(vo.getStartTime())) {
            throw new ApiException(ResultCode.INVALIDPARAMETER.getResultCode(), "开始时间不能为空");
        }
        if(StringUtil.isBlank(vo.getEndTime())) {
            throw new ApiException(ResultCode.INVALIDPARAMETER.getResultCode(), "结束时间不能为空");
        }
        int day = DateUtils.diffDays(DateUtils.str2Date(vo.getStartTime(), DateUtils.yyyymmddhhmmss.get()),
                DateUtils.str2Date(vo.getEndTime(), DateUtils.yyyymmddhhmmss.get()));
        if(day > 30) {
            throw new ApiException(ResultCode.INVALIDPARAMETER.getResultCode(), "只能导出30天的数据");
        }
        List<SysUserLogEntity> list = this.userLogService.list(paramVo.getData());
        List<UserLogExportVo> data = new ArrayList<>();
        list.forEach(entity -> {
            UserLogExportVo exportVo = new UserLogExportVo();
            if(entity.getUserInfo() != null) {
                UserInfoLogVo userInfo = JSONObject.parseObject(entity.getUserInfo(), UserInfoLogVo.class);
                exportVo.setUsername(userInfo.getUsername());
                exportVo.setXm(userInfo.getXm());
                exportVo.setPolieNo(userInfo.getPoliceNo());
                exportVo.setIdCard(userInfo.getIdCard());
                exportVo.setDepartCode(userInfo.getDepartCode());
                exportVo.setDepartName(userInfo.getDepartNmae());
            }
            exportVo.setOperResult(entity.getOperResult());
            exportVo.setOperType(entity.getOperType());
            exportVo.setOperDesc(entity.getOperDesc());
            exportVo.setData1(entity.getData1());
            exportVo.setCreateTime(entity.getCreateTime());
            exportVo.setRequestIp(entity.getRequestIp());
            exportVo.setErrorCode(entity.getErrorCode());
            exportVo.setErrorMsg(entity.getErrorMsg());
            exportVo.setOperName(entity.getOperName());
            exportVo.setRequestParam(entity.getRequestParam());
            exportVo.setCostTime(entity.getCostTime());
            exportVo.setVersion(entity.getVersion());
            if(entity.getData2() != null) {
                AnShenLogInfo anShenLogInfo = JSONObject.parseObject(entity.getData2(), AnShenLogInfo.class);
                exportVo.setOperateType(anShenLogInfo.getOperateType());
                exportVo.setResourceType(anShenLogInfo.getResourceType());
                exportVo.setResourceName(anShenLogInfo.getResourceName());
            }
            data.add(exportVo);
        });
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        LoginUserVo userVo = LoginUtil.getLoginUser();
        mv.addObject(NormalExcelConstants.FILE_NAME, "日志数据，导出人:"+userVo.getRealname());
        mv.addObject(NormalExcelConstants.CLASS, UserLogExportVo.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("用户操作日志记录",  "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, data);
        return mv;
    }


}
