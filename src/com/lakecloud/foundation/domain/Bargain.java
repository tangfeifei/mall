package com.lakecloud.foundation.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 今日特价管理类
 * @since V1.3
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net wang
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "bargain")
public class Bargain extends IdEntity {
	@Temporal(TemporalType.DATE)
	private Date bargain_time;// 开始时间
	@Column(precision = 3, scale = 2)
	private BigDecimal rebate;// 特价折扣
	@Column(columnDefinition = "int default 0")
	private int maximum;// 审核通过最大商品数
	@Column(columnDefinition = "LongText")
	private String state;// 特价说明

	public Date getBargain_time() {
		return bargain_time;
	}

	public void setBargain_time(Date bargain_time) {
		this.bargain_time = bargain_time;
	}

	public BigDecimal getRebate() {
		return rebate;
	}

	public void setRebate(BigDecimal rebate) {
		this.rebate = rebate;
	}

	public int getMaximum() {
		return maximum;
	}

	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
