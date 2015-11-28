package com.lakecloud.core.security.support;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.AccessDeniedException;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.ui.AccessDeniedHandler;
import org.springframework.security.ui.AccessDeniedHandlerImpl;

import com.lakecloud.foundation.domain.User;
/**
 * 
* <p>Title: ShopAccessDeniedHandlerImpl.java</p>

* <p>Description:  重写SpringSecurity权限控制接口，系统采用二次登陆机制，用户从前台登陆，仅仅加载非管理员权限，从管理员页面登陆，通过该控制器加载管理员权限</p>

* <p>Copyright: Copyright (c) 2012-2014</p>
 */
public class ShopAccessDeniedHandlerImpl implements AccessDeniedHandler {
	public static final String SPRING_SECURITY_ACCESS_DENIED_EXCEPTION_KEY = "SPRING_SECURITY_403_EXCEPTION";
	protected static final Log logger = LogFactory
			.getLog(AccessDeniedHandlerImpl.class);
	private String errorPage;

	public void handle(ServletRequest request, ServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException,
			ServletException {
		this.errorPage = "/authority.htm";
		User user = SecurityUserHolder.getCurrentUser();
		GrantedAuthority[] all_authorities = user.get_all_Authorities();
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		GrantedAuthority[] current_authorities = auth.getAuthorities();
		if (user.getUserRole().indexOf("ADMIN") < 0) {
			this.errorPage = "/buyer/authority.htm";
		} else {
			if (all_authorities.length != current_authorities.length) {
				this.errorPage = "/admin/login.htm";
			}
		}
		if (this.errorPage != null) {
			((HttpServletRequest) request).setAttribute(
					"SPRING_SECURITY_403_EXCEPTION", accessDeniedException);

			RequestDispatcher rd = request.getRequestDispatcher(this.errorPage);
			rd.forward(request, response);
		}

		if (!response.isCommitted()) {
			((HttpServletResponse) response).sendError(403,
					accessDeniedException.getMessage());
		}
	}

	public void setErrorPage(String errorPage) {
		if ((errorPage != null) && (!errorPage.startsWith("/"))) {
			throw new IllegalArgumentException("errorPage must begin with '/'");
		}

		this.errorPage = errorPage;
	}

}
