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
import com.lakecloud.foundation.domain.ActivityGoods;
import com.lakecloud.foundation.service.IActivityGoodsService;

@Service
@Transactional
public class ActivityGoodsServiceImpl implements IActivityGoodsService{
	@Resource(name = "activityGoodsDAO")
	private IGenericDAO<ActivityGoods> activityGoodsDao;
	
	public boolean save(ActivityGoods activityGoods) {
		/**
		 * init other field here
		 */
		try {
			this.activityGoodsDao.save(activityGoods);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public ActivityGoods getObjById(Long id) {
		ActivityGoods activityGoods = this.activityGoodsDao.get(id);
		if (activityGoods != null) {
			return activityGoods;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.activityGoodsDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> activityGoodsIds) {
		// TODO Auto-generated method stub
		for (Serializable id : activityGoodsIds) {
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
		GenericPageList pList = new GenericPageList(ActivityGoods.class, query,
				params, this.activityGoodsDao);
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
	
	public boolean update(ActivityGoods activityGoods) {
		try {
			this.activityGoodsDao.update( activityGoods);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<ActivityGoods> query(String query, Map params, int begin, int max){
		return this.activityGoodsDao.query(query, params, begin, max);
		
	}
}
