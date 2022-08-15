package com.fardo.modules.system.config.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fardo.common.api.vo.Result;
import com.fardo.common.constant.CommonConstant;
import com.fardo.modules.system.config.entity.SysSoftwareUpgradeStrategyEntity;
import com.fardo.modules.system.config.model.SysSoftwareUpgradeStrategyModel;
import com.fardo.modules.system.config.service.ISysSoftwareUpgradeStrategyService;
import com.fardo.modules.system.config.vo.SysSoftwareUpgradeStrategyVo;
import com.fardo.modules.system.user.entity.SysUserEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/sys/softwareUpgradeStrategy")
@Api(tags="升级策略配置")
public class SysSoftwareUpgradeStrategyController {

    @Autowired
    private ISysSoftwareUpgradeStrategyService softwareUpgradeStrategyService;

    @ApiOperation(value="升级策略配置-分页列表查询")
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public Result<IPage<SysSoftwareUpgradeStrategyModel>> queryPageList(SysSoftwareUpgradeStrategyVo softwareUpgradeStrategyVo) {
        Result<IPage<SysSoftwareUpgradeStrategyModel>> result = new Result<>();//结果
        IPage<SysSoftwareUpgradeStrategyModel> modelIPage = softwareUpgradeStrategyService.getPageList(softwareUpgradeStrategyVo);
        result.setSuccess(true);
        result.setResult(modelIPage);
        return result;
    }

    @ApiOperation("保存升级策略(带id则更新)")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Result<?> add(@Valid @RequestBody SysSoftwareUpgradeStrategyVo softwareUpgradeStrategyVo) {
        Result<?> result = Result.ok();
        try {
            if(StringUtils.isBlank(softwareUpgradeStrategyVo.getId())){
                SysSoftwareUpgradeStrategyEntity entity = new SysSoftwareUpgradeStrategyEntity();
                BeanUtils.copyProperties(softwareUpgradeStrategyVo, entity);
                softwareUpgradeStrategyService.save(entity);
            }else {
                softwareUpgradeStrategyService.updateUpgradeStrategy(softwareUpgradeStrategyVo);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }


    @ApiOperation("删除升级策略")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<?> delete(@ApiParam(value = "升级策略id",required = true) @RequestParam(name="id") String id) {
        Result<?> result = Result.ok();
        try {
            if(StringUtils.isBlank(id)){
                result.error500("参数无效");
            }
            softwareUpgradeStrategyService.removeById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }



}
