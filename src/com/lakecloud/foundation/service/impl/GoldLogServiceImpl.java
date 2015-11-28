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
import com.lakecloud.foundation.domain.GoldLog;
import com.lakecloud.foundation.service.IGoldLogService;

@Service
@Transactional
public class GoldLogServiceImpl implements IGoldLogService{
	@Resource(name = "goldLogDAO")
	private IGenericDAO<GoldLog> goldLogDao;
	
	public boolean save(GoldLog goldLog) {
		/**
		 * init other field here
		 */
		try {
			this.goldLogDao.save(goldLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public GoldLog getObjById(Long id) {
		GoldLog goldLog = this.goldLogDao.get(id);
		if (goldLog != null) {
			return goldLog;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.goldLogDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> goldLogIds) {
		// TODO Auto-generated method stub
		for (Serializable id : goldLogIds) {
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
		GenericPageList pList = new GenericPageList(GoldLog.class, query,
				params, this.goldLogDao);
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
	
	public boolean update(GoldLog goldLog) {
		try {
			this.goldLogDao.update( goldLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<GoldLog> query(String query, Map params, int begin, int max){
		return this.goldLogDao.query(query, params, begin, max);
		
	}
}
