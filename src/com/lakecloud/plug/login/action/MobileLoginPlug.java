package com.lakecloud.plug.login.action;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.ConstEnum;
import com.lakecloud.core.tools.Md5Encrypt;
import com.lakecloud.foundation.domain.Album;
import com.lakecloud.foundation.domain.IntegralLog;
import com.lakecloud.foundation.domain.NoticeAttemp;
import com.lakecloud.foundation.domain.Role;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.IAlbumService;
import com.lakecloud.foundation.service.IIntegralLogService;
import com.lakecloud.foundation.service.INoticeAttempService;
import com.lakecloud.foundation.service.IRoleService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.manage.admin.tools.MsgTools;

/************
 * 
 * @author tangf
 * 用户手机登陆时进行处理
 * 
 * 
 * 主要提供如下方法 
 * 
 * 短信生成发送并存储于系统中
 * 
 * 创建过滤器用于在手机一侧认证用户手机以及短信验证码是否是正确的 
 * 如果手机号码上没有注册过这里首先开一个账户，执行登录 
 * 
 */
@Controller
@RequestMapping("/m")
public class MobileLoginPlug {
	private final static int validateCodeLength = 6;
	private final static int MAX_VILDATENUM = 3;
	
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IIntegralLogService integralLogService;
	
	@Autowired
	private INoticeAttempService noticeAttempService;
	@Autowired
	private MsgTools msgTools;//短信发送工具
	@Autowired
	private ResourceBundleMessageSource resourceBundleMessageSource;
	
 
 
	
	@RequestMapping("/mobile_notice.htm")
	@ResponseBody
	public Map mobile_notice(HttpServletRequest request,
			HttpServletResponse response , String mobileNo) throws IOException {
			Map map = new HashMap();
			map.put("success" , "false");
			Matcher matcher= ConstEnum.MOBILEPATTER.matcher(mobileNo);
			if(!matcher.find()){
				map.put("message",resourceBundleMessageSource.getMessage("PHONEINVALIDATE", null, request.getLocale()));
				return map;
			}
			NoticeAttemp noticetemplate =noticeAttempService.getObjByProperty("telephone", mobileNo);
			if(noticetemplate==null){
				noticetemplate = new NoticeAttemp();
				noticetemplate.setAddTime(new Date());
				noticetemplate.setTelephone(mobileNo);
				noticetemplate.setLasttime(new Date());
				noticetemplate.setLimitmillsec(10*60*1000);
				noticeAttempService.save(noticetemplate);//保存数据模型信息
			}else{
				if(noticetemplate.getLimitmillsec() < (new Date().getTime() -  noticetemplate.getLasttime().getTime())){
					//应该将此事限制为宜
					noticetemplate.setTimeTicks(1);
					noticetemplate.setLasttime(new Date());//更新为当前时间
				}else{
					noticetemplate.setTimeTicks(noticetemplate.getTimeTicks()+1);//降价一个限制
				}
				noticeAttempService.update(noticetemplate);//更新消息信息
			}
			
			/*
			if(noticetemplate.getTimeTicks()>MAX_VILDATENUM){
				map.put("message", resourceBundleMessageSource.getMessage("MESSAGELIMIT", null, request.getLocale()));
				return map;
			}*/
			//调用Token生成器
			String rand = CommUtil.randomInt(validateCodeLength).toUpperCase();
		 	request.getSession(false).setAttribute(ConstEnum.MOBILE_VALIDATE_CODE, new ValidateCodeInfo(rand,ConstEnum.VALICODE_INVALIATE_TIME)); //记录认证吗
		 	System.out.println("the message code is :"+rand);
		 	map.put("success", true); //表明设置成功
		 	map.put("code", rand); //表明设置成功
		 	return map;
	}
	
	//验证码信息
	public class ValidateCodeInfo{
		private Date createDate ; //认证吗创建时间
		private String code ;    //认证吗
		private long validateTime ;//有效时间
		public  ValidateCodeInfo(String code , long validateTime){
			this.code = code ;
			this.validateTime  = validateTime ;
			this.createDate = new Date(); //创建当前时间作为标准时间
		}
		//验证短信西悉尼是否正常
		public boolean isVAlidate(String code){
			if(this.code!=null && this.code.equals(code)&&(this.validateTime < 0 ||(new Date().getTime() - this.createDate.getTime()) < this.validateTime)){
				return true ;
			}
			return false ;
		}
		
		public boolean beCreate(){
			
			return false ;
		}
	}
			
		 	
		
 
	
	@RequestMapping("/mobile_bind.htm")
	public String mobile_bind(HttpServletRequest request,
			HttpServletResponse response ,String mobileNo){
		
		String result ="";
		User user = this.userService.getObjByProperty("mobile",
				mobileNo);
		if (SecurityUserHolder.getCurrentUser() == null) {// 使用QQ账号登录
			if (user == null) {
				user = new User();
				user.setUserName(mobileNo);
				user.setUserRole("BUYER");
				user.setMobile(mobileNo);
				user.setAddTime(new Date());
				user.setPassword(Md5Encrypt.md5("123456").toLowerCase());
				Map params = new HashMap();
				params.put("type", "BUYER");
				List<Role> roles = this.roleService.query(
						"select obj from Role obj where obj.type=:type",
						params, -1, -1);
				user.getRoles().addAll(roles);
				if (this.configService.getSysConfig().isIntegral()) {
					user.setIntegral(this.configService.getSysConfig()
							.getMemberRegister());
					this.userService.save(user);
					IntegralLog log = new IntegralLog();
					log.setAddTime(new Date());
					log.setContent("注册赠送积分:"
							+ this.configService.getSysConfig()
									.getMemberRegister());
					log.setIntegral(this.configService.getSysConfig()
							.getMemberRegister());
					log.setIntegral_user(user);
					log.setType("reg");
					this.integralLogService.save(log);
				} else {
					this.userService.save(user);
				}
				// 创建用户默认相册
				Album album = new Album();
				album.setAddTime(new Date());
				album.setAlbum_default(true);
				album.setAlbum_name("默认相册");
				album.setAlbum_sequence(-10000);
				album.setUser(user);
				this.albumService.save(album);
				request.getSession(false).removeAttribute("verify_code");
				request.getSession(false).setAttribute("bind", "mobile");
				return "redirect:" + CommUtil.getURL(request)
						+ "/lakecloud_login.htm?username="
						+ CommUtil.encode(user.getUsername())
						+ "&password=123456";
			} else {
				request.getSession(false).removeAttribute("verify_code");
				result =  "redirect:" + CommUtil.getURL(request)
						+ "/lakecloud_login.htm?username="
						+ CommUtil.encode(user.getUsername()) + "&password="
						+ Globals.THIRD_ACCOUNT_LOGIN + user.getPassword();
			}
		} else {
				User curuser = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
				if(user==null|| user.getId() == curuser.getId()){
					// 用户已经登录，在用户中心绑定QQ账号
					curuser.setMobile(mobileNo);
					this.userService.update(curuser);
					result = "redirect:" + CommUtil.getURL(request)
							+ "/buyer/account_bind.htm";
				}else{
					result = "redirect:" + CommUtil.getURL(request)
							+ "/buyer/account_bind.htm?result=bindready&type=mobile";
				}
				
	 
			}
		return result;
		
	}
	
	/************
	 * 执行手机绑定工作
	 * @param request
	 * @param response
	 * @param mobileNo
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/mobile_api.htm")
	public ModelAndView mobile_api(HttpServletRequest request,
			HttpServletResponse response ) throws IOException {
		ModelAndView mv = new JModelAndView("mobile_login_api.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		return mv;
	}
	
}
