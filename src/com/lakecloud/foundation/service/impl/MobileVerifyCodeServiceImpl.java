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
import com.lakecloud.foundation.domain.MobileVerifyCode;
import com.lakecloud.foundation.service.IMobileVerifyCodeService;

@Service
@Transactional
public class MobileVerifyCodeServiceImpl implements IMobileVerifyCodeService{
	@Resource(name = "mobileVerifyCodeDAO")
	private IGenericDAO<MobileVerifyCode> mobileVerifyCodeDao;
	
	public boolean save(MobileVerifyCode mobileVerifyCode) {
		/**
		 * init other field here
		 */
		try {
			this.mobileVerifyCodeDao.save(mobileVerifyCode);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public MobileVerifyCode getObjById(Long id) {
		MobileVerifyCode mobileVerifyCode = this.mobileVerifyCodeDao.get(id);
		if (mobileVerifyCode != null) {
			return mobileVerifyCode;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.mobileVerifyCodeDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> mobileVerifyCodeIds) {
		// TODO Auto-generated method stub
		for (Serializable id : mobileVerifyCodeIds) {
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
		GenericPageList pList = new GenericPageList(MobileVerifyCode.class, query,
				params, this.mobileVerifyCodeDao);
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
	
	public boolean update(MobileVerifyCode mobileVerifyCode) {
		try {
			this.mobileVerifyCodeDao.update( mobileVerifyCode);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<MobileVerifyCode> query(String query, Map params, int begin, int max){
		return this.mobileVerifyCodeDao.query(query, params, begin, max);
		
	}

	@Override
	public MobileVerifyCode getObjByProperty(String propertyName, Object value) {
		// TODO Auto-generated method stub
		return this.mobileVerifyCodeDao.getBy(propertyName, value);
	}
}
