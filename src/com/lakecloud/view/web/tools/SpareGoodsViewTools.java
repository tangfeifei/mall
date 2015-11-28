package com.lakecloud.view.web.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lakecloud.foundation.domain.SpareGoodsClass;
import com.lakecloud.foundation.domain.SpareGoodsFloor;
import com.lakecloud.foundation.service.ISpareGoodsClassService;

/**
 * 闲置商品首页分类处理工具
 * 
 * @author hezeng 20130929
 * 
 */
@Component
public class SpareGoodsViewTools {
	@Autowired
	private ISpareGoodsClassService sgcService;

	/**
	 * 首页楼层下方查询所有分类
	 * 
	 * @param sgc
	 * @return
	 */
	public List<SpareGoodsClass> query_childclass(SpareGoodsClass sgc) {
		List<SpareGoodsClass> list = new ArrayList<SpareGoodsClass>();
		for (SpareGoodsClass child : sgc.getChilds()) {
			list.add(child);
			for (SpareGoodsClass c : child.getChilds()) {
				list.add(c);
			}
		}
		return list;
	}

	/**
	 * 楼层分类显示方法
	 * 
	 * @param sgf
	 * @return
	 */
	public List<SpareGoodsClass> query_floorClass(SpareGoodsFloor sgf) {
		List<SpareGoodsClass> list = new ArrayList<SpareGoodsClass>();
		for (SpareGoodsClass child : sgf.getSgc().getChilds()) {
			if (child.isViewInFloor() == true) {
				list.add(child);
			}
			for (SpareGoodsClass c : child.getChilds()) {
				if (c.isViewInFloor() == true) {
					list.add(c);
				}
			}

		}
		return list;
	}

	/**
	 * 闲置商品搜索列表页商品内容过滤html代码，避免html代码干扰页面样式
	 * 
	 * @param inputString
	 * @return
	 */
	public String ClearContent(String inputString) {
		String htmlStr = inputString; // 含html标签的字符串
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;

		java.util.regex.Pattern p_html1;
		java.util.regex.Matcher m_html1;

		try {
			String regEx_script = "<[//s]*?script[^>]*?>[//s//S]*?<[//s]*?///[//s]*?script[//s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[//s//S]*?<///script>
			String regEx_style = "<[//s]*?style[^>]*?>[//s//S]*?<[//s]*?///[//s]*?style[//s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[//s//S]*?<///style>
			String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
			String regEx_html1 = "<[^>]+";
			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 过滤script标签

			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤style标签

			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签

			p_html1 = Pattern.compile(regEx_html1, Pattern.CASE_INSENSITIVE);
			m_html1 = p_html1.matcher(htmlStr);
			htmlStr = m_html1.replaceAll(""); // 过滤html标签

			textStr = htmlStr;
		} catch (Exception e) {
			System.err.println("Html2Text: " + e.getMessage());
		}
		return textStr;// 返回文本字符串
	}
}
