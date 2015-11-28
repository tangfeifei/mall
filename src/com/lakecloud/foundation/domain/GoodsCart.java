package com.lakecloud.foundation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 商品购物车管理类
 
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goodscart")
public class GoodsCart extends IdEntity {
	@OneToOne
	private Goods goods;// 对应的商品
	private int count;// 数量
	@Column(precision = 12, scale = 2)
	private BigDecimal price;// 价格
	@ManyToMany
	@JoinTable(name = Globals.DEFAULT_TABLE_SUFFIX + "cart_gsp", joinColumns = @JoinColumn(name = "cart_id"), inverseJoinColumns = @JoinColumn(name = "gsp_id"))
	private List<GoodsSpecProperty> gsps = new ArrayList<GoodsSpecProperty>();// 对应的规格
	@Lob
	@Column(columnDefinition = "LongText")
	private String spec_info;// 规格内容
	@ManyToOne(fetch = FetchType.LAZY)
	private OrderForm of;
	private String cart_type;// 默认为空，组合销售时候为"combin"
	@ManyToOne
	private StoreCart sc;// 对应的店铺购物车

	public StoreCart getSc() {
		return sc;
	}

	public void setSc(StoreCart sc) {
		this.sc = sc;
	}

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<GoodsSpecProperty> getGsps() {
		return gsps;
	}

	public void setGsps(List<GoodsSpecProperty> gsps) {
		this.gsps = gsps;
	}

	public String getSpec_info() {
		return spec_info;
	}

	public void setSpec_info(String spec_info) {
		this.spec_info = spec_info;
	}

	public OrderForm getOf() {
		return of;
	}

	public void setOf(OrderForm of) {
		this.of = of;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getCart_type() {
		return cart_type;
	}

	public void setCart_type(String cart_type) {
		this.cart_type = cart_type;
	}
}
