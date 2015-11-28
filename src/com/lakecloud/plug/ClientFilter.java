package com.lakecloud.plug;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.ConstEnum;
import com.lakecloud.core.tools.CookieUtils;

public class ClientFilter  implements Filter{
	
	private String loginURL="";
	private Integer tokenLength =12 ;
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest request = (HttpServletRequest)arg0;
		HttpServletResponse response =(HttpServletResponse)arg1;
		String token = CookieUtils.getCookieValue(request, ConstEnum.TOKENKEY); //获取传递过来的token
		//如果是登录那么这里不做处理
		System.out.println(request.getRequestURI());
		if(!request.getRequestURI().contains(loginURL)){
			String tokenKey=(String)request.getSession(false).getAttribute(ConstEnum.TOKENKEY);
			if(tokenKey==null||!tokenKey.equals(token)){
				//这个时候系统跑出异常
				throw new RuntimeException("Error for provide token info");
			}
		}
		String tokenKey = CommUtil.randomInt(tokenLength);
		CookieUtils.setCookie(request, response,ConstEnum.TOKENKEY, tokenKey);//设置tokenKey;
		request.getSession(false).setAttribute(ConstEnum.TOKENKEY, tokenKey);//用于下一次验证信息
		arg2.doFilter(arg0, arg1);//如果一切正常的话那么直接过滤
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		loginURL= arg0.getInitParameter("loginurl");
		String templateTokenLength  = arg0.getInitParameter("tokenlength");
		try{
			tokenLength  = Integer.parseInt(templateTokenLength);
		}catch(Exception e){
			//tokenLength = 12;//默认是12个
		}
	}

}
