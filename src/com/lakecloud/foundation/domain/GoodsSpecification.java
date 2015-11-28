package com.lakecloud.foundation.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 商品规格管理类，用来描述商品规格信息，由超级管理员添加，卖家发布商品时候可以为商品选择相应的规格信息
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net wang 2012-06-25
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goodsspecification")
public class GoodsSpecification extends IdEntity {
	private String name;// 名称
	private int sequence;// 序号
	private String type;// 规格类型(文字或图片)
	@ManyToMany(mappedBy = "gss")
	private List<GoodsType> types = new ArrayList<GoodsType>();// 商品类型
	@OneToMany(mappedBy = "spec")
	@OrderBy(value = "sequence asc")
	private List<GoodsSpecProperty> properties = new ArrayList<GoodsSpecProperty>();

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<GoodsType> getTypes() {
		return types;
	}

	public void setTypes(List<GoodsType> types) {
		this.types = types;
	}

	public List<GoodsSpecProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<GoodsSpecProperty> properties) {
		this.properties = properties;
	}

}
