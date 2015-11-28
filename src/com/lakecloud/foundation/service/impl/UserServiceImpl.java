package com.lakecloud.foundation.service.impl;

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
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.IUserService;

@Service
@Transactional
public class UserServiceImpl implements IUserService {
	@Resource(name = "userDAO")
	private IGenericDAO<User> userDAO;

	public boolean delete(Long id) {
		// TODO Auto-generated method stub
		try {
			this.userDAO.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public User getObjById(Long id) {
		// TODO Auto-generated method stub
		return this.userDAO.get(id);
	}

	public boolean save(User user) {
		// TODO Auto-generated method stub
		try {
			this.userDAO.save(user);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean update(User user) {
		// TODO Auto-generated method stub
		try {
			this.userDAO.update(user);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public List<User> query(String query, Map params, int begin, int max) {
		// TODO Auto-generated method stub
		return this.userDAO.query(query, params, begin, max);

	}

	public IPageList list(IQueryObject properties) {
		// TODO Auto-generated method stub
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(User.class, query, params,
				this.userDAO);
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

	@Override
	public User getObjByProperty(String propertyName, String value) {
		// TODO Auto-generated method stub
		return this.userDAO.getBy(propertyName, value);
	}

}
