package com.fardo.modules.system.service;

import com.alibaba.fastjson.JSON;
import com.fardo.modules.system.user.enums.PermissionEnum;
import com.fardo.modules.system.user.service.ISysUserRoleService;
import com.fardo.modules.system.user.vo.DataScopeVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @(#)SysUserRoleServiceTest <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/4/15 15:49
 * 描　述：
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class SysUserRoleServiceTest {
    @Autowired
    private ISysUserRoleService userRoleService;

    @Test
    public void getDataRange() {
        DataScopeVo dataScopeVo = userRoleService.getDataScope("1381911464658460674", PermissionEnum.detaineesManager);
        log.info(JSON.toJSONString(dataScopeVo));
    }
}
