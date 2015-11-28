package com.lakecloud.foundation.domain.query;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.query.QueryObject;

public class FavoriteQueryObject extends QueryObject {
	public FavoriteQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}
	public FavoriteQueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}
}
