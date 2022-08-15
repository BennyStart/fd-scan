package com.fardo.modules.system.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fardo.common.api.vo.Result;
import com.fardo.modules.system.user.entity.SysUserEntity;
import com.fardo.modules.system.user.model.SysUserModel;
import com.fardo.modules.system.user.service.ISysUserService;
import com.fardo.modules.system.user.vo.SysUserParamVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @(#)SysUserServiceTest <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/9 17:35
 * 描　述：
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class SysUserServiceTest {

    @Autowired
    private ISysUserService sysUserService;

    @Test
    public void testGetUserByName() {
        sysUserService.getUserByName("admin");
    }

    @Test
    public void testResetPassword() {
        Result result = sysUserService.resetPassword("admin", "123456","123456","123456");
        log.info(JSON.toJSONString(result));
    }
    @Test
    public void getPageModelList() {
        SysUserParamVo sysUserParamVo = new SysUserParamVo();
        IPage<SysUserModel> page = sysUserService.getPageModelList("",sysUserParamVo);
        log.info(JSON.toJSONString(page));
    }

    @Test
    public void initRealnamePy(){
        sysUserService.initRealnamePy();
    }
}
