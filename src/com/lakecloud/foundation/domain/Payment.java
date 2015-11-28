package com.lakecloud.foundation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 系统支付方式管理类，支付方式分为平台支付方式、店铺支付方式，平台支付方式可以接受所有用户对平台的付款，店铺支付方式接受买家对店铺的付款
 
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "payment")
public class Payment extends IdEntity {
	// 以下是通用信息
	private boolean install;// 是否启用
	private String name;// 支付方式名称
	private String mark;// 支付方式标识代码,alipay为支付宝，alipay_wap为支付宝手机网页支付，99bill为快钱，tenpay为财付通,balance为预存款，outline为线下支付
	// 以下为支付宝信息
	private String safeKey; // 交易安全校验码
	private String partner;// 合作者身份ID
	private String seller_email; // 签约支付宝账号或卖家收款支付宝帐户
	private int interfaceType;// 选择接口类型，支付宝使用
	@Column(precision = 12, scale = 2)
	private BigDecimal alipay_rate;// 支付宝手续费率
	@Column(precision = 12, scale = 2)
	private BigDecimal alipay_divide_rate;// 支付宝平台分成比例，根据支付宝规定，目前仅支持支付宝平台商即时到帐
	// 以下为快钱信息
	private String merchantAcctId;// 快钱收款账户
	private String rmbKey;// 人民币网关密钥
	private String pid;// 快钱的合作伙伴的账户号
	// 以下为财付通信息,20120313之后审核成功的中介担保用户及所有即时到帐用户
	private String spname;
	private String tenpay_partner;
	private String tenpay_key;
	private int trade_mode;
	// 以下是网银在线信息
	private String chinabank_account;
	private String chinabank_key;
	// 以下是其他支付信息说明文字,如银行账户信息等
	@Lob
	@Column(columnDefinition = "LongText")
	private String content;
	// 以下是预存款信息
	@Column(precision = 12, scale = 2)
	private BigDecimal balance_divide_rate;// 预付款平台分成比例
	// 以下为paypal支付信息
	private String paypal_userId;// paypal商户Id
	private String currency_code;// paypal支付货币种类
	@Column(precision = 12, scale = 2)
	private BigDecimal poundage;// paypal支付手续费
	// 以下为支付方式分类
	private String type;// 支付方式类型，admin为向平台支付，user为向商家店铺的支付
	@ManyToOne(fetch = FetchType.LAZY)
	private Store store;// 支付方式对应的店铺
	@OneToMany(mappedBy = "payment")
	private List<OrderForm> ofs = new ArrayList<OrderForm>();// 对应的订单，反向映射，便于获取订单信息

	public String getPaypal_userId() {
		return paypal_userId;
	}

	public void setPaypal_userId(String paypal_userId) {
		this.paypal_userId = paypal_userId;
	}

	public String getCurrency_code() {
		return currency_code;
	}

	public void setCurrency_code(String currency_code) {
		this.currency_code = currency_code;
	}

	public BigDecimal getPoundage() {
		return poundage;
	}

	public void setPoundage(BigDecimal poundage) {
		this.poundage = poundage;
	}

	public BigDecimal getAlipay_rate() {
		return alipay_rate;
	}

	public void setAlipay_rate(BigDecimal alipay_rate) {
		this.alipay_rate = alipay_rate;
	}

	public BigDecimal getAlipay_divide_rate() {
		return alipay_divide_rate;
	}

	public void setAlipay_divide_rate(BigDecimal alipay_divide_rate) {
		this.alipay_divide_rate = alipay_divide_rate;
	}

	public BigDecimal getBalance_divide_rate() {
		return balance_divide_rate;
	}

	public void setBalance_divide_rate(BigDecimal balance_divide_rate) {
		this.balance_divide_rate = balance_divide_rate;
	}

	public List<OrderForm> getOfs() {
		return ofs;
	}

	public void setOfs(List<OrderForm> ofs) {
		this.ofs = ofs;
	}

	public boolean isInstall() {
		return install;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public void setInstall(boolean install) {
		this.install = install;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getSafeKey() {
		return safeKey;
	}

	public void setSafeKey(String safeKey) {
		this.safeKey = safeKey;
	}

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public String getSeller_email() {
		return seller_email;
	}

	public void setSeller_email(String seller_email) {
		this.seller_email = seller_email;
	}

	public int getInterfaceType() {
		return interfaceType;
	}

	public void setInterfaceType(int interfaceType) {
		this.interfaceType = interfaceType;
	}

	public String getMerchantAcctId() {
		return merchantAcctId;
	}

	public void setMerchantAcctId(String merchantAcctId) {
		this.merchantAcctId = merchantAcctId;
	}

	public String getRmbKey() {
		return rmbKey;
	}

	public void setRmbKey(String rmbKey) {
		this.rmbKey = rmbKey;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getChinabank_account() {
		return chinabank_account;
	}

	public void setChinabank_account(String chinabank_account) {
		this.chinabank_account = chinabank_account;
	}

	public String getChinabank_key() {
		return chinabank_key;
	}

	public void setChinabank_key(String chinabank_key) {
		this.chinabank_key = chinabank_key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSpname() {
		return spname;
	}

	public void setSpname(String spname) {
		this.spname = spname;
	}

	public String getTenpay_partner() {
		return tenpay_partner;
	}

	public void setTenpay_partner(String tenpay_partner) {
		this.tenpay_partner = tenpay_partner;
	}

	public String getTenpay_key() {
		return tenpay_key;
	}

	public void setTenpay_key(String tenpay_key) {
		this.tenpay_key = tenpay_key;
	}

	public int getTrade_mode() {
		return trade_mode;
	}

	public void setTrade_mode(int trade_mode) {
		this.trade_mode = trade_mode;
	}

}
