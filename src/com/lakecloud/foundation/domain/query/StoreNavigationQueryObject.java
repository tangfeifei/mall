package com.lakecloud.foundation.domain.query;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.query.QueryObject;

public class StoreNavigationQueryObject extends QueryObject {
	public StoreNavigationQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}
	public StoreNavigationQueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}
}
