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
 * @info 个人主页关注管理类,可以作为关注列表，查看粉丝
 * @since V1.3
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net hezeng 20130815
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "user_attention")
public class SnsAttention extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private User fromUser;// 关注者
	@ManyToOne(fetch = FetchType.LAZY)
	private User toUser;// 被关注者

	public User getFromUser() {
		return fromUser;
	}

	public void setFromUser(User fromUser) {
		this.fromUser = fromUser;
	}

	public User getToUser() {
		return toUser;
	}

	public void setToUser(User toUser) {
		this.toUser = toUser;
	}

}
