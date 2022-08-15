package com.fardo.modules.api.fallback;

import com.fardo.modules.api.SysPrivateDataRomoteApi;
import com.fardo.modules.system.config.entity.SysPrivateDataEntity;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Component
public class SysPrivateDataRomoteApiFallbackImpl implements SysPrivateDataRomoteApi {

    @Setter
    private Throwable cause;

    @Override
    public SysPrivateDataEntity getById(String id) {
        log.error("获取私有数据错误 {}",id, cause);
        return null;
    }

    @Override
    public void save(String id, String data) {
        log.error("保存私有数据错误 {},{}",id, data, cause);
    }
}
