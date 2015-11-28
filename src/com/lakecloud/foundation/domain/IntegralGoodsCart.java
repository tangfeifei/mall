package com.lakecloud.foundation.domain;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 积分商城兑换购物车
 
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "integral_goodscart")
public class IntegralGoodsCart extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private IntegralGoods goods;// 兑换的礼品
	private int count;// 兑换数量
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private IntegralGoodsOrder order;// 兑换订单
	@Column(precision = 12, scale = 2)
	private BigDecimal trans_fee;// 购物车运费
	private int integral;// 积分小计

	public IntegralGoodsOrder getOrder() {
		return order;
	}

	public void setOrder(IntegralGoodsOrder order) {
		this.order = order;
	}

	public BigDecimal getTrans_fee() {
		return trans_fee;
	}

	public void setTrans_fee(BigDecimal trans_fee) {
		this.trans_fee = trans_fee;
	}

	public int getIntegral() {
		return integral;
	}

	public void setIntegral(int integral) {
		this.integral = integral;
	}

	public IntegralGoods getGoods() {
		return goods;
	}

	public void setGoods(IntegralGoods goods) {
		this.goods = goods;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
