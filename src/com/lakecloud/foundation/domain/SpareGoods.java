package com.lakecloud.foundation.domain;

import javax.persistence.Column;
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
 * @info 闲置商品管理类，商城所有注册用户都可以发布闲置二手商品，二手商品只支持线下支付交易，目前不支持在线支付交易
 * @since V1.3
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang,hezeng
 * @Date 20130922
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "spare_goods")
public class SpareGoods extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;// 发布用户
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory main_img;// 闲置商品封面，默认为图片1
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory img1;// 闲置商品图片1
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory img2;// 闲置商品图片2
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory img3;// 闲置商品图片3
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory img4;// 闲置商品图片4
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory img5;// 闲置商品图片5

	@Column(columnDefinition = "bit default false")
	private boolean recommend;// 系统推荐，默认为false不推荐，true为推荐，推荐后在搜索列表页点击"帮你淘",查询系统推荐的闲置商品
	@ManyToOne(fetch = FetchType.LAZY)
	private SpareGoodsFloor sgf;// 对应的首页楼层
	@Column(columnDefinition = "bit default false")
	private boolean viewInFloor;// 是否在楼层中显示，默认为不显示

	@Column(columnDefinition = "int default 0")
	private int status;// 商品违规状态，0为正常、-1为违规，游客不可见,管理员操作

	@Column(columnDefinition = "int default 0")
	private int down;// 商品下架状态，-1为下架，0为正常

	@Column(columnDefinition = "int default 0")
	private String errorMessage; // 违规说明，管理员对违规闲置商品的说明
	private String title;// 发布闲置商品标题
	@Column(columnDefinition = "int default 0")
	private int goods_old_price;// 原价
	@Column(columnDefinition = "int default 0")
	private int goods_price;// 转让价格
	@OneToOne(fetch = FetchType.LAZY)
	private SpareGoodsClass spareGoodsClass;// 闲置商品分类
	@Column(columnDefinition = "int default 0")
	private int oldAndnew;// 新旧程度，0为非全新，1为全新
	private String phone; // 联系电话
	private String QQ; // 联系QQ
	private String name;// 姓名
	@OneToOne(fetch = FetchType.LAZY)
	private Area area;// 所在地
	@Column(columnDefinition = "LongText")
	private String content;// 商品内容，文字和商品详细图片

	public int getDown() {
		return down;
	}

	public void setDown(int down) {
		this.down = down;
	}

	public boolean isRecommend() {
		return recommend;
	}

	public void setRecommend(boolean recommend) {
		this.recommend = recommend;
	}

	public String getQQ() {
		return QQ;
	}

	public void setQQ(String qq) {
		QQ = qq;
	}

	public boolean isViewInFloor() {
		return viewInFloor;
	}

	public void setViewInFloor(boolean viewInFloor) {
		this.viewInFloor = viewInFloor;
	}

	public SpareGoodsFloor getSgf() {
		return sgf;
	}

	public void setSgf(SpareGoodsFloor sgf) {
		this.sgf = sgf;
	}

	public Accessory getImg5() {
		return img5;
	}

	public void setImg5(Accessory img5) {
		this.img5 = img5;
	}

	public Accessory getMain_img() {
		return main_img;
	}

	public void setMain_img(Accessory main_img) {
		this.main_img = main_img;
	}

	public Accessory getImg1() {
		return img1;
	}

	public void setImg1(Accessory img1) {
		this.img1 = img1;
	}

	public Accessory getImg2() {
		return img2;
	}

	public void setImg2(Accessory img2) {
		this.img2 = img2;
	}

	public Accessory getImg3() {
		return img3;
	}

	public void setImg3(Accessory img3) {
		this.img3 = img3;
	}

	public Accessory getImg4() {
		return img4;
	}

	public void setImg4(Accessory img4) {
		this.img4 = img4;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getGoods_old_price() {
		return goods_old_price;
	}

	public void setGoods_old_price(int goods_old_price) {
		this.goods_old_price = goods_old_price;
	}

	public int getGoods_price() {
		return goods_price;
	}

	public void setGoods_price(int goods_price) {
		this.goods_price = goods_price;
	}

	public SpareGoodsClass getSpareGoodsClass() {
		return spareGoodsClass;
	}

	public void setSpareGoodsClass(SpareGoodsClass spareGoodsClass) {
		this.spareGoodsClass = spareGoodsClass;
	}

	public int getOldAndnew() {
		return oldAndnew;
	}

	public void setOldAndnew(int oldAndnew) {
		this.oldAndnew = oldAndnew;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
