package com.lakecloud.foundation.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lakecloud.core.dao.IGenericDAO;
import com.lakecloud.foundation.domain.NoticeAttemp;
import com.lakecloud.foundation.service.INoticeAttempService;

@Service
@Transactional
public class NoticeAttempService implements INoticeAttempService {
	@Resource(name = "noticeAttempDAO")
	private IGenericDAO<NoticeAttemp> noticeAttempDAO;
	@Override
	public boolean save(NoticeAttemp instance) {
		// TODO Auto-generated method stub
		try {
			noticeAttempDAO.save(instance);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public NoticeAttemp getObjById(Long id) {
		// TODO Auto-generated method stub
		NoticeAttemp noticeAttemp = this.noticeAttempDAO.get(id);
		if (noticeAttemp != null) {
			return noticeAttemp;
		}
		return null;
	}
   

	@Override
	public boolean update(NoticeAttemp instance) {
		// TODO Auto-generated method stub
		try {
			this.noticeAttempDAO.update( instance);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public NoticeAttemp getObjByProperty(String propertyName, Object value) {
		// TODO Auto-generated method stub
		return this.noticeAttempDAO.getBy(propertyName, value);
	}

}
