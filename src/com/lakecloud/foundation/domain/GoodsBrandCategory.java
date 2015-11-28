package com.lakecloud.foundation.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 品牌类别管理类
 
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "brandcategory")
public class GoodsBrandCategory extends IdEntity {
	private String name;
	private int sequence;
	@OneToMany(mappedBy = "category")
	private List<GoodsBrand> brands = new ArrayList<GoodsBrand>();

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

	public List<GoodsBrand> getBrands() {
		return brands;
	}

	public void setBrands(List<GoodsBrand> brands) {
		this.brands = brands;
	}

}
