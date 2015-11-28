package com.lakecloud.foundation.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 商品规格属性管理类，用来描述商品属性信息
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net wang 2012-06-25
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goodsspecproperty")
public class GoodsSpecProperty extends IdEntity {
	private int sequence;
	@Column(columnDefinition = "LongText")
	private String value;
	@OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
	private Accessory specImage;
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsSpecification spec;

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Accessory getSpecImage() {
		return specImage;
	}

	public void setSpecImage(Accessory specImage) {
		this.specImage = specImage;
	}

	public GoodsSpecification getSpec() {
		return spec;
	}

	public void setSpec(GoodsSpecification spec) {
		this.spec = spec;
	}

}
