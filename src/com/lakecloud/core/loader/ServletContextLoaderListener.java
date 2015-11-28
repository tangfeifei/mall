package com.lakecloud.core.loader;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.lakecloud.core.security.SecurityManager;

/**
 * 
* <p>Title: ServletContextLoaderListener.java</p>

* <p>Description:系统基础信息加载监听器，目前用来加载系统权限数据，也可以将系统语言包在这里加载进来，该监听器会在系统系统的时候进行一次数据加载 </p>

 */
public class ServletContextLoaderListener implements ServletContextListener {

	public void contextInitialized(ServletContextEvent servletContextEvent) {
		ServletContext servletContext = servletContextEvent.getServletContext();
		SecurityManager securityManager = this
				.getSecurityManager(servletContext);
		Map<String, String> urlAuthorities = securityManager
				.loadUrlAuthorities();
		servletContext.setAttribute("urlAuthorities", urlAuthorities);
	}

	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		servletContextEvent.getServletContext().removeAttribute(
				"urlAuthorities");
	}

	protected SecurityManager getSecurityManager(ServletContext servletContext) {
		return (SecurityManager) WebApplicationContextUtils
				.getWebApplicationContext(servletContext).getBean(
						"securityManager");
	}
}
