package com.lakecloud.weixin.platform.view.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.GoodsBrand;
import com.lakecloud.foundation.domain.GroupGoods;
import com.lakecloud.foundation.service.IGoodsBrandService;
import com.lakecloud.foundation.service.IGoodsCartService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IGroupGoodsService;
import com.lakecloud.foundation.service.IStoreCartService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;

/**
 * @info 微信商城客户端首页控制器，
 * @since V1.3
 * @version 1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net hz
 * 
 */
@Controller
public class WeixinPlatformIndexViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IStoreCartService storeCartService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IGoodsBrandService brandService;
	@Autowired
	private IGroupGoodsService groupgoodsService;

	/**
	 * 微信商城首页
	 * 
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@RequestMapping("/weixin/platform/index.htm")
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/platform/index.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).setAttribute("lakecloud_view_type", "weixin");
		Map map = new HashMap();
		map.put("weixin_shop_recommend", true);
		map.put("goods_status", 0);
		map.put("weixin_status", 1);
		List<Goods> goods_recommends = this.goodsService
				.query("select obj from Goods obj where obj.goods_store.weixin_status=:weixin_status and obj.weixin_shop_recommend=:weixin_shop_recommend and obj.goods_status=:goods_status order by weixin_shop_recommendTime desc",
						map, 0, 3);
		mv.addObject("goods_recommends", goods_recommends);
		map.clear();
		map.put("weixin_shop_hot", true);
		map.put("goods_status", 0);
		map.put("weixin_status", 1);
		List<Goods> goods_hots = this.goodsService
				.query("select obj from Goods obj where obj.goods_store.weixin_status=:weixin_status and obj.weixin_shop_hot=:weixin_shop_hot and obj.goods_status=:goods_status order by weixin_shop_hotTime desc",
						map, 0, 3);
		mv.addObject("goods_hots", goods_hots);
		map.clear();
		map.put("weixin_shop_recommend", true);
		map.put("audit", 1);
		List<GoodsBrand> brands_recommend = this.brandService
				.query("select obj from GoodsBrand obj where obj.weixin_shop_recommend=:weixin_shop_recommend and obj.audit=:audit order by weixin_shop_recommendTime desc",
						map, 0, 12);
		mv.addObject("brands_recommend", brands_recommend);
		map.clear();
		map.put("weixin_shop_recommend", true);
		map.put("gg_status", 1);
		List<GroupGoods> GroupGoods_recommend = this.groupgoodsService
				.query("select obj from GroupGoods obj where obj.weixin_shop_recommend=:weixin_shop_recommend and obj.gg_status=:gg_status",
						map, 0, 1);
		if (GroupGoods_recommend.size() > 0) {
			mv.addObject("gg_recommend", GroupGoods_recommend.get(0));
		}
		return mv;
	}

	@RequestMapping("/weixin/platform/nav_bottom.htm")
	public ModelAndView nav_bottom(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/platform/nav_bottom.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String op = CommUtil.null2String(request.getAttribute("op"));
		if (op != null && !op.equals("")) {
			mv.addObject("op", op);
		}
		return mv;
	}

	@RequestMapping("/weixin/platform/footer.htm")
	public ModelAndView footer(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("weixin/platform/footer.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}
}
