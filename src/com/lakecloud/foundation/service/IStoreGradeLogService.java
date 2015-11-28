package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;

import com.lakecloud.foundation.domain.StoreGradeLog;

public interface IStoreGradeLogService {
	/**
	 * 保存一个StoreGradeLog，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(StoreGradeLog instance);
	
	/**
	 * 根据一个ID得到StoreGradeLog
	 * 
	 * @param id
	 * @return
	 */
	StoreGradeLog getObjById(Long id);
	
	/**
	 * 删除一个StoreGradeLog
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除StoreGradeLog
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到StoreGradeLog
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个StoreGradeLog
	 * 
	 * @param id
	 *            需要更新的StoreGradeLog的id
	 * @param dir
	 *            需要更新的StoreGradeLog
	 */
	boolean update(StoreGradeLog instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<StoreGradeLog> query(String query, Map params, int begin, int max);
}
