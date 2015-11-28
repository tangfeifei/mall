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
import com.lakecloud.foundation.domain.Visit;
import com.lakecloud.foundation.service.IVisitService;

@Service
@Transactional
public class VisitServiceImpl implements IVisitService{
	@Resource(name = "visitDAO")
	private IGenericDAO<Visit> visitDao;
	
	public boolean save(Visit visit) {
		/**
		 * init other field here
		 */
		try {
			this.visitDao.save(visit);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Visit getObjById(Long id) {
		Visit visit = this.visitDao.get(id);
		if (visit != null) {
			return visit;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.visitDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> visitIds) {
		// TODO Auto-generated method stub
		for (Serializable id : visitIds) {
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
		GenericPageList pList = new GenericPageList(Visit.class, query,
				params, this.visitDao);
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
	
	public boolean update(Visit visit) {
		try {
			this.visitDao.update( visit);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<Visit> query(String query, Map params, int begin, int max){
		return this.visitDao.query(query, params, begin, max);
		
	}
}
