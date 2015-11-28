package com.lakecloud.foundation.domain.query;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.query.QueryObject;

public class ReportSubjectQueryObject extends QueryObject {
	public ReportSubjectQueryObject(String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}
	public ReportSubjectQueryObject() {
		super();
		// TODO Auto-generated constructor stub
	}
}
