package com.fardo.modules.job;

import com.fardo.modules.system.sys.service.ApiSessionService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 系统job
 */
@Component
public class SysJobHandler {

    @Autowired
    private ApiSessionService apiSessionService;

    @XxlJob("checkApiSession")
    public ReturnT<String> execute(String s) throws Exception {
        apiSessionService.checkSessionTimeOut();
        return ReturnT.SUCCESS;
    }
}
