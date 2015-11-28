package com.lakecloud.foundation.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: StoreDepositLog.java
 * </p>
 * 
 * <p>
 * Description: 店铺保证金管理类，不同的店铺等级可以缴纳不同的保证金，
 * 保证金数额由店铺等级控制，保证金缴纳后会在用户店铺页显示，缴纳保证金的店铺，用户购物更加放心
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011-2014
 * </p>
 * 
 * <p>
 * Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-5-8
 * 
 * @version LakeCloud_C2C 1.4
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "sp_log")
public class StoreDepositLog extends IdEntity {
	private String dp_no;// 保证金缴纳单号，保持唯一
	private Long dp_user_id;// 缴纳保证金的用户Id
	private String dp_store_name;// 缴纳保证金的店铺名称
	private String dp_grade_name;// 店铺等级名称
	private String dp_store_ower;// 店主姓名
	private int dp_amount;// 保证金数额
	private String dp_payment;// 保证金支付方式
	private String dp_payment_mark;//保证金支付方式mark
	private int dp_type;// 保证金操作类型，0为缴纳，1为扣除，2为增加，3为退还,
	@Column(columnDefinition = "int default 0")
	private int dp_status;// 保证金支付状态，0为未支付，5为线下支付待审核，10为支付成功,-1为拒绝通过
	private Long dp_op_user_id;// 保证金操作用户id，记录扣除、退还保证金操作的用户id
	private String dp_op_user_name;// 操作保证金管理员用户名
	private Date dp_op_time;// 管理员操作时间
	private String dp_content;// 保证金操作说明
	private Date dp_store_open_time;// 店铺开通日期

	
	public String getDp_payment_mark() {
		return dp_payment_mark;
	}

	public void setDp_payment_mark(String dp_payment_mark) {
		this.dp_payment_mark = dp_payment_mark;
	}

	public String getDp_no() {
		return dp_no;
	}

	public void setDp_no(String dp_no) {
		this.dp_no = dp_no;
	}

	public Date getDp_op_time() {
		return dp_op_time;
	}

	public void setDp_op_time(Date dp_op_time) {
		this.dp_op_time = dp_op_time;
	}

	public Date getDp_store_open_time() {
		return dp_store_open_time;
	}

	public void setDp_store_open_time(Date dp_store_open_time) {
		this.dp_store_open_time = dp_store_open_time;
	}

	public String getDp_op_user_name() {
		return dp_op_user_name;
	}

	public void setDp_op_user_name(String dp_op_user_name) {
		this.dp_op_user_name = dp_op_user_name;
	}

	public String getDp_grade_name() {
		return dp_grade_name;
	}

	public void setDp_grade_name(String dp_grade_name) {
		this.dp_grade_name = dp_grade_name;
	}

	public String getDp_store_ower() {
		return dp_store_ower;
	}

	public void setDp_store_ower(String dp_store_ower) {
		this.dp_store_ower = dp_store_ower;
	}

	public String getDp_store_name() {
		return dp_store_name;
	}

	public void setDp_store_name(String dp_store_name) {
		this.dp_store_name = dp_store_name;
	}

	public int getDp_status() {
		return dp_status;
	}

	public void setDp_status(int dp_status) {
		this.dp_status = dp_status;
	}

	public String getDp_payment() {
		return dp_payment;
	}

	public void setDp_payment(String dp_payment) {
		this.dp_payment = dp_payment;
	}

	public Long getDp_user_id() {
		return dp_user_id;
	}

	public void setDp_user_id(Long dp_user_id) {
		this.dp_user_id = dp_user_id;
	}

	public int getDp_amount() {
		return dp_amount;
	}

	public void setDp_amount(int dp_amount) {
		this.dp_amount = dp_amount;
	}

	public int getDp_type() {
		return dp_type;
	}

	public void setDp_type(int dp_type) {
		this.dp_type = dp_type;
	}

	public Long getDp_op_user_id() {
		return dp_op_user_id;
	}

	public void setDp_op_user_id(Long dp_op_user_id) {
		this.dp_op_user_id = dp_op_user_id;
	}

	public String getDp_content() {
		return dp_content;
	}

	public void setDp_content(String dp_content) {
		this.dp_content = dp_content;
	}

}
