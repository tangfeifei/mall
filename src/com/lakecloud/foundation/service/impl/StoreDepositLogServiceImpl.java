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
import com.lakecloud.foundation.domain.StoreDepositLog;
import com.lakecloud.foundation.service.IStoreDepositLogService;

@Service
@Transactional
public class StoreDepositLogServiceImpl implements IStoreDepositLogService{
	@Resource(name = "storeDepositLogDAO")
	private IGenericDAO<StoreDepositLog> storeDepositLogDao;
	
	public boolean save(StoreDepositLog storeDepositLog) {
		/**
		 * init other field here
		 */
		try {
			this.storeDepositLogDao.save(storeDepositLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public StoreDepositLog getObjById(Long id) {
		StoreDepositLog storeDepositLog = this.storeDepositLogDao.get(id);
		if (storeDepositLog != null) {
			return storeDepositLog;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.storeDepositLogDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> storeDepositLogIds) {
		// TODO Auto-generated method stub
		for (Serializable id : storeDepositLogIds) {
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
		GenericPageList pList = new GenericPageList(StoreDepositLog.class, query,
				params, this.storeDepositLogDao);
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
	
	public boolean update(StoreDepositLog storeDepositLog) {
		try {
			this.storeDepositLogDao.update( storeDepositLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<StoreDepositLog> query(String query, Map params, int begin, int max){
		return this.storeDepositLogDao.query(query, params, begin, max);
		
	}
}
