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
import com.lakecloud.foundation.domain.ZTCGoldLog;
import com.lakecloud.foundation.service.IZTCGoldLogService;

@Service
@Transactional
public class ZTCGoldLogServiceImpl implements IZTCGoldLogService{
	@Resource(name = "zTCGlodLogDAO")
	private IGenericDAO<ZTCGoldLog> zTCGlodLogDao;
	
	public boolean save(ZTCGoldLog zTCGlodLog) {
		/**
		 * init other field here
		 */
		try {
			this.zTCGlodLogDao.save(zTCGlodLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public ZTCGoldLog getObjById(Long id) {
		ZTCGoldLog zTCGlodLog = this.zTCGlodLogDao.get(id);
		if (zTCGlodLog != null) {
			return zTCGlodLog;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.zTCGlodLogDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> zTCGlodLogIds) {
		// TODO Auto-generated method stub
		for (Serializable id : zTCGlodLogIds) {
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
		GenericPageList pList = new GenericPageList(ZTCGoldLog.class, query,
				params, this.zTCGlodLogDao);
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
	
	public boolean update(ZTCGoldLog zTCGlodLog) {
		try {
			this.zTCGlodLogDao.update( zTCGlodLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<ZTCGoldLog> query(String query, Map params, int begin, int max){
		return this.zTCGlodLogDao.query(query, params, begin, max);
		
	}
}
