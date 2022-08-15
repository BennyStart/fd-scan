package com.fardo.modules.api.fallback;

import com.fardo.modules.api.SysJobRomoteApi;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SysJobRomoteApiFallbackImpl implements SysJobRomoteApi {

    @Setter
    private Throwable cause;

    @Override
    public void updateCron(Integer id, String day, String hour) {
        log.error("修改定时器cron错误 {},{},{},{}",id, day, hour, cause);
    }

    @Override
    public void stop(Integer id) {
        log.error("停止定时器cron错误 {},{}",id, cause);
    }
}
