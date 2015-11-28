package com.lakecloud.foundation.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 网站管理 合作伙伴,在首页底部合作伙伴位置显示
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 wang 2012-06-29
 * 
 */

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "partner")
public class Partner extends IdEntity {
	private int sequence;// 排序
	private String url;// 连接
	private String title;// 标题
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory image;// 标识图片

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Accessory getImage() {
		return image;
	}

	public void setImage(Accessory image) {
		this.image = image;
	}

}
