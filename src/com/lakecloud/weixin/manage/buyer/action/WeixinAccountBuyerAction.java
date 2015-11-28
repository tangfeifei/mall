package com.lakecloud.weixin.manage.buyer.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.CouponInfo;
import com.lakecloud.foundation.domain.Favorite;
import com.lakecloud.foundation.domain.GoodsCart;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.ICouponInfoService;
import com.lakecloud.foundation.service.IFavoriteService;
import com.lakecloud.foundation.service.IOrderFormService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;

/**
 * @info 微信客户端买家用户中心
 * @since V1.3
 * @version 1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net hz 2013-11-25
 * 
 */
@Controller
public class WeixinAccountBuyerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IFavoriteService favoriteService;

	private int orderGoodsCount(List<OrderForm> OrderForms) {
		int goods_count = 0;
		for (OrderForm order : OrderForms) {
			for (GoodsCart gc : order.getGcs()) {
				if (gc.getGoods() != null && !gc.equals("")) {
					goods_count++;
				}
			}
		}
		return goods_count;
	}

	/**
	 * 用户中心
	 * 
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "用户中心", value = "/weixin/buyer/account.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/account.htm")
	public ModelAndView weixin_buyer_account(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/account.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		Map map = new HashMap();
		map.put("uid", user.getId());
		map.put("status", 0);
		List<CouponInfo> CouponInfos = this.couponInfoService
				.query("select obj from CouponInfo obj where obj.user.id=:uid and obj.status=:status",
						map, -1, -1);
		map.clear();
		map.put("order_status", 10);
		map.put("uid", user.getId());
		List<OrderForm> order_status_10 = this.orderFormService
				.query("select obj from OrderForm obj where obj.order_status=:order_status and obj.user.id=:uid order by addTime desc",
						map, -1, -1);
		map.clear();
		map.put("order_status", 20);
		map.put("uid", user.getId());
		List<OrderForm> order_status_20 = this.orderFormService
				.query("select obj from OrderForm obj where obj.order_status=:order_status and obj.user.id=:uid order by addTime desc",
						map, -1, -1);
		map.clear();
		map.put("order_status", 30);
		map.put("uid", user.getId());
		List<OrderForm> order_status_30 = this.orderFormService
				.query("select obj from OrderForm obj where obj.order_status=:order_status and obj.user.id=:uid order by addTime desc",
						map, -1, -1);
		map.clear();
		map.put("order_status", 40);
		map.put("uid", user.getId());
		List<OrderForm> order_status_40 = this.orderFormService
				.query("select obj from OrderForm obj where obj.order_status=:order_status and obj.user.id=:uid order by addTime desc",
						map, -1, -1);
		mv.addObject("goods_count_10", this.orderGoodsCount(order_status_10));
		mv.addObject("goods_count_20", this.orderGoodsCount(order_status_20));
		mv.addObject("goods_count_30", this.orderGoodsCount(order_status_30));
		mv.addObject("goods_count_40", this.orderGoodsCount(order_status_40));
		mv.addObject("Coupons", CouponInfos.size());
		mv.addObject("user", user);
		return mv;
	}

	/**
	 * 我的收藏列表页
	 * 
	 * @param beginCount
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "微信用户我的收藏", value = "/weixin/buyer/favorite_goods.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/favorite_goods.htm")
	public ModelAndView weixin_favorite_goods(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/favorite_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("type", 0);
		params.put("uid", SecurityUserHolder.getCurrentUser().getId());
		List<Favorite> objs = this.favoriteService
				.query("select obj from Favorite obj where obj.type=:type and obj.user.id=:uid",
						params, 0, 6);
		mv.addObject("objs", objs);
		return mv;
	}

	/**
	 * 我的收藏列表页ajax加载
	 * 
	 * @param beginCount
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "微信用户我的收藏", value = "/weixin/buyer/favorite_goods_ajax.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/favorite_goods_ajax.htm")
	public ModelAndView weixin_favorite_goods_ajax(HttpServletRequest request,
			HttpServletResponse response, String beginCount) {
		int begin = 0;
		if (beginCount != null && !beginCount.equals("")) {
			begin = CommUtil.null2Int(beginCount);
		}
		ModelAndView mv = new JModelAndView("weixin/favorite_goods_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("type", 0);
		params.put("uid", SecurityUserHolder.getCurrentUser().getId());
		List<Favorite> objs = this.favoriteService
				.query("select obj from Favorite obj where obj.type=:type and obj.user.id=:uid",
						params, begin, 6);
		mv.addObject("objs", objs);
		return mv;
	}

	@SecurityMapping(title = "微信用户惠券列表", value = "/weixin/buyer/coupon.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/coupon.htm")
	public ModelAndView weixin_coupon(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/coupon.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("uid", SecurityUserHolder.getCurrentUser().getId());
		List<CouponInfo> objs = this.couponInfoService
				.query("select obj from CouponInfo obj where obj.user.id=:uid order by addTime desc",
						params, 0, 6);
		mv.addObject("objs", objs);
		return mv;
	}

	@SecurityMapping(title = "微信用户惠券列表ajax加载", value = "/weixin/buyer/coupon_ajax.htm*", rtype = "buyer", rname = "微信用户中心", rcode = "weixin_user_center", rgroup = "微信用户中心")
	@RequestMapping("/weixin/buyer/coupon_ajax.htm")
	public ModelAndView weixin_coupon_ajax(HttpServletRequest request,
			HttpServletResponse response, String beginCount) {
		ModelAndView mv = new JModelAndView("weixin/coupon_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		int begin = 0;
		if (beginCount != null && !beginCount.equals("")) {
			begin = CommUtil.null2Int(beginCount);
		}
		Map params = new HashMap();
		params.put("uid", SecurityUserHolder.getCurrentUser().getId());
		List<CouponInfo> objs = this.couponInfoService
				.query("select obj from CouponInfo obj where obj.user.id=:uid order by addTime desc",
						params, begin, 6);
		mv.addObject("objs", objs);
		return mv;
	}

}
