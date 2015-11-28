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
 * @info 个人主页中商品分类管理类，用户个人主页中宝贝动态列表上方分类选项卡选择
 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "homepage_goodsclass")
public class HomePageGoodsClass extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;// 对应的用户
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsClass gc;// 对应商品分类

	public GoodsClass getGc() {
		return gc;
	}

	public void setGc(GoodsClass gc) {
		this.gc = gc;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
