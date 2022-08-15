package com.fardo.modules.system.sys.vo;

import com.alibaba.fastjson.JSON;

/**
 * @(#)LoginConfigVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2020/7/3 9:38
 * 描　述：
 */
public class LoginConfigVo {

    /**限制密码错误重试次数*/
    private Integer retryCount;

    /**账号锁定时间*/
    private Integer lockSecend;

    public static LoginConfigVo getLoginConfigVo(String loginConfig){
        LoginConfigVo vo = JSON.toJavaObject(JSON.parseObject(loginConfig),LoginConfigVo.class);
        return vo;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Integer getLockSecend() {
        return lockSecend;
    }

    public void setLockSecend(Integer lockSecend) {
        this.lockSecend = lockSecend;
    }
}
