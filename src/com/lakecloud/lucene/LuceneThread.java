package com.lakecloud.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LuceneThread implements Runnable {
	private String path;
	private List<LuceneVo> vo_list = new ArrayList<LuceneVo>();

	public LuceneThread(String path, List<LuceneVo> vo_list) {
		super();
		this.path = path;
		this.vo_list = vo_list;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		LuceneUtil lucene = LuceneUtil.instance();
		lucene.setIndex_path(this.path);
		lucene.deleteAllIndex(true);
		try {
			lucene.writeIndex(vo_list);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
