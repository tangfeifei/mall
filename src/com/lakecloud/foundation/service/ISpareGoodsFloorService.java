package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;

import com.lakecloud.foundation.domain.SpareGoodsFloor;

public interface ISpareGoodsFloorService {
	/**
	 * 保存一个SpareGoodsFloor，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(SpareGoodsFloor instance);
	
	/**
	 * 根据一个ID得到SpareGoodsFloor
	 * 
	 * @param id
	 * @return
	 */
	SpareGoodsFloor getObjById(Long id);
	
	/**
	 * 删除一个SpareGoodsFloor
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除SpareGoodsFloor
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到SpareGoodsFloor
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个SpareGoodsFloor
	 * 
	 * @param id
	 *            需要更新的SpareGoodsFloor的id
	 * @param dir
	 *            需要更新的SpareGoodsFloor
	 */
	boolean update(SpareGoodsFloor instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<SpareGoodsFloor> query(String query, Map params, int begin, int max);
}
