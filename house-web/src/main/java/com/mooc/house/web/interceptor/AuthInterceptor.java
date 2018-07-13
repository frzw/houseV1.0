package com.mooc.house.web.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Joiner;
import com.mooc.house.common.constants.CommonConstants;
import com.mooc.house.common.model.User;

/**
 * 授权管理，判断用户是否已经登录（拦截器）
 */
@Component
public class AuthInterceptor implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		Map<String, String[]> map = request.getParameterMap();
		map.forEach((k,v) -> {
			if (k.equals("errorMsg") || k.equals("successMsg") || k.equals("target")) {
				request.setAttribute(k, Joiner.on(",").join(v));
			}
		});
	    String reqUri =	request.getRequestURI();
	    if (reqUri.startsWith("/static") || reqUri.startsWith("/error") ) {
			return true;
		}
	    HttpSession session = request.getSession(true);//session没有时，自动创建
	    User user =  (User)session.getAttribute(CommonConstants.USER_ATTRIBUTE);
	    if (user != null) {
			UserContext.setUser(user);
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		UserContext.remove();
	}
	

}
