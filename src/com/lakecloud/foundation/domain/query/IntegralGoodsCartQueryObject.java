package com.lakecloud.foundation.domain.query;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.query.QueryObject;

public class IntegralGoodsCartQueryObject extends QueryObject {
	public IntegralGoodsCartQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}
	public IntegralGoodsCartQueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}
}
