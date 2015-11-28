package com.lakecloud.foundation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 店铺分类实体类，用来描述管理系统店铺分类，用户开设店铺必须选择对应的店铺分类
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "storeclass")
public class StoreClass extends IdEntity {
	private String className;// 分类名称
	private int sequence;// 分类序号
	@ManyToOne(fetch = FetchType.LAZY)
	private StoreClass parent;// 上级分类
	@OneToMany(mappedBy = "parent")
	private List<StoreClass> childs = new ArrayList<StoreClass>();// 下级分类
	private int level;// 分类层级
	@Column(precision = 4, scale = 1)
	private BigDecimal description_evaluate;// 商品分类描述相符评分，同行业均分
	@Column(precision = 4, scale = 1)
	private BigDecimal service_evaluate;// 商品分类服务态度评价，同行业均分
	@Column(precision = 4, scale = 1)
	private BigDecimal ship_evaluate;// 商品分类发货速度评价，同行业均分

	public BigDecimal getDescription_evaluate() {
		return description_evaluate;
	}

	public void setDescription_evaluate(BigDecimal description_evaluate) {
		this.description_evaluate = description_evaluate;
	}

	public BigDecimal getService_evaluate() {
		return service_evaluate;
	}

	public void setService_evaluate(BigDecimal service_evaluate) {
		this.service_evaluate = service_evaluate;
	}

	public BigDecimal getShip_evaluate() {
		return ship_evaluate;
	}

	public void setShip_evaluate(BigDecimal ship_evaluate) {
		this.ship_evaluate = ship_evaluate;
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

	public StoreClass getParent() {
		return parent;
	}

	public void setParent(StoreClass parent) {
		this.parent = parent;
	}

	public List<StoreClass> getChilds() {
		return childs;
	}

	public void setChilds(List<StoreClass> childs) {
		this.childs = childs;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
}
