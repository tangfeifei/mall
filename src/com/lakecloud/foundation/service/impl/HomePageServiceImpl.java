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
import com.lakecloud.foundation.domain.HomePage;
import com.lakecloud.foundation.service.IHomePageService;

@Service
@Transactional
public class HomePageServiceImpl implements IHomePageService{
	@Resource(name = "homePageDAO")
	private IGenericDAO<HomePage> homePageDao;
	
	public boolean save(HomePage homePage) {
		/**
		 * init other field here
		 */
		try {
			this.homePageDao.save(homePage);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public HomePage getObjById(Long id) {
		HomePage homePage = this.homePageDao.get(id);
		if (homePage != null) {
			return homePage;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.homePageDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> homePageIds) {
		// TODO Auto-generated method stub
		for (Serializable id : homePageIds) {
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
		GenericPageList pList = new GenericPageList(HomePage.class, query,
				params, this.homePageDao);
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
	
	public boolean update(HomePage homePage) {
		try {
			this.homePageDao.update( homePage);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<HomePage> query(String query, Map params, int begin, int max){
		return this.homePageDao.query(query, params, begin, max);
		
	}
}
