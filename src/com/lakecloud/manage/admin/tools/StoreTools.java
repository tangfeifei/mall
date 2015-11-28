package com.lakecloud.manage.admin.tools;

import java.io.File;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.GoodsClass;
import com.lakecloud.foundation.domain.GoodsSpecProperty;
import com.lakecloud.foundation.domain.GoodsSpecification;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.SysConfig;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.IGoodsClassService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.IUserService;

/**
 * 
* <p>Title: StoreTools.java</p>

* <p>Description: 后台管理店铺、商品工具类</p>
 
 */
@Component
public class StoreTools {
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IUserService userService;

	/**
	 * 生成商品属性字符串
	 * 
	 * @param spec
	 * @return
	 */
	public String genericProperty(GoodsSpecification spec) {
		String val = "";
		for (GoodsSpecProperty gsp : spec.getProperties()) {
			val = val + "," + gsp.getValue();
		}
		if (!val.equals(""))
			return val.substring(1);
		else
			return "";
	}

	/**
	 * 根据系统规则建立图片存储文件夹
	 * 
	 * @param request
	 * @param config
	 * @param store
	 * @return
	 */
	public String createUserFolder(HttpServletRequest request,
			SysConfig config, Store store) {
		String path = "";
		String uploadFilePath = config.getUploadFilePath();
		if (config.getImageSaveType().equals("sidImg")) {// 按照文件名存放(例:/店铺id/图片)
			path = request.getSession().getServletContext().getRealPath("/")+File.separator
					+ uploadFilePath + File.separator + "store"
					+ File.separator + store.getId();

		}
		if (config.getImageSaveType().equals("sidYearImg")) {// 按照年份存放(例:/店铺id/年/图片)
			path = request.getSession().getServletContext().getRealPath("/")+File.separator
					+ uploadFilePath + File.separator + "store"
					+ File.separator + store.getId() + File.separator
					+ CommUtil.formatTime("yyyy", new Date());
		}
		if (config.getImageSaveType().equals("sidYearMonthImg")) {// 按照年月存放(例:/店铺id/年/月/图片)
			path = request.getSession().getServletContext().getRealPath("/")+File.separator
					+ uploadFilePath + File.separator + "store"
					+ File.separator + store.getId() + File.separator
					+ CommUtil.formatTime("yyyy", new Date()) + File.separator
					+ CommUtil.formatTime("MM", new Date());
		}
		if (config.getImageSaveType().equals("sidYearMonthDayImg")) {// 按照年月日存放(例:/店铺id/年/月/日/图片)
			path = request.getSession().getServletContext().getRealPath("/")+File.separator
					+ uploadFilePath + File.separator + "store"
					+ File.separator + store.getId() + File.separator
					+ CommUtil.formatTime("yyyy", new Date()) + File.separator
					+ CommUtil.formatTime("MM", new Date()) + File.separator
					+ CommUtil.formatTime("dd", new Date());
		}
		CommUtil.createFolder(path);
		return path;
	}

	public String createUserFolderURL(SysConfig config, Store store) {
		String path = "";
		String uploadFilePath = config.getUploadFilePath();
		if (config.getImageSaveType().equals("sidImg")) {// 按照文件名存放(例:/店铺id/图片)
			path = uploadFilePath + "/store/" + store.getId().toString();

		}
		if (config.getImageSaveType().equals("sidYearImg")) {// 按照年份存放(例:/店铺id/年/图片)
			path = uploadFilePath + "/store/" + store.getId() + "/"
					+ CommUtil.formatTime("yyyy", new Date());
		}
		if (config.getImageSaveType().equals("sidYearMonthImg")) {// 按照年月存放(例:/店铺id/年/月/图片)
			path = uploadFilePath + "/store/" + store.getId() + "/"
					+ CommUtil.formatTime("yyyy", new Date()) + "/"
					+ CommUtil.formatTime("MM", new Date());
		}
		if (config.getImageSaveType().equals("sidYearMonthDayImg")) {// 按照年月日存放(例:/店铺id/年/月/日/图片)
			path = uploadFilePath + "/store/" + store.getId() + "/"
					+ CommUtil.formatTime("yyyy", new Date()) + "/"
					+ CommUtil.formatTime("MM", new Date()) + "/"
					+ CommUtil.formatTime("dd", new Date());
		}
		return path;
	}

	/**
	 * 根据分类生成分类信息字符串
	 * 
	 * @param gc
	 * @return
	 */
	public String generic_goods_class_info(GoodsClass gc) {
		if (gc != null) {
			String goods_class_info = this.generic_the_goods_class_info(gc);
			return goods_class_info.substring(0, goods_class_info.length() - 1);
		} else
			return "";
	}

	private String generic_the_goods_class_info(GoodsClass gc) {
		if (gc != null) {
			String goods_class_info = gc.getClassName() + ">";
			if (gc.getParent() != null) {
				String class_info = generic_the_goods_class_info(gc.getParent());
				goods_class_info = class_info + goods_class_info;
			}
			return goods_class_info;
		} else
			return "";
	}

	public int query_store_with_user(String user_id) {
		int status = 0;
		Store store = this.storeService.getObjByProperty("user.id",
				CommUtil.null2Long(user_id));
		if (store != null)
			status = 1;
		return status;
	}

}
