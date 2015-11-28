package com.lakecloud.view.web.action;

import java.util.List;

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
import com.lakecloud.foundation.domain.Article;
import com.lakecloud.foundation.domain.ArticleClass;
import com.lakecloud.foundation.domain.query.ArticleQueryObject;
import com.lakecloud.foundation.service.IArticleClassService;
import com.lakecloud.foundation.service.IArticleService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.view.web.tools.ArticleViewTools;

/**
 * @info www.chinacloud.net版权所有,前台文章控制器，主要功能: 1、列表文章 2、显示文章
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Controller
public class ArticleViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IArticleService articleService;
	@Autowired
	private IArticleClassService articleClassService;
	@Autowired
	private ArticleViewTools articleTools;

	@RequestMapping("/articlelist.htm")
	public ModelAndView articlelist(HttpServletRequest request,
			HttpServletResponse response, String param, String currentPage) {
		ModelAndView mv = new JModelAndView("articlelist.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		ArticleClass ac = null;
		ArticleQueryObject aqo = new ArticleQueryObject();
		aqo.setCurrentPage(CommUtil.null2Int(currentPage));
		Long id = CommUtil.null2Long(param);
		String mark = "";
		if (id == -1l) {
			mark = param;
		}
		if (!mark.equals("")) {
			aqo
					.addQuery("obj.articleClass.mark",
							new SysMap("mark", mark), "=");
			ac = this.articleClassService.getObjByPropertyName("mark", mark);
		}
		if (id != -1l) {
			aqo.addQuery("obj.articleClass.id", new SysMap("id", id), "=");
			ac = this.articleClassService.getObjById(id);
		}
		aqo.addQuery("obj.display", new SysMap("display", true), "=");
		aqo.setOrderBy("addTime");
		aqo.setOrderType("desc");
		IPageList pList = this.articleService.list(aqo);
		String url = CommUtil.getURL(request) + "/articlelist_" + ac.getId();
		CommUtil.saveIPageList2ModelAndView("", url, "", pList, mv);
		List<ArticleClass> acs = this.articleClassService
				.query(
						"select obj from ArticleClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		List<Article> articles = this.articleService.query(
				"select obj from Article obj order by obj.addTime desc", null,
				0, 6);
		mv.addObject("ac", ac);
		mv.addObject("articles", articles);
		mv.addObject("acs", acs);
		return mv;
	}

	@RequestMapping("/article.htm")
	public ModelAndView article(HttpServletRequest request,
			HttpServletResponse response, String param) {
		ModelAndView mv = new JModelAndView("article.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		Article obj = null;
		Long id = CommUtil.null2Long(param);
		String mark = "";
		if (id == -1l) {
			mark = param;
		}
		if (id != -1l) {
			obj = this.articleService.getObjById(id);
		}
		if (!mark.equals("")) {
			obj = this.articleService.getObjByProperty("mark", mark);
		}
		List<ArticleClass> acs = this.articleClassService
				.query(
						"select obj from ArticleClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		List<Article> articles = this.articleService.query(
				"select obj from Article obj order by obj.addTime desc", null,
				0, 6);
		mv.addObject("articles", articles);
		mv.addObject("acs", acs);
		mv.addObject("obj", obj);
		mv.addObject("articleTools", articleTools);
		return mv;
	}
}
