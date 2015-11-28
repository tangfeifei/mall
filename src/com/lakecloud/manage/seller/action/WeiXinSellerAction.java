package com.lakecloud.manage.seller.action;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.domain.virtual.SysMap;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.WebForm;
import com.lakecloud.foundation.domain.Accessory;
import com.lakecloud.foundation.domain.GoldLog;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.IAccessoryService;
import com.lakecloud.foundation.service.IGoldLogService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.weixin.domain.VLog;
import com.lakecloud.weixin.domain.VMenu;
import com.lakecloud.weixin.domain.VMessage;
import com.lakecloud.weixin.domain.query.VLogQueryObject;
import com.lakecloud.weixin.domain.query.VMessageQueryObject;
import com.lakecloud.weixin.service.IVLogService;
import com.lakecloud.weixin.service.IVMenuService;
import com.lakecloud.weixin.service.IVMessageService;
import com.lakecloud.weixin.tools.WeixinTools;

/**
 * @info 卖家微信商城控制器，卖家支付相应金币可以申请微信商城开通，开通商城后可以配置微信菜单及微信App相关信息，微信商城和卖家店铺直接对应
  
 * 
 */
@Controller
public class WeiXinSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoldLogService goldLogService;
	@Autowired
	private IVLogService vLogService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IVMenuService vMenuService;
	@Autowired
	private IVMessageService vmessageService;
	@Autowired
	private WeixinTools weixinTools;

	@SecurityMapping(title = "微信商城", value = "/seller/weixin_store.htm*", rtype = "seller", rname = "微信商城", rcode = "weixin_store_seller", rgroup = "微信管理")
	@RequestMapping("/seller/weixin_store.htm")
	public ModelAndView weixin_store(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/weixin_store.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		String developer_url = CommUtil.getURL(request)
				+ "/weixin_store_action.htm?store_id="
				+ user.getStore().getId();
		mv.addObject("developer_url", developer_url);
		mv.addObject("store", user.getStore());
		return mv;
	}

	@SecurityMapping(title = "微信商城数据保存", value = "/seller/weixin_store_save.htm*", rtype = "seller", rname = "微信商城", rcode = "weixin_store_seller", rgroup = "微信管理")
	@RequestMapping("/seller/weixin_store_save.htm")
	public ModelAndView weixin_store_save(HttpServletRequest request,
			HttpServletResponse response) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		Store store = user.getStore();
		WebForm wf = new WebForm();
		store = (Store) wf.toPo(request, store);
		// 保存微信店铺二维码
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "weixin_qr";
		Map map = new HashMap();
		try {
			String fileName = store.getWeixin_qr_img() == null ? "" : store
					.getWeixin_qr_img().getName();
			map = CommUtil.saveFileToServer(request, "qr_img",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory qr_img = new Accessory();
					qr_img.setName(CommUtil.null2String(map.get("fileName")));
					qr_img.setExt(CommUtil.null2String(map.get("mime")));
					qr_img.setSize(CommUtil.null2Float(map.get("fileSize")));
					qr_img.setPath(uploadFilePath + "/weixin_qr");
					qr_img.setWidth(CommUtil.null2Int(map.get("width")));
					qr_img.setHeight(CommUtil.null2Int(map.get("height")));
					qr_img.setAddTime(new Date());
					this.accessoryService.save(qr_img);
					store.setWeixin_qr_img(qr_img);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory qr_img = store.getWeixin_qr_img();
					qr_img.setName(CommUtil.null2String(map.get("fileName")));
					qr_img.setExt(CommUtil.null2String(map.get("mime")));
					qr_img.setSize(CommUtil.null2Float(map.get("fileSize")));
					qr_img.setPath(uploadFilePath + "/weixin_qr");
					qr_img.setWidth(CommUtil.null2Int(map.get("width")));
					qr_img.setHeight(CommUtil.null2Int(map.get("height")));
					qr_img.setAddTime(new Date());
					this.accessoryService.update(qr_img);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 微信店铺logo
		Map map2 = new HashMap();
		try {
			String fileName = store.getStore_weixin_logo() == null ? "" : store
					.getStore_weixin_logo().getName();
			map2 = CommUtil.saveFileToServer(request, "store_weixin_logo",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map2.get("fileName") != "") {
					Accessory store_weixin_logo = new Accessory();
					store_weixin_logo.setName(CommUtil.null2String(map2
							.get("fileName")));
					store_weixin_logo.setExt(CommUtil.null2String(map2
							.get("mime")));
					store_weixin_logo.setSize(CommUtil.null2Float(map2
							.get("fileSize")));
					store_weixin_logo.setPath(uploadFilePath + "/weixin_qr");
					store_weixin_logo.setWidth(CommUtil.null2Int(map2
							.get("width")));
					store_weixin_logo.setHeight(CommUtil.null2Int(map2
							.get("height")));
					store_weixin_logo.setAddTime(new Date());
					this.accessoryService.save(store_weixin_logo);
					store.setStore_weixin_logo(store_weixin_logo);
				}
			} else {
				if (map2.get("fileName") != "") {
					Accessory store_weixin_logo = store.getStore_weixin_logo();
					store_weixin_logo.setName(CommUtil.null2String(map2
							.get("fileName")));
					store_weixin_logo.setExt(CommUtil.null2String(map2
							.get("mime")));
					store_weixin_logo.setSize(CommUtil.null2Float(map2
							.get("fileSize")));
					store_weixin_logo.setPath(uploadFilePath + "/weixin_qr");
					store_weixin_logo.setWidth(CommUtil.null2Int(map2
							.get("width")));
					store_weixin_logo.setHeight(CommUtil.null2Int(map2
							.get("height")));
					store_weixin_logo.setAddTime(new Date());
					this.accessoryService.update(store_weixin_logo);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.storeService.update(store);
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("op_title", "微信商城信息保存成功");
		mv.addObject("url", CommUtil.getURL(request)
				+ "/seller/weixin_store.htm");
		return mv;
	}

	@SecurityMapping(title = "微信商城", value = "/seller/weixin_store_buy.htm*", rtype = "seller", rname = "微信商城", rcode = "weixin_store_seller", rgroup = "微信管理")
	@RequestMapping("/seller/weixin_store_buy.htm")
	public ModelAndView weixin_store_buy(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/weixin_store_buy.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		String developer_url = CommUtil.getURL(request)
				+ "/seller/wexin_store_action.htm?store_id="
				+ user.getStore().getId();
		mv.addObject("developer_url", developer_url);
		mv.addObject("store", user.getStore());
		mv.addObject("user", user);
		return mv;
	}

	@SecurityMapping(title = "微信商城购买保存", value = "/seller/weixin_store_buy_save.htm*", rtype = "seller", rname = "微信商城", rcode = "weixin_store_seller", rgroup = "微信管理")
	@RequestMapping("/seller/weixin_store_buy_save.htm")
	public String weixin_store_buy_save(HttpServletRequest request,
			HttpServletResponse response, String count) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		int gold = user.getGold();
		int weixin_gold = CommUtil.null2Int(count)
				* this.configService.getSysConfig().getWeixin_amount();
		if (gold >= weixin_gold) {
			// 扣除用户金币
			user.setGold(gold - weixin_gold);
			this.userService.update(user);
			// 记录金币日志
			GoldLog log = new GoldLog();
			log.setAddTime(new Date());
			log.setGl_content("购买微信商城");
			log.setGl_count(weixin_gold);
			log.setGl_user(user);
			log.setGl_type(-1);
			this.goldLogService.save(log);
			// 设置店铺的买就送套餐信息
			Store store = user.getStore();
			if (store.getWeixin_begin_time() == null) {
				store.setWeixin_begin_time(new Date());
			}
			Calendar cal = Calendar.getInstance();
			if (store.getWeixin_end_time() != null) {
				cal.setTime(store.getWeixin_end_time());
			}
			cal.add(Calendar.MONTH, CommUtil.null2Int(count));
			store.setWeixin_end_time(cal.getTime());
			store.setWeixin_status(1);
			this.storeService.update(store);
			// 保存套餐购买信息
			VLog v_log = new VLog();
			v_log.setAddTime(new Date());
			v_log.setBegin_time(new Date());
			v_log.setEnd_time(cal.getTime());
			v_log.setGold(weixin_gold);
			v_log.setStore(store);
			this.vLogService.save(v_log);
			return "redirect:weixin_store_buy_success.htm";
		} else {
			return "redirect:weixin_store_buy_error.htm";
		}

	}

	@SecurityMapping(title = "微信商城购买成功", value = "/seller/weixin_store_buy_success.htm*", rtype = "seller", rname = "微信商城", rcode = "weixin_store_seller", rgroup = "微信管理")
	@RequestMapping("/seller/weixin_store_buy_success.htm")
	public ModelAndView weixin_store_buy_success(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("op_title", "微信商城购买成功");
		mv.addObject("url", CommUtil.getURL(request)
				+ "/seller/weixin_store.htm");
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "微信商城购买失败", value = "/seller/weixin_store_buy_error.htm*", rtype = "seller", rname = "微信商城", rcode = "weixin_store_seller", rgroup = "微信管理")
	@RequestMapping("/seller/weixin_store_buy_error.htm")
	public ModelAndView weixin_store_buy_error(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("error.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("op_title", "金币不足，购买失败");
		mv.addObject("url", CommUtil.getURL(request)
				+ "/seller/weixin_store.htm");
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "微信商城购买日志", value = "/seller/weixin_store_log.htm*", rtype = "seller", rname = "微信商城", rcode = "weixin_store_seller", rgroup = "微信管理")
	@RequestMapping("/seller/weixin_store_log.htm")
	public ModelAndView weixin_store_log(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/weixin_store_log.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		VLogQueryObject qo = new VLogQueryObject(currentPage, mv, orderBy,
				orderType);
		qo.addQuery("obj.store.id", new SysMap("store_id", user.getStore()
				.getId()), "=");
		IPageList pList = this.vLogService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("store", user.getStore());
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "微信商城菜单配置页面", value = "/seller/weixin_store_menu.htm*", rtype = "seller", rname = "微信商城", rcode = "weixin_store_seller", rgroup = "微信管理")
	@RequestMapping("/seller/weixin_store_menu.htm")
	public ModelAndView weixin_store_menu(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/weixin_store_menu.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		mv.addObject("store", user.getStore());
		Map params = new HashMap();
		params.put("store_id", user.getStore().getId());
		List<VMenu> vmenus = this.vMenuService
				.query("select obj from VMenu obj where obj.store.id=:store_id  and obj.parent.id is null order by obj.menu_sequence asc",
						params, -1, -1);
		mv.addObject("vmenus", vmenus);
		return mv;
	}

	@SecurityMapping(title = "微信商城菜单配置", value = "/seller/weixin_store_menu_add.htm*", rtype = "seller", rname = "微信商城", rcode = "weixin_store_seller", rgroup = "微信管理")
	@RequestMapping("/seller/weixin_store_menu_add.htm")
	public ModelAndView weixin_store_menu_add(HttpServletRequest request,
			HttpServletResponse response, String menu_id, String pmenu_id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/weixin_store_menu_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		mv.addObject("store", user.getStore());
		VMenu obj = this.vMenuService.getObjById(CommUtil.null2Long(menu_id));
		mv.addObject("obj", obj);
		mv.addObject("pmenu_id", obj == null ? pmenu_id
				: (obj.getParent() != null ? obj.getParent().getId() : ""));
		return mv;
	}

	@SecurityMapping(title = "微信商城菜单保存", value = "/seller/weixin_store_menu_save.htm*", rtype = "seller", rname = "微信商城", rcode = "weixin_store_seller", rgroup = "微信管理")
	@RequestMapping("/seller/weixin_store_menu_save.htm")
	public String weixin_store_menu_save(HttpServletRequest request,
			HttpServletResponse response, String menu_id, String pmenu_id)
			throws IOException {
		WebForm wf = new WebForm();
		Store store = this.userService.getObjById(
				SecurityUserHolder.getCurrentUser().getId()).getStore();
		VMenu parent = this.vMenuService.getObjById(CommUtil
				.null2Long(pmenu_id));
		if (!CommUtil.null2String(menu_id).equals("")) {
			VMenu obj = this.vMenuService.getObjById(CommUtil
					.null2Long(menu_id));
			obj = (VMenu) wf.toPo(request, obj);
			obj.setStore(store);
			obj.setParent(parent);
			this.vMenuService.update(obj);
		} else {
			VMenu obj = wf.toPo(request, VMenu.class);
			obj.setStore(store);
			obj.setParent(parent);
			this.vMenuService.save(obj);
		}
		return "redirect:weixin_store_menu.htm";
	}

	@SecurityMapping(title = "微信商城菜单删除", value = "/seller/weixin_store_menu_delete.htm*", rtype = "seller", rname = "微信商城", rcode = "weixin_store_seller", rgroup = "微信管理")
	@RequestMapping("/seller/weixin_store_menu_delete.htm")
	public String weixin_store_menu_delete(HttpServletRequest request,
			HttpServletResponse response, String menu_id) throws IOException {
		if (!CommUtil.null2String(menu_id).equals("")) {
			this.vMenuService.delete(CommUtil.null2Long(menu_id));
		}
		return "redirect:weixin_store_menu.htm";
	}

	@SecurityMapping(title = "微信菜单创建", value = "/seller/weixin_store_menu_create.htm*", rtype = "seller", rname = "微信商城", rcode = "weixin_store_seller", rgroup = "微信管理")
	@RequestMapping("/seller/weixin_store_menu_create.htm")
	public void weixin_store_menu_create(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		int ret = this.createMenu();
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getAccess_token() {
		// 获得ACCESS_TOKEN
		Store store = this.userService.getObjById(
				SecurityUserHolder.getCurrentUser().getId()).getStore();
		String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
				+ store.getWeixin_appId()
				+ "&secret="
				+ store.getWeixin_appSecret();
		String accessToken = null;
		try {
			URL urlGet = new URL(url);
			HttpURLConnection http = (HttpURLConnection) urlGet
					.openConnection();
			http.setRequestMethod("GET"); // 必须是get方式请求
			http.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			http.setDoOutput(true);
			http.setDoInput(true);
			System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
			System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
			http.connect();
			InputStream is = http.getInputStream();
			int size = is.available();
			byte[] jsonBytes = new byte[size];
			is.read(jsonBytes);
			String message = new String(jsonBytes, "UTF-8");
			Map map = Json.fromJson(HashMap.class, message);
			accessToken = CommUtil.null2String(map.get("access_token"));
			// System.out.println("message：" + message);
			// System.out.println("accessToken：" + accessToken);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accessToken;
	}

	private int createMenu() throws IOException {
		int ret = 0;
		Store store = this.userService.getObjById(
				SecurityUserHolder.getCurrentUser().getId()).getStore();
		Map weixin_store_menu = new HashMap();
		List<Weixin_Menu> list = new ArrayList<Weixin_Menu>();
		Map params = new HashMap();
		params.put("store_id", store.getId());
		List<VMenu> vmenus = this.vMenuService
				.query("select obj from VMenu obj where obj.store.id=:store_id and obj.parent.id is null order by obj.menu_sequence asc",
						params, -1, -1);
		for (VMenu vmenu : vmenus) {
			Weixin_Menu menu = new Weixin_Menu();
			menu.setKey(vmenu.getMenu_key());
			menu.setName(vmenu.getMenu_name());
			menu.setType(vmenu.getMenu_type());
			menu.setUrl(vmenu.getMenu_url());
			for (VMenu c_vmenu : vmenu.getChilds()) {
				Weixin_Menu c_menu = new Weixin_Menu();
				c_menu.setKey(c_vmenu.getMenu_key());
				c_menu.setName(c_vmenu.getMenu_name());
				c_menu.setType(c_vmenu.getMenu_type());
				c_menu.setUrl(c_vmenu.getMenu_url());
				menu.getSub_button().add(c_menu);
			}
			list.add(menu);
		}
		weixin_store_menu.put("button", list);
		System.out
				.println(Json.toJson(weixin_store_menu, JsonFormat.compact()));
		String user_define_menu = Json.toJson(weixin_store_menu,
				JsonFormat.compact());
		String access_token = getAccess_token();
		String action = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token="
				+ access_token;
		try {
			URL url = new URL(action);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			http.setDoOutput(true);
			http.setDoInput(true);
			System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
			System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
			http.connect();
			OutputStream os = http.getOutputStream();
			os.write(user_define_menu.getBytes("UTF-8"));// 传入参数
			os.flush();
			os.close();
			InputStream is = http.getInputStream();
			int size = is.available();
			byte[] jsonBytes = new byte[size];
			is.read(jsonBytes);
			String message = new String(jsonBytes, "UTF-8");
			System.out.println("menu:" + message);
			Map ret_map = Json.fromJson(HashMap.class, message);
			ret = CommUtil.null2Int(ret_map.get("errcode"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	@SecurityMapping(title = "微信消息", value = "/seller/weixin_msg.htm*", rtype = "seller", rname = "微信消息", rcode = "weixin_store_seller", rgroup = "微信管理")
	@RequestMapping("/seller/weixin_msg.htm")
	public ModelAndView weixin_msg(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/weixin_msg.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		VMessageQueryObject qo = new VMessageQueryObject(currentPage, mv,
				orderBy, orderType);
		IPageList pList = this.vmessageService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	@SecurityMapping(title = "微信消息回复", value = "/seller/weixin_msg_reply.htm*", rtype = "seller", rname = "微信消息", rcode = "weixin_store_seller", rgroup = "微信管理")
	@RequestMapping("/seller/weixin_msg_reply.htm")
	public void weixin_msg_reply(HttpServletRequest request,
			HttpServletResponse response, String id, String reply)
			throws IOException {
		boolean flag = false;
		try {
			VMessage vmsg = this.vmessageService.getObjById(CommUtil
					.null2Long(id));
			vmsg.setReply(reply);
			vmsg.setStatus(1);
			flag = this.vmessageService.update(vmsg);
			if (flag) {
				this.weixin_msg_send(reply, CommUtil.null2String(vmsg.getId()));
			}
			PrintWriter writer;
			writer = response.getWriter();
			writer.print(flag);
		} catch (IOException e1) {
			PrintWriter writer;
			writer = response.getWriter();
			writer.print(flag);
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	// 向微信服务器发送信息，调用微信客服回复信息接口
	private void weixin_msg_send(String reply, String vmsg_id)
			throws HttpException, IOException {
		String access_token = this.weixin_getAccess_token(vmsg_id);
		if (access_token != null && !access_token.equals("")) {
			String send_url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="
					+ access_token;
			System.out.println("access_token:" + access_token);
			VMessage vmsg = this.vmessageService.getObjById(CommUtil
					.null2Long(vmsg_id));
			String content = "{content:" + vmsg.getReply() + "}";
			// 构造HttpClient的实例
			HttpClient httpClient = new HttpClient();
			// 创建post方法的实例
			PostMethod postMethod = new PostMethod(send_url);
			// 将发送信息转换成json数据
			Map map = new HashMap();
			map.put("touser", vmsg.getFromUserName());
			map.put("msgtype", vmsg.getMsgType());
			map.put("text", "{'content':" + vmsg.getMsgType() + "}");
			String date_json = Json.toJson(map, JsonFormat.compact());
			// 将表单的值放入postMethod中
			postMethod.setRequestBody(date_json);
			// 执行postMethod
			int statusCode = httpClient.executeMethod(postMethod);
			// 读取内容
			byte[] responseBody = postMethod.getResponseBody();
			// 处理内容
			HashMap map_msg = Json.fromJson(HashMap.class, new String(
					responseBody));
			String msg = CommUtil.null2String(map_msg.get("errmsg"));
			System.out.println(msg);

		}
	}

	// 获取access_token
	private String weixin_getAccess_token(String vmsg_id) {
		VMessage vmsg = this.vmessageService.getObjById(CommUtil
				.null2Long(vmsg_id));
		String access_token = "";
		if (vmsg.getStore() != null && !vmsg.getStore().equals("")) {
			String token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
					+ vmsg.getStore().getWeixin_appId()
					+ "&secret="
					+ vmsg.getStore().getWeixin_appSecret();
			// 构造HttpClient的实例
			HttpClient httpClient = new HttpClient();
			// 创建GET方法的实例
			GetMethod getMethod = new GetMethod(token_url);
			// 使用系统提供的默认的恢复策略
			getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler());
			try {
				// 执行getMethod
				int statusCode = httpClient.executeMethod(getMethod);
				if (statusCode != HttpStatus.SC_OK) {
					System.err.println("Method failed: "
							+ getMethod.getStatusLine());
				}
				// 读取内容
				byte[] responseBody = getMethod.getResponseBody();
				// 处理内容
				HashMap map = Json.fromJson(HashMap.class, new String(
						responseBody));
				access_token = CommUtil.null2String(map.get("access_token"));
			} catch (HttpException e) {
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				System.out.println("Please check your provided http address!");
				e.printStackTrace();
			} catch (IOException e) {
				// 发生网络异常
				e.printStackTrace();
			} finally {
				// 释放连接
				getMethod.releaseConnection();
			}
		}
		return access_token;
	}

	public class Weixin_Menu {
		private String type;// click或者view
		private String name;// 菜单名称
		private String key;// 菜单key
		private String url;// 菜单url
		private List<Weixin_Menu> sub_button = new ArrayList<Weixin_Menu>();// 子菜单

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public List<Weixin_Menu> getSub_button() {
			return sub_button;
		}

		public void setSub_button(List<Weixin_Menu> sub_button) {
			this.sub_button = sub_button;
		}

	}
}
