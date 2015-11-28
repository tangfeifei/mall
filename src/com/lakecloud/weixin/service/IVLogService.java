package com.lakecloud.weixin.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;
import com.lakecloud.weixin.domain.VLog;

public interface IVLogService {
	/**
	 * 保存一个VLog，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(VLog instance);
	
	/**
	 * 根据一个ID得到VLog
	 * 
	 * @param id
	 * @return
	 */
	VLog getObjById(Long id);
	
	/**
	 * 删除一个VLog
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除VLog
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到VLog
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个VLog
	 * 
	 * @param id
	 *            需要更新的VLog的id
	 * @param dir
	 *            需要更新的VLog
	 */
	boolean update(VLog instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<VLog> query(String query, Map params, int begin, int max);
}
