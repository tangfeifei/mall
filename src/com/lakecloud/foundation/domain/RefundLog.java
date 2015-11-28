package com.lakecloud.foundation.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 退款日志类,用来记录店铺对买家的退款日志信息

 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "refund_log")
public class RefundLog extends IdEntity {
	private String refund_id;// 退款编号
	@ManyToOne(fetch = FetchType.LAZY)
	private OrderForm of;// 对应的订单
	private String refund_log;// 日志信息
	private String refund_type;// 退款方式
	@Column(precision = 12, scale = 2)
	private BigDecimal refund;// 退款金额
	@ManyToOne(fetch = FetchType.LAZY)
	private User refund_user;// 日志操作员

	@Lob
	@Column(columnDefinition = "LongText")
	public OrderForm getOf() {
		return of;
	}

	public void setOf(OrderForm of) {
		this.of = of;
	}

	public String getRefund_log() {
		return refund_log;
	}

	public void setRefund_log(String refund_log) {
		this.refund_log = refund_log;
	}

	public BigDecimal getRefund() {
		return refund;
	}

	public void setRefund(BigDecimal refund) {
		this.refund = refund;
	}

	public User getRefund_user() {
		return refund_user;
	}

	public void setRefund_user(User refund_user) {
		this.refund_user = refund_user;
	}

	public String getRefund_type() {
		return refund_type;
	}

	public void setRefund_type(String refund_type) {
		this.refund_type = refund_type;
	}

	public String getRefund_id() {
		return refund_id;
	}

	public void setRefund_id(String refund_id) {
		this.refund_id = refund_id;
	}
}
