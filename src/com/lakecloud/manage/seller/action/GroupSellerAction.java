package com.lakecloud.manage.seller.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.lakecloud.core.tools.WebForm;
import com.lakecloud.foundation.domain.Accessory;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.Group;
import com.lakecloud.foundation.domain.GroupArea;
import com.lakecloud.foundation.domain.GroupClass;
import com.lakecloud.foundation.domain.GroupGoods;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.UserGoodsClass;
import com.lakecloud.foundation.domain.query.GroupGoodsQueryObject;
import com.lakecloud.foundation.service.IAccessoryService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IGroupAreaService;
import com.lakecloud.foundation.service.IGroupClassService;
import com.lakecloud.foundation.service.IGroupGoodsService;
import com.lakecloud.foundation.service.IGroupService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserGoodsClassService;
import com.lakecloud.foundation.service.IUserService;

/**
 * @info 卖家中心团购管理控制器
  
 * 
 */
@Controller
public class GroupSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGroupService groupService;
	@Autowired
	private IGroupAreaService groupAreaService;
	@Autowired
	private IGroupClassService groupClassService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private IUserGoodsClassService userGoodsClassService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IUserService userService;

	@SecurityMapping(title = "卖家团购列表", value = "/seller/group.htm*", rtype = "seller", rname = "团购管理", rcode = "group_seller", rgroup = "促销管理")
	@RequestMapping("/seller/group.htm")
	public ModelAndView group(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String gg_name) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/group.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GroupGoodsQueryObject qo = new GroupGoodsQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.gg_goods.goods_store.user.id", new SysMap("user_id",
				SecurityUserHolder.getCurrentUser().getId()), "=");
		if (!CommUtil.null2String(gg_name).equals("")) {
			qo.addQuery("obj.gg_name", new SysMap("gg_name", "%" + gg_name
					+ "%"), "like");
		}
		IPageList pList = this.groupGoodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("gg_name", gg_name);
		return mv;
	}

	@SecurityMapping(title = "卖家团购添加", value = "/seller/group_add.htm*", rtype = "seller", rname = "团购管理", rcode = "group_seller", rgroup = "促销管理")
	@RequestMapping("/seller/group_add.htm")
	public ModelAndView group_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/group_add.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("joinEndTime", new Date());
		List<Group> groups = this.groupService
				.query(
						"select obj from Group obj where obj.joinEndTime>=:joinEndTime",
						params, -1, -1);
		List<GroupArea> gas = this.groupAreaService
				.query(
						"select obj from GroupArea obj where obj.parent.id is null order by obj.ga_sequence asc",
						null, -1, -1);
		List<GroupClass> gcs = this.groupClassService
				.query(
						"select obj from GroupClass obj where obj.parent.id is null order by obj.gc_sequence asc",
						null, -1, -1);
		mv.addObject("gcs", gcs);
		mv.addObject("gas", gas);
		mv.addObject("groups", groups);
		return mv;
	}

	@SecurityMapping(title = "卖家团购编辑", value = "/seller/group_edit.htm*", rtype = "seller", rname = "团购管理", rcode = "group_seller", rgroup = "促销管理")
	@RequestMapping("/seller/group_edit.htm")
	public ModelAndView group_edit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/group_add.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("joinEndTime", new Date());
		List<Group> groups = this.groupService
				.query(
						"select obj from Group obj where obj.joinEndTime>=:joinEndTime",
						params, -1, -1);
		List<GroupArea> gas = this.groupAreaService
				.query(
						"select obj from GroupArea obj where obj.parent.id is null order by obj.ga_sequence asc",
						null, -1, -1);
		List<GroupClass> gcs = this.groupClassService
				.query(
						"select obj from GroupClass obj where obj.parent.id is null order by obj.gc_sequence asc",
						null, -1, -1);
		GroupGoods obj = this.groupGoodsService.getObjById(CommUtil
				.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("gcs", gcs);
		mv.addObject("gas", gas);
		mv.addObject("groups", groups);
		return mv;
	}

	@SecurityMapping(title = "卖家团购商品", value = "/seller/group_goods.htm*", rtype = "seller", rname = "团购管理", rcode = "group_seller", rgroup = "促销管理")
	@RequestMapping("/seller/group_goods.htm")
	public ModelAndView group_goods(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/group_goods.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		List<UserGoodsClass> gcs = this.userGoodsClassService
				.query(
						"select obj from UserGoodsClass obj where obj.parent.id is null and obj.user.id=:user_id order by obj.sequence asc",
						params, -1, -1);
		mv.addObject("gcs", gcs);
		return mv;
	}

	@RequestMapping("/seller/group_goods_load.htm")
	public void group_goods_load(HttpServletRequest request,
			HttpServletResponse response, String goods_name, String gc_id) {
		goods_name = CommUtil.convert(goods_name, "UTF-8");
		boolean ret = true;
		Map params = new HashMap();
		params.put("goods_name", "%" + goods_name.trim() + "%");
		params.put("group_buy", 0);
		params.put("as", 0);
		params.put("delivery_status", 0);
		params.put("combin_status", 0);
		Store store = this.userService.getObjById(
				SecurityUserHolder.getCurrentUser().getId()).getStore();
		params.put("store_id", store.getId());
		UserGoodsClass ugc = this.userGoodsClassService.getObjById(CommUtil
				.null2Long(gc_id));
		Set<Long> ids = this.genericUserGcIds(ugc);
		List<UserGoodsClass> ugc_list = new ArrayList<UserGoodsClass>();
		for (Long g_id : ids) {
			UserGoodsClass temp_ugc = this.userGoodsClassService
					.getObjById(g_id);
			ugc_list.add(temp_ugc);
		}
		String query = "select obj from Goods obj where obj.goods_name like:goods_name and obj.group_buy=:group_buy and obj.goods_store.id=:store_id and obj.activity_status=:as and obj.delivery_status=:delivery_status and obj.combin_status=:combin_status";
		for (int i = 0; i < ugc_list.size(); i++) {
			if (i == 0) {
				query = query + " and (:ugc" + i + " member of obj.goods_ugcs";
				if (ugc_list.size() == 1) {
					query = query + ")";
				}
			} else {
				if (i == ugc_list.size() - 1) {
					query = query + " or :ugc" + i
							+ " member of obj.goods_ugcs)";
				} else
					query = query + " or :ugc" + i
							+ " member of obj.goods_ugcs";
			}
			params.put("ugc" + i, ugc_list.get(i));
		}
		List<Goods> goods = this.goodsService.query(query, params, -1, -1);
		List<Map> list = new ArrayList<Map>();
		for (Goods obj : goods) {
			Map map = new HashMap();
			map.put("id", obj.getId());
			map.put("goods_name", obj.getGoods_name());
			map.put("store_price", obj.getStore_price());
			map.put("store_inventory", obj.getGoods_inventory());
			list.add(map);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(list, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "团购商品保存", value = "/seller/group_goods_save.htm*", rtype = "seller", rname = "团购管理", rcode = "group_seller", rgroup = "促销管理")
	@RequestMapping("/seller/group_goods_save.htm")
	public String group_goods_save(HttpServletRequest request,
			HttpServletResponse response, String id, String group_id,
			String goods_id, String gc_id, String ga_id) {
		WebForm wf = new WebForm();
		GroupGoods gg = null;
		if (id.equals("")) {
			gg = wf.toPo(request, GroupGoods.class);
			gg.setAddTime(new Date());
		} else {
			GroupGoods obj = this.groupGoodsService.getObjById(CommUtil
					.null2Long(id));
			gg = (GroupGoods) wf.toPo(request, obj);
		}
		Group group = this.groupService
				.getObjById(CommUtil.null2Long(group_id));
		gg.setGroup(group);
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		gg.setGg_goods(goods);
		GroupClass gc = this.groupClassService.getObjById(CommUtil
				.null2Long(gc_id));
		gg.setGg_gc(gc);
		GroupArea ga = this.groupAreaService.getObjById(CommUtil
				.null2Long(ga_id));
		gg.setGg_ga(ga);
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "group";
		Map map = new HashMap();
		try {
			String fileName = gg.getGg_img() == null ? "" : gg.getGg_img()
					.getName();
			map = CommUtil.saveFileToServer(request, "gg_acc",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory gg_img = new Accessory();
					gg_img.setName(CommUtil.null2String(map.get("fileName")));
					gg_img.setExt(CommUtil.null2String(map.get("mime")));
					gg_img.setSize(CommUtil.null2Float(map.get("fileSize")));
					gg_img.setPath(uploadFilePath + "/group");
					gg_img.setWidth(CommUtil.null2Int(map.get("width")));
					gg_img.setHeight(CommUtil.null2Int(map.get("height")));
					gg_img.setAddTime(new Date());
					this.accessoryService.save(gg_img);
					gg.setGg_img(gg_img);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory gg_img = gg.getGg_img();
					gg_img.setName(CommUtil.null2String(map.get("fileName")));
					gg_img.setExt(CommUtil.null2String(map.get("mime")));
					gg_img.setSize(CommUtil.null2Float(map.get("fileSize")));
					gg_img.setPath(uploadFilePath + "/group");
					gg_img.setWidth(CommUtil.null2Int(map.get("width")));
					gg_img.setHeight(CommUtil.null2Int(map.get("height")));
					gg_img.setAddTime(new Date());
					this.accessoryService.update(gg_img);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		gg.setGg_rebate(BigDecimal.valueOf(CommUtil.div(CommUtil.mul(gg
				.getGg_price(), 10), gg.getGg_goods().getGoods_price())));
		if (id.equals("")) {
			this.groupGoodsService.save(gg);
		} else {
			this.groupGoodsService.update(gg);
		}
		goods.setGroup_buy(1);
		// 商品的团购价格
		this.goodsService.update(goods);
		request.getSession(false).setAttribute("url",
				CommUtil.getURL(request) + "/seller/group.htm");
		request.getSession(false).setAttribute("op_title", "团购商品保存成功");
		return "redirect:" + CommUtil.getURL(request) + "/success.htm";
	}

	private Set<Long> genericUserGcIds(UserGoodsClass ugc) {
		Set<Long> ids = new HashSet<Long>();
		if (ugc != null) {
			ids.add(ugc.getId());
			for (UserGoodsClass child : ugc.getChilds()) {
				Set<Long> cids = genericUserGcIds(child);
				for (Long cid : cids) {
					ids.add(cid);
				}
				ids.add(child.getId());
			}
		}
		return ids;
	}

	@SecurityMapping(title = "团购商品删除", value = "/seller/group_del.htm*", rtype = "seller", rname = "团购管理", rcode = "group_seller", rgroup = "促销管理")
	@RequestMapping("/seller/group_del.htm")
	public String group_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			GroupGoods gg = this.groupGoodsService.getObjById(CommUtil
					.null2Long(id));
			Goods goods = gg.getGg_goods();
			goods.setGroup_buy(0);
			this.goodsService.update(goods);
			CommUtil.del_acc(request, gg.getGg_img());
			this.groupGoodsService.delete(CommUtil.null2Long(id));
		}
		return "redirect:group.htm?currentPage=" + currentPage;
	}

}
