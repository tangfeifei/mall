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
import com.lakecloud.foundation.domain.IntegralLog;
import com.lakecloud.foundation.service.IIntegralLogService;

@Service
@Transactional
public class IntegralLogServiceImpl implements IIntegralLogService{
	@Resource(name = "integralLogDAO")
	private IGenericDAO<IntegralLog> integralLogDao;
	
	public boolean save(IntegralLog integralLog) {
		/**
		 * init other field here
		 */
		try {
			this.integralLogDao.save(integralLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public IntegralLog getObjById(Long id) {
		IntegralLog integralLog = this.integralLogDao.get(id);
		if (integralLog != null) {
			return integralLog;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.integralLogDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> integralLogIds) {
		// TODO Auto-generated method stub
		for (Serializable id : integralLogIds) {
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
		GenericPageList pList = new GenericPageList(IntegralLog.class, query,
				params, this.integralLogDao);
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
	
	public boolean update(IntegralLog integralLog) {
		try {
			this.integralLogDao.update( integralLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<IntegralLog> query(String query, Map params, int begin, int max){
		return this.integralLogDao.query(query, params, begin, max);
		
	}
}
