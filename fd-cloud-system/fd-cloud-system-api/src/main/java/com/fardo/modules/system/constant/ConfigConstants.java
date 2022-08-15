package com.fardo.modules.system.constant;

/**
 * 客户端，服务端配置常量
 */
public class ConfigConstants {

    /**
     * 法度帐户api的url
     */
    public static final String ParamOfFdUserUrl = "fduser_url";
    /**
     * 客户端软件包下载地址
     *
     */
    public static final String ParamOfDownloadUrl = "downloadUrl";
    /**
     * 用于存放客户端软件包的本地根路径
     */
    public static final String ParamOfSoftRootPath = "softRootPath";

    /**
     * 登录校验次数和时间
     */
    public static final String loginParamConfig = "LOGIN_CONFIG";
    /**
     * 客户端api请求会话超时时间,单位：分钟
     */
    public static final String ParamOfSessionValidTime = "sessionValidTime";
    /**
     * 复用请求：请求人默认身份证号
     */
    public static final String ParamOfRsadapterIdCard = "rsadapterIdCard";
    /**
     * 复用请求：请求人默认姓名
     */
    public static final String ParamOfRsadapterUserName = "rsadapterUserName";
    /**
     * 复用请求：请求人默认单位编码
     */
    public static final String ParamOfRsadapterDeprtCode = "rsadapterDeprtCode";
    /**
     * 复用请求：是否使用本地模拟数据
     */
    public static final String ParamOfIsRsLocal = "isRsLocal";
    /**
     * 专家库地址
     */
    public static final String ParamOfExpertServerUrl = "expert_server_url";
    /**
     * 思悦笔录内容对比接口
     */
    public static final String ParamOfSiYueCompareUrl = "SIYUE_COMPARE_URL";

    /**
     * 审讯宝地址
     */
    public static final String ParamOfSxbUrl = "sxb_url";

    /**
     * 人像对比相似值（65以上可视为同一人）
     */
    public static final String ParamOfImageSimilarityThreshold = "image_similarity_threshold";

    /**
     * 管理中心地址
     */
    public static final String ParamOfUserUrl = "user_url";

    /**
     * 是否开放笔录问答内容表给第三方
     */
    public static final String ParamOfOpenBlData = "OPEN_BL_DATA";
    /**
     * 开放笔录问答内容表给第三方,使用的加密key
     */
    public static final String ParamOfOpenBlDataKey = "OPEN_BL_DATA_KEY";

    /**
     * 笔录快照在数据库里的保存时间
     */
    public static final String ParamOfBlHistory = "BL_SNAPSHOT_SAVETIME";

    /**
     * 第三方系统配置（用户笔录数据上传到警综或执法办案等第三方系统）
     */
    public static final String ParamOfThirdParyService = "THIRD_PARTY_SERVICE";

    /**
     * 是否自动上传到第三方平台
     */
    public static final String ParamOfAutoUploadThirdParyService = "AUTO_UPLOAD_THIRD_PARTY_SERVICE";

    /**
     * @名称:ParamOfPluginLawUrl
     * @描述:法律法规插件入口地址
     */
    public static final String ParamOfPluginLawUrl = "plugin_law_url";

    /**
     * @名称:ParamOfPluginZncfUrl
     * @描述:智能处罚插件入口地址
     */
    public static final String ParamOfPluginZncfUrl = "plugin_zncf_url";

    /**
     * @名称:ParamOfPluginZncfUrl
     * @描述:案由插件入口地址
     */
    public static final String ParamOfPluginCauseLawUrl = "plugin_cause_law_url";
    /**
     * @名称:ParamOfPluginAppkey
     * @描述:插件appkey
     */
    public static final String ParamOfPluginAppkey = "plugin_appkey";

    /**
     * @名称:ParamOfPluginAppsecret
     * @描述:插件appsecret
     */
    public static final String ParamOfPluginAppsecret = "plugin_appsecret";

    /**
     * 办案通地址
     */
    public static final String ParamOfBATURL = "BAT_URL";

    /**
     * 以图搜图服务
     */
    public static final String ParamOfSearchByImgUrl = "SEARCH_BY_IMG_URL";
    /**
     * 以图搜图服务登录用户名
     */
    public static final String ParamOfSearchByImgUser = "SEARCH_BY_IMG_USER";
    /**
     * 以图搜图服务登录密码
     */
    public static final String ParamOfSearchByImgPwd = "SEARCH_BY_IMG_PWD";
    /**
     * 以图搜图服务门限
     */
    public static final String ParamOfSearchByImgThr = "SEARCH_BY_IMG_THR";


    /**
     * 报表服务通讯秘钥
     */
    public static final String ParamOfReportSecret = "report_secret";
    /**
     * 报表服务通讯AppKey
     */
    public static final String ParamOfReportAppKey = "report_app_key";
    /**
     * 报表服务地址
     */
    public static final String ParamOfReportServerUrl = "report_server_url";

    /**
     * 深海请求报文FTP服务器
     */
    public static final String ParamOfShenHaiFtpQuery = "SHEN_HAI_FTP_QUERY";

    /**
     * 深海结果报文FTP服务器
     */
    public static final String ParamOfShenHaiFtpResult = "SHEN_HAI_FTP_RESULT";

    /**
     * 深海结果报文FTP服务器过期文件清理间隔（单位：小时）
     */
    public static final String ParamOfShenHaiFtpFileCleanPerHour = "SHEN_HAI_FTP_CLEAN_TIME";

    /**
     * 第三方用户登录时，用户不存是否自动注册（true-自动注册，false-不注册）
     */
    public static final String ParamOfThirdUserAutoRegist = "THIRD_USER_AUTO_REGIST";

    /**
     * 怀化警务云地址
     */
    public static final String ParamOfHuaiHuaURL = "HuaiHuaURL";

    /**
     * 客户端最低版本登录限制
     */
    public static final String ParamOfSysMinVersionLimit = "SYS_MIN_VERSION_LIMIT";

    /**
     * 当客户端低版本低于最低版本登录限制时的自定义消息
     */
    public static final String ParamOfSysMinVersionLimitMsg = "SYS_MIN_VERSION_LIMIT_MSG";

    /**
     * @名称:ParamOfCasTokenLoginApiUrl
     * @描述:单点登录URL
     */
    public static final String ParamOfCasTokenLoginApiUrl = "CAS_LOGIN_TOKEN_URL";

    /**
     * 专家库服务引擎服务地址
     */
    public static final String ParamOfExpertSeUrl = "expert_se";

    /**
     * pc端法律法规插件入口地址(法度搜索地址)
     */
    public static final String ParamOfPcPluginLawUrl = "pc_plugin_law_url";


    /**
     * sms 发送钥匙
     */
    public static final String ParamOfSmsSendKey = "SMSKey";

    public static final String ParamOfSMSSendUrl = "sms_send_url";
    /**
     * 云搜服务地址
     */
    public static final String ParamOfYunsouUrl = "yun_sou_url";
    /**
     * 云搜服务对接key
     */
    public static final String ParamOfYunsouAppKey = "yun_sou_appkey";

    /**
     * 自动标签问题样本匹配相似度阈值
     */
    public static final String ParamOfAutoLabelThreshold = "auto_label_threshold";

    public static final String ParamOfAppKey = "app_key";
    /**
     * 串并案查询类型，0或空未配置，其它值再约定 mas-马鞍山
     */
    public static final String ParamOfCbaType = "CBA_TYPE";

    /**
     * 法度nlp地址
     */
    public static final String FD_NLP_URL = "fd_nlp_url";

    /**
     * 法度nlp appkey
     */
    public static final String FD_NLP_APP_KEY = "fd_nlp_app_key";

    /**
     * 法度nlp appsecret
     */
    public static final String FD_NLP_APP_SECRET = "fd_nlp_app_secret";

    /**
     * 三汇普元服务地址
     */
    public static final String SAN_HUI_SERVER_URL = "san_hui_server_url";

    /**
     * 宏联服务地址
     */
    public static final String HONG_LIAN_SERVER_URL = "hong_lian_server_url";

    /**
     * 阿里redis配置
     */
    public static final String ALI_REDIS_CONFIG = "ali_redis_config";

    /**
     * 大庆SSO统一登录认证服务
     */
    public static final String DAQING_SSO_SERVER_URL = "daqing_sso_server_url";
    /**
     * 安康app用户日志管控上传服务地址
     */
    public static final String ANKANG_LOG_UPLOAD_SERVER = "ankang_log_upload_server";
    /**
     * 安康app用户-授权管控接口-用户名
     */
    public static final String ANKANG_LOG_USERNAME = "ankang_log_username";
    /**
     * 安康app用户-授权管控接口-用户密码
     */
    public static final String ANKANG_LOG_PASSWORD = "ankang_log_password";
    /**
     * 安康app用户日志管控上传服务-appId
     */
    public static final String ANKANG_LOG_APP_ID = "ankang_log_app_id";

    /**
     * 颖上公安登录地址
     */
    public static final String YING_SHANG_LOGIN_URL = "yingshang_login_url";

    /**
     * 颖上公安用户信息查询地址
     */
    public static final String YING_SHANG_USERINFO_URL = "yingshang_userinfo_url";

    /**
     * 颖上布控预警信息回传地址（实际就是kafka的地址）
     */
    public static final String YING_SHANG_UPLOAD_BKYJ_URL = "yingshang_upload_bkyj_url";

    /**
     * 颖上布控预警信息生产主题
     */
    public static final String YING_SHANG_UPLOAD_BKYJ_TOPIC = "yingshang_upload_bkyj_topic";

    /**
     * 颖上布控预警信息消费主题
     */
    public static final String YING_SHANG_DOWNLOAD_BKYJ_TOPIC = "yingshang_download_bkyj_topic";

    /**
     * 颖上布控预警信息消费群组
     */
    public static final String YING_SHANG_DOWNLOAD_BKYJ_GROUP = "yingshang_download_bkyj_group";

    public static final String YING_SHANG_UPLOAD_BKYJ_CLIENTID = "yingshang_upload_bkyj_client_id";

    /**
     * kafka对接系统接收预警信息url
     */
    public static final String YING_SHANG_PUSH_BKYJ_MSG_URL = "yingshang_push_bkyj_msg_url";

    /**
     * 颖上布控预警功能开启开关
     */
    public static final String OPEN_YINGSHANG_BKYJ = "OPEN_BKYJ";

    /**
     * 节点根路径
     */
    public static final String NODE_BASE_URL = "nodeBaseUrl";

    /**
     * 阜阳案事件查询接口地址
     */
    public static final String FUYANG_CASE_DATA_URL = "fuyang_case_data_url";

    public static final String YING_SHANG_TELQUERY_URL = "yingshang_telquery_url";

    public static final String YING_SHANG_CARQUERY_URL = "yingshang_carquery_url";

    /**
     * 电子签名设备最大接入数限制
     */
    public static final String DEVICE_SIGN_MAX_ACCESS = "device_sign_max_access";

    /**
     * 服务端请求总线地址，默认为空（不请求总线）
     */
    public static final String ZONGXIAN_ADDRESS = "zongxian_address";

    public static final String MOBILE_PLAY_VIDEO_URI = "mobile_play_video_uri";

    /**
     * 视频转文本服务地址
     */
    public static final String ZNBL_VIDEO_TRANSFER_URL = "znbl_video_transfer_url";

    /**
     * 贵州SSO统一登录认证服务
     */
    public static final String GUIZHOU_SSO_URL = "guizhou_sso_server_url";
    /**
     * 贵州SSO统一登录认证key
     */
    public static final String GUIZHOU_SSO_CLIENT_KEY = "guizhou_sso_client_key";
    /**
     * 贵州SSO统一登录认证秘钥
     */
    public static final String GUIZHOU_SSO_CLIENT_SECRET = "guizhou_sso_client_secret";
    /**
     * 文件服务地址（视频询问笔录）
     */
    public static final String FILE_SERVER_URL_MOBILE = "file_server_url_moblie";
    /**
     * 贵州楼宇
     */
    public static final String GUIZHOU_LOUYU = "guizhou_louyu";

    /**
     * 徐州丰县警综对接总线地址
     */
    public static final String XUZHOU_FENGXIAN_ZONGXIAN_ADDRESS = "xuzhou_fengxian_zongxian_address";
    /**
     * 徐州丰县中转服务地址
     */
    public static final String XUZHOU_FENGXIAN_TRANSFER_ADDRESS = "xuzhou_fengxian_transfer_address";
    /**
     * 徐州丰县发送方ID
     */
    public static final String XUZHOU_FENGXIAN_SENDER_ID = "xuzhou_fengxian_sender_id";
    /**
     * 徐州丰县案件服务ID
     */
    public static final String XUZHOU_FENGXIAN_AJ_SERVICE_ID = "xuzhou_fengxian_aj_service_id";
    /**
     * 徐州丰县处警服务ID
     */
    public static final String XUZHOU_FENGXIAN_JCJ_SERVICE_ID = "xuzhou_fengxian_jcj_service_id";
    /**
     * 徐州丰县人员服务ID
     */
    public static final String XUZHOU_FENGXIAN_RY_SERVICE_ID = "xuzhou_fengxian_ry_service_id";
    /**
     * 徐州丰县记录仪服务ID
     */
    public static final String XUZHOU_FENGXIAN_JLY_SERVICE_ID = "xuzhou_fengxian_jly_service_id";
    /**
     * 徐州丰县记录仪视频文件服务url
     */
    public static final String XUZHOU_FENGXIAN_FTP_URL = "xuzhou_fengxian_ftp_url";
    /**
     * 徐州丰县人员信息查询服务ID
     */
    public static final String XUZHOU_FENGXIAN_RY_INFO_SERVICE_ID = "fengxian_ry_info_service_id";

    /**
     * 笔录文书初始获取积分页码
     */
    public static final String BL_WS_INIT_PAGES = "blWsInitPage";
    /**
     * 笔录文书初始获取积分数
     */
    public static final String BL_WS_INIT_POINTS = "blWsInitPoiints";
    /**
     * 增加每页获取多少积分数
     */
    public static final String BL_WS_PER_PAGE_POINTS = "blWsPerPagePoints";

    /**珠海集中管控日志上传参数*/
    /**
     * 珠海集中管控日志上传服务地址
     */
    public static final String ZHUHAI_LOG_UPLOAD_SERVER = "zhuhai_log_upload_server";
    /**
     * 珠海集中管控日志上传appid
     */
    public static final String ZHUHAI_LOG_UPLOAD_APP_ID = "zhuhai_log_upload_app_id";
    /**
     * 珠海集中管控日志上传appcode
     */
    public static final String ZHUHAI_LOG_UPLOAD_APP_CODE = "zhuhai_log_upload_app_code";
    /**
     * 珠海集中管控日志上传秘钥
     */
    public static final String ZHUHAI_LOG_UPLOAD_SECRET = "zhuhai_log_upload_secret";
    /**
     * 深圳海关关键字推送topicName
     **/
    public static final String SZHG_PUSH_TOPICNAME_KEYWORD = "szhg_push_topicname_keyword";
    /**
     * 深圳海关搜索推送topicName
     **/
    public static final String SZHG_PUSH_TOPICNAME_SEARCH = "szhg_push_topicname_search";
    /**
     * 深圳海关嫌疑人推送topicName
     **/
    public static final String SZHG_PUSH_TOPICNAME_XYR = "szhg_push_topicname_xyr";
    /**
     * 深圳海关笔录内容推送topicName
     **/
    public static final String SZHG_PUSH_TOPICNAME_BLCONTENT = "szhg_push_topicname_blcontent";
    /**
     * 深圳海关大数据推送URL
     **/
    public static final String SZHG_PUSH_URL = "szhg_push_url";
    /**
     * 深圳海关大数据推送SystemCode
     **/
    public static final String SZHG_PUSH_SYSTEMCODE = "szhg_push_systemCode";

    /**
     * 单点登录配置
     */
    public static final String SSO_CONFIG = "sso_config";

    /**
     * 单点登录失败提示语
     */
    public static final String PKI_LOGIN_FAIL_TIP = "pki_login_fail_tip";

    /**
     * 笔录协同数据查看权限open-开放，close-封闭，默认封闭
     */
    public static final String BLXT_DATA_VIEW_PERMISSION = "blxt_data_view_permission";

    /**
     * 第三方日志平台
     */
    public static final String LOG_UPLOAD_SERVER = "log_upload_server";
    /**
     * 马鞍山日志平台授权码
     */
    public static final String MAS_SYS_CODE = "mas_sys_code";

    /**
     * 获取当前远程审讯信息url
     */
    public static final String GET_YCTX_INFO_URL = "get_yctx_info_url";

    /**
     * 获取当前远程审讯接口测试标志
     */
    public static final String GET_YCTX_INFO_TEST = "get_yctx_info_test";

    /**
     * 获取当前远程审讯接口测试Ip
     */
    public static final String GET_YCTX_INFO_TEST_IP = "get_yctx_info_test_ip";

    /**
     * 未上传笔录提示开关，false-关，true-开
     */
    public static final String TIPS_UNUPLOAD_BL = "tips_unupload_bl";

    /**
     * 太仓笔录开始/结束询问接口地址
     */
    public static final String TAICANG_BL_START_OR_END_URL = "tc_bl_start_or_end_url";
    /**
     * 是否强制国产加密标志 1-强制国产加密
     **/
    public static final String FORCE_DOMESTIC_ENCRYPT_FLAG = "FORCE_DOMESTIC_ENCRYPT";
    /**
     * 用户名初始化按钮功能是否开启（true-开启，false-关闭，默认关闭）
     */
    public static final String USER_NAME_INIT_BTN_SHOW = "userNameInitBtnShow";
    /**
     * 物品信息上传平台url（广西）
     */
    public static final String GOODS_UPLOAD_URL = "goods_upload_url";

    /**
     * 办案中心url（泰维思）
     */
    public static final String RECORD_URL = "record_url";

    /**
     * 广西sftp配置
     */
    public static final String SFTP_CONFIG_GX = "sftp_config_gx";

    /**
     * 数据统计实时统计设置：-1-关闭实时统计，其他大于等于0的整数代表距离上次统计结束开始多少秒之内数据不再进行统计，
     * 如配置值为30，则代表距离上一次统计之后，30秒内在点击查询列表数据结果不变。
     * 0则表示每次点击查询都会进行重新计算。
     */
    public static final String ParamOfDataCountRealTime = "data_count_real_time";

    /**
     * 专家库案由、模板同步适用地域配置
     **/
    public static final String CAUSE_TEMPLATE_SYDY_CODE = "cause_template_sydy_code";
    /**
     * 专家库案由、模板自动同步配置
     **/
    public static final String AY_MB_SCHEDULED_SYNC_CONFIG = "ay_mb_scheduled_sync_config";
    /**
     * 是否启用同步案由,若使用第三方的案由则不同步.
     **/
    public static final String SYNC_AY_ENABLE = "sync_ay_enable";

    /**
     * 广西上传PDF到minIO配置文件
     **/
    public static final String GX_MINIO_CONFIG = "gx_minio_config";
    /**
     * 是否单点登录
     */
    public static final String CLIENT_IS_SSO = "isSSO";
    /**
     * 是否启用白名单
     */
    public static final  String WHILE_IS_OPEN = "WHILE_LIST_OPEN";

    /**
     * 文件服务器地址
     */
    public static final String FILE_SERVICE_BASE_URL = "file_service_base_url";

    /**
     * 文件服务请求key
     */
    public static final String FILE_SERVICE_APPKEY = "file_service_appkey";

    /**
     * 文件服务请求秘钥
     */
    public static final String FILE_SERVICE_SECRET = "file_service_secret";

    /**
     * 文件服务请求加密算法（ md5 sha256 sm3）
     */
    public static final String FILE_SERVICE_ENCRY_TYPE = "file_service_encryption_type";

    /**
     * 笔录完成状态配置
     */
    public static final String BL_WCZT = "BL_WCZT";
    /**
     * 笔录完成状态值，1-显示完成归档
     */
    public static final String BL_WCZT_1 = "1";
    /**
     * 单点登录前端地址
     */
    public static final String LOGIN_SSO_URL = "LOGIN_SSO_URL";

    /**
     * 文书行业类型配置:公安，BJJC-边境检查
     */
    public static final String WENSHU_CATEGORY = "WENSHU_CATEGORY";

    /**
     * 报表文书url
     */
    public static final String WENSHU_URL = "WENSHU_URL";

    /**
     * 报表照片url
     */
    public static final String PHOTO_URL = "PHOTO_URL";

    /**
     * 报表密钥
     */
    public static final String WENSHU_SECRET = "WENSHU_SECRET";

    /**
     * 报表key
     */
    public static final String WENSHU_KEY = "WENSHU_KEY";

    /**
     * 打印配置
     */
    public static final String WS_PRIVIEW_SETTING = "WS_PRIVIEW_SETTING";

    /**
     * 笔录数据回传第三方系统，配置值为0时，打印的同时将笔录数据上传；配置值为1时，签字捺印完成时上传笔录数据；配置值为2时，退出笔录制作时上传笔录数据；配置值为3时，完成归档时上传笔录数据；如回传节点有多个，配置多个并由英文逗号隔开，列 1,2,3'
     */
    public static final String BL_AUTOMATIC_UPLOAD_TIMING = "BL_AUTOMATIC_UPLOAD_TIMING";
    /**
     * 笔录数据回传第三方系统配置值，配置值为0时，打印的同时将笔录数据上传；
     */
    public static final String BL_AUTOMATIC_UPLOAD_TIMING_0 = "0";
    /**
     * 笔录数据回传第三方系统配置值，配置值为1时，签字捺印完成时上传笔录数据；
     */
    public static final String BL_AUTOMATIC_UPLOAD_TIMING_1 = "1";
    /**
     * 笔录数据回传第三方系统配置值，配置值为2时，退出笔录制作时上传笔录数据；
     */
    public static final String BL_AUTOMATIC_UPLOAD_TIMING_2 = "2";
    /**
     * 笔录数据回传第三方系统配置值，配置值为3时，完成归档时上传笔录数据；
     */
    public static final String BL_AUTOMATIC_UPLOAD_TIMING_3 = "3";

    /**
     * 执法办案系统接口地址
     */
    public static final String ZFBA_URL = "ZFBA_URL";
    /**
     * 执法办案系统公钥
     */
    public static final String ZFBA_APP_KEY = "ZFBA_APP_KEY";
    /**
     * 执法办案系统密钥
     */
    public static final String ZFBA_SECRET = "ZFBA_SECRET";
    /**
     * 执法办案系统加密方式
     */
    public static final String ZFBA_ENCRYPT = "ZFBA_ENCRYPT";
    /**
     * 执法办案系统加密类型
     */
    public static final String ZFBA_ENCRYPT_TYPE = "ZFBA_ENCRYPT_TYPE";
    /**
     * 执法办案系统des密钥
     */
    public static final String ZFBA_DES_KEY = "ZFBA_DES_KEY";
    /**
     * 执法办案系统国密4密钥
     */
    public static final String ZFBA_SM4_KEY = "ZFBA_SM4_KEY";

    /**
     * 浙江汇信服务url前缀
     */
    public static final String ZJHXKJ_URL = "ZJHXKJ_URL";
    /**
     * 浙江汇信服务appKey
     */
    public static final String ZJHXKJ_APP_KEY = "ZJHXKJ_APP_KEY";
    /**
     * 浙江汇信服务appSecret
     */
    public static final String ZJHXKJ_APP_SECRET = "ZJHXKJ_APP_SECRET";

}
