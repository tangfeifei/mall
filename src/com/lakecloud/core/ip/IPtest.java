package com.lakecloud.core.ip;

/**
 * 
* <p>Title: IPtest.java</p>

* <p>Description: </p>

* <p>Copyright: Copyright (c) 2012-2014</p>

* <p>Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net</p>

* @author erikzhang

* @date 2014-4-27

* @version LakeCloud_C2C 1.3
 */
public class IPtest {
	public static void main(String[] args) {
		// 指定纯真数据库的文件名，所在文件夹
		IPSeeker ip = new IPSeeker("QQWry.Dat", "f:/");
		String temp="192.168.1.1";
		// 测试IP 58.20.43.13
		System.out.println(ip.getIPLocation(temp).getCountry() + ":"+ ip.getIPLocation(temp).getArea());
	}
	

}
