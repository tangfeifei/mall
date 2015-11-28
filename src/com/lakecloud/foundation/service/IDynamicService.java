package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;

import com.lakecloud.foundation.domain.Dynamic;

public interface IDynamicService {
	/**
	 * 保存一个Dynamic，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Dynamic instance);
	
	/**
	 * 根据一个ID得到Dynamic
	 * 
	 * @param id
	 * @return
	 */
	Dynamic getObjById(Long id);
	
	/**
	 * 删除一个Dynamic
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除Dynamic
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到Dynamic
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个Dynamic
	 * 
	 * @param id
	 *            需要更新的Dynamic的id
	 * @param dir
	 *            需要更新的Dynamic
	 */
	boolean update(Dynamic instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Dynamic> query(String query, Map params, int begin, int max);
}
