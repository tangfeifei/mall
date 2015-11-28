package com.lakecloud.weixin.service.impl;
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
import com.lakecloud.weixin.domain.VMessage;
import com.lakecloud.weixin.service.IVMessageService;

@Service
@Transactional
public class VMessageServiceImpl implements IVMessageService{
	@Resource(name = "vMessageDAO")
	private IGenericDAO<VMessage> vMessageDao;
	
	public boolean save(VMessage vMessage) {
		/**
		 * init other field here
		 */
		try {
			this.vMessageDao.save(vMessage);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public VMessage getObjById(Long id) {
		VMessage vMessage = this.vMessageDao.get(id);
		if (vMessage != null) {
			return vMessage;
		}
		return null;
	}
	
	public boolean delete(Long id) {
		try {
			this.vMessageDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean batchDelete(List<Serializable> vMessageIds) {
		// TODO Auto-generated method stub
		for (Serializable id : vMessageIds) {
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
		GenericPageList pList = new GenericPageList(VMessage.class, query,
				params, this.vMessageDao);
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
	
	public boolean update(VMessage vMessage) {
		try {
			this.vMessageDao.update( vMessage);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}	
	public List<VMessage> query(String query, Map params, int begin, int max){
		return this.vMessageDao.query(query, params, begin, max);
		
	}
}
