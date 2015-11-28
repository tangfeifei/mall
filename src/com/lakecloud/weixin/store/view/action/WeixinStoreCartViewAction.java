package com.lakecloud.weixin.store.view.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.domain.virtual.SysMap;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.WebForm;
import com.lakecloud.foundation.domain.Address;
import com.lakecloud.foundation.domain.Area;
import com.lakecloud.foundation.domain.CouponInfo;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsCart;
import com.lakecloud.foundation.domain.GoodsSpecProperty;
import com.lakecloud.foundation.domain.GroupGoods;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.OrderFormLog;
import com.lakecloud.foundation.domain.Payment;
import com.lakecloud.foundation.domain.PredepositLog;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.StoreCart;
import com.lakecloud.foundation.domain.Template;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.IAddressService;
import com.lakecloud.foundation.service.IAreaService;
import com.lakecloud.foundation.service.ICouponInfoService;
import com.lakecloud.foundation.service.IGoodsCartService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IGoodsSpecPropertyService;
import com.lakecloud.foundation.service.IGroupGoodsService;
import com.lakecloud.foundation.service.IOrderFormLogService;
import com.lakecloud.foundation.service.IOrderFormService;
import com.lakecloud.foundation.service.IPaymentService;
import com.lakecloud.foundation.service.IPredepositLogService;
import com.lakecloud.foundation.service.IStoreCartService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.ITemplateService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.manage.admin.tools.MsgTools;
import com.lakecloud.manage.admin.tools.PaymentTools;
import com.lakecloud.manage.seller.Tools.TransportTools;
import com.lakecloud.pay.tools.PayTools;
import com.lakecloud.view.web.tools.GoodsViewTools;

/**
 * 
* <p>Title: WeixinStoreCartViewAction.java</p>

* <p>Description:微信店铺购物控制器,包括购物车所有操作及订单相关操作 </p>
* @version LakeCloud_C2C 1.3
 */
@Controller
public class WeixinStoreCartViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsSpecPropertyService goodsSpecPropertyService;
	@Autowired
	private IAddressService addressService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private IStoreCartService storeCartService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private PaymentTools paymentTools;
	@Autowired
	private PayTools payTools;
	@Autowired
	private TransportTools transportTools;
	@Autowired
	private GoodsViewTools goodsViewTools;

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
	 * 根据商品规格加载商品的数量、价格
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param gsp
	 */
	@RequestMapping("/weixin/add_goods_cart.htm")
	public void weixin_add_goods_cart(HttpServletRequest request,
			HttpServletResponse response, String id, String count,
			String price, String gsp, String buy_type) {
		String cart_session_id = "";
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("cart_session_id")) {
					cart_session_id = CommUtil.null2String(cookie.getValue());
				}
			}
		}

		if (cart_session_id.equals("")) {
			cart_session_id = UUID.randomUUID().toString();
			Cookie cookie = new Cookie("cart_session_id", cart_session_id);
			cookie.setDomain(CommUtil.generic_domain(request));
			response.addCookie(cookie);
		}
		List<StoreCart> cart = new ArrayList<StoreCart>();// 整体店铺购物车
		List<StoreCart> user_cart = new ArrayList<StoreCart>();// 当前用户未提交订单的店铺购物车
		List<StoreCart> cookie_cart = new ArrayList<StoreCart>();// 当前cookie指向的店铺购物车
		User user = null;
		if (SecurityUserHolder.getCurrentUser() != null) {
			user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
		}
		Map params = new HashMap();
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
		// 购物车查询合并完毕
		String[] gsp_ids = gsp.split(",");
		Arrays.sort(gsp_ids);
		boolean add = true;
		double total_price = 0;
		int total_count = 0;
		for (StoreCart sc : cart) {
			for (GoodsCart gc : sc.getGcs()) {
				if (gsp_ids != null && gsp_ids.length > 0
						&& gc.getGsps() != null && gc.getGsps().size() > 0) {
					String[] gsp_ids1 = new String[gc.getGsps().size()];
					for (int i = 0; i < gc.getGsps().size(); i++) {
						gsp_ids1[i] = gc.getGsps().get(i) != null ? gc
								.getGsps().get(i).getId().toString() : "";
					}
					Arrays.sort(gsp_ids1);
					if (gc.getGoods().getId().toString().equals(id)
							&& Arrays.equals(gsp_ids, gsp_ids1)) {
						add = false;
					}
				} else {
					if (gc.getGoods().getId().toString().equals(id)) {
						add = false;
					}
				}
			}
		}
		if (add) {// 排除购物车中没有重复商品后添加该商品到购物车
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
			String type = "save";
			StoreCart sc = new StoreCart();
			for (StoreCart sc1 : cart) {
				if (sc1.getStore().getId()
						.equals(goods.getGoods_store().getId())) {
					sc = sc1;
					type = "update";
					break;
				}
			}
			sc.setStore(goods.getGoods_store());
			if (type.equals("save")) {
				sc.setAddTime(new Date());
				this.storeCartService.save(sc);// 保存该StoreCart
			} else {
				this.storeCartService.update(sc);// 如果是已经存在的则更新storeCart
			}

			GoodsCart obj = new GoodsCart();
			obj.setAddTime(new Date());
			if (CommUtil.null2String(buy_type).equals("")) {
				obj.setCount(CommUtil.null2Int(count));
				obj.setPrice(BigDecimal.valueOf(CommUtil.null2Double(price)));
			}
			if (CommUtil.null2String(buy_type).equals("combin")) {// 组合销售只添加一件商品
				obj.setCount(1);// 设置组合销售套数
				obj.setCart_type("combin");// 设置组合销售标识
				obj.setPrice(goods.getCombin_price());// 设置为组合销售价格
			}
			obj.setGoods(goods);
			String spec_info = "";
			for (String gsp_id : gsp_ids) {
				GoodsSpecProperty spec_property = this.goodsSpecPropertyService
						.getObjById(CommUtil.null2Long(gsp_id));
				obj.getGsps().add(spec_property);
				if (spec_property != null) {
					spec_info = spec_property.getSpec().getName() + ":"
							+ spec_property.getValue() + " " + spec_info;
				}
			}
			obj.setSc(sc);
			obj.setSpec_info(spec_info);
			this.goodsCartService.save(obj);
			sc.getGcs().add(obj);
			double cart_total_price = 0;
			for (GoodsCart gc1 : sc.getGcs()) {
				if (CommUtil.null2String(gc1.getCart_type()).equals("")) {
					cart_total_price = cart_total_price
							+ CommUtil.null2Double(gc1.getGoods()
									.getGoods_current_price()) * gc1.getCount();
				}
				if (CommUtil.null2String(gc1.getCart_type()).equals("combin")) { // 如果是组合销售购买，则设置组合价格
					cart_total_price = cart_total_price
							+ CommUtil.null2Double(gc1.getGoods()
									.getCombin_price()) * gc1.getCount();
				}
			}
			sc.setTotal_price(BigDecimal.valueOf(CommUtil
					.formatMoney(cart_total_price)));
			if (user == null) {
				sc.setCart_session_id(cart_session_id);
			} else {
				sc.setUser(user);
			}
			if (type.equals("save")) {
				sc.setAddTime(new Date());
				this.storeCartService.save(sc);// 保存该StoreCart
			} else {
				this.storeCartService.update(sc);
			}
			boolean cart_add = true;
			for (StoreCart sc1 : cart) {
				if (sc1.getStore().getId().equals(sc.getStore().getId())) {
					cart_add = false;
				}
			}
			if (cart_add) {
				cart.add(sc);// 将新增的StoreCart添加到整体购物车中，计算总体价格和数量
			}
		}
		for (StoreCart sc1 : cart) {
			// System.out.println(sc1.getGcs().size());
			total_count = total_count + sc1.getGcs().size();
			for (GoodsCart gc1 : sc1.getGcs()) {
				total_price = total_price
						+ CommUtil.mul(gc1.getPrice(), gc1.getCount());
			}
		}
		Map map = new HashMap();
		map.put("count", total_count);
		map.put("total_price", total_price);
		String ret = Json.toJson(map, JsonFormat.compact());
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

	/**
	 * 从购物车移除商品
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param count
	 * @param price
	 * @param spec_info
	 */
	@RequestMapping("/weixin/remove_goods_cart.htm")
	public void weixin_remove_goods_cart(HttpServletRequest request,
			HttpServletResponse response, String id, String store_id) {
		GoodsCart gc = this.goodsCartService.getObjById(CommUtil.null2Long(id));
		StoreCart the_sc = gc.getSc();
		gc.getGsps().clear();
		// the_sc.getGcs().remove(gc);
		this.goodsCartService.delete(CommUtil.null2Long(id));
		if (the_sc.getGcs().size() == 0) {
			this.storeCartService.delete(the_sc.getId());
		}
		List<StoreCart> cart = this.cart_calc(request);
		double total_price = 0;
		double sc_total_price = 0;
		double count = 0;
		for (StoreCart sc2 : cart) {
			for (GoodsCart gc1 : sc2.getGcs()) {
				GoodsCart temp_gc = this.goodsCartService.getObjById(gc1
						.getId());
				if (temp_gc != null) {
					total_price = CommUtil.null2Double(gc1.getPrice())
							* gc1.getCount() + total_price;
					count++;
					if (store_id != null
							&& !store_id.equals("")
							&& sc2.getStore().getId().toString()
									.equals(store_id)) {
						sc_total_price = sc_total_price
								+ CommUtil.null2Double(gc1.getPrice())
								* gc1.getCount();
						sc2.setTotal_price(BigDecimal.valueOf(sc_total_price));
					}
				}
			}
			this.storeCartService.update(sc2);
		}
		request.getSession(false).setAttribute("cart", cart);
		Map map = new HashMap();
		map.put("count", count);
		map.put("total_price", total_price);
		map.put("sc_total_price", sc_total_price);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 商品数量调整
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param store_id
	 */
	@RequestMapping("/weixin/goods_count_adjust.htm")
	public void weixin_goods_count_adjust(HttpServletRequest request,
			HttpServletResponse response, String cart_id, String store_id,
			String count) {
		List<StoreCart> cart = this.cart_calc(request);
		//
		double goods_total_price = 0;
		String error = "100";// 100表示修改成功，200表示库存不足,300表示团购库存不足
		Goods goods = null;
		String cart_type = "";// 判断是否为组合销售
		for (StoreCart sc : cart) {
			for (GoodsCart gc : sc.getGcs()) {
				if (gc.getId().toString().equals(cart_id)) {
					goods = gc.getGoods();
					cart_type = CommUtil.null2String(gc.getCart_type());
				}
			}
		}
		if (cart_type.equals("")) {// 普通商品的处理
			if (goods.getGroup_buy() == 2) {
				GroupGoods gg = new GroupGoods();
				for (GroupGoods gg1 : goods.getGroup_goods_list()) {
					if (gg1.getGg_goods().equals(goods.getId())) {
						gg = gg1;
					}
				}
				if (gg.getGg_count() >= CommUtil.null2Int(count)) {
					for (StoreCart sc : cart) {
						for (int i = 0; i < sc.getGcs().size(); i++) {
							GoodsCart gc = sc.getGcs().get(i);
							GoodsCart gc1 = gc;
							if (gc.getId().toString().equals(cart_id)) {
								sc.setTotal_price(BigDecimal.valueOf(CommUtil.add(
										sc.getTotal_price(),
										(CommUtil.null2Int(count) - gc
												.getCount())
												* CommUtil.null2Double(gc
														.getPrice()))));
								gc.setCount(CommUtil.null2Int(count));
								gc1 = gc;
								sc.getGcs().remove(gc);
								sc.getGcs().add(gc1);
								goods_total_price = CommUtil.null2Double(gc1
										.getPrice()) * gc1.getCount();
								this.storeCartService.update(sc);
							}
						}
					}
				} else {
					error = "300";
				}
			} else {
				if (goods.getGoods_inventory() >= CommUtil.null2Int(count)) {
					for (StoreCart sc : cart) {
						for (int i = 0; i < sc.getGcs().size(); i++) {
							GoodsCart gc = sc.getGcs().get(i);
							GoodsCart gc1 = gc;
							if (gc.getId().toString().equals(cart_id)) {
								sc.setTotal_price(BigDecimal.valueOf(CommUtil
										.add(sc.getTotal_price(),
												(CommUtil.null2Int(count) - gc
														.getCount())
														* Double.parseDouble(gc
																.getPrice()
																.toString()))));
								gc.setCount(CommUtil.null2Int(count));
								gc1 = gc;
								sc.getGcs().remove(gc);
								sc.getGcs().add(gc1);
								goods_total_price = Double.parseDouble(gc1
										.getPrice().toString())
										* gc1.getCount();
								this.storeCartService.update(sc);
							}
						}
					}
				} else {
					error = "200";
				}
			}
		}
		if (cart_type.equals("combin")) {// 组合销售的处理
			if (goods.getGoods_inventory() >= CommUtil.null2Int(count)) {
				for (StoreCart sc : cart) {
					for (int i = 0; i < sc.getGcs().size(); i++) {
						GoodsCart gc = sc.getGcs().get(i);
						GoodsCart gc1 = gc;
						if (gc.getId().toString().equals(cart_id)) {
							sc.setTotal_price(BigDecimal.valueOf(CommUtil.add(
									sc.getTotal_price(),
									(CommUtil.null2Int(count) - gc.getCount())
											* CommUtil.null2Float(gc.getGoods()
													.getCombin_price()))));
							gc.setCount(CommUtil.null2Int(count));
							gc1 = gc;
							sc.getGcs().remove(gc);
							sc.getGcs().add(gc1);
							goods_total_price = Double.parseDouble(gc1
									.getPrice().toString()) * gc1.getCount();
							this.storeCartService.update(sc);
						}
					}
				}
			} else {
				error = "200";
			}
		}
		DecimalFormat df = new DecimalFormat("0.00");
		Map map = new HashMap();
		map.put("count", count);
		for (StoreCart sc : cart) {
			// System.out.println("=============:" + sc.getTotal_price());
			if (sc.getStore().getId().equals(CommUtil.null2Long(store_id))) {
				map.put("sc_total_price",
						CommUtil.null2Float(sc.getTotal_price()));
			}
		}
		map.put("goods_total_price",
				Double.valueOf(df.format(goods_total_price)));
		map.put("error", error);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			// System.out.println(Json.toJson(map, JsonFormat.compact()));
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 确认购物车商品
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "查看购物车", value = "/weixin/goods_cart1.htm*", rtype = "buyer", rname = "微信购物流程1", rcode = "weixin_goods_cart", rgroup = "微信在线购物")
	@RequestMapping("/weixin/goods_cart1.htm")
	public ModelAndView weixin_goods_cart1(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/goods_cart1.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<StoreCart> cart = this.cart_calc(request);
		if (cart != null) {
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			Store store = user.getStore() != null ? user.getStore() : null;
			if (store != null) {
				for (StoreCart sc : cart) {
					if (sc.getStore().getId().equals(store.getId())) {
						for (GoodsCart gc : sc.getGcs()) {
							gc.getGsps().clear();
							this.goodsCartService.delete(gc.getId());
						}
						sc.getGcs().clear();
						this.storeCartService.delete(sc.getId());
					}
				}
			}
			request.getSession(false).setAttribute("cart", cart);
			mv.addObject("cart", cart);
			mv.addObject("goodsViewTools", goodsViewTools);
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "购物车信息为空");
			String store_id = CommUtil.null2String(request.getSession(false)
					.getAttribute("store_id"));
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/index.htm?store_id=" + store_id);
		}
		return mv;
	}

	/**
	 * 购物确认,填写用户地址，配送方式，支付方式等
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "确认购物车填写地址", value = "/weixin/goods_cart2.htm*", rtype = "buyer", rname = "微信购物流程2", rcode = "weixin_goods_cart", rgroup = "微信在线购物")
	@RequestMapping("/weixin/goods_cart2.htm")
	public ModelAndView weixin_goods_cart2(HttpServletRequest request,
			HttpServletResponse response, String store_id) {
		ModelAndView mv = new JModelAndView("weixin/goods_cart2.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<StoreCart> cart = this.cart_calc(request);
		StoreCart sc = null;
		if (cart != null) {
			for (StoreCart sc1 : cart) {
				if (sc1.getStore().getId().equals(CommUtil.null2Long(store_id))) {
					sc = sc1;
					break;
				}
			}
		}
		if (sc != null) {
			Map params = new HashMap();
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			List<Address> addrs = this.addressService
					.query("select obj from Address obj where obj.user.id=:user_id order by obj.addTime desc",
							params, -1, -1);
			mv.addObject("addrs", addrs);
			if (store_id == null || store_id.equals("")) {
				store_id = sc.getStore().getId().toString();
			}
			String cart_session = CommUtil.randomString(32);
			request.getSession(false)
					.setAttribute("cart_session", cart_session);
			params.clear();
			params.put("coupon_order_amount", sc.getTotal_price());
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			params.put("coupon_begin_time", new Date());
			params.put("coupon_end_time", new Date());
			params.put("status", 0);
			List<CouponInfo> couponinfos = this.couponInfoService
					.query("select obj from CouponInfo obj where obj.coupon.coupon_order_amount<=:coupon_order_amount and obj.status=:status and obj.user.id=:user_id and obj.coupon.coupon_begin_time<=:coupon_begin_time and obj.coupon.coupon_end_time>=:coupon_end_time",
							params, -1, -1);
			mv.addObject("couponinfos", couponinfos);
			mv.addObject("sc", sc);
			mv.addObject("cart_session", cart_session);
			mv.addObject("store_id", store_id);
			mv.addObject("transportTools", transportTools);
			mv.addObject("goodsViewTools", goodsViewTools);
			// 查询当前购物车内是否有实体商品,配送选项。
			boolean goods_delivery = false;
			List<GoodsCart> goodCarts = sc.getGcs();
			for (GoodsCart gc : goodCarts) {
				if (gc.getGoods().getGoods_choice_type() == 0) {
					goods_delivery = true;
					break;
				}
			}
			mv.addObject("goods_delivery", goods_delivery);
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "购物车信息为空");
			String store_id2 = CommUtil.null2String(request.getSession(false)
					.getAttribute("store_id"));
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/index.htm?store_id=" + store_id2);
		}
		return mv;
	}

	@SecurityMapping(title = "完成订单提交进入支付", value = "/weixin/goods_cart3.htm*", rtype = "buyer", rname = "微信购物流程3", rcode = "weixin_goods_cart", rgroup = "微信在线购物")
	@RequestMapping("/weixin/goods_cart3.htm")
	public ModelAndView weixin_goods_cart3(HttpServletRequest request,
			HttpServletResponse response, String cart_session, String store_id,
			String addr_id, String coupon_id) throws Exception {
		ModelAndView mv = new JModelAndView("weixin/goods_cart3.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map payment_map = new HashMap();
		List<Payment> store_Payments = new ArrayList<Payment>();
		if (this.configService.getSysConfig().getConfig_payment_type() == 1) {
			payment_map.put("install", true);
			payment_map.put("type", "admin");
			store_Payments = this.paymentService
					.query("select obj from Payment obj where obj.install=:install and obj.type=:type",
							payment_map, -1, -1);
		} else {
			payment_map.put("store_id", CommUtil.null2Long(store_id));
			payment_map.put("install", true);
			store_Payments = this.paymentService
					.query("select obj from Payment obj where obj.store.id=:store_id and obj.install=:install",
							payment_map, -1, -1);
		}
		if (store_Payments.size() > 0) {
			String cart_session1 = (String) request.getSession(false)
					.getAttribute("cart_session");
			List<StoreCart> cart = this.cart_calc(request);
			if (cart != null) {
				if (CommUtil.null2String(cart_session1).equals(cart_session)) {// 禁止重复提交订单信息
					request.getSession(false).removeAttribute("cart_session");// 删除订单提交唯一标示，用户不能进行第二次订单提交
					WebForm wf = new WebForm();
					OrderForm of = wf.toPo(request, OrderForm.class);
					of.setAddTime(new Date());
					of.setOrder_id(SecurityUserHolder.getCurrentUser().getId()
							+ CommUtil.formatTime("yyyyMMddHHmmss", new Date()));
					Address addr = this.addressService.getObjById(CommUtil
							.null2Long(addr_id));
					of.setAddr(addr);
					of.setOrder_status(10);
					of.setUser(SecurityUserHolder.getCurrentUser());
					of.setStore(this.storeService.getObjById(CommUtil
							.null2Long(store_id)));
					of.setTotalPrice(BigDecimal.valueOf(CommUtil.add(
							of.getGoods_amount(), of.getShip_price())));
					if (!CommUtil.null2String(coupon_id).equals("")) {
						CouponInfo ci = this.couponInfoService
								.getObjById(CommUtil.null2Long(coupon_id));
						ci.setStatus(1);
						this.couponInfoService.update(ci);
						of.setCi(ci);
						of.setTotalPrice(BigDecimal.valueOf(CommUtil.subtract(
								of.getTotalPrice(), ci.getCoupon()
										.getCoupon_amount())));
					}
					of.setOrder_type("weixin");// 设置为手机网页订单
					this.orderFormService.save(of);
					for (StoreCart sc : cart) {
						if (sc.getStore().getId().toString().equals(store_id)) {
							for (GoodsCart gc : sc.getGcs()) {
								gc.setOf(of);
								this.goodsCartService.update(gc);
							}
							sc.setCart_session_id(null);
							sc.setUser(SecurityUserHolder.getCurrentUser());
							sc.setSc_status(1);
							this.storeCartService.update(sc);// 更新该店铺购物车的状态为1，表示该店铺购物车已经完成订单提交，不需要再显示在前台用户购物车信息中
							break;
						}
					}
					Cookie[] cookies = request.getCookies();
					if (cookies != null) {
						for (Cookie cookie : cookies) {
							if (cookie.getName().equals("cart_session_id")) {
								cookie.setDomain(CommUtil
										.generic_domain(request));
								cookie.setValue("");
								cookie.setMaxAge(0);
								response.addCookie(cookie);
							}
						}
					}
					OrderFormLog ofl = new OrderFormLog();
					ofl.setAddTime(new Date());
					ofl.setOf(of);
					ofl.setLog_info("提交订单");
					ofl.setLog_user(SecurityUserHolder.getCurrentUser());
					this.orderFormLogService.save(ofl);
					mv.addObject("of", of);
					mv.addObject("paymentTools", paymentTools);
					if (this.configService.getSysConfig().isEmailEnable()) {// 如果系统启动了邮件功能，则发送邮件提示
						this.send_email(request, of, of.getUser().getEmail(),
								"email_tobuyer_order_submit_ok_notify");
					}
					if (this.configService.getSysConfig().isSmsEnbale()) {// 如果系统启用了短信功能，则发送短信提示
						this.send_sms(request, of, of.getUser().getMobile(),
								"sms_tobuyer_order_submit_ok_notify");
					}
				} else {
					mv = new JModelAndView("weixin/error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "订单已经失效");
					String store_id2 = CommUtil.null2String(request.getSession(
							false).getAttribute("store_id"));
					mv.addObject("url", CommUtil.getURL(request)
							+ "/weixin/index.htm?store_id=" + store_id2);
				}
			} else {
				mv = new JModelAndView("weixin/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "订单信息错误");
				String store_id2 = CommUtil.null2String(request.getSession(
						false).getAttribute("store_id"));
				mv.addObject("url", CommUtil.getURL(request)
						+ "/weixin/index.htm?store_id=" + store_id2);
			}
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "没有开通支付方式，不能付款");
			String store_id2 = CommUtil.null2String(request.getSession(false)
					.getAttribute("store_id"));
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/index.htm?store_id=" + store_id2);
		}
		return mv;
	}

	@SecurityMapping(title = "订单支付详情", value = "/weixin/order_pay_view.htm*", rtype = "buyer", rname = "微信购物流程3", rcode = "weixin_goods_cart", rgroup = "微信在线购物")
	@RequestMapping("/weixin/order_pay_view.htm")
	public ModelAndView weixin_order_pay_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("weixin/order_pay.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(id));
		String store_id = CommUtil.null2String(request.getSession(false)
				.getAttribute("store_id"));
		Map payment_map = new HashMap();
		List<Payment> store_Payments = new ArrayList<Payment>();
		if (this.configService.getSysConfig().getConfig_payment_type() == 1) {
			payment_map.put("install", true);
			payment_map.put("type", "admin");
			store_Payments = this.paymentService
					.query("select obj from Payment obj where obj.install=:install and obj.type=:type",
							payment_map, -1, -1);
		} else {
			payment_map.put("store_id", CommUtil.null2Long(store_id));
			payment_map.put("install", true);
			store_Payments = this.paymentService
					.query("select obj from Payment obj where obj.store.id=:store_id and obj.install=:install",
							payment_map, -1, -1);
		}
		if (store_Payments.size() > 0) {
			if (of.getOrder_status() == 10) {
				mv.addObject("of", of);
				mv.addObject("paymentTools", this.paymentTools);
				mv.addObject("url", CommUtil.getURL(request));
			} else if (of.getOrder_status() < 10) {
				mv = new JModelAndView("weixin/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "该订单已经取消！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/weixin/index.htm?store_id=" + store_id);
			} else {
				mv = new JModelAndView("weixin/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "该订单已经付款！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/weixin/index.htm?store_id=" + store_id);
			}
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "没有开通支付方式，不能付款");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/index.htm?store_id=" + store_id);
		}
		return mv;
	}

	@SecurityMapping(title = "订单支付", value = "/weixin/order_pay.htm*", rtype = "buyer", rname = "微信购物流程3", rcode = "weixin_goods_cart", rgroup = "微信在线购物")
	@RequestMapping("/weixin/order_pay.htm")
	public ModelAndView weixin_order_pay(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id) {
		ModelAndView mv = null;
		OrderForm of = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		if (of != null && of.getOrder_status() == 10) {
			if (CommUtil.null2String(payType).equals("")) {
				mv = new JModelAndView("weixin/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "支付方式错误！");
				String store_id = CommUtil.null2String(request
						.getSession(false).getAttribute("store_id"));
				mv.addObject("url", CommUtil.getURL(request)
						+ "/weixin/index.htm?store_id=" + store_id);
			} else {
				// 给订单添加支付方式 ,
				List<Payment> payments = new ArrayList<Payment>();
				Map params = new HashMap();
				if (this.configService.getSysConfig().getConfig_payment_type() == 1) {// 系统开启平台支付
					params.put("mark", payType);
					params.put("type", "admin");
					payments = this.paymentService
							.query("select obj from Payment obj where obj.mark=:mark and obj.type=:type",
									params, -1, -1);
				} else {
					params.put("store_id", of.getStore().getId());
					params.put("mark", payType);
					payments = this.paymentService
							.query("select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id",
									params, -1, -1);
				}
				of.setPayment(payments.get(0));
				this.orderFormService.update(of);
				if (payType.equals("balance")) {
					mv = new JModelAndView("weixin/balance_pay.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
				} else if (payType.equals("outline")) {
					mv = new JModelAndView("weixin/outline_pay.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					String pay_session = CommUtil.randomString(32);
					request.getSession(false).setAttribute("pay_session",
							pay_session);
					mv.addObject("paymentTools", this.paymentTools);
					mv.addObject(
							"store_id",
							this.orderFormService
									.getObjById(CommUtil.null2Long(order_id))
									.getStore().getId());
					mv.addObject("pay_session", pay_session);
				} else if (payType.equals("payafter")) {
					mv = new JModelAndView("weixin/payafter_pay.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					String pay_session = CommUtil.randomString(32);
					request.getSession(false).setAttribute("pay_session",
							pay_session);
					mv.addObject("paymentTools", this.paymentTools);
					mv.addObject(
							"store_id",
							this.orderFormService
									.getObjById(CommUtil.null2Long(order_id))
									.getStore().getId());
					mv.addObject("pay_session", pay_session);
				} else {
					mv = new JModelAndView("weixin/line_pay.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("payType", payType);
					mv.addObject("url", CommUtil.getURL(request));
					mv.addObject("payTools", payTools);
					mv.addObject("type", "goods");
					mv.addObject("payment_id", of.getPayment().getId());
					mv.addObject("spbill_ip", CommUtil.getIpAddr(request));
				}
				mv.addObject("order_id", order_id);
			}
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "该订单不能进行付款！");
			String store_id = CommUtil.null2String(request.getSession(false)
					.getAttribute("store_id"));
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/index.htm?store_id=" + store_id);
		}
		return mv;
	}

	@SecurityMapping(title = "订单线下支付", value = "/weixin/order_pay_outline.htm*", rtype = "buyer", rname = "微信购物流程3", rcode = "weixin_goods_cart", rgroup = "微信在线购物")
	@RequestMapping("/weixin/order_pay_outline.htm")
	public ModelAndView weixin_order_pay_outline(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String pay_msg, String pay_session) throws Exception {
		ModelAndView mv = new JModelAndView("weixin/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String pay_session1 = CommUtil.null2String(request.getSession(false)
				.getAttribute("pay_session"));
		if (pay_session1.equals(pay_session)) {
			OrderForm of = this.orderFormService.getObjById(CommUtil
					.null2Long(order_id));
			of.setPay_msg(pay_msg);
			Map params = new HashMap();
			params.put("mark", "outline");
			params.put("store_id", of.getStore().getId());
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id",
							params, -1, -1);
			if (payments.size() > 0) {
				of.setPayment(payments.get(0));
				of.setPayTime(new Date());
			}
			of.setOrder_status(15);
			this.orderFormService.update(of);
			if (this.configService.getSysConfig().isSmsEnbale()) {
				this.send_sms(request, of, of.getStore().getUser().getMobile(),
						"sms_toseller_outline_pay_ok_notify");
			}
			if (this.configService.getSysConfig().isEmailEnable()) {
				this.send_email(request, of,
						of.getStore().getUser().getEmail(),
						"email_toseller_outline_pay_ok_notify");
			}
			// 记录支付日志
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("提交线下支付申请");
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(of);
			this.orderFormLogService.save(ofl);
			request.getSession(false).removeAttribute("pay_session");
			mv.addObject("op_title", "线下支付提交成功，等待卖家审核！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/buyer/order.htm");
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "订单已经支付，禁止重复支付！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/buyer/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "订单预付款支付", value = "/weixin/order_pay_balance.htm*", rtype = "buyer", rname = "微信购物流程3", rcode = "weixin_goods_cart", rgroup = "微信在线购物")
	@RequestMapping("/weixin/order_pay_balance.htm")
	public ModelAndView weixin_order_pay_balance(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String pay_msg) throws Exception {
		ModelAndView mv = new JModelAndView("weixin/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm of = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (CommUtil.null2Double(user.getAvailableBalance()) > CommUtil
				.null2Double(of.getTotalPrice())) {
			of.setPay_msg(pay_msg);
			of.setOrder_status(20);
			Map params = new HashMap();
			params.put("mark", "balance");
			params.put("store_id", of.getStore().getId());
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id",
							params, -1, -1);
			if (payments.size() > 0) {
				of.setPayment(payments.get(0));
				of.setPayTime(new Date());
			}
			boolean ret = this.orderFormService.update(of);
			if (this.configService.getSysConfig().isEmailEnable()) {
				this.send_email(request, of,
						of.getStore().getUser().getEmail(),
						"email_toseller_balance_pay_ok_notify");
				this.send_email(request, of,
						of.getStore().getUser().getEmail(),
						"email_tobuyer_balance_pay_ok_notify");
			}
			if (this.configService.getSysConfig().isSmsEnbale()) {
				this.send_sms(request, of, of.getStore().getUser().getMobile(),
						"sms_toseller_balance_pay_ok_notify");
				this.send_sms(request, of, of.getUser().getMobile(),
						"sms_tobuyer_balance_pay_ok_notify");
			}
			if (ret) {
				user.setAvailableBalance(BigDecimal.valueOf(CommUtil.subtract(
						user.getAvailableBalance(), of.getTotalPrice())));
				user.setFreezeBlance(BigDecimal.valueOf(CommUtil.add(
						user.getFreezeBlance(), of.getTotalPrice())));
				this.userService.update(user);
				PredepositLog log = new PredepositLog();
				log.setAddTime(new Date());
				log.setPd_log_user(user);
				log.setPd_op_type("消费");
				log.setPd_log_amount(BigDecimal.valueOf(-CommUtil
						.null2Double(of.getTotalPrice())));
				log.setPd_log_info("订单" + of.getOrder_id() + "购物减少可用预存款");
				log.setPd_type("可用预存款");
				this.predepositLogService.save(log);
				// 执行库存减少,如果是团购商品，团购库存同步减少
				for (GoodsCart gc : of.getGcs()) {
					Goods goods = gc.getGoods();
					if (goods.getGroup() != null && goods.getGroup_buy() == 2) {
						for (GroupGoods gg : goods.getGroup_goods_list()) {
							if (gg.getGroup().getId()
									.equals(goods.getGroup().getId())) {
								gg.setGg_count(gg.getGg_count() - gc.getCount());
								gg.setGg_def_count(gg.getGg_def_count()
										+ gc.getCount());
								this.groupGoodsService.update(gg);
							}
						}
					}
					List<String> gsps = new ArrayList<String>();
					for (GoodsSpecProperty gsp : gc.getGsps()) {
						gsps.add(gsp.getId().toString());
					}
					String[] gsp_list = new String[gsps.size()];
					gsps.toArray(gsp_list);
					goods.setGoods_salenum(goods.getGoods_salenum()
							+ gc.getCount());
					if (goods.getInventory_type().equals("all")) {
						goods.setGoods_inventory(goods.getGoods_inventory()
								- gc.getCount());
					} else {
						List<HashMap> list = Json.fromJson(ArrayList.class,
								goods.getGoods_inventory_detail());
						for (Map temp : list) {
							String[] temp_ids = CommUtil.null2String(
									temp.get("id")).split("_");
							Arrays.sort(temp_ids);
							Arrays.sort(gsp_list);
							if (Arrays.equals(temp_ids, gsp_list)) {
								temp.put("count",
										CommUtil.null2Int(temp.get("count"))
												- gc.getCount());
							}
						}
						goods.setGoods_inventory_detail(Json.toJson(list,
								JsonFormat.compact()));
					}
					for (GroupGoods gg : goods.getGroup_goods_list()) {
						if (gg.getGroup().getId()
								.equals(goods.getGroup().getId())
								&& gg.getGg_count() == 0) {
							goods.setGroup_buy(3);// 标识商品的状态为团购数量已经结束
						}
					}
					this.goodsService.update(goods);

				}
			}
			// 记录支付日志
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("预付款支付");
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(of);
			this.orderFormLogService.save(ofl);
			mv.addObject("op_title", "预付款支付成功！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/buyer/order.htm");
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "可用余额不足，支付失败！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/buyer/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "订单货到付款", value = "/weixin/order_pay_payafter.htm*", rtype = "buyer", rname = "微信购物流程3", rcode = "weixin_goods_cart", rgroup = "微信在线购物")
	@RequestMapping("/weixin/order_pay_payafter.htm")
	public ModelAndView weixin_order_pay_payafter(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String pay_msg, String pay_session) throws Exception {
		ModelAndView mv = new JModelAndView("weixin/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String pay_session1 = CommUtil.null2String(request.getSession(false)
				.getAttribute("pay_session"));
		if (pay_session1.equals(pay_session)) {
			OrderForm of = this.orderFormService.getObjById(CommUtil
					.null2Long(order_id));
			of.setPay_msg(pay_msg);
			Map params = new HashMap();
			params.put("mark", "payafter");
			params.put("store_id", of.getStore().getId());
			List<Payment> payments = this.paymentService
					.query("select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id",
							params, -1, -1);
			if (payments.size() > 0) {
				of.setPayment(payments.get(0));
				of.setPayTime(new Date());
			}
			of.setOrder_status(16);
			this.orderFormService.update(of);
			if (this.configService.getSysConfig().isSmsEnbale()) {
				this.send_sms(request, of, of.getStore().getUser().getMobile(),
						"sms_toseller_payafter_pay_ok_notify");
			}
			if (this.configService.getSysConfig().isEmailEnable()) {
				this.send_email(request, of,
						of.getStore().getUser().getEmail(),
						"email_toseller_payafter_pay_ok_notify");
			}
			// 记录支付日志
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("提交货到付款申请");
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(of);
			this.orderFormLogService.save(ofl);
			request.getSession(false).removeAttribute("pay_session");
			mv.addObject("op_title", "货到付款提交成功，等待卖家发货！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/buyer/order.htm");
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "订单已经支付，禁止重复支付！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/buyer/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "订单支付结果", value = "/weixin/order_finish.htm*", rtype = "buyer", rname = "微信购物流程3", rcode = "weixin_goods_cart", rgroup = "微信在线购物")
	@RequestMapping("/weixin/order_finish.htm")
	public ModelAndView weixin_order_finish(HttpServletRequest request,
			HttpServletResponse response, String order_id) {
		ModelAndView mv = new JModelAndView("weixin/wap_order_finish.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm obj = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		mv.addObject("obj", obj);
		return mv;
	}

	@SecurityMapping(title = "地址新增", value = "/weixin/cart_address.htm*", rtype = "buyer", rname = "微信购物流程3", rcode = "weixin_goods_cart", rgroup = "微信在线购物")
	@RequestMapping("/weixin/cart_address.htm")
	public ModelAndView weixin_cart_address(HttpServletRequest request,
			HttpServletResponse response, String id, String store_id) {
		ModelAndView mv = new JModelAndView("weixin/cart_address.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		mv.addObject("areas", areas);
		mv.addObject("store_id", store_id);
		return mv;
	}

	@SecurityMapping(title = "购物车中收货地址保存", value = "/weixin/cart_address_save.htm*", rtype = "buyer", rname = "微信购物流程3", rcode = "weixin_goods_cart", rgroup = "微信在线购物")
	@RequestMapping("/weixin/cart_address_save.htm")
	public String cart_address_save(HttpServletRequest request,
			HttpServletResponse response, String id, String area_id,
			String store_id) {
		WebForm wf = new WebForm();
		Address address = null;
		if (id.equals("")) {
			address = wf.toPo(request, Address.class);
			address.setAddTime(new Date());
		} else {
			Address obj = this.addressService.getObjById(Long.parseLong(id));
			address = (Address) wf.toPo(request, obj);
		}
		address.setUser(SecurityUserHolder.getCurrentUser());
		Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
		address.setArea(area);
		if (id.equals("")) {
			this.addressService.save(address);
		} else
			this.addressService.update(address);
		return "redirect:/weixin/goods_cart2.htm?store_id=" + store_id;
	}

	private void send_email(HttpServletRequest request, OrderForm order,
			String email, String mark) throws Exception {
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template != null && template.isOpen()) {
			String subject = template.getTitle();
			String path = request.getSession().getServletContext()
					.getRealPath("")
					+ File.separator + "vm" + File.separator;
			if (!CommUtil.fileExist(path)) {
				CommUtil.createFolder(path);
			}
			PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(path + "msg.vm", false), "UTF-8"));
			pwrite.print(template.getContent());
			pwrite.flush();
			pwrite.close();
			// 生成模板
			Properties p = new Properties();
			p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,
					request.getRealPath("/") + "vm" + File.separator);
			p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
			p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
			Velocity.init(p);
			org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm",
					"UTF-8");
			VelocityContext context = new VelocityContext();
			context.put("buyer", order.getUser());
			context.put("seller", order.getStore().getUser());
			context.put("config", this.configService.getSysConfig());
			context.put("send_time", CommUtil.formatLongDate(new Date()));
			context.put("webPath", CommUtil.getURL(request));
			context.put("order", order);
			StringWriter writer = new StringWriter();
			blank.merge(context, writer);
			// System.out.println(writer.toString());
			String content = writer.toString();
			this.msgTools.sendEmail(email, subject, content);
		}
	}

	@SecurityMapping(title = "地址切换", value = "/weixin/order_address.htm*", rtype = "buyer", rname = "微信购物流程3", rcode = "weixin_goods_cart", rgroup = "微信在线购物")
	@RequestMapping("/weixin/order_address.htm")
	public void weixin_order_address(HttpServletRequest request,
			HttpServletResponse response, String addr_id, String store_id) {
		List<StoreCart> cart = (List<StoreCart>) request.getSession(false)
				.getAttribute("cart");
		StoreCart sc = null;
		if (cart != null) {
			for (StoreCart sc1 : cart) {
				if (sc1.getStore().getId().equals(CommUtil.null2Long(store_id))) {
					sc = sc1;
					break;
				}
			}
		}
		Address addr = this.addressService.getObjById(CommUtil
				.null2Long(addr_id));
		List<SysMap> sms = this.transportTools.query_cart_trans(sc,
				CommUtil.null2String(addr.getArea().getId()));
		// System.out.println(Json.toJson(sms, JsonFormat.compact()));
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(sms, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void send_sms(HttpServletRequest request, OrderForm order,
			String mobile, String mark) throws Exception {
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template != null && template.isOpen()) {
			String path = request.getSession().getServletContext()
					.getRealPath("")
					+ File.separator + "vm" + File.separator;
			if (!CommUtil.fileExist(path)) {
				CommUtil.createFolder(path);
			}
			PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(path + "msg.vm", false), "UTF-8"));
			pwrite.print(template.getContent());
			pwrite.flush();
			pwrite.close();
			// 生成模板
			Properties p = new Properties();
			p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,
					request.getRealPath("/") + "vm" + File.separator);
			p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
			p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
			Velocity.init(p);
			org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm",
					"UTF-8");
			VelocityContext context = new VelocityContext();
			context.put("buyer", order.getUser());
			context.put("seller", order.getStore().getUser());
			context.put("config", this.configService.getSysConfig());
			context.put("send_time", CommUtil.formatLongDate(new Date()));
			context.put("webPath", CommUtil.getURL(request));
			context.put("order", order);
			StringWriter writer = new StringWriter();
			blank.merge(context, writer);
			// System.out.println(writer.toString());
			String content = writer.toString();
			this.msgTools.sendSMS(mobile, content);
		}
	}
}
