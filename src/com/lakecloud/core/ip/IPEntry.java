package com.lakecloud.core.ip;

/**
 * 
* <p>Title: IPEntry.java</p>

* <p>Description: 纯真ip查询，该类用来读取QQWry.dat中的的IP记录信息 </p>

* <p>Copyright: Copyright (c) 2012-2014</p>

* <p>Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net</p>

* @author erikzhang

* @date 2014-4-27

* @version LakeCloud_C2C 1.3
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
