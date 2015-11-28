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
 * @info 会话好友管理类，用于查询最近联系人 当用户与对方会话，自动将对方添加为自己的最近联系人，对方也会将用户添加为他的最近联系人
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net hezeng 2013-09-03
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "chattingfriend")
public class ChattingFriend extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;// 用户
	@ManyToOne(fetch = FetchType.LAZY)
	private User friendUser;// 最近联系人

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getFriendUser() {
		return friendUser;
	}

	public void setFriendUser(User friendUser) {
		this.friendUser = friendUser;
	}

}
