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
import com.lakecloud.foundation.domain.GoodsClassStaple;
import com.lakecloud.foundation.service.IGoodsClassStapleService;

@Service
@Transactional
public class GoodsClassStapleServiceImpl implements IGoodsClassStapleService{
	@Resource(name = "goodsClassStapleDAO")
	private IGenericDAO<GoodsClassStaple> goodsClassStapleDao;
	
	public boolean save(GoodsClassStaple goodsClassStaple) {
		/**
		 * init other field here
		 */
		try {
			this.goodsClassStapleDao.save(goodsClassStaple);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public GoodsClassStaple getObjById(Long id) {
		GoodsClassStaple goodsClassStaple = this.goodsClassStapleDao.get(id);
		if (goodsClassStaple != null) {
			return goodsClassStaple;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.goodsClassStapleDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> goodsClassStapleIds) {
		// TODO Auto-generated method stub
		for (Serializable id : goodsClassStapleIds) {
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
		GenericPageList pList = new GenericPageList(GoodsClassStaple.class, query,
				params, this.goodsClassStapleDao);
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
	
	public boolean update(GoodsClassStaple goodsClassStaple) {
		try {
			this.goodsClassStapleDao.update( goodsClassStaple);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<GoodsClassStaple> query(String query, Map params, int begin, int max){
		return this.goodsClassStapleDao.query(query, params, begin, max);
		
	}
}
