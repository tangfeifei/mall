package com.lakecloud.weixin.store.view.action;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.Md5Encrypt;
import com.lakecloud.foundation.domain.Album;
import com.lakecloud.foundation.domain.Document;
import com.lakecloud.foundation.domain.IntegralLog;
import com.lakecloud.foundation.domain.Role;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.IAlbumService;
import com.lakecloud.foundation.service.IDocumentService;
import com.lakecloud.foundation.service.IIntegralLogService;
import com.lakecloud.foundation.service.IRoleService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.view.web.tools.ImageViewTools;

/**
 * @info 微信店铺客户端注册、登录管理控制器

 */
@Controller
public class WeixinStoreLoginViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private ImageViewTools imageViewTools;
	@Autowired
	private IDocumentService documentService;

	/**
	 * 用户登录页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/weixin/login.htm")
	public ModelAndView login(HttpServletRequest request,
			HttpServletResponse response, String url) {
		ModelAndView mv = new JModelAndView("weixin/login.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).removeAttribute("verify_code");
		return mv;
	}

	/**
	 * 用户注册页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/weixin/register.htm")
	public ModelAndView register(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/register.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).removeAttribute("verify_code");
		Document obj = this.documentService.getObjByProperty("mark", "agree");
		mv.addObject("obj", obj);
		return mv;
	}

	/**
	 * 注册完成
	 * 
	 * @param request
	 * @param userName
	 * @param password
	 * @param email
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	@RequestMapping("/weixin/register_finish.htm")
	public String register_finish(HttpServletRequest request,
			HttpServletResponse response, String userName, String password,
			String email) throws HttpException, IOException {
		boolean reg = true;// 防止机器注册，如后台开启验证码则强行验证验证码
		// 进一步控制用户名不能重复，防止在未开启注册码的情况下注册机恶意注册
		Map params = new HashMap();
		params.put("userName", userName);
		List<User> users = this.userService.query(
				"select obj from User obj where obj.userName=:userName",
				params, -1, -1);
		if (users != null && users.size() > 0) {
			reg = false;
		}
		if (reg) {
			User user = new User();
			user.setUserName(userName);
			user.setUserRole("BUYER");
			user.setAddTime(new Date());
			user.setEmail(email);
			user.setPassword(Md5Encrypt.md5(password).toLowerCase());
			params.clear();
			params.put("type", "BUYER");
			List<Role> roles = this.roleService.query(
					"select obj from Role obj where obj.type=:type", params,
					-1, -1);
			user.getRoles().addAll(roles);
			if (this.configService.getSysConfig().isIntegral()) {
				user.setIntegral(this.configService.getSysConfig()
						.getMemberRegister());
				this.userService.save(user);
				IntegralLog log = new IntegralLog();
				log.setAddTime(new Date());
				log.setContent("用户注册增加"
						+ this.configService.getSysConfig().getMemberRegister()
						+ "分");
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
			return "redirect:lakecloud_login.htm?username="
					+ CommUtil.encode(userName) + "&password=" + password
					+ "&encode=true";
		} else {
			return "redirect:weixin/register.htm";
		}
	}
}
