package com.fardo.modules.system.personal.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fardo.modules.system.personal.entity.BlQualitysetUserEntity;
import com.fardo.modules.system.personal.service.BlQualitysetUserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/sys/blQualitysetUser")
@Api(tags="个人中心文书校验")
public class BlQualitysetUserController {


    @Autowired
    private BlQualitysetUserService blQualitysetUserService;

    @RequestMapping(value = "/getByUserId", method = RequestMethod.GET)
    public BlQualitysetUserEntity getById(@RequestParam("id") String id) {
        LambdaQueryWrapper<BlQualitysetUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BlQualitysetUserEntity::getUserId, id);
        return blQualitysetUserService.getOne(queryWrapper, false);
    }



}
