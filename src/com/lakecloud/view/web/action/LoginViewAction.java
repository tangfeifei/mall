package com.lakecloud.view.web.action;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.Md5Encrypt;
import com.lakecloud.foundation.domain.Album;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.IntegralLog;
import com.lakecloud.foundation.domain.Role;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.IAlbumService;
import com.lakecloud.foundation.service.IGoodsClassService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IIntegralLogService;
import com.lakecloud.foundation.service.IRoleService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.uc.api.UCClient;
import com.lakecloud.uc.api.UCTools;
import com.lakecloud.view.web.tools.ImageViewTools;

/**
 * 
 * <p>
 * Title: LoginViewAction.java
 * </p>
 * 
 * <p>
 * Description:
 * 用户登录、注册管理控制器，用来管理用户登录、注册、UC统一登录等功能,V1.3版本开始独立出来，为了方便外部系统（如UCenter等）的使用
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-5-9
 * 
 * @version LakeCloud_C2C 1.4 集群版
 */
@Controller
public class LoginViewAction {
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
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private ImageViewTools imageViewTools;
	@Autowired
	private UCTools ucTools;

	/**
	 * 用户登录页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/user/login.htm")
	public ModelAndView login(HttpServletRequest request,
			HttpServletResponse response, String url) {
		ModelAndView mv = new JModelAndView("login.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).removeAttribute("verify_code");
		boolean domain_error = CommUtil.null2Boolean(request.getSession(false)
				.getAttribute("domain_error"));
		if (url != null && !url.equals("")) {
			request.getSession(false).setAttribute("refererUrl", url);
		}
		if (domain_error) {
			mv.addObject("op_title", "域名绑定错误，请与http://www.chinacloud.net联系");
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		} else {
			mv.addObject("imageViewTools", imageViewTools);
		}
		mv.addObject("uc_logout_js",
				request.getSession(false).getAttribute("uc_logout_js"));
		String lakecloud_view_type = CommUtil.null2String(request.getSession(
				false).getAttribute("lakecloud_view_type"));
		if (lakecloud_view_type != null && !lakecloud_view_type.equals("")) {
			if (lakecloud_view_type.equals("weixin")) {
				String store_id = CommUtil.null2String(request
						.getSession(false).getAttribute("store_id"));
				mv = new JModelAndView("weixin/success.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "退出成功！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/weixin/index.htm?store_id=" + store_id);
			}
		}
		return mv;
	}

	/**
	 * 用户注册页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/register.htm")
	public ModelAndView register(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("register.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).removeAttribute("verify_code");
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
	@RequestMapping("/register_finish.htm")
	public String register_finish(HttpServletRequest request,
			HttpServletResponse response, String userName, String password,
			String email, String code) throws HttpException, IOException {
		boolean reg = true;// 防止机器注册，如后台开启验证码则强行验证验证码
		if (code != null && !code.equals("")) {
			code = CommUtil.filterHTML(code);// 过滤验证码
		}
		// System.out.println(this.configService.getSysConfig().isSecurityCodeRegister());
		if (this.configService.getSysConfig().isSecurityCodeRegister()) {
			if (!request.getSession(false).getAttribute("verify_code")
					.equals(code)) {
				reg = false;
			}
		}
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
			if (this.configService.getSysConfig().isUc_bbs()) {// 是否开启UC_bbs同步
				UCClient client = new UCClient();
				String ret = client.uc_user_register(userName, password, email);
				int uid = Integer.parseInt(ret);
				if (uid <= 0) {
					if (uid == -1) {
						System.out.print("用户名不合法");
					} else if (uid == -2) {
						System.out.print("包含要允许注册的词语");
					} else if (uid == -3) {
						System.out.print("用户名已经存在");
					} else if (uid == -4) {
						System.out.print("Email 格式有误");
					} else if (uid == -5) {
						System.out.print("Email 不允许注册");
					} else if (uid == -6) {
						System.out.print("该 Email 已经被注册");
					} else {
						System.out.print("未定义");
					}
				} else {
					this.ucTools.active_user(userName, user.getPassword(),
							email);
				}
			}
			return "redirect:lakecloud_login.htm?username="
					+ CommUtil.encode(userName) + "&password=" + password
					+ "&encode=true";
		} else {
			return "redirect:register.htm";
		}
	}

	/**
	 * springsecurity登录成功后跳转到该页面，如有登录相关处理可以在该方法中完成
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/user_login_success.htm")
	public ModelAndView user_login_success(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String url = CommUtil.getURL(request) + "/index.htm";
		String lakecloud_view_type = CommUtil.null2String(request.getSession(
				false).getAttribute("lakecloud_view_type"));
		if (lakecloud_view_type != null && !lakecloud_view_type.equals("")) {
			if (lakecloud_view_type.equals("weixin")) {
				String store_id = CommUtil.null2String(request
						.getSession(false).getAttribute("store_id"));
				mv = new JModelAndView("weixin/success.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				url = CommUtil.getURL(request) + "/weixin/index.htm?store_id="
						+ store_id;
			}
		}
		HttpSession session = request.getSession(false);
		if (session.getAttribute("refererUrl") != null
				&& !session.getAttribute("refererUrl").equals("")) {
			url = (String) session.getAttribute("refererUrl");
			session.removeAttribute("refererUrl");
		}
		if (this.configService.getSysConfig().isUc_bbs()) {
			String uc_login_js = CommUtil.null2String(request.getSession(false)
					.getAttribute("uc_login_js"));
			mv.addObject("uc_login_js", uc_login_js);
		}
		String bind = CommUtil.null2String(request.getSession(false)
				.getAttribute("bind"));
		if (!bind.equals("")) {
			mv = new JModelAndView(bind + "_login_bind.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			User user = SecurityUserHolder.getCurrentUser();
			mv.addObject("user", user);
			request.getSession(false).removeAttribute("bind");
		}
		mv.addObject("op_title", "登录成功");
		mv.addObject("url", url);
		return mv;
	}

	/**
	 * 弹窗登录页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/user_dialog_login.htm")
	public ModelAndView user_dialog_login(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user_dialog_login.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}

	class StoreTest implements Runnable {
		private int store_thread_num;

		public StoreTest(int num) {
			this.store_thread_num = num;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("第" + this.store_thread_num + "线程启动");
			for (int i = (store_thread_num - 1) * 2000; i < store_thread_num * 2000; i++) {
				Store store = new Store();
				Random ran = new Random();
				User user = userService.getObjById(CommUtil.null2Long(i));
				store.setAddTime(new Date());
				store.setStore_info("store" + i);
				store.setTemplate("default");
				store.setStore_status(2);
				storeService.save(store);
				Map params = new HashMap();
				params.put("type", "SELLER");
				List<Role> roles = roleService.query(
						"select obj from Role obj where obj.type=:type",
						params, -1, -1);
				user.setStore(store);
				user.getRoles().addAll(roles);
				userService.update(user);
			}
		}

	}

	class UserTest implements Runnable {

		private int user_thread_num;

		public UserTest(int num) {
			this.user_thread_num = num;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("第" + this.user_thread_num + "线程启动");
			for (int i = (user_thread_num - 1) * 2000; i < user_thread_num * 2000; i++) {
				User user = new User();
				user.setUserName("user" + i);
				user.setUserRole("BUYER");
				user.setAddTime(new Date());
				user.setEmail("user" + i + "@163.com");
				user.setPassword(Md5Encrypt.md5("123456").toLowerCase());
				Map params = new HashMap();
				params.clear();
				params.put("type", "BUYER");
				List<Role> roles = roleService.query(
						"select obj from Role obj where obj.type=:type",
						params, -1, -1);
				user.getRoles().addAll(roles);
				if (configService.getSysConfig().isIntegral()) {
					user.setIntegral(configService.getSysConfig()
							.getMemberRegister());
					userService.save(user);
					IntegralLog log = new IntegralLog();
					log.setAddTime(new Date());
					log.setContent("用户注册增加"
							+ configService.getSysConfig().getMemberRegister()
							+ "分");
					log.setIntegral(configService.getSysConfig()
							.getMemberRegister());
					log.setIntegral_user(user);
					log.setType("reg");
					integralLogService.save(log);
				} else {
					userService.save(user);
				}
				// 创建用户默认相册
				Album album = new Album();
				album.setAddTime(new Date());
				album.setAlbum_default(true);
				album.setAlbum_name("默认相册");
				album.setAlbum_sequence(-10000);
				album.setUser(user);
				albumService.save(album);
			}
			user_thread_num = user_thread_num + 1;
		}

	}

	class GoodsTest implements Runnable {
		private int goods_thread_num;

		public GoodsTest(int num) {
			this.goods_thread_num = num;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("第" + this.goods_thread_num + "线程启动");
			for (int i = (goods_thread_num - 1) * 200; i < goods_thread_num * 200; i++) {
				Goods goods = new Goods();
				goods.setAddTime(new Date());
				goods.setGc(goodsClassService.getObjById(3l));
				goods.setGoods_name("测试商品" + i);
				double price = CommUtil.null2Double(CommUtil.randomInt(3));
				goods.setStore_price(BigDecimal.valueOf(price));
				goods.setGoods_price(BigDecimal.valueOf(price + 80));
				Random ran = new Random();
				Store goods_store = storeService.getObjById(CommUtil
						.null2Long(ran.nextInt(20000)));
				goods.setGoods_store(goods_store);
				goodsService.save(goods);
			}
			System.out.println("第" + this.goods_thread_num + "线程运行完毕");
			if (this.goods_thread_num == 5000) {
				System.out.println("所有线程运行完毕，生成100万件商品");
			}
		}

	}

	@RequestMapping("/init_test_data.htm")
	public ModelAndView init_test_data(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		// 初始化一百万商品
		System.out.println("开始初始化100万商品信息....");
		for (int i = 1; i <= 5000; i++) {
			GoodsTest goodsTest1 = new GoodsTest(i);
			Thread thread1 = new Thread(goodsTest1);
			thread1.start();
		}
		mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		return mv;
	}
}
