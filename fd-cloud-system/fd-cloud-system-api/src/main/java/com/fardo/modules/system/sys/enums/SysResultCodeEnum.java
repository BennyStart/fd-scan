package com.fardo.modules.system.sys.enums;

import com.fardo.common.api.vo.IResultCode;

public enum SysResultCodeEnum implements IResultCode {

    NOT_FIND_SECRET("101", "找不到密钥"),
    ERROR_FORMAT("102", "格式错误"),
    File_SERVE_ERROR("103", "文件服务响应异常");

    private String resultCode;
    private String resultMsg;

    SysResultCodeEnum(String resultCode, String resultMsg){
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
