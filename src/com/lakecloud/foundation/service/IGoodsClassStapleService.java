package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;

import com.lakecloud.foundation.domain.GoodsClassStaple;

public interface IGoodsClassStapleService {
	/**
	 * 保存一个GoodsClassStaple，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(GoodsClassStaple instance);
	
	/**
	 * 根据一个ID得到GoodsClassStaple
	 * 
	 * @param id
	 * @return
	 */
	GoodsClassStaple getObjById(Long id);
	
	/**
	 * 删除一个GoodsClassStaple
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除GoodsClassStaple
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到GoodsClassStaple
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个GoodsClassStaple
	 * 
	 * @param id
	 *            需要更新的GoodsClassStaple的id
	 * @param dir
	 *            需要更新的GoodsClassStaple
	 */
	boolean update(GoodsClassStaple instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<GoodsClassStaple> query(String query, Map params, int begin, int max);
}
