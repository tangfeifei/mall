package com.lakecloud.foundation.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 商品类型管理控制类，用来描述商品类型信息
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net wang 2012-06-25
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goodstype")
public class GoodsType extends IdEntity {
	private String name;// 类型名称
	private int sequence;// 排序
	@ManyToMany
	@JoinTable(name = Globals.DEFAULT_TABLE_SUFFIX + "goodstype_spec", joinColumns = @JoinColumn(name = "type_id"), inverseJoinColumns = @JoinColumn(name = "spec_id"))
	private List<GoodsSpecification> gss = new ArrayList<GoodsSpecification>();
	@ManyToMany
	@JoinTable(name = Globals.DEFAULT_TABLE_SUFFIX + "goodstype_brand", joinColumns = @JoinColumn(name = "type_id"), inverseJoinColumns = @JoinColumn(name = "brand_id"))
	private List<GoodsBrand> gbs = new ArrayList<GoodsBrand>();
	@OneToMany(mappedBy = "goodsType", cascade = CascadeType.REMOVE)
	private List<GoodsTypeProperty> properties = new ArrayList<GoodsTypeProperty>();
	@OneToMany(mappedBy = "goodsType")
	private List<GoodsClass> gcs = new ArrayList<GoodsClass>();

	public List<GoodsClass> getGcs() {
		return gcs;
	}

	public void setGcs(List<GoodsClass> gcs) {
		this.gcs = gcs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public List<GoodsSpecification> getGss() {
		return gss;
	}

	public void setGss(List<GoodsSpecification> gss) {
		this.gss = gss;
	}

	public List<GoodsBrand> getGbs() {
		return gbs;
	}

	public void setGbs(List<GoodsBrand> gbs) {
		this.gbs = gbs;
	}

	public List<GoodsTypeProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<GoodsTypeProperty> properties) {
		this.properties = properties;
	}

}
