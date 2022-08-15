package com.fardo.modules.system.security.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fardo.common.api.vo.ResultCode;
import com.fardo.common.api.vo.ResultVo;
import com.fardo.common.aspect.annotation.RequestAop;
import com.fardo.common.system.vo.PageVo;
import com.fardo.modules.system.security.entity.SysApiSecretEntity;
import com.fardo.modules.system.security.model.ApiSecretPageModel;
import com.fardo.modules.system.security.service.ISysApiSecretService;
import com.fardo.modules.system.security.vo.ApiSecretIdsVo;
import com.fardo.modules.system.security.vo.ApiSecretSaveVo;
import com.fardo.modules.system.sys.vo.ParamVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "api-接入应用")
@RestController
@RequestMapping("/api/system/secret")
public class ApiSecretController {

    @Autowired
    private ISysApiSecretService sysApiSecretService;


    @RequestAop(value = "分页查询接入应用", clazz = PageVo.class)
    @ApiOperation(value = "分页查询接入应用", notes = "分页查询接入应用")
    @PostMapping(value = "/queryPage")
    public ResultVo<IPage<ApiSecretPageModel>> queryPage(ParamVo<PageVo> paramVo) {
        ResultVo<IPage<ApiSecretPageModel>> result = new ResultVo<>();
        result.setResultCode(ResultCode.SUCCESS);
        IPage<ApiSecretPageModel> modelIPage = this.sysApiSecretService.queryPage(paramVo.getData());
        result.setResults(modelIPage);
        return result;
    }

    @RequestAop(value = "保存接入应用", clazz = ApiSecretSaveVo.class)
    @ApiOperation(value = "保存接入应用", notes = "保存接入应用")
    @PostMapping(value = "/save")
    public ResultVo save(ParamVo<ApiSecretSaveVo> paramVo) {
        ResultVo result = new ResultVo<>();
        result.setResultCode(ResultCode.SUCCESS);
        this.sysApiSecretService.save(paramVo.getData());
        return result;
    }

    @RequestAop(value = "删除接入应用", clazz = ApiSecretIdsVo.class)
    @ApiOperation(value = "删除接入应用", notes = "删除接入应用")
    @PostMapping(value = "/delete")
    public ResultVo delete(ParamVo<ApiSecretIdsVo> paramVo) {
        ResultVo result = new ResultVo<>();
        result.setResultCode(ResultCode.SUCCESS);
        this.sysApiSecretService.delete(paramVo.getData());
        return result;
    }

    @GetMapping("/get")
    public SysApiSecretEntity getApiSecretByKey(@RequestParam("apiKey") String apiKey){
        SysApiSecretEntity secretEntity = sysApiSecretService.query().eq("apiKey", apiKey).one();
        return secretEntity;
    }

}
