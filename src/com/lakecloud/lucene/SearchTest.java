package com.lakecloud.lucene;

import java.util.Date;
import java.util.List;

public class SearchTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LuceneUtil lucence = LuceneUtil.instance();
		lucence.setIndex_path("F:\\JAVA_PRO\\lakecloud\\luence\\goods");
		Date d1 = new Date();
		LuceneResult list = lucence.search("手提包", 0, 0, 500.0, null, null);
		Date d2 = new Date();
		System.out.println("查询时间为：" + (d2.getTime() - d1.getTime()) + "毫秒");
		for (int i = 0; i < list.getVo_list().size(); i++) {
			LuceneVo vo = list.getVo_list().get(i);
			System.out.println("标题："+vo.getVo_title());
			System.out.println("价格:" + vo.getVo_store_price());
			System.out.println("添加时间:"+vo.getVo_add_time());
		}
		System.out.println("查询结果为:" + list.getVo_list().size());

	}
}
