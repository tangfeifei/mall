package com.lakecloud.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
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
import com.lakecloud.core.tools.Md5Encrypt;
import com.lakecloud.foundation.domain.Role;
import com.lakecloud.foundation.domain.RoleGroup;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.domain.query.UserQueryObject;
import com.lakecloud.foundation.service.IEvaluateService;
import com.lakecloud.foundation.service.IGoodsCartService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IOrderFormLogService;
import com.lakecloud.foundation.service.IOrderFormService;
import com.lakecloud.foundation.service.IRoleGroupService;
import com.lakecloud.foundation.service.IRoleService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;

/**
 * @info 卖家子账户管理，卖家根据店铺等级，可以有多个子账户，子账户可以协助卖家管理店铺，卖家自行添加子账户信息，并可以自行给子账户赋予相关卖家中心权限
 
 * 
 */
@Controller
public class SubAccountSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IRoleGroupService roleGroupService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IGoodsService goodsService;

	@SecurityMapping(title = "子账户列表", value = "/seller/sub_account_list.htm*", rtype = "seller", rname = "子账户管理", rcode = "sub_account_seller", rgroup = "店铺设置")
	@RequestMapping("/seller/sub_account_list.htm")
	public ModelAndView sub_account_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/sub_account_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Store store = this.userService.getObjById(
				SecurityUserHolder.getCurrentUser().getId()).getStore();
		mv.addObject("store", store);
		UserQueryObject uqo = new UserQueryObject(currentPage, mv, orderBy,
				orderType);
		uqo.addQuery("obj.parent.id", new SysMap("user_ids", SecurityUserHolder
				.getCurrentUser().getId()), "=");
		IPageList pList = this.userService.list(uqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	@SecurityMapping(title = "子账户添加", value = "/seller/sub_account_add.htm*", rtype = "seller", rname = "子账户管理", rcode = "sub_account_seller", rgroup = "店铺设置")
	@RequestMapping("/seller/sub_account_add.htm")
	public ModelAndView sub_account_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/sub_account_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		Store store = user.getStore();
		if (store == null) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您尚未开设店铺");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}

		if (user.getChilds().size() >= store.getGrade().getAcount_num()) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您的店铺等级不能继续添加子账户,请升级店铺等级");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/seller/store_grade.htm");
		}
		mv.addObject("store", store);
		Map params = new HashMap();
		params.put("type", "SELLER");
		List<RoleGroup> rgs = this.roleGroupService
				.query("select obj from RoleGroup obj where obj.type=:type order by obj.addTime asc",
						params, -1, -1);
		mv.addObject("rgs", rgs);
		return mv;
	}

	@SecurityMapping(title = "子账户编辑", value = "/seller/sub_account_edit.htm*", rtype = "seller", rname = "子账户管理", rcode = "sub_account_seller", rgroup = "店铺设置")
	@RequestMapping("/seller/sub_account_edit.htm")
	public ModelAndView sub_account_edit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/sub_account_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		Store store = user.getStore();
		if (store == null) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您尚未开设店铺");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		mv.addObject("store", store);
		Map params = new HashMap();
		params.put("type", "SELLER");
		List<RoleGroup> rgs = this.roleGroupService
				.query("select obj from RoleGroup obj where obj.type=:type order by obj.addTime asc",
						params, -1, -1);
		mv.addObject("rgs", rgs);
		mv.addObject("obj", this.userService.getObjById(CommUtil.null2Long(id)));
		return mv;
	}

	/**
	 * 过滤名称中的html代码
	 * 
	 * @param inputString
	 * @return
	 */
	private String clearContent(String inputString) {
		String htmlStr = inputString; // 含html标签的字符串
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;

		java.util.regex.Pattern p_html1;
		java.util.regex.Matcher m_html1;

		try {
			String regEx_script = "<[//s]*?script[^>]*?>[//s//S]*?<[//s]*?///[//s]*?script[//s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[//s//S]*?<///script>
			String regEx_style = "<[//s]*?style[^>]*?>[//s//S]*?<[//s]*?///[//s]*?style[//s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[//s//S]*?<///style>
			String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
			String regEx_html1 = "<[^>]+";
			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 过滤script标签

			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤style标签

			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签

			p_html1 = Pattern.compile(regEx_html1, Pattern.CASE_INSENSITIVE);
			m_html1 = p_html1.matcher(htmlStr);
			htmlStr = m_html1.replaceAll(""); // 过滤html标签

			textStr = htmlStr;
		} catch (Exception e) {
			System.err.println("Html2Text: " + e.getMessage());
		}
		return textStr;// 返回文本字符串
	}

	@SecurityMapping(title = "子账户保存", value = "/seller/sub_account_save.htm*", rtype = "seller", rname = "子账户管理", rcode = "sub_account_seller", rgroup = "店铺设置")
	@RequestMapping("/seller/sub_account_save.htm")
	public void sub_account_save(HttpServletRequest request,
			HttpServletResponse response, String id, String userName,
			String trueName, String sex, String birthday, String QQ,
			String telephone, String mobile, String password, String role_ids) {
		boolean ret = true;
		String msg = "保存成功";
		User parent = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		Store store = parent.getStore();
		userName = this.clearContent(userName);
		if (parent.getChilds().size() >= store.getGrade().getAcount_num()) {
			ret = false;
			msg = "已经超过子账户上线";
		} else {
			if (CommUtil.null2String(id).equals("")) {
				User user = new User();
				user.setAddTime(new Date());
				user.setUserName(userName);
				user.setTrueName(trueName);
				user.setSex(CommUtil.null2Int(sex));
				user.setBirthday(CommUtil.formatDate(birthday));
				user.setQQ(QQ);
				user.setMobile(mobile);
				user.setTelephone(telephone);
				user.setParent(parent);
				user.setUserRole("BUYER_SELLER");
				user.setPassword(Md5Encrypt.md5(password).toLowerCase());
				Map params = new HashMap();
				params.put("type", "BUYER");
				List<Role> roles = this.roleService.query(
						"select obj from Role obj where obj.type=:type",
						params, -1, -1);
				user.getRoles().addAll(roles);
				for (String role_id : role_ids.split(",")) {
					if (!role_id.equals("")) {
						Role role = this.roleService.getObjById(CommUtil
								.null2Long(role_id));
						user.getRoles().add(role);
					}
				}
				ret = this.userService.save(user);
			} else {
				User user = this.userService.getObjById(CommUtil.null2Long(id));
				user.setUserName(userName);
				user.setTrueName(trueName);
				user.setSex(CommUtil.null2Int(sex));
				user.setBirthday(CommUtil.formatDate(birthday));
				user.setQQ(QQ);
				user.setMobile(mobile);
				user.setTelephone(telephone);
				user.getRoles().clear();
				Map params = new HashMap();
				params.put("type", "BUYER");
				List<Role> roles = this.roleService.query(
						"select obj from Role obj where obj.type=:type",
						params, -1, -1);
				user.getRoles().addAll(roles);
				for (String role_id : role_ids.split(",")) {
					if (!role_id.equals("")) {
						Role role = this.roleService.getObjById(CommUtil
								.null2Long(role_id));
						user.getRoles().add(role);
					}
				}
				ret = this.userService.update(user);
				msg = "更新成功";
			}
		}

		Map map = new HashMap();
		map.put("ret", ret);
		map.put("msg", msg);
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

	@SecurityMapping(title = "子账户删除", value = "/seller/sub_account_del.htm*", rtype = "seller", rname = "子账户管理", rcode = "sub_account_seller", rgroup = "店铺设置")
	@RequestMapping("/seller/sub_account_del.htm")
	public String sub_account_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId) {
		User user = this.userService.getObjById(CommUtil.null2Long(mulitId));
		user.getRoles().clear();
		this.userService.delete(user.getId());
		return "redirect:sub_account_list.htm";
	}

}
