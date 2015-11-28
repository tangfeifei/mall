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
import com.lakecloud.foundation.domain.GoodsReturnLog;
import com.lakecloud.foundation.service.IGoodsReturnLogService;

@Service
@Transactional
public class GoodsReturnLogServiceImpl implements IGoodsReturnLogService{
	@Resource(name = "goodsReturnLogDAO")
	private IGenericDAO<GoodsReturnLog> goodsReturnLogDao;
	
	public boolean save(GoodsReturnLog goodsReturnLog) {
		/**
		 * init other field here
		 */
		try {
			this.goodsReturnLogDao.save(goodsReturnLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public GoodsReturnLog getObjById(Long id) {
		GoodsReturnLog goodsReturnLog = this.goodsReturnLogDao.get(id);
		if (goodsReturnLog != null) {
			return goodsReturnLog;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.goodsReturnLogDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> goodsReturnLogIds) {
		// TODO Auto-generated method stub
		for (Serializable id : goodsReturnLogIds) {
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
		GenericPageList pList = new GenericPageList(GoodsReturnLog.class, query,
				params, this.goodsReturnLogDao);
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
	
	public boolean update(GoodsReturnLog goodsReturnLog) {
		try {
			this.goodsReturnLogDao.update( goodsReturnLog);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<GoodsReturnLog> query(String query, Map params, int begin, int max){
		return this.goodsReturnLogDao.query(query, params, begin, max);
		
	}
}
