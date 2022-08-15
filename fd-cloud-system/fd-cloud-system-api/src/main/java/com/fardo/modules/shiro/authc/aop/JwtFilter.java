package com.fardo.modules.shiro.authc.aop;

import com.fardo.common.util.SpringContextUtils;
import com.fardo.common.util.StringUtil;
import com.fardo.modules.shiro.authc.JwtToken;
import com.fardo.modules.shiro.vo.DefContants;
import com.fardo.modules.system.sys.service.ApiSessionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 鉴权登录拦截器
 * @Author: Scott
 * @Date: 2018/10/7
 **/
@Slf4j
public class JwtFilter extends BasicHttpAuthenticationFilter {

	private static final List<String> IGNORE_LOGIN_API_LIST = Arrays.asList("/sys/privateData");

	/**
	 * 执行登录认证
	 *
	 * @param request
	 * @param response
	 * @param mappedValue
	 * @return
	 */
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		try {
			//免登录
			if(this.ignoreApi((ShiroHttpServletRequest) request)){
				return true;
			}

            // 验证sid，验证成功后，不校验token
            if(this.checkSid(request)) {
                return true;
            }
			executeLogin(request, response);
			return true;
		} catch (Exception e) {
			throw new AuthenticationException("Token失效，请重新登录", e);
		}
	}


    /**
     * 验证sid是否有效
     * @param request
     * @return
     */
	private boolean checkSid(ServletRequest request) {
	    String sid = request.getParameter("sid");
	    if(StringUtil.isEmpty(sid)) {
	        return false;
        }
        ApiSessionService apiSessionService = SpringContextUtils.getBean(ApiSessionService.class);
        String loginUser = apiSessionService.getLoginUser(sid);
	    if(StringUtil.isEmpty(loginUser)) {
	        return false;
        }
	    return true;
    }


	/**
	 *
	 */
	@Override
	protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String token = httpServletRequest.getHeader(DefContants.X_ACCESS_TOKEN);

		JwtToken jwtToken = new JwtToken(token);
		// 提交给realm进行登入，如果错误他会抛出异常并被捕获
		getSubject(request, response).login(jwtToken);
		// 如果没有抛出异常则代表登入成功，返回true
		return true;
	}

	private boolean ignoreApi(ShiroHttpServletRequest request) {
		for(String api : IGNORE_LOGIN_API_LIST) {
			if (request.getServletPath().contains(api)) {
				return true;
			}
		}
		return false;
	}

//	/**
//	 * 对跨域提供支持
//	 */
//	@Override
//	protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
//		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//		httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
//		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
//		httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
//		// 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
//		if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
//			httpServletResponse.setStatus(HttpStatus.OK.value());
//			return false;
//		}
//		return super.preHandle(request, response);
//	}
}
