package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;

import com.lakecloud.foundation.domain.SpareGoodsClass;

public interface ISpareGoodsClassService {
	/**
	 * 保存一个SpareGoodsClass，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(SpareGoodsClass instance);
	
	/**
	 * 根据一个ID得到SpareGoodsClass
	 * 
	 * @param id
	 * @return
	 */
	SpareGoodsClass getObjById(Long id);
	
	/**
	 * 删除一个SpareGoodsClass
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除SpareGoodsClass
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到SpareGoodsClass
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个SpareGoodsClass
	 * 
	 * @param id
	 *            需要更新的SpareGoodsClass的id
	 * @param dir
	 *            需要更新的SpareGoodsClass
	 */
	boolean update(SpareGoodsClass instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<SpareGoodsClass> query(String query, Map params, int begin, int max);
}
