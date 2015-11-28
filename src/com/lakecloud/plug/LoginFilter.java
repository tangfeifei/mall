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

import com.lakecloud.core.tools.ConstEnum;
import com.lakecloud.plug.login.action.MobileLoginPlug.ValidateCodeInfo;

/***********
 * 用于在系统中检查手机登录信息
 * @author tangf
 *
 */
public class LoginFilter implements Filter {
	String messageURI =null ;
	String loginURI = null ;
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
		String requestURI = request.getRequestURI();
		System.out.println("I am in the system");
		//表明这个是messageURI
		if(requestURI.contains(loginURI)){ //表明这里是登录链接
			ValidateCodeInfo codeInfo =	(ValidateCodeInfo)request.getSession(false).getAttribute(ConstEnum.MOBILE_VALIDATE_CODE);
			//获取验证码参数
			String validate_code=request.getParameter(ConstEnum.MOBILE_VALIDATE_CODE);
			System.out.println(validate_code);
			if(codeInfo!=null&&codeInfo.isVAlidate(validate_code)){
				request.getSession(false).removeAttribute(ConstEnum.MOBILE_VALIDATE_CODE);//移除掉验证信息
				arg2.doFilter(request, response);
			}else{
				throw new RuntimeException("手机验证码不匹配！");
			}
		}else{
			arg2.doFilter(arg0,arg1);
		}
		 
	}
	
	//出事化时添加连哥哥uri
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		messageURI =arg0.getInitParameter("messageuri");
		loginURI   =arg0.getInitParameter("loginuri"); 
		
	}

}
