package com.fardo.modules.system.config.service;

public interface SysParameterService {

    String getSysParam(String key);

    /**
     * 获取int类型系统参数
     *
     * @param key
     * @return
     * @更新时间:2016-8-11
     * @更新作者:李木泉
     */
    public int getIntSysParam(String key);

    /**
     * 获取Bool类型系统参数
     *
     * @param key
     * @return
     * @更新时间:2016-8-11
     * @更新作者:李木泉
     */
    public boolean getBoolSysParam(String key);

    /**
     * 插件appkey
     */
    public static final String ParamOfPluginAppkey = "plugin_appkey";

    /**
     * 插件appsecret
     */
    public static final String ParamOfPluginAppsecret = "plugin_appsecret";

    /**
     * 专家库服务引擎服务地址
     */
    public static final String ParamOfExpertSeUrl = "expert_se";

    /**
     * 专家库案由、模板同步适用地域配置
     **/
    String CAUSE_TEMPLATE_SYDY_CODE = "cause_template_sydy_code";

    /**
     * 是否开放笔录问答内容表给第三方
     */
    String ParamOfOpenBlData = "OPEN_BL_DATA";
    /**
     * 开放笔录问答内容表给第三方,使用的加密key
     */
    String ParamOfOpenBlDataKey = "OPEN_BL_DATA_KEY";


}
