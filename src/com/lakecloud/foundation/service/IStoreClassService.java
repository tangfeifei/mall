package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.StoreClass;

public interface IStoreClassService {
	/**
	 * 保存一个StoreClass，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(StoreClass instance);

	/**
	 * 根据一个ID得到StoreClass
	 * 
	 * @param id
	 * @return
	 */
	StoreClass getObjById(Long id);

	/**
	 * 删除一个StoreClass
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除StoreClass
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到StoreClass
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个StoreClass
	 * 
	 * @param id
	 *            需要更新的StoreClass的id
	 * @param dir
	 *            需要更新的StoreClass
	 */
	boolean update(StoreClass instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<StoreClass> query(String query, Map params, int begin, int max);

}
