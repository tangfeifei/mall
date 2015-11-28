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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.annotation.Lock;
import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * 
 * <p>
 * Title: OrderForm.java
 * </p>
 * 
 * <p>
 * Description: 系统订单管理类，管理商城系统订单信息
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011-2014
 * </p>
 * 
 
 * 
 * @author erikzhang、hezeng
 * 
 * @date 2014-5-9
 * 
 * @version LakeCloud_C2C 1.4
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "orderform")
public class OrderForm extends IdEntity {
	private String trade_no;// 交易流水号,在线支付时每次随机生成唯一的号码，重复提交时替换当前订单的交易流水号
	private String order_id;// 订单号
	private String out_order_id;// 外部单号
	private String order_type;// 订单类型，分为web:PC网页订单，weixin:手机网页订单,android:android手机客户端订单，ios:iOS手机客户端订单
	@OneToMany(mappedBy = "of")
	List<GoodsCart> gcs = new ArrayList<GoodsCart>();// 订单对应的购物车
	@Lock
	@Column(precision = 12, scale = 2)
	private BigDecimal totalPrice;// 订单总价格
	@Lock
	@Column(precision = 12, scale = 2)
	private BigDecimal goods_amount;// 商品总价格
	@Lob
	@Column(columnDefinition = "LongText")
	private String msg;// 订单附言
	@ManyToOne(fetch = FetchType.LAZY)
	private Payment payment;// 支付方式
	private String transport;// 配送方式
	private String shipCode;// 物流单号
	private String return_shipCode;// 买家退货时退货物流单号
	private Date return_shipTime;// 买家退货发货截止时间
	@Lob
	@Column(columnDefinition = "LongText")
	private String return_content;// 退货理由
	@ManyToOne(fetch = FetchType.LAZY)
	private ExpressCompany ec;// 物流公司信息
	@ManyToOne(fetch = FetchType.LAZY)
	private ExpressCompany return_ec;// 买家退货时退货物流公司信息
	@Lock
	@Column(precision = 12, scale = 2)
	private BigDecimal ship_price;// 配送价格
	@Lock
	private int order_status;// 订单状态，0为订单取消，10为已提交待付款，15为线下付款提交申请，16为货到付款，20为已付款待发货，30为已发货待收货，40为已收货,45买家申请退货，

	// 46退货中(卖家同意退货)，47退货成功,48退货申请被拒绝，49退货失败，买家没有在系统设定时间内输入提交退货物流信息
	// 50买家评价完毕，60卖家评价完毕订单完成,65订单不可评价，到达设定时间，系统自动关闭订单相互评价功能
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;// 买家信息
	@ManyToOne(fetch = FetchType.LAZY)
	private Store store;// 订单对应的店铺
	private Date payTime;// 付款时间
	private Date shipTime;// 发货时间
	private Date finishTime;// 完成时间
	@ManyToOne(fetch = FetchType.LAZY)
	private Address addr;// 配送地址
	private int invoiceType;// 发票类型，0为个人，1为单位
	private String invoice;// 发票信息
	@OneToMany(mappedBy = "of", cascade = CascadeType.REMOVE)
	private List<OrderFormLog> ofls = new ArrayList<OrderFormLog>();// 订单日志
	@OneToMany(mappedBy = "of", cascade = CascadeType.REMOVE)
	private List<RefundLog> rls = new ArrayList<RefundLog>();// 退款日志记录
	@Lob
	@Column(columnDefinition = "LongText")
	private String pay_msg;// 支付相关说明，比如汇款账号、时间等
	@Column(precision = 12, scale = 2)
	private BigDecimal refund;// 退款金额
	private String refund_type;// 退款方式
	@Column(columnDefinition = "bit default 0")
	private boolean auto_confirm_email;// 自动收款的邮件提示
	@Column(columnDefinition = "bit default 0")
	private boolean auto_confirm_sms;// 自动收款的短信提示
	@OneToMany(mappedBy = "of", cascade = CascadeType.REMOVE)
	private List<GoodsReturnLog> grls = new ArrayList<GoodsReturnLog>();// 退款日志
	@OneToMany(mappedBy = "of", cascade = CascadeType.REMOVE)
	private List<Evaluate> evas = new ArrayList<Evaluate>();// 订单对应的评价
	@OneToMany(mappedBy = "of", cascade = CascadeType.REMOVE)
	private List<Complaint> complaints = new ArrayList<Complaint>();// 投诉管理类
	@OneToOne(fetch = FetchType.LAZY)
	private CouponInfo ci;// 订单使用的优惠券
	@Column(columnDefinition = "LongText")
	private String order_seller_intro;// 订单卖家给予的说明，用在虚拟商品信息，比如购买充值卡，卖家发货时在这里给出对应的卡号和密钥

	public String getTrade_no() {
		return trade_no;
	}

	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}

	public String getOrder_type() {
		return order_type;
	}

	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}

	public String getReturn_content() {
		return return_content;
	}

	public void setReturn_content(String return_content) {
		this.return_content = return_content;
	}

	public Date getReturn_shipTime() {
		return return_shipTime;
	}

	public void setReturn_shipTime(Date return_shipTime) {
		this.return_shipTime = return_shipTime;
	}

	public String getReturn_shipCode() {
		return return_shipCode;
	}

	public void setReturn_shipCode(String return_shipCode) {
		this.return_shipCode = return_shipCode;
	}

	public ExpressCompany getReturn_ec() {
		return return_ec;
	}

	public void setReturn_ec(ExpressCompany return_ec) {
		this.return_ec = return_ec;
	}

	public CouponInfo getCi() {
		return ci;
	}

	public void setCi(CouponInfo ci) {
		this.ci = ci;
	}

	public List<Complaint> getComplaints() {
		return complaints;
	}

	public void setComplaints(List<Complaint> complaints) {
		this.complaints = complaints;
	}

	public List<Evaluate> getEvas() {
		return evas;
	}

	public void setEvas(List<Evaluate> evas) {
		this.evas = evas;
	}

	public List<GoodsReturnLog> getGrls() {
		return grls;
	}

	public void setGrls(List<GoodsReturnLog> grls) {
		this.grls = grls;
	}

	public BigDecimal getRefund() {
		return refund;
	}

	public void setRefund(BigDecimal refund) {
		this.refund = refund;
	}

	public String getRefund_type() {
		return refund_type;
	}

	public void setRefund_type(String refund_type) {
		this.refund_type = refund_type;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public BigDecimal getShip_price() {
		return ship_price;
	}

	public void setShip_price(BigDecimal ship_price) {
		this.ship_price = ship_price;
	}

	public int getOrder_status() {
		return order_status;
	}

	public void setOrder_status(int order_status) {
		this.order_status = order_status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public List<GoodsCart> getGcs() {
		return gcs;
	}

	public void setGcs(List<GoodsCart> gcs) {
		this.gcs = gcs;
	}

	public Address getAddr() {
		return addr;
	}

	public void setAddr(Address addr) {
		this.addr = addr;
	}

	public String getShipCode() {
		return shipCode;
	}

	public void setShipCode(String shipCode) {
		this.shipCode = shipCode;
	}

	public Date getShipTime() {
		return shipTime;
	}

	public void setShipTime(Date shipTime) {
		this.shipTime = shipTime;
	}

	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}

	public int getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(int invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getInvoice() {
		return invoice;
	}

	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public List<OrderFormLog> getOfls() {
		return ofls;
	}

	public void setOfls(List<OrderFormLog> ofls) {
		this.ofls = ofls;
	}

	public String getPay_msg() {
		return pay_msg;
	}

	public void setPay_msg(String pay_msg) {
		this.pay_msg = pay_msg;
	}

	public BigDecimal getGoods_amount() {
		return goods_amount;
	}

	public void setGoods_amount(BigDecimal goods_amount) {
		this.goods_amount = goods_amount;
	}

	public List<RefundLog> getRls() {
		return rls;
	}

	public void setRls(List<RefundLog> rls) {
		this.rls = rls;
	}

	public boolean isAuto_confirm_email() {
		return auto_confirm_email;
	}

	public void setAuto_confirm_email(boolean auto_confirm_email) {
		this.auto_confirm_email = auto_confirm_email;
	}

	public boolean isAuto_confirm_sms() {
		return auto_confirm_sms;
	}

	public void setAuto_confirm_sms(boolean auto_confirm_sms) {
		this.auto_confirm_sms = auto_confirm_sms;
	}

	public String getTransport() {
		return transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}

	public ExpressCompany getEc() {
		return ec;
	}

	public void setEc(ExpressCompany ec) {
		this.ec = ec;
	}

	public String getOut_order_id() {
		return out_order_id;
	}

	public void setOut_order_id(String out_order_id) {
		this.out_order_id = out_order_id;
	}

	public String getOrder_seller_intro() {
		return order_seller_intro;
	}

	public void setOrder_seller_intro(String order_seller_intro) {
		this.order_seller_intro = order_seller_intro;
	}
}
