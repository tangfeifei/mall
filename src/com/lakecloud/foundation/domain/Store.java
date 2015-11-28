package com.lakecloud.foundation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.annotation.Lock;
import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;
import com.lakecloud.weixin.domain.VLog;

/**
 * @info 店铺信息管理类，描述卖家店铺信息
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "store")
public class Store extends IdEntity {
	private String store_name;// 店铺名称
	private String store_ower;// 店主真实姓名
	private String store_ower_card;// 店主身份证号
	private String store_telephone;// 店铺电话号码
	private String store_qq;// 店铺qq
	private String store_msn;// 店铺msn
	private String store_ww;// 店铺淘宝旺旺
	private String store_address;// 店铺详细地址
	private String store_zip;// 店铺邮编
	private int store_status;// 店铺状态，0为未开店铺，1为待审核,2为通过,3为违规关闭,4店铺过期关闭
	@OneToOne(mappedBy = "store", fetch = FetchType.LAZY)
	private User user;// 店铺对应的用户，反向映射
	@ManyToOne(fetch = FetchType.LAZY)
	private StoreGrade grade;// 店铺等级
	@ManyToOne(fetch = FetchType.LAZY)
	private StoreClass sc;// 店铺分类
	@ManyToOne(fetch = FetchType.LAZY)
	private Area area;// 店铺地址，这里保存的是最底层地址
	private boolean store_recommend;// 是否被推荐
	private Date store_recommend_time;// 店铺推荐时间,根据推荐时间倒序显示明星店铺
	private Date validity;// 店铺有效期，用在收费店铺等级，超过有效期，自动将为免费店铺,为空时表示无限期
	private boolean card_approve;// 是否经过实名认证
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory store_logo;// 店铺logo
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory store_banner;// 店铺banner
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory card;// 认证身份证
	private boolean realstore_approve;// 是否实体店铺认证，通过店铺营业执照
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory store_license;// 店铺营业执照
	@OneToMany(mappedBy = "goods_store",cascade = CascadeType.REMOVE)
	private List<Goods> goods_list = new ArrayList<Goods>();// 店铺所属商品
	private int store_credit;// 店铺等级，根据好评数累加
	private String template;// 店铺模板，根据名称进行管理
	@Lob
	@Column(columnDefinition = "LongText")
	private String violation_reseaon;// 违规原因
	@Lob
	@Column(columnDefinition = "LongText")
	private String store_seo_keywords;// 店铺SEO关键字
	@Lob
	@Column(columnDefinition = "LongText")
	private String store_seo_description; // 店铺SEO描述
	@Lob
	@Column(columnDefinition = "LongText")
	private String store_info;// 店铺介绍
	@OneToOne(fetch = FetchType.LAZY)
	private StoreGrade update_grade;// 申请升级的店铺等级
	@OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
	private List<StoreSlide> slides = new ArrayList<StoreSlide>();// 店铺幻灯
	@Lock
	private String store_second_domain;// 店铺二级域名
	@Column(columnDefinition = "int default 0")
	private int domain_modify_count;// 店铺二级域名修改次数，受商城设置影响
	@Column(columnDefinition = "int default 0")
	private int favorite_count;// 店铺收藏人气
	@OneToOne(mappedBy = "store", fetch = FetchType.LAZY)
	private StorePoint point;// 店铺评分统计
	@Column(columnDefinition = "varchar(255) default 'baidu'")
	private String map_type;// 地图类型
	@Column(precision = 18, scale = 15)
	private BigDecimal store_lat;// 店铺纬度
	@Column(precision = 18, scale = 15)
	private BigDecimal store_lng;// 店铺经度
	private Date delivery_begin_time;// 买就送套餐开始日期
	private Date delivery_end_time;// 买就送套餐结束日期
	private Date combin_begin_time;// 组合销售套餐开始日期
	private Date combin_end_time;// 组合销售套餐结束日期
	@OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
	private List<StoreGradeLog> logs = new ArrayList<StoreGradeLog>();// 店铺对应的店铺升级日志
	@OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
	private List<Payment> payments = new ArrayList<Payment>();// 店铺对应的支付方式
	@OneToOne(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private StorePoint sp;// 店铺评分统计类
	@OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
	private List<StoreNavigation> navs = new ArrayList<StoreNavigation>();// 店铺对应的自定义导航
	@OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
	private List<Favorite> favs = new ArrayList<Favorite>();// 店铺被用户所收藏
	@OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
	private List<GoodsClassStaple> gcss = new ArrayList<GoodsClassStaple>();// 店铺常用分类收藏
	@OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
	private List<OrderForm> ofs = new ArrayList<OrderForm>();// 店铺的订单
	@OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
	private List<DeliveryLog> delivery_logs = new ArrayList<DeliveryLog>();// 买就送购买套餐
	@OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
	private List<CombinLog> combin_logs = new ArrayList<CombinLog>();// 组合销售购买套餐日志
	@OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
	private List<Transport> transport_list = new ArrayList<Transport>();// 运费模板信息
	@OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
	private List<Dynamic> dynamics = new ArrayList<Dynamic>();// 店铺对应的动态信息
	@OneToMany(mappedBy = "store",cascade =CascadeType.REMOVE)
	private List<StoreCart> carts = new ArrayList<StoreCart>();// 对应的购物车信息
	@Column(columnDefinition = "int default 0")
	private int weixin_status;// 微信店铺状态，0为未开启，1为已经开启,2为过期关闭,3违规关闭
	@Temporal(value = TemporalType.DATE)
	private Date weixin_begin_time;// 微信店铺开始时间
	@Temporal(value = TemporalType.DATE)
	private Date weixin_end_time;// 微信店铺结束时间
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory weixin_qr_img;// 微信二维码图片
	private String weixin_account;// 微信账号
	private String weixin_token;// 微信token，申请开发者时自己填写的token
	private String weixin_appId;// 微信App Id，申请开发者成功后微信生成的AppId
	private String weixin_appSecret;// 微信AppSecret，申请开发者成功后微信生成的AppSecret
	@Column(columnDefinition = "LongText")
	private String weixin_welecome_content;// 关注微信时候的欢迎词
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory store_weixin_logo;// 微信店铺logo
	@OneToMany(mappedBy = "store",cascade = CascadeType.REMOVE)
	private List<VLog> vlogs = new ArrayList<VLog>();// 微信商城购买日志

	public List<VLog> getVlogs() {
		return vlogs;
	}

	public void setVlogs(List<VLog> vlogs) {
		this.vlogs = vlogs;
	}

	public List<StoreCart> getCarts() {
		return carts;
	}

	public void setCarts(List<StoreCart> carts) {
		this.carts = carts;
	}

	public Accessory getStore_weixin_logo() {
		return store_weixin_logo;
	}

	public void setStore_weixin_logo(Accessory store_weixin_logo) {
		this.store_weixin_logo = store_weixin_logo;
	}

	public String getWeixin_account() {
		return weixin_account;
	}

	public void setWeixin_account(String weixin_account) {
		this.weixin_account = weixin_account;
	}

	public Accessory getWeixin_qr_img() {
		return weixin_qr_img;
	}

	public void setWeixin_qr_img(Accessory weixin_qr_img) {
		this.weixin_qr_img = weixin_qr_img;
	}

	public String getWeixin_welecome_content() {
		return weixin_welecome_content;
	}

	public void setWeixin_welecome_content(String weixin_welecome_content) {
		this.weixin_welecome_content = weixin_welecome_content;
	}

	public String getWeixin_token() {
		return weixin_token;
	}

	public void setWeixin_token(String weixin_token) {
		this.weixin_token = weixin_token;
	}

	public String getWeixin_appId() {
		return weixin_appId;
	}

	public void setWeixin_appId(String weixin_appId) {
		this.weixin_appId = weixin_appId;
	}

	public String getWeixin_appSecret() {
		return weixin_appSecret;
	}

	public void setWeixin_appSecret(String weixin_appSecret) {
		this.weixin_appSecret = weixin_appSecret;
	}

	public int getWeixin_status() {
		return weixin_status;
	}

	public void setWeixin_status(int weixin_status) {
		this.weixin_status = weixin_status;
	}

	public Date getWeixin_begin_time() {
		return weixin_begin_time;
	}

	public void setWeixin_begin_time(Date weixin_begin_time) {
		this.weixin_begin_time = weixin_begin_time;
	}

	public Date getWeixin_end_time() {
		return weixin_end_time;
	}

	public void setWeixin_end_time(Date weixin_end_time) {
		this.weixin_end_time = weixin_end_time;
	}

	public List<Dynamic> getDynamics() {
		return dynamics;
	}

	public void setDynamics(List<Dynamic> dynamics) {
		this.dynamics = dynamics;
	}

	public List<Transport> getTransport_list() {
		return transport_list;
	}

	public void setTransport_list(List<Transport> transport_list) {
		this.transport_list = transport_list;
	}

	public List<CombinLog> getCombin_logs() {
		return combin_logs;
	}

	public void setCombin_logs(List<CombinLog> combin_logs) {
		this.combin_logs = combin_logs;
	}

	public List<OrderForm> getOfs() {
		return ofs;
	}

	public void setOfs(List<OrderForm> ofs) {
		this.ofs = ofs;
	}

	public List<GoodsClassStaple> getGcss() {
		return gcss;
	}

	public void setGcss(List<GoodsClassStaple> gcss) {
		this.gcss = gcss;
	}

	public List<Favorite> getFavs() {
		return favs;
	}

	public void setFavs(List<Favorite> favs) {
		this.favs = favs;
	}

	public List<StoreNavigation> getNavs() {
		return navs;
	}

	public void setNavs(List<StoreNavigation> navs) {
		this.navs = navs;
	}

	public StorePoint getSp() {
		return sp;
	}

	public void setSp(StorePoint sp) {
		this.sp = sp;
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}

	public BigDecimal getStore_lat() {
		return store_lat;
	}

	public void setStore_lat(BigDecimal store_lat) {
		this.store_lat = store_lat;
	}

	public BigDecimal getStore_lng() {
		return store_lng;
	}

	public void setStore_lng(BigDecimal store_lng) {
		this.store_lng = store_lng;
	}

	public int getFavorite_count() {
		return favorite_count;
	}

	public void setFavorite_count(int favorite_count) {
		this.favorite_count = favorite_count;
	}

	public String getStore_second_domain() {
		return store_second_domain;
	}

	public void setStore_second_domain(String store_second_domain) {
		this.store_second_domain = store_second_domain;
	}

	public int getDomain_modify_count() {
		return domain_modify_count;
	}

	public void setDomain_modify_count(int domain_modify_count) {
		this.domain_modify_count = domain_modify_count;
	}

	public List<StoreSlide> getSlides() {
		return slides;
	}

	public void setSlides(List<StoreSlide> slides) {
		this.slides = slides;
	}

	public String getViolation_reseaon() {
		return violation_reseaon;
	}

	public void setViolation_reseaon(String violation_reseaon) {
		this.violation_reseaon = violation_reseaon;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public int getStore_credit() {
		return store_credit;
	}

	public void setStore_credit(int store_credit) {
		this.store_credit = store_credit;
	}

	public List<Goods> getGoods_list() {
		return goods_list;
	}

	public void setGoods_list(List<Goods> goods_list) {
		this.goods_list = goods_list;
	}

	public StoreClass getSc() {
		return sc;
	}

	public void setSc(StoreClass sc) {
		this.sc = sc;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public String getStore_address() {
		return store_address;
	}

	public void setStore_address(String store_address) {
		this.store_address = store_address;
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	public String getStore_ower() {
		return store_ower;
	}

	public void setStore_ower(String store_ower) {
		this.store_ower = store_ower;
	}

	public String getStore_ower_card() {
		return store_ower_card;
	}

	public void setStore_ower_card(String store_ower_card) {
		this.store_ower_card = store_ower_card;
	}

	public StoreGrade getGrade() {
		return grade;
	}

	public void setGrade(StoreGrade grade) {
		this.grade = grade;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isStore_recommend() {
		return store_recommend;
	}

	public void setStore_recommend(boolean store_recommend) {
		this.store_recommend = store_recommend;
	}

	public Date getValidity() {
		return validity;
	}

	public void setValidity(Date validity) {
		this.validity = validity;
	}

	public boolean isCard_approve() {
		return card_approve;
	}

	public void setCard_approve(boolean card_approve) {
		this.card_approve = card_approve;
	}

	public Accessory getCard() {
		return card;
	}

	public void setCard(Accessory card) {
		this.card = card;
	}

	public boolean isRealstore_approve() {
		return realstore_approve;
	}

	public void setRealstore_approve(boolean realstore_approve) {
		this.realstore_approve = realstore_approve;
	}

	public Accessory getStore_license() {
		return store_license;
	}

	public void setStore_license(Accessory store_license) {
		this.store_license = store_license;
	}

	public int getStore_status() {
		return store_status;
	}

	public void setStore_status(int store_status) {
		this.store_status = store_status;
	}

	public String getStore_telephone() {
		return store_telephone;
	}

	public void setStore_telephone(String store_telephone) {
		this.store_telephone = store_telephone;
	}

	public String getStore_zip() {
		return store_zip;
	}

	public void setStore_zip(String store_zip) {
		this.store_zip = store_zip;
	}

	public Accessory getStore_logo() {
		return store_logo;
	}

	public void setStore_logo(Accessory store_logo) {
		this.store_logo = store_logo;
	}

	public Accessory getStore_banner() {
		return store_banner;
	}

	public void setStore_banner(Accessory store_banner) {
		this.store_banner = store_banner;
	}

	public String getStore_seo_keywords() {
		return store_seo_keywords;
	}

	public void setStore_seo_keywords(String store_seo_keywords) {
		this.store_seo_keywords = store_seo_keywords;
	}

	public String getStore_seo_description() {
		return store_seo_description;
	}

	public void setStore_seo_description(String store_seo_description) {
		this.store_seo_description = store_seo_description;
	}

	public String getStore_info() {
		return store_info;
	}

	public void setStore_info(String store_info) {
		this.store_info = store_info;
	}

	public StoreGrade getUpdate_grade() {
		return update_grade;
	}

	public void setUpdate_grade(StoreGrade update_grade) {
		this.update_grade = update_grade;
	}

	public Date getStore_recommend_time() {
		return store_recommend_time;
	}

	public void setStore_recommend_time(Date store_recommend_time) {
		this.store_recommend_time = store_recommend_time;
	}

	public String getStore_qq() {
		return store_qq;
	}

	public void setStore_qq(String store_qq) {
		this.store_qq = store_qq;
	}

	public String getStore_msn() {
		return store_msn;
	}

	public void setStore_msn(String store_msn) {
		this.store_msn = store_msn;
	}

	public StorePoint getPoint() {
		return point;
	}

	public void setPoint(StorePoint point) {
		this.point = point;
	}

	public List<StoreGradeLog> getLogs() {
		return logs;
	}

	public void setLogs(List<StoreGradeLog> logs) {
		this.logs = logs;
	}

	public String getStore_ww() {
		return store_ww;
	}

	public void setStore_ww(String store_ww) {
		this.store_ww = store_ww;
	}

	public String getMap_type() {
		return map_type;
	}

	public void setMap_type(String map_type) {
		this.map_type = map_type;
	}

	public Date getDelivery_begin_time() {
		return delivery_begin_time;
	}

	public void setDelivery_begin_time(Date delivery_begin_time) {
		this.delivery_begin_time = delivery_begin_time;
	}

	public Date getDelivery_end_time() {
		return delivery_end_time;
	}

	public void setDelivery_end_time(Date delivery_end_time) {
		this.delivery_end_time = delivery_end_time;
	}

	public List<DeliveryLog> getDelivery_logs() {
		return delivery_logs;
	}

	public void setDelivery_logs(List<DeliveryLog> delivery_logs) {
		this.delivery_logs = delivery_logs;
	}

	public Date getCombin_begin_time() {
		return combin_begin_time;
	}

	public void setCombin_begin_time(Date combin_begin_time) {
		this.combin_begin_time = combin_begin_time;
	}

	public Date getCombin_end_time() {
		return combin_end_time;
	}

	public void setCombin_end_time(Date combin_end_time) {
		this.combin_end_time = combin_end_time;
	}

}
