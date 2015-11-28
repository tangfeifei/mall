package com.lakecloud.lucene;

import java.util.ArrayList;
import java.util.List;

import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.Store;

public class LuceneResult {
	private List<LuceneVo> vo_list = new ArrayList<LuceneVo>();
	private int pages;// 总页数
	private int rows;// 总记录数
	private int currentPage;// 当前页码
	private int pageSize;// 每页大小
	private List<Goods> goods_list = new ArrayList<Goods>();// 查询LuceneVo结果前台显示需要转换到该list中
	private List<Store> store_list = new ArrayList<Store>();// 查询LuceneVo结果前台显示需要转换到该list中

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<LuceneVo> getVo_list() {
		return vo_list;
	}

	public void setVo_list(List<LuceneVo> vo_list) {
		this.vo_list = vo_list;
	}

	public List<Goods> getGoods_list() {
		return goods_list;
	}

	public void setGoods_list(List<Goods> goods_list) {
		this.goods_list = goods_list;
	}

	public List<Store> getStore_list() {
		return store_list;
	}

	public void setStore_list(List<Store> store_list) {
		this.store_list = store_list;
	}
}
