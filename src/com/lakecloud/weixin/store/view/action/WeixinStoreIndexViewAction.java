package com.lakecloud.weixin.store.view.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.lakecloud.foundation.domain.GoodsCart;
import com.lakecloud.foundation.domain.IntegralLog;
import com.lakecloud.foundation.domain.Role;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.StoreCart;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.domain.UserGoodsClass;
import com.lakecloud.foundation.service.IAlbumService;
import com.lakecloud.foundation.service.IGoodsBrandService;
import com.lakecloud.foundation.service.IGoodsCartService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IGroupGoodsService;
import com.lakecloud.foundation.service.IIntegralLogService;
import com.lakecloud.foundation.service.IRoleService;
import com.lakecloud.foundation.service.IStoreCartService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserGoodsClassService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.weixin.domain.VMenu;
import com.lakecloud.weixin.domain.VMessage;
import com.lakecloud.weixin.service.IVMenuService;
import com.lakecloud.weixin.service.IVMessageService;
import com.lakecloud.weixin.tools.WeixinTools;

/**
 * @info 微信店铺客户端首页处理控制器
 * @since V1.3
 * @version 1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang hz
 * 
 */
@Controller
public class WeixinStoreIndexViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IVMenuService vMenuService;
	@Autowired
	private WeixinTools weixinTools;
	@Autowired
	private IStoreCartService storeCartService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IUserGoodsClassService userGoodsClassService;
	@Autowired
	private IGoodsBrandService brandService;
	@Autowired
	private IGroupGoodsService groupgoodsService;
	@Autowired
	private IVMessageService wxmsgService;

	/**
	 * 计算并合并购车信息
	 * 
	 * @param request
	 * @return
	 */
	private List<StoreCart> cart_calc(HttpServletRequest request) {
		List<StoreCart> cart = new ArrayList<StoreCart>();// 整体店铺购物车
		List<StoreCart> user_cart = new ArrayList<StoreCart>();// 当前用户未提交订单的店铺购物车
		List<StoreCart> cookie_cart = new ArrayList<StoreCart>();// 当前cookie指向的店铺购物车
		User user = null;
		if (SecurityUserHolder.getCurrentUser() != null) {
			user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
		}
		String cart_session_id = "";
		Map params = new HashMap();
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("cart_session_id")) {
					cart_session_id = CommUtil.null2String(cookie.getValue());
				}
			}
		}
		if (user != null) {
			if (!cart_session_id.equals("")) {
				// 如果用户拥有自己的店铺，删除购物车中自己店铺中的商品信息
				if (user.getStore() != null) {
					params.clear();
					params.put("cart_session_id", cart_session_id);
					params.put("user_id", user.getId());
					params.put("sc_status", 0);
					params.put("store_id", user.getStore().getId());
					List<StoreCart> store_cookie_cart = this.storeCartService
							.query("select obj from StoreCart obj where (obj.cart_session_id=:cart_session_id or obj.user.id=:user_id) and obj.sc_status=:sc_status and obj.store.id=:store_id",
									params, -1, -1);
					for (StoreCart sc : store_cookie_cart) {
						for (GoodsCart gc : sc.getGcs()) {
							gc.getGsps().clear();
							this.goodsCartService.delete(gc.getId());
						}
						this.storeCartService.delete(sc.getId());
					}
				}
				// 查询出cookie中的商品信息
				params.clear();
				params.put("cart_session_id", cart_session_id);
				params.put("sc_status", 0);
				cookie_cart = this.storeCartService
						.query("select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
								params, -1, -1);
				// 查询用户未提交订单的购物车信息
				params.clear();
				params.put("user_id", user.getId());
				params.put("sc_status", 0);
				user_cart = this.storeCartService
						.query("select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status",
								params, -1, -1);
			} else {
				// 查询用户未提交订单的购物车信息
				params.clear();
				params.put("user_id", user.getId());
				params.put("sc_status", 0);
				user_cart = this.storeCartService
						.query("select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status",
								params, -1, -1);

			}
		} else {
			// 查询cookie中保存的购物车信息
			if (!cart_session_id.equals("")) {
				params.clear();
				params.put("cart_session_id", cart_session_id);
				params.put("sc_status", 0);
				cookie_cart = this.storeCartService
						.query("select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
								params, -1, -1);
			}
		}

		// 合并当前用户未提交订单的店铺购物车和当前cookie指向的店铺购物车
		for (StoreCart sc : user_cart) {
			boolean sc_add = true;
			for (StoreCart sc1 : cart) {
				if (sc1.getStore().getId().equals(sc.getStore().getId())) {
					sc_add = false;
				}
			}
			if (sc_add) {
				cart.add(sc);
			}
		}
		for (StoreCart sc : cookie_cart) {
			boolean sc_add = true;
			for (StoreCart sc1 : cart) {
				if (sc1.getStore().getId().equals(sc.getStore().getId())) {
					sc_add = false;
				}
			}
			if (sc_add) {
				cart.add(sc);
			}
		}
		return cart;
	}

	/**
	 * 微信开发URL，所有用户信息及
	 * 
	 * @param request
	 * @param response
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @param echostr
	 * @param store_id
	 */
	@RequestMapping("/weixin_store_action.htm")
	public void weixin_store_action(HttpServletRequest request,
			HttpServletResponse response, String signature, String timestamp,
			String nonce, String echostr, String store_id) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/xml");
		Store store = this.storeService
				.getObjById(CommUtil.null2Long(store_id));
		if (store.getWeixin_status() == 1) {// 微信店铺在开通的情况下运行访问
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(ServletInputStream) request.getInputStream(), "UTF-8"));
				String line = null;
				StringBuilder sb = new StringBuilder();
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				Map<String, String> map = this.weixinTools.parse_xml(sb
						.toString());
				String ToUserName = map.get("ToUserName");
				String FromUserName = map.get("FromUserName");
				String CreateTime = map.get("CreateTime");
				String MsgType = map.get("MsgType");
				String Content = CommUtil.null2String(map.get("Content"));
				String MsgId = map.get("MsgId");
				String Event = CommUtil.null2String(map.get("Event"));
				String EventKey = CommUtil.null2String(map.get("EventKey"));
				String reply_xml = "";
				String reply_title = "";
				String reply_content = "";
				String reply_bottom = "";
				String web_url = CommUtil.getURL(request);
				String reply_all = store.getWeixin_welecome_content();
				int num = 1;
				if (Event.equals("")) {
					if (Content.equals("0")) {
						reply_all = "<a href='" + web_url
								+ "/weixin/index.htm?store_id=" + store_id
								+ "'>\ue022点击进入微信商城\ue022</a>";
					}
					if (Content.equals("1")) {
						reply_title = "推荐商品\ue022";
						reply_bottom = "\n\n<a href='" + web_url
								+ "/weixin/goods_recommend_list.htm?store_id="
								+ store_id + "'>点击查看更多\ue23c</a>";
						Map params = new HashMap();
						params.put("store_id", store.getId());
						params.put("goods_status", 0);
						params.put("goods_recommend", true);
						List<Goods> goods_list = this.goodsService
								.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_recommend=:goods_recommend order by obj.addTime desc",
										params, 0, 5);
						for (Goods goods : goods_list) {
							reply_content = reply_content + "<a href='"
									+ web_url + "/weixin/goods.htm?goods_id="
									+ goods.getId() + "'>" + num + "、"
									+ goods.getGoods_name() + "</a>\n\n";
							num++;
						}
						reply_all = reply_title + "\n" + reply_content + "\n"
								+ reply_bottom;
					}
					if (Content.equals("2")) {
						reply_title = "热卖商品\ue022";
						reply_bottom = "\n\n<a href='" + web_url
								+ "/weixin/goods_list.htm?store_id=" + store_id
								+ "&queryType=goods_salenum'>点击查看更多\ue23c</a>";
						Map params = new HashMap();
						params.put("store_id", store.getId());
						params.put("goods_status", 0);
						List<Goods> goods_list = this.goodsService
								.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_salenum desc",
										params, 0, 5);
						for (Goods goods : goods_list) {
							reply_content = reply_content + "<a href='"
									+ web_url + "/weixin/goods.htm?goods_id="
									+ goods.getId() + "'>" + num + "、"
									+ goods.getGoods_name() + "</a>\n";
							num++;
						}
						reply_all = reply_title + "\n" + reply_content + "\n"
								+ reply_bottom;
					}
					if (Content.equals("3")) {
						reply_title = "新品上市\ue022";
						reply_bottom = "\n\n<a href='" + web_url
								+ "/weixin/goods_list.htm?store_id=" + store_id
								+ "&queryType=addTime'>点击查看更多\ue23c</a>";
						Map params = new HashMap();
						params.put("store_id", store.getId());
						params.put("goods_status", 0);
						List<Goods> goods_list = this.goodsService
								.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.addTime desc",
										params, 0, 5);
						for (Goods goods : goods_list) {
							reply_content = reply_content + "<a href='"
									+ web_url + "/weixin/goods.htm?goods_id="
									+ goods.getId() + "'>" + num + "、"
									+ goods.getGoods_name() + "</a>\n";
							num++;
						}
						reply_all = reply_title + "\n" + reply_content + "\n"
								+ reply_bottom;
					}
					if (Content.equals("4")) {
						reply_title = "商品分类\ue022";
						reply_bottom = "\n\n<a href='" + web_url
								+ "/weixin/classes_first_list.htm?store_id="
								+ store_id + "'>点击查看更多\ue23c</a>";
						Map params = new HashMap();
						params.put("uid", store.getUser().getId());
						List<UserGoodsClass> usergoodsClasses = this.userGoodsClassService
								.query("select obj from UserGoodsClass obj where obj.user.id=:uid and obj.parent is null",
										params, 0, 10);
						for (UserGoodsClass ugc : usergoodsClasses) {
							reply_content = reply_content
									+ "<a href='"
									+ web_url
									+ "/weixin/classes_second_list.htm?class_id="
									+ ugc.getId() + "'>" + num + "、"
									+ ugc.getClassName() + "</a>\n";
							num++;
						}
						reply_all = reply_title + "\n" + reply_content + "\n"
								+ reply_bottom;
					}
					VMessage wxmsg = new VMessage();
					wxmsg.setAddTime(new Date());
					wxmsg.setFromUserName(FromUserName);
					wxmsg.setContent(Content);
					wxmsg.setStore(store);
					this.wxmsgService.save(wxmsg);
					// 微信注册用户
					if (Content.indexOf("注册") == 0) {
						String[] list = Content.split("#");// 注册#erikzhang#123456#erikzhang@163.com
						if (list.length == 4) {
							String userName = CommUtil.null2String(list[1]);
							String password = CommUtil.null2String(list[2]);
							String email = CommUtil.null2String(list[3]);
							Map params = new HashMap();
							params.put("userName", userName);
							List<User> users = this.userService
									.query("select obj from User obj where obj.userName=:userName",
											params, -1, -1);
							int reg = 0;
							if (users != null && users.size() > 0) {
								reg = 1;
							}
							if(reg==0){
								if(!CommUtil.checkEmail(email)){
									reg=2;
								}
							}
							if (reg == 0) {
								params.clear();
								params.put("email", email);
								users = this.userService
										.query("select obj from User obj where obj.email=:email",
												params, -1, -1);
								if (users != null && users.size() > 0) {
									reg = 3;
								}
							}
							if (reg == 0) {// 完成用户注册
								User user = new User();
								user.setUserName(userName);
								user.setUserRole("BUYER");
								user.setAddTime(new Date());
								user.setEmail(email);
								user.setPassword(Md5Encrypt.md5(password)
										.toLowerCase());
								params.clear();
								params.put("type", "BUYER");
								List<Role> roles = this.roleService
										.query("select obj from Role obj where obj.type=:type",
												params, -1, -1);
								user.getRoles().addAll(roles);
								if (this.configService.getSysConfig()
										.isIntegral()) {
									user.setIntegral(this.configService
											.getSysConfig().getMemberRegister());
									this.userService.save(user);
									IntegralLog log = new IntegralLog();
									log.setAddTime(new Date());
									log.setContent("用户注册增加"
											+ this.configService.getSysConfig()
													.getMemberRegister() + "分");
									log.setIntegral(this.configService
											.getSysConfig().getMemberRegister());
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
								reply_all = "用户名注册成功，您可以使用刚刚注册的用户名及密码登录微商城，感谢您的支持！";
							}
							if (reg == 1) {
								reply_all = "用户名已经存在，请更换用户名";
							}
							if (reg == 2) {
								reply_all = "邮箱格式不对，如:zhangsan@kuh889.cn";
							}
							if (reg == 3) {
								reply_all = "邮箱已经存在，请更换邮箱";
							}
						} else {
							reply_all = "格式错误，格式如：注册#用户名#密码#邮箱";
						}
					}
				} else {
					if (Event.equals("subscribe")) {// 订阅，回复欢迎词
						reply_all = store.getWeixin_welecome_content();
					}
					if (Event.equals("unsubscribe")) {// 取消订阅，不需要回复任何内容
					}
					if (Event.equals("click") || Event.equals("CLICK")) {// 返回菜单key对应的内容
						Map menu_map = new HashMap();
						menu_map.put("menu_key", EventKey);
						menu_map.put("store_id", store.getId());
						List<VMenu> vMeuns = this.vMenuService
								.query("select obj from VMenu obj where obj.store.id=:store_id and obj.menu_key=:menu_key",
										menu_map, -1, -1);
						if (vMeuns.size() > 0) {
							reply_all = vMeuns.get(0).getMenu_key_content();
						}
					}
				}
				reply_xml = this.weixinTools.reply_xml(MsgType, map, reply_all);
				PrintWriter writer;
				writer = response.getWriter();
				System.out.println(response);
				if (echostr != null && !echostr.equals("")) {
					writer.print(echostr);
				} else
					writer.print(reply_xml);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			PrintWriter writer;
			try {
				writer = response.getWriter();
				writer.print("");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 微信店铺首页
	 * 
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@RequestMapping("/weixin/index.htm")
	public ModelAndView weixin_index(HttpServletRequest request,
			HttpServletResponse response, String store_id) {
		ModelAndView mv = null;
		Store store = this.storeService
				.getObjById(CommUtil.null2Long(store_id));
		if (store.getWeixin_status() == 1) {
			mv = new JModelAndView("weixin/index.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			Map params = new HashMap();
			params.put("store_id", CommUtil.null2Long(store_id));
			request.getSession(false).setAttribute("lakecloud_view_type",
					"weixin");
			request.getSession(false).setAttribute("store_id", store_id);
			params.put("goods_status", 0);
			List<Goods> hot_goods_list = this.goodsService
					.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_salenum desc",
							params, 0, 6);
			List<Goods> new_goods_list = this.goodsService
					.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.addTime desc",
							params, 0, 3);
			params.put("goods_recommend", true);
			List<Goods> recommend_goods_list = this.goodsService
					.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_recommend=:goods_recommend order by obj.addTime desc",
							params, 0, 3);
			mv.addObject("hot_goods_list", hot_goods_list);
			mv.addObject("new_goods_list", new_goods_list);
			mv.addObject("recommend_goods_list", recommend_goods_list);
			mv.addObject("store_id", store_id);
			return mv;
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "未开通微信店铺");
			return mv;
		}
	}

	@RequestMapping("/weixin/footer.htm")
	public ModelAndView weixin_footer(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/footer.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);

		return mv;
	}

	@RequestMapping("/weixin/header.htm")
	public ModelAndView weixin_head(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/header.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String store_id = CommUtil
				.null2String(request.getAttribute("store_id"));
		mv.addObject("store_id", store_id);
		return mv;
	}

	@RequestMapping("/weixin/nav_bottom.htm")
	public ModelAndView weixin_nav_bottom(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/nav_bottom.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String store_id = CommUtil.null2String(request.getSession(false)
				.getAttribute("store_id"));
		Store store = this.storeService
				.getObjById(CommUtil.null2Long(store_id));
		List<GoodsCart> list = new ArrayList<GoodsCart>();
		List<StoreCart> cart = new ArrayList<StoreCart>();// 整体店铺购物车
		List<StoreCart> user_cart = new ArrayList<StoreCart>();// 当前用户未提交订单的店铺购物车
		List<StoreCart> cookie_cart = new ArrayList<StoreCart>();// 当前cookie指向的店铺购物车
		User user = null;
		if (SecurityUserHolder.getCurrentUser() != null) {
			user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
		}
		String cart_session_id = "";
		Map params = new HashMap();
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("cart_session_id")) {
					cart_session_id = CommUtil.null2String(cookie.getValue());
				}
			}
		}
		if (user != null) {
			if (!cart_session_id.equals("")) {
				// 如果用户拥有自己的店铺，删除购物车中自己店铺中的商品信息
				if (user.getStore() != null) {
					params.clear();
					params.put("cart_session_id", cart_session_id);
					params.put("user_id", user.getId());
					params.put("sc_status", 0);
					params.put("store_id", user.getStore().getId());
					List<StoreCart> store_cookie_cart = this.storeCartService
							.query("select obj from StoreCart obj where (obj.cart_session_id=:cart_session_id or obj.user.id=:user_id) and obj.sc_status=:sc_status and obj.store.id=:store_id",
									params, -1, -1);
					for (StoreCart sc : store_cookie_cart) {
						for (GoodsCart gc : sc.getGcs()) {
							gc.getGsps().clear();
							this.goodsCartService.delete(gc.getId());
						}
						this.storeCartService.delete(sc.getId());
					}
				}
				// 查询出cookie中的商品信息
				params.clear();
				params.put("cart_session_id", cart_session_id);
				params.put("sc_status", 0);
				cookie_cart = this.storeCartService
						.query("select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
								params, -1, -1);
				// 查询用户未提交订单的购物车信息
				params.clear();
				params.put("user_id", user.getId());
				params.put("sc_status", 0);
				user_cart = this.storeCartService
						.query("select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status",
								params, -1, -1);
			} else {
				// 查询用户未提交订单的购物车信息
				params.clear();
				params.put("user_id", user.getId());
				params.put("sc_status", 0);
				user_cart = this.storeCartService
						.query("select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status",
								params, -1, -1);

			}
		} else {
			if (!cart_session_id.equals("")) {
				params.clear();
				params.put("cart_session_id", cart_session_id);
				params.put("sc_status", 0);
				cookie_cart = this.storeCartService
						.query("select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
								params, -1, -1);
			}
		}
		// 合并当前用户未提交订单的店铺购物车和当前cookie指向的店铺购物车
		for (StoreCart sc : user_cart) {
			boolean sc_add = true;
			for (StoreCart sc1 : cart) {
				if (sc1.getStore().getId().equals(sc.getStore().getId())) {
					sc_add = false;
				}
			}
			if (sc_add) {
				cart.add(sc);
			}
		}
		for (StoreCart sc : cookie_cart) {
			boolean sc_add = true;
			for (StoreCart sc1 : cart) {
				if (sc1.getStore().getId().equals(sc.getStore().getId())) {
					sc_add = false;
					for (GoodsCart gc : sc.getGcs()) {
						gc.setSc(sc1);
						this.goodsCartService.update(gc);
					}
					this.storeCartService.delete(sc.getId());
				}
			}
			if (sc_add) {
				cart.add(sc);
			}
		}
		if (cart != null) {
			for (StoreCart sc : cart) {
				if (sc != null) {
					list.addAll(sc.getGcs());
				}
			}
		}
		float total_price = 0;
		for (GoodsCart gc : list) {
			Goods goods = this.goodsService.getObjById(gc.getGoods().getId());
			if (CommUtil.null2String(gc.getCart_type()).equals("combin")) {
				total_price = CommUtil.null2Float(goods.getCombin_price());
			} else {
				total_price = CommUtil.null2Float(CommUtil.mul(gc.getCount(),
						goods.getGoods_current_price())) + total_price;
			}
		}
		mv.addObject("cart_size", list.size());
		String op = CommUtil.null2String(request.getAttribute("op"));
		if (op != null && !op.equals("")) {
			mv.addObject("op", op);
		}
		mv.addObject("store", store);
		return mv;
	}

	@RequestMapping("/weixin/search.htm")
	public ModelAndView weixin_search(HttpServletRequest request,
			HttpServletResponse response, String keyword) {
		ModelAndView mv = new JModelAndView("weixin/search.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("keyword", keyword);
		return mv;
	}
}
