package com.lakecloud.view.web.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.domain.virtual.SysMap;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.StoreClass;
import com.lakecloud.foundation.domain.StoreNavigation;
import com.lakecloud.foundation.domain.StorePartner;
import com.lakecloud.foundation.domain.UserGoodsClass;
import com.lakecloud.foundation.domain.query.EvaluateQueryObject;
import com.lakecloud.foundation.domain.query.GoodsQueryObject;
import com.lakecloud.foundation.domain.query.StoreQueryObject;
import com.lakecloud.foundation.service.IEvaluateService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IStoreClassService;
import com.lakecloud.foundation.service.IStoreNavigationService;
import com.lakecloud.foundation.service.IStorePartnerService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserGoodsClassService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.view.web.tools.AreaViewTools;
import com.lakecloud.view.web.tools.GoodsViewTools;
import com.lakecloud.view.web.tools.StoreViewTools;

/**
 * 
* <p>Title: StoreViewAction.java</p>

* <p>Description:前台店铺控制器，前端店铺所有信息均在该控制器进行操作显示 </p>

* <p>Copyright: Copyright (c) 2011-2014</p>

* <p>Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net</p>

* @author erikzhang

* @date 2014-5-9

* @version LakeCloud_C2C 1.4
 */
@Controller
public class StoreViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IStoreClassService storeClassService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IUserGoodsClassService userGoodsClassService;
	@Autowired
	private IStoreNavigationService storenavigationService;
	@Autowired
	private IStorePartnerService storepartnerService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IUserService userService;
	@Autowired
	private AreaViewTools areaViewTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private StoreViewTools storeViewTools;

	@RequestMapping("/store.htm")
	public ModelAndView store(HttpServletRequest request,
			HttpServletResponse response, String id) {
		String serverName = request.getServerName().toLowerCase();
		Store store = null;
		if (id == null && serverName.indexOf(".") >= 0
				&& serverName.indexOf(".") != serverName.lastIndexOf(".")
				&& this.configService.getSysConfig().isSecond_domain_open()) {
			String secondDomain = serverName.substring(0,
					serverName.indexOf("."));
			store = this.storeService.getObjByProperty("store_second_domain",
					secondDomain);
		} else {
			store = this.storeService.getObjById(CommUtil.null2Long(id));
		}
		if (store == null) {
			ModelAndView mv = new JModelAndView("error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "不存在该店铺信息");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			return mv;
		} else {
			String template = "default";
			if (store.getTemplate() != null && !store.getTemplate().equals("")) {
				template = store.getTemplate();
			}
			ModelAndView mv = new JModelAndView(template + "/store_index.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			if (store.getStore_status() == 2) {
				this.add_store_common_info(mv, store);
				mv.addObject("store", store);
				mv.addObject("nav_id", "store_index");
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "店铺已经关闭或者未开通店铺");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			}
			this.generic_evaluate(store, mv);
			return mv;
		}
	}

	@RequestMapping("/store_left.htm")
	public ModelAndView store_left(HttpServletRequest request,
			HttpServletResponse response) {
		Store store = this.storeService.getObjById(CommUtil.null2Long(request
				.getAttribute("id")));
		String template = "default";
		if (store != null && store.getTemplate() != null
				&& !store.getTemplate().equals("")) {
			template = store.getTemplate();
		}
		ModelAndView mv = new JModelAndView(template + "/store_left.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("store", store);
		this.add_store_common_info(mv, store);
		this.generic_evaluate(store, mv);
		Map params = new HashMap();
		params.put("store_id", store.getId());
		List<StorePartner> partners = this.storepartnerService
				.query("select obj from StorePartner obj where obj.store.id=:store_id order by obj.sequence asc",
						params, -1, -1);
		mv.addObject("partners", partners);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		return mv;
	}

	@RequestMapping("/store_left1.htm")
	public ModelAndView store_left1(HttpServletRequest request,
			HttpServletResponse response) {
		Store store = this.storeService.getObjById(CommUtil.null2Long(request
				.getAttribute("id")));
		String template = "default";
		if (store != null && store.getTemplate() != null
				&& !store.getTemplate().equals("")) {
			template = store.getTemplate();
		}
		ModelAndView mv = new JModelAndView(template + "/store_left1.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("store", store);
		this.add_store_common_info(mv, store);
		Map params = new HashMap();
		params.put("store_id", store.getId());
		List<StorePartner> partners = this.storepartnerService
				.query("select obj from StorePartner obj where obj.store.id=:store_id order by obj.sequence asc",
						params, -1, -1);
		mv.addObject("partners", partners);
		return mv;
	}

	@RequestMapping("/store_left2.htm")
	public ModelAndView store_left2(HttpServletRequest request,
			HttpServletResponse response) {
		Store store = this.storeService.getObjById(CommUtil.null2Long(request
				.getAttribute("id")));
		String template = "default";
		if (store != null && store.getTemplate() != null
				&& !store.getTemplate().equals("")) {
			template = store.getTemplate();
		}
		ModelAndView mv = new JModelAndView(template + "/store_left2.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("store", store);
		this.add_store_common_info(mv, store);
		return mv;
	}

	@RequestMapping("/store_nav.htm")
	public ModelAndView store_nav(HttpServletRequest request,
			HttpServletResponse response) {
		Long id = CommUtil.null2Long(request.getAttribute("id"));
		Store store = this.storeService.getObjById(id);
		String template = "default";
		if (store.getTemplate() != null && !store.getTemplate().equals("")) {
			template = store.getTemplate();
		}
		ModelAndView mv = new JModelAndView(template + "/store_nav.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (store.getStore_status() == 2) {
			Map params = new HashMap();
			params.put("store_id", store.getId());
			params.put("display", true);
			List<StoreNavigation> navs = this.storenavigationService
					.query("select obj from StoreNavigation obj where obj.store.id=:store_id and obj.display=:display order by obj.sequence asc",
							params, -1, -1);
			mv.addObject("navs", navs);
			mv.addObject("store", store);
			String goods_view = CommUtil.null2String(request
					.getAttribute("goods_view"));
			mv.addObject("goods_view", CommUtil.null2Boolean(goods_view));
			mv.addObject("goods_id",
					CommUtil.null2String(request.getAttribute("goods_id")));
			mv.addObject("goods_list",
					CommUtil.null2Boolean(request.getAttribute("goods_list")));
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "店铺信息错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	@RequestMapping("/store_credit.htm")
	public ModelAndView store_credit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		Store store = this.storeService.getObjById(CommUtil.null2Long(id));
		String template = "default";
		if (store.getTemplate() != null && !store.getTemplate().equals("")) {
			template = store.getTemplate();
		}
		ModelAndView mv = new JModelAndView(template + "/store_credit.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (store.getStore_status() == 2) {
			EvaluateQueryObject qo = new EvaluateQueryObject("1", mv,
					"addTime", "desc");
			qo.addQuery("obj.of.store.id",
					new SysMap("store_id", store.getId()), "=");
			IPageList pList = this.evaluateService.list(qo);
			CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
					+ "/store_eva.htm", "", "", pList, mv);
			mv.addObject("store", store);
			mv.addObject("nav_id", "store_credit");
			mv.addObject("storeViewTools", storeViewTools);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "店铺信息错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	@RequestMapping("/store_eva.htm")
	public ModelAndView store_eva(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String eva_val) {
		Store store = this.storeService.getObjById(CommUtil.null2Long(id));
		String template = "default";
		if (store.getTemplate() != null && !store.getTemplate().equals("")) {
			template = store.getTemplate();
		}
		ModelAndView mv = new JModelAndView(template + "/store_eva.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (store.getStore_status() == 2) {
			EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv,
					"addTime", "desc");
			qo.addQuery("obj.evaluate_goods.goods_store.id", new SysMap(
					"store_id", store.getId()), "=");
			if (!CommUtil.null2String(eva_val).equals("")) {
				qo.addQuery("obj.evaluate_buyer_val", new SysMap(
						"evaluate_buyer_val", CommUtil.null2Int(eva_val)), "=");
			}
			IPageList pList = this.evaluateService.list(qo);
			CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
					+ "/store_eva.htm", "",
					"&eva_val=" + CommUtil.null2String(eva_val), pList, mv);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "店铺信息错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	@RequestMapping("/store_info.htm")
	public ModelAndView store_info(HttpServletRequest request,
			HttpServletResponse response, String id) {
		Store store = this.storeService.getObjById(Long.parseLong(id));
		String template = "default";
		if (store.getTemplate() != null && !store.getTemplate().equals("")) {
			template = store.getTemplate();
		}
		ModelAndView mv = new JModelAndView(template + "/store_info.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (store.getStore_status() == 2) {
			mv.addObject("store", store);
			mv.addObject("nav_id", "store_info");
			mv.addObject("areaViewTools", areaViewTools);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "店铺信息错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	@RequestMapping("/store_url.htm")
	public ModelAndView store_url(HttpServletRequest request,
			HttpServletResponse response, String id) {
		StoreNavigation nav = this.storenavigationService.getObjById(CommUtil
				.null2Long(id));
		String template = "default";
		if (nav.getStore().getTemplate() != null
				&& !nav.getStore().getTemplate().equals("")) {
			template = nav.getStore().getTemplate();
		}
		ModelAndView mv = new JModelAndView(template + "/store_url.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("store", nav.getStore());
		mv.addObject("nav", nav);
		mv.addObject("nav_id", nav.getId());
		return mv;
	}

	private void add_store_common_info(ModelAndView mv, Store store) {
		Map params = new HashMap();
		params.put("user_id", store.getUser().getId());
		params.put("display", true);
		List<UserGoodsClass> ugcs = this.userGoodsClassService
				.query("select obj from UserGoodsClass obj where obj.user.id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc",
						params, -1, -1);
		mv.addObject("ugcs", ugcs);
		params.clear();
		params.put("recommend", true);
		params.put("goods_store_id", store.getId());
		params.put("goods_status", 0);
		List<Goods> goods_recommend = this.goodsService
				.query("select obj from Goods obj where obj.goods_recommend=:recommend and obj.goods_store.id=:goods_store_id and obj.goods_status=:goods_status order by obj.addTime desc",
						params, 0, 8);
		params.clear();
		params.put("goods_store_id", store.getId());
		params.put("goods_status", 0);
		List<Goods> goods_new = this.goodsService
				.query("select obj from Goods obj where obj.goods_store.id=:goods_store_id and obj.goods_status=:goods_status order by obj.addTime desc ",
						params, 0, 12);
		mv.addObject("goods_recommend", goods_recommend);
		mv.addObject("goods_new", goods_new);
		mv.addObject("goodsViewTools", goodsViewTools);
		mv.addObject("storeViewTools", storeViewTools);
		mv.addObject("areaViewTools", areaViewTools);
	}

	@RequestMapping("/store_list.htm")
	public ModelAndView store_list(HttpServletRequest request,
			HttpServletResponse response, String id, String sc_id,
			String currentPage, String orderType, String store_name,
			String store_ower, String type) {
		ModelAndView mv = new JModelAndView("store_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<StoreClass> scs = this.storeClassService
				.query("select obj from StoreClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		mv.addObject("scs", scs);
		StoreQueryObject sqo = new StoreQueryObject(currentPage, mv,
				"store_credit", orderType);
		if (sc_id != null && !sc_id.equals("")) {
			sqo.addQuery("obj.sc.id",
					new SysMap("sc_id", CommUtil.null2Long(sc_id)), "=");
		}
		if (store_name != null && !store_name.equals("")) {
			sqo.addQuery("obj.store_name", new SysMap("store_name", "%"
					+ store_name + "%"), "like");
			mv.addObject("store_name", store_name);
		}
		if (store_ower != null && !store_ower.equals("")) {
			sqo.addQuery("obj.store_ower", new SysMap("store_ower", "%"
					+ store_ower + "%"), "like");
			mv.addObject("store_ower", store_ower);
		}
		sqo.addQuery("obj.store_status", new SysMap("store_status", 2), "=");
		IPageList pList = this.storeService.list(sqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("storeViewTools", storeViewTools);
		mv.addObject("type", type);
		return mv;
	}

	@RequestMapping("/store_goods_search.htm")
	public ModelAndView store_goods_search(HttpServletRequest request,
			HttpServletResponse response, String keyword, String store_id,
			String currentPage) {
		Store store = this.storeService.getObjById(Long.parseLong(store_id));
		String template = "default";
		if (store.getTemplate() != null && !store.getTemplate().equals("")) {
			template = store.getTemplate();
		}
		ModelAndView mv = new JModelAndView(template
				+ "/store_goods_search.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, null, null);
		gqo.addQuery("obj.goods_store.id",
				new SysMap("store_id", CommUtil.null2Long(store_id)), "=");
		gqo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + keyword
				+ "%"), "like");
		gqo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");
		gqo.setPageSize(20);
		IPageList pList = this.goodsService.list(gqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("keyword", keyword);
		mv.addObject("store", store);
		return mv;
	}

	/**
	 * 店铺头部文件
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/store_head.htm")
	public ModelAndView store_head(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("store_head.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Long store_id=CommUtil.null2Long(request.getAttribute("store_id"));
		Store store = this.storeService.getObjById(store_id);
		this.generic_evaluate(store, mv);
		mv.addObject("store", store);
		mv.addObject("storeViewTools", storeViewTools);
		return mv;
	}

	private void generic_evaluate(Store store, ModelAndView mv) {
		double description_result = 0;
		double service_result = 0;
		double ship_result = 0;
		if (store != null && store.getSc() != null && store.getPoint() != null) {
			StoreClass sc = this.storeClassService.getObjById(store.getSc()
					.getId());
			float description_evaluate = CommUtil.null2Float(sc
					.getDescription_evaluate());
			float service_evaluate = CommUtil.null2Float(sc
					.getService_evaluate());
			float ship_evaluate = CommUtil.null2Float(sc.getShip_evaluate());
			float store_description_evaluate = CommUtil.null2Float(store
					.getPoint().getDescription_evaluate());
			float store_service_evaluate = CommUtil.null2Float(store.getPoint()
					.getService_evaluate());
			float store_ship_evaluate = CommUtil.null2Float(store.getPoint()
					.getShip_evaluate());
			// 计算和同行比较结果
			description_result = CommUtil.div(store_description_evaluate
					- description_evaluate, description_evaluate);
			service_result = CommUtil.div(store_service_evaluate
					- service_evaluate, service_evaluate);
			ship_result = CommUtil.div(store_ship_evaluate - ship_evaluate,
					ship_evaluate);
		}
		if (description_result > 0) {
			mv.addObject("description_css", "better");
			mv.addObject("description_type", "高于");
			mv.addObject(
					"description_result",
					CommUtil.null2String(CommUtil.mul(description_result, 100) > 100 ? 100
							: CommUtil.mul(description_result, 100))
							+ "%");
		}
		if (description_result == 0) {
			mv.addObject("description_css", "better");
			mv.addObject("description_type", "持平");
			mv.addObject("description_result", "-----");
		}
		if (description_result < 0) {
			mv.addObject("description_css", "lower");
			mv.addObject("description_type", "低于");
			mv.addObject(
					"description_result",
					CommUtil.null2String(CommUtil.mul(-description_result, 100))
							+ "%");
		}
		if (service_result > 0) {
			mv.addObject("service_css", "better");
			mv.addObject("service_type", "高于");
			mv.addObject("service_result",
					CommUtil.null2String(CommUtil.mul(service_result, 100))
							+ "%");
		}
		if (service_result == 0) {
			mv.addObject("service_css", "better");
			mv.addObject("service_type", "持平");
			mv.addObject("service_result", "-----");
		}
		if (service_result < 0) {
			mv.addObject("service_css", "lower");
			mv.addObject("service_type", "低于");
			mv.addObject("service_result",
					CommUtil.null2String(CommUtil.mul(-service_result, 100))
							+ "%");
		}
		if (ship_result > 0) {
			mv.addObject("ship_css", "better");
			mv.addObject("ship_type", "高于");
			mv.addObject("ship_result",
					CommUtil.null2String(CommUtil.mul(ship_result, 100)) + "%");
		}
		if (ship_result == 0) {
			mv.addObject("ship_css", "better");
			mv.addObject("ship_type", "持平");
			mv.addObject("ship_result", "-----");
		}
		if (ship_result < 0) {
			mv.addObject("ship_css", "lower");
			mv.addObject("ship_type", "低于");
			mv.addObject("ship_result",
					CommUtil.null2String(CommUtil.mul(-ship_result, 100)) + "%");
		}
	}
}
