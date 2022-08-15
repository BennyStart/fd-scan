/**
 * @(#)SysParameterService.java 版权声明  巨龙软件工程有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：李木泉
 * 时　间：2016-11-15
 * 描　述：创建
 */

package com.fardo.modules.system.config.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fardo.modules.system.config.entity.SysConfigEntity;
import com.fardo.modules.system.config.vo.QueryConfigByCodeVo;
import com.fardo.modules.system.config.vo.SysConfigListVo;
import com.fardo.modules.system.config.vo.SysConfigVo;
import com.fardo.modules.system.config.vo.UpdateConfigByCodeVo;

import java.util.List;
import java.util.Map;


public interface ISysConfigService extends IService<SysConfigEntity> {
    /**
     * 获取系统参数
     *
     * @param key
     * @return
     */
    public String getSysParam(String key);

    /**
     * 获取int类型系统参数
     *
     * @param key
     * @return
     */
    public int getIntSysParam(String key);

    /**
     * 获取Bool类型系统参数
     *
     * @param key
     * @return
     */
    public boolean getBoolSysParam(String key);

    /**
     * 初始化系统参数
     *
     */
    public void init();

    /**
     * 刷新系统参数缓存
     *
     */
    public void refresh();

    public List<Map<String, Object>> doFindAlList();

    boolean findConfigSwitch(String code);

    void uploadConfig(String key, String value);

    IPage<SysConfigEntity> getPageList(SysConfigListVo sysConfigVo);

    /**
     * 校验参数主键是否合法
     * @param id 编辑时传id
     * @param key 参数主键
     * @return true为参数主键可用
     */
    boolean checkParameterConfig(String id,String key);

    void addParameterConfig(SysConfigVo sysConfigVo,String userId);

    void updatParameterConfig(SysConfigVo sysConfigVo,String userId);

    void deleteSysConfig(String id);

    Integer selectCount(String code);

    void updateConfigByCode(UpdateConfigByCodeVo vo);

    SysConfigVo queryConfigByCode(QueryConfigByCodeVo vo);
}
