package com.lakecloud.foundation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
 * @info 
 *       商城购物车类，根据店铺区分具体购物车内容信息,V1.3版之前购物车再订单提交前是保存到session中，为了更好地结合集群及多域名，V1.3版开始
 *       ，购物车信息直接保存到数据库中，未登录用户根据随机唯一Id保存，已经登录的用户根据User来保存，未登录用户购物车间隔1天自动删除，
 *       已经登录用户购物车保存7天，7天未提交为订单自动删除,购物车信息存在及时性，不加入缓存管理
 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "storecart")
public class StoreCart extends IdEntity {
	@ManyToOne
	private Store store;// 购物车对应的店铺
	@OneToMany(mappedBy = "sc")
	private List<GoodsCart> gcs = new ArrayList<GoodsCart>();// 购物车对应的商品信息
	private BigDecimal total_price;// 购物车总价格
	@ManyToOne
	private User user;// 商城购物车对应的用户
	private String cart_session_id;// 未登录用户会话Id
	@Column(columnDefinition = "int default 0")
	private int sc_status;// 商城购物车状态，0表示没有提交为订单，1表示已经提交为订单，已经提交为订单信息的不再为缓存购物车，同时定时器也不进行删除操作

	public int getSc_status() {
		return sc_status;
	}

	public void setSc_status(int sc_status) {
		this.sc_status = sc_status;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getCart_session_id() {
		return cart_session_id;
	}

	public void setCart_session_id(String cart_session_id) {
		this.cart_session_id = cart_session_id;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public List<GoodsCart> getGcs() {
		return gcs;
	}

	public void setGcs(List<GoodsCart> gcs) {
		this.gcs = gcs;
	}

	public BigDecimal getTotal_price() {
		return total_price;
	}

	public void setTotal_price(BigDecimal total_price) {
		this.total_price = total_price;
	}

}
