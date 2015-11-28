package com.lakecloud.foundation.domain.query;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.query.QueryObject;

public class HomePageGoodsClassQueryObject extends QueryObject {
	public HomePageGoodsClassQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}
	public HomePageGoodsClassQueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}
}
