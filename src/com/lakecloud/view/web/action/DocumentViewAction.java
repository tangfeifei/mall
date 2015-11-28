package com.lakecloud.view.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.foundation.domain.Document;
import com.lakecloud.foundation.service.IDocumentService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;

/**
 * 系统文章前台显示控制器
 * 
 * @author erikchang
 * 
 */
@Controller
public class DocumentViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IDocumentService documentService;

	@RequestMapping("/doc.htm")
	public ModelAndView doc(HttpServletRequest request,
			HttpServletResponse response, String mark) {
		ModelAndView mv = new JModelAndView("doc.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		Document obj = this.documentService.getObjByProperty("mark", mark);
		mv.addObject("obj", obj);
		return mv;
	}
}
