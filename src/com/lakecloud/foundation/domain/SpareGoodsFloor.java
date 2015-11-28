package com.lakecloud.foundation.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 闲置商品首页楼层管理类，首页显示楼层信息
 
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "spare_goodsFloor")
public class SpareGoodsFloor extends IdEntity {
	private String title;// 楼层标题
	@Column(columnDefinition = "int default 0")
	private int sequence;// 楼层索引,数字越小越排前
	@OneToOne(fetch = FetchType.LAZY)
	private SpareGoodsClass sgc;// 对应的闲置商品分类
	@Column(columnDefinition = "bit default true")
	private boolean display;// 是否显示，默认显示,true显示，false不显示
	@OneToMany(mappedBy = "sgf")
	private List<SpareGoods> sgs = new ArrayList<SpareGoods>();
	@Column(columnDefinition = "int default 0")
	private int adver_type;// 楼层广告类型，0为上传广告，1为系统广告
	@OneToOne(fetch = FetchType.LAZY)
	private AdvertPosition adp;// 当楼层广告类型为1（系统广告），所对应的商城系统广告位，选择广告位时只能选择200X250,且广告位类别为图片的广告位
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory advert_img;// 当楼层广告类型为0时，后台上传楼层广告图片
	private String advert_url;// 楼层广告链接

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public int getAdver_type() {
		return adver_type;
	}

	public void setAdver_type(int adver_type) {
		this.adver_type = adver_type;
	}

	public AdvertPosition getAdp() {
		return adp;
	}

	public void setAdp(AdvertPosition adp) {
		this.adp = adp;
	}

	public Accessory getAdvert_img() {
		return advert_img;
	}

	public void setAdvert_img(Accessory advert_img) {
		this.advert_img = advert_img;
	}

	public String getAdvert_url() {
		return advert_url;
	}

	public void setAdvert_url(String advert_url) {
		this.advert_url = advert_url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public SpareGoodsClass getSgc() {
		return sgc;
	}

	public void setSgc(SpareGoodsClass sgc) {
		this.sgc = sgc;
	}

	public List<SpareGoods> getSgs() {
		return sgs;
	}

	public void setSgs(List<SpareGoods> sgs) {
		this.sgs = sgs;
	}

}
