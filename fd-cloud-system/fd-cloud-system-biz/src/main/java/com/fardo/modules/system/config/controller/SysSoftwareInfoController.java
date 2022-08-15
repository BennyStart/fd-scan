package com.fardo.modules.system.config.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fardo.common.api.vo.Result;
import com.fardo.modules.system.config.entity.SysSoftwareUpgradeStrategyEntity;
import com.fardo.modules.system.config.model.SysSoftwareInfoModel;
import com.fardo.modules.system.config.service.ISysSoftwareInfoService;
import com.fardo.modules.system.config.vo.SysSoftwareInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.utils.UuidUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/sys/softwareInfo")
@Api(tags="软件下载配置")
public class SysSoftwareInfoController {

    @Autowired
    private ISysSoftwareInfoService softeareInfoService;

    @Value("${project.version}")
    private String serverVersion;

    @ApiOperation(value="软件下载配置-分页列表查询", notes="软件下载配置-分页列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public  Result<IPage<SysSoftwareInfoModel>> queryPageList(SysSoftwareInfoVo sysSoftwareInfoVo) {
        Result<IPage<SysSoftwareInfoModel>> result = new Result<>();//结果
        IPage<SysSoftwareInfoModel> pageList = softeareInfoService.getPageList(sysSoftwareInfoVo);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @ApiOperation(value="保存软件下载配置(带id则更新)")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public  Result<?> add(@Valid @RequestBody SysSoftwareInfoVo sysSoftwareInfoVo) {
        Result<?> result = Result.ok();
        try {
            if(StringUtils.isBlank(sysSoftwareInfoVo.getId())){
                softeareInfoService.addSysSoftwareInfo(sysSoftwareInfoVo);
            }else {
                softeareInfoService.updateSysSoftwareInfo(sysSoftwareInfoVo);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }



    @ApiOperation(value="删除软件下载配置", notes="删除软件下载配置")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public  Result<?> delete(@ApiParam(value = "软件下载配置id",required=true) @RequestParam(name="id") String id) {
        Result<?> result = Result.ok();
        try {
            if(StringUtils.isBlank(id)){
                result.error500("参数无效");
            }
            softeareInfoService.deleteSoftwareInfo(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }


}
