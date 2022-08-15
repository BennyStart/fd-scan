package com.fardo.modules.job;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Component;

/**
 * 任务Handler示例（Bean模式）
 */
@Component
public class JobHandlerDemo {

    @XxlJob("jobHandlerDemo")
    public ReturnT<String> execute(String s) throws Exception {
        XxlJobLogger.log("XXL-JOB, Hello World.");
        System.out.println("XXL-JOB, Hello World.");
        return ReturnT.SUCCESS;
    }
}
