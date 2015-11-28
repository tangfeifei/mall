package com.lakecloud.weixin.platform.view.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.GoodsClass;
import com.lakecloud.foundation.service.IGoodsClassService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;

/**
 * @info 微信商城客户端商品类目管理控制器，
 * @since V1.3
 * @version 1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net hz
 * 
 */
@Controller
public class WeixinPlatformClassViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsClassService goodsclassService;

	/**
	 * 商品类目列表（一级）
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param type
	 * @return
	 */
	@RequestMapping("/weixin/platform/classes_first.htm")
	public ModelAndView classes_first(HttpServletRequest request,
			HttpServletResponse response, String type) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/classes_first.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).setAttribute("lakecloud_view_type", "weixin");
		List<GoodsClass> gcs = this.goodsclassService
				.query("select obj from GoodsClass obj where obj.parent.id is null order by sequence asc ",
						null, -1, -1);
		mv.addObject("objs", gcs);
		return mv;
	}

	/**
	 * 商品类目列表（二级）
	 * 
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param pgc_id
	 *            :只有在微信号为订阅号时使用的类目id
	 * @return
	 */

	@RequestMapping("/weixin/platform/classes_second.htm")
	public ModelAndView classes_second(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String pgc_id) {
		ModelAndView mv = new JModelAndView(
				"weixin/platform/classes_second.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).setAttribute("lakecloud_view_type", "weixin");
		if (pgc_id != null && !pgc_id.equals("")) {
			GoodsClass parent = this.goodsclassService.getObjById(CommUtil
					.null2Long(pgc_id));
			mv.addObject("parent", parent);
			mv.addObject("pgc_id", pgc_id);
		}
		if (gc_id != null && !gc_id.equals("")) {
			GoodsClass gc = this.goodsclassService.getObjById(CommUtil
					.null2Long(gc_id));
			mv.addObject("parent", gc.getParent());
		}
		mv.addObject("gc_id", gc_id);
		return mv;
	}

}
