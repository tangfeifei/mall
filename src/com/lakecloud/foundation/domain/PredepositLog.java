package com.lakecloud.foundation.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 预存款日志,记录所有系统预存款信息
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "predeposit_Log")
public class PredepositLog extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private User pd_log_user;// 日志操作用户
	@Column(precision = 12, scale = 2)
	private BigDecimal pd_log_amount;// 日志操作金额
	private String pd_type;// 预存款类型，分为可用预存款，冻结预存款
	private String pd_op_type;// 操作类型，分为充值、提现、消费、兑换金币、人工操作
	@ManyToOne(fetch = FetchType.LAZY)
	private User pd_log_admin;// 操作管理员
	@Column(columnDefinition = "LongText")
	private String pd_log_info;// 日志信息
	@OneToOne(fetch = FetchType.LAZY)
	private Predeposit predeposit;// 对应的预存款记录

	public Predeposit getPredeposit() {
		return predeposit;
	}

	public void setPredeposit(Predeposit predeposit) {
		this.predeposit = predeposit;
	}

	public User getPd_log_user() {
		return pd_log_user;
	}

	public void setPd_log_user(User pd_log_user) {
		this.pd_log_user = pd_log_user;
	}

	public BigDecimal getPd_log_amount() {
		return pd_log_amount;
	}

	public void setPd_log_amount(BigDecimal pd_log_amount) {
		this.pd_log_amount = pd_log_amount;
	}

	public String getPd_type() {
		return pd_type;
	}

	public void setPd_type(String pd_type) {
		this.pd_type = pd_type;
	}

	public String getPd_op_type() {
		return pd_op_type;
	}

	public void setPd_op_type(String pd_op_type) {
		this.pd_op_type = pd_op_type;
	}

	public User getPd_log_admin() {
		return pd_log_admin;
	}

	public void setPd_log_admin(User pd_log_admin) {
		this.pd_log_admin = pd_log_admin;
	}

	public String getPd_log_info() {
		return pd_log_info;
	}

	public void setPd_log_info(String pd_log_info) {
		this.pd_log_info = pd_log_info;
	}

}
