package com.lakecloud.view.web.action;

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

import com.lakecloud.core.domain.virtual.SysMap;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Area;
import com.lakecloud.foundation.domain.Navigation;
import com.lakecloud.foundation.domain.SpareGoods;
import com.lakecloud.foundation.domain.SpareGoodsClass;
import com.lakecloud.foundation.domain.SpareGoodsFloor;
import com.lakecloud.foundation.domain.query.SpareGoodsQueryObject;
import com.lakecloud.foundation.service.IAreaService;
import com.lakecloud.foundation.service.INavigationService;
import com.lakecloud.foundation.service.ISpareGoodsClassService;
import com.lakecloud.foundation.service.ISpareGoodsFloorService;
import com.lakecloud.foundation.service.ISpareGoodsService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.view.web.tools.SpareGoodsViewTools;

/**
 * 
 * @author hezeng 2013-09-26
 * @info 闲置商品控制器,用来查看前台闲置商品首页、列表以及详细页
 * 
 */
@Controller
public class SpareGoodsViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private ISpareGoodsClassService sparegoodsclassService;
	@Autowired
	private ISpareGoodsFloorService sparegoodsfloorService;
	@Autowired
	private ISpareGoodsService sparegoodsService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private SpareGoodsViewTools SpareGoodsTools;
	@Autowired
	private INavigationService navService;

	/**
	 * 闲置商品head页面，包含系统logo及全文搜索，使用自定义标签httpInclude.include("/sparegoods_head.htm")完成页面读取
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/sparegoods_head.htm")
	public ModelAndView sparegoods_head(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("sparegoods_head.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		return mv;
	}

	/**
	 * 卖家发布页及列表详细页菜单导航
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@RequestMapping("/sparegoods_nav.htm")
	public ModelAndView sparegoods_nav(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("sparegoods_nav.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		List<SpareGoodsClass> sgcs = this.sparegoodsclassService
				.query(
						"select obj from SpareGoodsClass obj where obj.parent.id is null order by sequence asc",
						null, 0, 6);
		mv.addObject("sgcs", sgcs);
		if (SecurityUserHolder.getCurrentUser() != null
				&& !SecurityUserHolder.getCurrentUser().equals("")) {
			// 出售中
			Map params = new HashMap();
			params.put("status", 0);
			params.put("down", 0);
			params.put("uid", SecurityUserHolder.getCurrentUser().getId());
			List<SpareGoods> sps = this.sparegoodsService
					.query(
							"select obj from SpareGoods obj where obj.status=:status and obj.down=:down and obj.user.id=:uid",
							params, -1, -1);
			// 违规
			params.clear();
			params.put("status", -1);
			params.put("uid", SecurityUserHolder.getCurrentUser().getId());
			List<SpareGoods> drops = this.sparegoodsService
					.query(
							"select obj from SpareGoods obj where obj.status=:status and obj.user.id=:uid",
							params, -1, -1);
			// 下架
			params.clear();
			params.put("down", -1);
			params.put("uid", SecurityUserHolder.getCurrentUser().getId());
			List<SpareGoods> down = this.sparegoodsService
					.query(
							"select obj from SpareGoods obj where obj.down=:down and obj.user.id=:uid",
							params, -1, -1);
			mv.addObject("selling", sps.size());
			mv.addObject("drops", drops.size());
			mv.addObject("down", down.size());
		}
		Map map = new HashMap();
		map.put("type", "sparegoods");
		map.put("display", true);
		List<Navigation> navs = this.navService
				.query(
						"select obj from Navigation obj where obj.type=:type and obj.display=:display order by sequence asc",
						map, -1, -1);
		mv.addObject("navs", navs);
		return mv;
	}

	/**
	 * 闲置商品首页菜单导航
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@RequestMapping("/sparegoods_nav2.htm")
	public ModelAndView sparegoods_nav2(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("sparegoods_nav2.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		List<SpareGoodsClass> sgcs = this.sparegoodsclassService
				.query(
						"select obj from SpareGoodsClass obj where obj.parent.id is null order by sequence asc",
						null, 0, 6);
		mv.addObject("sgcs", sgcs);
		if (SecurityUserHolder.getCurrentUser() != null
				&& !SecurityUserHolder.getCurrentUser().equals("")) {
			// 出售中
			Map params = new HashMap();
			params.put("status", 0);
			params.put("down", 0);
			params.put("uid", SecurityUserHolder.getCurrentUser().getId());
			List<SpareGoods> sps = this.sparegoodsService
					.query(
							"select obj from SpareGoods obj where obj.status=:status and obj.down=:down and obj.user.id=:uid",
							params, -1, -1);
			// 违规
			params.clear();
			params.put("status", -1);
			params.put("uid", SecurityUserHolder.getCurrentUser().getId());
			List<SpareGoods> drops = this.sparegoodsService
					.query(
							"select obj from SpareGoods obj where obj.status=:status and obj.user.id=:uid",
							params, -1, -1);
			// 下架
			params.clear();
			params.put("down", -1);
			params.put("uid", SecurityUserHolder.getCurrentUser().getId());
			List<SpareGoods> down = this.sparegoodsService
					.query(
							"select obj from SpareGoods obj where obj.down=:down and obj.user.id=:uid",
							params, -1, -1);
			mv.addObject("selling", sps.size());
			mv.addObject("drops", drops.size());
			mv.addObject("down", down.size());
		}
		Map map = new HashMap();
		map.put("type", "sparegoods");
		map.put("display", true);
		List<Navigation> navs = this.navService
				.query(
						"select obj from Navigation obj where obj.type=:type and obj.display=:display order by sequence asc",
						map, -1, -1);
		mv.addObject("navs", navs);
		return mv;
	}

	@RequestMapping("/sparegoods.htm")
	public ModelAndView sparegoods(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("sparegoods.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		Map map = new HashMap();
		map.put("display", true);
		List<SpareGoodsFloor> floors = this.sparegoodsfloorService
				.query(
						"select obj from SpareGoodsFloor obj where obj.display=:display order By sequence asc",
						map, -1, -1);
		List<SpareGoodsClass> sgcs = this.sparegoodsclassService
				.query(
						"select obj from SpareGoodsClass obj where obj.parent.id is null order by sequence asc",
						null, -1, -1);
		mv.addObject("sgcs", sgcs);
		mv.addObject("floors", floors);
		mv.addObject("SpareGoodsTools", SpareGoodsTools);
		return mv;
	}

	@RequestMapping("/sparegoods_detail.htm")
	public ModelAndView sparegoods_detail(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("sparegoods_detail.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		SpareGoods obj = this.sparegoodsService.getObjById(CommUtil
				.null2Long(id));
		if (obj.getStatus() == 0) {
			mv.addObject("obj", obj);
		}
		if (obj.getStatus() == -1) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("url", CommUtil.getURL(request) + "/sparegoods.htm");
			mv.addObject("op_title", "该商品已下架!");
		}
		if (obj.getDown() == -1) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("url", CommUtil.getURL(request) + "/sparegoods.htm");
			mv.addObject("op_title", "该商品因违规已下架!");
		}

		return mv;
	}

	@RequestMapping("/sparegoods_search.htm")
	public ModelAndView sparegoods_search(HttpServletRequest request,
			HttpServletResponse response, String cid, String orderBy,
			String orderType, String currentPage, String price_begin,
			String price_end, String keyword, String area_id) {
		ModelAndView mv = new JModelAndView("sparegoods_search.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 1, request, response);
		if (orderType != null && !orderType.equals("")) {
			if (orderType.equals("asc")) {
				orderType = "desc";
			} else {
				orderType = "asc";
			}
		} else {
			orderType = "desc";
		}
		if (orderBy != null && !orderBy.equals("")) {
			if (orderBy.equals("addTime")) {
				orderType = "desc";
			}
		} else {
			orderBy = "addTime";
		}
		SpareGoodsQueryObject qo = new SpareGoodsQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.addQuery("obj.status", new SysMap("status", 0), "=");
		qo.addQuery("obj.down", new SysMap("down", 0), "=");
		if (cid != null && !cid.equals("")) {
			SpareGoodsClass sgc = this.sparegoodsclassService
					.getObjById(CommUtil.null2Long(cid));
			Set<Long> ids = this.genericIds(sgc);
			Map map = new HashMap();
			map.put("ids", ids);
			qo.addQuery("obj.spareGoodsClass.id in (:ids)", map);
			mv.addObject("cid", cid);
			mv.addObject("sgc", sgc);
		}
		if (orderBy != null && !orderBy.equals("")) {
			if (orderBy.equals("recommend")) {
				qo.addQuery("obj.recommend", new SysMap("obj_recommend", true),
						"=");
			}
			if (price_begin != null && !price_begin.equals("")) {
				qo.addQuery("obj.goods_price", new SysMap("goods_price",
						CommUtil.null2Int(price_begin)), ">=");
			}
			if (price_end != null && !price_end.equals("")) {
				qo.addQuery("obj.goods_price", new SysMap("goods_end", CommUtil
						.null2Int(price_end)), "<=");
			}
		}
		if (keyword != null && !keyword.equals("")) {
			qo.addQuery("obj.title", new SysMap("obj_title", "%"
					+ keyword.trim() + "%"), "like");
		}
		if (area_id != null && !area_id.equals("")) {
			qo.addQuery("obj.area.parent.id", new SysMap("obj_area_id",
					CommUtil.null2Long(area_id)), "=");
			Area area = this.areaService
					.getObjById(CommUtil.null2Long(area_id));
			mv.addObject("area", area);
		}
		IPageList pList = this.sparegoodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		List<Area> citys = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		mv.addObject("citys", citys);
		mv.addObject("area_id", area_id);
		mv.addObject("keyword", keyword);
		mv.addObject("price_begin", price_begin);
		mv.addObject("price_end", price_end);
		mv.addObject("allCount", pList.getRowCount());
		mv.addObject("SpareGoodsTools", SpareGoodsTools);
		return mv;
	}

	private Set<Long> genericIds(SpareGoodsClass gc) {
		Set<Long> ids = new HashSet<Long>();
		ids.add(gc.getId());
		for (SpareGoodsClass child : gc.getChilds()) {
			Set<Long> cids = genericIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}

}
