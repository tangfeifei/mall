package com.lakecloud.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.query.support.IQueryObject;

import com.lakecloud.foundation.domain.ChattingFriend;

public interface IChattingFriendService {
	/**
	 * 保存一个ChattingFriend，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(ChattingFriend instance);
	
	/**
	 * 根据一个ID得到ChattingFriend
	 * 
	 * @param id
	 * @return
	 */
	ChattingFriend getObjById(Long id);
	
	/**
	 * 删除一个ChattingFriend
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除ChattingFriend
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到ChattingFriend
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个ChattingFriend
	 * 
	 * @param id
	 *            需要更新的ChattingFriend的id
	 * @param dir
	 *            需要更新的ChattingFriend
	 */
	boolean update(ChattingFriend instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<ChattingFriend> query(String query, Map params, int begin, int max);
}
