package com.fardo.modules.system.area.controller;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fardo.common.api.vo.Result;
import com.fardo.modules.system.area.entity.SysAreaEntity;
import com.fardo.modules.system.area.service.ISysAreaService;
import com.fardo.modules.system.area.vo.QuerySpecifiedLevelAreaDataVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(tags = "地区相关接口")
@RestController
@RequestMapping("/sys/area")
public class SysAreaController {

    @Autowired
    ISysAreaService sysAreaService;

    /**
     * 行政区域数据
     *
     * @return
     */
    @RequestMapping(value = "/areaList", method = RequestMethod.GET)
    @ApiOperation(value = "行政区域数据列表")
    public Result<IPage<SysAreaEntity>> getAreaList(@ApiParam(name = "parentAreaId", value = "获取省级传-1,获取市级传省级的id,获取区/县传市级id") @RequestParam(name = "parentAreaId", required = true) String parentAreaId) {
        if (StringUtil.isBlank(parentAreaId)) {
            return new Result().error500("请选择行政区域节点!");
        }
        Result<IPage<SysAreaEntity>> result = new Result<>();
        IPage<SysAreaEntity> modelIPage = new Page<>();
        List<SysAreaEntity> areaInfoList = sysAreaService.findByParentAreaCode(parentAreaId);
        modelIPage.setRecords(areaInfoList);
        result.setSuccess(true);
        result.setResult(modelIPage);
        return result;
    }

    /**
     * 获取区域
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据id获取区域", notes = "根据id获取区域")
    @RequestMapping(value = "/getArea/{id}", method = RequestMethod.GET)
    public String getArea(@PathVariable("id") String id) {
        log.info(" id : " + id);
        SysAreaEntity sysAreaEntity = sysAreaService.getSysAreaEntity(id);
        if (ObjectUtils.isEmpty(sysAreaEntity)) {
            return null;
        }
        return sysAreaEntity.getName();
    }

    @ApiOperation(value = "通过区域编号查询指定等级区域")
    @PostMapping("getSpecifiedLevelListByAreaCode")
    public List<SysAreaEntity> getSpecifiedLevelListByAreaCode(@RequestBody QuerySpecifiedLevelAreaDataVo vo) {
        return sysAreaService.getSpecifiedLevelListByAreaCode(vo);
    }

}
