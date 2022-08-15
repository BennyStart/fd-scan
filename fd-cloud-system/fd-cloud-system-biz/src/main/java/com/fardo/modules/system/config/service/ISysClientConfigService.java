package com.fardo.modules.system.config.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fardo.modules.system.config.entity.SysClientConfigEntity;
import com.fardo.modules.system.config.vo.SysFuncVo;

import java.util.List;

public interface ISysClientConfigService extends IService<SysClientConfigEntity> {

    /**
     *
     * @更新时间:2016-8-18
     * @更新作者:李木泉
     * @param configId
     * @return
     */
    public SysClientConfigEntity getConfigId(String configId);

    /**
     * 获取配置内容，
     * @更新时间:2016-8-18
     * @更新作者:李木泉
     * @param configId
     * @return
     */
    public String getConfigContentByConfigId(String configId);
    /**
     * 初始化加载
     * @更新时间:2016-8-18
     * @更新作者:李木泉
     */
    public void load();

    void reload(String configId);
    /**
     * 刷新
     * @更新时间:2016-8-18
     * @更新作者:李木泉
     */
    public void reload();

    public void refreshClientConfig();
    /**
     * 提供给同步客户端配置项的维护管理接口使用
     * @更新时间:2016-8-30
     * @更新作者:李木泉
     * @param configId
     */
    public void refreshClientConfig(String configId);

    /**同步刷新客户端配置项缓存
     *
     * @更新时间:2019年1月25日
     * @更新作者:suzc
     * @param configId
     */
    void synRefreshClientConfig(String configId);
    /**
     * 刷新常用网站
     * @更新时间:2016-8-30
     * @更新作者:李木泉
     */
    public void refreshSite();
    /**
     * 刷新文书配置
     * @更新时间:2016-8-30
     * @更新作者:李木泉
     */
    public void refreshWenShu();
    /**
     * 刷新人员关系
     * @更新时间:2016-8-30
     * @更新作者:李木泉
     */
    public void refreshRygx();
    /**
     * 刷新语种类别
     * @更新时间:2016-8-30
     * @更新作者:李木泉
     */
    public void refreshLangType();

    /**
     * 刷新审讯设备
     * @更新时间:2017年5月4日
     * @更新作者:suzc
     */
    public void refreshDeviceSx();

    /**
     * 刷新笔录类型管理缓存
     * @更新时间:2017年6月28日
     * @更新作者:suzc
     */
    public void refreshBllx();

    /**
     * 刷新必问项不全缓存
     * @更新时间:2017年8月25日
     * @更新作者:suzc
     */
    public void refreshBwxbq();

    /**
     * 刷新客户端功能启用白名单缓存
     * @author zhanghc
     * @date 2019年5月8日
     * @version 3.3.6
     */
    public void refreshFuncFilter();

    /**
     * 保存客户端启用功能
     * @更新时间:2018年11月13日
     * @更新作者:Liusx
     * @param checkFunction 启用的功能
     * @param uncheckFunction 不启用的功能
     */
    public void saveClientFunctionOpen(String checkFunction, String uncheckFunction);

    /**
     * 查询客户端功能是否启用
     * @更新时间:2018年11月13日
     * @更新作者:Liusx
     * @param proKeys 客户端功能的pro_key
     * @return
     */
    public List<SysFuncVo> queryFunctionSetting(String proKeys);

}
