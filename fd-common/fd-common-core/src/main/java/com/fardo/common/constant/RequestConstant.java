package com.fardo.common.constant;

import java.util.LinkedHashMap;
import java.util.Map;

public class RequestConstant {

    /**
     * 编码方式  UTF-8
     */
    public static final String CHARSET_UTF8 = "utf-8";

    public static final String AppSecrect = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmMIICdgIBADANBgk";

    public static final String AppKeyOfPc = "9999999999";
    public static final String AppKeyOfMobile = "8888888888";

    public static final Map<String,String> AppKeyAndNameMap = new LinkedHashMap<String,String>();
    static{
        AppKeyAndNameMap.put(AppKeyOfPc, "PC客户端");
        AppKeyAndNameMap.put(AppKeyOfMobile, "手机客户端");
    }

    //0为不加密，默认值为0,1为请求参数加密，2为返回值加密，3为请求/返回双向加密
    public static final String EncryptNo = "0";
    public static final String EncryptRequest = "1";
    public static final String EncryptReturn = "2";
    public static final String EncryptAll = "3";

    /**
     * 加密方式：国产加密
     */
    public static final String DOMESTIC_ENCRYPT_TYPE = "1";

    /**
     * 强制国产加密 配置值
     */
    public static final String FORCE_DOMESTIC_ENCRYPT_VALUE = "1";

}
