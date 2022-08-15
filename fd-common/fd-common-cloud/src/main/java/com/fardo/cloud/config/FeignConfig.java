package com.fardo.cloud.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import com.fardo.common.constant.CommonConstant;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Feign配置
 * 使用FeignClient进行服务间调用，传递headers信息
 */
@Configuration
public class FeignConfig implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        if(RequestContextHolder.getRequestAttributes() == null) {
            return;
        }
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //添加token
        requestTemplate.header(CommonConstant.X_ACCESS_TOKEN, request.getHeader(CommonConstant.X_ACCESS_TOKEN));
    }
}