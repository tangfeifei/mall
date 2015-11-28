package com.lakecloud.foundation.domain.query;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.query.QueryObject;

public class GoodsSpecificationQueryObject extends QueryObject {
	public GoodsSpecificationQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}
	public GoodsSpecificationQueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}
}
