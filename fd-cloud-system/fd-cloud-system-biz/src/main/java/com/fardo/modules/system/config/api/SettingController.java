package com.fardo.modules.system.config.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fardo.common.api.vo.ResultCode;
import com.fardo.common.api.vo.ResultVo;
import com.fardo.common.aspect.annotation.RequestAop;
import com.fardo.modules.system.config.model.SysSettingQueryModel;
import com.fardo.modules.system.config.service.ISysSettingService;
import com.fardo.modules.system.config.vo.SysSettingDeleteVo;
import com.fardo.modules.system.config.vo.SysSettingQueryVo;
import com.fardo.modules.system.config.vo.SysSettingSaveVo;
import com.fardo.modules.system.sys.vo.ParamVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "api-客户端配置相关接口")
@RestController
@RequestMapping("/api/system/setting")
public class SettingController {

    @Autowired
    private ISysSettingService sysSettingService;


    @RequestAop(value = "分页查询客户端配置", clazz = SysSettingQueryVo.class)
    @ApiOperation(value = "分页查询客户端配置", notes = "分页查询客户端配置")
    @PostMapping(value = "/queryPage")
    public ResultVo<IPage<SysSettingQueryModel>> queryPage(ParamVo<SysSettingQueryVo> paramVo) {
        ResultVo<IPage<SysSettingQueryModel>> result = new ResultVo<>();
        result.setResultCode(ResultCode.SUCCESS);
        IPage<SysSettingQueryModel> modelIPage = this.sysSettingService.queryPage(paramVo.getData());
        result.setResults(modelIPage);
        return result;
    }

    @RequestAop(value = "保存客户端配置", clazz = SysSettingSaveVo.class)
    @ApiOperation(value = "保存客户端配置", notes = "保存客户端配置")
    @PostMapping(value = "/save")
    public ResultVo save(ParamVo<SysSettingSaveVo> paramVo) {
        ResultVo result = new ResultVo<>();
        result.setResultCode(ResultCode.SUCCESS);
        this.sysSettingService.save(paramVo.getData());
        return result;
    }

    @RequestAop(value = "删除客户端配置", clazz = SysSettingDeleteVo.class)
    @ApiOperation(value = "删除客户端配置", notes = "删除客户端配置")
    @PostMapping(value = "/delete")
    public ResultVo delete(ParamVo<SysSettingDeleteVo> paramVo) {
        ResultVo result = new ResultVo<>();
        result.setResultCode(ResultCode.SUCCESS);
        this.sysSettingService.delete(paramVo.getData());
        return result;
    }

}
