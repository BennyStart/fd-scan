package com.fardo.modules.system.constant;

public class SysConstants {

    /**
     * 常量1 true
     */
    public final static String YES = "1";
    /**
     * 常量0 flase
     */
    public final static String NO = "0";

    public final static String TRUE = "true";

    public final static String FALSE = "false";

    /**
     * 加密方式：国产加密
     */
    public static final String DOMESTIC_ENCRYPT_TYPE = "1";

    /**
     * 强制国产加密 配置值
     */
    public static final String FORCE_DOMESTIC_ENCRYPT_VALUE = "1";

    public static final String APP_KEY_OF_PC = "9999999999";
    public static final String APP_KEY_OF_MOBILE = "8888888888";

    public static final String CHANGE_PWD_WARN = "安全警示：请及时修改原始密码！";
    public static final String RE_POLICE_NO = "^[0-9]{1,9}$";
    public static final String USER_ADMIN = "admin";

    //给字符传加%
    public static final String FORMAT_LIKE_ANYWHERE = "%%%s%%";
    public static final String FORMAT_LIKE_LEFT = "%%%s";
    public static final String FORMAT_LIKE_RIGHT = "%s%%";

    //登录方式
    public static final String LOGIN_MODES_PKI = "1";
    public static final String LOGIN_MODES_PWD = "2";

    //用户身份
    /**
     * 用户身份-2超级管理员
     */
    public static final Integer USER_IDENTITY_SUPERADMIN = 2;

    /**
     * 菜单权限-01首页
     */
    public static final String PERMISSION_INDEX = "01";

    /**
     * 登录方式
     */
    public static final String LOGIN_TYPE_USERPASS = "用户密码登录";
    public static final String LOGIN_TYPE_SSO = "单点登录";

    /**
     * 退出方式
     */
    public static final String LOGOUT_TYPE_MANUAL = "手动退出";
    public static final String LOGOUT_TYPE_TIMEOUT = "超时自动退出";


    public static final String LOG_OPER_DESC = "operDesc";
    public static final String LOG_OPER_TYPE = "operType";
    public static final String LOG_RESULT_DATA = "resultData";




}
