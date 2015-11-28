package com.lakecloud.manage.timer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Activity;
import com.lakecloud.foundation.domain.ActivityGoods;
import com.lakecloud.foundation.domain.DeliveryGoods;
import com.lakecloud.foundation.domain.Evaluate;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.Group;
import com.lakecloud.foundation.domain.GroupGoods;
import com.lakecloud.foundation.domain.MobileVerifyCode;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.OrderFormLog;
import com.lakecloud.foundation.domain.Payment;
import com.lakecloud.foundation.domain.PredepositLog;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.StoreClass;
import com.lakecloud.foundation.domain.StorePoint;
import com.lakecloud.foundation.domain.StoreStat;
import com.lakecloud.foundation.domain.Template;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.IActivityGoodsService;
import com.lakecloud.foundation.service.IActivityService;
import com.lakecloud.foundation.service.IDeliveryGoodsService;
import com.lakecloud.foundation.service.IEvaluateService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IGroupGoodsService;
import com.lakecloud.foundation.service.IGroupService;
import com.lakecloud.foundation.service.IMobileVerifyCodeService;
import com.lakecloud.foundation.service.IOrderFormLogService;
import com.lakecloud.foundation.service.IOrderFormService;
import com.lakecloud.foundation.service.IPaymentService;
import com.lakecloud.foundation.service.IPredepositLogService;
import com.lakecloud.foundation.service.IStoreClassService;
import com.lakecloud.foundation.service.IStorePointService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.IStoreStatService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.ITemplateService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.manage.admin.tools.MsgTools;
import com.lakecloud.manage.admin.tools.StatTools;

/**
 * @info 系统定制器类，每间隔半小时执行一次，用在数据统计及团购开启关闭上,其他按小时计算的定制器都可以在这里增加代码控制
  
 * 
 */
@Component(value = "shop_stat")
public class StatManageAction {
	@Autowired
	private IStoreStatService storeStatService;
	@Autowired
	private StatTools statTools;
	@Autowired
	private IMobileVerifyCodeService mobileverifycodeService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IStorePointService storePointService;
	@Autowired
	private IGroupService groupService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IActivityService activityService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IDeliveryGoodsService deliveryGoodsService;
	@Autowired
	private IStoreClassService storeClassService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private MsgTools msgTools;

	private void execute() throws Exception {
		// System.out.println(System.getProperty("lakecloud.root"));
		// 统计信息
		List<StoreStat> stats = this.storeStatService.query(
				"select obj from StoreStat obj", null, -1, -1);
		StoreStat stat = null;
		if (stats.size() > 0) {
			stat = stats.get(0);
		} else {
			stat = new StoreStat();
		}
		stat.setAddTime(new Date());
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 30);
		stat.setNext_time(cal.getTime());
		stat.setWeek_complaint(this.statTools.query_complaint(-7));
		stat.setWeek_goods(this.statTools.query_goods(-7));
		stat.setWeek_order(this.statTools.query_order(-7));
		stat.setWeek_report(this.statTools.query_report(-7));
		stat.setWeek_store(this.statTools.query_store(-7));
		stat.setWeek_user(this.statTools.query_user(-7));
		stat.setAll_goods(this.statTools.query_all_goods());
		stat.setAll_store(this.statTools.query_all_store());
		stat.setAll_user(this.statTools.query_all_user());
		stat.setStore_update(this.statTools.query_update_store());
		stat.setOrder_amount(BigDecimal.valueOf(this.statTools
				.query_all_amount()));
		if (stats.size() > 0) {
			this.storeStatService.update(stat);
		} else
			this.storeStatService.save(stat);
		// 删除验证码信息
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE, -15);
		Map params = new HashMap();
		params.put("time", cal.getTime());
		List<MobileVerifyCode> mvcs = this.mobileverifycodeService
				.query(
						"select obj from MobileVerifyCode obj where obj.addTime<=:time",
						params, -1, -1);
		for (MobileVerifyCode mvc : mvcs) {
			this.mobileverifycodeService.delete(mvc.getId());
		}
		// 统计店铺信誉评分
		List<Store> stores = this.storeService.query(
				"select obj from Store obj", null, -1, -1);
		for (Store store : stores) {
			params.clear();
			params.put("store_id", store.getId());
			List<Evaluate> evas = this.evaluateService
					.query(
							"select obj from Evaluate obj where obj.of.store.id=:store_id",
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
						+ CommUtil.null2Double(eva1.getDescription_evaluate());
				service_evaluate_total = service_evaluate_total
						+ CommUtil.null2Double(eva1.getService_evaluate());
				ship_evaluate_total = ship_evaluate_total
						+ CommUtil.null2Double(eva1.getShip_evaluate());
			}
			store_evaluate1 = CommUtil.null2Double(df
					.format(store_evaluate1_total / evas.size()));
			description_evaluate = CommUtil.null2Double(df
					.format(description_evaluate_total / evas.size()));
			service_evaluate = CommUtil.null2Double(df
					.format(service_evaluate_total / evas.size()));
			ship_evaluate = CommUtil.null2Double(df.format(ship_evaluate_total
					/ evas.size()));
			double description_evaluate_halfyear = 0;
			double service_evaluate_halfyear = 0;
			double ship_evaluate_halfyear = 0;
			int description_evaluate_halfyear_count5 = 0;// 半年内描述评价4-5分人数
			int description_evaluate_halfyear_count4 = 0;// 半年内描述评价3-4分人数
			int description_evaluate_halfyear_count3 = 0;// 半年内描述评价2-3分人数
			int description_evaluate_halfyear_count2 = 0;// 半年内描述评价1-2分人数
			int description_evaluate_halfyear_count1 = 0;// 半年内描述评价0-1分人数
			int service_evaluate_halfyear_count5 = 0;// 半年内服务态度评价4-5分人数
			int service_evaluate_halfyear_count4 = 0;// 半年内服务态度评价3-4分人数
			int service_evaluate_halfyear_count3 = 0;// 半年内服务态度评价2-3分人数
			int service_evaluate_halfyear_count2 = 0;// 半年内服务态度评价1-2分人数
			int service_evaluate_halfyear_count1 = 0;// 半年内服务态度评价0-1分人数
			int ship_evaluate_halfyear_count5 = 0;// 半年内发货速度评价4-5分人数
			int ship_evaluate_halfyear_count4 = 0;// 半年内发货速度评价3-4分人数
			int ship_evaluate_halfyear_count3 = 0;// 半年内发货速度评价2-3分人数
			int ship_evaluate_halfyear_count2 = 0;// 半年内发货速度评价1-2分人数
			int ship_evaluate_halfyear_count1 = 0;// 半年内发货速度评价0-1分人数
			Calendar cal1 = Calendar.getInstance();
			cal1.add(Calendar.MONTH, -6);
			params.clear();
			params.put("store_id", store.getId());
			params.put("addTime", cal1.getTime());
			evas = this.evaluateService
					.query(
							"select obj from Evaluate obj where obj.of.store.id=:store_id and obj.addTime>=:addTime",
							params, -1, -1);
			for (Evaluate eva : evas) {
				description_evaluate_halfyear = description_evaluate_halfyear
						+ CommUtil.null2Double(eva.getDescription_evaluate());
				service_evaluate_halfyear = service_evaluate_halfyear
						+ CommUtil.null2Double(eva.getService_evaluate());
				ship_evaluate_halfyear = ship_evaluate_halfyear
						+ CommUtil.null2Double(eva.getService_evaluate());
				if (CommUtil.null2Double(eva.getDescription_evaluate()) >= 4) {
					description_evaluate_halfyear_count5++;
				}
				if (CommUtil.null2Double(eva.getDescription_evaluate()) >= 3
						&& CommUtil.null2Double(eva.getDescription_evaluate()) < 4) {
					description_evaluate_halfyear_count4++;
				}
				if (CommUtil.null2Double(eva.getDescription_evaluate()) >= 2
						&& CommUtil.null2Double(eva.getDescription_evaluate()) < 3) {
					description_evaluate_halfyear_count3++;
				}
				if (CommUtil.null2Double(eva.getDescription_evaluate()) >= 1
						&& CommUtil.null2Double(eva.getDescription_evaluate()) < 2) {
					description_evaluate_halfyear_count2++;
				}
				if (CommUtil.null2Double(eva.getDescription_evaluate()) >= 0
						&& CommUtil.null2Double(eva.getDescription_evaluate()) < 1) {
					description_evaluate_halfyear_count1++;
				}
				if (CommUtil.null2Double(eva.getService_evaluate()) >= 4) {
					service_evaluate_halfyear_count5++;
				}
				if (CommUtil.null2Double(eva.getService_evaluate()) >= 3
						&& CommUtil.null2Double(eva.getService_evaluate()) < 4) {
					service_evaluate_halfyear_count4++;
				}
				if (CommUtil.null2Double(eva.getService_evaluate()) >= 2
						&& CommUtil.null2Double(eva.getService_evaluate()) < 3) {
					service_evaluate_halfyear_count3++;
				}
				if (CommUtil.null2Double(eva.getService_evaluate()) >= 1
						&& CommUtil.null2Double(eva.getService_evaluate()) < 2) {
					service_evaluate_halfyear_count2++;
				}
				if (CommUtil.null2Double(eva.getService_evaluate()) >= 0
						&& CommUtil.null2Double(eva.getService_evaluate()) < 1) {
					service_evaluate_halfyear_count1++;
				}
				if (CommUtil.null2Double(eva.getShip_evaluate()) >= 4) {
					ship_evaluate_halfyear_count5++;
				}
				if (CommUtil.null2Double(eva.getShip_evaluate()) >= 3
						&& CommUtil.null2Double(eva.getShip_evaluate()) < 4) {
					ship_evaluate_halfyear_count4++;
				}
				if (CommUtil.null2Double(eva.getShip_evaluate()) >= 2
						&& CommUtil.null2Double(eva.getShip_evaluate()) < 3) {
					ship_evaluate_halfyear_count3++;
				}
				if (CommUtil.null2Double(eva.getShip_evaluate()) >= 1
						&& CommUtil.null2Double(eva.getShip_evaluate()) < 2) {
					ship_evaluate_halfyear_count2++;
				}
				if (CommUtil.null2Double(eva.getShip_evaluate()) >= 0
						&& CommUtil.null2Double(eva.getShip_evaluate()) < 1) {
					ship_evaluate_halfyear_count1++;
				}
			}
			if (evas.size() > 0) {
				description_evaluate_halfyear = description_evaluate_halfyear
						/ evas.size();
				service_evaluate_halfyear = service_evaluate_halfyear
						/ evas.size();
				ship_evaluate_halfyear = ship_evaluate_halfyear / evas.size();
			}
			params.clear();
			params.put("store_id", store.getId());
			List<StorePoint> sps = this.storePointService
					.query(
							"select obj from StorePoint obj where obj.store.id=:store_id",
							params, -1, -1);
			StorePoint point = null;
			if (sps.size() > 0) {
				point = sps.get(0);
			} else {
				point = new StorePoint();
			}
			point.setStatTime(new Date());
			point.setStore(store);
			point.setDescription_evaluate(BigDecimal
					.valueOf(description_evaluate));
			point.setService_evaluate(BigDecimal.valueOf(service_evaluate));
			point.setShip_evaluate(BigDecimal.valueOf(ship_evaluate));
			point.setStore_evaluate1(BigDecimal.valueOf(store_evaluate1));
			point.setDescription_evaluate_halfyear(BigDecimal
					.valueOf(description_evaluate_halfyear));
			point
					.setDescription_evaluate_halfyear_count1(description_evaluate_halfyear_count1);
			point
					.setDescription_evaluate_halfyear_count2(description_evaluate_halfyear_count2);
			point
					.setDescription_evaluate_halfyear_count3(description_evaluate_halfyear_count3);
			point
					.setDescription_evaluate_halfyear_count4(description_evaluate_halfyear_count4);
			point
					.setDescription_evaluate_halfyear_count5(description_evaluate_halfyear_count5);
			point.setService_evaluate_halfyear(BigDecimal
					.valueOf(service_evaluate_halfyear));
			point
					.setService_evaluate_halfyear_count1(service_evaluate_halfyear_count1);
			point
					.setService_evaluate_halfyear_count2(service_evaluate_halfyear_count2);
			point
					.setService_evaluate_halfyear_count3(service_evaluate_halfyear_count3);
			point
					.setService_evaluate_halfyear_count4(service_evaluate_halfyear_count4);
			point
					.setService_evaluate_halfyear_count5(service_evaluate_halfyear_count5);
			point.setShip_evaluate_halfyear(BigDecimal.valueOf(ship_evaluate));
			point
					.setShip_evaluate_halfyear_count1(ship_evaluate_halfyear_count1);
			point
					.setShip_evaluate_halfyear_count2(ship_evaluate_halfyear_count2);
			point
					.setShip_evaluate_halfyear_count3(ship_evaluate_halfyear_count3);
			point
					.setShip_evaluate_halfyear_count4(ship_evaluate_halfyear_count4);
			point
					.setShip_evaluate_halfyear_count5(ship_evaluate_halfyear_count5);
			if (sps.size() > 0) {
				this.storePointService.update(point);
			} else {
				this.storePointService.save(point);
			}
		}
		// 统计店铺分类的评分信息
		List<StoreClass> scs = this.storeClassService.query(
				"select obj from StoreClass obj", null, -1, -1);
		for (StoreClass sc : scs) {
			double description_evaluate = 0;
			double service_evaluate = 0;
			double ship_evaluate = 0;
			params.clear();
			params.put("sc_id", sc.getId());
			List<StorePoint> sp_list = this.storePointService
					.query(
							"select obj from StorePoint obj where obj.store.sc.id=:sc_id",
							params, -1, -1);
			for (StorePoint sp : sp_list) {
				description_evaluate = CommUtil.add(description_evaluate, sp
						.getDescription_evaluate());
				service_evaluate = CommUtil.add(service_evaluate, sp
						.getService_evaluate());
				ship_evaluate = CommUtil.add(ship_evaluate, sp
						.getShip_evaluate());
			}
			sc.setDescription_evaluate(BigDecimal.valueOf(CommUtil.div(
					description_evaluate, sp_list.size())));
			sc.setService_evaluate(BigDecimal.valueOf(CommUtil.div(
					service_evaluate, sp_list.size())));
			sc.setShip_evaluate(BigDecimal.valueOf(CommUtil.div(ship_evaluate,
					sp_list.size())));
			this.storeClassService.update(sc);
		}
		// 团购监控，团购添加时候可以控制到小时，每个半小时统计一次团购是否过期，是否开启
		List<Group> groups = this.groupService.query(
				"select obj from Group obj order by obj.addTime", null, -1, -1);
		for (Group group : groups) {
			if (group.getBeginTime().before(new Date())
					&& group.getEndTime().after(new Date())) {
				group.setStatus(0);
				this.groupService.update(group);
			}
			if (group.getEndTime().before(new Date())) {
				group.setStatus(-2);
				this.groupService.update(group);
				for (GroupGoods gg : group.getGg_list()) {
					gg.setGg_status(-2);
					this.groupGoodsService.update(gg);
					Goods goods = gg.getGg_goods();
					goods.setGroup_buy(0);
					goods.setGoods_current_price(goods.getStore_price());
					this.goodsService.update(goods);
				}
			}
		}
		// 商城活动监控，自动关闭过期的商城活动,同时恢复对应的商品状态、价格
		params.clear();
		params.put("ac_end_time", new Date());
		params.put("ac_status", 1);
		List<Activity> acts = this.activityService
				.query(
						"select obj from Activity obj where obj.ac_end_time<=:ac_end_time and obj.ac_status=:ac_status",
						params, -1, -1);
		for (Activity act : acts) {
			act.setAc_status(0);
			this.activityService.update(act);
			for (ActivityGoods ac : act.getAgs()) {
				ac.setAg_status(-2);// 到期关闭
				this.activityGoodsService.update(ac);
				Goods goods = ac.getAg_goods();
				goods.setActivity_status(0);
				goods.setGoods_current_price(goods.getStore_price());
				this.goodsService.update(goods);
			}
		}
		// 检测给予短信、邮件提醒即将确认自动收货的订单信息
		int auto_order_notice = this.configService.getSysConfig()
				.getAuto_order_notice();
		cal = Calendar.getInstance();
		params.clear();
		cal.add(Calendar.DAY_OF_YEAR, -auto_order_notice);
		params.put("shipTime", cal.getTime());
		params.put("auto_confirm_email", true);
		params.put("auto_confirm_sms", true);
		List<OrderForm> notice_ofs = this.orderFormService
				.query(
						"select obj from OrderForm obj where obj.shipTime<=:shipTime and (obj.auto_confirm_email=:auto_confirm_email or obj.auto_confirm_sms=:auto_confirm_sms)",
						params, -1, -1);
		for (OrderForm of : notice_ofs) {
			if (!of.isAuto_confirm_email()) {
				boolean email = this.send_email(of,
						"email_tobuyer_order_will_confirm_notify");
				if (email) {
					of.setAuto_confirm_email(true);
					this.orderFormService.update(of);
				}
			}
			if (!of.isAuto_confirm_sms()) {
				boolean sms = this.send_sms(of, of.getUser().getMobile(),
						"sms_tobuyer_order_will_confirm_notify");
				if (sms) {
					of.setAuto_confirm_sms(true);
					this.orderFormService.update(of);
				}
			}
		}
		// 检测默认自动收货的订单信息
		int auto_order_confirm = this.configService.getSysConfig()
				.getAuto_order_confirm();
		cal = Calendar.getInstance();
		params.clear();
		cal.add(Calendar.DAY_OF_YEAR, -auto_order_confirm);
		params.put("shipTime", cal.getTime());
		List<OrderForm> confirm_ofs = this.orderFormService.query(
				"select obj from OrderForm obj where obj.shipTime<=:shipTime",
				params, -1, -1);
		for (OrderForm of : confirm_ofs) {
			of.setOrder_status(40);// 自动确认收货
			boolean ret = this.orderFormService.update(of);
			if (ret) {
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("确认收货");
				ofl.setLog_user(SecurityUserHolder.getCurrentUser());
				ofl.setOf(of);
				this.orderFormLogService.save(ofl);
				if (this.configService.getSysConfig().isEmailEnable()) {
					this.send_email(of,
							"email_toseller_order_receive_ok_notify");
				}
				if (this.configService.getSysConfig().isSmsEnbale()) {
					this.send_sms(of, of.getStore().getUser().getMobile(),
							"sms_toseller_order_receive_ok_notify");
				}
				if (of.getPayment().getMark().equals("balance")) {// 如果是预存款，则自动完成预存款转移
					User seller = this.userService.getObjById(of.getStore()
							.getUser().getId());
					if (this.configService.getSysConfig().getBalance_fenrun() == 1) {// 系统开启预存款分润，实行分润转账处理
						// 卖家预存款增加
						params.clear();
						params.put("type", "admin");
						params.put("mark", "alipay");
						List<Payment> payments = this.paymentService
								.query(
										"select obj from Payment obj where obj.type=:type and obj.mark=:mark",
										params, -1, -1);
						Payment shop_payment = new Payment();
						if (payments.size() > 0) {
							shop_payment = payments.get(0);
						}
						// 按照分润比例计算平台应得利润金额
						double shop_availableBalance = CommUtil.null2Double(of
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
						log.setPd_log_info("自动确认收货平台分润获得预存款，订单"
								+ of.getOrder_id());
						log.setPd_type("可用预存款");
						this.predepositLogService.save(log);
						// 减去平台应得利润金额，剩下的就是商家应得利润
						double seller_availableBalance = CommUtil
								.null2Double(of.getTotalPrice())
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
						log1.setPd_log_info("自动确认收货增加预存款，订单号"
								+ of.getOrder_id());
						log1.setPd_type("可用预存款");
						this.predepositLogService.save(log1);
						// 买家冻结预存款减少
						User buyer = of.getUser();
						buyer.setFreezeBlance(BigDecimal.valueOf(CommUtil
								.subtract(buyer.getFreezeBlance(), of
										.getTotalPrice())));
						this.userService.update(buyer);
					} else {
						// 卖家预存款增加
						seller.setAvailableBalance(BigDecimal.valueOf(CommUtil
								.add(seller.getAvailableBalance(), of
										.getTotalPrice())));
						this.userService.update(seller);
						PredepositLog log = new PredepositLog();
						log.setAddTime(new Date());
						log.setPd_log_user(seller);
						log.setPd_op_type("增加");
						log.setPd_log_amount(of.getTotalPrice());
						log.setPd_log_info("自动确认收货增加预存款,订单号" + of.getOrder_id()
								+ "");
						log.setPd_type("可用预存款");
						this.predepositLogService.save(log);
						// 买家冻结预存款减少
						User buyer = of.getUser();
						buyer.setFreezeBlance(BigDecimal.valueOf(CommUtil
								.subtract(buyer.getFreezeBlance(), of
										.getTotalPrice())));
						this.userService.update(buyer);
					}
				}
			}
		}
		// 到达设定时间，系统自动关闭订单相互评价功能
		int auto_order_evaluate = this.configService.getSysConfig()
				.getAuto_order_evaluate();
		cal = Calendar.getInstance();
		params.clear();
		cal.add(Calendar.DAY_OF_YEAR, -auto_order_evaluate);
		params.put("auto_order_evaluate", cal.getTime());
		params.put("order_status_40", 40);
		params.put("order_status_47", 47);
		params.put("order_status_48", 48);
		params.put("order_status_49", 49);
		params.put("order_status_50", 50);
		params.put("order_status_60", 60);
		List<OrderForm> confirm_evaluate_ofs = this.orderFormService
				.query(
						"select obj from OrderForm obj where obj.return_shipTime<=:return_shipTime and obj.order_status>=:order_status_40 "
								+ "and obj.order_status!=:order_status_47 and obj.order_status!=:order_status_48 and obj.order_status!=:order_status_49 "
								+ "and obj.order_status!=:order_status_50 and obj.order_status!=:order_status_60",
						params, -1, -1);
		for (OrderForm order : confirm_evaluate_ofs) {
			order.setOrder_status(65);
			this.orderFormService.update(order);
		}
		// 申请退货后到达设定时间，未能输入退货物流单号和物流公司，订单变为已收货状态并等待评价，订单状态为49
		int auto_order_return = this.configService.getSysConfig()
				.getAuto_order_return();
		cal = Calendar.getInstance();
		params.clear();
		cal.add(Calendar.DAY_OF_YEAR, -auto_order_return);
		params.put("return_shipTime", cal.getTime());
		params.put("order_status", 46);
		List<OrderForm> confirm_return_ofs = this.orderFormService
				.query(
						"select obj from OrderForm obj where obj.return_shipTime<=:return_shipTime and obj.order_status=:order_status",
						params, -1, -1);
		for (OrderForm order : confirm_return_ofs) {
			order.setOrder_status(49);
			this.orderFormService.update(order);
		}

		// 自动恢复已经到期的买就送商品
		params.clear();
		params.put("delivery_end_time", new Date());
		List<DeliveryGoods> dgs = this.deliveryGoodsService
				.query(
						"select obj from DeliveryGoods obj where obj.d_goods.goods_store.delivery_end_time<:delivery_end_time",
						params, -1, -1);
		for (DeliveryGoods dg : dgs) {
			dg.setD_status(-2);// 设置买就送商品已经过期
			this.deliveryGoodsService.update(dg);
			Goods goods = dg.getD_goods();
			goods.setDelivery_status(0);// 恢复对应的商品信息，可以重新进入买就送
			this.goodsService.update(goods);
		}
		// 定时器自动取消到期的组合销售
		params.clear();
		params.put("combin_end_time", new Date());
		stores = this.storeService
				.query(
						"select obj from Store obj where obj.combin_end_time<=:combin_end_time",
						params, -1, -1);
		for (Store store : stores) {
			for (Goods goods : store.getGoods_list()) {
				if (goods.getCombin_status() != 0) {
					goods.setCombin_begin_time(null);
					goods.setCombin_end_time(null);
					goods.setCombin_price(null);
					goods.setCombin_status(0);
					goods.getCombin_goods().clear();
					this.goodsService.update(goods);
				}
			}
		}
		// 统计所有商品的描述相符评分
		List<Goods> goods_list = this.evaluateService.query_goods(
				"select distinct obj.evaluate_goods from Evaluate obj ", null,
				-1, -1);
		for (Goods goods : goods_list) {
			double description_evaluate = 0;
			params.clear();
			params.put("evaluate_goods_id", goods.getId());
			List<Evaluate> eva_list = this.evaluateService
					.query(
							"select obj from Evaluate obj where obj.evaluate_goods.id=:evaluate_goods_id",
							params, -1, -1);
			for (Evaluate eva : eva_list) {
				description_evaluate = CommUtil.add(eva
						.getDescription_evaluate(), description_evaluate);
			}

			description_evaluate = CommUtil.div(description_evaluate, eva_list
					.size());
			goods.setDescription_evaluate(BigDecimal
					.valueOf(description_evaluate));
			this.goodsService.update(goods);
		}
		// 自动处理过期的微信店铺
		params.clear();
		params.put("weixin_status", 1);
		List<Store> store_list = this.storeService
				.query(
						"select obj from Store obj where obj.weixin_status=:weixin_status",
						params, -1, -1);
		for (Store store : store_list) {
			if (store.getWeixin_end_time().before(new Date())) {
				store.setWeixin_status(2);// 设置微信店铺状态为过期状态
				this.storeService.update(store);
			}
		}
	}

	private boolean send_email(OrderForm order, String mark) throws Exception {
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template.isOpen()) {
			String email = order.getStore().getUser().getEmail();
			String subject = template.getTitle();
			String path = System.getProperty("lakecloud.root") + "vm"
					+ File.separator;
			PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(path + "msg.vm", false), "UTF-8"));
			pwrite.print(template.getContent());
			pwrite.flush();
			pwrite.close();
			// 生成模板
			Properties p = new Properties();
			p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, System
					.getProperty("lakecloud.root")
					+ "vm" + File.separator);
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
			context.put("webPath", this.configService.getSysConfig()
					.getAddress());
			context.put("order", order);
			StringWriter writer = new StringWriter();
			blank.merge(context, writer);
			// System.out.println(writer.toString());
			String content = writer.toString();
			boolean ret = this.msgTools.sendEmail(email, subject, content);
			return ret;
		} else
			return false;
	}

	private boolean send_sms(OrderForm order, String mobile, String mark)
			throws Exception {
		Template template = this.templateService.getObjByProperty("mark", mark);
		if (template.isOpen()) {
			String path = System.getProperty("lakecloud.root") + "vm"
					+ File.separator;
			PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(path + "msg.vm", false), "UTF-8"));
			pwrite.print(template.getContent());
			pwrite.flush();
			pwrite.close();
			// 生成模板
			Properties p = new Properties();
			p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, System
					.getProperty("lakecloud.root")
					+ "vm" + File.separator);
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
			context.put("webPath", this.configService.getSysConfig()
					.getAddress());
			context.put("order", order);
			StringWriter writer = new StringWriter();
			blank.merge(context, writer);
			// System.out.println(writer.toString());
			String content = writer.toString();
			boolean ret = this.msgTools.sendSMS(mobile, content);
			return ret;
		} else
			return false;
	}

}
