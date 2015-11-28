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
 * @info 店铺升级日志类,店铺可以根据需要选择升级店铺等级，经过管理员批准（收费等级需要线下缴纳费用管理员确认）生效，该类是记录所有升级记录
  
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "storegrade_log")
public class StoreGradeLog extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private Store store;// 审核日志对应的店铺
	private int store_grade_status;// 店铺升级状态，0为待审核中，1为审核通过,-1为审核失败
	private String store_grade_info;// 店铺审核处理说明
	private boolean log_edit;// 是否已经处理，日志只可以处理一次

	public boolean isLog_edit() {
		return log_edit;
	}

	public void setLog_edit(boolean log_edit) {
		this.log_edit = log_edit;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public int getStore_grade_status() {
		return store_grade_status;
	}

	public void setStore_grade_status(int store_grade_status) {
		this.store_grade_status = store_grade_status;
	}

	public String getStore_grade_info() {
		return store_grade_info;
	}

	public void setStore_grade_info(String store_grade_info) {
		this.store_grade_info = store_grade_info;
	}
}
