package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;

import com.lakecloud.foundation.domain.SnsAttention;

public interface ISnsAttentionService {
	/**
	 * 保存一个HomeAttention，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(SnsAttention instance);
	
	/**
	 * 根据一个ID得到HomeAttention
	 * 
	 * @param id
	 * @return
	 */
	SnsAttention getObjById(Long id);
	
	/**
	 * 删除一个HomeAttention
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除HomeAttention
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到HomeAttention
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个HomeAttention
	 * 
	 * @param id
	 *            需要更新的HomeAttention的id
	 * @param dir
	 *            需要更新的HomeAttention
	 */
	boolean update(SnsAttention instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<SnsAttention> query(String query, Map params, int begin, int max);
}
