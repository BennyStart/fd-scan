package com.fardo.modules.system.sys.vo;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * @作者:wangbt
 * @文件名:ParamVo
 * @版本号:1.0
 * @创建日期:2020-12-11
 * @描述:接口传入参数包装vo
 */
@Data
public class ParamVo<T> {

    @ApiModelProperty(value = "appKey", hidden = true)
	private String app_key;
    @ApiModelProperty(value = "时间戳", hidden = true)
	private String timestamp;
    @ApiModelProperty(value = "签名", hidden = true)
	private String sign;
    @ApiModelProperty(value = "sid")
	private String sid;
    @ApiModelProperty(value = "字段说明")
	private T data;
    @ApiModelProperty(value = "业务参数，json格式")
	private String params;
    @ApiModelProperty(value = "日志参数", hidden = true)
	private String opLog;
    @ApiModelProperty(value = "操作类型", hidden = true)
	private String opType;
    @ApiModelProperty(value = "版本号", hidden = true)
	private String clientVersion;
    @ApiModelProperty(value = "是否需要压缩返回", hidden = true)
	private String compress;
    @ApiModelProperty(value = "是否需要加密", hidden = true)
	private String encrypt;
    @ApiModelProperty(value = "加密类型", hidden = true)
	private String encryptType;

    @ApiModelProperty(hidden = true)
    private String serverName;
    @ApiModelProperty(hidden = true)
    private String serverPort;
    @ApiModelProperty(hidden = true)
    private String loginUserVo;
    @ApiModelProperty(hidden = true)
    private String remoteIp;
    @ApiModelProperty(hidden = true)
    private String remotePort;

    public LoginUserVo currentLoginUser(){
        if(StringUtils.isNotEmpty(loginUserVo)) {
            return JSONObject.parseObject(loginUserVo, LoginUserVo.class);
        }
        return null;
    }

}
