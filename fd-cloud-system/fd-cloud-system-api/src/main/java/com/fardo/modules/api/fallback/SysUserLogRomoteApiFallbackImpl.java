package com.fardo.modules.api.fallback;

import com.fardo.modules.api.SysUserLogRomoteApi;
import com.fardo.modules.system.log.entity.SysUserLogEntity;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SysUserLogRomoteApiFallbackImpl implements SysUserLogRomoteApi {

    @Setter
    private Throwable cause;

    @Override
    public void save(SysUserLogEntity entity) {
        log.error("保存接口日志错误", cause);
    }

    @Override
    public Integer getUserDlcs() {
        log.error("获取登录次数错误", cause);
        return null;
    }
}
