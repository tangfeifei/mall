package com.lakecloud.manage.buyer.action;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.lakecloud.foundation.domain.Dynamic;
import com.lakecloud.foundation.domain.Favorite;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.HomePage;
import com.lakecloud.foundation.domain.HomePageGoodsClass;
import com.lakecloud.foundation.domain.OrderForm;
import com.lakecloud.foundation.domain.SnsAttention;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.domain.Visit;
import com.lakecloud.foundation.domain.query.DynamicQueryObject;
import com.lakecloud.foundation.domain.query.SnsAttentionQueryObject;
import com.lakecloud.foundation.domain.query.SnsFriendQueryObject;
import com.lakecloud.foundation.service.IDynamicService;
import com.lakecloud.foundation.service.IFavoriteService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IHomePageGoodsClassService;
import com.lakecloud.foundation.service.IHomePageService;
import com.lakecloud.foundation.service.IOrderFormService;
import com.lakecloud.foundation.service.ISnsAttentionService;
import com.lakecloud.foundation.service.ISnsFriendService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.foundation.service.IVisitService;

/**
 * 个人主页管理控制器
 * 
 * @author erikchang hz
 * 
 */
@Controller
public class HomePageBuyerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IVisitService visitService;
	@Autowired
	private IHomePageService homePageService;
	@Autowired
	private IDynamicService dynamicService;
	@Autowired
	private ISnsAttentionService attentionService;
	@Autowired
	private ISnsFriendService snsFriendService;
	@Autowired
	private IFavoriteService favoriteService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IHomePageGoodsClassService HomeGoodsClassService;

	/**
	 * 个人主页头部，页面使用$!httpInclude.include("/buyer/homepage_head.htm")标签引用方法，
	 * 
	 * @param request
	 * @param response
	 * @param dynamic_id
	 */
	@SecurityMapping(title = "个人主页头部", value = "/buyer/homepage_head.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/homepage_head.htm")
	public ModelAndView homepage_head(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/homepage_head.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String uid = request.getParameter("uid");
		User user = new User();
		if (uid != null && !uid.equals("")) {
			user = this.userService.getObjById(CommUtil.null2Long(uid));
		} else {
			user = SecurityUserHolder.getCurrentUser();
		}
		mv.addObject("owner", user);
		Map map = new HashMap();
		map.put("uid", user.getId());
		List<HomePage> HomePages = this.homePageService.query(
				"select obj from HomePage obj where obj.owner.id=:uid", map,
				-1, -1);
		if (HomePages.size() > 0) {
			mv.addObject("homePage", HomePages.get(0));
		}
		map.clear();
		map.put("uid", user.getId());
		List<SnsAttention> fans = this.attentionService.query(
				"select obj from SnsAttention obj where obj.toUser.id=:uid",
				map, -1, -1);
		map.clear();
		map.put("uid", user.getId());
		List<SnsAttention> attentions = this.attentionService.query(
				"select obj from SnsAttention obj where obj.fromUser.id=:uid",
				map, -1, -1);
		mv.addObject("attentions_num", attentions.size());
		mv.addObject("fans_num", fans.size());
		return mv;
	}

	/**
	 * 个人主页，访问自己主页的方法,记录访问游客
	 * 
	 * @param request
	 * @param response
	 * @param uid：用户id
	 * @return
	 */
	@SecurityMapping(title = "个人主页", value = "/buyer/homepage.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/homepage.htm")
	public ModelAndView homepage(HttpServletRequest request,
			HttpServletResponse response, String type, String currentPage,
			String orderBy, String orderType, String uid, String goodclass_id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/homepage.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		HomePage home = new HomePage();
		User user = new User();
		if (uid != null && !uid.equals("")) {
			user = this.userService.getObjById(CommUtil.null2Long(uid));
		} else {
			user = SecurityUserHolder.getCurrentUser();
		}
		mv.addObject("owner", user);
		Map map = new HashMap();
		map.put("uid", user.getId());
		List<HomePage> homePages = this.homePageService.query(
				"select obj from HomePage obj where obj.owner.id=:uid", map,
				-1, -1);
		if (homePages.size() > 0) {
			home = homePages.get(0);
		} else {// 第一次创建自己的主页
			home.setOwner(SecurityUserHolder.getCurrentUser());
			home.setAddTime(new Date());
			this.homePageService.save(home);
		}
		// 查询分享的新鲜事，默认查询商品新鲜事
		DynamicQueryObject qo = new DynamicQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.addQuery("obj.display", new SysMap("display",true), "=");
		if (type != null && !type.equals("")) {
			mv.addObject("type", type);
			if (type.equals("1")) {
				qo.addQuery("obj.user.id", new SysMap("uid", home.getOwner()
						.getId()), "=");
				qo.addQuery("obj.store.id is not null", null);
				Map params = new HashMap();
				params.put("uid", home.getOwner().getId());
				List<Dynamic> dynamics = this.dynamicService
						.query(
								"select obj from Dynamic obj where obj.store.id is not null and obj.user.id=:uid",
								params, -1, -1);
				if (dynamics.size() > 0) {
					mv.addObject("allNum", CommUtil.null2Int(dynamics.size()));
				}
			}
			if (type.equals("2")) {
				qo.addQuery("obj.user.id", new SysMap("uid", home.getOwner()
						.getId()), "=");
				qo.addQuery("obj.store.id is null", null);
				qo.addQuery("obj.goods.id is null", null);
				qo.addQuery("obj.dissParent.id is null", null);
				Map params = new HashMap();
				params.put("uid", home.getOwner().getId());
				List<Dynamic> dynamics = this.dynamicService
						.query(
								"select obj from Dynamic obj where obj.store.id is null and obj.store.id is null and obj.user.id=:uid",
								params, -1, -1);
				if (dynamics.size() > 0) {
					mv.addObject("allNum", CommUtil.null2Int(dynamics.size()));
				}
			}
		} else {
			qo.addQuery("obj.user.id", new SysMap("uid", home.getOwner()
					.getId()), "=");
			qo.addQuery("obj.goods.id is not null", null);

			Map params = new HashMap();
			params.put("uid", home.getOwner().getId());
			List<Dynamic> dynamics = this.dynamicService
					.query(
							"select obj from Dynamic obj where obj.goods.id is not null and obj.user.id=:uid",
							params, -1, -1);
			if (dynamics.size() > 0) {
				mv.addObject("allNum", CommUtil.null2Int(dynamics.size()));
			}
			if (goodclass_id != null && !goodclass_id.equals("")) {
				mv.addObject("goodclass_id", goodclass_id);
				qo.addQuery("obj.goods.gc.id", new SysMap("goodClass_id",
						CommUtil.null2Long(goodclass_id)), "=");
			}
			// 显示商品分类
			params.clear();
			params.put("uid", home.getOwner().getId());
			List<HomePageGoodsClass> hgcs = this.HomeGoodsClassService
					.query(
							"select obj from HomePageGoodsClass obj where obj.user.id=:uid ",
							params, -1, -1);
			/*
			 * if (HomeGoodsClass.size() > 0) { mv.addObject("HomeGoodsClass",
			 * HomeGoodsClass.get(0)); }
			 */
			mv.addObject("hgcs", hgcs);
		}
		// 添加并保存访客
		if (uid != null && !uid.equals("")) {
			List<Visit> custs = home.getCustomers();

			if (custs.size() == 0) {
				Visit visit = new Visit();
				visit.setAddTime(new Date());
				visit.setHomepage(home);
				visit.setUser(SecurityUserHolder.getCurrentUser());
				this.visitService.save(visit);
			} else {
				map.clear();
				map.put("home_owner_id", home.getOwner().getId());
				map.put("uid", SecurityUserHolder.getCurrentUser().getId());
				List<Visit> visits = this.visitService
						.query(
								"select obj from Visit obj where obj.user.id=:uid and obj.homepage.owner.id=:home_owner_id",
								map, -1, -1);
				if (visits.size() > 0) {
					visits.get(0).setAddTime(new Date());
					this.visitService.update(visits.get(0));
				} else {
					Visit visit = new Visit();
					visit.setAddTime(new Date());
					visit.setHomepage(home);
					visit.setUser(SecurityUserHolder.getCurrentUser());
					this.visitService.save(visit);
				}
			}
		}
		map.clear();
		map.put("uid", home.getOwner().getId());
		List<Visit> visits = this.visitService
				.query(
						"select obj from Visit obj where obj.homepage.owner.id=:uid order by addTime desc",
						map, -1, 10);
		mv.addObject("visits", visits);
		IPageList pList = this.dynamicService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	/**
	 * 个人主页删除动态，ajax删除
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "个人主页删除动态", value = "/buyer/homepage_dynamic_del.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/homepage_dynamic_del.htm")
	public void homepage_dynamic_del(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String orderBy, String orderType, String type) {
		boolean flag = false;
		if (id != null && !id.equals("")) {
			Dynamic dynamic = this.dynamicService
					.getObjById(Long.parseLong(id));
			flag = this.dynamicService.delete(Long.parseLong(id));
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(flag);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 设置主页新鲜事是否为加密，加密状态别人不可见
	 * 
	 * @param request
	 * @param response
	 * @param dynamic_id
	 */
	@SecurityMapping(title = "个人主页新鲜事加密", value = "/buyer/homepage_dynamic_lock.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/homepage_dynamic_lock.htm")
	public void homepage_dynamic_lock(HttpServletRequest request,
			HttpServletResponse response, String dynamic_id) {
		Dynamic dynamic = this.dynamicService.getObjById(CommUtil
				.null2Long(dynamic_id));
		boolean locked = dynamic.isLocked();
		if (locked == false) {
			dynamic.setLocked(true);
		} else {
			dynamic.setLocked(false);
		}
		this.dynamicService.update(dynamic);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(dynamic.isLocked());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 个人主页添加关注
	 * 
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "个人主页添加关注", value = "/buyer/homepage_add_attention.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/homepage_add_attention.htm")
	public void homepage_add_attention(HttpServletRequest request,
			HttpServletResponse response, String user_id) {
		boolean flag = false;
		Map params = new HashMap();
		params.put("uid", SecurityUserHolder.getCurrentUser().getId());
		params.put("user_id", CommUtil.null2Long(user_id));
		List<SnsAttention> SnsAttentions = this.attentionService
				.query(
						"select obj from SnsAttention obj where obj.fromUser.id=:uid and obj.toUser.id=:user_id ",
						params, -1, -1);
		if (SnsAttentions.size() == 0) {
			User atted = this.userService.getObjById(CommUtil
					.null2Long(user_id));
			SnsAttention attention = new SnsAttention();
			attention.setAddTime(new Date());
			attention.setFromUser(SecurityUserHolder.getCurrentUser());
			attention.setToUser(atted);
			flag = this.attentionService.save(attention);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(flag);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 个人主页添加关注
	 * 
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "个人主页添加关注", value = "/buyer/homepage_remove_attention.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/homepage_remove_attention.htm")
	public void homepage_remove_attention(HttpServletRequest request,
			HttpServletResponse response, String id) {
		boolean flag = false;
		flag = this.attentionService.delete(CommUtil.null2Long(id));
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(flag);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 好友列表，分页查询当前主页的好友
	 * 
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "好友列表", value = "/buyer/homepage/myfriends.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/homepage/myfriends.htm")
	public ModelAndView homepage_myfriends(HttpServletRequest request,
			HttpServletResponse response, String uid, String currentPage,
			String orderBy, String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/homepage_myfriends.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		SnsFriendQueryObject qo = new SnsFriendQueryObject(currentPage, mv,
				orderBy, orderType);
		User user = new User();
		if (uid != null && !uid.equals("")) {
			user = this.userService.getObjById(CommUtil.null2Long(uid));
		} else {
			user = SecurityUserHolder.getCurrentUser();
		}
		mv.addObject("owner", user);
		qo.addQuery("obj.fromUser.id", new SysMap("fromUser_id", user.getId()),
				"=");
		IPageList pList = this.snsFriendService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;

	}

	/**
	 * 关注列表，分页查询当前主页的关注信息
	 * 
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "关注列表", value = "/buyer/homepage/myattention.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/homepage/myattention.htm")
	public ModelAndView homepage_myattention(HttpServletRequest request,
			HttpServletResponse response, String uid, String currentPage,
			String orderBy, String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/homepage_myattention.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		SnsAttentionQueryObject qo = new SnsAttentionQueryObject(currentPage,
				mv, orderBy, orderType);
		User user = new User();
		if (uid != null && !uid.equals("")) {
			user = this.userService.getObjById(CommUtil.null2Long(uid));
		} else {
			user = SecurityUserHolder.getCurrentUser();
		}
		mv.addObject("owner", user);
		qo
				.addQuery("obj.fromUser.id",
						new SysMap("user_id", user.getId()), "=");
		IPageList pList = this.attentionService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;

	}

	/**
	 * 粉丝列表，分页查询当前主页的粉丝信息
	 * 
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "关注列表", value = "/buyer/homepage/myfans.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/homepage/myfans.htm")
	public ModelAndView homepage_myfans(HttpServletRequest request,
			HttpServletResponse response, String uid, String currentPage,
			String orderBy, String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/homepage_myfans.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SnsAttentionQueryObject qo = new SnsAttentionQueryObject(currentPage,
				mv, orderBy, orderType);
		User user = new User();
		if (uid != null && !uid.equals("")) {
			user = this.userService.getObjById(CommUtil.null2Long(uid));
		} else {
			user = SecurityUserHolder.getCurrentUser();
		}
		mv.addObject("owner", user);
		qo.addQuery("obj.toUser.id", new SysMap("user_id", user.getId()), "=");
		IPageList pList = this.attentionService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;

	}

	/**
	 * 个人主页最近访客，页面使用$!httpInclude.include("/buyer/homepage_visit.htm")标签引用方法，
	 * 
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "最近访客", value = "/buyer/homepage_visit.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/homepage_visit.htm")
	public ModelAndView homepage_visit(HttpServletRequest request,
			HttpServletResponse response, String orderBy, String orderType,
			String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/homepage_visit.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String uid = request.getParameter("uid");
		User user = new User();
		if (uid != null && !uid.equals("")) {
			user = this.userService.getObjById(CommUtil.null2Long(uid));
		} else {
			user = SecurityUserHolder.getCurrentUser();
		}
		mv.addObject("owner", user);
		Map map = new HashMap();
		map.put("uid", user.getId());
		List<Visit> visits = this.visitService
				.query(
						"select obj from Visit obj where obj.homepage.owner.id=:uid order by addTime desc",
						map, -1, 10);
		mv.addObject("visits", visits);
		return mv;
	}

	/**
	 * 个人主页删除访客ajax
	 * 
	 * @param request
	 * @param response
	 * @param dynamic_id
	 */
	@SecurityMapping(title = "删除访客ajax", value = "/buyer/homepage_visit_dele.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/homepage_visit_dele.htm")
	public void homepage_visit_dele(HttpServletRequest request,
			HttpServletResponse response, String visit_id) {
		boolean flag = false;
		Map params = new HashMap();
		params.put("custom_id", CommUtil.null2Long(visit_id));
		params.put("uid", SecurityUserHolder.getCurrentUser().getId());
		List<Visit> customer = this.visitService
				.query(
						"select obj from Visit obj where obj.user.id=:custom_id and obj.homepage.owner.id=:uid",
						params, -1, -1);
		if (customer.size() > 0) {
			flag = this.visitService.delete(customer.get(0).getId());
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(flag);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 个人主页发布新鲜事时查询已经购买宝贝和已经收藏宝贝
	 * 
	 * @param request
	 * @param response
	 * @param dynamic_id
	 */
	@SecurityMapping(title = "查询已经购买宝贝和已经收藏宝贝", value = "/buyer/homepage_query_goods.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/homepage_query_goods.htm")
	public ModelAndView homepage_query_goods(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/homepage_query_goods.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		int fcount = 0;
		int ocount = 0;
		Map map = new HashMap();
		map.put("uid", SecurityUserHolder.getCurrentUser().getId());
		map.put("type", 0);
		List<Favorite> favorites = this.favoriteService
				.query(
						"select obj from Favorite obj where obj.user.id=:uid and obj.type=:type order by addTime desc",
						map, fcount, 7);
		map.clear();
		map.put("uid", SecurityUserHolder.getCurrentUser().getId());
		map.put("type", 0);
		List<Favorite> Allfavorites = this.favoriteService
				.query(
						"select obj from Favorite obj where obj.user.id=:uid and obj.type=:type order by addTime desc",
						map, -1, -1);
		mv.addObject("favorites", favorites);
		map.clear();
		map.put("uid", SecurityUserHolder.getCurrentUser().getId());
		map.put("order_status", 50);
		List<OrderForm> orders = this.orderFormService
				.query(
						"select obj from OrderForm obj where obj.user.id=:uid and obj.order_status=:order_status order by finishTime desc",
						map, ocount, 7);
		map.clear();
		map.put("uid", SecurityUserHolder.getCurrentUser().getId());
		map.put("order_status", 50);
		List<OrderForm> Allorders = this.orderFormService
				.query(
						"select obj from OrderForm obj where obj.user.id=:uid and obj.order_status=:order_status order by finishTime desc",
						map, -1, -1);
		mv.addObject("favorite_Allsize", Allfavorites.size());
		mv.addObject("order_Allsize", Allorders.size());
		mv.addObject("orders", orders);
		mv.addObject("fcurrentCount", fcount);
		mv.addObject("ocurrentCount", ocount);
		return mv;
	}

	/**
	 * 查询已经购买宝贝和已经收藏宝贝ajax分页
	 * 
	 * @param request
	 * @param response
	 * @param dynamic_id
	 */
	@SecurityMapping(title = "查询收藏宝贝ajax分页", value = "/buyer/homepage_query_goods_favorite_ajax.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/homepage_query_goods_favorite_ajax.htm")
	public ModelAndView homepage_query_goods_favorite_ajax(
			HttpServletRequest request, HttpServletResponse response,
			String fcurrentCount) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/homepage_query_goods_favorite_ajax.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		int fcount = 0;
		if (fcurrentCount != null && !fcurrentCount.equals("")) {
			fcount = CommUtil.null2Int(fcurrentCount);
		}
		Map map = new HashMap();
		map.put("uid", SecurityUserHolder.getCurrentUser().getId());
		map.put("type", 0);
		List<Favorite> favorites = this.favoriteService
				.query(
						"select obj from Favorite obj where obj.user.id=:uid and obj.type=:type order by addTime desc",
						map, fcount, 7);
		mv.addObject("favorites", favorites);
		mv.addObject("fcurrentCount", fcount);
		return mv;
	}

	/**
	 * 查询已经购买宝贝ajax分页
	 * 
	 * @param request
	 * @param response
	 * @param dynamic_id
	 */
	@SecurityMapping(title = "查询已经购买宝贝ajax分页", value = "/buyer/homepage_query_goods_order_ajax.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/homepage_query_goods_order_ajax.htm")
	public ModelAndView homepage_query_goods_order_ajax(
			HttpServletRequest request, HttpServletResponse response,
			String ocurrentCount) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/homepage_query_goods_order_ajax.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		int ocount = 0;
		if (ocurrentCount != null && !ocurrentCount.equals("")) {
			ocount = CommUtil.null2Int(ocurrentCount);
		}
		Map map = new HashMap();
		map.put("uid", SecurityUserHolder.getCurrentUser().getId());
		map.put("order_status", 50);
		List<OrderForm> orders = this.orderFormService
				.query(
						"select obj from OrderForm obj where obj.user.id=:uid and obj.order_status=:order_status order by finishTime desc",
						map, ocount, 7);
		mv.addObject("orders", orders);
		mv.addObject("ocurrentCount", ocount);
		return mv;
	}

	/**
	 * 个人主页发布新鲜事时查询已经收藏店铺
	 * 
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "查询已经收藏店铺", value = "/buyer/homepage_query_stores.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/homepage_query_stores.htm")
	public ModelAndView homepage_query_stores(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/homepage_query_stores.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		int currentCount = 0;
		Map map = new HashMap();
		map.put("uid", SecurityUserHolder.getCurrentUser().getId());
		map.put("type", 1);
		List<Favorite> favorites = this.favoriteService
				.query(
						"select obj from Favorite obj where obj.user.id=:uid and obj.type=:type order by addTime desc",
						map, currentCount, 7);
		map.clear();
		map.put("uid", SecurityUserHolder.getCurrentUser().getId());
		map.put("type", 1);
		List<Favorite> Allfavorites = this.favoriteService
				.query(
						"select obj from Favorite obj where obj.user.id=:uid and obj.type=:type order by addTime desc",
						map, -1, -1);
		mv.addObject("favorites", favorites);
		mv.addObject("favorite_Allsize", Allfavorites.size());
		mv.addObject("currentCount", currentCount);
		return mv;
	}

	/**
	 * 查询已经购买宝贝ajax分页
	 * 
	 * @param request
	 * @param response
	 * @param dynamic_id
	 */
	@SecurityMapping(title = "查询已关注店铺ajax分页", value = "/buyer/homepage_query_stores_ajax.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/homepage_query_stores_ajax.htm")
	public ModelAndView homepage_query_stores_ajax(HttpServletRequest request,
			HttpServletResponse response, String currentCount) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/homepage_query_stores_ajax.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		int count = 0;
		if (currentCount != null && !currentCount.equals("")) {
			count = CommUtil.null2Int(currentCount);
		}
		Map map = new HashMap();
		map.put("uid", SecurityUserHolder.getCurrentUser().getId());
		map.put("type", 1);
		List<Favorite> favorites = this.favoriteService
				.query(
						"select obj from Favorite obj where obj.user.id=:uid and obj.type=:type order by addTime desc",
						map, count, 7);
		mv.addObject("favorites", favorites);
		mv.addObject("currentCount", count);
		return mv;
	}

	/**
	 * 宝贝链接添加宝贝方法
	 * 
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "个人主页添加关注", value = "/buyer/homepage_goods_url_add.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/homepage_goods_url_add.htm")
	public void homepage_goods_url_add(HttpServletRequest request,
			HttpServletResponse response, String url) {
		boolean flag = true;
		Goods goods = null;
		String str = null;
		String address = CommUtil.getURL(request) + "/goods";
		String[] urls = url.split("_");
		if (urls.length == 2) {
			if (!address.equals(urls[0])) {
				flag = false;
			}
			String ids[] = urls[1].split("\\.");
			if (ids.length == 2) {
				if (!ids[1].equals("htm")) {
					flag = false;
				}
				if (flag == true) {
					goods = this.goodsService.getObjById(CommUtil
							.null2Long(ids[0]));
				}
			}
		}
		if (goods != null) {
			String img_url = CommUtil.getURL(request) + "/"
					+ goods.getGoods_main_photo().getPath() + "/"
					+ goods.getGoods_main_photo().getName() + "_small" + "."
					+ goods.getGoods_main_photo().getExt();
			str = img_url + "," + goods.getId();
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
