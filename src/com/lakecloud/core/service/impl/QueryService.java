package com.lakecloud.core.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lakecloud.core.base.GenericEntityDao;
import com.lakecloud.core.service.IQueryService;
/**
 * 
* <p>Title: QueryService.java</p>

* <p>Description:基础sevice接口的实现类，系统中暂时使用该类 </p>

* <p>Copyright: Copyright (c) 2012-2014</p>

* <p>Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net</p>

* @author erikzhang

* @date 2014-4-27

* @version LakeCloud_C2C 1.3
 */
@Service
@Transactional
public class QueryService implements IQueryService {
	@Autowired
	@Qualifier("genericEntityDao")
	private GenericEntityDao geDao;

	public GenericEntityDao getGeDao() {
		return geDao;
	}

	public void setGeDao(GenericEntityDao geDao) {
		this.geDao = geDao;
	}

	public List query(String scope, Map params, int page, int pageSize) {
		// TODO Auto-generated method stub
		return this.geDao.query(scope, params, page, pageSize);
	}

}
