package com.lakecloud.core.ip;

/**
 * 
* <p>Title: IPEntry.java</p>

* <p>Description: 纯真ip查询，该类用来读取QQWry.dat中的的IP记录信息 </p>

 */
public class IPEntry {
	public String beginIp;
	public String endIp;
	public String country;
	public String area;

	/**
	 * 14. * 构造函数
	 */
	public IPEntry() {
		beginIp = endIp = country = area = "";
	}

}
