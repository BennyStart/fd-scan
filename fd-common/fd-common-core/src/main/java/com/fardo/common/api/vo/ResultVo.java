/**
 *
 */
package com.fardo.common.api.vo;

import com.fardo.common.enums.OperTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 返回结果
 * @author wangbt 2021年3月29日
 *
 */
@Data
public class ResultVo<T> implements Serializable {

    private static final long serialVersionUID = 884692462286884597L;
    public final static String SEPARATOR = "，";

    private String resultCode;
    private String resultMsg;
    private String data;
    private T results;
    // 请求切面返回数据，在FdResponseBodyAdvice做处理，不返回给前端
    @ApiModelProperty(hidden = true)
    private AspectLogVo aspectLogVo;

    public static ResultVo getResultVo(String status, String message) {
        ResultVo rsVo = getResultVo();
        rsVo.resultCode = status;
        rsVo.resultMsg = message;
        return rsVo;
    }

    public static ResultVo getResultVo() {
        ResultVo rsVo = new ResultVo<>();
        return rsVo;
    }

    public static ResultVo getResultVo(IResultCode resultCode) {
        ResultVo rsVo = new ResultVo();
        rsVo.setResultCode(resultCode);
        return rsVo;
    }

    public ResultVo() {
    }

    public ResultVo(String data) {
        this.data = data;
    }

    public void setResultCode(IResultCode resultCode) {
        this.resultCode = resultCode.getResultCode();
        this.resultMsg = resultCode.getResultMsg();
    }

    public void setAspectLogVo(String operDesc, String resultData) {
        aspectLogVo = new AspectLogVo(operDesc, resultData);
    }

    public void setAspectLogVo(String operDesc) {
        aspectLogVo = new AspectLogVo(operDesc, operDesc);
    }

    public void setAspectLogVo(String operDesc, String resultData, OperTypeEnum operTypeEnum) {
        aspectLogVo = new AspectLogVo(operDesc, resultData, operTypeEnum);
    }

}

