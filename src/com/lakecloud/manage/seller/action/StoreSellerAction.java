package com.lakecloud.manage.seller.action;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.domain.virtual.SysMap;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.QRCodeEncoderHandler;
import com.lakecloud.core.tools.WebForm;
import com.lakecloud.foundation.domain.Accessory;
import com.lakecloud.foundation.domain.Area;
import com.lakecloud.foundation.domain.Payment;
import com.lakecloud.foundation.domain.PredepositLog;
import com.lakecloud.foundation.domain.Role;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.StoreClass;
import com.lakecloud.foundation.domain.StoreDepositLog;
import com.lakecloud.foundation.domain.StoreGrade;
import com.lakecloud.foundation.domain.StoreGradeLog;
import com.lakecloud.foundation.domain.StorePoint;
import com.lakecloud.foundation.domain.StoreSlide;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.domain.query.StoreDepositLogQueryObject;
import com.lakecloud.foundation.service.IAccessoryService;
import com.lakecloud.foundation.service.IAreaService;
import com.lakecloud.foundation.service.IPaymentService;
import com.lakecloud.foundation.service.IPredepositLogService;
import com.lakecloud.foundation.service.IRoleService;
import com.lakecloud.foundation.service.IStoreClassService;
import com.lakecloud.foundation.service.IStoreDepositLogService;
import com.lakecloud.foundation.service.IStoreGradeLogService;
import com.lakecloud.foundation.service.IStoreGradeService;
import com.lakecloud.foundation.service.IStorePointService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.IStoreSlideService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.pay.tools.PayTools;
import com.lakecloud.view.web.tools.AreaViewTools;
import com.lakecloud.view.web.tools.StoreViewTools;

/**
 * 
 * <p>
 * Title: StoreSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 卖家中心店铺控制器
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
public class StoreSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IStoreGradeService storeGradeService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IStoreClassService storeClassService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IStoreGradeLogService storeGradeLogService;
	@Autowired
	private IStoreSlideService storeSlideService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IStoreDepositLogService storeDepositLogService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private StoreViewTools storeTools;
	@Autowired
	private IStorePointService storepointService;
	@Autowired
	private AreaViewTools areaViewTools;
	@Autowired
	private PayTools payTools;

	/**
	 * 卖家第一次申请开店控制器
	 * 
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "申请店铺第一步", value = "/seller/store_create_first.htm*", rtype = "buyer", rname = "申请店铺", rcode = "create_store", rgroup = "申请店铺")
	@RequestMapping("/seller/store_create_first.htm")
	public ModelAndView store_create_first(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = null;
		int store_status = 0;
		Store store = this.storeService.getObjByProperty("user.id",
				SecurityUserHolder.getCurrentUser().getId());
		if (store != null) {
			store_status = store.getStore_status();
		}
		if (this.configService.getSysConfig().isStore_allow()) {
			if (store_status == 0) {
				mv = new JModelAndView("store_create_first.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				List<StoreGrade> sgs = this.storeGradeService
						.query("select obj from StoreGrade obj order by obj.sequence asc",
								null, -1, -1);
				mv.addObject("sgs", sgs);
				mv.addObject("storeTools", storeTools);
			}
			if (store_status == 1) {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "您的店铺正在审核中");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			}
			if (store_status == 2) {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "您已经开通店铺");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			}
			if (store_status == 3) {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "您的店铺已经被关闭");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统暂时关闭了申请店铺");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "申请店铺第二步", value = "/seller/store_create_second.htm*", rtype = "buyer", rname = "申请店铺", rcode = "create_store", rgroup = "申请店铺")
	@RequestMapping("/seller/store_create_second.htm")
	public ModelAndView store_create_second(HttpServletRequest request,
			HttpServletResponse response, String grade_id) {
		ModelAndView mv = null;
		Store store = this.storeService.getObjByProperty("user.id",
				SecurityUserHolder.getCurrentUser().getId());
		int store_status = store == null ? 0 : store.getStore_status();
		if (this.configService.getSysConfig().isStore_allow()) {
			if (grade_id == null || grade_id.equals("")) {
				mv = new JModelAndView("store_create_first.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				List<StoreGrade> sgs = this.storeGradeService
						.query("select obj from StoreGrade obj order by obj.sequence asc",
								null, -1, -1);
				mv.addObject("sgs", sgs);
				mv.addObject("storeTools", storeTools);
			} else {
				if (store_status == 0) {
					mv = new JModelAndView("store_create_second.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					List<Area> areas = this.areaService
							.query("select obj from Area obj where obj.parent.id is null",
									null, -1, -1);
					List<StoreClass> scs = this.storeClassService
							.query("select obj from StoreClass obj where obj.parent.id is null",
									null, -1, -1);
					String store_create_session = CommUtil.randomString(32);
					request.getSession(false).setAttribute(
							"store_create_session", store_create_session);
					mv.addObject("store_create_session", store_create_session);
					mv.addObject("scs", scs);
					mv.addObject("areas", areas);
					mv.addObject("grade_id", grade_id);
				}
				if (store_status == 1) {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "您的店铺正在审核中");
					mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
				}
				if (store_status == 2) {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "您已经开通店铺");
					mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
				}
				if (store_status == 3) {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "您的店铺已经被关闭");
					mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
				}
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统暂时关闭了申请店铺");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "申请店铺完成", value = "/seller/store_create_finish.htm*", rtype = "buyer", rname = "申请店铺", rcode = "create_store", rgroup = "申请店铺")
	@RequestMapping("/seller/store_create_finish.htm")
	public ModelAndView store_create_finish(HttpServletRequest request,
			HttpServletResponse response, String sc_id, String grade_id,
			String area_id, String store_create_session) {
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String store_create_session1 = CommUtil.null2String(request.getSession(
				false).getAttribute("store_create_session"));
		if (!store_create_session1.equals("")
				&& store_create_session1.equals(store_create_session)) {
			Store user_store = this.storeService.getObjByProperty("user.id",
					SecurityUserHolder.getCurrentUser().getId());
			int store_status = user_store == null ? 0 : user_store
					.getStore_status();
			if (store_status == 0) {
				WebForm wf = new WebForm();
				Store store = wf.toPo(request, Store.class);
				StoreClass sc = this.storeClassService.getObjById(Long
						.parseLong(sc_id));
				store.setSc(sc);
				StoreGrade grade = this.storeGradeService.getObjById(Long
						.parseLong(grade_id));
				store.setGrade(grade);
				Area area = this.areaService
						.getObjById(Long.parseLong(area_id));
				store.setArea(area);
				store.setTemplate("default");
				store.setAddTime(new Date());
				store.setStore_second_domain("shop"
						+ SecurityUserHolder.getCurrentUser().getId()
								.toString());
				this.storeService.save(store);
				// 根据商城图片上传规则创建用户存放图片的文件夹
				String path = request.getSession().getServletContext()
						.getRealPath("/")
						+ File.separator
						+ "upload"
						+ File.separator
						+ "store"
						+ File.separator + store.getId();
				CommUtil.createFolder(path);
				// 生成店铺二维码
				String store_url = CommUtil.getURL(request) + "/store_"
						+ store.getId() + ".htm";
				QRCodeEncoderHandler handler = new QRCodeEncoderHandler();
				handler.encoderQRCode(store_url, path + "/code.png");
				User user = this.userService.getObjById(SecurityUserHolder
						.getCurrentUser().getId());
				user.setStore(store);
				if (store.getGrade().isAudit()) {
					store.setStore_status(1);// 待审核
				} else {
					store.setStore_status(2);// 直接通过,通过后直接生成storepoint，防止定时器执行时产生多个对应一个store的storepoint
					StorePoint point = new StorePoint();
					point.setStore(store);
					this.storepointService.save(point);
				}
				if (user.getUserRole().equals("BUYER")) {
					user.setUserRole("BUYER_SELLER");
				}
				if (user.getUserRole().equals("ADMIN")) {
					user.setUserRole("ADMIN_BUYER_SELLER");
				}
				// 给用户赋予卖家权限
				Map params = new HashMap();
				params.put("type", "SELLER");
				List<Role> roles = this.roleService.query(
						"select obj from Role obj where obj.type=:type",
						params, -1, -1);
				user.getRoles().addAll(roles);
				this.userService.update(user);
				// 重新加载用户权限
				Authentication authentication = new UsernamePasswordAuthenticationToken(
						SecurityContextHolder.getContext().getAuthentication()
								.getPrincipal(), SecurityContextHolder
								.getContext().getAuthentication()
								.getCredentials(),
						user.get_common_Authorities());
				SecurityContextHolder.getContext().setAuthentication(
						authentication);
				mv.addObject("op_title", "店铺申请成功");
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				if (store_status == 1) {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "您的店铺正在审核中");
					mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
				}
				if (store_status == 2) {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "您已经开通店铺");
					mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
				}
				if (store_status == 3) {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "您的店铺已经被关闭");
					mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
				}
			}
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
			request.getSession(false).removeAttribute("store_create_session");
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "表单已经失效");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "店铺设置", value = "/seller/store_set.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
	@RequestMapping("/seller/store_set.htm")
	public ModelAndView store_set(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/store_set.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Store store = this.storeService.getObjByProperty("user.id",
				SecurityUserHolder.getCurrentUser().getId());
		mv.addObject("store", store);
		mv.addObject("areaViewTools", areaViewTools);
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		mv.addObject("areas", areas);
		return mv;
	}

	@SecurityMapping(title = "店铺设置保存", value = "/seller/store_set_save.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
	@RequestMapping("/seller/store_set_save.htm")
	public ModelAndView store_set_save(HttpServletRequest request,
			HttpServletResponse response, String area_id,
			String store_second_domain) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Store store = this.storeService.getObjByProperty("user.id",
				SecurityUserHolder.getCurrentUser().getId());
		WebForm wf = new WebForm();
		wf.toPo(request, store);
		// 图片上传开始logo
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + "/store_logo";
		Map map = new HashMap();
		try {
			String fileName = store.getStore_logo() == null ? "" : store
					.getStore_logo().getName();
			map = CommUtil.saveFileToServer(request, "logo", saveFilePathName,
					fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory store_logo = new Accessory();
					store_logo
							.setName(CommUtil.null2String(map.get("fileName")));
					store_logo.setExt(CommUtil.null2String(map.get("mime")));
					store_logo
							.setSize(CommUtil.null2Float(map.get("fileSize")));
					store_logo.setPath(uploadFilePath + "/store_logo");
					store_logo.setWidth(CommUtil.null2Int(map.get("width")));
					store_logo.setHeight(CommUtil.null2Int(map.get("height")));
					store_logo.setAddTime(new Date());
					this.accessoryService.save(store_logo);
					store.setStore_logo(store_logo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory store_logo = store.getStore_logo();
					store_logo
							.setName(CommUtil.null2String(map.get("fileName")));
					store_logo.setExt(CommUtil.null2String(map.get("mime")));
					store_logo
							.setSize(CommUtil.null2Float(map.get("fileSize")));
					store_logo.setPath(uploadFilePath + "/store_logo");
					store_logo.setWidth(CommUtil.null2Int(map.get("width")));
					store_logo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(store_logo);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
		saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ this.configService.getSysConfig().getUploadFilePath()
				+ "/store_banner";
		try {
			String fileName = store.getStore_banner() == null ? "" : store
					.getStore_banner().getName();
			map = CommUtil.saveFileToServer(request, "banner",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory store_banner = new Accessory();
					store_banner.setName(CommUtil.null2String(map
							.get("fileName")));
					store_banner.setExt(CommUtil.null2String(map.get("mime")));
					store_banner.setSize(CommUtil.null2Float(map
							.get("fileSize")));
					store_banner.setPath(uploadFilePath + "/store_banner");
					store_banner.setWidth(CommUtil.null2Int(map.get("width")));
					store_banner
							.setHeight(CommUtil.null2Int(map.get("height")));
					store_banner.setAddTime(new Date());
					this.accessoryService.save(store_banner);
					store.setStore_banner(store_banner);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory store_banner = store.getStore_banner();
					store_banner.setName(CommUtil.null2String(map
							.get("fileName")));
					store_banner.setExt(CommUtil.null2String(map.get("mime")));
					store_banner.setSize(CommUtil.null2Float(map
							.get("fileSize")));
					store_banner.setPath(uploadFilePath + "/store_banner");
					store_banner.setWidth(CommUtil.null2Int(map.get("width")));
					store_banner
							.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(store_banner);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
		store.setArea(area);
		if (this.configService.getSysConfig().isSecond_domain_open()
				&& this.configService.getSysConfig().getDomain_allow_count() > store
						.getDomain_modify_count()) {
			if (!CommUtil.null2String(store_second_domain).equals("")
					&& !store_second_domain.equals(store
							.getStore_second_domain())) {
				store.setStore_second_domain(store_second_domain);
				store.setDomain_modify_count(store.getDomain_modify_count() + 1);
			}
		}
		this.storeService.update(store);
		mv.addObject("op_title", "店铺设置成功");
		mv.addObject("url", CommUtil.getURL(request) + "/seller/store_set.htm");
		return mv;
	}

	@SecurityMapping(title = "店铺地图", value = "/seller/store_map.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
	@RequestMapping("/seller/store_map.htm")
	public ModelAndView store_map(HttpServletRequest request,
			HttpServletResponse response, String map_type) {
		Store store = this.storeService.getObjByProperty("user.id",
				SecurityUserHolder.getCurrentUser().getId());
		if (CommUtil.null2String(map_type).equals("")) {
			if (store.getMap_type() != null && !store.getMap_type().equals("")) {
				map_type = store.getMap_type();
			} else {
				map_type = "baidu";
			}

		}
		ModelAndView mv = new JModelAndView("user/default/usercenter/store_"
				+ map_type + "_map.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("store", store);
		return mv;
	}

	@SecurityMapping(title = "店铺地图保存", value = "/seller/store_map_save.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
	@RequestMapping("/seller/store_map_save.htm")
	public ModelAndView store_map_save(HttpServletRequest request,
			HttpServletResponse response, String store_lat, String store_lng,
			String map_type) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Store store = this.storeService.getObjByProperty("user.id",
				SecurityUserHolder.getCurrentUser().getId());
		store.setStore_lat(BigDecimal.valueOf(CommUtil.null2Double(store_lat)));
		store.setStore_lng(BigDecimal.valueOf(CommUtil.null2Double(store_lng)));
		store.setMap_type(map_type);
		this.storeService.update(store);
		mv.addObject("op_title", "店铺设置成功");
		mv.addObject("url", CommUtil.getURL(request) + "/seller/store_map.htm");
		return mv;
	}

	@SecurityMapping(title = "主题设置", value = "/seller/store_theme.htm*", rtype = "seller", rname = "主题设置", rcode = "store_theme_seller", rgroup = "店铺设置")
	@RequestMapping("/seller/store_theme.htm")
	public ModelAndView store_theme(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/store_theme.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Store store = this.storeService.getObjByProperty("user.id",
				SecurityUserHolder.getCurrentUser().getId());
		mv.addObject("store", store);
		return mv;
	}

	@SecurityMapping(title = "主题设置", value = "/seller/store_theme_save.htm*", rtype = "seller", rname = "主题设置", rcode = "store_theme_seller", rgroup = "店铺设置")
	@RequestMapping("/seller/store_theme_save.htm")
	public String store_theme_set(HttpServletRequest request,
			HttpServletResponse response, String theme) {
		Store store = this.storeService.getObjByProperty("user.id",
				SecurityUserHolder.getCurrentUser().getId());
		if (store.getGrade().getTemplates().indexOf(theme) >= 0) {
			store.setTemplate(theme);
			this.storeService.update(store);
		}
		return "redirect:store_theme.htm";
	}

	@SecurityMapping(title = "店铺认证", value = "/seller/store_approve.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
	@RequestMapping("/seller/store_approve.htm")
	public ModelAndView store_approve(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/store_approve.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Store store = this.storeService.getObjByProperty("user.id",
				SecurityUserHolder.getCurrentUser().getId());
		mv.addObject("store", store);
		return mv;
	}

	@SecurityMapping(title = "店铺认证保存", value = "/seller/store_approve_save.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
	@RequestMapping("/seller/store_approve_save.htm")
	public String store_approve_save(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/store_approve.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Store store = this.storeService.getObjByProperty("user.id",
				SecurityUserHolder.getCurrentUser().getId());
		// 图片上传开始logo
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath;
		Map map = new HashMap();
		try {
			String fileName = store.getCard() == null ? "" : store.getCard()
					.getName();
			map = CommUtil.saveFileToServer(request, "card_img",
					saveFilePathName + File.separator + "card", fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory card = new Accessory();
					card.setName(CommUtil.null2String(map.get("fileName")));
					card.setExt(CommUtil.null2String(map.get("mime")));
					card.setSize(CommUtil.null2Float(map.get("fileSize")));
					card.setPath(uploadFilePath + "/card");
					card.setWidth(CommUtil.null2Int(map.get("width")));
					card.setHeight(CommUtil.null2Int(map.get("height")));
					card.setAddTime(new Date());
					this.accessoryService.save(card);
					store.setCard(card);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory card = store.getCard();
					card.setName(CommUtil.null2String(map.get("fileName")));
					card.setExt(CommUtil.null2String(map.get("mime")));
					card.setSize(CommUtil.null2Float(map.get("fileSize")));
					card.setPath(uploadFilePath + "/card");
					card.setWidth(CommUtil.null2Int(map.get("width")));
					card.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(card);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
		try {
			String fileName = store.getStore_license() == null ? "" : store
					.getStore_license().getName();
			map = CommUtil.saveFileToServer(request, "license_img",
					saveFilePathName + File.separator + "license", fileName,
					null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory store_license = new Accessory();
					store_license.setName(CommUtil.null2String(map
							.get("fileName")));
					store_license.setExt(CommUtil.null2String(map.get("mime")));
					store_license.setSize(CommUtil.null2Float(map
							.get("fileSize")));
					store_license.setPath(uploadFilePath + "/license");
					store_license.setWidth(CommUtil.null2Int(map.get("width")));
					store_license
							.setHeight(CommUtil.null2Int(map.get("height")));
					store_license.setAddTime(new Date());
					this.accessoryService.save(store_license);
					store.setStore_license(store_license);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory store_license = store.getStore_license();
					store_license.setName(CommUtil.null2String(map
							.get("fileName")));
					store_license.setExt(CommUtil.null2String(map.get("mime")));
					store_license.setSize(CommUtil.null2Float(map
							.get("fileSize")));
					store_license.setPath(uploadFilePath + "/license");
					store_license.setWidth(CommUtil.null2Int(map.get("width")));
					store_license
							.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(store_license);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.storeService.update(store);
		return "redirect:store_approve.htm";
	}

	@SecurityMapping(title = "店铺升级", value = "/seller/store_grade.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
	@RequestMapping("/seller/store_grade.htm")
	public ModelAndView store_grade(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/store_grade.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		Store store = user.getStore();
		if (store.getUpdate_grade() == null) {
			List<StoreGrade> sgs = this.storeGradeService.query(
					"select obj from StoreGrade obj order by obj.sequence asc",
					null, -1, -1);
			mv.addObject("sgs", sgs);
			mv.addObject("store", store);
		} else {
			mv = new JModelAndView("user/default/usercenter/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您的店铺升级正在审核中");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/seller/store_set.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "店铺升级申请完成", value = "/seller/store_grade_finish.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
	@RequestMapping("/seller/store_grade_finish.htm")
	public ModelAndView store_grade_finish(HttpServletRequest request,
			HttpServletResponse response, String grade_id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Store store = this.storeService.getObjByProperty("user.id",
				SecurityUserHolder.getCurrentUser().getId());
		// store.setStore_grade_status(1);
		store.setUpdate_grade(this.storeGradeService.getObjById(CommUtil
				.null2Long(grade_id)));
		this.storeService.update(store);
		StoreGradeLog grade_log = new StoreGradeLog();
		grade_log.setAddTime(new Date());
		grade_log.setStore(store);
		this.storeGradeLogService.save(grade_log);
		mv.addObject("op_title", "申请提交成功");
		mv.addObject("url", CommUtil.getURL(request) + "/seller/store_set.htm");
		return mv;
	}

	@SecurityMapping(title = "店铺幻灯", value = "/seller/store_slide.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
	@RequestMapping("/seller/store_slide.htm")
	public ModelAndView store_slide(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/store_slide.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Store store = this.storeService.getObjByProperty("user.id",
				SecurityUserHolder.getCurrentUser().getId());
		mv.addObject("store", store);
		return mv;
	}

	@SecurityMapping(title = "店铺幻灯保存", value = "/seller/store_slide_save.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
	@RequestMapping("/seller/store_slide_save.htm")
	public ModelAndView store_slide_save(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/store_slide.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Store store = this.storeService.getObjByProperty("user.id",
				SecurityUserHolder.getCurrentUser().getId());
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "store_slide";
		for (int i = 1; i <= 5; i++) {
			Map map = new HashMap();
			String fileName = "";
			StoreSlide slide = null;
			if (store.getSlides().size() >= i) {
				fileName = store.getSlides().get(i - 1).getAcc().getName();
				slide = store.getSlides().get(i - 1);
			}
			try {
				map = CommUtil.saveFileToServer(request, "acc" + i,
						saveFilePathName, fileName, null);
				Accessory acc = null;
				if (fileName.equals("")) {
					if (map.get("fileName") != "") {
						acc = new Accessory();
						acc.setName(CommUtil.null2String(map.get("fileName")));
						acc.setExt(CommUtil.null2String(map.get("mime")));
						acc.setSize(CommUtil.null2Float(map.get("fileSize")));
						acc.setPath(uploadFilePath + "/store_slide");
						acc.setWidth(CommUtil.null2Int(map.get("width")));
						acc.setHeight(CommUtil.null2Int(map.get("height")));
						acc.setAddTime(new Date());
						this.accessoryService.save(acc);
					}
				} else {
					if (map.get("fileName") != "") {
						acc = slide.getAcc();
						acc.setName(CommUtil.null2String(map.get("fileName")));
						acc.setExt(CommUtil.null2String(map.get("mime")));
						acc.setSize(CommUtil.null2Float(map.get("fileSize")));
						acc.setPath(uploadFilePath + "/store_slide");
						acc.setWidth(CommUtil.null2Int(map.get("width")));
						acc.setHeight(CommUtil.null2Int(map.get("height")));
						acc.setAddTime(new Date());
						this.accessoryService.update(acc);
					}
				}
				if (acc != null) {
					if (slide == null) {
						slide = new StoreSlide();
						slide.setAcc(acc);
						slide.setAddTime(new Date());
						slide.setStore(store);
					}
					slide.setUrl(request.getParameter("acc_url" + i));
					if (slide == null) {
						this.storeSlideService.save(slide);
					} else {
						this.storeSlideService.update(slide);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mv.addObject("store", store);
		return mv;
	}

	@SecurityMapping(title = "店铺保证金缴纳", value = "/seller/store_deposit.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
	@RequestMapping("/seller/store_deposit.htm")
	public ModelAndView store_deposit(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/store_deposit.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		Store store = user.getStore();
		Map params = new HashMap();
		params.put("type", "admin");
		params.put("install", true);
		params.put("mark", "alipay_wap");
		params.put("mark2", "weixin");
		List<Payment> payments = this.paymentService
				.query("select obj from Payment obj where obj.type=:type and obj.mark!=:mark and obj.mark!=:mark2 and obj.install=:install",
						params, -1, -1);
		String deposit_session = CommUtil.randomString(32);
		request.getSession(false).setAttribute("deposit_session",
				deposit_session);
		mv.addObject("deposit_session", deposit_session);
		mv.addObject("payments", payments);
		mv.addObject("store", store);
		mv.addObject("user", user);
		return mv;
	}

	@SecurityMapping(title = "店铺保证金缴纳保存", value = "/seller/store_deposit_save.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
	@RequestMapping("/seller/store_deposit_save.htm")
	public ModelAndView store_deposit_save(HttpServletRequest request,
			HttpServletResponse response, String id, String dp_payment,
			String deposit_session) {
		ModelAndView mv = new JModelAndView("line_pay.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String dp_session = CommUtil.null2String(request.getSession(false)
				.getAttribute("deposit_session"));
		if (!dp_session.equals("") && dp_session.equals(deposit_session)) {
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			if (dp_payment.equals("outline")) {
				StoreDepositLog log = new StoreDepositLog();
				String dp_no = UUID.randomUUID().toString().replace("-", "");
				log.setDp_no(dp_no);
				log.setAddTime(new Date());
				log.setDp_store_name(user.getStore().getStore_name());
				log.setDp_payment("线下支付");
				log.setDp_amount(user.getStore().getGrade().getStore_deposit());
				log.setDp_store_name(user.getStore().getStore_name());
				log.setDp_store_ower(user.getStore().getStore_ower());
				log.setDp_grade_name(user.getStore().getGrade().getGradeName());
				log.setDp_user_id(SecurityUserHolder.getCurrentUser().getId());
				log.setDp_type(0);
				log.setDp_payment_mark("outline");
				log.setDp_store_open_time(user.getStore().getAddTime());
				this.storeDepositLogService.save(log);
				user.setStore_deposit_status(5);
				this.userService.update(user);
				mv = new JModelAndView("success.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "线下支付提交成功，等待审核");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/store_deposit.htm");
			} else if (dp_payment.equals("balance")) {
				double balance = CommUtil.null2Double(user
						.getAvailableBalance());
				if (balance > user.getStore().getGrade().getStore_deposit()) {
					int store_deposit = user.getStore().getGrade()
							.getStore_deposit();
					user.setStore_deposit(store_deposit);
					user.setAvailableBalance(BigDecimal.valueOf(CommUtil
							.subtract(user.getAvailableBalance(), store_deposit)));// 减少用户预存款
					user.setStore_deposit_status(10);
					this.userService.update(user);
					// 记录缴费日志
					StoreDepositLog log = new StoreDepositLog();
					String dp_no = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
					log.setDp_no(dp_no);
					log.setAddTime(new Date());
					log.setDp_store_name(user.getStore().getStore_name());
					log.setDp_payment("预存款支付");
					log.setDp_amount(user.getStore().getGrade()
							.getStore_deposit());
					log.setDp_store_name(user.getStore().getStore_name());
					log.setDp_store_ower(user.getStore().getStore_ower());
					log.setDp_grade_name(user.getStore().getGrade()
							.getGradeName());
					log.setDp_user_id(SecurityUserHolder.getCurrentUser()
							.getId());
					log.setDp_type(0);
					log.setDp_payment_mark("balance");
					log.setDp_store_open_time(user.getStore().getAddTime());
					this.storeDepositLogService.save(log);
					// 记录预付款日志
					PredepositLog pre_log = new PredepositLog();
					pre_log.setAddTime(new Date());
					pre_log.setPd_log_user(user);
					pre_log.setPd_op_type("缴纳店铺保证金");
					pre_log.setPd_log_amount(BigDecimal.valueOf(-store_deposit));
					pre_log.setPd_log_info("缴纳店铺保证金物减少可用预存款");
					pre_log.setPd_type("可用预存款");
					this.predepositLogService.save(pre_log);
					// 记录店铺保证金缴纳操作日志
					mv = new JModelAndView("success.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "缴纳店铺保证金成功");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/seller/store_deposit.htm");
				} else {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "预存款金额不足");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/seller/store_deposit.htm");
				}
			} else {
				StoreDepositLog log = new StoreDepositLog();
				String dp_no = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
				log.setDp_no(dp_no);
				log.setAddTime(new Date());
				log.setDp_store_name(user.getStore().getStore_name());
				if (dp_payment.equals("alipay")) {
					log.setDp_payment("支付宝");
				}
				if (dp_payment.equals("chinabank")) {
					log.setDp_payment("网银在线");
				}
				if (dp_payment.equals("paypal")) {
					log.setDp_payment("paypal");
				}
				if (dp_payment.equals("bill")) {
					log.setDp_payment("快钱");
				}
				if (dp_payment.equals("tenpay")) {
					log.setDp_payment("财付通");
				}
				log.setDp_payment_mark(dp_payment);
				log.setDp_amount(user.getStore().getGrade().getStore_deposit());
				log.setDp_store_name(user.getStore().getStore_name());
				log.setDp_store_ower(user.getStore().getStore_ower());
				log.setDp_grade_name(user.getStore().getGrade().getGradeName());
				log.setDp_user_id(SecurityUserHolder.getCurrentUser().getId());
				log.setDp_type(0);
				log.setDp_store_open_time(user.getStore().getAddTime());
				this.storeDepositLogService.save(log);

				mv.addObject("payType", dp_payment);
				mv.addObject("type", "store_deposit");
				mv.addObject("url", CommUtil.getURL(request));
				mv.addObject("payTools", payTools);
				mv.addObject("deposit_id", log.getId());
				Map params = new HashMap();
				params.put("install", true);
				params.put("mark", dp_payment);
				params.put("type", "admin");
				List<Payment> payments = this.paymentService
						.query("select obj from Payment obj where obj.install=:install and obj.mark=:mark and obj.type=:type",
								params, -1, -1);
				mv.addObject("payment_id", payments.size() > 0 ? payments
						.get(0).getId() : new Payment());
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，请重新发起请求");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "店铺保证金缴纳", value = "/seller/store_deposit_log.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
	@RequestMapping("/seller/store_deposit_log.htm")
	public ModelAndView store_deposit_log(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/store_deposit_log.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		StoreDepositLogQueryObject qo = new StoreDepositLogQueryObject(
				currentPage, mv, orderBy, orderType);
		qo.addQuery("obj.dp_user_id", new SysMap("user_id", SecurityUserHolder
				.getCurrentUser().getId()), "=");
		qo.setPageSize(30);
		IPageList pList = this.storeDepositLogService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
}
