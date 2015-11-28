package com.lakecloud.manage.seller.action;

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

import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.domain.virtual.SysMap;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.WebForm;
import com.lakecloud.foundation.domain.UserGoodsClass;
import com.lakecloud.foundation.domain.query.UserGoodsClassQueryObject;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserGoodsClassService;

/**
 * 卖家中心商品分类管理
 * 
 * @author www.chinacloud.net erikchang
 * 
 */
@Controller
public class GoodsClassSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserGoodsClassService usergoodsclassService;

	/**
	 * UserGoodsClass列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "卖家商品分类列表", value = "/seller/usergoodsclass_list.htm*", rtype = "seller", rname = "商品分类", rcode = "usergoodsclass_seller", rgroup = "商品管理")
	@RequestMapping("/seller/usergoodsclass_list.htm")
	public ModelAndView usergoodsclass_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/usergoodsclass_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		UserGoodsClassQueryObject qo = new UserGoodsClassQueryObject(
				currentPage, mv, orderBy, orderType);
		qo.setPageSize(20);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, UserGoodsClass.class, mv);
		qo.addQuery("obj.parent.id is null", null);
		if (SecurityUserHolder.getCurrentUser().getParent() == null) {
			qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder
					.getCurrentUser().getId()), "=");
		} else {
			qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder
					.getCurrentUser().getParent().getId()), "=");
		}
		qo.setOrderBy("sequence");
		qo.setOrderType("asc");
		IPageList pList = this.usergoodsclassService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/seller/usergoodsclass_list.htm", "", params, pList, mv);
		return mv;
	}

	/**
	 * usergoodsclass保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "卖家商品分类保存", value = "/seller/usergoodsclass_save.htm*", rtype = "seller", rname = "商品分类", rcode = "usergoodsclass_seller", rgroup = "商品管理")
	@RequestMapping("/seller/usergoodsclass_save.htm")
	public String usergoodsclass_save(HttpServletRequest request,
			HttpServletResponse response, String id, String pid) {
		WebForm wf = new WebForm();
		UserGoodsClass usergoodsclass = null;
		if (id.equals("")) {
			usergoodsclass = wf.toPo(request, UserGoodsClass.class);
			usergoodsclass.setAddTime(new Date());
		} else {
			UserGoodsClass obj = this.usergoodsclassService.getObjById(Long
					.parseLong(id));
			usergoodsclass = (UserGoodsClass) wf.toPo(request, obj);
		}
		usergoodsclass.setUser(SecurityUserHolder.getCurrentUser());
		if (!pid.equals("")) {
			UserGoodsClass parent = this.usergoodsclassService.getObjById(Long
					.parseLong(pid));
			usergoodsclass.setParent(parent);
		}
		boolean ret = true;
		if (id.equals("")) {
			ret = this.usergoodsclassService.save(usergoodsclass);
		} else
			ret = this.usergoodsclassService.update(usergoodsclass);
		return "redirect:usergoodsclass_list.htm";
	}

	@SecurityMapping(title = "卖家商品分类删除", value = "/seller/usergoodsclass_del.htm*", rtype = "seller", rname = "商品分类", rcode = "usergoodsclass_seller", rgroup = "商品管理")
	@RequestMapping("/seller/usergoodsclass_del.htm")
	public String usergoodsclass_del(HttpServletRequest request, String mulitId) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				UserGoodsClass usergoodsclass = this.usergoodsclassService
						.getObjById(Long.parseLong(id));
				this.usergoodsclassService.delete(Long.parseLong(id));
			}
		}
		return "redirect:usergoodsclass_list.htm";
	}

	@SecurityMapping(title = "新增卖家商品分类", value = "/seller/usergoodsclass_add.htm*", rtype = "seller", rname = "商品分类", rcode = "usergoodsclass_seller", rgroup = "商品管理")
	@RequestMapping("/seller/usergoodsclass_add.htm")
	public ModelAndView usergoodsclass_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String pid) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/usergoodsclass_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map map = new HashMap();
		map.put("uid", SecurityUserHolder.getCurrentUser().getId());
		List<UserGoodsClass> ugcs = this.usergoodsclassService
				.query("select obj from UserGoodsClass obj where obj.parent.id is null and obj.user.id = :uid order by obj.sequence asc",
						map, -1, -1);
		if (!CommUtil.null2String(pid).equals("")) {
			UserGoodsClass parent = this.usergoodsclassService
					.getObjById(CommUtil.null2Long(pid));
			UserGoodsClass obj = new UserGoodsClass();
			obj.setParent(parent);
			mv.addObject("obj", obj);
		}
		mv.addObject("ugcs", ugcs);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@SecurityMapping(title = "编辑卖家商品分类", value = "/seller/usergoodsclass_edit.htm*", rtype = "seller", rname = "商品分类", rcode = "usergoodsclass_seller", rgroup = "商品管理")
	@RequestMapping("/seller/usergoodsclass_edit.htm")
	public ModelAndView usergoodsclass_edit(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/usergoodsclass_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<UserGoodsClass> ugcs = this.usergoodsclassService
				.query("select obj from UserGoodsClass obj where obj.parent.id is null and obj.user.id = :uid order by obj.sequence asc",
						null, -1, -1);
		UserGoodsClass obj = this.usergoodsclassService.getObjById(CommUtil
				.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("ugcs", ugcs);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
}