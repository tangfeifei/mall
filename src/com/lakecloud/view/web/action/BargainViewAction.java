package com.lakecloud.view.web.action;

import java.util.ArrayList;
import java.util.Calendar;
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

import com.lakecloud.core.domain.virtual.SysMap;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.BargainGoods;
import com.lakecloud.foundation.domain.query.BargainGoodsQueryObject;
import com.lakecloud.foundation.service.IBargainGoodsService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;

/**
 * @info 今日特价前端控制，包括特价页面显示等
 * @since V1.3
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Controller
public class BargainViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IBargainGoodsService bargainGoodsService;

	/**
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@RequestMapping("/bargain.htm")
	public ModelAndView bargain(HttpServletRequest request,
			HttpServletResponse response, String bg_time, String currentPage,
			String orderBy, String orderType) {
		ModelAndView mv = new JModelAndView("bargain.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		BargainGoodsQueryObject bqo = new BargainGoodsQueryObject(currentPage,
				mv, orderBy, orderType);
		if (CommUtil.null2String(bg_time).equals("")) {
			bqo.addQuery(
					"obj.bg_time",
					new SysMap("bg_time", CommUtil.formatDate(CommUtil
							.formatShortDate(new Date()))), "=");
		} else {
			bqo.addQuery("obj.bg_time",
					new SysMap("bg_time", CommUtil.formatDate(bg_time)), "=");
		}
		bqo.addQuery("obj.bg_status", new SysMap("bg_status", 1), "=");
		bqo.setPageSize(20);
		IPageList pList = this.bargainGoodsService.list(bqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		// 查询第二天的特价5条数据
		Map params = new HashMap();
		Calendar cal = Calendar.getInstance();
		if (CommUtil.null2String(bg_time).equals("")) {
			bg_time = CommUtil.formatShortDate(new Date());
		}
		cal.setTime(CommUtil.formatDate(bg_time));
		cal.add(Calendar.DAY_OF_YEAR, 1);
		params.put("bg_time",
				CommUtil.formatDate(CommUtil.formatShortDate(cal.getTime())));
		params.put("bg_status", 1);
		List<BargainGoods> bgs = this.bargainGoodsService
				.query("select obj from BargainGoods obj where obj.bg_time=:bg_time and obj.bg_status=:bg_status order by audit_time desc",
						params, 0, 5);
		mv.addObject("bgs", bgs);
		int day_count = this.configService.getSysConfig().getBargain_validity();
		List<Date> dates = new ArrayList<Date>();
		for (int i = 0; i < day_count; i++) {
			cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, i);
			dates.add(cal.getTime());
		}
		mv.addObject("dates", dates);
		mv.addObject("bg_time", bg_time);
		return mv;
	}

}
