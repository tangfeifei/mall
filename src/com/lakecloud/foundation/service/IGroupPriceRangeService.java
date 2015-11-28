﻿package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;

import com.lakecloud.foundation.domain.GroupPriceRange;

public interface IGroupPriceRangeService {
	/**
	 * 保存一个GroupPriceRange，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GroupPriceRange instance);
	
	/**
	 * 根据一个ID得到GroupPriceRange
	 * 
	 * @param id
	 * @return
	 */
	GroupPriceRange getObjById(Long id);
	
	/**
	 * 删除一个GroupPriceRange
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除GroupPriceRange
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到GroupPriceRange
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个GroupPriceRange
	 * 
	 * @param id
	 *            需要更新的GroupPriceRange的id
	 * @param dir
	 *            需要更新的GroupPriceRange
	 */
	boolean update(GroupPriceRange instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<GroupPriceRange> query(String query, Map params, int begin, int max);
}
