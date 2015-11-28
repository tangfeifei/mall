package com.lakecloud.manage.admin.tools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Complaint;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.Report;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.IComplaintService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IOrderFormService;
import com.lakecloud.foundation.service.IReportService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.IUserService;

@Component
public class StatTools {
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IReportService reportService;
	@Autowired
	private IComplaintService complaintService;

	public int query_store(int count) {
		List<Store> stores = new ArrayList<Store>();
		Map params = new HashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, count);
		params.put("time", cal.getTime());
		stores = this.storeService.query(
				"select obj from Store obj where obj.addTime>=:time", params,
				-1, -1);
		return stores.size();
	}

	public int query_user(int count) {
		List<User> users = new ArrayList<User>();
		Map params = new HashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, count);
		params.put("time", cal.getTime());
		users = this.userService.query(
				"select obj from User obj where obj.addTime>=:time", params,
				-1, -1);
		return users.size();
	}

	public int query_goods(int count) {
		List<Goods> goods = new ArrayList<Goods>();
		Map params = new HashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, count);
		params.put("time", cal.getTime());
		goods = this.goodsService.query(
				"select obj from Goods obj where obj.addTime>=:time", params,
				-1, -1);
		return goods.size();
	}

	public int query_order(int count) {
		List<OrderForm> orders = new ArrayList<OrderForm>();
		Map params = new HashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, count);
		params.put("time", cal.getTime());
		orders = this.orderFormService.query(
				"select obj from OrderForm obj where obj.addTime>=:time",
				params, -1, -1);
		return orders.size();
	}

	public int query_all_user() {
		Map params = new HashMap();
		params.put("userRole", "ADMIN");
		List<User> users = this.userService.query(
				"select obj from User obj where obj.userRole!=:userRole",
				params, -1, -1);
		return users.size();

	}

	public int query_all_goods() {
		List<Goods> goods = this.goodsService.query(
				"select obj from Goods obj", null, -1, -1);
		return goods.size();
	}

	public int query_all_store() {
		List<Store> stores = this.storeService.query(
				"select obj from Store obj", null, -1, -1);
		return stores.size();
	}

	public int query_update_store() {
		List<Store> stores = this.storeService
				.query(
						"select obj from Store obj where obj.update_grade.id is not null",
						null, -1, -1);
		return stores.size();
	}

	public double query_all_amount() {
		double price = 0;
		Map params = new HashMap();
		params.put("order_status", 60);
		List<OrderForm> ofs = this.orderFormService
				.query(
						"select obj from OrderForm obj where obj.order_status=:order_status",
						params, -1, -1);
		for (OrderForm of : ofs) {
			price = CommUtil.null2Double(of.getTotalPrice()) + price;
		}
		return price;
	}

	public int query_complaint(int count) {
		List<Complaint> objs = new ArrayList<Complaint>();
		Map params = new HashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, count);
		params.put("time", cal.getTime());
		params.put("status", 0);
		objs = this.complaintService
				.query(
						"select obj from Complaint obj where obj.addTime>=:time and obj.status=:status",
						params, -1, -1);
		return objs.size();
	}

	public int query_report(int count) {
		List<Report> objs = new ArrayList<Report>();
		Map params = new HashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, count);
		params.put("time", cal.getTime());
		params.put("status", 0);
		objs = this.reportService
				.query(
						"select obj from Report obj where obj.addTime>=:time and obj.status=:status",
						params, -1, -1);
		return objs.size();
	}
}
