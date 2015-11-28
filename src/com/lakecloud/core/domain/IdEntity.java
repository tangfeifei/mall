package com.lakecloud.core.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.lakecloud.core.annotation.Lock;

/**
 * 
* <p>Title: IdEntity.java</p>

* <p>Description: 系统域模型基类，该类包含3个常用字段，其中id为自增长类型，该类实现序列化，只有序列化后才可以实现tomcat集群配置session共享</p>

 */
@MappedSuperclass
public class IdEntity implements Serializable {
	/**
	 * 序列化接口，自动生成序列号
	 */
	private static final long serialVersionUID = -7741168269971132706L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(unique = true, nullable = false)
	private Long id;// 域模型id，这里为自增类型
	private Date addTime;// 添加时间，这里为长时间格式
	@Lock
	private boolean deleteStatus;// 是否删除

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public boolean isDeleteStatus() {
		return deleteStatus;
	}

	public void setDeleteStatus(boolean deleteStatus) {
		this.deleteStatus = deleteStatus;
	}
}
