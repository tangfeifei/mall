package com.lakecloud.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Date;

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
import com.lakecloud.foundation.domain.Consult;
import com.lakecloud.foundation.domain.query.ConsultQueryObject;
import com.lakecloud.foundation.service.IConsultService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;

@Controller
public class ConsultManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IConsultService consultService;

	/**
	 * Consult列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "咨询列表", value = "/admin/consult_list.htm*", rtype = "admin", rname = "咨询管理", rcode = "consult_admin", rgroup = "交易")
	@RequestMapping("/admin/consult_list.htm")
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String consult_user_userName,
			String consult_content) {
		ModelAndView mv = new JModelAndView("admin/blue/consult_list.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		ConsultQueryObject qo = new ConsultQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.setPageSize(1);
		if (consult_user_userName != null && !consult_user_userName.equals("")) {
			qo.addQuery("obj.consult_user.userName", new SysMap("userName",
					CommUtil.null2String(consult_user_userName).trim()), "=");
		}
		if (consult_content != null && !consult_content.equals("")) {
			qo.addQuery("obj.consult_content", new SysMap("consult_content",
					"%" + consult_content + "%"), "like");
		}
		IPageList pList = this.consultService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/consult_list.htm",
				"", params, pList, mv);
		mv.addObject("consult_user_userName", consult_user_userName);
		mv.addObject("consult_content", consult_content);
		return mv;
	}

	@SecurityMapping(title = "咨询删除", value = "/admin/consult_del.htm*", rtype = "admin", rname = "咨询管理", rcode = "consult_admin", rgroup = "交易")
	@RequestMapping("/admin/consult_del.htm")
	public String delete(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Consult consult = this.consultService.getObjById(Long
						.parseLong(id));
				this.consultService.delete(Long.parseLong(id));
			}
		}
		return "redirect:consult_list.htm?currentPage=" + currentPage;
	}
}