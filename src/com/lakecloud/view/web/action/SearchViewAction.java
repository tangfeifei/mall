package com.lakecloud.view.web.action;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.domain.virtual.SysMap;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Area;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsClass;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.StoreClass;
import com.lakecloud.foundation.domain.StoreGrade;
import com.lakecloud.foundation.domain.query.GoodsQueryObject;
import com.lakecloud.foundation.domain.query.StoreQueryObject;
import com.lakecloud.foundation.service.IAreaService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IStoreClassService;
import com.lakecloud.foundation.service.IStoreGradeService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.lucene.LuceneResult;
import com.lakecloud.lucene.LuceneUtil;
import com.lakecloud.lucene.LuceneVo;
import com.lakecloud.view.web.tools.StoreViewTools;

/**
 * @info 商品搜索控制器，商城搜索支持关键字全文搜索,店铺搜索使用数据库关键字搜索
 * @since v1.2
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Controller
public class SearchViewAction {
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
	private StoreViewTools storeViewTools;
	@Autowired
	private IStoreGradeService storeGradeService;
	@Autowired
	private IAreaService areaService;

	@RequestMapping("/search.htm")
	public ModelAndView search(HttpServletRequest request,
			HttpServletResponse response, String type, String keyword,
			String currentPage, String orderBy, String orderType,
			String store_price_begin, String store_price_end, String view_type,
			String sc_id, String storeGrade_id, String checkbox_id,
			String storepoint, String area_id, String area_name,
			String goods_view) {
		ModelAndView mv = new JModelAndView("search_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (type == null || type.equals(""))
			type = "goods";
		keyword = CommUtil.decode(keyword);
		if (type.equals("store")) {
			mv = new JModelAndView("store_list.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			StoreQueryObject sqo = new StoreQueryObject(currentPage, mv,
					"addTime", "desc");
			if (keyword != null && !keyword.equals("")) {
				sqo.addQuery("obj.store_name", new SysMap("store_name", "%"
						+ keyword + "%"), "like");
				mv.addObject("store_name", keyword);
			}
			if (sc_id != null && !sc_id.equals("")) {
				StoreClass storeclass = this.storeClassService
						.getObjById(CommUtil.null2Long(sc_id));
				Set<Long> ids = this.getStoreClassChildIds(storeclass);
				Map map = new HashMap();
				map.put("ids", ids);
				sqo.addQuery("obj.sc.id in (:ids)", map);
				mv.addObject("sc_id", sc_id);
			}
			if (storeGrade_id != null && !storeGrade_id.equals("")) {
				sqo.addQuery(
						"obj.grade.id",
						new SysMap("grade_id", CommUtil
								.null2Long(storeGrade_id)), "=");
				mv.addObject("storeGrade_id", storeGrade_id);
			}
			if (orderBy != null && !orderBy.equals("")) {
				sqo.setOrderBy(orderBy);
				if (orderBy.equals("addTime")) {
					orderType = "asc";
				} else {
					orderType = "desc";
				}
				sqo.setOrderType(orderType);
				mv.addObject("orderBy", orderBy);
				mv.addObject("orderType", orderType);
			}
			if (checkbox_id != null && !checkbox_id.equals("")) {
				sqo.addQuery("obj." + checkbox_id, new SysMap(
						"obj_checkbox_id", true), "=");
				mv.addObject("checkbox_id", checkbox_id);
			}
			if (storepoint != null && !storepoint.equals("")) {
				sqo.addQuery("obj.sp.store_evaluate1", new SysMap(
						"sp_store_evaluate1", new BigDecimal(storepoint)), ">=");
				mv.addObject("storepoint", storepoint);
			}
			if (area_id != null && !area_id.equals("")) {
				mv.addObject("area_id", area_id);
				Area area = this.areaService.getObjById(CommUtil
						.null2Long(area_id));
				Set<Long> area_ids = this.getAreaChildIds(area);
				Map params = new HashMap();
				params.put("ids", area_ids);
				sqo.addQuery("obj.area.id in (:ids)", params);
			}
			if (area_name != null && !area_name.equals("")) {
				mv.addObject("area_name", area_name);
				sqo.addQuery("obj.area.areaName", new SysMap("areaName", "%"
						+ area_name.trim() + "%"), "like");
				sqo.addQuery("obj.area.parent.areaName", new SysMap("areaName",
						"%" + area_name.trim() + "%"), "like", "or");
				sqo.addQuery("obj.area.parent.parent.areaName", new SysMap(
						"areaName", "%" + area_name.trim() + "%"), "like", "or");
			}
			sqo.addQuery("obj.store_status", new SysMap("store_status", 2), "=");
			sqo.setPageSize(20);
			IPageList pList = this.storeService.list(sqo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			List<StoreClass> scs = this.storeClassService
					.query("select obj from StoreClass obj where obj.parent.id is null order by obj.sequence asc",
							null, -1, -1);
			// 查询常用地区
			Map map = new HashMap();
			map.put("common", true);
			List<Area> areas = this.areaService
					.query("select obj from Area obj where obj.common =:common order by sequence asc",
							map, -1, -1);
			mv.addObject("areas", areas);
			mv.addObject("storeViewTools", storeViewTools);
			mv.addObject("scs", scs);
			List<StoreGrade> storeGrades = this.storeGradeService.query(
					"select obj from StoreGrade obj order by sequence asc",
					null, -1, -1);
			mv.addObject("storeGrades", storeGrades);
		}
		if (type.equals("goods") && !CommUtil.null2String(keyword).equals("")) {
			String path = System.getProperty("user.dir") + File.separator
					+ "luence" + File.separator + "goods";
			LuceneUtil lucene = LuceneUtil.instance();
			lucene.setIndex_path(path);
			boolean order_type = true;
			String order_by = "";
			if (CommUtil.null2String(orderType).equals("asc")) {
				order_type = false;
			}
			if (CommUtil.null2String(orderType).equals("")) {
				orderType = "desc";
			}
			if (CommUtil.null2String(orderBy).equals("store_price")) {
				order_by = "store_price";
			}
			if (CommUtil.null2String(orderBy).equals("goods_salenum")) {
				order_by = "goods_salenum";
			}
			if (CommUtil.null2String(orderBy).equals("goods_collect")) {
				order_by = "goods_collect";
			}
			if (CommUtil.null2String(orderBy).equals("goods_addTime")) {
				order_by = "addTime";
			}
			Sort sort = null;
			if (!CommUtil.null2String(order_by).equals("")) {
				sort = new Sort(new SortField(order_by, SortField.DOUBLE,
						order_type));// 排序false升序,true降序
			}
			LuceneResult pList = lucene.search(keyword,
					CommUtil.null2Int(currentPage),
					CommUtil.null2Int(store_price_begin),
					CommUtil.null2Int(store_price_end), null, sort);
			for (LuceneVo vo : pList.getVo_list()) {
				Goods goods = this.goodsService.getObjById(vo.getVo_id());
				pList.getGoods_list().add(goods);
			}
			CommUtil.saveLucene2ModelAndView("goods", pList, mv);
			GoodsClass gc = new GoodsClass();
			gc.setClassName("商品搜索结果");
			mv.addObject("gc", gc);
			mv.addObject("store_price_end", store_price_end);
			mv.addObject("store_price_begin", store_price_begin);
			mv.addObject("keyword", keyword);
			mv.addObject("orderBy", orderBy);
			mv.addObject("orderType", orderType);
			if (CommUtil.null2String(goods_view).equals("list")) {
				goods_view = "list";
			} else {
				goods_view = "thumb";
			}
			// 当天直通车商品，按照直通车价格排序
			if (this.configService.getSysConfig().isZtc_status()) {
				Map ztc_map = new HashMap();
				ztc_map.put("ztc_status", 3);
				ztc_map.put("now_date", new Date());
				ztc_map.put("ztc_gold", 0);
				List<Goods> ztc_goods = this.goodsService
						.query("select obj from Goods obj where obj.ztc_status =:ztc_status "
								+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold order by obj.ztc_dredge_price desc",
								ztc_map, 0, 5);
				mv.addObject("ztc_goods", ztc_goods);
			}
			mv.addObject("goods_view", goods_view);
		}
		if (CommUtil.null2String(view_type).equals("")) {
			view_type = "list";
		}
		mv.addObject("view_type", view_type);
		mv.addObject("type", type);
		return mv;
	}

	private Set<Long> getStoreClassChildIds(StoreClass sc) {
		Set<Long> ids = new HashSet<Long>();
		ids.add(sc.getId());
		for (StoreClass storeclass : sc.getChilds()) {
			Set<Long> cids = getStoreClassChildIds(storeclass);
			for (Long cid : cids) {
				ids.add(cid);
			}
		}
		return ids;
	}

	private Set<Long> getAreaChildIds(Area area) {
		Set<Long> ids = new HashSet<Long>();
		ids.add(area.getId());
		for (Area are : area.getChilds()) {
			Set<Long> cids = getAreaChildIds(are);
			for (Long cid : cids) {
				ids.add(cid);
			}
		}
		return ids;
	}

}
