package com.lakecloud.foundation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 系统优惠券，管理系统优惠券信息,优惠券由平台管理员发放
 
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "coupon")
public class Coupon extends IdEntity {
	private String coupon_name;// 优惠券名称
	@Column(precision = 12, scale = 2)
	private BigDecimal coupon_amount;// 优惠券金额
	@Temporal(TemporalType.DATE)
	private Date coupon_begin_time;// 优惠券使用开始时间
	@Temporal(TemporalType.DATE)
	private Date coupon_end_time;// 优惠券使用结束时间
	private int coupon_count;// 优惠券发行数量
	@Column(precision = 12, scale = 2)
	private BigDecimal coupon_order_amount;// 优惠券使用的订单金额，订单满足该金额时才可以使用该优惠券
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Accessory coupon_acc;// 优惠券图片
	@OneToMany(mappedBy = "coupon", cascade = CascadeType.REMOVE)
	private List<CouponInfo> couponinfos = new ArrayList<CouponInfo>();

	public String getCoupon_name() {
		return coupon_name;
	}

	public void setCoupon_name(String coupon_name) {
		this.coupon_name = coupon_name;
	}

	public BigDecimal getCoupon_amount() {
		return coupon_amount;
	}

	public void setCoupon_amount(BigDecimal coupon_amount) {
		this.coupon_amount = coupon_amount;
	}

	public Date getCoupon_begin_time() {
		return coupon_begin_time;
	}

	public void setCoupon_begin_time(Date coupon_begin_time) {
		this.coupon_begin_time = coupon_begin_time;
	}

	public Date getCoupon_end_time() {
		return coupon_end_time;
	}

	public void setCoupon_end_time(Date coupon_end_time) {
		this.coupon_end_time = coupon_end_time;
	}

	public int getCoupon_count() {
		return coupon_count;
	}

	public void setCoupon_count(int coupon_count) {
		this.coupon_count = coupon_count;
	}

	public BigDecimal getCoupon_order_amount() {
		return coupon_order_amount;
	}

	public void setCoupon_order_amount(BigDecimal coupon_order_amount) {
		this.coupon_order_amount = coupon_order_amount;
	}

	public Accessory getCoupon_acc() {
		return coupon_acc;
	}

	public void setCoupon_acc(Accessory coupon_acc) {
		this.coupon_acc = coupon_acc;
	}

	public List<CouponInfo> getCouponinfos() {
		return couponinfos;
	}

	public void setCouponinfos(List<CouponInfo> couponinfos) {
		this.couponinfos = couponinfos;
	}

}
