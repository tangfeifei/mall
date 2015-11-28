package com.lakecloud.foundation.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 快递公司管理，系统默认有多个快递公司信息，用户可以在订单页面，查询快递配送信息，V1.3版开始，目前使用快递100接口查询，
 *             后续公司内部自己设计快递查询接口 快递公司为系统数据，管理员随意添加需要遵循快递100的公司编码，和程序绑定使用
 
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "express_company")
public class ExpressCompany extends IdEntity {
	private String company_name;// 快递公司名称
	private String company_mark;// 快递公司代码，根据该代码查询对应的物流信息，代码见：http://code.google.com/p/kuaidi-api/wiki/Open_API_API_URL
	@Column(columnDefinition = "int default 0")
	private int company_sequence;// 公司序号，默认按照升序排列
	@Column(columnDefinition = "varchar(255) default 'EXPRESS'")
	private String company_type;// 快递公司类型，POST为平邮、EXPRESS为快递、EMS
	@Column(columnDefinition = "int default 0")
	private int company_status;// 快递公司状态，0为启用，-1为关闭状态
	@OneToMany(mappedBy = "ec")
	List<OrderForm> ofs = new ArrayList<OrderForm>();// 物流公司对应的订单信息

	public List<OrderForm> getOfs() {
		return ofs;
	}

	public void setOfs(List<OrderForm> ofs) {
		this.ofs = ofs;
	}

	public String getCompany_name() {
		return company_name;
	}

	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}

	public String getCompany_mark() {
		return company_mark;
	}

	public void setCompany_mark(String company_mark) {
		this.company_mark = company_mark;
	}

	public String getCompany_type() {
		return company_type;
	}

	public void setCompany_type(String company_type) {
		this.company_type = company_type;
	}

	public int getCompany_status() {
		return company_status;
	}

	public void setCompany_status(int company_status) {
		this.company_status = company_status;
	}

	public int getCompany_sequence() {
		return company_sequence;
	}

	public void setCompany_sequence(int company_sequence) {
		this.company_sequence = company_sequence;
	}

}
