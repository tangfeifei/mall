package com.lakecloud.view.web.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Navigation;
import com.lakecloud.foundation.service.IActivityService;
import com.lakecloud.foundation.service.IArticleService;
import com.lakecloud.foundation.service.IGoodsClassService;
import com.lakecloud.foundation.service.INavigationService;

/**
 * @info 前台导航工具类，查询显示对应的导航信息
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Component
public class NavViewTools {
	@Autowired
	private INavigationService navService;
	@Autowired
	private IArticleService articleService;
	@Autowired
	private IActivityService activityService;
	@Autowired
	private IGoodsClassService goodsClassService;

	/**
	 * 查询页面导航
	 * 
	 * @param position
	 *            导航位置
	 * @param count
	 *            导航数目
	 * @return
	 */
	public List<Navigation> queryNav(int location, int count) {
		List<Navigation> navs = new ArrayList<Navigation>();
		Map params = new HashMap();
		params.put("display", true);
		params.put("location", location);
		params.put("type", "sparegoods");
		navs = this.navService
				.query(
						"select obj from Navigation obj where obj.display=:display and obj.location=:location and obj.type!=:type order by obj.sequence asc",
						params, 0, count);
		return navs;
	}
}
