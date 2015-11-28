package com.lakecloud.foundation.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 团购价格区间,用在用户快速选择团购商品
 
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "group_price_range")
public class GroupPriceRange extends IdEntity {
	private String gpr_name;// 价格区间名称
	private int gpr_begin;// 区间下限
	private int gpr_end;// 区间上限

	public String getGpr_name() {
		return gpr_name;
	}

	public void setGpr_name(String gpr_name) {
		this.gpr_name = gpr_name;
	}

	public int getGpr_begin() {
		return gpr_begin;
	}

	public void setGpr_begin(int gpr_begin) {
		this.gpr_begin = gpr_begin;
	}

	public int getGpr_end() {
		return gpr_end;
	}

	public void setGpr_end(int gpr_end) {
		this.gpr_end = gpr_end;
	}
}
