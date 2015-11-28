package com.lakecloud.view.web.action;

import java.util.Date;

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
import com.lakecloud.foundation.domain.Activity;
import com.lakecloud.foundation.domain.query.ActivityGoodsQueryObject;
import com.lakecloud.foundation.service.IActivityGoodsService;
import com.lakecloud.foundation.service.IActivityService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;

/**
 * @info 商城活动前台管理控制控制器
 * 
 */
@Controller
public class ActivityViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IActivityService activityService;
	@Autowired
	private IActivityGoodsService activityGoodsService;

	@RequestMapping("/activity.htm")
	public ModelAndView activity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("activity.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Activity act = this.activityService.getObjById(CommUtil.null2Long(id));
		ActivityGoodsQueryObject qo = new ActivityGoodsQueryObject(currentPage,
				mv, "addTime", "desc");
		qo.setPageSize(24);
		qo.addQuery("obj.ag_status", new SysMap("ag_status", 1), "=");
		qo.addQuery("obj.act.id", new SysMap("act_id", act.getId()), "=");
		qo.addQuery("obj.act.ac_status", new SysMap("ac_status", 1), "=");
		qo.addQuery("obj.act.ac_begin_time", new SysMap("ac_begin_time",
				new Date()), "<=");
		qo.addQuery("obj.act.ac_end_time",
				new SysMap("ac_end_time", new Date()), ">=");
		qo.addQuery("obj.ag_goods.goods_status", new SysMap("goods_status", 0),
				"=");
		IPageList pList = this.activityGoodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("act", act);
		return mv;
	}
}
