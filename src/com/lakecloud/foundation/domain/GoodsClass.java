package com.lakecloud.foundation.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 商品分类管理类，用来管理商城商品分类信息
 
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goodsclass")
public class GoodsClass extends IdEntity {
	private String className;// 类别名称
	@OneToMany(mappedBy = "parent")
	@OrderBy(value = "sequence asc")
	private List<GoodsClass> childs = new ArrayList<GoodsClass>();// 子分类
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsClass parent;// 父级分类
	private int sequence;// 索引
	private int level;// 层级
	private boolean display;// 是否显示
	private boolean recommend;// 是否推荐
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsType goodsType;// 商品类型
	@Column(columnDefinition = "LongText")
	private String seo_keywords;// 关键字
	@Column(columnDefinition = "LongText")
	private String seo_description;// 描述
	@OneToMany(mappedBy = "gc")
	private List<Goods> goods_list = new ArrayList<Goods>();// 商城分类对应的商品
	@OneToMany(mappedBy = "gc")
	private List<GoodsClassStaple> gcss = new ArrayList<GoodsClassStaple>();// 包含该分类的店铺常用分类
	@Column(columnDefinition = "int default 0")
	private int icon_type;// 一级分类图标显示类型，0为系统图标，1为上传图标
	private String icon_sys;// 系统图标
	@OneToOne
	private Accessory icon_acc;// 上传图标

	public int getIcon_type() {
		return icon_type;
	}

	public void setIcon_type(int icon_type) {
		this.icon_type = icon_type;
	}

	public String getIcon_sys() {
		return icon_sys;
	}

	public void setIcon_sys(String icon_sys) {
		this.icon_sys = icon_sys;
	}

	public Accessory getIcon_acc() {
		return icon_acc;
	}

	public void setIcon_acc(Accessory icon_acc) {
		this.icon_acc = icon_acc;
	}

	public List<Goods> getGoods_list() {
		return goods_list;
	}

	public void setGoods_list(List<Goods> goods_list) {
		this.goods_list = goods_list;
	}

	public List<GoodsClassStaple> getGcss() {
		return gcss;
	}

	public void setGcss(List<GoodsClassStaple> gcss) {
		this.gcss = gcss;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public boolean isRecommend() {
		return recommend;
	}

	public void setRecommend(boolean recommend) {
		this.recommend = recommend;
	}

	public GoodsType getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(GoodsType goodsType) {
		this.goodsType = goodsType;
	}

	public List<GoodsClass> getChilds() {
		return childs;
	}

	public void setChilds(List<GoodsClass> childs) {
		this.childs = childs;
	}

	public GoodsClass getParent() {
		return parent;
	}

	public void setParent(GoodsClass parent) {
		this.parent = parent;
	}

	public String getSeo_keywords() {
		return seo_keywords;
	}

	public void setSeo_keywords(String seo_keywords) {
		this.seo_keywords = seo_keywords;
	}

	public String getSeo_description() {
		return seo_description;
	}

	public void setSeo_description(String seo_description) {
		this.seo_description = seo_description;
	}

}
