package com.lakecloud.foundation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 
 *             店铺等级,由超级管理员添加，用户开店时申请店铺对应的等级，不同的店铺等级可以收取不同的费用，同时不同的店铺等级可以拥有不同的店铺模板
 *             、发布不同的商品、不同的图片空间大小等
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "storegrade")
public class StoreGrade extends IdEntity {
	private String gradeName;// 等级名称
	private boolean sysGrade;// 是否系统店铺等级，系统店铺等级不可删除
	private boolean audit;// 申请该等级店铺是否需要审核
	@Column(columnDefinition = "int default 50")
	private int goodsCount;// 允许发布商品数量
	@Column(columnDefinition = "int default 0")
	private int sequence;// 店铺序号，越小越靠前
	private float spaceSize;// 店铺图片空间大小
	@Lob
	@Column(columnDefinition = "LongText")
	private String content;// 申请说明
	private String price;// 收费标准
	private String add_funciton;// 附件功能，功能列表使用,符号间隔
	private String templates;// 该等级店铺允许使用的模板样式，样式列表使用,符号间隔
	@Column(columnDefinition = "int default 0")
	private int gradeLevel;// 等级级别，数据越大级别越高
	@Column(columnDefinition = "int default 0")
	private int acount_num;// 允许卖家添加的子账户个数
	@Column(columnDefinition = "int default 0")
	private int store_deposit;//店铺对应的保证金，可以缴纳，也可以不缴纳

	public int getStore_deposit() {
		return store_deposit;
	}

	public void setStore_deposit(int store_deposit) {
		this.store_deposit = store_deposit;
	}

	public int getAcount_num() {
		return acount_num;
	}

	public void setAcount_num(int acount_num) {
		this.acount_num = acount_num;
	}

	public String getGradeName() {
		return gradeName;
	}

	public void setGradeName(String gradeName) {
		this.gradeName = gradeName;
	}

	public boolean isAudit() {
		return audit;
	}

	public void setAudit(boolean audit) {
		this.audit = audit;
	}

	public int getGoodsCount() {
		return goodsCount;
	}

	public void setGoodsCount(int goodsCount) {
		this.goodsCount = goodsCount;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getAdd_funciton() {
		return add_funciton;
	}

	public void setAdd_funciton(String add_funciton) {
		this.add_funciton = add_funciton;
	}

	public String getTemplates() {
		return templates;
	}

	public void setTemplates(String templates) {
		this.templates = templates;
	}

	public boolean isSysGrade() {
		return sysGrade;
	}

	public void setSysGrade(boolean sysGrade) {
		this.sysGrade = sysGrade;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public float getSpaceSize() {
		return spaceSize;
	}

	public void setSpaceSize(float spaceSize) {
		this.spaceSize = spaceSize;
	}

	public int getGradeLevel() {
		return gradeLevel;
	}

	public void setGradeLevel(int gradeLevel) {
		this.gradeLevel = gradeLevel;
	}
}
