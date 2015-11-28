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
import com.lakecloud.foundation.domain.query.DeliveryGoodsQueryObject;
import com.lakecloud.foundation.service.IDeliveryGoodsService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;

/**
 * @info 买就送前台管理控制器，用来管理买就送信息

 * 
 */
@Controller
public class DeliveryViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IDeliveryGoodsService deliveryGoodsService;

	@RequestMapping("/delivery.htm")
	public ModelAndView delivery(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String orderBy, String orderType) {
		ModelAndView mv = new JModelAndView("delivery.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		DeliveryGoodsQueryObject qo = new DeliveryGoodsQueryObject(currentPage,
				mv, orderBy, orderType);
		qo.addQuery("obj.d_status", new SysMap("d_status", 1), "=");
		qo.addQuery("obj.d_begin_time", new SysMap("d_begin_time", new Date()),
				"<=");
		qo.addQuery("obj.d_end_time", new SysMap("d_end_time", new Date()),
				">=");
		qo.setPageSize(20);
		IPageList pList = this.deliveryGoodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
}
