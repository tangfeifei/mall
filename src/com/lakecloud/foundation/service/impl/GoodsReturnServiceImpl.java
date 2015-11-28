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
import com.lakecloud.foundation.domain.GoodsReturn;
import com.lakecloud.foundation.service.IGoodsReturnService;

@Service
@Transactional
public class GoodsReturnServiceImpl implements IGoodsReturnService{
	@Resource(name = "goodsReturnDAO")
	private IGenericDAO<GoodsReturn> goodsReturnDao;
	
	public boolean save(GoodsReturn goodsReturn) {
		/**
		 * init other field here
		 */
		try {
			this.goodsReturnDao.save(goodsReturn);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public GoodsReturn getObjById(Long id) {
		GoodsReturn goodsReturn = this.goodsReturnDao.get(id);
		if (goodsReturn != null) {
			return goodsReturn;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.goodsReturnDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> goodsReturnIds) {
		// TODO Auto-generated method stub
		for (Serializable id : goodsReturnIds) {
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
		GenericPageList pList = new GenericPageList(GoodsReturn.class, query,
				params, this.goodsReturnDao);
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
	
	public boolean update(GoodsReturn goodsReturn) {
		try {
			this.goodsReturnDao.update( goodsReturn);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<GoodsReturn> query(String query, Map params, int begin, int max){
		return this.goodsReturnDao.query(query, params, begin, max);
		
	}
}
