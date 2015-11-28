package com.lakecloud.weixin.platform.view.action;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Activity;
import com.lakecloud.foundation.domain.ActivityGoods;
import com.lakecloud.foundation.domain.BargainGoods;
import com.lakecloud.foundation.domain.DeliveryGoods;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsBrand;
import com.lakecloud.foundation.domain.GoodsBrandCategory;
import com.lakecloud.foundation.domain.GoodsClass;
import com.lakecloud.foundation.domain.GroupGoods;
import com.lakecloud.foundation.service.IActivityGoodsService;
import com.lakecloud.foundation.service.IActivityService;
import com.lakecloud.foundation.service.IBargainGoodsService;
import com.lakecloud.foundation.service.IDeliveryGoodsService;
import com.lakecloud.foundation.service.IGoodsBrandCategoryService;
import com.lakecloud.foundation.service.IGoodsBrandService;
import com.lakecloud.foundation.service.IGoodsClassService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IGroupGoodsService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;

/**
 * @info 微信商城客户端商品管理控制器，用来查看微信商城中的商品
 * @since V1.3
 * @version 1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net hz
 * 
 */
@Controller
public class WeixinPlatformGoodsViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGroupGoodsService groupgoodsService;
	@Autowired
	private IActivityService activityService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private IBargainGoodsService bargainGoodsService;
	@Autowired
	private IGoodsBrandCategoryService goodsBrandCategorySerivce;
	@Autowired
	private IGoodsBrandService goodsBrandSerivce;
	@Autowired
	private IDeliveryGoodsService deliverygoodsSerivce;
	@Autowired
	private IGoodsClassService gcSerivce;

	/**
	 * 商品列表
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/goods_list.htm")
	public ModelAndView goods_list(HttpServletRequest request,
			HttpServletResponse response, String type) {
		ModelAndView mv = new JModelAndView("weixin/platform/goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).setAttribute("lakecloud_view_type", "weixin");
		String op_title = "商品列表";
		List<Goods> goods_list = null;
		if (type != null && !type.equals("")) {
			if (type.equals("hot")) {
				op_title = "热卖宝贝";
				Map params = new HashMap();
				params.put("goods_status", 0);
				params.put("weixin_status", 1);
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.weixin_status=:weixin_status and obj.goods_status=:goods_status order by obj.goods_salenum desc",
								params, 0, 12);
			}
			if (type.equals("recommend")) {
				op_title = "商城推荐";
				Map params = new HashMap();
				params.put("goods_status", 0);
				params.put("weixin_status", 1);
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.weixin_status=:weixin_status and obj.goods_status=:goods_status order by obj.weixin_shop_recommendTime asc",
								params, 0, 12);
			}
			mv.addObject("type", type);
		}
		mv.addObject("objs", goods_list);
		mv.addObject("op_title", op_title);
		return mv;
	}

	/**
	 * 商品列表动态加载
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/goods_data.htm")
	public ModelAndView goods_data(HttpServletRequest request,
			HttpServletResponse response, String begin_count, String type) {
		ModelAndView mv = new JModelAndView("weixin/platform/goods_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<Goods> goods_list = null;
		int begin = 0;
		if (begin_count != null && !begin_count.equals("")) {
			begin = CommUtil.null2Int(begin_count);
		}
		if (type != null && !type.equals("")) {
			if (type.equals("hot")) {
				Map params = new HashMap();
				params.put("goods_status", 0);
				params.put("weixin_status", 1);
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.weixin_status=:weixin_status and obj.goods_status=:goods_status order by obj.goods_salenum desc",
								params, begin, 12);
			}
			if (type.equals("recommend")) {
				Map params = new HashMap();
				params.put("goods_status", 0);
				params.put("weixin_status", 1);
				params.put("weixin_shop_recommend", true);
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.weixin_status=:weixin_status and obj.goods_status=:goods_status and obj.weixin_shop_recommend=:weixin_shop_recommend order by obj.weixin_shop_recommendTime asc",
								params, begin, 12);
			}
		}
		mv.addObject("objs", goods_list);
		return mv;
	}

	/***
	 * 搜索列表
	 * 
	 * @param request
	 * @param response
	 * @param gbc_id
	 *            ：品牌类型id
	 * @param gb_id
	 *            ：品牌id
	 * @param keyword
	 *            ：关键字
	 * @return
	 */

	@RequestMapping("/weixin/platform/search_goods_list.htm")
	public ModelAndView search_goods_list(HttpServletRequest request,
			HttpServletResponse response, String gbc_id, String gb_id,
			String keyword, String gc_id) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/search_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String op_title = "商品列表";
		List<Goods> goods_list = null;
		Map params = new HashMap();
		if (gbc_id != null && !gbc_id.equals("")) {
			mv.addObject("gbc_id", gbc_id);
			GoodsBrandCategory gbc = this.goodsBrandCategorySerivce
					.getObjById(CommUtil.null2Long(gbc_id));
			op_title = gbc.getName();
			Set<Long> ids = this.getBrandIds(gbc);
			params.clear();
			params.put("goods_status", 0);
			params.put("weixin_status", 1);
			params.put("brand_ids", ids);
			goods_list = this.goodsService
					.query("select obj from Goods obj where obj.goods_store.weixin_status=:weixin_status and obj.goods_status=:goods_status and obj.goods_brand.id in(:brand_ids) order by obj.addTime desc",
							params, 0, 12);
		}
		if (gb_id != null && !gb_id.equals("")) {
			mv.addObject("gb_id", gb_id);
			GoodsBrand gb = this.goodsBrandSerivce.getObjById(CommUtil
					.null2Long(gb_id));
			op_title = gb.getName();
			params.clear();
			params.put("goods_status", 0);
			params.put("weixin_status", 1);
			params.put("brand_id", gb.getId());
			goods_list = this.goodsService
					.query("select obj from Goods obj where obj.goods_store.weixin_status=:weixin_status and obj.goods_status=:goods_status and obj.goods_brand.id=:brand_id order by obj.addTime desc",
							params, 0, 12);
		}
		if (keyword != null && !CommUtil.null2String(keyword).equals("")) {
			mv.addObject("keyword", CommUtil.null2String(keyword));
			op_title = "“" + CommUtil.null2String(keyword) + "”搜索结果";
			params.clear();
			params.put("goods_status", 0);
			params.put("weixin_status", 1);
			params.put("keyword", "%" + CommUtil.null2String(keyword) + "%");
			goods_list = this.goodsService
					.query("select obj from Goods obj where obj.goods_store.weixin_status=:weixin_status and obj.goods_status=:goods_status and obj.goods_name like:keyword order by obj.addTime desc",
							params, 0, 12);
		}
		if (gc_id != null && !gc_id.equals("")) {
			mv.addObject("gc_id", gc_id);
			GoodsClass gc = this.gcSerivce
					.getObjById(CommUtil.null2Long(gc_id));
			op_title = "“" + gc.getClassName() + "”搜索结果";
			params.clear();
			params.put("goods_status", 0);
			params.put("weixin_status", 1);
			params.put("gc_id", gc.getId());
			goods_list = this.goodsService
					.query("select obj from Goods obj where obj.goods_store.weixin_status=:weixin_status and obj.goods_status=:goods_status and obj.gc.id=:gc_id order by obj.addTime desc",
							params, 0, 12);
		}
		mv.addObject("objs", goods_list);
		mv.addObject("op_title", op_title);
		request.getSession(false).setAttribute("lakecloud_view_type", "weixin");
		return mv;
	}

	/***
	 * 搜索列表动态加载
	 * 
	 * @param request
	 * @param response
	 * @param gbc_id
	 *            ：品牌类型id
	 * @param gb_id
	 *            ：品牌id
	 * @param keyword
	 *            ：关键字
	 * @return
	 */
	@RequestMapping("/weixin/platform/search_goods_data.htm")
	public ModelAndView search_goods_data(HttpServletRequest request,
			HttpServletResponse response, String begin_count, String gbc_id,
			String gb_id, String keyword, String gc_id) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/search_goods_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		int begin = 0;
		if (begin_count != null && !begin_count.equals("")) {
			begin = CommUtil.null2Int(begin_count);
		}
		List<Goods> goods_list = null;
		Map params = new HashMap();
		if (gbc_id != null && !gbc_id.equals("")) {
			GoodsBrandCategory gbc = this.goodsBrandCategorySerivce
					.getObjById(CommUtil.null2Long(gbc_id));
			Set<Long> ids = this.getBrandIds(gbc);
			params.clear();
			params.put("goods_status", 0);
			params.put("weixin_status", 1);
			params.put("brand_ids", ids);
			goods_list = this.goodsService
					.query("select obj from Goods obj where obj.goods_store.weixin_status=:weixin_status and obj.goods_status=:goods_status and obj.goods_brand.id in(:brand_ids)",
							params, begin, 12);
		}
		if (gb_id != null && !gb_id.equals("")) {
			GoodsBrand gb = this.goodsBrandSerivce.getObjById(CommUtil
					.null2Long(gb_id));
			params.clear();
			params.put("goods_status", 0);
			params.put("weixin_status", 1);
			params.put("brand_id", gb.getId());
			goods_list = this.goodsService
					.query("select obj from Goods obj where obj.goods_store.weixin_status=:weixin_status and obj.goods_status=:goods_status and obj.goods_brand.id=:brand_id",
							params, begin, 12);
		}
		if (keyword != null && !CommUtil.null2String(keyword).equals("")) {
			params.clear();
			params.put("goods_status", 0);
			params.put("weixin_status", 1);
			params.put("keyword", "%" + keyword + "%");
			goods_list = this.goodsService
					.query("select obj from Goods obj where obj.goods_store.weixin_status=:weixin_status and obj.goods_status=:goods_status and obj.goods_name like:keyword",
							params, begin, 12);
		}
		if (gc_id != null && !gc_id.equals("")) {
			mv.addObject("gc_id", gc_id);
			GoodsClass gc = this.gcSerivce
					.getObjById(CommUtil.null2Long(gc_id));
			params.clear();
			params.put("goods_status", 0);
			params.put("weixin_status", 1);
			params.put("gc_id", gc.getId());
			goods_list = this.goodsService
					.query("select obj from Goods obj where obj.goods_store.weixin_status=:weixin_status and obj.goods_status=:goods_status and obj.gc.id=:gc_id",
							params, begin, 12);
		}
		mv.addObject("objs", goods_list);
		return mv;
	}

	private Set<Long> getBrandIds(GoodsBrandCategory gbc) {
		Set<Long> ids = new HashSet<Long>();
		for (GoodsBrand gb : gbc.getBrands()) {
			ids.add(gb.getId());
		}
		return ids;
	}

	/**
	 * 精品团购列表
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/group_goods_list.htm")
	public ModelAndView group_goods_list(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/group_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).setAttribute("lakecloud_view_type", "weixin");
		Map params = new HashMap();
		params.put("gg_status", 1);// 团购商品状态
		params.put("store_weixin_status", 1);// 店铺开通微信状态
		params.put("group_status", 0);// 团购商品对应团购活动状态
		params.put("beginTime", new Date());
		params.put("endTime", new Date());
		List<GroupGoods> goods_list = this.groupgoodsService
				.query("select obj from GroupGoods obj where obj.gg_goods.goods_store.weixin_status=:store_weixin_status and obj.gg_status=:gg_status and obj.group.status=:group_status and obj.group.beginTime<=:beginTime and obj.group.endTime>=:endTime",
						params, 0, 12);
		mv.addObject("objs", goods_list);
		return mv;
	}

	/**
	 * 精品团购动态加载
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/group_goods_data.htm")
	public ModelAndView group_goods_data(HttpServletRequest request,
			HttpServletResponse response, String begin_count) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/group_goods_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		int begin = 0;
		if (begin_count != null && !begin_count.equals("")) {
			begin = CommUtil.null2Int(begin_count);
		}
		Map params = new HashMap();
		params.put("gg_status", 1);// 团购商品状态
		params.put("store_weixin_status", 1);// 店铺开通微信状态
		params.put("group_status", 0);// 团购商品对应团购活动状态
		List<GroupGoods> goods_list = this.groupgoodsService
				.query("select obj from GroupGoods obj where obj.gg_goods.goods_store.weixin_status=:store_weixin_status and obj.gg_status=:gg_status and obj.group.status=:group_status",
						params, begin, 12);
		mv.addObject("objs", goods_list);
		return mv;
	}

	/**
	 * 活动商品列表
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/activity_goods_list.htm")
	public ModelAndView activity_goods_list(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/activity_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).setAttribute("lakecloud_view_type", "weixin");
		Map map = new HashMap();
		map.put("ac_status", 1);
		map.put("ac_begin_time", new Date());
		map.put("ac_end_time", new Date());
		List<Activity> activities = this.activityService
				.query("select obj from Activity obj where obj.ac_status=:ac_status and obj.ac_begin_time<=:ac_begin_time and obj.ac_end_time>=:ac_end_time",
						map, 0, 1);
		if (activities.size() > 0) {
			map.clear();
			map.put("ag_status", 1);
			map.put("act_id", activities.get(0).getId());
			map.put("store_weixin_status", 1);// 店铺开通微信状态
			List<ActivityGoods> act_goods = this.activityGoodsService
					.query("select obj from ActivityGoods obj where obj.ag_status=:ag_status and obj.act.id=:act_id and obj.ag_goods.goods_store.weixin_status=:store_weixin_status",
							map, 0, 12);
			mv.addObject("objs", act_goods);
		}
		return mv;
	}

	/**
	 * 活动商品列表动态加载
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/activity_goods_data.htm")
	public ModelAndView activity_goods_data(HttpServletRequest request,
			HttpServletResponse response, String begin_count) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/activity_goods_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		int begin = 0;
		if (begin_count != null && !begin_count.equals("")) {
			begin = CommUtil.null2Int(begin_count);
		}
		Map map = new HashMap();
		map.put("ac_status", 1);
		map.put("ac_begin_time", new Date());
		map.put("ac_end_time", new Date());
		List<Activity> activities = this.activityService
				.query("select obj from Activity obj where obj.ac_status=:ac_status and obj.ac_begin_time<=:ac_begin_time and obj.ac_end_time>=:ac_end_time",
						map, 0, 1);
		if (activities.size() > 0) {
			map.clear();
			map.put("ag_status", 1);
			map.put("act_id", activities.get(0).getId());
			map.put("store_weixin_status", 1);// 店铺开通微信状态
			List<ActivityGoods> act_goods = this.activityGoodsService
					.query("select obj from ActivityGoods obj where obj.ag_status=:ag_status and obj.act.id=:act_id and obj.ag_goods.goods_store.weixin_status=:store_weixin_status",
							map, begin, 12);
			mv.addObject("objs", act_goods);
		}
		return mv;
	}

	/**
	 * 天天特价商品列表
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/bargain_goods_list.htm")
	public ModelAndView bargain_goods_list(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/bargain_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).setAttribute("lakecloud_view_type", "weixin");
		Map map = new HashMap();
		map.put("bg_status", 1);
		map.put("bg_time",
				CommUtil.formatDate(CommUtil.formatShortDate(new Date())));// 特价时间
		map.put("store_weixin_status", 1);// 店铺开通微信状态
		List<BargainGoods> bar_goods = this.bargainGoodsService
				.query("select obj from BargainGoods obj where obj.bg_status=:bg_status and obj.bg_time=:bg_time and obj.bg_goods.goods_store.weixin_status=:store_weixin_status",
						map, 0, 12);
		mv.addObject("objs", bar_goods);
		return mv;
	}

	/**
	 * 天天特价商品列表动态加载
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/bargain_goods_data.htm")
	public ModelAndView bargain_goods_data(HttpServletRequest request,
			HttpServletResponse response, String begin_count) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/bargain_goods_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		int begin = 0;
		if (begin_count != null && !begin_count.equals("")) {
			begin = CommUtil.null2Int(begin_count);
		}
		Map map = new HashMap();
		map.put("bg_status", 1);
		map.put("bg_time",
				CommUtil.formatDate(CommUtil.formatShortDate(new Date())));// 特价时间
		map.put("store_weixin_status", 1);// 店铺开通微信状态
		List<BargainGoods> bar_goods = this.bargainGoodsService
				.query("select obj from BargainGoods obj where obj.bg_status=:bg_status and obj.bg_time=:bg_time and obj.bg_goods.goods_store.weixin_status=:store_weixin_status",
						map, begin, 12);
		mv.addObject("objs", bar_goods);
		return mv;
	}

	/**
	 * 组合销售商品列表
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/combin_goods_list.htm")
	public ModelAndView combin_goods_list(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/combin_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).setAttribute("lakecloud_view_type", "weixin");
		Map params = new HashMap();
		params.put("goods_status", 0);
		params.put("store_weixin_status", 1);
		params.put("combin_status", 2);
		params.put("combin_begin_time", new Date());
		params.put("combin_end_time", new Date());
		List<Goods> goods_list = this.goodsService
				.query("select obj from Goods obj where obj.goods_store.weixin_status=:store_weixin_status and obj.goods_status=:goods_status and obj.combin_status=:combin_status and obj.combin_begin_time<=:combin_begin_time and obj.combin_end_time>=:combin_end_time",
						params, 0, 12);
		mv.addObject("objs", goods_list);
		return mv;
	}

	/**
	 * 组合销售商品列表动态加载
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/combin_goods_data.htm")
	public ModelAndView combin_goods_data(HttpServletRequest request,
			HttpServletResponse response, String begin_count) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/combin_goods_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		int begin = 0;
		if (begin_count != null && !begin_count.equals("")) {
			begin = CommUtil.null2Int(begin_count);
		}
		Map params = new HashMap();
		params.put("goods_status", 0);
		params.put("store_weixin_status", 1);
		params.put("combin_status", 2);
		params.put("combin_begin_time", new Date());
		params.put("combin_end_time", new Date());
		List<Goods> goods_list = this.goodsService
				.query("select obj from Goods obj where obj.goods_store.weixin_status=:store_weixin_status and obj.goods_status=:goods_status and obj.combin_status=:combin_status and obj.combin_begin_time<=:combin_begin_time and obj.combin_end_time>=:combin_end_time",
						params, begin, 12);
		mv.addObject("objs", goods_list);
		return mv;
	}

	/**
	 * 买就送商品列表
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/delivery_goods_list.htm")
	public ModelAndView delivery_goods_list(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/delivery_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).setAttribute("lakecloud_view_type", "weixin");
		Map params = new HashMap();
		params.put("d_status", 1);
		params.put("store_weixin_status", 1);
		params.put("d_begin_time", new Date());
		params.put("d_end_time", new Date());
		List<DeliveryGoods> goods_list = this.deliverygoodsSerivce
				.query("select obj from DeliveryGoods obj where obj.d_goods.goods_store.weixin_status=:store_weixin_status and obj.d_status=:d_status and obj.d_begin_time<=:d_begin_time and obj.d_end_time>=:d_end_time",
						params, 0, 12);
		mv.addObject("objs", goods_list);
		return mv;
	}

	/**
	 * 买就送商品列表动态加载
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/delivery_goods_data.htm")
	public ModelAndView delivery_goods_data(HttpServletRequest request,
			HttpServletResponse response, String begin_count) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/delivery_goods_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		int begin = 0;
		if (begin_count != null && !begin_count.equals("")) {
			begin = CommUtil.null2Int(begin_count);
		}
		Map params = new HashMap();
		params.put("d_status", 0);
		params.put("weixin_status", 1);
		params.put("d_begin_time", new Date());
		params.put("d_end_time", new Date());
		List<DeliveryGoods> goods_list = this.deliverygoodsSerivce
				.query("select obj from DeliveryGoods obj where obj.d_goods.goods_store.weixin_status=:weixin_status and obj.d_status=:d_status and obj.d_begin_time<=:d_begin_time and obj.d_end_time>=:d_end_time",
						params, begin, 12);
		mv.addObject("objs", goods_list);
		return mv;
	}

}
