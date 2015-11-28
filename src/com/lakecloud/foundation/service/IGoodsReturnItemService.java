package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;

import com.lakecloud.foundation.domain.GoodsReturnItem;

public interface IGoodsReturnItemService {
	/**
	 * 保存一个GoodsReturnItem，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GoodsReturnItem instance);
	
	/**
	 * 根据一个ID得到GoodsReturnItem
	 * 
	 * @param id
	 * @return
	 */
	GoodsReturnItem getObjById(Long id);
	
	/**
	 * 删除一个GoodsReturnItem
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除GoodsReturnItem
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到GoodsReturnItem
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个GoodsReturnItem
	 * 
	 * @param id
	 *            需要更新的GoodsReturnItem的id
	 * @param dir
	 *            需要更新的GoodsReturnItem
	 */
	boolean update(GoodsReturnItem instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<GoodsReturnItem> query(String query, Map params, int begin, int max);
}
