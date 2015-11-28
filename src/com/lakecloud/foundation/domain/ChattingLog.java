package com.lakecloud.foundation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 聊天管理日志类，单个用户聊天记录的管理类
 * @since V1.3
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net hezeng
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "chattinglog")
public class ChattingLog extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private Chatting chatting;// 对应会话管理类
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;// 聊天发言人
	@Column(columnDefinition = "LongText")
	private String content;// 聊天内容
	@Column(columnDefinition = "int default 0")
	private int mark;// 聊天内容标记，0为未读，1为已读，每次获取未读信息显示

	public int getMark() {
		return mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
	}

	public Chatting getChatting() {
		return chatting;
	}

	public void setChatting(Chatting chatting) {
		this.chatting = chatting;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
