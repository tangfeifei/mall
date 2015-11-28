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
import com.lakecloud.foundation.domain.CombinLog;
import com.lakecloud.foundation.service.ICombinLogService;

@Service
@Transactional
public class CombinLogServiceImpl implements ICombinLogService{
	@Resource(name = "combinLogDAO")
	private IGenericDAO<CombinLog> combinLogDao;
	
	public boolean save(CombinLog combinLog) {
		/**
		 * init other field here
		 */
		try {
			this.combinLogDao.save(combinLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public CombinLog getObjById(Long id) {
		CombinLog combinLog = this.combinLogDao.get(id);
		if (combinLog != null) {
			return combinLog;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.combinLogDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> combinLogIds) {
		// TODO Auto-generated method stub
		for (Serializable id : combinLogIds) {
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
		GenericPageList pList = new GenericPageList(CombinLog.class, query,
				params, this.combinLogDao);
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
	
	public boolean update(CombinLog combinLog) {
		try {
			this.combinLogDao.update( combinLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<CombinLog> query(String query, Map params, int begin, int max){
		return this.combinLogDao.query(query, params, begin, max);
		
	}
}
