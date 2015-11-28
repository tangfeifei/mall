package com.lakecloud.foundation.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 个人主页SNS信息，用来显示用户分享的店铺、商品、心情等信息
 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "homepage")
public class HomePage extends IdEntity {
	@OneToOne(fetch = FetchType.LAZY)
	private User owner;// 主页主人
	@OneToMany(mappedBy = "homepage", cascade = CascadeType.REMOVE)
	private List<Visit> customers = new ArrayList<Visit>(); // 访客列表

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public List<Visit> getCustomers() {
		return customers;
	}

	public void setCustomers(List<Visit> customers) {
		this.customers = customers;
	}

}
