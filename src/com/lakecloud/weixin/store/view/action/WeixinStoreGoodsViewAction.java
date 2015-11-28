package com.lakecloud.weixin.store.view.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.UserGoodsClass;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserGoodsClassService;
import com.lakecloud.view.web.tools.GoodsViewTools;
import com.lakecloud.view.web.tools.StoreViewTools;

/**
 * @info 微信店铺客户端商品管理控制器，用来查看商品列表页、详细页
 * @since V1.3
 * @version 1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang hz
 * 
 */
@Controller
public class WeixinStoreGoodsViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IUserGoodsClassService userGoodsClassService;
	@Autowired
	private GoodsViewTools goodsViewTools;

	@RequestMapping("/weixin/goods.htm")
	public ModelAndView weixin_goods(HttpServletRequest request,
			HttpServletResponse response, String goods_id) {
		ModelAndView mv = new JModelAndView("weixin/goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
		Store store = obj.getGoods_store();
		request.getSession(false).setAttribute("lakecloud_view_type", "weixin");
		request.getSession(false).setAttribute("store_id", store.getId());
		if (store.getWeixin_status() == 1) {
			request.getSession(false).setAttribute("store_id", store.getId());
			Map params = new HashMap();
			params.put("store_id", store.getId());
			params.put("goods_status", 0);
			params.put("goods_recommend", true);
			params.put("goods_id", CommUtil.null2Long(goods_id));
			List<Goods> recommend_goods_list = this.goodsService
					.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_recommend=:goods_recommend and obj.id!=:goods_id order by obj.addTime desc",
							params, 0, 4);
			mv.addObject("obj", obj);
			mv.addObject("store", obj.getGoods_store());
			mv.addObject("storeViewTools", storeViewTools);
			mv.addObject("recommend_goods_list", recommend_goods_list);
			mv.addObject("goodsViewTools", goodsViewTools);
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "未开通微信商城");
		}
		return mv;
	}

	@RequestMapping("/weixin/goods_recommend_list.htm")
	public ModelAndView weixin_goods_recommend_list(HttpServletRequest request,
			HttpServletResponse response, String queryType, String store_id) {
		ModelAndView mv = new JModelAndView("weixin/goods_recommend_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = null;
		if (store_id != null && !store_id.equals("")) {
			request.getSession(false).setAttribute("store_id", store_id);
			store = this.storeService.getObjById(CommUtil.null2Long(store_id));
		} else {
			store = this.storeService.getObjById(CommUtil.null2Long(request
					.getSession(false).getAttribute("store_id")));
		}
		if (store != null) {
			if (store.getWeixin_status() == 1) {
				Map params = new HashMap();
				params.put("store_id", store.getId());
				params.put("goods_status", 0);
				params.put("goods_recommend", true);
				List<Goods> recommend_goods_list = null;
				if (queryType != null && !queryType.equals("")) {
					mv.addObject("queryType", queryType);
					if (queryType.equals("goods_collect")) {
						recommend_goods_list = this.goodsService
								.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_recommend=:goods_recommend order by obj.goods_collect desc",
										params, 0, 6);
					}
					if (queryType.equals("goods_salenum")) {
						recommend_goods_list = this.goodsService
								.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_recommend=:goods_recommend order by obj.goods_salenum desc",
										params, 0, 6);
					}
					if (queryType.equals("store_price")) {
						recommend_goods_list = this.goodsService
								.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_recommend=:goods_recommend order by obj.store_price asc",
										params, 0, 6);
					}
					if (queryType.equals("store_credit")) {
						recommend_goods_list = this.goodsService
								.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_recommend=:goods_recommend order by obj.goods_store.store_credit desc",
										params, 0, 6);
					}
				} else {
					recommend_goods_list = this.goodsService
							.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_recommend=:goods_recommend order by obj.addTime desc",
									params, 0, 6);
				}
				mv.addObject("objs", recommend_goods_list);
			} else {
				mv = new JModelAndView("weixin/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "未开通微信商城");
			}
		}
		return mv;
	}

	@RequestMapping("/weixin/goods_recommend_list_ajax.htm")
	public ModelAndView weixin_goods_recommend_list_ajax(
			HttpServletRequest request, HttpServletResponse response,
			String begin_count, String queryType) {
		ModelAndView mv = new JModelAndView("weixin/goods_list_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Long store_id = CommUtil.null2Long(request.getSession(false)
				.getAttribute("store_id"));
		Map params = new HashMap();
		params.put("store_id", store_id);
		params.put("goods_status", 0);
		params.put("goods_recommend", true);
		List<Goods> recommend_goods_list = null;
		if (queryType != null && !queryType.equals("")) {
			mv.addObject("queryType", queryType);
			if (queryType.equals("goods_collect")) {
				recommend_goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_recommend=:goods_recommend order by obj.goods_collect desc",
								params, CommUtil.null2Int(begin_count), 6);
			}
			if (queryType.equals("goods_salenum")) {
				recommend_goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_recommend=:goods_recommend order by obj.goods_salenum desc",
								params, CommUtil.null2Int(begin_count), 6);
			}
			if (queryType.equals("store_price")) {
				recommend_goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_recommend=:goods_recommend order by obj.store_price asc",
								params, CommUtil.null2Int(begin_count), 6);
			}
			if (queryType.equals("store_credit")) {
				recommend_goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_recommend=:goods_recommend order by obj.goods_store.store_credit desc",
								params, CommUtil.null2Int(begin_count), 6);
			}
		} else {
			recommend_goods_list = this.goodsService
					.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_recommend=:goods_recommend order by obj.addTime desc",
							params, CommUtil.null2Int(begin_count), 6);
		}
		mv.addObject("objs", recommend_goods_list);
		return mv;
	}

	@RequestMapping("/weixin/goods_list.htm")
	public ModelAndView weixin_goods_list(HttpServletRequest request,
			HttpServletResponse response, String queryType, String store_id) {
		ModelAndView mv = new JModelAndView("weixin/goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = null;
		if (store_id != null && !store_id.equals("")) {
			request.getSession(false).setAttribute("store_id", store_id);
			store = this.storeService.getObjById(CommUtil.null2Long(store_id));
		} else {
			store = this.storeService.getObjById(CommUtil.null2Long(request
					.getSession(false).getAttribute("store_id")));
		}
		if (store != null) {
			if (store.getWeixin_status() == 1) {
				if (queryType != null && !queryType.equals("")) {
					mv.addObject("queryType", queryType);
				}
				Map params = new HashMap();
				params.put("store_id", store.getId());
				params.put("goods_status", 0);
				List<Goods> goods_list = null;
				if (queryType != null && !queryType.equals("")) {
					if (queryType.equals("goods_collect")) {
						goods_list = this.goodsService
								.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_collect desc",
										params, 0, 6);
					}
					if (queryType.equals("goods_salenum")) {
						goods_list = this.goodsService
								.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_salenum desc",
										params, 0, 6);
					}
					if (queryType.equals("store_price")) {
						goods_list = this.goodsService
								.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.store_price asc",
										params, 0, 6);
					}
					if (queryType.equals("store_credit")) {
						goods_list = this.goodsService
								.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_store.store_credit desc",
										params, 0, 6);
					}
					if (queryType.equals("addTime")) {
						goods_list = this.goodsService
								.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.addTime desc",
										params, 0, 6);
					}
				}
				mv.addObject("objs", goods_list);
			} else {
				mv = new JModelAndView("weixin/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "未开通微信商城");
			}
		}
		return mv;
	}

	@RequestMapping("/weixin/goods_list_ajax.htm")
	public ModelAndView weixin_goods_list_ajax(HttpServletRequest request,
			HttpServletResponse response, String begin_count, String queryType) {
		ModelAndView mv = new JModelAndView("weixin/goods_list_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Long store_id = CommUtil.null2Long(request.getSession(false)
				.getAttribute("store_id"));
		Map params = new HashMap();
		params.put("store_id", store_id);
		params.put("goods_status", 0);
		List<Goods> goods_list = null;
		if (queryType != null && !queryType.equals("")) {
			mv.addObject("queryType", queryType);
			if (queryType.equals("goods_collect")) {
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_collect desc",
								params, CommUtil.null2Int(begin_count), 6);
			}
			if (queryType.equals("goods_salenum")) {
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_salenum desc",
								params, CommUtil.null2Int(begin_count), 6);
			}
			if (queryType.equals("store_price")) {
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.store_price asc",
								params, CommUtil.null2Int(begin_count), 6);
			}
			if (queryType.equals("store_credit")) {
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_store.store_credit desc",
								params, CommUtil.null2Int(begin_count), 6);
			}
			if (queryType.equals("addTime")) {
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.addTime desc",
								params, CommUtil.null2Int(begin_count), 6);
			}
		}
		mv.addObject("objs", goods_list);
		return mv;
	}

	// 店铺商品分类一级
	@RequestMapping("/weixin/classes_first_list.htm")
	public ModelAndView weixin_classes_first_list(HttpServletRequest request,
			HttpServletResponse response, String store_id) {
		ModelAndView mv = new JModelAndView("weixin/classes_first_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = null;
		if (store_id != null && !store_id.equals("")) {
			request.getSession(false).setAttribute("store_id", store_id);
			store = this.storeService.getObjById(CommUtil.null2Long(store_id));
		} else {
			store = this.storeService.getObjById(CommUtil.null2Long(request
					.getSession(false).getAttribute("store_id")));
		}
		if (store != null) {
			if (store.getWeixin_status() == 1) {
				Map map = new HashMap();
				map.put("uid", store.getUser().getId());
				List<UserGoodsClass> usergoodsClasses = this.userGoodsClassService
						.query("select obj from UserGoodsClass obj where obj.user.id=:uid and obj.parent is null",
								map, -1, -1);
				mv.addObject("usergoodsClasses", usergoodsClasses);
			} else {
				mv = new JModelAndView("weixin/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "未开通微信商城");
			}
		}
		return mv;
	}

	// 店铺商品分类二级
	@RequestMapping("/weixin/classes_second_list.htm")
	public ModelAndView weixin_classes_second_list(HttpServletRequest request,
			HttpServletResponse response, String class_id) {
		ModelAndView mv = new JModelAndView("weixin/classes_second_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		UserGoodsClass parent = this.userGoodsClassService.getObjById(CommUtil
				.null2Long(class_id));
		if (parent.getUser().getStore().getWeixin_status() == 1) {
			request.getSession(false).setAttribute("store_id",
					parent.getUser().getStore().getId());
			Map map = new HashMap();
			map.put("uid", parent.getUser().getId());
			map.put("parent_id", parent.getId());
			List<UserGoodsClass> usergoodsClasses = this.userGoodsClassService
					.query("select obj from UserGoodsClass obj where obj.user.id=:uid and obj.parent.id=:parent_id",
							map, -1, -1);
			mv.addObject("usergoodsClasses", usergoodsClasses);
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "未开通微信商城");
		}
		return mv;
	}

	// 分类商品列表
	@RequestMapping("/weixin/classes_goods_list.htm")
	public ModelAndView weixin_classes_goods_list(HttpServletRequest request,
			HttpServletResponse response, String queryType, String ugc_id) {
		ModelAndView mv = new JModelAndView("weixin/classes_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		UserGoodsClass parent = this.userGoodsClassService.getObjById(CommUtil
				.null2Long(ugc_id));
		if (parent.getUser().getStore().getWeixin_status() == 1) {
			request.getSession(false).setAttribute("store_id",
					parent.getUser().getStore().getId());
			Map params = new HashMap();
			params.put("store_id", parent.getUser().getStore().getId());
			params.put("goods_status", 0);
			params.put("ugc", parent);
			List<Goods> goods_list = null;
			if (queryType != null && !queryType.equals("")) {
				mv.addObject("queryType", queryType);
				if (queryType.equals("goods_collect")) {
					goods_list = this.goodsService
							.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and :ugc member of obj.goods_ugcs order by obj.goods_collect desc",
									params, 0, 6);
				}
				if (queryType.equals("goods_salenum")) {
					goods_list = this.goodsService
							.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and :ugc member of obj.goods_ugcs order by obj.goods_salenum desc",
									params, 0, 6);
				}
				if (queryType.equals("store_price")) {
					goods_list = this.goodsService
							.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and :ugc member of obj.goods_ugcs order by obj.store_price asc",
									params, 0, 6);
				}
				if (queryType.equals("store_credit")) {
					goods_list = this.goodsService
							.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and :ugc member of obj.goods_ugcs order by obj.goods_store.store_credit desc",
									params, 0, 6);
				}
			} else {
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and :ugc member of obj.goods_ugcs order by obj.addTime desc",
								params, 0, 6);
			}
			mv.addObject("ugc_id", ugc_id);
			mv.addObject("objs", goods_list);
		} else {
			mv = new JModelAndView("weixin/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "未开通微信商城");
		}
		return mv;
	}

	@RequestMapping("/weixin/classes_goods_ajax.htm")
	public ModelAndView weixin_classes_goods_ajax(HttpServletRequest request,
			HttpServletResponse response, String begin_count, String queryType,
			String ugc_id) {
		ModelAndView mv = new JModelAndView("weixin/classes_goods_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Long store_id = CommUtil.null2Long(request.getSession(false)
				.getAttribute("store_id"));
		UserGoodsClass ugc = this.userGoodsClassService.getObjById(CommUtil
				.null2Long(ugc_id));
		Map params = new HashMap();
		params.put("store_id", store_id);
		params.put("goods_status", 0);
		params.put("ugc", ugc);
		List<Goods> goods_list = null;
		if (queryType != null && !queryType.equals("")) {
			mv.addObject("queryType", queryType);
			if (queryType.equals("goods_collect")) {
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and :ugc member of obj.goods_ugcs order by obj.goods_collect desc",
								params, CommUtil.null2Int(begin_count), 6);
			}
			if (queryType.equals("goods_salenum")) {
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and :ugc member of obj.goods_ugcs order by obj.goods_salenum desc",
								params, CommUtil.null2Int(begin_count), 6);
			}
			if (queryType.equals("store_price")) {
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and :ugc member of obj.goods_ugcs order by obj.store_price asc",
								params, CommUtil.null2Int(begin_count), 6);
			}
			if (queryType.equals("store_credit")) {
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and :ugc member of obj.goods_ugcs order by obj.goods_store.store_credit desc",
								params, CommUtil.null2Int(begin_count), 6);
			}
		} else {
			goods_list = this.goodsService
					.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and :ugc member of obj.goods_ugcs order by obj.addTime desc",
							params, CommUtil.null2Int(begin_count), 6);
		}
		mv.addObject("objs", goods_list);
		return mv;
	}

	@RequestMapping("/weixin/search_goods_list.htm")
	public ModelAndView weixin_search_goods_list(HttpServletRequest request,
			HttpServletResponse response, String queryType, String keyword) {
		ModelAndView mv = new JModelAndView("weixin/search_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (queryType != null && !queryType.equals("")) {
			mv.addObject("queryType", queryType);
		}
		Long store_id = CommUtil.null2Long(request.getSession(false)
				.getAttribute("store_id"));
		Map params = new HashMap();
		params.put("store_id", store_id);
		params.put("goods_status", 0);
		params.put("goods_name", "%" + keyword.trim() + "%");
		List<Goods> goods_list = null;
		if (keyword != null && !keyword.equals("")) {
			if (queryType != null && !queryType.equals("")) {
				if (queryType.equals("goods_collect")) {
					goods_list = this.goodsService
							.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_name like:goods_name order by obj.goods_collect desc",
									params, 0, 6);
				}
				if (queryType.equals("goods_salenum")) {
					goods_list = this.goodsService
							.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_name like:goods_name order by obj.goods_salenum desc",
									params, 0, 6);
				}
				if (queryType.equals("store_price")) {
					goods_list = this.goodsService
							.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_name like:goods_name order by obj.store_price asc",
									params, 0, 6);
				}
				if (queryType.equals("store_credit")) {
					goods_list = this.goodsService
							.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_name like:goods_name order by obj.goods_store.store_credit desc",
									params, 0, 6);
				}
			} else {
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_name like:goods_name order by obj.addTime desc",
								params, 0, 6);
			}
		}
		mv.addObject("keyword", keyword);
		mv.addObject("objs", goods_list);
		return mv;
	}

	@RequestMapping("/weixin/search_goods_ajax.htm")
	public ModelAndView weixin_search_goods_ajax(HttpServletRequest request,
			HttpServletResponse response, String begin_count, String queryType,
			String keyword) {
		ModelAndView mv = new JModelAndView("weixin/search_goods_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Long store_id = CommUtil.null2Long(request.getSession(false)
				.getAttribute("store_id"));
		Map params = new HashMap();
		params.put("store_id", store_id);
		params.put("goods_status", 0);
		params.put("goods_name", "%" + keyword.trim() + "%");
		List<Goods> goods_list = null;
		if (queryType != null && !queryType.equals("")) {
			mv.addObject("queryType", queryType);
			if (queryType.equals("goods_collect")) {
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_name like:goods_name  order by obj.goods_collect desc",
								params, CommUtil.null2Int(begin_count), 6);
			}
			if (queryType.equals("goods_salenum")) {
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_name like:goods_name order by obj.goods_salenum desc",
								params, CommUtil.null2Int(begin_count), 6);
			}
			if (queryType.equals("store_price")) {
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_name like:goods_name  order by obj.store_price asc",
								params, CommUtil.null2Int(begin_count), 6);
			}
			if (queryType.equals("store_credit")) {
				goods_list = this.goodsService
						.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_name like:goods_name  order by obj.goods_store.store_credit desc",
								params, CommUtil.null2Int(begin_count), 6);
			}
		} else {
			goods_list = this.goodsService
					.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status and obj.goods_name like:goods_name order by obj.addTime desc",
							params, CommUtil.null2Int(begin_count), 6);
		}
		mv.addObject("objs", goods_list);
		return mv;
	}
}
