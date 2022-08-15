package com.fardo.modules.system.sys.enums;

import com.fardo.common.api.vo.IResultCode;

public enum LoginResultCodeEnum implements IResultCode {

    LOGIN_IDCARD_ERROR("304", "不允许使用身份证号登录"),
    USER_NOT_EXIST("201", "账号输入错误，请重新输入"),
    USER_CANCELLED("202", "该用户已注销"),
    USER_FREEZE("203", "该用户已冻结"),
    PASSWORD_ERROR("204", "密码输入错误，请重新输入"),
    USER_LOCK("205", ""),
    LOGIN_FAIL_USERNAM_EXITS_ERROR("206", "该账号已经在其他地方登录"),
    LOGIN_FAIL_IP_AUTH("207", "此ip不允许访问"),
    USER_WITHOUT_ROLE("208", "用户未分配角色"),
    LOGIN_PWD_NO_SUPPORT("209", "该用户不支持用户名密码登录方式"),
    LOGIN_PKI_NO_SUPPORT("210", "该用户不支持PKI登录方式"),
    LOGIN_SSO_TYPE_ERROR("301", "账号类型错误"),
    LOGIN_SSO_NOT_USER("302", "账号不存在"),
    LOGIN_SSO_NOT_REQUESTID("303", "requestId无效"),
    ;

    private String resultCode;
    private String resultMsg;

    LoginResultCodeEnum(String resultCode, String resultMsg){
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    /**
     * @return the resultCode
     */
    public String getResultCode() {
        return resultCode;
    }

    /**
     * @param resultCode the resultCode to set
     */
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    /**
     * @return the resultMsg
     */
    public String getResultMsg() {
        return resultMsg;
    }

    /**
     * @param resultMsg the resultMsg to set
     */
    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

}
