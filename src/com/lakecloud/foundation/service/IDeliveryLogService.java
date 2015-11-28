package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;

import com.lakecloud.foundation.domain.DeliveryLog;

public interface IDeliveryLogService {
	/**
	 * 保存一个DeliveryLog，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(DeliveryLog instance);
	
	/**
	 * 根据一个ID得到DeliveryLog
	 * 
	 * @param id
	 * @return
	 */
	DeliveryLog getObjById(Long id);
	
	/**
	 * 删除一个DeliveryLog
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除DeliveryLog
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到DeliveryLog
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个DeliveryLog
	 * 
	 * @param id
	 *            需要更新的DeliveryLog的id
	 * @param dir
	 *            需要更新的DeliveryLog
	 */
	boolean update(DeliveryLog instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<DeliveryLog> query(String query, Map params, int begin, int max);
}
