package com.lakecloud.core.query;

import org.springframework.web.servlet.ModelAndView;

/**
 * 
* <p>Title: BaseQueryObject.java</p>

* <p>Description:基础的查询对象类，包装了page信息和order信息 </p>

* <p>Copyright: Copyright (c) 2012-2014</p>

* <p>Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net</p>

* @author erikzhang

* @date 2014-4-27

* @version LakeCloud_C2C 1.3
 */
public class BaseQueryObject extends QueryObject {

	public BaseQueryObject(String currentPage, ModelAndView mv, String orderBy,
			String orderType) {
		super(currentPage, mv, orderBy, orderType);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getQuery() {

		return super.getQuery();
	}
}
