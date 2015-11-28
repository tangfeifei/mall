package com.lakecloud.foundation.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 买就送商品类
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "delivery_goods")
public class DeliveryGoods extends IdEntity {
	@OneToOne(fetch = FetchType.LAZY)
	private Goods d_goods;// 商品
	@ManyToOne(fetch = FetchType.LAZY)
	private Goods d_delivery_goods;// 赠送的商品
	private int d_status;// 特送商品审核状态，0为待审核，1为审核通过，-1为审核拒绝,-2已经过期
	@ManyToOne(fetch = FetchType.LAZY)
	private User d_admin_user;// 特送审核管理员
	private Date d_audit_time;// 审核时间
	private Date d_refuse_time;// 拒绝时间
	@Temporal(value = TemporalType.DATE)
	private Date d_begin_time;// 参加买就送开始时间
	@Temporal(value = TemporalType.DATE)
	private Date d_end_time;// 参加买就送结束时间

	public Goods getD_goods() {
		return d_goods;
	}

	public void setD_goods(Goods d_goods) {
		this.d_goods = d_goods;
	}

	public Goods getD_delivery_goods() {
		return d_delivery_goods;
	}

	public void setD_delivery_goods(Goods d_delivery_goods) {
		this.d_delivery_goods = d_delivery_goods;
	}

	public int getD_status() {
		return d_status;
	}

	public void setD_status(int d_status) {
		this.d_status = d_status;
	}

	public User getD_admin_user() {
		return d_admin_user;
	}

	public void setD_admin_user(User d_admin_user) {
		this.d_admin_user = d_admin_user;
	}

	public Date getD_begin_time() {
		return d_begin_time;
	}

	public void setD_begin_time(Date d_begin_time) {
		this.d_begin_time = d_begin_time;
	}

	public Date getD_end_time() {
		return d_end_time;
	}

	public void setD_end_time(Date d_end_time) {
		this.d_end_time = d_end_time;
	}

	public Date getD_audit_time() {
		return d_audit_time;
	}

	public void setD_audit_time(Date d_audit_time) {
		this.d_audit_time = d_audit_time;
	}

	public Date getD_refuse_time() {
		return d_refuse_time;
	}

	public void setD_refuse_time(Date d_refuse_time) {
		this.d_refuse_time = d_refuse_time;
	}

}
