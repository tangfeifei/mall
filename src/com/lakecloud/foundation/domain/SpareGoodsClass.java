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
 * @info 闲置商品分类管理类，商城所有注册用户都可以发布闲置二手商品，二手商品只支持线下支付交易，目前不支持在线支付交易
 * @since V1.3
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net hezeng
 * @Date 20130922
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "spare_goodsClass")
public class SpareGoodsClass extends IdEntity {
	private String className;// 类别名称
	@Column(columnDefinition = "int default 0")
	private int sequence;// 索引,数字越小越排前
	@Column(columnDefinition = "int default 0")
	private int level;// 层级
	@Column(columnDefinition = "bit default true")
	private boolean viewInFloor;// 是否在楼层中显示，当该分类添加到楼层中默认为显示状态
	@OneToMany(mappedBy = "parent")
	@OrderBy(value = "sequence asc")
	private List<SpareGoodsClass> childs = new ArrayList<SpareGoodsClass>();// 子分类
	@ManyToOne(fetch = FetchType.LAZY)
	private SpareGoodsClass parent;// 父级分类
	@OneToOne(mappedBy = "sgc", fetch = FetchType.LAZY)
	private SpareGoodsFloor floor;// 对应的楼层分类
	@OneToMany(mappedBy = "spareGoodsClass")
	private List<SpareGoods> sgs = new ArrayList<SpareGoods>();// 对应的闲置商品

	public List<SpareGoods> getSgs() {
		return sgs;
	}

	public void setSgs(List<SpareGoods> sgs) {
		this.sgs = sgs;
	}

	public boolean isViewInFloor() {
		return viewInFloor;
	}

	public void setViewInFloor(boolean viewInFloor) {
		this.viewInFloor = viewInFloor;
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

	public List<SpareGoodsClass> getChilds() {
		return childs;
	}

	public void setChilds(List<SpareGoodsClass> childs) {
		this.childs = childs;
	}

	public SpareGoodsClass getParent() {
		return parent;
	}

	public void setParent(SpareGoodsClass parent) {
		this.parent = parent;
	}

	public SpareGoodsFloor getFloor() {
		return floor;
	}

	public void setFloor(SpareGoodsFloor floor) {
		this.floor = floor;
	}

}