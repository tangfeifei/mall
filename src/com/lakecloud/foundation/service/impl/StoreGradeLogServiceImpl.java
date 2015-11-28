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
import com.lakecloud.foundation.domain.StoreGradeLog;
import com.lakecloud.foundation.service.IStoreGradeLogService;

@Service
@Transactional
public class StoreGradeLogServiceImpl implements IStoreGradeLogService{
	@Resource(name = "storeGradeLogDAO")
	private IGenericDAO<StoreGradeLog> storeGradeLogDao;
	
	public boolean save(StoreGradeLog storeGradeLog) {
		/**
		 * init other field here
		 */
		try {
			this.storeGradeLogDao.save(storeGradeLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public StoreGradeLog getObjById(Long id) {
		StoreGradeLog storeGradeLog = this.storeGradeLogDao.get(id);
		if (storeGradeLog != null) {
			return storeGradeLog;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.storeGradeLogDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> storeGradeLogIds) {
		// TODO Auto-generated method stub
		for (Serializable id : storeGradeLogIds) {
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
		GenericPageList pList = new GenericPageList(StoreGradeLog.class, query,
				params, this.storeGradeLogDao);
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
	
	public boolean update(StoreGradeLog storeGradeLog) {
		try {
			this.storeGradeLogDao.update( storeGradeLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<StoreGradeLog> query(String query, Map params, int begin, int max){
		return this.storeGradeLogDao.query(query, params, begin, max);
		
	}
}
