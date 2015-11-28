package com.lakecloud.foundation.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 退货子项详细描述类
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goods_returnitem")
public class GoodsReturnItem extends IdEntity {
	@OneToOne(fetch = FetchType.LAZY)
	private Goods goods;
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsReturn gr;
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = Globals.DEFAULT_TABLE_SUFFIX + "return_gsp", joinColumns = @JoinColumn(name = "item_id"), inverseJoinColumns = @JoinColumn(name = "gsp_id"))
	private List<GoodsSpecProperty> gsps = new ArrayList<GoodsSpecProperty>();// 对应的规格
	@Lob
	@Column(columnDefinition = "LongText")
	private String spec_info;// 规格内容
	private int count;

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public GoodsReturn getGr() {
		return gr;
	}

	public void setGr(GoodsReturn gr) {
		this.gr = gr;
	}

	public List<GoodsSpecProperty> getGsps() {
		return gsps;
	}

	public void setGsps(List<GoodsSpecProperty> gsps) {
		this.gsps = gsps;
	}

	public String getSpec_info() {
		return spec_info;
	}

	public void setSpec_info(String spec_info) {
		this.spec_info = spec_info;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
