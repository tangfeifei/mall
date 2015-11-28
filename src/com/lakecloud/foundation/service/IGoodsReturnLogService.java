package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;

import com.lakecloud.foundation.domain.GoodsReturnLog;

public interface IGoodsReturnLogService {
	/**
	 * 保存一个GoodsReturnLog，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GoodsReturnLog instance);
	
	/**
	 * 根据一个ID得到GoodsReturnLog
	 * 
	 * @param id
	 * @return
	 */
	GoodsReturnLog getObjById(Long id);
	
	/**
	 * 删除一个GoodsReturnLog
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除GoodsReturnLog
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到GoodsReturnLog
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个GoodsReturnLog
	 * 
	 * @param id
	 *            需要更新的GoodsReturnLog的id
	 * @param dir
	 *            需要更新的GoodsReturnLog
	 */
	boolean update(GoodsReturnLog instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<GoodsReturnLog> query(String query, Map params, int begin, int max);
}
