package com.lakecloud.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.ParseException;
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

import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.WebForm;
import com.lakecloud.foundation.domain.Article;
import com.lakecloud.foundation.domain.ArticleClass;
import com.lakecloud.foundation.domain.query.ArticleQueryObject;
import com.lakecloud.foundation.service.IArticleClassService;
import com.lakecloud.foundation.service.IArticleService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;

/**
 * @info 系统文章管理控制器，用来发布、修改系统文章信息
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Controller
public class ArticleManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IArticleService articleService;
	@Autowired
	private IArticleClassService articleClassService;

	/**
	 * Article列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "文章列表", value = "/admin/article_list.htm*", rtype = "admin", rname = "文章管理", rcode = "article", rgroup = "网站")
	@RequestMapping("/admin/article_list.htm")
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/article_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		ArticleQueryObject qo = new ArticleQueryObject(currentPage, mv,
				orderBy, orderType);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, Article.class, mv);
		IPageList pList = this.articleService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/article_list.htm",
				"", params, pList, mv);
		return mv;
	}

	/**
	 * article添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "文章添加", value = "/admin/article_add.htm*", rtype = "admin", rname = "文章管理", rcode = "article", rgroup = "网站")
	@RequestMapping("/admin/article_add.htm")
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String class_id) {
		ModelAndView mv = new JModelAndView("admin/blue/article_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<ArticleClass> acs = this.articleClassService
				.query("select obj from ArticleClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		Article obj = new Article();
		obj.setDisplay(true);
		if (class_id != null && !class_id.equals(""))
			obj.setArticleClass(this.articleClassService.getObjById(Long
					.parseLong(class_id)));
		mv.addObject("obj", obj);
		mv.addObject("acs", acs);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * article编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "文章编辑", value = "/admin/article_edit.htm*", rtype = "admin", rname = "文章管理", rcode = "article", rgroup = "网站")
	@RequestMapping("/admin/article_edit.htm")
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/article_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			Article article = this.articleService
					.getObjById(Long.parseLong(id));
			List<ArticleClass> acs = this.articleClassService
					.query("select obj from ArticleClass obj where obj.parent.id is null order by obj.sequence asc",
							null, -1, -1);
			mv.addObject("acs", acs);
			mv.addObject("obj", article);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * article保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "文章保存", value = "/admin/article_save.htm*", rtype = "admin", rname = "文章管理", rcode = "article", rgroup = "网站")
	@RequestMapping("/admin/article_save.htm")
	public ModelAndView save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url, String class_id,
			String content) {
		WebForm wf = new WebForm();
		Article article = null;
		if (id.equals("")) {
			article = wf.toPo(request, Article.class);
			article.setAddTime(new Date());
		} else {
			Article obj = this.articleService.getObjById(Long.parseLong(id));
			article = (Article) wf.toPo(request, obj);
		}
		article.setArticleClass(this.articleClassService.getObjById(Long
				.parseLong(class_id)));
		System.out.println(content);
		if (id.equals("")) {
			this.articleService.save(article);
		} else
			this.articleService.update(article);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存文章成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage
					+ "&class_id=" + class_id);
		}
		return mv;
	}

	@SecurityMapping(title = "文章删除", value = "/admin/article_del.htm*", rtype = "admin", rname = "文章管理", rcode = "article", rgroup = "网站")
	@RequestMapping("/admin/article_del.htm")
	public String delete(HttpServletRequest request, String mulitId) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Article article = this.articleService.getObjById(Long
						.parseLong(id));
				this.articleService.delete(Long.parseLong(id));
			}
		}
		return "redirect:article_list.htm";
	}

	@SecurityMapping(title = "文章AJAX更新", value = "/admin/article_ajax.htm*", rtype = "admin", rname = "文章管理", rcode = "article", rgroup = "网站")
	@RequestMapping("/admin/article_ajax.htm")
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		Article obj = this.articleService.getObjById(Long.parseLong(id));
		Field[] fields = Article.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(obj);
		Object val = null;
		for (Field field : fields) {
			if (field.getName().equals(fieldName)) {
				Class clz = Class.forName("java.lang.String");
				if (field.getType().getName().equals("int")) {
					clz = Class.forName("java.lang.Integer");
				}
				if (field.getType().getName().equals("boolean")) {
					clz = Class.forName("java.lang.Boolean");
				}
				if (!value.equals("")) {
					val = BeanUtils.convertType(value, clz);
				} else {
					val = !CommUtil.null2Boolean(wrapper
							.getPropertyValue(fieldName));
				}
				wrapper.setPropertyValue(fieldName, val);
			}
		}
		this.articleService.update(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@RequestMapping("/admin/article_mark.htm")
	public void article_mark(HttpServletRequest request,
			HttpServletResponse response, String mark, String id) {
		Map params = new HashMap();
		params.put("mark", mark.trim());
		params.put("id", CommUtil.null2Long(id));
		List<Article> arts = this.articleService
				.query("select obj from Article obj where obj.mark=:mark and obj.id!=:id",
						params, -1, -1);
		boolean ret = true;
		if (arts.size() > 0) {
			ret = false;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}