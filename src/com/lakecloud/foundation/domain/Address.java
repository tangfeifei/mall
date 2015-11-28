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
 * @info 用户收货地址类，用来管理所有买家的收货地址信息，买家在提交订单时候可以选择这里的收货地址
 
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "address")
public class Address extends IdEntity {
	private String trueName;// 收货人姓名
	@ManyToOne(fetch = FetchType.LAZY)
	private Area area;// 收货人地区
	private String area_info;// 收货人详细地址
	private String zip;// 邮政编码
	private String telephone;// 联系电话
	private String mobile;// 手机号码
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;// 地址对应的用户

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getTrueName() {
		return trueName;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public String getArea_info() {
		return area_info;
	}

	public void setArea_info(String area_info) {
		this.area_info = area_info;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
}
