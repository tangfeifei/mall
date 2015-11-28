package com.lakecloud.core.query;

import java.util.Map;

import com.lakecloud.core.dao.IGenericDAO;
import com.lakecloud.core.query.support.IQuery;
import com.lakecloud.core.query.support.IQueryObject;
/**
 * 
* <p>Title: GenericPageList.java</p>

* <p>Description: 面向对象分页类，该类用来进行数据查询并分页返回数据信息 </p>

* <p>Copyright: Copyright (c) 2012-2014</p>

* <p>Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net</p>

* @author erikzhang

* @date 2014-4-27

* @version LakeCloud_C2C 1.3
 */
public class GenericPageList extends PageList {
	private static final long serialVersionUID = 6730593239674387757L;

	protected String scope;

	protected Class cls;
	public GenericPageList(Class cls,IQueryObject queryObject,IGenericDAO dao)
	{
		this(cls,queryObject.getQuery(),queryObject.getParameters(),dao);
	}
	public GenericPageList(Class cls, String scope, Map paras,
			IGenericDAO dao) {
		this.cls = cls;
		this.scope = scope;
		IQuery query = new GenericQuery(dao);
		query.setParaValues(paras);
		this.setQuery(query);
	}

	/**
	 * 查询
	 * 
	 * @param currentPage
	 *            当前页数
	 * @param pageSize
	 *            一页的查询个数
	 */
	public void doList(int currentPage, int pageSize) {
		String totalSql = "select COUNT(obj) from " + cls.getName() + " obj where "
				+ scope;
		super.doList(pageSize, currentPage, totalSql, scope);
	}
}
