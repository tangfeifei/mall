package com.lakecloud.foundation.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 最近来访实体类，用来记录个人主页中最近来访人
 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "Visit")
public class Visit extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private HomePage homepage;// 对应的个人主页
	@OneToOne(fetch = FetchType.LAZY)
	private User user;// 对应的访客
	private Date visitTime;// 访问时间

	public HomePage getHomepage() {
		return homepage;
	}

	public void setHomepage(HomePage homepage) {
		this.homepage = homepage;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getVisitTime() {
		return visitTime;
	}

	public void setVisitTime(Date visitTime) {
		this.visitTime = visitTime;
	}

}
