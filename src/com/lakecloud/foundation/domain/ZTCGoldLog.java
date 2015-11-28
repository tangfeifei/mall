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
 * @info 直通车金币日志,记录所有直通金币日志
 * @since v1.2
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "ztc_gold_log")
public class ZTCGoldLog extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private Goods zgl_goods;// 日志商品
	private int zgl_gold;// 金币数量
	private int zgl_type;// 0为增加，1为减少
	private String zgl_content;// 描述

	public Goods getZgl_goods() {
		return zgl_goods;
	}

	public void setZgl_goods(Goods zgl_goods) {
		this.zgl_goods = zgl_goods;
	}

	public int getZgl_gold() {
		return zgl_gold;
	}

	public void setZgl_gold(int zgl_gold) {
		this.zgl_gold = zgl_gold;
	}

	public int getZgl_type() {
		return zgl_type;
	}

	public void setZgl_type(int zgl_type) {
		this.zgl_type = zgl_type;
	}

	public String getZgl_content() {
		return zgl_content;
	}

	public void setZgl_content(String zgl_content) {
		this.zgl_content = zgl_content;
	}
}
