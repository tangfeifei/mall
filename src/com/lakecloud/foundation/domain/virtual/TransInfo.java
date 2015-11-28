package com.lakecloud.foundation.domain.virtual;

import java.util.ArrayList;
import java.util.List;

/**
 * @info 该类不对应任何数据表，用在解析快递接口数据使用
 * @since v1.3
 * @version V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
public class TransInfo {
	private String message;
	private String status;
	private String state;
	List<TransContent> data = new ArrayList<TransContent>();

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List<TransContent> getData() {
		return data;
	}

	public void setData(List<TransContent> data) {
		this.data = data;
	}

}
