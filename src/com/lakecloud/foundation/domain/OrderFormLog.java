package com.lakecloud.foundation.domain;

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
 * @info 订单日志类
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "order_log")
public class OrderFormLog extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private OrderForm of;// 对应的订单
	private String log_info;// 日志信息
	@ManyToOne(fetch = FetchType.LAZY)
	private User log_user;// 日志操作员
	@Lob
	@Column(columnDefinition = "LongText")
	private String state_info;// 操作日志详情

	public String getState_info() {
		return state_info;
	}

	public void setState_info(String state_info) {
		this.state_info = state_info;
	}

	public OrderForm getOf() {
		return of;
	}

	public void setOf(OrderForm of) {
		this.of = of;
	}

	public String getLog_info() {
		return log_info;
	}

	public void setLog_info(String log_info) {
		this.log_info = log_info;
	}

	public User getLog_user() {
		return log_user;
	}

	public void setLog_user(User log_user) {
		this.log_user = log_user;
	}

}
