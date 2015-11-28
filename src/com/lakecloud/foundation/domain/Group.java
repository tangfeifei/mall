package com.lakecloud.foundation.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 
 *             团购管理控制类，用来描述系统团购信息，团购由超级管理员发起，所有卖家都可以申请参加，多个团购时间不允许交叉，团购商品审核通过后在对应的团购活动中显示
 *             ,团购到期后，系统自恢复团购商品为普通商品
  
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "group")
public class Group extends IdEntity {
	private String group_name;// 团购活动名称
	private Date beginTime;// 开始时间
	private Date endTime;// 结束时间
	private Date joinEndTime;// 报名截止时间
	private int status;// 状态，0为正常，-1为关闭,-2为已经结束,1为即将开始
	@OneToMany(mappedBy = "group")
	private List<Goods> goods_list = new ArrayList<Goods>();// 团购对应的商品列表
	@OneToMany(mappedBy = "group")
	private List<GroupGoods> gg_list = new ArrayList<GroupGoods>();// 对应的团购商品列表

	public List<Goods> getGoods_list() {
		return goods_list;
	}

	public void setGoods_list(List<Goods> goods_list) {
		this.goods_list = goods_list;
	}

	public List<GroupGoods> getGg_list() {
		return gg_list;
	}

	public void setGg_list(List<GroupGoods> gg_list) {
		this.gg_list = gg_list;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getGroup_name() {
		return group_name;
	}

	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getJoinEndTime() {
		return joinEndTime;
	}

	public void setJoinEndTime(Date joinEndTime) {
		this.joinEndTime = joinEndTime;
	}

}
