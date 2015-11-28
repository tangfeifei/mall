package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;

import com.lakecloud.foundation.domain.MobileVerifyCode;

public interface IMobileVerifyCodeService {
	/**
	 * 保存一个MobileVerifyCode，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(MobileVerifyCode instance);

	/**
	 * 根据一个ID得到MobileVerifyCode
	 * 
	 * @param id
	 * @return
	 */
	MobileVerifyCode getObjById(Long id);

	/**
	 * 删除一个MobileVerifyCode
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);

	/**
	 * 批量删除MobileVerifyCode
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);

	/**
	 * 通过一个查询对象得到MobileVerifyCode
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);

	/**
	 * 更新一个MobileVerifyCode
	 * 
	 * @param id
	 *            需要更新的MobileVerifyCode的id
	 * @param dir
	 *            需要更新的MobileVerifyCode
	 */
	boolean update(MobileVerifyCode instance);

	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<MobileVerifyCode> query(String query, Map params, int begin, int max);

	/**
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	MobileVerifyCode getObjByProperty(String propertyName, Object value);
}
