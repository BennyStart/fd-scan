package com.fardo.modules.system.constant;

/**
 * 缓存常量
 */
public class CacheKeyConstants {

    public static final String Module = "BL" + "znbl4";
    public static final String KeySep = ":";

    /**
     * api接口会话
     */
    public static final String API_SESSION_PREFIX = Module + KeySep + "ApiSession" + KeySep;
    /**
     * 被踢除的用户的sid的key前缀
     */
    public static final String BE_KICKED_OUT_SID_PREFIX = Module + KeySep + "BeKickedOutSid" + KeySep;
    /**
     * api会话，最后一次检测时间，格式yyyyMMddHHmmss
     */
    public static final String API_SESSION_TIME_OUT_CHECK_TIME = Module + KeySep + "ApiSessionTimeOutCheckTime" + KeySep;
    /**
     * api在线会话集合
     */
    public static final String API_SESSION_ON_LINE_MAP = Module + KeySep + "apiSessiononLineMap" + KeySep;

    /**
     * 系统参数模块
     */
    public static final String SYS_PARAMETER = Module + KeySep + "Sys.Param";
    /**
     * 客户端参数模块
     */
    public static final String CLIENT_PARAMETER = Module + KeySep + "Client.Param";
    /**
     * 登录失败重试次数
     */
    public static final String PwdLoginFail = Module + KeySep + "pwdLoginFail" + KeySep;
    /**
     * 客户端配置同步
     */
    public static final String CLIENT_CONFIG = Module + KeySep + "ClientConfig";
    public static final String CLIENT_CONFIG_CONTENT = Module + KeySep + "ClientConfig" + KeySep + "Content";

    /**
     * 分布式锁前缀
     */
    public static final String REDIS_LOCK_KEY_PREFIX = "redisLock" + KeySep;

    /**
     * 用户导入任务进度信息
     */
    public static final String USER_IMP_TASK = Module + KeySep + "userImpTask";
    /**
     * 用户导入任务是否开启
     */
    public static final String USER_IMP_TASK_START = Module + KeySep + "userImpTaskStart";

    /**
     * 系统代码递增序列
     */
    public static final String SYS_CODE = Module + KeySep + "sysCode";

    /**
     * 系统代码递增序列
     */
    public static final String TALK_TIME_JOB_EXCUTE_TIME = Module + KeySep + "talkTimeJobExcuteTime";

    /**
     * 笔录基础问题缓存key值
     */
    public static final String BL_BASE_QUESTION = Module + KeySep + "BaseQuestion";
    /**
     * api接口会话
     */
    public static final String LOGIN_SSO_PREFIX = Module + KeySep + "loginSSO" + KeySep;
    /**
     * 接入应用缓存
     */
    public static final String API_SECRET_PREFIX = Module + KeySep + "apiSecret" + KeySep;
    /**
     * 笔录大类型缓存
     */
    public static final String DM_BL_DLX = Module + KeySep + "DM.BL_DLX" + KeySep;
    /**
     * 笔录类型缓存
     */
    public static final String DM_BL_LX = Module + KeySep + "DM.BL_LX" + KeySep;
    /**
     * 笔录询问对象类型缓存
     */
    public static final String DM_BL_XWDXLX = Module + KeySep + "DM.BL_XWDXLX" + KeySep;
    /**
     * 案由缓存
     */
    public static final String DM_BL_AY = Module + KeySep + "DM.BL_AY" + KeySep;


    /**
     * 开放笔录同步任务缓存
     */
    public static final String SYN_OPEN_BL_TASK = Module + KeySep + "synOpenBlTask";

    /**
     * solr 初始化进度状态
     */
    public static final String SOLR_INIT_STATUS = Module + KeySep + "solrInitStatus";

}
