package com.fardo.modules.shiro.interceptor;

import com.fardo.common.util.SpringContextUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import groovy.util.logging.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;

@Configuration
@Slf4j
public class FeignInterceptor implements RequestInterceptor {

    private final static String PARAM_SID = "sid";

    @Override
    public void apply(RequestTemplate template) {
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        if(request == null) {
            return;
        }
        template.query(PARAM_SID, request.getParameter(PARAM_SID));
    }
}
