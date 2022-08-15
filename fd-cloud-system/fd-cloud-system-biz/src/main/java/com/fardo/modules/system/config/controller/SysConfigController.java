package com.fardo.modules.system.config.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fardo.common.api.vo.Result;
import com.fardo.common.api.vo.ResultCode;
import com.fardo.common.api.vo.ResultVo;
import com.fardo.common.aspect.annotation.RequestAop;
import com.fardo.modules.system.config.entity.SysConfigEntity;
import com.fardo.modules.system.config.model.SysConfigModel;
import com.fardo.modules.system.config.service.ISysClientConfigService;
import com.fardo.modules.system.config.service.ISysConfigService;
import com.fardo.modules.system.config.vo.*;
import com.fardo.modules.system.sys.vo.ParamVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/system/parameterConfig")
@Api(tags = "api-系统参数配置")
public class SysConfigController {


    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private ISysClientConfigService sysClientConfigService;

    @RequestAop(value = "系统参数配置-分页列表查询", clazz = SysConfigListVo.class)
    @ApiOperation(value = "系统参数配置-分页列表查询", notes = "系统参数配置-分页列表查询")
    @PostMapping(value = "/list")
    public ResultVo<IPage<SysConfigEntity>> queryPageList(ParamVo<SysConfigListVo> paramVo) {
        ResultVo<IPage<SysConfigEntity>> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        SysConfigListVo sysConfigVo = paramVo.getData();
        IPage<SysConfigEntity> modelIPage = sysConfigService.getPageList(sysConfigVo);
        resultVo.setResults(modelIPage);
        return resultVo;
    }

    @RequestAop(value = "保存系统参数(带id则更新)", clazz = SysConfigVo.class)
    @ApiOperation("保存系统参数(带id则更新)")
    @PostMapping(value = "/save")
    public ResultVo<String> add(ParamVo<SysConfigVo> paramVo) {
        ResultVo<String> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        String userId = paramVo.currentLoginUser().getId();
        SysConfigVo sysConfigVo = paramVo.getData();
        try {
            if (StringUtils.isBlank(sysConfigVo.getId())) {
                if (!sysConfigService.checkParameterConfig("", sysConfigVo.getCode())) {
                    resultVo.setResultCode(ResultCode.REPEAT);
                    resultVo.setResultMsg("配置项代码已存在");
                    return resultVo;
                }
                //查看配置项代码是否重复
                // Integer integer = sysConfigService.selectCount(sysConfigVo.getCode());
                // if (integer > 0){
                //     resultVo.setResultCode(ResultCode.REPEAT);
                //     resultVo.setResultMsg("配置项代码重复");
                //     return resultVo;
                // }
                sysConfigService.addParameterConfig(sysConfigVo,userId);
            } else {
                if (!sysConfigService.checkParameterConfig(sysConfigVo.getId(), sysConfigVo.getCode())) {
                    resultVo.setResultCode(ResultCode.REPEAT);
                    resultVo.setResultMsg("配置项代码已存在");
                    return resultVo;
                }
                //判断是否自己添加的-不允许编辑他人数据
                SysConfigEntity byId = sysConfigService.getById(sysConfigVo.getId());
                if (!ObjectUtils.isEmpty(byId) && StringUtils.isNotBlank(byId.getCreateBy())){
                    if (!userId.equals(byId.getCreateBy())){
                        resultVo.setResultCode(ResultCode.ADD_ERROR);
                        resultVo.setResultMsg("他人数据不允许编辑");
                        return resultVo;
                    }
                }

                //查看配置项代码是否重复
                Integer integer = sysConfigService.selectCount(sysConfigVo.getCode());
                if (integer > 1){
                    resultVo.setResultCode(ResultCode.REPEAT);
                    resultVo.setResultMsg("配置项代码重复");
                    return resultVo;
                }
                sysConfigService.updatParameterConfig(sysConfigVo,userId);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            resultVo.setResultCode(ResultCode.ADD_ERROR);
            resultVo.setResultMsg("操作失败");
        }
        return resultVo;
    }

    @ApiOperation("查看详情")
    @RequestAop(value = "查看详情", clazz = SysConfigLookVo.class)
    @PostMapping(value = "/getSysConfig")
    public ResultVo<SysConfigVo> getSysConfig(ParamVo<SysConfigLookVo> paramVo) {
        ResultVo<SysConfigVo> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        SysConfigLookVo sysConfigVo = paramVo.getData();
        String id = sysConfigVo.getId();
        if (StringUtils.isBlank(id)) {
            resultVo.setResultMsg("操作失败");
            return resultVo;
        }
        SysConfigVo vo = new SysConfigVo();
        SysConfigEntity byId = sysConfigService.getById(id);
        if (!ObjectUtils.isEmpty(byId)) {
            BeanUtils.copyProperties(byId, vo);
        }
        resultVo.setResults(vo);
        return resultVo;
    }


    @RequestAop(value = "根据代码获取配置", clazz = QueryConfigByCodeVo.class)
    @ApiOperation(value = "根据代码获取配置", notes = "根据代码获取配置（不走缓存）")
    @PostMapping(value = "/queryConfigByCode")
    public ResultVo<SysConfigVo> queryConfigByCode(ParamVo<QueryConfigByCodeVo> paramVo) {
        ResultVo<SysConfigVo> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        SysConfigVo vo = this.sysConfigService.queryConfigByCode(paramVo.getData());
        resultVo.setResults(vo);
        return resultVo;
    }


    @RequestAop(value = "根据代码修改配置", clazz = UpdateConfigByCodeVo.class)
    @ApiOperation(value = "根据代码修改配置", notes = "根据代码修改配置")
    @PostMapping(value = "/updateConfigByCode")
    public ResultVo updateConfigByCode(ParamVo<UpdateConfigByCodeVo> paramVo) {
        ResultVo resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        this.sysConfigService.updateConfigByCode(paramVo.getData());
        return resultVo;
    }

    /* @ApiOperation("修改系统参数")
     @RequestMapping(value = "/update", method = RequestMethod.POST)
     public Result<?> update(@Valid @RequestBody SysConfigVo sysConfigVo) {
         Result<?> result = Result.ok();
         try {
             if(StringUtils.isBlank(sysConfigVo.getId()) ||StringUtils.isBlank(sysConfigVo.getCode())||StringUtils.isBlank(sysConfigVo.getValue())){
                 return result.error500("参数缺失");
             }

         } catch (Exception e) {
             log.error(e.getMessage(), e);
             result.error500("操作失败");
         }
         return result;
     }
 */
    @ApiOperation("检验参数主键是否可用")
    @RequestMapping(value = "/checkParam", method = RequestMethod.GET)
    public Result<?> check(@ApiParam(value = "系统参数id,编辑时必传") @RequestParam(name = "id") String id, @ApiParam(value = "参数主键", required = true) @RequestParam(name = "key") String key) {
        Result<?> result = Result.ok();
        try {
            if (!sysConfigService.checkParameterConfig(id, key)) {
                result.error500("参数主键已存在");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500(e.getMessage());
        }
        return result;
    }

    @RequestAop(value = "删除系统参数", clazz = SysConfigLookVo.class)
    @ApiOperation("删除系统参数")
    @PostMapping(value = "/delete")
    public ResultVo<String> delete(ParamVo<SysConfigLookVo> paramVo) {
        ResultVo<String> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        String id = paramVo.getData().getId();
        String userId = paramVo.currentLoginUser().getId();
        try {
            if (StringUtils.isBlank(id)) {
                resultVo.setResultMsg("参数无效");
            }
            //判断是否自己添加的-不允许删除他人数据
            SysConfigEntity byId = sysConfigService.getById(id);
            if (!ObjectUtils.isEmpty(byId) && StringUtils.isNotBlank(byId.getCreateBy())){
                if (!userId.equals(byId.getCreateBy())){
                    resultVo.setResultCode(ResultCode.ADD_ERROR);
                    resultVo.setResultMsg("他人数据不允许删除");
                    return resultVo;
                }
            }
            sysConfigService.deleteSysConfig(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            resultVo.setResultMsg("操作失败");
        }
        return resultVo;
    }

    @ApiOperation("刷新缓存")
    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    public Result<?> refresh() {
        Result<?> result = Result.ok();
        try {
            sysConfigService.refresh();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }


    @ApiOperation("根据参数主键获取对应value")
    @RequestMapping(value = "/getSysParam", method = RequestMethod.GET)
    public String getSysParam(@ApiParam(value = "系统参数code", required = true) @RequestParam(name = "key") String key) {
        return sysConfigService.getSysParam(key);
    }

    @RequestMapping(value = "/refreshClientConfig", method = RequestMethod.GET)
    public void refreshClientConfig(@ApiParam(value = "参数key", required = true) @RequestParam(name = "configId") String configId) {
        this.sysClientConfigService.refreshClientConfig(configId);
    }

}
