package com.lakecloud.foundation.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 会话管理类，该类只用于生成会话以及会话记录的查询与保存，user1和user2分别对应会话中的两个用户，保存记录时哪个用户首先发送信息
 *       ，该用户为user1 ， 如果user1和user2的会话记录已经存在，则在已经存在的会话中保存聊天记录，没有则新建，
 *       查询记录时只要这两个用户对应上就可以，没有先后顺序，
 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "chatting")
public class Chatting extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private User user1;// 会话记录主人1
	@ManyToOne(fetch = FetchType.LAZY)
	private User user2;// 会话记录主人2

	@Column(columnDefinition = "int default 0")
	private int type;// 会话类型，0为临时好友会话，1为在线好友会话，2为最近联系会话
	@OneToMany(mappedBy = "chatting", cascade = CascadeType.REMOVE)
	private List<ChattingLog> logs = new ArrayList<ChattingLog>();// 聊天记录

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public User getUser1() {
		return user1;
	}

	public void setUser1(User user1) {
		this.user1 = user1;
	}

	public User getUser2() {
		return user2;
	}

	public void setUser2(User user2) {
		this.user2 = user2;
	}

	public List<ChattingLog> getLogs() {
		return logs;
	}

	public void setLogs(List<ChattingLog> logs) {
		this.logs = logs;
	}

}
