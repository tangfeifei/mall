package com.lakecloud.foundation.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 退货日志管理控制类，用来描述退货管理日志
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goods_returnlog")
public class GoodsReturnLog extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private OrderForm of;
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsReturn gr;
	@ManyToOne(fetch = FetchType.LAZY)
	private User return_user;

	public OrderForm getOf() {
		return of;
	}

	public void setOf(OrderForm of) {
		this.of = of;
	}

	public GoodsReturn getGr() {
		return gr;
	}

	public void setGr(GoodsReturn gr) {
		this.gr = gr;
	}

	public User getReturn_user() {
		return return_user;
	}

	public void setReturn_user(User return_user) {
		this.return_user = return_user;
	}
}
