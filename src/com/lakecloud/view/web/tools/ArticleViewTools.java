package com.lakecloud.view.web.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lakecloud.foundation.domain.Article;
import com.lakecloud.foundation.service.IArticleService;

/**
 * 文章查询工具类
 * 
 * @author erikchang
 * 
 */
@Component
public class ArticleViewTools {
	@Autowired
	private IArticleService articleService;

	/**
	 * 根据postion参数查询谋id文章的上一篇、下一篇文章，position为-1为上一篇，position为1为下一篇
	 * 
	 * @param id
	 * @param position
	 * @return
	 */
	public Article queryArticle(Long id, int position) {
		String query = "select obj from Article obj where obj.articleClass.id=:class_id and obj.display=:display and ";
		Article article = this.articleService.getObjById(id);
		if (article != null) {
			Map params = new HashMap();
			params.put("addTime", article.getAddTime());
			params.put("class_id", article.getArticleClass().getId());
			params.put("display", true);
			if (position > 0) {
				query = query
						+ "obj.addTime>:addTime order by obj.addTime desc";
			} else {
				query = query
						+ "obj.addTime<:addTime order by obj.addTime desc";
			}
			List<Article> objs = this.articleService.query(query, params, 0, 1);
			if (objs.size() > 0) {
				return objs.get(0);
			} else {
				Article obj = new Article();
				obj.setTitle("没有了");
				return obj;
			}
		} else {
			Article obj = new Article();
			obj.setTitle("没有了");
			return obj;
		}
	}
}
