package com.lakecloud.core.domain.virtual;

import java.util.Date;

/**
 * 
* <p>Title: ShopData.java</p>

* <p>Description:数据备份信息，该信息对应备份文件夹，和数据库表无关联 </p>

* <p>Copyright: Copyright (c) 2012-2014</p>

* <p>Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net</p>

* @author erikzhang

* @date 2014-4-27

* @version LakeCloud_C2C 1.3
 */
public class ShopData {
	private String name;// 备份名称
	private String phyPath;// 存储的物理路径
	private double size;// 数据大小
	private int boundSize;// 分卷数
	private Date addTime;// 备份时间

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhyPath() {
		return phyPath;
	}

	public void setPhyPath(String phyPath) {
		this.phyPath = phyPath;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public int getBoundSize() {
		return boundSize;
	}

	public void setBoundSize(int boundSize) {
		this.boundSize = boundSize;
	}
}
