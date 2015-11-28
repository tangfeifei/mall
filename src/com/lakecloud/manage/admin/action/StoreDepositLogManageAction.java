package com.lakecloud.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

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
import com.lakecloud.foundation.domain.Area;
import com.lakecloud.foundation.domain.IntegralLog;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.StoreClass;
import com.lakecloud.foundation.domain.StoreDepositLog;
import com.lakecloud.foundation.domain.StoreGrade;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.domain.query.StoreDepositLogQueryObject;
import com.lakecloud.foundation.service.IStoreDepositLogService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: StoreDepositLogManageAction.java
 * </p>
 * 
 * <p>
 * Description:店铺保证金平台管理控制器，用来审核线下支付的店铺保证金，扣除违规用户保证金，显示所有用户保证金等功能
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011-2014
 * </p>
 * 
 * <p>
 * Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-5-8
 * 
 * @version LakeCloud_C2C 1.4
 */
@Controller
public class StoreDepositLogManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IStoreDepositLogService storeDepositLogService;
	@Autowired
	private IUserService userService;

	/**
	 * StoreDepositLog列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "店铺保证金", value = "/admin/deposit_list.htm*", rtype = "admin", rname = "店铺保证金", rcode = "admin_store_deposit", rgroup = "店铺")
	@RequestMapping("/admin/deposit_list.htm")
	public ModelAndView deposit_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String beginTime, String endTime,
			String store_name) {
		ModelAndView mv = new JModelAndView("admin/blue/deposit_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		StoreDepositLogQueryObject qo = new StoreDepositLogQueryObject(
				currentPage, mv, orderBy, orderType);
		if (!CommUtil.null2String(beginTime).equals("")) {
			qo.addQuery("obj.addTime",
					new SysMap("beginTime", CommUtil.formatDate(beginTime)),
					">=");
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			qo.addQuery("obj.addTime",
					new SysMap("endTime", CommUtil.formatDate(endTime)), "<=");
		}
		if (!CommUtil.null2String(store_name).equals("")) {
			qo.addQuery("obj.dp_store_name", new SysMap("store_name", "%"
					+ CommUtil.null2String(store_name) + "%"), "like");
		}
		qo.addQuery("obj.dp_type", new SysMap("dp_type", 0), "=");
		IPageList pList = this.storeDepositLogService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("store_name", store_name);
		return mv;
	}

	@SecurityMapping(title = "店铺保证金操作日志", value = "/admin/deposit_log_list.htm*", rtype = "admin", rname = "店铺保证金", rcode = "admin_store_deposit", rgroup = "店铺")
	@RequestMapping("/admin/deposit_log_list.htm")
	public ModelAndView deposit_log_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String beginTime, String endTime,
			String store_name) {
		ModelAndView mv = new JModelAndView("admin/blue/deposit_log_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		StoreDepositLogQueryObject qo = new StoreDepositLogQueryObject(
				currentPage, mv, orderBy, orderType);
		if (!CommUtil.null2String(beginTime).equals("")) {
			qo.addQuery("obj.addTime",
					new SysMap("beginTime", CommUtil.formatDate(beginTime)),
					">=");
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			qo.addQuery("obj.addTime",
					new SysMap("endTime", CommUtil.formatDate(endTime)), "<=");
		}
		if (!CommUtil.null2String(store_name).equals("")) {
			qo.addQuery("obj.dp_store_name", new SysMap("store_name", "%"
					+ CommUtil.null2String(store_name) + "%"), "like");
		}
		IPageList pList = this.storeDepositLogService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("store_name", store_name);
		return mv;
	}

	/**
	 * storedepositlog添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "店铺保证金调整", value = "/admin/deposit_adjust.htm*", rtype = "admin", rname = "店铺保证金", rcode = "admin_store_deposit", rgroup = "店铺")
	@RequestMapping("/admin/deposit_adjust.htm")
	public ModelAndView deposit_adjust(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/deposit_adjust.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@SecurityMapping(title = "店铺保证金获取", value = "/admin/verify_user_deposit.htm*", rtype = "admin", rname = "店铺保证金", rcode = "admin_store_deposit", rgroup = "店铺")
	@RequestMapping("/admin/verify_user_deposit.htm")
	public void verify_user_deposit(HttpServletRequest request,
			HttpServletResponse response, String userName) {
		User user = this.userService.getObjByProperty("userName", userName);
		int ret = -1;
		if (user != null) {
			ret = user.getStore_deposit();
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

	/**
	 * storedepositlog保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "店铺保证金调整保存", value = "/admin/deposit_adjust_save.htm*", rtype = "admin", rname = "店铺保证金", rcode = "admin_store_deposit", rgroup = "店铺")
	@RequestMapping("/admin/deposit_adjust_save.htm")
	public ModelAndView deposit_adjust_save(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String userName,
			String operate_type, String amount, String content) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjByProperty("userName", userName);
		if (operate_type.equals("2")) {
			user.setStore_deposit(user.getStore_deposit()
					+ CommUtil.null2Int(amount));
		} else {
			if (user.getStore_deposit() > CommUtil.null2Int(amount)) {
				user.setStore_deposit(user.getStore_deposit()
						- CommUtil.null2Int(amount));
			} else {
				user.setStore_deposit(0);
				user.setStore_deposit_status(0);
			}
		}
		this.userService.update(user);
		StoreDepositLog log = new StoreDepositLog();
		log.setAddTime(new Date());
		log.setDp_content(content);
		if (operate_type.equals("2")) {
			log.setDp_amount(CommUtil.null2Int(amount));
		} else {
			log.setDp_amount(-CommUtil.null2Int(amount));
		}
		log.setDp_type(CommUtil.null2Int(operate_type));
		log.setDp_op_time(new Date());
		User admin = SecurityUserHolder.getCurrentUser();
		log.setDp_op_user_id(admin.getId());
		log.setDp_op_user_name(admin.getUsername());
		log.setDp_store_name(user.getStore().getStore_name());
		log.setDp_store_open_time(user.getStore().getAddTime());
		log.setDp_store_ower(user.getStore().getStore_ower());
		log.setDp_user_id(user.getId());
		log.setDp_grade_name(user.getStore().getGrade().getGradeName());
		log.setDp_payment("管理员操作");
		log.setDp_status(10);
		this.storeDepositLogService.save(log);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/deposit_list.htm");
		mv.addObject("op_title", "操作店铺保证金成功");
		return mv;
	}

	@SecurityMapping(title = "店铺保证金", value = "/admin/deposit_log_list.htm*", rtype = "admin", rname = "店铺保证金", rcode = "admin_store_deposit", rgroup = "店铺")
	@RequestMapping("/admin/deposit_log_del.htm")
	public String deposit_log_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				StoreDepositLog log = this.storeDepositLogService
						.getObjById(CommUtil.null2Long(id));
				if (log.getDp_status() != 10) {// 只有未成功缴纳的才可以删除
					User user = this.userService
							.getObjById(log.getDp_user_id());
					user.setStore_deposit_status(0);
					user.setStore_deposit(0);
					this.userService.update(user);
					this.storeDepositLogService.delete(Long.parseLong(id));
				}
			}
		}
		return "redirect:deposit_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "店铺保证金审核", value = "/admin/deposit_log_audit.htm*", rtype = "admin", rname = "店铺保证金", rcode = "admin_store_deposit", rgroup = "店铺")
	@RequestMapping("/admin/deposit_log_audit.htm")
	public String deposit_log_audit(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		User user = SecurityUserHolder.getCurrentUser();
		for (String id : ids) {
			if (!id.equals("")) {
				StoreDepositLog log = this.storeDepositLogService
						.getObjById(CommUtil.null2Long(id));
				log.setDp_status(10);
				log.setDp_op_user_id(user.getId());
				log.setDp_op_user_name(user.getUsername());
				log.setDp_op_time(new Date());
				this.storeDepositLogService.update(log);
				User store_user = this.userService.getObjById(log
						.getDp_user_id());
				store_user.setStore_deposit_status(10);
				store_user.setStore_deposit(log.getDp_amount());
				this.userService.update(store_user);
			}
		}
		return "redirect:deposit_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "店铺保证金审核拒绝", value = "/admin/deposit_log_refuse.htm*", rtype = "admin", rname = "店铺保证金", rcode = "admin_store_deposit", rgroup = "店铺")
	@RequestMapping("/admin/deposit_log_refuse.htm")
	public String deposit_log_refuse(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		User user = SecurityUserHolder.getCurrentUser();
		for (String id : ids) {
			if (!id.equals("")) {
				StoreDepositLog log = this.storeDepositLogService
						.getObjById(CommUtil.null2Long(id));
				if (log.getDp_status() != 10) {// 已经审核通过的无法拒绝
					log.setDp_status(-1);
					log.setDp_op_user_id(user.getId());
					log.setDp_op_user_name(user.getUsername());
					log.setDp_op_time(new Date());
					this.storeDepositLogService.update(log);
					User store_user = this.userService.getObjById(log
							.getDp_user_id());
					store_user.setStore_deposit_status(0);
					store_user.setStore_deposit(0);
					this.userService.update(store_user);
				}
			}
		}
		return "redirect:deposit_list.htm?currentPage=" + currentPage;
	}
}