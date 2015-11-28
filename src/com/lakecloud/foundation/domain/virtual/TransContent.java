package com.lakecloud.foundation.domain.virtual;

/**
 * @info 该类不对应任何数据表，用在解析快递接口数据使用
 
 * 
 */
public class TransContent {
	private String time;
	private String context;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

}
