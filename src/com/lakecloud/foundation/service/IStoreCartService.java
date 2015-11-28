package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;

import com.lakecloud.foundation.domain.StoreCart;

public interface IStoreCartService {
	/**
	 * 保存一个StoreCart，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(StoreCart instance);
	
	/**
	 * 根据一个ID得到StoreCart
	 * 
	 * @param id
	 * @return
	 */
	StoreCart getObjById(Long id);
	
	/**
	 * 删除一个StoreCart
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除StoreCart
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到StoreCart
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个StoreCart
	 * 
	 * @param id
	 *            需要更新的StoreCart的id
	 * @param dir
	 *            需要更新的StoreCart
	 */
	boolean update(StoreCart instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<StoreCart> query(String query, Map params, int begin, int max);
}
