package com.lakecloud.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Article;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.SysConfig;
import com.lakecloud.foundation.service.IArticleService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.lucene.LuceneThread;
import com.lakecloud.lucene.LuceneVo;

/**
 * @info 全文检索处理器
 * @since V1.3
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Controller
public class LuceneManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IArticleService articleService;

	@SecurityMapping(title = "全文检索设置", value = "/admin/lucene.htm*", rtype = "admin", rname = "全文检索", rcode = "luence_manage", rgroup = "工具")
	@RequestMapping("/admin/lucene.htm")
	public ModelAndView lucene(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/lucene.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		String path = System.getProperty("user.dir") + File.separator
				+ "luence";
		File file = new File(path);
		if (!file.exists()) {
			CommUtil.createFolder(path);
		}
		mv.addObject("lucene_disk_size", CommUtil.fileSize(file));
		mv.addObject("lucene_disk_path", path);
		return mv;
	}

	@SecurityMapping(title = "全文检索关键字保存", value = "/admin/lucene_hot_save.htm*", rtype = "admin", rname = "全文检索", rcode = "luence_manage", rgroup = "工具")
	@RequestMapping("/admin/lucene_hot_save.htm")
	public void lucene_hot_save(HttpServletRequest request,
			HttpServletResponse response, String id, String hotSearch) {
		SysConfig obj = this.configService.getSysConfig();
		boolean ret = true;
		if (id.equals("")) {
			obj.setHotSearch(hotSearch);
			obj.setAddTime(new Date());
			ret = this.configService.save(obj);
		} else {
			obj.setHotSearch(hotSearch);
			ret = this.configService.update(obj);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "全文检索更新", value = "/admin/lucene_update.htm*", rtype = "admin", rname = "全文检索", rcode = "luence_manage", rgroup = "工具")
	@RequestMapping("/admin/lucene_update.htm")
	public void lucene_update(HttpServletRequest request,
			HttpServletResponse response, String id, String hotSearch) {
		Map params = new HashMap();
		params.put("goods_status", 0);
		List<Goods> goods_list = this.goodsService
				.query(
						"select obj from Goods obj where obj.goods_status=:goods_status",
						params, -1, -1);
		params.clear();
		params.put("store_status", 2);
		List<Store> store_list = this.storeService
				.query(
						"select obj from Store obj where obj.store_status=:store_status",
						params, -1, -1);
		params.clear();
		params.put("display", true);
		List<Article> article_list = this.articleService.query(
				"select obj from Article obj where obj.display=:display",
				params, -1, -1);
		String goods_lucene_path = System.getProperty("user.dir")
				+ File.separator + "luence" + File.separator + "goods";
		String store_lucene_path = System.getProperty("user.dir")
				+ File.separator + "luence" + File.separator + "store";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		file = new File(store_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(store_lucene_path);
		}
		List<LuceneVo> goods_vo_list = new ArrayList<LuceneVo>();
		for (Goods goods : goods_list) {
			LuceneVo vo = new LuceneVo();
			vo.setVo_id(goods.getId());
			vo.setVo_title(goods.getGoods_name());
			vo.setVo_content(goods.getGoods_details());
			vo.setVo_type("goods");
			vo.setVo_store_price(CommUtil.null2Double(goods.getStore_price()));
			vo.setVo_add_time(goods.getAddTime().getTime());
			vo.setVo_goods_salenum(goods.getGoods_salenum());
			goods_vo_list.add(vo);
		}
		List<LuceneVo> store_vo_list = new ArrayList<LuceneVo>();
		for (Store store : store_list) {
			LuceneVo vo = new LuceneVo();
			vo.setVo_id(store.getId());
			vo.setVo_title(store.getStore_name());
			vo.setVo_content(store.getStore_address()
					+ store.getStore_seo_description()
					+ store.getStore_seo_keywords() + store.getStore_info());
			store_vo_list.add(vo);

		}
		LuceneThread goods_thread = new LuceneThread(goods_lucene_path,
				goods_vo_list);
		LuceneThread store_thread = new LuceneThread(store_lucene_path,
				goods_vo_list);
		Date d1 = new Date();
		goods_thread.run();
		store_thread.run();
		Date d2 = new Date();
		String path = System.getProperty("user.dir") + File.separator
				+ "luence";
		Map map = new HashMap();
		map.put("run_time", d2.getTime() - d1.getTime());
		map.put("file_size", CommUtil.fileSize(new File(path)));
		map.put("update_time", CommUtil.formatLongDate(new Date()));
		SysConfig config = this.configService.getSysConfig();
		config.setLucene_update(new Date());
		this.configService.update(config);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
