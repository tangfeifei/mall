package com.lakecloud.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.ParseException;
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

import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.domain.virtual.SysMap;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.WebForm;
import com.lakecloud.foundation.domain.GoodsBrand;
import com.lakecloud.foundation.domain.SpareGoods;
import com.lakecloud.foundation.domain.SpareGoodsClass;
import com.lakecloud.foundation.domain.SpareGoodsFloor;
import com.lakecloud.foundation.domain.query.SpareGoodsClassQueryObject;
import com.lakecloud.foundation.domain.query.SpareGoodsQueryObject;
import com.lakecloud.foundation.service.ISpareGoodsClassService;
import com.lakecloud.foundation.service.ISpareGoodsFloorService;
import com.lakecloud.foundation.service.ISpareGoodsService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;

/**
 * @info 平台闲置商品管理控制器，用来管理所有闲置商品信息
 
 * 
 */
@Controller
public class SpareGoodsManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private ISpareGoodsClassService sparegoodsclassService;
	@Autowired
	private ISpareGoodsService sparegoodsService;
	@Autowired
	private ISpareGoodsFloorService spareGoodsFloorService;

	/**
	 * 闲置商品列表
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param type,当type为-1的时候查询违规下架的闲置商品
	 * @return
	 */

	@SecurityMapping(title = "闲置商品列表", value = "/admin/sparegoods_list.htm*", rtype = "admin", rname = "闲置商品", rcode = "sparegoods_admin", rgroup = "闲置")
	@RequestMapping("/admin/sparegoods_list.htm")
	public ModelAndView spare_goods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String type) {
		ModelAndView mv = new JModelAndView("admin/blue/sparegoods_list.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		SpareGoodsQueryObject qo = new SpareGoodsQueryObject(currentPage, mv,
				orderBy, orderType);
		if (type != null && !type.equals("")) {
			qo.addQuery("obj.status", new SysMap("obj_status", CommUtil
					.null2Int(type)), "=");
			mv.addObject("type", type);
		} else {
			qo.addQuery("obj.status", new SysMap("obj_status", 0), "=");
		}
		qo.setPageSize(15);
		qo.setOrderBy("addTime");
		qo.setOrderType("desc");
		IPageList pList = this.sparegoodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	/**
	 * 闲置商品下架上架切换
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */

	@SecurityMapping(title = "闲置商品下架上架切换", value = "/admin/sparegoods_switch.htm*", rtype = "admin", rname = "闲置商品", rcode = "sparegoods_admin", rgroup = "闲置")
	@RequestMapping("/admin/sparegoods_switch.htm")
	public String sparegoods_switch(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String type) {
		if (id != null && !id.equals("")) {
			SpareGoods sg = this.sparegoodsService.getObjById(CommUtil
					.null2Long(id));
			if (sg.getStatus() == 0) {
				sg.setStatus(-1);
			} else {
				sg.setStatus(0);
			}
			this.sparegoodsService.update(sg);
		}
		return "redirect:sparegoods_list.htm?currentPage=" + currentPage
				+ (type != null ? "&type=" + type : "");
	}

	/**
	 * 闲置商品信息删除
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "闲置商品信息删除", value = "/admin/sparegoods_dele.htm*", rtype = "admin", rname = "闲置商品", rcode = "sparegoods_admin", rgroup = "闲置")
	@RequestMapping("/admin/sparegoods_dele.htm")
	public String sparegoods_delete(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			this.sparegoodsService.delete(CommUtil.null2Long(id));
		}
		return "redirect:sparegoods_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "闲置商品AJAX更新", value = "/admin/sparegoods_ajax.htm*", rtype = "admin", rname = "闲置商品", rcode = "sparegoods_admin", rgroup = "闲置")
	@RequestMapping("/admin/sparegoods_ajax.htm")
	public void sparegoods_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		SpareGoods obj = this.sparegoodsService.getObjById(Long.parseLong(id));
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
		this.sparegoodsService.update(obj);
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
	 * SpareGoodsClass列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "闲置商品分类列表", value = "/admin/sparegoods_class_list.htm*", rtype = "admin", rname = "闲置分类", rcode = "sparegoods_class_admin", rgroup = "闲置")
	@RequestMapping("/admin/sparegoods_class_list.htm")
	public ModelAndView sparegoods_class_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/sparegoods_class_list.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SpareGoodsClassQueryObject qo = new SpareGoodsClassQueryObject(
				currentPage, mv, orderBy, orderType);
		qo.setOrderBy("sequence");
		qo.setOrderType("asc");
		qo.addQuery("obj.parent.id is null", null);
		IPageList pList = this.sparegoodsclassService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	/**
	 * sparegoodsclass添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "闲置商品分类添加", value = "/admin/sparegoods_class_add.htm*", rtype = "admin", rname = "闲置分类", rcode = "sparegoods_class_admin", rgroup = "闲置")
	@RequestMapping("/admin/sparegoods_class_add.htm")
	public ModelAndView sparegoodsclass_add(HttpServletRequest request,
			HttpServletResponse response, String pid) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/sparegoods_class_add.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<SpareGoodsClass> sparegoodsClass = this.sparegoodsclassService
				.query(
						"select obj from SpareGoodsClass obj where obj.parent.id is null",
						null, -1, -1);
		mv.addObject("sgcs", sparegoodsClass);
		if (pid != null && !pid.equals("")) {
			SpareGoodsClass parent = this.sparegoodsclassService
					.getObjById(CommUtil.null2Long(pid));
			SpareGoodsClass obj = new SpareGoodsClass();
			obj.setParent(parent);
			mv.addObject("obj", obj);
		}
		return mv;
	}

	/**
	 * sparegoodsclass编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "闲置商品分类编辑", value = "/admin/sparegoods_class_edit.htm*", rtype = "admin", rname = "闲置分类", rcode = "sparegoods_class_admin", rgroup = "闲置")
	@RequestMapping("/admin/sparegoods_class_edit.htm")
	public ModelAndView sparegoods_class_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/sparegoods_class_add.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			SpareGoodsClass sparegoodsclass = this.sparegoodsclassService
					.getObjById(Long.parseLong(id));
			mv.addObject("obj", sparegoodsclass);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		List<SpareGoodsClass> sparegoodsClass = this.sparegoodsclassService
				.query(
						"select obj from SpareGoodsClass obj where obj.parent.id is null",
						null, -1, -1);
		mv.addObject("sgcs", sparegoodsClass);
		return mv;
	}

	/**
	 * sparegoodsclass保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "闲置商品分类保存", value = "/admin/sparegoods_class_save.htm*", rtype = "admin", rname = "闲置分类", rcode = "sparegoods_class_admin", rgroup = "闲置")
	@RequestMapping("/admin/sparegoods_class_save.htm")
	public ModelAndView sparegoods_class_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String list_url, String add_url, String pid) {
		WebForm wf = new WebForm();
		SpareGoodsClass sparegoodsclass = null;
		if (id.equals("")) {
			sparegoodsclass = wf.toPo(request, SpareGoodsClass.class);
			sparegoodsclass.setAddTime(new Date());
			sparegoodsclass.setLevel(1);
		} else {
			SpareGoodsClass obj = this.sparegoodsclassService.getObjById(Long
					.parseLong(id));
			sparegoodsclass = (SpareGoodsClass) wf.toPo(request, obj);
		}
		if (pid != null && !pid.equals("")) {
			SpareGoodsClass parent = this.sparegoodsclassService
					.getObjById(CommUtil.null2Long(pid));
			sparegoodsclass.setParent(parent);
			sparegoodsclass.setLevel(parent.getLevel() + 1);
		}
		if (id.equals("")) {
			this.sparegoodsclassService.save(sparegoodsclass);
		} else
			this.sparegoodsclassService.update(sparegoodsclass);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "闲置商品分类保存成功!");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?pid=" + pid + "&currentPage="
					+ currentPage);
		}
		return mv;
	}

	@SecurityMapping(title = "闲置商品分类删除", value = "/admin/sparegoods_class_del.htm*", rtype = "admin", rname = "闲置分类", rcode = "sparegoods_class_admin", rgroup = "闲置")
	@RequestMapping("/admin/sparegoods_class_del.htm")
	public String sparegoods_class_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Set<Long> list = this.genericIds(this.sparegoodsclassService
						.getObjById(Long.parseLong(id)));
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("ids", list);
				List<SpareGoodsClass> sgcs = this.sparegoodsclassService
						.query(
								"select obj from SpareGoodsClass obj where obj.id in (:ids) order by obj.level desc",
								params, -1, -1);
				for (SpareGoodsClass gc : sgcs) {
					for (SpareGoods sg : gc.getSgs()) {
						sg.setSpareGoodsClass(null);
						this.sparegoodsService.update(sg);
					}
					SpareGoodsFloor floor = gc.getFloor();
					if (floor != null) {
						floor.setSgc(null);
						this.spareGoodsFloorService.update(floor);
					}
					gc.setParent(null);
					this.sparegoodsclassService.delete(gc.getId());
				}
			}
		}
		return "redirect:sparegoods_class_list.htm?currentPage=" + currentPage;
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

	@SecurityMapping(title = "闲置商品分类Ajax更新", value = "/admin/sparegoods_class_ajax.htm*", rtype = "admin", rname = "闲置分类", rcode = "sparegoods_class_admin", rgroup = "闲置")
	@RequestMapping("/admin/sparegoods_class_ajax.htm")
	public void sparegoods_class_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		SpareGoodsClass obj = this.sparegoodsclassService.getObjById(Long
				.parseLong(id));
		Field[] fields = SpareGoodsClass.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(obj);
		Object val = null;
		for (Field field : fields) {
			// System.out.println(field.getName());
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
		this.sparegoodsclassService.update(obj);
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

	@SecurityMapping(title = "闲置商品分类下级加载", value = "/admin/sparegoods_class_data.htm*", rtype = "admin", rname = "闲置分类", rcode = "sparegoods_class_admin", rgroup = "闲置")
	@RequestMapping("/admin/sparegoods_class_data.htm")
	public ModelAndView sparegoods_class_data(HttpServletRequest request,
			HttpServletResponse response, String pid) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/sparegoods_class_data.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map map = new HashMap();
		map.put("pid", Long.parseLong(pid));
		List<SpareGoodsClass> gcs = this.sparegoodsclassService
				.query(
						"select obj from SpareGoodsClass obj where obj.parent.id =:pid",
						map, -1, -1);
		mv.addObject("gcs", gcs);
		return mv;
	}

	@SecurityMapping(title = "闲置商品分类验证是否存在", value = "/admin/sparegoods_class_verify.htm*", rtype = "admin", rname = "闲置分类", rcode = "sparegoods_class_admin", rgroup = "闲置")
	@RequestMapping("/admin/sparegoods_class_verify.htm")
	public void sparegoods_class_verify(HttpServletRequest request,
			HttpServletResponse response, String className, String pid) {
		boolean ret = true;
		List<SpareGoodsClass> sgcs = null;
		Map params = new HashMap();
		params.put("className", className);
		if (pid.equals("")) {
			sgcs = this.sparegoodsclassService
					.query(
							"select obj from SpareGoodsClass obj where obj.className=:className",
							params, -1, -1);
		} else {
			params.put("pid", CommUtil.null2Long(pid));
			sgcs = this.sparegoodsclassService
					.query(
							"select obj from SpareGoodsClass obj where obj.className=:className and obj.parent.id =:pid",
							params, -1, -1);
		}
		if (sgcs.size() > 0) {
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