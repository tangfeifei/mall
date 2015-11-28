package com.lakecloud.foundation.service.impl;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import com.lakecloud.core.query.PageObject;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lakecloud.core.dao.IGenericDAO;
import com.lakecloud.core.query.GenericPageList;
import com.lakecloud.foundation.domain.Dynamic;
import com.lakecloud.foundation.service.IDynamicService;

@Service
@Transactional
public class DynamicServiceImpl implements IDynamicService{
	@Resource(name = "dynamicDAO")
	private IGenericDAO<Dynamic> dynamicDao;
	
	public boolean save(Dynamic dynamic) {
		/**
		 * init other field here
		 */
		try {
			this.dynamicDao.save(dynamic);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Dynamic getObjById(Long id) {
		Dynamic dynamic = this.dynamicDao.get(id);
		if (dynamic != null) {
			return dynamic;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.dynamicDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> dynamicIds) {
		// TODO Auto-generated method stub
		for (Serializable id : dynamicIds) {
			delete((Long) id);
		}
		return true;
	}
	
	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(Dynamic.class, query,
				params, this.dynamicDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj
						.getCurrentPage(), pageObj.getPageSize() == null ? 0
						: pageObj.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}
	
	public boolean update(Dynamic dynamic) {
		try {
			this.dynamicDao.update( dynamic);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<Dynamic> query(String query, Map params, int begin, int max){
		return this.dynamicDao.query(query, params, begin, max);
		
	}
}
