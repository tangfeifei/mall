package com.lakecloud.foundation.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 商城统计类，每个30分钟统计一次,超级管理员登录后台后显示该数据信息
 
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "store_stat")
public class StoreStat extends IdEntity {
	private int week_user;// 一周新增会员
	private int week_goods;// 一周新增商品数
	private int week_store;// 一周新增点店铺数
	private int week_order;// 一周新增订单数
	private int week_complaint;// 一周投诉数
	private int week_report;// 一周举报数
	private int all_user;// 会员总数
	private int all_store;// 店铺总数
	private int store_update;// 店铺升级申请数
	private int all_goods;// 商品总数
	@Column(precision = 12, scale = 2)
	private BigDecimal order_amount;// 订单总金额
	private Date next_time;// 下次统计时间

	public int getWeek_user() {
		return week_user;
	}

	public void setWeek_user(int week_user) {
		this.week_user = week_user;
	}

	public int getWeek_goods() {
		return week_goods;
	}

	public void setWeek_goods(int week_goods) {
		this.week_goods = week_goods;
	}

	public int getWeek_store() {
		return week_store;
	}

	public void setWeek_store(int week_store) {
		this.week_store = week_store;
	}

	public int getWeek_order() {
		return week_order;
	}

	public void setWeek_order(int week_order) {
		this.week_order = week_order;
	}

	public int getWeek_complaint() {
		return week_complaint;
	}

	public void setWeek_complaint(int week_complaint) {
		this.week_complaint = week_complaint;
	}

	public int getWeek_report() {
		return week_report;
	}

	public void setWeek_report(int week_report) {
		this.week_report = week_report;
	}

	public int getAll_user() {
		return all_user;
	}

	public void setAll_user(int all_user) {
		this.all_user = all_user;
	}

	public int getAll_store() {
		return all_store;
	}

	public void setAll_store(int all_store) {
		this.all_store = all_store;
	}

	public int getStore_update() {
		return store_update;
	}

	public void setStore_update(int store_update) {
		this.store_update = store_update;
	}

	public int getAll_goods() {
		return all_goods;
	}

	public void setAll_goods(int all_goods) {
		this.all_goods = all_goods;
	}

	public BigDecimal getOrder_amount() {
		return order_amount;
	}

	public void setOrder_amount(BigDecimal order_amount) {
		this.order_amount = order_amount;
	}

	public Date getNext_time() {
		return next_time;
	}

	public void setNext_time(Date next_time) {
		this.next_time = next_time;
	}
}
