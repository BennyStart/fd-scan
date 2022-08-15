package com.fardo.modules.system.log.controller;

import com.fardo.modules.system.log.entity.SysUserLogEntity;
import com.fardo.modules.system.log.service.ISysUserLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @(#)SysUserLogController <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：wangbt
 * 时　间：2021/7/22 9:34
 * 描　述：
 */
@RestController
@RequestMapping("/sys/user/log")
@Slf4j
public class SysUserLogController {

    @Autowired
    private ISysUserLogService userLogService;

    /**
     * 保存日志
     * @param entity
     * @return
     */
    @PostMapping("/save")
    public void save(@RequestBody SysUserLogEntity entity) {
        userLogService.save(entity);
    }


    /**
     * 获取登录次数
     * @return
     */
    @PostMapping("/getUserDlcs")
    public Integer getUserDlcs() {
        return userLogService.getUserDlcs();
    }

}
