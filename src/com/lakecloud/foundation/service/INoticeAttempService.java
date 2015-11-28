package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.NoticeAttemp;

/************
 * 
 * @author tangf
 *
 */
public interface INoticeAttempService {
	/**
	 * 保存一个Goods，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(NoticeAttemp instance);

	/**
	 * 根据一个ID得到Goods
	 * 
	 * @param id
	 * @return
	 */
	NoticeAttemp getObjById(Long id);

	   

	/**
	 * 更新一个Goods
	 * 
	 * @param id
	 *            需要更新的Goods的id
	 * @param dir
	 *            需要更新的Goods
	 */
	boolean update(NoticeAttemp instance);


	/**
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	NoticeAttemp getObjByProperty(String propertyName, Object value);
}
