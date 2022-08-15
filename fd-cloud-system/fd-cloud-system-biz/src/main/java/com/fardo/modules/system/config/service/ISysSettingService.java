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
import com.fardo.modules.system.config.entity.SysSettingEntity;
import com.fardo.modules.system.config.model.SysSettingQueryModel;
import com.fardo.modules.system.config.vo.SysSettingDeleteVo;
import com.fardo.modules.system.config.vo.SysSettingQueryVo;
import com.fardo.modules.system.config.vo.SysSettingSaveVo;

public interface ISysSettingService extends IService<SysSettingEntity> {


    /**
     * 获取系统参数
     *
     * @param key
     * @return
     */
    String getSysParam(String key);

    /**
     * 获取int类型系统参数
     *
     * @param key
     * @return
     */
    int getIntSysParam(String key);

    /**
     * 获取Bool类型系统参数
     *
     * @param key
     * @return
     */
    boolean getBoolSysParam(String key);

    /**
     * 初始化系统参数
     *
     */
    void init();

    /**
     * 刷新系统参数缓存
     *
     */
    void refresh();

    /**
     * 修改系统参数
     * @param key
     * @param value
     * @return
     */
    boolean updateSysParam(String key, String value);
    
    IPage<SysSettingQueryModel> queryPage(SysSettingQueryVo vo);

    void save(SysSettingSaveVo vo);

    void delete(SysSettingDeleteVo vo);
    
    
}
