package com.lakecloud.manage.admin.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lakecloud.foundation.domain.Area;
import com.lakecloud.foundation.service.IAreaService;

/**
 * 区域工具类
 * 
 * @author erikchang
 * 
 */
@Component
public class AreaManageTools {
	@Autowired
	private IAreaService areaService;

	/**
	 * 根据区域生成区域信息字符串
	 * 
	 * @param area
	 * @return
	 */
	public String generic_area_info(Area area) {
		String area_info = "";
		if (area != null) {
			area_info = area.getAreaName() + " ";
			if (area.getParent() != null) {
				String info = generic_area_info(area.getParent());
				area_info = info + area_info;
			}
		}
		return area_info;
	}
}
