package com.fardo.modules.listener;

import com.fardo.common.util.MD5Util;
import com.fardo.common.util.MacheInfoUtils;
import com.fardo.modules.system.config.entity.SysPrivateDataEntity;
import com.fardo.modules.system.config.mapper.SysPrivateDataMapper;
import com.fardo.modules.system.config.service.ISysClientConfigService;
import com.fardo.modules.system.config.service.ISysConfigService;
import com.fardo.modules.system.config.service.ISysDictService;
import com.fardo.modules.system.config.service.ISysSettingService;
import com.fardo.modules.system.security.service.ISysApiSecretService;
import com.fardo.modules.system.user.service.ISysUserService;
import com.fardo.modules.system.util.DmPyInitUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 系统初始化监听器,在系统启动时运行,进行一些初始化工作
 *
 */
@Component
public class InitSystemListener implements InitializingBean {


    @Autowired
    private ISysClientConfigService sysClientConfigService;
    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private ISysSettingService sysSettingService;
    @Resource
    private SysPrivateDataMapper sysPrivateDataMapper;
    @Autowired
    private DmPyInitUtil dmPyInitUtil;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysApiSecretService sysApiSecretService;
    @Autowired
    private ISysDictService sysDictService;


    @Override
    public void afterPropertiesSet() throws Exception {
        //将机器信息注册进私有参数表
        this.initMachine();
        //初始化字典py首字母
        dmPyInitUtil.initPy();
        //初始化服务端系统参数
        sysConfigService.init();
        //刷新数据字典缓存
        sysDictService.refleshCache();
        //初始化客户端系统参数
        sysSettingService.init();

        //初始化客户端下发配置
        sysClientConfigService.refreshClientConfig();

        //初始化用户姓名拼音字段
        sysUserService.initRealnamePy();

        //初始化接入应用
        sysApiSecretService.initApiSecretMap();
    }


    private void initMachine() {
        String machineInfo = MacheInfoUtils.cpuKey() + MacheInfoUtils.macKey();
        String machineID = Objects.requireNonNull(MD5Util.MD5(machineInfo)).substring(0, 30);
        SysPrivateDataEntity sysPrivateDataEntity = sysPrivateDataMapper.selectById(machineID);
        if(sysPrivateDataEntity == null) {
            sysPrivateDataEntity = SysPrivateDataEntity.createMachine(machineID);
            sysPrivateDataMapper.insert(sysPrivateDataEntity);
        }
    }

}
