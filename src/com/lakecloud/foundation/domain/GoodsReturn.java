package com.lakecloud.foundation.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 退货管理类
 * @since V1.0
 * @author江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goods_return")
public class GoodsReturn extends IdEntity {
	private String return_id;
	@ManyToOne(fetch = FetchType.LAZY)
	private OrderForm of;
	@OneToMany(mappedBy = "gr", cascade = CascadeType.REMOVE)
	private List<GoodsReturnItem> items = new ArrayList<GoodsReturnItem>();
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;
	@Column(columnDefinition = "LongText")
	private String return_info;

	public OrderForm getOf() {
		return of;
	}

	public void setOf(OrderForm of) {
		this.of = of;
	}

	public List<GoodsReturnItem> getItems() {
		return items;
	}

	public void setItems(List<GoodsReturnItem> items) {
		this.items = items;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getReturn_id() {
		return return_id;
	}

	public void setReturn_id(String return_id) {
		this.return_id = return_id;
	}

	public String getReturn_info() {
		return return_info;
	}

	public void setReturn_info(String return_info) {
		this.return_info = return_info;
	}

}
