package com.lakecloud.weixin.manage.buyer.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Evaluate;
import com.lakecloud.foundation.domain.GoodsCart;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.OrderFormLog;
import com.lakecloud.foundation.domain.Payment;
import com.lakecloud.foundation.domain.PredepositLog;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.StorePoint;
import com.lakecloud.foundation.domain.Template;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.domain.virtual.TransInfo;
import com.lakecloud.foundation.service.ICouponInfoService;
import com.lakecloud.foundation.service.IEvaluateService;
import com.lakecloud.foundation.service.IExpressCompanyService;
import com.lakecloud.foundation.service.IGoodsCartService;
import com.lakecloud.foundation.service.IGoodsReturnItemService;
import com.lakecloud.foundation.service.IGoodsReturnService;
import com.lakecloud.foundation.service.IOrderFormLogService;
import com.lakecloud.foundation.service.IOrderFormService;
import com.lakecloud.foundation.service.IPaymentService;
import com.lakecloud.foundation.service.IPredepositLogService;
import com.lakecloud.foundation.service.IStorePointService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.ITemplateService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.manage.admin.tools.MsgTools;

/**
 * @info 微信客户端买家订单控制器，用于查看订单列表、查看订单详情、支付订单，确认收货
 * 
 */
@Controller
public class WeixinOrderBuyerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IStorePointService storePointService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IGoodsReturnItemService goodsReturnItemService;
	@Autowired
	private IGoodsReturnService goodsReturnService;
	@Autowired
	private IExpressCompanyService expressCompayService;
	@Autowired
	private MsgTools msgTools;

	/**
	 * 订单列表
	 * 
	 * @param request
	 * @param response
	 * @param order_status
	 *            ：订单状态，根据订单状态不同查询相应的订单
	 * @return
	 */
	@SecurityMapping(title = "订单列表", value = "/weixin/buyer/order.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/order.htm")
	public ModelAndView weixin_order(HttpServletRequest request,
			HttpServletResponse response, String order_status) {
		ModelAndView mv = new JModelAndView("weixin/buyer_order.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<OrderForm> orders = null;
		Map map = new HashMap();
		map.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		if (!CommUtil.null2String(order_status).equals("")) {
			map.put("order_status", CommUtil.null2Int(order_status));
			orders = this.orderFormService
					.query("select obj from OrderForm obj where obj.order_status=:order_status and obj.user.id=:user_id order by addTime desc",
							map, 0, 6);
			mv.addObject("order_status", order_status);
		} else {
			orders = this.orderFormService
					.query("select obj from OrderForm obj where obj.user.id=:user_id order by addTime desc",
							map, 0, 6);
		}
		mv.addObject("objs", orders);
		return mv;
	}

	/**
	 * 订单列表ajax加载
	 * 
	 * @param request
	 * @param response
	 * @param order_status
	 *            ：订单状态
	 * @param beginCount
	 *            ：订单查询开始值，每次查询6条数据
	 * @return
	 */
	@SecurityMapping(title = "订单列表ajax加载", value = "/weixin/buyer/order_ajax.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/order_ajax.htm")
	public ModelAndView weixin_order_data(HttpServletRequest request,
			HttpServletResponse response, String order_status, String beginCount) {
		ModelAndView mv = new JModelAndView("weixin/buyer_order_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<OrderForm> orders = null;
		Map map = new HashMap();
		map.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		if (!CommUtil.null2String(order_status).equals("")) {
			map.put("order_status", CommUtil.null2Int(order_status));
			orders = this.orderFormService
					.query("select obj from OrderForm obj where obj.order_status=:order_status and obj.user.id=:user_id order by addTime desc",
							map, CommUtil.null2Int(beginCount), 6);
			mv.addObject("order_status", order_status);
		} else {
			orders = this.orderFormService
					.query("select obj from OrderForm obj where obj.user.id=:user_id order by addTime desc",
							map, CommUtil.null2Int(beginCount), 6);
		}
		mv.addObject("objs", orders);
		mv.addObject("beginCount", beginCount);
		return mv;
	}

	/**
	 * 订单详情
	 * 
	 * @param request
	 * @param response
	 * @param id
	 *            :订单id
	 * @return
	 */
	@SecurityMapping(title = "订单详情", value = "/weixin/buyer/order_view.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/order_view.htm")
	public ModelAndView weixin_order_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("weixin/order_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj != null
				&& obj.getUser() != null
				&& obj.getUser().getId()
						.equals(SecurityUserHolder.getCurrentUser().getId())) {
			mv.addObject("obj", obj);
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/buyer/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "订单取消", value = "/weixin/buyer/order_cancel.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/order_cancel.htm")
	public ModelAndView weixin_order_cancel(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("weixin/buyer_order_cancel.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getUser().getId()
				.equals(SecurityUserHolder.getCurrentUser().getId())) {
			mv.addObject("obj", obj);
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/buyer/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "订单取消", value = "/weixin/buyer/order_cancel_save.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/order_cancel_save.htm")
	public String weixin_order_cancel_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String state_info, String other_state_info) throws Exception {
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getUser().getId()
				.equals(SecurityUserHolder.getCurrentUser().getId())) {
			obj.setOrder_status(0);
			this.orderFormService.update(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("取消订单");
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(obj);
			if (state_info.equals("other")) {
				ofl.setState_info(other_state_info);
			} else {
				ofl.setState_info(state_info);
			}
			this.orderFormLogService.save(ofl);
			if (this.configService.getSysConfig().isEmailEnable()) {
				this.send_email(request, obj,
						"email_toseller_order_cancel_notify");
			}
			if (this.configService.getSysConfig().isSmsEnbale()) {
				this.send_sms(request, obj, obj.getStore().getUser()
						.getMobile(), "sms_toseller_order_cancel_notify");
			}
		}
		return "redirect:order.htm?currentPage=" + currentPage;
	}

	/**
	 * 买家确认收货，确认收货后，订单状态值改变为40，如果是预存款支付，买家冻结预存款中同等订单账户金额自动转入商家预存款，如果开启预存款分润，
	 * 则按照分润比例，买家预存款分别进入商家及平台商的账户
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "收货确认保存", value = "/weixin/buyer/order_cofirm_save.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/order_cofirm_save.htm")
	public String weixin_order_cofirm_save(HttpServletRequest request,
			HttpServletResponse response, String id) throws Exception {
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getUser().getId()
				.equals(SecurityUserHolder.getCurrentUser().getId())) {
			obj.setOrder_status(40);
			boolean ret = this.orderFormService.update(obj);
			if (ret) {// 订单状态更新成功，更新相关信息
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("确认收货");
				ofl.setLog_user(SecurityUserHolder.getCurrentUser());
				ofl.setOf(obj);
				this.orderFormLogService.save(ofl);
				if (this.configService.getSysConfig().isEmailEnable()) {
					this.send_email(request, obj,
							"email_toseller_order_receive_ok_notify");
				}
				if (this.configService.getSysConfig().isSmsEnbale()) {
					this.send_sms(request, obj, obj.getStore().getUser()
							.getMobile(),
							"sms_toseller_order_receive_ok_notify");
				}
				if (obj.getPayment().getMark().equals("balance")) {
					User seller = this.userService.getObjById(obj.getStore()
							.getUser().getId());
					if (this.configService.getSysConfig().getBalance_fenrun() == 1) {// 系统开启预存款分润，实行分润转账处理
						// 卖家预存款增加
						Map params = new HashMap();
						params.put("type", "admin");
						params.put("mark", "balance");
						List<Payment> payments = this.paymentService
								.query("select obj from Payment obj where obj.type=:type and obj.mark=:mark",
										params, -1, -1);
						Payment shop_payment = new Payment();
						if (payments.size() > 0) {
							shop_payment = payments.get(0);
						}
						// 按照分润比例计算平台应得利润金额
						double shop_availableBalance = CommUtil.null2Double(obj
								.getTotalPrice())
								* CommUtil.null2Double(shop_payment
										.getBalance_divide_rate());
						User admin = this.userService.getObjByProperty(
								"userName", "admin");
						admin.setAvailableBalance(BigDecimal.valueOf(CommUtil
								.add(admin.getAvailableBalance(),
										shop_availableBalance)));
						this.userService.update(admin);
						PredepositLog log = new PredepositLog();
						log.setAddTime(new Date());
						log.setPd_log_user(seller);
						log.setPd_op_type("分润");
						log.setPd_log_amount(BigDecimal
								.valueOf(shop_availableBalance));
						log.setPd_log_info("订单" + obj.getOrder_id()
								+ "确认收货平台分润获得预存款");
						log.setPd_type("可用预存款");
						this.predepositLogService.save(log);
						// 减去平台应得利润金额，剩下的就是商家应得利润
						double seller_availableBalance = CommUtil
								.null2Double(obj.getTotalPrice())
								- shop_availableBalance;
						seller.setAvailableBalance(BigDecimal.valueOf(CommUtil
								.add(seller.getAvailableBalance(),
										seller_availableBalance)));
						this.userService.update(seller);
						PredepositLog log1 = new PredepositLog();
						log1.setAddTime(new Date());
						log1.setPd_log_user(seller);
						log1.setPd_op_type("增加");
						log1.setPd_log_amount(BigDecimal
								.valueOf(seller_availableBalance));
						log1.setPd_log_info("订单" + obj.getOrder_id()
								+ "确认收货增加预存款");
						log1.setPd_type("可用预存款");
						this.predepositLogService.save(log1);
						// 买家冻结预存款减少
						User buyer = obj.getUser();
						buyer.setFreezeBlance(BigDecimal.valueOf(CommUtil
								.subtract(buyer.getFreezeBlance(),
										obj.getTotalPrice())));
						this.userService.update(buyer);
					} else {
						// 卖家预存款增加
						seller.setAvailableBalance(BigDecimal.valueOf(CommUtil
								.add(seller.getAvailableBalance(),
										obj.getTotalPrice())));
						this.userService.update(seller);
						PredepositLog log = new PredepositLog();
						log.setAddTime(new Date());
						log.setPd_log_user(seller);
						log.setPd_op_type("增加");
						log.setPd_log_amount(obj.getTotalPrice());
						log.setPd_log_info("订单" + obj.getOrder_id()
								+ "确认收货增加预存款");
						log.setPd_type("可用预存款");
						this.predepositLogService.save(log);
						// 买家冻结预存款减少
						User buyer = obj.getUser();
						buyer.setFreezeBlance(BigDecimal.valueOf(CommUtil
								.subtract(buyer.getFreezeBlance(),
										obj.getTotalPrice())));
						this.userService.update(buyer);
					}
				}
			}
		}
		String url = "redirect:/weixin/buyer/order.htm";
		return url;
	}

	@SecurityMapping(title = "买家评价", value = "/weixin/buyer/order_evaluate.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/order_evaluate.htm")
	public ModelAndView weixin_order_evaluate(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("weixin/buyer_order_evaluate.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getUser().getId()
				.equals(SecurityUserHolder.getCurrentUser().getId())) {
			mv.addObject("obj", obj);
			if (obj.getOrder_status() >= 50) {
				mv = new JModelAndView("weixin/success.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "订单已经评价！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/weixin/buyer/order.htm");
			}
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/buyer/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "买家评价保存", value = "/weixin/buyer/order_evaluate_save.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/order_evaluate_save.htm")
	public ModelAndView weixin_order_evaluate_save(HttpServletRequest request,
			HttpServletResponse response, String id) throws Exception {
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getUser().getId()
				.equals(SecurityUserHolder.getCurrentUser().getId())) {
			if (obj.getOrder_status() == 40) {
				obj.setOrder_status(50);
				this.orderFormService.update(obj);
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("评价订单");
				ofl.setLog_user(SecurityUserHolder.getCurrentUser());
				ofl.setOf(obj);
				this.orderFormLogService.save(ofl);
				for (GoodsCart gc : obj.getGcs()) {
					Evaluate eva = new Evaluate();
					eva.setAddTime(new Date());
					eva.setEvaluate_goods(gc.getGoods());
					eva.setEvaluate_info(request.getParameter("evaluate_info_"
							+ gc.getId()));
					eva.setEvaluate_buyer_val(CommUtil.null2Int(request
							.getParameter("evaluate_buyer_val" + gc.getId())));
					eva.setDescription_evaluate(BigDecimal.valueOf(CommUtil
							.null2Double(request
									.getParameter("description_evaluate"
											+ gc.getId()))));
					eva.setService_evaluate(BigDecimal.valueOf(CommUtil
							.null2Double(request
									.getParameter("service_evaluate"
											+ gc.getId()))));
					eva.setShip_evaluate(BigDecimal.valueOf(CommUtil
							.null2Double(request.getParameter("ship_evaluate"
									+ gc.getId()))));
					eva.setEvaluate_type("goods");
					eva.setEvaluate_user(SecurityUserHolder.getCurrentUser());
					eva.setOf(obj);
					eva.setGoods_spec(gc.getSpec_info());
					this.evaluateService.save(eva);
					Map params = new HashMap();
					params.put("store_id", obj.getStore().getId());
					List<Evaluate> evas = this.evaluateService
							.query("select obj from Evaluate obj where obj.of.store.id=:store_id",
									params, -1, -1);
					double store_evaluate1 = 0;
					double store_evaluate1_total = 0;
					double description_evaluate = 0;
					double description_evaluate_total = 0;
					double service_evaluate = 0;
					double service_evaluate_total = 0;
					double ship_evaluate = 0;
					double ship_evaluate_total = 0;
					DecimalFormat df = new DecimalFormat("0.0");
					for (Evaluate eva1 : evas) {
						store_evaluate1_total = store_evaluate1_total
								+ eva1.getEvaluate_buyer_val();
						description_evaluate_total = description_evaluate_total
								+ CommUtil.null2Double(eva1
										.getDescription_evaluate());
						service_evaluate_total = service_evaluate_total
								+ CommUtil.null2Double(eva1
										.getService_evaluate());
						ship_evaluate_total = ship_evaluate_total
								+ CommUtil.null2Double(eva1.getShip_evaluate());
					}
					store_evaluate1 = CommUtil.null2Double(df
							.format(store_evaluate1_total / evas.size()));
					description_evaluate = CommUtil.null2Double(df
							.format(description_evaluate_total / evas.size()));
					service_evaluate = CommUtil.null2Double(df
							.format(service_evaluate_total / evas.size()));
					ship_evaluate = CommUtil.null2Double(df
							.format(ship_evaluate_total / evas.size()));
					Store store = obj.getStore();
					store.setStore_credit(store.getStore_credit()
							+ eva.getEvaluate_buyer_val());
					this.storeService.update(store);
					params.clear();
					params.put("store_id", store.getId());
					List<StorePoint> sps = this.storePointService
							.query("select obj from StorePoint obj where obj.store.id=:store_id",
									params, -1, -1);
					StorePoint point = null;
					if (sps.size() > 0) {
						point = sps.get(0);
					} else {
						point = new StorePoint();
					}
					point.setAddTime(new Date());
					point.setStore(store);
					point.setDescription_evaluate(BigDecimal
							.valueOf(description_evaluate));
					point.setService_evaluate(BigDecimal
							.valueOf(service_evaluate));
					point.setShip_evaluate(BigDecimal.valueOf(ship_evaluate));
					point.setStore_evaluate1(BigDecimal
							.valueOf(store_evaluate1));
					if (sps.size() > 0) {
						this.storePointService.update(point);
					} else {
						this.storePointService.save(point);
					}
					// 增加用户积分
					User user = obj.getUser();
					user.setIntegral(user.getIntegral()
							+ this.configService.getSysConfig()
									.getIndentComment());
					this.userService.update(user);
				}
			}
			if (this.configService.getSysConfig().isEmailEnable()) {
				this.send_email(request, obj,
						"email_toseller_evaluate_ok_notify");
			}
		}
		ModelAndView mv = new JModelAndView("weixin/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("op_title", "订单评价成功！");
		mv.addObject("url", CommUtil.getURL(request)
				+ "/weixin/buyer/order.htm");
		return mv;
	}

	/**
	 * 微信查看订单物流
	 * 
	 * @param request
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "订单列表", value = "/weixin/buyer/ship_view.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/ship_view.htm")
	public ModelAndView weixin_ship_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("weixin/ship_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj.getUser().getId()
				.equals(SecurityUserHolder.getCurrentUser().getId())) {
			mv.addObject("obj", obj);
			TransInfo transInfo = this.query_ship_getData(CommUtil
					.null2String(obj.getId()));
			mv.addObject("transInfo", transInfo);
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/weixin/buyer/order.htm");
		}
		return mv;
	}

	private TransInfo query_ship_getData(String id) {
		TransInfo info = new TransInfo();
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		try {
			String query_url = "http://api.kuaidi100.com/api?id="
					+ this.configService.getSysConfig().getKuaidi_id()
					+ "&com="
					+ (obj.getEc() != null ? obj.getEc().getCompany_mark() : "")
					+ "&nu=" + obj.getShipCode() + "&show=0&muti=1&order=asc";
			URL url = new URL(query_url);
			URLConnection con = url.openConnection();
			con.setAllowUserInteraction(false);
			InputStream urlStream = url.openStream();
			String type = con.guessContentTypeFromStream(urlStream);
			String charSet = null;
			if (type == null)
				type = con.getContentType();
			if (type == null || type.trim().length() == 0
					|| type.trim().indexOf("text/html") < 0)
				return info;
			if (type.indexOf("charset=") > 0)
				charSet = type.substring(type.indexOf("charset=") + 8);
			byte b[] = new byte[10000];
			int numRead = urlStream.read(b);
			String content = new String(b, 0, numRead, charSet);
			while (numRead != -1) {
				numRead = urlStream.read(b);
				if (numRead != -1) {
					// String newContent = new String(b, 0, numRead);
					String newContent = new String(b, 0, numRead, charSet);
					content += newContent;
				}
			}
			info = Json.fromJson(TransInfo.class, content);
			urlStream.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return info;
	}

	private void send_email(HttpServletRequest request, OrderForm order,
			String mark) throws Exception {
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template.isOpen()) {
			String email = order.getStore().getUser().getEmail();
			String subject = template.getTitle();
			String path = request.getSession().getServletContext()
					.getRealPath("/")
					+ "/vm/";
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

	private void send_sms(HttpServletRequest request, OrderForm order,
			String mobile, String mark) throws Exception {
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template.isOpen()) {
			String path = request.getSession().getServletContext()
					.getRealPath("/")
					+ "/vm/";
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
