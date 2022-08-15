package com.fardo.modules.system.user.enums;

import com.fardo.common.api.vo.IResultCode;

public enum UserResultCodeEnum implements IResultCode {

    USERNAME_EXIST("101", "检测数据异常，用户名已存在，请联系管理员处理。"),
    PHONE_EXIST("103", "检测数据异常，手机号已存在，请联系管理员处理。"),
  IDCARD_EXIST("102", "检测数据异常，身份证号已存在，请联系管理员处理。");

    private String resultCode;
    private String resultMsg;

    UserResultCodeEnum(String resultCode, String resultMsg){
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
