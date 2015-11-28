package com.lakecloud.foundation.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 今日特价商品类
 * @since V1.3
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net wang
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "bargain_goods")
public class BargainGoods extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private Goods bg_goods;// 特价商品
	private int bg_status;// 特价商品审核状态，0为待审核，1为审核通过，-1为审核拒绝,-2为到期
	@Column(precision = 3, scale = 1)
	private BigDecimal bg_rebate;// 特价折扣
	@Column(columnDefinition = "int default 1")
	private int bg_count;// 参与特价的商品数量
	@ManyToOne(fetch = FetchType.LAZY)
	private User bg_admin_user;// 特价审核管理员
	@Column(precision = 12, scale = 2)
	private BigDecimal bg_price;// 特价价格
	@Temporal(value = TemporalType.DATE)
	private Date bg_time;// 特价时间
	private Date audit_time;// 审核时间

	public Date getAudit_time() {
		return audit_time;
	}

	public void setAudit_time(Date audit_time) {
		this.audit_time = audit_time;
	}

	public Date getBg_time() {
		return bg_time;
	}

	public void setBg_time(Date bg_time) {
		this.bg_time = bg_time;
	}

	public Goods getBg_goods() {
		return bg_goods;
	}

	public void setBg_goods(Goods bg_goods) {
		this.bg_goods = bg_goods;
	}

	public int getBg_status() {
		return bg_status;
	}

	public void setBg_status(int bg_status) {
		this.bg_status = bg_status;
	}

	public User getBg_admin_user() {
		return bg_admin_user;
	}

	public void setBg_admin_user(User bg_admin_user) {
		this.bg_admin_user = bg_admin_user;
	}

	public BigDecimal getBg_price() {
		return bg_price;
	}

	public void setBg_price(BigDecimal bg_price) {
		this.bg_price = bg_price;
	}

	public BigDecimal getBg_rebate() {
		return bg_rebate;
	}

	public void setBg_rebate(BigDecimal bg_rebate) {
		this.bg_rebate = bg_rebate;
	}

	public int getBg_count() {
		return bg_count;
	}

	public void setBg_count(int bg_count) {
		this.bg_count = bg_count;
	}

}
