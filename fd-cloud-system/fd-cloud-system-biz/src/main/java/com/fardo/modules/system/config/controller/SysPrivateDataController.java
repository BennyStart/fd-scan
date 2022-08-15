package com.fardo.modules.system.config.controller;

import com.fardo.modules.system.config.entity.SysPrivateDataEntity;
import com.fardo.modules.system.config.service.ISysPrivateDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/sys/privateData")
@Api(tags="系统私有数据")
public class SysPrivateDataController {


    @Autowired
    private ISysPrivateDataService sysPrivateDataService;

    @ApiOperation("获取系统私有数据")
    @RequestMapping(value = "/getById/{id}", method = RequestMethod.GET)
    public SysPrivateDataEntity getById(@PathVariable("id") String id) {
        return sysPrivateDataService.getById(id);
    }

    @ApiOperation("保存系统私有数据")
    @RequestMapping(value = "/save/{id}/{data}", method = RequestMethod.GET)
    public void save(@PathVariable("id") String id, @PathVariable("data") String data) {
        SysPrivateDataEntity entity = new SysPrivateDataEntity(id, data);
        this.sysPrivateDataService.saveOrUpdate(entity);
    }


}
