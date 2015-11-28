package com.lakecloud.core.tools;

import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;
/**
 * 
* <p>Title: TemplateEngine.java</p>

* <p>Description: velcocity模板合成引擎，用来合成静态html信息</p>

* <p>Copyright: Copyright (c) 2012-2014</p>

* <p>Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net</p>

* @author erikzhang

* @date 2014-4-27

* @version LakeCloud_C2C 1.3
 */
@Component
public class TemplateEngine {
	@Autowired
	private VelocityEngine velocityEngine;

	public String generateWithTemplate(String templateName, Map map) {
		try {
			return VelocityEngineUtils.mergeTemplateIntoString(
					this.velocityEngine, templateName, "UTF-8", map);
		} catch (VelocityException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String generateWithString(String content, Map map) {
		try {
			StringWriter writer = new StringWriter();
			VelocityEngineUtils.mergeTemplate(this.velocityEngine, content,
					map, writer);
			return writer.toString();
		} catch (VelocityException e) {
			e.printStackTrace();
		}
		return "";
	}
}
