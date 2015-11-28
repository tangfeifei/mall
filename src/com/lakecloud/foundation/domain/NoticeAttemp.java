package com.lakecloud.foundation.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "noticeattemp")
public class NoticeAttemp extends IdEntity {
	private String telephone ;
	private Integer timeTicks =1; 
	private Date lasttime;
	private  Integer limitmillsec;
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public Integer getTimeTicks() {
		return timeTicks;
	}
	public void setTimeTicks(Integer timeTicks) {
		this.timeTicks = timeTicks;
	}
	public Date getLasttime() {
		return lasttime;
	}
	public void setLasttime(Date lasttime) {
		this.lasttime = lasttime;
	}
	public Integer getLimitmillsec() {
		return limitmillsec;
	}
	public void setLimitmillsec(Integer limitmillsec) {
		this.limitmillsec = limitmillsec;
	}
}
