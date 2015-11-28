package com.lakecloud.manage.buyer.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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

import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.domain.virtual.SysMap;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.WebForm;
import com.lakecloud.foundation.domain.Dynamic;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.HomePageGoodsClass;
import com.lakecloud.foundation.domain.Message;
import com.lakecloud.foundation.domain.SnsAttention;
import com.lakecloud.foundation.domain.SnsFriend;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.query.DynamicQueryObject;
import com.lakecloud.foundation.domain.query.FavoriteQueryObject;
import com.lakecloud.foundation.service.IDynamicService;
import com.lakecloud.foundation.service.IFavoriteService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IHomePageGoodsClassService;
import com.lakecloud.foundation.service.IMessageService;
import com.lakecloud.foundation.service.ISnsAttentionService;
import com.lakecloud.foundation.service.ISnsFriendService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.view.web.tools.OrderViewTools;
import com.lakecloud.view.web.tools.StoreViewTools;

/**
 * @info 买家中心基础管理控制器
  
 * 
 */
@Controller
public class BaseBuyerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private OrderViewTools orderViewTools;
	@Autowired
	private IDynamicService dynamicService;
	@Autowired
	private ISnsFriendService snsFriendService;
	@Autowired
	private IFavoriteService favService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private ISnsAttentionService SnsAttentionService;
	@Autowired
	private IHomePageGoodsClassService HomeGoodsClassService;

	/**
	 * * 买家首页并分页查询所有动态,可以根据type参数不同进行不同的条件查询，
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param type：条件查询参数，type=1为查询自己，type=2为查询好友，type=3为查询相互关注
	 * @return
	 */
	@SecurityMapping(title = "买家中心", value = "/buyer/index.htm*", rtype = "buyer", rname = "买家中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/index.htm")
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String type) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/buyer_index.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<Message> msgs = new ArrayList<Message>();
		if (SecurityUserHolder.getCurrentUser() != null) {
			Map params = new HashMap();
			params.put("status", 0);
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			msgs = this.messageService
					.query(
							"select obj from Message obj where obj.status=:status and obj.toUser.id=:user_id and obj.parent.id is null",
							params, -1, -1);
		}
		mv.addObject("msgs", msgs);
		mv.addObject("storeViewTools", storeViewTools);
		mv.addObject("orderViewTools", orderViewTools);
		// 分页查询所有商城用户动态
		DynamicQueryObject qo = new DynamicQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.addQuery("obj.display", new SysMap("display", true), "=");
		if (type == null || type.equals("")) {
			type = "2";
		}
		if (type.equals("1")) {
			qo.addQuery("obj.user.id", new SysMap("uid", SecurityUserHolder
					.getCurrentUser().getId()), "=");
		}
		if (type.equals("2")) {
			Map map = new HashMap();
			map.put("f_uid", SecurityUserHolder.getCurrentUser().getId());
			List<SnsFriend> myFriends = this.snsFriendService
					.query(
							"select obj from SnsFriend obj where obj.fromUser.id=:f_uid",
							map, -1, -1);
			Set<Long> ids = this.getSnsFriendToUserIds(myFriends);
			Map paras = new HashMap();
			paras.put("ids", null);
			if (myFriends.size() > 0) {
				paras.put("ids", ids);
			}
			qo.addQuery("obj.user.id in (:ids)", paras);
		}
		if (type.equals("3")) {
			Map params = new HashMap();
			params.put("uid", SecurityUserHolder.getCurrentUser().getId());
			List<SnsAttention> SnsAttentions = this.SnsAttentionService
					.query(
							"select obj from SnsAttention obj where obj.fromUser.id=:uid ",
							params, -1, -1);
			Set<Long> ids = this.getSnsAttentionToUserIds(SnsAttentions);
			params.clear();
			params.put("ids", ids);
			if (ids != null && ids.size() > 0) {
				qo.addQuery("obj.user.id in (:ids)", params);
			}
		}
		if (type.equals("4")) {
			qo.addQuery("obj.user.id", new SysMap("uid", SecurityUserHolder
					.getCurrentUser().getId()), "=");
			qo.addQuery("obj.store.id is not null", null);
		}
		qo.addQuery("obj.locked", new SysMap("locked", false), "=");
		qo.addQuery("obj.dissParent.id is null", null);
		qo.setOrderBy("addTime");
		qo.setOrderType("desc");
		qo.setPageSize(10);
		IPageList pList = this.dynamicService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		List list = new ArrayList();
		for (int i = 1; i <= 120; i++) {
			list.add(i);
		}
		mv.addObject("type", type);
		mv.addObject("emoticons", list);
		return mv;
	}

	private Set<Long> getSnsAttentionToUserIds(List<SnsAttention> SnsAttentions) {
		Set<Long> ids = new HashSet<Long>();
		for (SnsAttention attention : SnsAttentions) {
			ids.add(attention.getToUser().getId());
		}
		return ids;
	};

	@SecurityMapping(title = "买家中心导航", value = "/buyer/nav.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/nav.htm")
	public ModelAndView nav(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/buyer_nav.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String op = CommUtil.null2String(request.getAttribute("op"));
		mv.addObject("op", op);
		return mv;
	}

	@SecurityMapping(title = "买家中心导航", value = "/buyer/head.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/head.htm")
	public ModelAndView head(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/buyer_head.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@RequestMapping("/buyer/authority.htm")
	public ModelAndView authority(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("error.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		mv.addObject("op_title", "您没有该项操作权限");
		mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		return mv;
	}

	private Set<Long> getSnsFriendToUserIds(List<SnsFriend> myfriends) {
		Set<Long> ids = new HashSet<Long>();
		if (myfriends.size() > 0) {
			for (SnsFriend friend : myfriends) {
				ids.add(friend.getToUser().getId());
			}
		}
		return ids;
	};

	/**
	 * 动态发布保存，页面中使用ajax提交，保存后将返回的结果无刷新放入form中
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "动态发布保存", value = "/buyer/dynamic_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/dynamic_save.htm")
	public ModelAndView dynamic_save(HttpServletRequest request,
			HttpServletResponse response, String content, String currentPage,
			String orderBy, String orderType, String store_id, String goods_id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/dynamic_list.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		WebForm wf = new WebForm();
		Dynamic dynamic = wf.toPo(request, Dynamic.class);
		dynamic.setAddTime(new Date());
		dynamic.setUser(SecurityUserHolder.getCurrentUser());
		dynamic.setContent(content);
		dynamic.setDisplay(true);
		if (store_id != null && !store_id.equals("")) {
			Store store = this.storeService.getObjById(CommUtil
					.null2Long(store_id));
			dynamic.setStore(store);
		}
		if (goods_id != null && !goods_id.equals("")) {
			Goods goods = this.goodsService.getObjById(CommUtil
					.null2Long(goods_id));
			dynamic.setGoods(goods);
			// 记录商品对应的分类
			Map params = new HashMap();
			params.put("uid", SecurityUserHolder.getCurrentUser().getId());
			params.put("gc_id", goods.getGc().getId());
			List<HomePageGoodsClass> hgcs = this.HomeGoodsClassService
					.query(
							"select obj from HomePageGoodsClass obj where obj.user.id=:uid and obj.gc.id=:gc_id",
							params, -1, -1);
			if (hgcs.size() == 0) {
				Map map = new HashMap();
				map.put("uid", SecurityUserHolder.getCurrentUser().getId());
				HomePageGoodsClass hpgc = new HomePageGoodsClass();
				hpgc.setAddTime(new Date());
				hpgc.setUser(SecurityUserHolder.getCurrentUser());
				hpgc.setGc(goods.getGc());
				this.HomeGoodsClassService.save(hpgc);
			}
		}
		this.dynamicService.save(dynamic);
		// 查询所有动态
		DynamicQueryObject qo = new DynamicQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.addQuery("obj.dissParent.id is null", null);
		qo.setOrderBy("addTime");
		qo.setOrderType("desc");
		qo.setPageSize(10);
		IPageList pList = this.dynamicService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	/**
	 * 删除动态，删除后查询所有动态返回dynamic_list页面，将返回的结果无刷新放入form中
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "删除动态", value = "/buyer/dynamic_del.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/dynamic_del.htm")
	public ModelAndView dynamic_ajax_del(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String orderBy, String orderType) {
		if (!id.equals("")) {
			Dynamic dynamic = this.dynamicService
					.getObjById(Long.parseLong(id));
			this.dynamicService.delete(Long.parseLong(id));
		}
		// 删除后查询所有动态
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/dynamic_list.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		DynamicQueryObject qo = new DynamicQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.addQuery("obj.dissParent.id is null", null);
		qo.setOrderBy("addTime");
		qo.setOrderType("desc");
		qo.setPageSize(10);
		IPageList pList = this.dynamicService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	/**
	 * ajax回复保存方法，提交后将返回结果无刷新放入到对应动态的最下方
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "ajax回复保存方法", value = "/buyer/dynamic_ajax_reply.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/dynamic_ajax_reply.htm")
	public ModelAndView dynamic_ajax_reply(HttpServletRequest request,
			HttpServletResponse response, String parent_id, String fieldName,
			String reply_content) throws ClassNotFoundException {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/dynamic_childs_list.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		WebForm wf = new WebForm();
		Dynamic dynamic = wf.toPo(request, Dynamic.class);
		Dynamic parent = null;
		if (parent_id != null && !parent_id.equals("")) {
			parent = this.dynamicService.getObjById(Long.parseLong(parent_id));
			dynamic.setDissParent(parent);
			this.dynamicService.update(parent);
			dynamic.setDissParent(parent);
		}
		dynamic.setAddTime(new Date());
		dynamic.setUser(SecurityUserHolder.getCurrentUser());
		dynamic.setContent(reply_content);
		this.dynamicService.save(dynamic);
		mv.addObject("obj", parent);
		return mv;

	}

	/**
	 * ajax赞动态方法，将赞数量返回页面中，在页面中无刷新到相应数量位置
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "ajax赞动态方法", value = "/buyer/dynamic_ajax_praise.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/dynamic_ajax_praise.htm")
	public void dynamic_ajax_praise(HttpServletRequest request,
			HttpServletResponse response, String dynamic_id)
			throws ClassNotFoundException {
		Dynamic dynamic = this.dynamicService.getObjById(Long
				.parseLong(dynamic_id));
		dynamic.setPraiseNum(dynamic.getPraiseNum() + 1);
		this.dynamicService.update(dynamic);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(dynamic.getPraiseNum());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * ajax转发动态保存方法，保存转发动态后查询所有动态并替换页面form中数据
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "ajax转发动态保存方法", value = "/buyer/dynamic_ajax_turn.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/dynamic_ajax_turn.htm")
	public ModelAndView dynamic_ajax_turn(HttpServletRequest request,
			HttpServletResponse response, String dynamic_id, String content,
			String currentPage, String orderType, String orderBy)
			throws ClassNotFoundException {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/dynamic_list.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Dynamic dynamic = this.dynamicService.getObjById(Long
				.parseLong(dynamic_id));
		dynamic.setTurnNum(dynamic.getTurnNum() + 1);
		this.dynamicService.update(dynamic);
		Dynamic turn = new Dynamic();
		turn.setAddTime(new Date());
		turn.setContent(content + "//转自" + dynamic.getUser().getUserName()
				+ ":" + dynamic.getContent());
		turn.setUser(SecurityUserHolder.getCurrentUser());
		this.dynamicService.save(turn);
		// 查询所有动态
		DynamicQueryObject qo = new DynamicQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.addQuery("obj.dissParent.id is null", null);
		qo.setOrderBy("addTime");
		qo.setOrderType("desc");
		qo.setPageSize(10);
		IPageList pList = this.dynamicService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;

	}

	/**
	 * 删除动态下方自己发布的评论，删除后查询所有该动态的评论返回dynamic_childs_list页面，将返回的结果无刷新放入对应动态下方
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "删除动态下方自己发布的评论", value = "/buyer/dynamic_reply_del.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/dynamic_reply_del.htm")
	public ModelAndView dynamic_reply_del(HttpServletRequest request,
			HttpServletResponse response, String id, String parent_id) {
		if (!id.equals("")) {
			Dynamic dynamic = this.dynamicService
					.getObjById(Long.parseLong(id));
			this.dynamicService.delete(Long.parseLong(id));
		}
		// 删除后查询所有该动态的评论
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/dynamic_childs_list.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		if (parent_id != null && !parent_id.equals("")) {
			Dynamic obj = this.dynamicService.getObjById(CommUtil
					.null2Long(parent_id));
			mv.addObject("obj", obj);
		}
		return mv;
	}

	/**
	 * 分页显示用户分享的店铺列表，
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "用户分享收藏店铺列表", value = "/buyer/fav_store_list.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/fav_store_list.htm")
	public ModelAndView fav_store_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/fav_store_list.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		FavoriteQueryObject qo = new FavoriteQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.addQuery("obj.user.id", new SysMap("uid", SecurityUserHolder
				.getCurrentUser().getId()), "=");
		qo.addQuery("obj.type", new SysMap("type", 1), "=");
		qo.setPageSize(4);
		IPageList pList = this.favService.list(qo);
		mv.addObject("objs", pList.getResult());
		String Ajax_url = CommUtil.getURL(request)
				+ "/buyer/fav_store_list_ajax.htm";
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(Ajax_url,
				"", pList.getCurrentPage(), pList.getPages()));
		return mv;
	}

	/**
	 * 分页显示用户收藏店铺ajax列表，将返回结果使用jQuery().html()到页面中
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "用户分享收藏店铺ajax列表", value = "/buyer/fav_store_list_ajax.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/fav_store_list_ajax.htm")
	public ModelAndView fav_store_list_ajax(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/fav_store_list_ajax.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		FavoriteQueryObject qo = new FavoriteQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.addQuery("obj.user.id", new SysMap("uid", SecurityUserHolder
				.getCurrentUser().getId()), "=");
		qo.addQuery("obj.type", new SysMap("type", 1), "=");
		qo.setPageSize(4);
		IPageList pList = this.favService.list(qo);
		mv.addObject("objs", pList.getResult());
		String Ajax_url = CommUtil.getURL(request)
				+ "/buyer/fav_store_list_ajax.htm";
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(Ajax_url,
				"", pList.getCurrentPage(), pList.getPages()));
		return mv;
	}

	/**
	 * 用户收藏商品列表，分页显示商品信息
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "用户分享收藏商品列表", value = "/buyer/fav_goods_list.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/fav_goods_list.htm")
	public ModelAndView fav_goods_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/fav_goods_list.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		FavoriteQueryObject qo = new FavoriteQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.addQuery("obj.user.id", new SysMap("uid", SecurityUserHolder
				.getCurrentUser().getId()), "=");
		qo.addQuery("obj.type", new SysMap("type", 0), "=");
		qo.setPageSize(4);
		IPageList pList = this.favService.list(qo);
		mv.addObject("objs", pList.getResult());
		String Ajax_url = CommUtil.getURL(request)
				+ "/buyer/fav_goods_list_ajax.htm";
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(Ajax_url,
				"", pList.getCurrentPage(), pList.getPages()));
		return mv;
	}

	/**
	 * 用户收藏商品列表，页面将返回结果使用jQuery().html()替换到页面的相应位置中
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "用户分享收藏商品ajax列表", value = "/buyer/fav_goods_list_ajax.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/fav_goods_list_ajax.htm")
	public ModelAndView fav_goods_list_ajax(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/fav_goods_list_ajax.html",
				configService.getSysConfig(), this.userConfigService
						.getUserConfig(), 0, request, response);
		FavoriteQueryObject qo = new FavoriteQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.addQuery("obj.user.id", new SysMap("uid", SecurityUserHolder
				.getCurrentUser().getId()), "=");
		qo.addQuery("obj.type", new SysMap("type", 0), "=");
		qo.setPageSize(4);
		IPageList pList = this.favService.list(qo);
		mv.addObject("objs", pList.getResult());
		String Ajax_url = CommUtil.getURL(request)
				+ "/buyer/fav_goods_list_ajax.htm";
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(Ajax_url,
				"", pList.getCurrentPage(), pList.getPages()));
		return mv;
	}

}
