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
import com.lakecloud.foundation.domain.IntegralGoodsOrder;
import com.lakecloud.foundation.service.IIntegralGoodsOrderService;

@Service
@Transactional
public class IntegralGoodsOrderServiceImpl implements IIntegralGoodsOrderService{
	@Resource(name = "integralGoodsOrderDAO")
	private IGenericDAO<IntegralGoodsOrder> integralGoodsOrderDao;
	
	public boolean save(IntegralGoodsOrder integralGoodsOrder) {
		/**
		 * init other field here
		 */
		try {
			this.integralGoodsOrderDao.save(integralGoodsOrder);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public IntegralGoodsOrder getObjById(Long id) {
		IntegralGoodsOrder integralGoodsOrder = this.integralGoodsOrderDao.get(id);
		if (integralGoodsOrder != null) {
			return integralGoodsOrder;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.integralGoodsOrderDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> integralGoodsOrderIds) {
		// TODO Auto-generated method stub
		for (Serializable id : integralGoodsOrderIds) {
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
		GenericPageList pList = new GenericPageList(IntegralGoodsOrder.class, query,
				params, this.integralGoodsOrderDao);
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
	
	public boolean update(IntegralGoodsOrder integralGoodsOrder) {
		try {
			this.integralGoodsOrderDao.update( integralGoodsOrder);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<IntegralGoodsOrder> query(String query, Map params, int begin, int max){
		return this.integralGoodsOrderDao.query(query, params, begin, max);
		
	}
}
