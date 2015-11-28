package com.lakecloud.weixin.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;
import com.lakecloud.foundation.domain.Store;

/**
 * @info 微信信息管理类，系统可以接受用户发送的微信信息，并进行回复
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "vmessage")
public class VMessage extends IdEntity {
	@ManyToOne
	private Store store;// 微信信息对应的店铺
	private String FromUserName;// 消息发送方姓名
	@Column(columnDefinition = "LongText")
	private String content;// 微信信息内容
	@Column(columnDefinition = "LongText")
	private String reply;// 回复信息内容
	private String MsgType;// 微信消息类型
	@Column(columnDefinition = "int default 0")
	private int status;// 消息状态，0为未回复，1为已回复

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsgType() {
		return MsgType;
	}

	public void setMsgType(String msgType) {
		MsgType = msgType;
	}

	public String getFromUserName() {
		return FromUserName;
	}

	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
