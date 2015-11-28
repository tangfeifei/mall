package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;

import com.lakecloud.foundation.domain.Bargain;

public interface IBargainService {
	/**
	 * 保存一个Bargain，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Bargain instance);
	
	/**
	 * 根据一个ID得到Bargain
	 * 
	 * @param id
	 * @return
	 */
	Bargain getObjById(Long id);
	
	/**
	 * 删除一个Bargain
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除Bargain
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到Bargain
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个Bargain
	 * 
	 * @param id
	 *            需要更新的Bargain的id
	 * @param dir
	 *            需要更新的Bargain
	 */
	boolean update(Bargain instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Bargain> query(String query, Map params, int begin, int max);
}
