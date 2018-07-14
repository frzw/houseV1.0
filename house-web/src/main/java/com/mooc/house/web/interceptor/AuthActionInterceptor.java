package com.mooc.house.web.interceptor;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.mooc.house.common.model.User;

/**
 * 鉴权管理- 在AuthInterceptor之后执行
 */
@Component
public class AuthActionInterceptor implements HandlerInterceptor {

	/**
	 * 请求controller之前调用
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//从线程池中取用户信息
		User user = UserContext.getUser();
		//用户未登陆，跳转登陆页面
		if (user == null) {
			String msg = URLEncoder.encode("请先登录","utf-8");
			String target = URLEncoder.encode(request.getRequestURL().toString(),"utf-8");
			//请求方式处理
			if ("GET".equalsIgnoreCase(request.getMethod())) {
				response.sendRedirect("/accounts/signin?errorMsg=" + msg + "&target="+target);//跳转登陆页
				return false;//修复bug,未登录要返回false
			}else {
				response.sendRedirect("/accounts/signin?errorMsg="+msg);
				return false;//修复bug,未登录要返回false
			}
		}
		return true;
	}

	/**
	 * 请求controller之后调用
	 * @param request
	 * @param response
	 * @param handler
	 * @param modelAndView
	 * @throws Exception
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}

	/**
	 * 页面渲染之后
	 * @param request
	 * @param response
	 * @param handler
	 * @param ex
	 * @throws Exception
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

	}

}
