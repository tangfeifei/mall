package com.lakecloud.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsBrand;
import com.lakecloud.foundation.domain.GoodsBrandCategory;
import com.lakecloud.foundation.domain.GoodsType;
import com.lakecloud.foundation.domain.query.GoodsBrandQueryObject;
import com.lakecloud.foundation.service.IAccessoryService;
import com.lakecloud.foundation.service.IGoodsBrandCategoryService;
import com.lakecloud.foundation.service.IGoodsBrandService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;

@Controller
public class GoodsBrandManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IGoodsBrandCategoryService goodsBrandCategoryService;
	@Autowired
	private IGoodsService goodsService;

	/**
	 * GoodsBrand列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商品品牌列表", value = "/admin/goods_brand_list.htm*", rtype = "admin", rname = "品牌管理", rcode = "goods_brand", rgroup = "商品")
	@RequestMapping("/admin/goods_brand_list.htm")
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String name, String category) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_brand_list.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
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

	/**
	 * GoodsBrand审核页
	 * 
	 * @param request
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "商品品牌待审核列表", value = "/admin/goods_brand_audit.htm*", rtype = "admin", rname = "品牌管理", rcode = "goods_brand", rgroup = "商品")
	@RequestMapping("/admin/goods_brand_audit.htm")
	public ModelAndView audit(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String name, String category) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/goods_brand_audit.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsBrandQueryObject qo = new GoodsBrandQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.addQuery("obj.audit", new SysMap("audit", 0), "=");
		qo.addQuery("obj.userStatus", new SysMap("userStatus", 1), "=");
		if (!CommUtil.null2String(name).equals("")) {
			qo.addQuery("obj.name",
					new SysMap("name", "%" + name.trim() + "%"), "like");
		}
		if (!CommUtil.null2String(category).equals("")) {
			qo.addQuery("obj.category.name", new SysMap("category", "%"
					+ category.trim() + "%"), "like");
		}
		IPageList pList = this.goodsBrandService.list(qo);
		CommUtil.saveIPageList2ModelAndView("/admin/goods_brand_audit.htm", "",
				"", pList, mv);
		mv.addObject("name", name);
		mv.addObject("category", category);
		return mv;
	}

	@SecurityMapping(title = "商品品牌审核通过", value = "/admin/goods_brands_pass.htm*", rtype = "admin", rname = "品牌管理", rcode = "goods_brand", rgroup = "商品")
	@RequestMapping("/admin/goods_brands_pass.htm")
	public String goods_brands_pass(HttpServletRequest request, String id) {
		if (!id.equals("")) {
			GoodsBrand goodsBrand = this.goodsBrandService.getObjById(Long
					.parseLong(id));
			goodsBrand.setAudit(1);
			this.goodsBrandService.update(goodsBrand);
		}
		return "redirect:goods_brand_audit.htm";
	}

	@SecurityMapping(title = "商品品牌审核拒绝", value = "/admin/goods_brands_refuse.htm*", rtype = "admin", rname = "品牌管理", rcode = "goods_brand", rgroup = "商品")
	@RequestMapping("/admin/goods_brands_refuse.htm")
	public String goods_brands_refuse(HttpServletRequest request, String id) {
		if (!id.equals("")) {
			GoodsBrand goodsBrand = this.goodsBrandService.getObjById(Long
					.parseLong(id));
			goodsBrand.setAudit(-1);
			this.goodsBrandService.update(goodsBrand);
		}
		return "redirect:goods_brand_audit.htm";
	}

	/**
	 * goodsBrand添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "商品品牌添加", value = "/admin/goods_brand_add.htm*", rtype = "admin", rname = "品牌管理", rcode = "goods_brand", rgroup = "商品")
	@RequestMapping("/admin/goods_brand_add.htm")
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_brand_add.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		List<GoodsBrandCategory> categorys = this.goodsBrandCategoryService
				.query("select obj from GoodsBrandCategory obj", null, -1, -1);
		mv.addObject("categorys", categorys);
		return mv;
	}

	/**
	 * goodsBrand编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "商品品牌编辑", value = "/admin/goods_brand_edit.htm*", rtype = "admin", rname = "品牌管理", rcode = "goods_brand", rgroup = "商品")
	@RequestMapping("/admin/goods_brand_edit.htm")
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_brand_add.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			GoodsBrand goodsBrand = this.goodsBrandService.getObjById(Long
					.parseLong(id));
			mv.addObject("obj", goodsBrand);
		}
		List<GoodsBrandCategory> categorys = this.goodsBrandCategoryService
				.query("select obj from GoodsBrandCategory obj", null, -1, -1);
		mv.addObject("categorys", categorys);
		mv.addObject("edit", true);
		return mv;
	}

	/**
	 * goodsBrand保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "商品品牌保存", value = "/admin/goods_band_save.htm*", rtype = "admin", rname = "品牌管理", rcode = "goods_brand", rgroup = "商品")
	@RequestMapping("/admin/goods_band_save.htm")
	public ModelAndView save(HttpServletRequest request,
			HttpServletResponse response, String id, String cmd,
			String cat_name, String list_url, String add_url) {
		WebForm wf = new WebForm();
		GoodsBrand goodsBrand = null;
		if (id.equals("")) {
			goodsBrand = wf.toPo(request, GoodsBrand.class);
			goodsBrand.setAddTime(new Date());
			goodsBrand.setAudit(1);
			goodsBrand.setUserStatus(0);
		} else {
			GoodsBrand obj = this.goodsBrandService.getObjById(Long
					.parseLong(id));
			goodsBrand = (GoodsBrand) wf.toPo(request, obj);
		}
		GoodsBrandCategory cat = this.goodsBrandCategoryService
				.getObjByProperty("name", cat_name);
		if (cat == null) {
			cat = new GoodsBrandCategory();
			cat.setAddTime(new Date());
			cat.setName(cat_name);
			this.goodsBrandCategoryService.save(cat);
			goodsBrand.setCategory(cat);
		} else {
			goodsBrand.setCategory(cat);
		}

		// 品牌标识图片
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "brand";
		Map map = new HashMap();
		try {
			String fileName = goodsBrand.getBrandLogo() == null ? ""
					: goodsBrand.getBrandLogo().getName();
			map = CommUtil.saveFileToServer(request, "brandLogo",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(CommUtil.null2Float(map.get("fileSize")));
					photo.setPath(uploadFilePath + "/brand");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					goodsBrand.setBrandLogo(photo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory photo = goodsBrand.getBrandLogo();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(CommUtil.null2Float(map.get("fileSize")));
					photo.setPath(uploadFilePath + "/brand");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.update(photo);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (id.equals("")) {
			this.goodsBrandService.save(goodsBrand);
		} else
			this.goodsBrandService.update(goodsBrand);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存品牌成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url);
		}
		return mv;
	}

	@SecurityMapping(title = "商品品牌删除", value = "/admin/goods_brand_del.htm*", rtype = "admin", rname = "品牌管理", rcode = "goods_brand", rgroup = "商品")
	@RequestMapping("/admin/goods_brand_del.htm")
	public String delete(HttpServletRequest request, String mulitId,
			String audit, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				GoodsBrand brand = this.goodsBrandService.getObjById(Long
						.parseLong(id));
				CommUtil.del_acc(request, brand.getBrandLogo());
				for (Goods goods : brand.getGoods_list()) {
					goods.setGoods_brand(null);
					this.goodsService.update(goods);
				}
				for (GoodsType type : brand.getTypes()) {
					type.getGbs().remove(brand);
				}
				this.goodsBrandService.delete(Long.parseLong(id));
			}
		}
		String returnUrl = "redirect:goods_brand_list.htm?currentPage="
				+ currentPage;
		if (audit != null && !audit.equals("")) {
			returnUrl = "redirect:goods_brand_audit.htm?currentPage="
					+ currentPage;
		}
		return returnUrl;
	}

	@SecurityMapping(title = "商品品牌AJAX更新", value = "/admin/goods_brand_ajax.htm*", rtype = "admin", rname = "品牌管理", rcode = "goods_brand", rgroup = "商品")
	@RequestMapping("/admin/goods_brand_ajax.htm")
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		GoodsBrand obj = this.goodsBrandService.getObjById(Long.parseLong(id));
		Field[] fields = GoodsBrand.class.getDeclaredFields();
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
		this.goodsBrandService.update(obj);
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

	@RequestMapping("/admin/goods_brand_verify.htm")
	public void goods_brand_verify(HttpServletRequest request,
			HttpServletResponse response, String name, String id) {
		boolean ret = true;
		Map params = new HashMap();
		params.put("name", name);
		params.put("id", CommUtil.null2Long(id));
		List<GoodsBrand> gcs = this.goodsBrandService
				.query(
						"select obj from GoodsBrand obj where obj.name=:name and obj.id!=:id",
						params, -1, -1);
		if (gcs != null && gcs.size() > 0) {
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
}