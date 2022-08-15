package com.fardo.modules.shiro.controller;

import com.fardo.common.constant.CommonConstant;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ErrorController extends BasicErrorController {

    public ErrorController() {
        super(new DefaultErrorAttributes(), new ErrorProperties());
    }

    @Override
    @RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        Map<String, Object> body = getErrorAttributes(request, isIncludeStackTrace(request, MediaType.ALL));
        HttpStatus status = getStatus(request);
        Map<String,Object> map = new HashMap<>();
        Object message = body.get("message");
        map.put("success",false);
        map.put("message",message);
        map.put("code", CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
        if(message != null && message.toString().startsWith("org.apache.shiro.authc.AuthenticationException")) {
            map.put("code",CommonConstant.SC_NO_AUTHZ);
            status = HttpStatus.OK;
        }
        map.put("timestamp",System.currentTimeMillis());
        return new ResponseEntity<>(map, status);
    }
}
