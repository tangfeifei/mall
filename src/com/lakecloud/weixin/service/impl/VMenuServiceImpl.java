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
import com.lakecloud.weixin.domain.VMenu;
import com.lakecloud.weixin.service.IVMenuService;

@Service
@Transactional
public class VMenuServiceImpl implements IVMenuService{
	@Resource(name = "vMenuDAO")
	private IGenericDAO<VMenu> vMenuDao;
	
	public boolean save(VMenu vMenu) {
		/**
		 * init other field here
		 */
		try {
			this.vMenuDao.save(vMenu);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public VMenu getObjById(Long id) {
		VMenu vMenu = this.vMenuDao.get(id);
		if (vMenu != null) {
			return vMenu;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.vMenuDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> vMenuIds) {
		// TODO Auto-generated method stub
		for (Serializable id : vMenuIds) {
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
		GenericPageList pList = new GenericPageList(VMenu.class, query,
				params, this.vMenuDao);
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
	
	public boolean update(VMenu vMenu) {
		try {
			this.vMenuDao.update( vMenu);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<VMenu> query(String query, Map params, int begin, int max){
		return this.vMenuDao.query(query, params, begin, max);
		
	}

	@Override
	public VMenu getObjByProperty(String propertyName, Object value) {
		// TODO Auto-generated method stub
		return this.vMenuDao.getBy(propertyName, value);
	}
}
