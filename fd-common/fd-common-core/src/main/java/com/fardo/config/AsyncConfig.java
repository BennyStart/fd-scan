package com.fardo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * 线程池的配置
 */
@Slf4j
@Configuration
public class AsyncConfig {

    private static final int MAX_POOL_SIZE = 50;

    private static final int CORE_POOL_SIZE = 20;

    @Bean("asyncTaskExecutor")
    public AsyncTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor asyncTaskExecutor = new ThreadPoolTaskExecutor();
        asyncTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        asyncTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        asyncTaskExecutor.setThreadNamePrefix("async-task-thread-pool-");
        asyncTaskExecutor.setTaskDecorator(new TaskDecorator() {
            @Override
            public Runnable decorate(Runnable runnable) {
                try {
                    // 获取主线程上下文
                    RequestAttributes context = RequestContextHolder.currentRequestAttributes();
                    ServletRequestAttributes attributes = (ServletRequestAttributes) context;
                    return new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Optional.ofNullable(attributes.getRequest()).ifPresent(req -> {
                                    log.info("当前线程request sid为:{}", req.getParameter("sid"));
                                });
                                // 把主线程上下文添加到当前线程中
                                RequestContextHolder.setRequestAttributes(context);
                                runnable.run();
                            } finally {
                                RequestContextHolder.resetRequestAttributes();
                            }
                        }
                    };
                } catch (Exception e) {
                    // 定时器等无request的线程使用feign时需要处理
                    return new Runnable() {
                        @Override
                        public void run() {
                            log.warn("当前线程无request上下文,需手动赋值sid...");
                            MockHttpServletRequest request = new MockHttpServletRequest();
                            request.addParameter("sid", "SYS");
                            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
                            runnable.run();
                        }
                    };
                }
            }
        });
        asyncTaskExecutor.initialize();
        return asyncTaskExecutor;
    }


}
