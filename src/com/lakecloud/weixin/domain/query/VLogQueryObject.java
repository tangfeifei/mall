package com.lakecloud.weixin.domain.query;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.query.QueryObject;

public class VLogQueryObject extends QueryObject {
	public VLogQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}
	public VLogQueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}
}
