package com.fardo.modules.system.sys.vo;

/**
 * @(#)Pwd <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2020/7/3 10:21
 * 描　述：
 */
public class PwdFailVo {

    private boolean userLocked;

    private String msg;

    public PwdFailVo(boolean userLocked, String msg) {
        this.userLocked = userLocked;
        this.msg = msg;
    }

    public boolean isUserLocked() {
        return userLocked;
    }

    public void setUserLocked(boolean userLocked) {
        this.userLocked = userLocked;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
