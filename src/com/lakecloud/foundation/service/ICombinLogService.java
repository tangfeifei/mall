package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;

import com.lakecloud.foundation.domain.CombinLog;

public interface ICombinLogService {
	/**
	 * 保存一个CombinLog，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(CombinLog instance);
	
	/**
	 * 根据一个ID得到CombinLog
	 * 
	 * @param id
	 * @return
	 */
	CombinLog getObjById(Long id);
	
	/**
	 * 删除一个CombinLog
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除CombinLog
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到CombinLog
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个CombinLog
	 * 
	 * @param id
	 *            需要更新的CombinLog的id
	 * @param dir
	 *            需要更新的CombinLog
	 */
	boolean update(CombinLog instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<CombinLog> query(String query, Map params, int begin, int max);
}
