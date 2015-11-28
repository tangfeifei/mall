package com.lakecloud.foundation.domain.query;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.query.QueryObject;

public class CommonGoodsClassQueryObject extends QueryObject {
	public CommonGoodsClassQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}
	public CommonGoodsClassQueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}
}
