package com.lakecloud.manage.admin.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.domain.virtual.SysMap;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.WebForm;
import com.lakecloud.foundation.domain.Accessory;
import com.lakecloud.foundation.domain.Activity;
import com.lakecloud.foundation.domain.ActivityGoods;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsBrand;
import com.lakecloud.foundation.domain.GoodsClass;
import com.lakecloud.foundation.domain.Group;
import com.lakecloud.foundation.domain.GroupGoods;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.SysConfig;
import com.lakecloud.foundation.domain.query.GoodsBrandQueryObject;
import com.lakecloud.foundation.domain.query.GoodsQueryObject;
import com.lakecloud.foundation.domain.query.GroupGoodsQueryObject;
import com.lakecloud.foundation.domain.query.StoreQueryObject;
import com.lakecloud.foundation.service.IAccessoryService;
import com.lakecloud.foundation.service.IActivityGoodsService;
import com.lakecloud.foundation.service.IActivityService;
import com.lakecloud.foundation.service.IGoodsBrandService;
import com.lakecloud.foundation.service.IGoodsClassService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IGroupGoodsService;
import com.lakecloud.foundation.service.IGroupService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.lucene.LuceneUtil;
import com.lakecloud.lucene.LuceneVo;
import com.lakecloud.weixin.domain.VMenu;
import com.lakecloud.weixin.service.IVMenuService;
import com.lakecloud.weixin.tools.WeixinTools;

/**
 * @info 微信平台管理控制器
 * @since V1.3
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Controller
public class WeixinManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IVMenuService vMenuService;
	@Autowired
	private WeixinTools weixinStoreTools;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private IActivityService activityService;
	@Autowired
	private IGoodsClassService goodsclassService;
	@Autowired
	private IGroupService groupService;
	@Autowired
	private IGroupGoodsService groupgoodsService;
	@Autowired
	private IVMenuService vmenuService;
	@Autowired
	private IUserService userService;

	@SecurityMapping(title = "微信店铺设置", value = "/admin/set_weixin.htm*", rtype = "admin", rname = "微信店铺", rcode = "weixin_admin", rgroup = "运营")
	@RequestMapping("/admin/set_weixin.htm")
	public ModelAndView set_weixin(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/set_weixin.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "微信店铺设置保存", value = "/admin/set_weixin_save.htm*", rtype = "admin", rname = "微信店铺", rcode = "weixin_admin", rgroup = "运营")
	@RequestMapping("/admin/set_weixin_save.htm")
	public ModelAndView set_weixin_save(HttpServletRequest request,
			HttpServletResponse response, String id, String weixin_store,
			String weixin_amount) {
		SysConfig obj = this.configService.getSysConfig();
		obj.setWeixin_amount(CommUtil.null2Int(weixin_amount));
		obj.setWeixin_store(CommUtil.null2Boolean(weixin_store));
		if (id.equals("")) {
			this.configService.save(obj);
		} else {
			this.configService.update(obj);
		}
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		mv.addObject("op_title", "微信商城设置成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/set_weixin.htm");
		return mv;
	}

	@SecurityMapping(title = "微信店铺", value = "/admin/weixin_store.htm*", rtype = "admin", rname = "微信店铺", rcode = "combin_admin", rgroup = "运营")
	@RequestMapping("/admin/weixin_store.htm")
	public ModelAndView weixin_store(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String store_name) {
		ModelAndView mv = new JModelAndView("admin/blue/weixin_store.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		StoreQueryObject qo = new StoreQueryObject(currentPage, mv, orderBy,
				orderType);
		qo.addQuery("obj.weixin_status", new SysMap("weixin_status", 0), ">");
		if (!CommUtil.null2String(store_name).equals("")) {
			qo.addQuery("obj.store_name", new SysMap("store_name", "%"
					+ CommUtil.null2String(store_name) + "%"), "like");
		}
		IPageList pList = this.storeService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("store_name", store_name);
		return mv;
	}

	@SecurityMapping(title = "微信店铺违规关闭", value = "/admin/weixin_store_close.htm*", rtype = "admin", rname = "微信店铺", rcode = "combin_admin", rgroup = "运营")
	@RequestMapping("/admin/weixin_store_close.htm")
	public String weixin_store_close(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String mulitId) {
		for (String id : mulitId.split(",")) {
			if (!id.equals("")) {
				Store store = this.storeService.getObjById(CommUtil
						.null2Long(id));
				store.setWeixin_status(3);// 违规关闭
				this.storeService.update(store);
			}
		}
		return "redirect:weixin_store.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "微信店铺开通", value = "/admin/weixin_store_open.htm*", rtype = "admin", rname = "微信店铺", rcode = "combin_admin", rgroup = "运营")
	@RequestMapping("/admin/weixin_store_open.htm")
	public String weixin_store_open(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String mulitId) {
		for (String id : mulitId.split(",")) {
			if (!id.equals("")) {
				Store store = this.storeService.getObjById(CommUtil
						.null2Long(id));
				store.setWeixin_status(1);// 开通微信商城
				this.storeService.update(store);
			}
		}
		return "redirect:weixin_store.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "商品列表", value = "/admin/weixin_goods_list.htm*", rtype = "admin", rname = "微信商品", rcode = "admin_weixin_goods", rgroup = "运营")
	@RequestMapping("/admin/weixin_goods_list.htm")
	public ModelAndView weixin_goods_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/weixin_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy,
				orderType);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, Goods.class, mv);
		qo.addQuery("obj.goods_status", new SysMap("goods_status", -2), ">");
		qo.addQuery("obj.goods_store.weixin_status", new SysMap(
				"weixin_status", 1), "=");
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/goods_list.htm", "",
				params, pList, mv);
		List<GoodsBrand> gbs = this.goodsBrandService.query(
				"select obj from GoodsBrand obj order by obj.sequence asc",
				null, -1, -1);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		mv.addObject("gcs", gcs);
		mv.addObject("gbs", gbs);
		return mv;
	}

	@SecurityMapping(title = "微信商品AJAX更新", value = "/admin/weixin_goods_ajax.htm*", rtype = "admin", rname = "微信商品", rcode = "admin_weixin_goods", rgroup = "运营")
	@RequestMapping("/admin/weixin_goods_ajax.htm")
	public void weixin_goods_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		Goods obj = this.goodsService.getObjById(Long.parseLong(id));
		Field[] fields = Goods.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(obj);
		Object val = null;
		for (Field field : fields) {
			if (field.getName().equals(fieldName)) {
				Class clz = Class.forName("java.lang.String");
				if (field.getType().getName().equals("int")) {
					clz = Class.forName("java.lang.Integer");
				}
				if (field.getType().getName().equals("boolean")) {
					clz = Class.forName("java.lang.Boolean");
				}
				if (!value.equals("")) {
					val = BeanUtils.convertType(value, clz);
				} else {
					val = !CommUtil.null2Boolean(wrapper
							.getPropertyValue(fieldName));
				}
				wrapper.setPropertyValue(fieldName, val);
			}
		}
		if (fieldName.equals("weixin_shop_recommend")) {
			if (obj.isWeixin_shop_recommend()) {
				obj.setWeixin_shop_recommendTime(new Date());
			} else
				obj.setWeixin_shop_recommendTime(null);
		}
		if (fieldName.equals("weixin_shop_hot")) {
			if (obj.isWeixin_shop_hot()) {
				obj.setWeixin_shop_hotTime(new Date());
			} else
				obj.setWeixin_shop_hotTime(null);
		}
		this.goodsService.update(obj);
		if (obj.getGoods_status() == 0) {
			// 更新lucene索引
			String goods_lucene_path = System.getProperty("user.dir")
					+ File.separator + "luence" + File.separator + "goods";
			File file = new File(goods_lucene_path);
			if (!file.exists()) {
				CommUtil.createFolder(goods_lucene_path);
			}
			LuceneVo vo = new LuceneVo();
			vo.setVo_id(obj.getId());
			vo.setVo_title(obj.getGoods_name());
			vo.setVo_content(obj.getGoods_details());
			vo.setVo_type("goods");
			vo.setVo_store_price(CommUtil.null2Double(obj.getStore_price()));
			vo.setVo_add_time(obj.getAddTime().getTime());
			vo.setVo_goods_salenum(obj.getGoods_salenum());
			LuceneUtil lucene = LuceneUtil.instance();
			lucene.setIndex_path(goods_lucene_path);
			lucene.update(CommUtil.null2String(obj.getId()), vo);
		} else {
			String goods_lucene_path = System.getProperty("user.dir")
					+ File.separator + "luence" + File.separator + "goods";
			File file = new File(goods_lucene_path);
			if (!file.exists()) {
				CommUtil.createFolder(goods_lucene_path);
			}
			LuceneUtil lucene = LuceneUtil.instance();
			lucene.setIndex_path(goods_lucene_path);
			lucene.delete_index(CommUtil.null2String(id));
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SecurityMapping(title = "团购商品列表", value = "/admin/weixin_groupgoods_list.htm*", rtype = "admin", rname = "微信商品", rcode = "admin_weixin_goods", rgroup = "运营")
	@RequestMapping("/admin/weixin_groupgoods_list.htm")
	public ModelAndView weixin_groupgoods_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String gg_name, String goods_name,
			String weixin_shop_recommend) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/weixin_groupgoods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("beginTime", new Date());
		params.put("endTime", new Date());
		List<Group> groups = this.groupService
				.query("select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime",
						params, -1, -1);
		if (groups.size() > 0) {
			GroupGoodsQueryObject qo = new GroupGoodsQueryObject(currentPage,
					mv, orderBy, orderType);
			WebForm wf = new WebForm();
			wf.toQueryPo(request, qo, GroupGoods.class, mv);
			qo.addQuery("obj.group.id", new SysMap("group_id", groups.get(0)
					.getId()), "=");
			qo.addQuery("obj.gg_status", new SysMap("obj_gg_status", 1), "=");
			qo.addQuery("obj.gg_goods.goods_store.weixin_status", new SysMap(
					"weixin_status", 1), "=");
			if (gg_name != null && !gg_name.equals("")) {
				qo.addQuery("obj.gg_name", new SysMap("obj_gg_name", "%"
						+ CommUtil.null2String(gg_name) + "%"), "like");
				mv.addObject("gg_name", gg_name);
			}
			if (goods_name != null && !goods_name.equals("")) {
				qo.addQuery(
						"obj.gg_goods.goods_name",
						new SysMap("obj_goods_name", "%"
								+ CommUtil.null2String(goods_name) + "%"),
						"like");
				mv.addObject("goods_name", goods_name);
			}
			if (weixin_shop_recommend != null
					&& !weixin_shop_recommend.equals("")) {
				qo.addQuery(
						"obj.weixin_shop_recommend",
						new SysMap("obj_goods_name", CommUtil
								.null2Boolean(weixin_shop_recommend)), "=");
				mv.addObject("weixin_shop_recommend", weixin_shop_recommend);
			}
			IPageList pList = this.groupgoodsService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		}
		return mv;
	}

	@SecurityMapping(title = "微信团购商品AJAX更新", value = "/admin/weixin_groupgoods_ajax.htm*", rtype = "admin", rname = "微信商品", rcode = "admin_weixin_goods", rgroup = "运营")
	@RequestMapping("/admin/weixin_groupgoods_ajax.htm")
	public void weixin_groupgoods_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		GroupGoods obj = this.groupgoodsService.getObjById(Long.parseLong(id));
		Field[] fields = GroupGoods.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(obj);
		Object val = null;
		for (Field field : fields) {
			if (field.getName().equals(fieldName)) {
				Class clz = Class.forName("java.lang.String");
				if (field.getType().getName().equals("int")) {
					clz = Class.forName("java.lang.Integer");
				}
				if (field.getType().getName().equals("boolean")) {
					clz = Class.forName("java.lang.Boolean");
				}
				if (!value.equals("")) {
					val = BeanUtils.convertType(value, clz);
				} else {
					val = !CommUtil.null2Boolean(wrapper
							.getPropertyValue(fieldName));
				}
				wrapper.setPropertyValue(fieldName, val);
			}
		}
		if (fieldName.equals("weixin_shop_recommend")) {
			if (obj.isWeixin_shop_recommend()) {
				obj.setWeixin_shop_recommendTime(new Date());
			} else
				obj.setWeixin_shop_recommendTime(null);
		}
		this.groupgoodsService.update(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 微信品牌列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "微信商品品牌列表", value = "/admin/weixin_brand_list.htm*", rtype = "admin", rname = "微信品牌", rcode = "admin_weixin_brand", rgroup = "运营")
	@RequestMapping("/admin/weixin_brand_list.htm")
	public ModelAndView weixin_brand_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String name, String category) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/weixin_brand_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsBrandQueryObject qo = new GoodsBrandQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.addQuery("obj.audit", new SysMap("audit", 1), "=");
		qo.setOrderBy("sequence");
		qo.setOrderType("asc");
		if (!CommUtil.null2String(name).equals("")) {
			qo.addQuery("obj.name",
					new SysMap("name", "%" + name.trim() + "%"), "like");
		}
		if (!CommUtil.null2String(category).equals("")) {
			qo.addQuery("obj.category.name", new SysMap("category", "%"
					+ category.trim() + "%"), "like");
		}
		IPageList pList = this.goodsBrandService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("name", name);
		mv.addObject("category", category);
		return mv;
	}

	@SecurityMapping(title = "品牌微信商城推荐", value = "/admin/weixin_brand_switch.htm*", rtype = "admin", rname = "微信品牌", rcode = "admin_weixin_brand", rgroup = "运营")
	@RequestMapping("/admin/weixin_brand_switch.htm")
	public String weixin_brand_switch(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		GoodsBrand obj = this.goodsBrandService.getObjById(Long.parseLong(id));
		if (obj.isWeixin_shop_recommend()) {
			obj.setWeixin_shop_recommend(false);
			obj.setWeixin_shop_recommendTime(new Date());
		} else {
			obj.setWeixin_shop_recommend(true);
			obj.setWeixin_shop_recommendTime(new Date());
		}
		this.goodsBrandService.update(obj);
		return "redirect:/admin/weixin_brand_list.htm?currentPage="
				+ currentPage;
	}

	@SecurityMapping(title = "微商城配置", value = "/admin/weixin_plat_set.htm*", rtype = "admin", rname = "微商城配置", rcode = "weixin_plat_admin", rgroup = "运营")
	@RequestMapping("/admin/weixin_plat_set.htm")
	public ModelAndView weixin_plat_set(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/weixin_plat_set.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("weixin_develop_action", CommUtil.getURL(request)
				+ "/weixin_develop_action.htm");
		return mv;
	}

	@SecurityMapping(title = "微商城配置保存", value = "/admin/weixin_plat_set_save.htm*", rtype = "admin", rname = "微商城配置", rcode = "weixin_plat_admin", rgroup = "运营")
	@RequestMapping("/admin/weixin_plat_set_save.htm")
	public ModelAndView weixin_plat_set_save(HttpServletRequest request,
			HttpServletResponse response, String id, String weixin_account,
			String weixin_token, String weixin_appId, String weixin_appSecret,
			String weixin_welecome_content) {
		SysConfig obj = this.configService.getSysConfig();
		WebForm wf = new WebForm();
		SysConfig sysConfig = null;
		if (id.equals("")) {
			sysConfig = wf.toPo(request, SysConfig.class);
			sysConfig.setAddTime(new Date());
		} else {
			sysConfig = (SysConfig) wf.toPo(request, obj);
		}
		sysConfig.setWeixin_account(weixin_account);
		sysConfig.setWeixin_token(weixin_token);
		sysConfig.setWeixin_appId(weixin_appId);
		sysConfig.setWeixin_appSecret(weixin_appSecret);
		sysConfig.setWeixin_welecome_content(weixin_welecome_content);
		// 图片上传开始logo
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "system";
		Map map = new HashMap();
		try {
			String fileName = this.configService.getSysConfig()
					.getStore_weixin_logo() == null ? "" : this.configService
					.getSysConfig().getStore_weixin_logo().getName();
			map = CommUtil.saveFileToServer(request, "weixin_logo",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory store_weixin_logo = new Accessory();
					store_weixin_logo.setName(CommUtil.null2String(map
							.get("fileName")));
					store_weixin_logo.setExt((String) map.get("mime"));
					store_weixin_logo.setSize((Float) map.get("fileSize"));
					store_weixin_logo.setPath(uploadFilePath + "/system");
					store_weixin_logo.setWidth((Integer) map.get("width"));
					store_weixin_logo.setHeight((Integer) map.get("height"));
					store_weixin_logo.setAddTime(new Date());
					this.accessoryService.save(store_weixin_logo);
					sysConfig.setStore_weixin_logo(store_weixin_logo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory store_weixin_logo = sysConfig
							.getStore_weixin_logo();
					store_weixin_logo.setName(CommUtil.null2String(map
							.get("fileName")));
					store_weixin_logo.setExt(CommUtil.null2String(map
							.get("mime")));
					store_weixin_logo.setSize(CommUtil.null2Float(map
							.get("fileSize")));
					store_weixin_logo.setPath(uploadFilePath + "/system");
					store_weixin_logo.setWidth(CommUtil.null2Int(map
							.get("width")));
					store_weixin_logo.setHeight(CommUtil.null2Int(map
							.get("height")));
					this.accessoryService.update(store_weixin_logo);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 微商城二维码
		map.clear();
		try {
			map = CommUtil.saveFileToServer(request, "qr_img",
					saveFilePathName, null, null);
			String fileName = sysConfig.getWeixin_qr_img() != null ? sysConfig
					.getWeixin_qr_img().getName() : "";
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory qr_img = new Accessory();
					qr_img.setName(CommUtil.null2String(map.get("fileName")));
					qr_img.setExt(CommUtil.null2String(map.get("mime")));
					qr_img.setSize(CommUtil.null2Float(map.get("fileSize")));
					qr_img.setPath(uploadFilePath + "/system");
					qr_img.setWidth(CommUtil.null2Int(map.get("width")));
					qr_img.setHeight(CommUtil.null2Int(map.get("heigh")));
					qr_img.setAddTime(new Date());
					this.accessoryService.save(qr_img);
					sysConfig.setWeixin_qr_img(qr_img);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory qr_img = sysConfig.getWeixin_qr_img();
					qr_img.setName(CommUtil.null2String(map.get("fileName")));
					qr_img.setExt(CommUtil.null2String(map.get("mime")));
					qr_img.setSize(CommUtil.null2Float(map.get("fileSize")));
					qr_img.setPath(uploadFilePath + "/system");
					qr_img.setWidth(CommUtil.null2Int(map.get("width")));
					qr_img.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(qr_img);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "微商城配置成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/weixin_plat_set.htm");
		return mv;
	}

	@RequestMapping("/weixin_develop_action.htm")
	public void weixin_develop_action(HttpServletRequest request,
			HttpServletResponse response, String signature, String timestamp,
			String nonce, String echostr) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/xml");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(ServletInputStream) request.getInputStream(), "UTF-8"));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			Map<String, String> map = this.weixinStoreTools.parse_xml(sb
					.toString());
			String ToUserName = map.get("ToUserName");
			String FromUserName = map.get("FromUserName");
			String CreateTime = map.get("CreateTime");
			String MsgType = map.get("MsgType");
			String Content = CommUtil.null2String(map.get("Content"));
			String MsgId = map.get("MsgId");
			String Event = CommUtil.null2String(map.get("Event"));
			String EventKey = CommUtil.null2String(map.get("EventKey"));
			String reply_xml = "";
			String reply_title = "";
			String reply_content = "";
			String reply_bottom = "";
			String web_url = CommUtil.getURL(request);
			String reply_all = this.configService.getSysConfig()
					.getWeixin_welecome_content();
			int num = 1;
			if (Event.equals("")) {
				if (Content.equals("0")) {
					reply_all = "<a href='" + web_url
							+ "/weixin/platform/index.htm"
							+ "'>\ue022点击进入微信商城\ue022</a>";
				}
				if (Content.equals("1")) {
					reply_title = "商城推荐\ue022";
					reply_bottom = "\n<a href='"
							+ web_url
							+ "/weixin/platform/goods_list.htm?type=recommend'>点击查看更多\ue23c</a>";
					Map params = new HashMap();
					params.put("goods_status", 0);
					params.put("weixin_status", 1);
					params.put("weixin_shop_recommend", true);
					List<Goods> goods_list = this.goodsService
							.query("select obj from Goods obj where obj.goods_store.weixin_status=:weixin_status and obj.goods_status=:goods_status and obj.weixin_shop_recommend=:weixin_shop_recommend order by obj.weixin_shop_recommendTime asc",
									params, 0, 5);
					for (Goods goods : goods_list) {
						reply_content = reply_content + "<a href='" + web_url
								+ "/weixin/goods.htm?goods_id=" + goods.getId()
								+ "'>" + num + "、" + goods.getGoods_name()
								+ "</a>\n";
						num++;
					}
					reply_all = reply_title + "\n" + reply_content
							+ reply_bottom;
				}
				if (Content.equals("2")) {
					reply_title = "商城热卖\ue022";
					reply_bottom = "\n<a href='"
							+ web_url
							+ "/weixin/platform/goods_list.htm?type=hot'>点击查看更多\ue23c</a>";
					Map params = new HashMap();
					params.put("goods_status", 0);
					params.put("weixin_status", 1);
					List<Goods> goods_list = this.goodsService
							.query("select obj from Goods obj where obj.goods_store.weixin_status=:weixin_status and obj.goods_status=:goods_status order by obj.goods_salenum desc",
									params, 0, 5);
					for (Goods goods : goods_list) {
						reply_content = reply_content + "<a href='" + web_url
								+ "/weixin/goods.htm?goods_id=" + goods.getId()
								+ "'>" + num + "、" + goods.getGoods_name()
								+ "</a>\n";
						num++;
					}
					reply_all = reply_title + "\n" + reply_content
							+ reply_bottom;
				}
				if (Content.equals("3")) {
					reply_title = "商城促销\ue022";
					reply_bottom = "\n<a href='"
							+ web_url
							+ "/weixin/platform/activity_goods_list.htm'>点击查看更多\ue23c</a>";
					Map params = new HashMap();
					params.put("ac_status", 1);
					params.put("ac_begin_time", new Date());
					params.put("ac_end_time", new Date());
					List<Activity> activities = this.activityService
							.query("select obj from Activity obj where obj.ac_status=:ac_status and obj.ac_begin_time<=:ac_begin_time and obj.ac_end_time>=:ac_end_time",
									params, 0, 1);
					if (activities.size() > 0) {
						params.clear();
						params.put("ag_status", 1);
						params.put("act_id", activities.get(0).getId());
						params.put("store_weixin_status", 1);// 店铺开通微信状态
						List<ActivityGoods> act_goods = this.activityGoodsService
								.query("select obj from ActivityGoods obj where obj.ag_status=:ag_status and obj.act.id=:act_id and obj.ag_goods.goods_store.weixin_status=:store_weixin_status",
										params, 0, 12);
						for (ActivityGoods act_good : act_goods) {
							reply_content = reply_content + "<a href='"
									+ web_url + "/weixin/goods.htm?goods_id="
									+ act_good.getAg_goods().getId() + "'>"
									+ num + "、"
									+ act_good.getAg_goods().getGoods_name()
									+ "</a>\n";
							num++;
						}
						reply_all = reply_title + "\n" + reply_content
								+ reply_bottom;
					}
				}
				if (Content.equals("4")) {
					reply_title = "商城类目\ue022";
					reply_bottom = "\n<a href='"
							+ web_url
							+ "/weixin/platform/classes_first.htm'>点击查看更多\ue23c</a>";
					List<GoodsClass> gcs = this.goodsclassService
							.query("select obj from GoodsClass obj where obj.parent.id is null order by sequence asc ",
									null, 0, 10);
					for (GoodsClass gc : gcs) {
						reply_content = reply_content + "<a href='" + web_url
								+ "/weixin/platform/classes_second.htm?pgc_id="
								+ gc.getId() + "'>" + num + "、"
								+ gc.getClassName() + "</a>\n";
						num++;
					}
					reply_all = reply_title + "\n" + reply_content
							+ reply_bottom;
				}
			} else {
				if (Event.equals("subscribe")) {// 订阅，回复欢迎词
					reply_all = this.configService.getSysConfig()
							.getWeixin_welecome_content();
				}
				if (Event.equals("unsubscribe")) {// 取消订阅，不需要回复任何内容
				}
				if (Event.equals("click") || Event.equals("CLICK")) {// 返回菜单key对应的内容
					Map menu_map = new HashMap();
					menu_map.put("menu_key", EventKey);
					menu_map.put("menu_cat", "admin");
					List<VMenu> vMeuns = this.vMenuService
							.query("select obj from VMenu obj where obj.menu_cat=:menu_cat and obj.menu_key=:menu_key",
									menu_map, -1, -1);
					if (vMeuns.size() > 0) {
						reply_all = vMeuns.get(0).getMenu_key_content();
					}
				}
			}
			reply_xml = this.weixinStoreTools
					.reply_xml(MsgType, map, reply_all);
			PrintWriter writer;
			writer = response.getWriter();
			if (echostr != null && !echostr.equals("")) {
				writer.print(echostr);
			} else
				writer.print(reply_xml);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@SecurityMapping(title = "微商城菜单配置", value = "/admin/weixin_plat_menu.htm*", rtype = "admin", rname = "微商城配置", rcode = "weixin_plat_admin", rgroup = "运营")
	@RequestMapping("/admin/weixin_plat_menu.htm")
	public ModelAndView weixin_plat_menu(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/weixin_plat_menu.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("menu_cat", "admin");
		List<VMenu> weixin_menus = this.vMenuService
				.query("select obj from VMenu obj where obj.menu_cat=:menu_cat  and obj.parent.id is null order by obj.menu_sequence asc",
						params, -1, -1);
		mv.addObject("weixin_menus", weixin_menus);
		return mv;
	}

	@SecurityMapping(title = "微商城菜单添加", value = "/admin/weixin_menu_add.htm*", rtype = "admin", rname = "微商城配置", rcode = "weixin_plat_admin", rgroup = "运营")
	@RequestMapping("/admin/weixin_menu_add.htm")
	public ModelAndView weixin_menu_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String menu_id,
			String pmenu_id) {
		ModelAndView mv = new JModelAndView("admin/blue/weixin_menu_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		VMenu obj = this.vMenuService.getObjById(CommUtil.null2Long(menu_id));
		mv.addObject("obj", obj);
		mv.addObject("pmenu_id", obj == null ? pmenu_id
				: (obj.getParent() != null ? obj.getParent().getId() : ""));
		return mv;
	}

	@SecurityMapping(title = "微商城菜单保存", value = "/admin/weixin_menu_save.htm*", rtype = "admin", rname = "微商城配置", rcode = "weixin_plat_admin", rgroup = "运营")
	@RequestMapping("/admin/weixin_menu_save.htm")
	public String weixin_menu_save(HttpServletRequest request,
			HttpServletResponse response, String menu_id, String pmenu_id)
			throws IOException {
		WebForm wf = new WebForm();
		VMenu parent = this.vMenuService.getObjById(CommUtil
				.null2Long(pmenu_id));
		if (!CommUtil.null2String(menu_id).equals("")) {
			VMenu obj = this.vMenuService.getObjById(CommUtil
					.null2Long(menu_id));
			obj = (VMenu) wf.toPo(request, obj);
			obj.setMenu_cat("admin");
			obj.setParent(parent);
			this.vMenuService.update(obj);
		} else {
			VMenu obj = wf.toPo(request, VMenu.class);
			obj.setMenu_cat("admin");
			obj.setParent(parent);
			this.vMenuService.save(obj);
		}
		return "redirect:weixin_plat_menu.htm";
	}

	@SecurityMapping(title = "微商城菜单删除", value = "/admin/weixin_menu_delete.htm*", rtype = "admin", rname = "微商城配置", rcode = "weixin_plat_admin", rgroup = "运营")
	@RequestMapping("/admin/weixin_menu_delete.htm")
	public String weixin_menu_delete(HttpServletRequest request,
			HttpServletResponse response, String menu_id) throws IOException {
		if (!CommUtil.null2String(menu_id).equals("")) {
			this.vMenuService.delete(CommUtil.null2Long(menu_id));
		}
		return "redirect:weixin_plat_menu.htm";
	}

	@SecurityMapping(title = "微商城菜单创建", value = "/admin/weixin_plat_menu_create.htm*", rtype = "admin", rname = "微商城配置", rcode = "weixin_plat_admin", rgroup = "运营")
	@RequestMapping("/admin/weixin_plat_menu_create.htm")
	public void weixin_plat_menu_create(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		int ret = this.createMenu();
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

	@SecurityMapping(title = "微商城菜单验证", value = "/admin/weixin_menukey_verify.htm*", rtype = "admin", rname = "微商城配置", rcode = "weixin_plat_admin", rgroup = "运营")
	@RequestMapping("/admin/weixin_menukey_verify.htm")
	public void weixin_menukey_verify(HttpServletRequest request,
			HttpServletResponse response, String menu_id, String menu_key,
			String store_id) throws IOException {
		boolean ret = true;
		Map params = new HashMap();
		params.put("menu_key", menu_key);
		params.put("menu_cat", "admin");
		params.put("menu_id", CommUtil.null2Long(menu_id));
		List<VMenu> VMenus = this.vmenuService
				.query("select obj from VMenu obj where obj.menu_key=:menu_key and obj.id!=:menu_id and obj.menu_cat=:menu_cat",
						params, -1, -1);
		if (VMenus != null && VMenus.size() > 0) {
			ret = false;
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

	private String getAccess_token() {
		// 获得ACCESS_TOKEN
		String appId = CommUtil.null2String(this.configService.getSysConfig()
				.getWeixin_appId());
		String appSecret = CommUtil.null2String(this.configService
				.getSysConfig().getWeixin_appSecret());
		String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
				+ appId + "&secret=" + appSecret;
		String accessToken = null;
		try {
			URL urlGet = new URL(url);
			HttpURLConnection http = (HttpURLConnection) urlGet
					.openConnection();
			http.setRequestMethod("GET"); // 必须是get方式请求
			http.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			http.setDoOutput(true);
			http.setDoInput(true);
			System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
			System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
			http.connect();
			InputStream is = http.getInputStream();
			int size = is.available();
			byte[] jsonBytes = new byte[size];
			is.read(jsonBytes);
			String message = new String(jsonBytes, "UTF-8");
			Map map = Json.fromJson(HashMap.class, message);
			accessToken = CommUtil.null2String(map.get("access_token"));
			// System.out.println("message：" + message);
			// System.out.println("accessToken：" + accessToken);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accessToken;
	}

	private int createMenu() throws IOException {
		int ret = 0;
		Map weixin_plat_menu = new HashMap();
		List<Weixin_Menu> list = new ArrayList<Weixin_Menu>();
		Map params = new HashMap();
		params.put("menu_cat", "admin");
		List<VMenu> vmenus = this.vMenuService
				.query("select obj from VMenu obj where obj.menu_cat=:menu_cat and obj.parent.id is null order by obj.menu_sequence asc",
						params, -1, -1);
		for (VMenu vmenu : vmenus) {
			Weixin_Menu menu = new Weixin_Menu();
			menu.setKey(vmenu.getMenu_key());
			menu.setName(vmenu.getMenu_name());
			menu.setType(vmenu.getMenu_type());
			menu.setUrl(vmenu.getMenu_url());
			for (VMenu c_vmenu : vmenu.getChilds()) {
				Weixin_Menu c_menu = new Weixin_Menu();
				c_menu.setKey(c_vmenu.getMenu_key());
				c_menu.setName(c_vmenu.getMenu_name());
				c_menu.setType(c_vmenu.getMenu_type());
				c_menu.setUrl(c_vmenu.getMenu_url());
				menu.getSub_button().add(c_menu);
			}
			list.add(menu);
		}
		weixin_plat_menu.put("button", list);
		System.out.println(Json.toJson(weixin_plat_menu, JsonFormat.compact()));
		String user_define_menu = Json.toJson(weixin_plat_menu,
				JsonFormat.compact());
		String access_token = getAccess_token();
		String action = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token="
				+ access_token;
		try {
			URL url = new URL(action);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			http.setDoOutput(true);
			http.setDoInput(true);
			System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
			System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
			http.connect();
			OutputStream os = http.getOutputStream();
			os.write(user_define_menu.getBytes("UTF-8"));// 传入参数
			os.flush();
			os.close();
			InputStream is = http.getInputStream();
			int size = is.available();
			byte[] jsonBytes = new byte[size];
			is.read(jsonBytes);
			String message = new String(jsonBytes, "UTF-8");
			System.out.println("menu:" + message);
			Map ret_map = Json.fromJson(HashMap.class, message);
			ret = CommUtil.null2Int(ret_map.get("errcode"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public class Weixin_Menu {
		private String type;// click或者view
		private String name;// 菜单名称
		private String key;// 菜单key
		private String url;// 菜单url
		private List<Weixin_Menu> sub_button = new ArrayList<Weixin_Menu>();// 子菜单

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public List<Weixin_Menu> getSub_button() {
			return sub_button;
		}

		public void setSub_button(List<Weixin_Menu> sub_button) {
			this.sub_button = sub_button;
		}

	}
}
