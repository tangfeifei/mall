package com.lakecloud.manage.seller.action;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.core.tools.WebForm;
import com.lakecloud.foundation.domain.Accessory;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.WaterMark;
import com.lakecloud.foundation.service.IAccessoryService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.foundation.service.IWaterMarkService;

/**
 * @info 水印管理控制器，卖家可以管理图片的文字水印、图片水印
  
 * 
 */
@Controller
public class WaterMarkSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IWaterMarkService watermarkService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IUserService userService;

	@SecurityMapping(title = "图片水印", value = "/seller/watermark.htm*", rtype = "seller", rname = "图片管理", rcode = "album_seller", rgroup = "其他设置")
	@RequestMapping("/seller/watermark.htm")
	public ModelAndView watermark(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/watermark.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Store store = this.userService.getObjById(
				SecurityUserHolder.getCurrentUser().getId()).getStore();
		if (store != null) {
			Map params = new HashMap();
			params.put("store_id", store.getId());
			List<WaterMark> wms = this.watermarkService
					.query("select obj from WaterMark obj where obj.store.id=:store_id",
							params, -1, -1);
			if (wms.size() > 0) {
				mv.addObject("obj", wms.get(0));
			}
		}
		return mv;
	}

	/**
	 * watermark保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "图片水印保存", value = "/seller/watermark_save.htm*", rtype = "seller", rname = "图片管理", rcode = "album_seller", rgroup = "其他设置")
	@RequestMapping("/seller/watermark_save.htm")
	public ModelAndView watermark_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd) {
		ModelAndView mv = null;
		if (SecurityUserHolder.getCurrentUser().getStore() != null) {
			WebForm wf = new WebForm();
			WaterMark watermark = null;
			if (id.equals("")) {
				watermark = wf.toPo(request, WaterMark.class);
				watermark.setAddTime(new Date());
			} else {
				WaterMark obj = this.watermarkService.getObjById(Long
						.parseLong(id));
				watermark = (WaterMark) wf.toPo(request, obj);
			}
			watermark.setStore(SecurityUserHolder.getCurrentUser().getStore());
			String path = request.getSession().getServletContext()
					.getRealPath("/")
					+ "upload/wm";
			try {
				Map map = CommUtil.saveFileToServer(request, "wm_img", path,
						null, null);
				if (!map.get("fileName").equals("")) {
					Accessory wm_image = new Accessory();
					wm_image.setAddTime(new Date());
					wm_image.setHeight(CommUtil.null2Int(map.get("height")));
					wm_image.setName(CommUtil.null2String(map.get("fileName")));
					wm_image.setPath("upload/wm");
					wm_image.setSize(CommUtil.null2Float(map.get("fileSize")));
					wm_image.setUser(SecurityUserHolder.getCurrentUser());
					wm_image.setWidth(CommUtil.null2Int("width"));
					this.accessoryService.save(wm_image);
					watermark.setWm_image(wm_image);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (id.equals("")) {
				this.watermarkService.save(watermark);
			} else
				this.watermarkService.update(watermark);
			mv = new JModelAndView("success.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "水印设置成功");
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您尚未开店");
		}
		mv.addObject("url", CommUtil.getURL(request) + "/seller/watermark.htm");
		return mv;
	}
}