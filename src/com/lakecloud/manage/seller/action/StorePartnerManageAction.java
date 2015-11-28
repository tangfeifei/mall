package com.lakecloud.manage.seller.action;

import java.text.ParseException;
import java.util.Date;

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
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.StorePartner;
import com.lakecloud.foundation.domain.query.StorePartnerQueryObject;
import com.lakecloud.foundation.service.IStorePartnerService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;

/**
 * @info 卖家中心友情链接控制器
 
 * 
 */
@Controller
public class StorePartnerManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IStorePartnerService storepartnerService;
	@Autowired
	private IStoreService storeService;

	/**
	 * StorePartner列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "卖家合作伙伴列表", value = "/seller/store_partner.htm*", rtype = "seller", rname = "友情链接", rcode = "store_partner_seller", rgroup = "店铺设置")
	@RequestMapping("/seller/store_partner.htm")
	public ModelAndView store_partner(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/store_partner.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		StorePartnerQueryObject qo = new StorePartnerQueryObject(currentPage,
				mv, orderBy, orderType);
		Store store = this.storeService.getObjByProperty("user.id",
				SecurityUserHolder.getCurrentUser().getId());
		qo.addQuery("obj.store.id", new SysMap("store_id", store.getId()), "=");
		// WebForm wf = new WebForm();
		// wf.toQueryPo(request, qo,StorePartner.class,mv);
		IPageList pList = this.storepartnerService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/seller/store_partner.htm",
				"", params, pList, mv);
		return mv;
	}

	/**
	 * storepartner添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "卖家合作伙伴添加", value = "/seller/store_partner_add.htm*", rtype = "seller", rname = "友情链接", rcode = "store_partner_seller", rgroup = "店铺设置")
	@RequestMapping("/seller/store_partner_add.htm")
	public ModelAndView store_partner_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/store_partner_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * storepartner编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "卖家合作伙伴编辑", value = "/seller/store_partner_edit.htm*", rtype = "seller", rname = "友情链接", rcode = "store_partner_seller", rgroup = "店铺设置")
	@RequestMapping("/seller/store_partner_edit.htm")
	public ModelAndView store_partner_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/store_partner_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			StorePartner storepartner = this.storepartnerService
					.getObjById(Long.parseLong(id));
			mv.addObject("obj", storepartner);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * storepartner保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "卖家合作伙伴保存", value = "/seller/store_partner_save.htm*", rtype = "seller", rname = "友情链接", rcode = "store_partner_seller", rgroup = "店铺设置")
	@RequestMapping("/seller/store_partner_save.htm")
	public ModelAndView store_partner_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url) {
		WebForm wf = new WebForm();
		StorePartner storepartner = null;
		if (id.equals("")) {
			storepartner = wf.toPo(request, StorePartner.class);
			storepartner.setAddTime(new Date());
		} else {
			StorePartner obj = this.storepartnerService.getObjById(Long
					.parseLong(id));
			storepartner = (StorePartner) wf.toPo(request, obj);
		}
		Store store = this.storeService.getObjByProperty("user.id",
				SecurityUserHolder.getCurrentUser().getId());
		storepartner.setStore(store);
		if (id.equals("")) {
			this.storepartnerService.save(storepartner);
		} else
			this.storepartnerService.update(storepartner);
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("url", CommUtil.getURL(request)
				+ "/seller/store_partner.htm");
		mv.addObject("op_title", "保存友情链接成功");
		return mv;
	}

	@SecurityMapping(title = "卖家合作伙伴删除", value = "/seller/store_partner_del.htm*", rtype = "seller", rname = "友情链接", rcode = "store_partner_seller", rgroup = "店铺设置")
	@RequestMapping("/seller/store_partner_del.htm")
	public String store_partner_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				StorePartner storepartner = this.storepartnerService
						.getObjById(Long.parseLong(id));
				this.storepartnerService.delete(Long.parseLong(id));
			}
		}
		return "redirect:store_partner.htm?currentPage=" + currentPage;
	}
}