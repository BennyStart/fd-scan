package com.fardo.modules.job;

import com.fardo.modules.system.sync.service.ITzfwDataSyncService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 台州服务数据机构、用户数据同步
 */
@Component
public class TzfwJobHandler {

    @Autowired
    private ITzfwDataSyncService tzfwDataSyncService;

    @XxlJob("syncDepartAndUser")
    public ReturnT<String> execute(String s) throws Exception {
        tzfwDataSyncService.syncDepartAndUserData();
        return ReturnT.SUCCESS;
    }
}
