package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;

import com.lakecloud.foundation.domain.SpareGoods;

public interface ISpareGoodsService {
	/**
	 * 保存一个SpareGoods，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(SpareGoods instance);
	
	/**
	 * 根据一个ID得到SpareGoods
	 * 
	 * @param id
	 * @return
	 */
	SpareGoods getObjById(Long id);
	
	/**
	 * 删除一个SpareGoods
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除SpareGoods
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到SpareGoods
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个SpareGoods
	 * 
	 * @param id
	 *            需要更新的SpareGoods的id
	 * @param dir
	 *            需要更新的SpareGoods
	 */
	boolean update(SpareGoods instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<SpareGoods> query(String query, Map params, int begin, int max);
}
