package com.lakecloud.weixin.tools;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import com.lakecloud.core.tools.CommUtil;

/**
 * @info 微信开发App工具类
 * 
 */
@Component
public class WeixinTools {

	public Map<String, String> parse_xml(String xml) {
		Map map = new HashMap();
		if (!CommUtil.null2String(xml).equals("")) {
			Document doc;
			try {
				doc = DocumentHelper.parseText(xml);
				String ToUserName = doc.selectSingleNode("xml/ToUserName") != null ? doc
						.selectSingleNode("xml/ToUserName").getText() : "";
				String FromUserName = doc.selectSingleNode("xml/FromUserName") != null ? doc
						.selectSingleNode("xml/FromUserName").getText() : "";
				String CreateTime = doc.selectSingleNode("xml/CreateTime") != null ? doc
						.selectSingleNode("xml/CreateTime").getText() : "";
				String MsgType = doc.selectSingleNode("xml/MsgType") != null ? doc
						.selectSingleNode("xml/MsgType").getText() : "";
				String Content = doc.selectSingleNode("xml/Content") != null ? doc
						.selectSingleNode("xml/Content").getText() : "";
				String MsgId = doc.selectSingleNode("xml/MsgId") != null ? doc
						.selectSingleNode("xml/MsgId").getText() : "";
				String Event = doc.selectSingleNode("xml/Event") != null ? doc
						.selectSingleNode("xml/Event").getText() : "";
				String EventKey = doc.selectSingleNode("xml/EventKey") != null ? doc
						.selectSingleNode("xml/EventKey").getText() : "";
				map.put("ToUserName", ToUserName);
				map.put("FromUserName", FromUserName);
				map.put("CreateTime", CreateTime);
				map.put("MsgType", MsgType);
				map.put("Content", Content);
				map.put("MsgId", MsgId);
				map.put("Event", Event);
				map.put("EventKey", EventKey);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return map;
	}

	public static String reply_xml(String reply_type, Map<String, String> map,
			String content) {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("xml");// 创建根节点
		if (reply_type == null) {
			reply_type = "";
		}
		if (reply_type.equals("text") || reply_type.equals("event")) {
			Element ToUserName = root.addElement("ToUserName");
			ToUserName.addCDATA(map.get("FromUserName"));
			Element FromUserName = root.addElement("FromUserName");
			FromUserName.addCDATA(map.get("ToUserName"));
			Element CreateTime = root.addElement("CreateTime");
			CreateTime.addCDATA(map.get("CreateTime"));
			Element MsgType = root.addElement("MsgType");
			MsgType.addCDATA("text");
			Element Content = root.addElement("Content");
			Content.addCDATA(content);
		}
		if (reply_type.equals("image")) {
			Element ToUserName = root.addElement("ToUserName");
			ToUserName.addCDATA(map.get("FromUserName"));
			Element FromUserName = root.addElement("FromUserName");
			FromUserName.addCDATA(map.get("ToUserName"));
			Element CreateTime = root.addElement("CreateTime");
			CreateTime.addCDATA(map.get("CreateTime"));
			Element MsgType = root.addElement("MsgType");
			MsgType.addCDATA("text");
			Element Content = root.addElement("Content");
			Content.addCDATA(content);
		}
		if (reply_type.equals("location")) {
			Element ToUserName = root.addElement("ToUserName");
			ToUserName.addCDATA(map.get("FromUserName"));
			Element FromUserName = root.addElement("FromUserName");
			FromUserName.addCDATA(map.get("ToUserName"));
			Element CreateTime = root.addElement("CreateTime");
			CreateTime.addCDATA(map.get("CreateTime"));
			Element MsgType = root.addElement("MsgType");
			MsgType.addCDATA("text");
			Element Content = root.addElement("Content");
			Content.addCDATA(content);
		}
		if (reply_type.equals("link")) {
			Element ToUserName = root.addElement("ToUserName");
			ToUserName.addCDATA(map.get("FromUserName"));
			Element FromUserName = root.addElement("FromUserName");
			FromUserName.addCDATA(map.get("ToUserName"));
			Element CreateTime = root.addElement("CreateTime");
			CreateTime.addCDATA(map.get("CreateTime"));
			Element MsgType = root.addElement("MsgType");
			MsgType.addCDATA("text");
			Element Content = root.addElement("Content");
			Content.addCDATA(content);
		}
		if (reply_type.equals("event")) {
			Element ToUserName = root.addElement("ToUserName");
			ToUserName.addCDATA(map.get("FromUserName"));
			Element FromUserName = root.addElement("FromUserName");
			FromUserName.addCDATA(map.get("ToUserName"));
			Element CreateTime = root.addElement("CreateTime");
			CreateTime.addCDATA(map.get("CreateTime"));
			Element MsgType = root.addElement("MsgType");
			MsgType.addCDATA("text");
			Element Content = root.addElement("Content");
			Content.addCDATA(content);
		}
		if (reply_type.equals("music")) {
			Element ToUserName = root.addElement("ToUserName");
			ToUserName.addCDATA(map.get("FromUserName"));
			Element FromUserName = root.addElement("FromUserName");
			FromUserName.addCDATA(map.get("ToUserName"));
			Element CreateTime = root.addElement("CreateTime");
			CreateTime.addCDATA(map.get("CreateTime"));
			Element MsgType = root.addElement("MsgType");
			MsgType.addCDATA("text");
			Element Content = root.addElement("Content");
			Content.addCDATA(content);
		}
		return doc
				.asXML()
				.replaceAll("<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?>",
						"").trim();
	}

}
