package com.lakecloud.core.security.support;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.User;
/**
 * 
* <p>Title: SecurityUserHolder.java</p>

* <p>Description: SpringSecurity用户获取工具类，该类的静态方法可以直接获取已经登录的用户信息</p>

* <p>Copyright: Copyright (c) 2012-2014</p>

* <p>Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net</p>

* @author erikzhang

* @date 2014-4-27

* @version LakeCloud_C2C 1.3
 */
public class SecurityUserHolder {

	/**
	 * Returns the current user
	 * 
	 * @return
	 */
	public static User getCurrentUser() {
		if (SecurityContextHolder.getContext().getAuthentication() != null
				&& SecurityContextHolder.getContext().getAuthentication()
						.getPrincipal() instanceof User) {

			return (User) SecurityContextHolder.getContext()
					.getAuthentication().getPrincipal();
		} else {
			User user = null;
			if (RequestContextHolder.getRequestAttributes() != null) {
				HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
						.getRequestAttributes()).getRequest();
				user = (request.getSession().getAttribute("user") != null ? (User) request
						.getSession().getAttribute("user") : null);
				// System.out.println(user != null ? user.getUserName() : "空");
				if (Globals.SSO_SIGN) {
					Cookie[] cookies = request.getCookies();
					String id = "";
					if (cookies != null) {
						for (Cookie cookie : cookies) {
							if (cookie.getName()
									.equals("lakecloud_user_session")) {
								id = CommUtil.null2String(cookie.getValue());
							}
						}
					}
					if (id.equals("")) {
						user = null;
					}
				}
			}
			return user;
		}

	}
}
