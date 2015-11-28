package com.lakecloud.weixin.service.impl;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lakecloud.core.dao.IGenericDAO;
import com.lakecloud.core.query.GenericPageList;
import com.lakecloud.core.query.PageObject;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;
import com.lakecloud.weixin.domain.VLog;
import com.lakecloud.weixin.service.IVLogService;

@Service
@Transactional
public class VLogServiceImpl implements IVLogService{
	@Resource(name = "vLogDAO")
	private IGenericDAO<VLog> vLogDao;
	
	public boolean save(VLog vLog) {
		/**
		 * init other field here
		 */
		try {
			this.vLogDao.save(vLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public VLog getObjById(Long id) {
		VLog vLog = this.vLogDao.get(id);
		if (vLog != null) {
			return vLog;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.vLogDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> vLogIds) {
		// TODO Auto-generated method stub
		for (Serializable id : vLogIds) {
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
		GenericPageList pList = new GenericPageList(VLog.class, query,
				params, this.vLogDao);
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
	
	public boolean update(VLog vLog) {
		try {
			this.vLogDao.update( vLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<VLog> query(String query, Map params, int begin, int max){
		return this.vLogDao.query(query, params, begin, max);
		
	}
}
