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
 * @info 常用商品分类，用在用户发布商品时快速选取
 
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goodsclassstaple")
public class GoodsClassStaple extends IdEntity {
	private String name;// 分类名称，如：服饰>女装>大衣
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsClass gc;// 商品分类，这里只记录最底层分类即可
	@ManyToOne(fetch = FetchType.LAZY)
	private Store store;// 商品对应的店铺

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public GoodsClass getGc() {
		return gc;
	}

	public void setGc(GoodsClass gc) {
		this.gc = gc;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}
}
