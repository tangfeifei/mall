package com.lakecloud.view.web.action;

import java.util.ArrayList;
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

import com.lakecloud.core.domain.virtual.SysMap;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Chatting;
import com.lakecloud.foundation.domain.ChattingFriend;
import com.lakecloud.foundation.domain.ChattingLog;
import com.lakecloud.foundation.domain.SnsFriend;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.domain.query.ChattingLogQueryObject;
import com.lakecloud.foundation.service.IChattingFriendService;
import com.lakecloud.foundation.service.IChattingLogService;
import com.lakecloud.foundation.service.IChattingService;
import com.lakecloud.foundation.service.ISnsFriendService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;
import com.lakecloud.foundation.service.IUserService;
import com.lakecloud.manage.admin.tools.UserTools;

/**
 * @info 商城内置聊天控制器，商城自V1.3版开始增加网页聊天功能，卖家无须设置客服及时聊天工具即可完成聊天功能

 * 
 */
@Controller
public class ChattingViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private ISnsFriendService snsFriendService;
	@Autowired
	private UserTools userTools;
	@Autowired
	private IChattingFriendService chattingFriendService;
	@Autowired
	private IChattingLogService chattinglogService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IChattingService chattingService;

	/**
	 * 我的联系人，查询用户所有好友(在线与不在线都查询)，并显示在聊天面板中,面板中在线的好友头像为亮的，不在线的好友头像为暗的
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/chatting.htm")
	public ModelAndView chatting(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("chatting.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		// 查询所有好友
		Map params = new HashMap();
		params.put("uid", SecurityUserHolder.getCurrentUser().getId());
		List<SnsFriend> Friends = this.snsFriendService.query(
				"select obj from SnsFriend obj where obj.fromUser.id=:uid ",
				params, -1, -1);
		mv.addObject("Friends", Friends);
		mv.addObject("userTools", userTools);
		// 查询在线好友数量
		if (Friends.size() > 0) {
			int count = 0;
			for (SnsFriend friend : Friends) {
				boolean flag = this.userTools.userOnLine(friend.getToUser()
						.getUserName());
				if (flag) {
					count++;
				}
				mv.addObject("OnlineCount", count);
			}
		}
		// 查询最近联系人
		params.clear();
		params.put("uid", SecurityUserHolder.getCurrentUser().getId());
		List<ChattingFriend> Contactings = this.chattingFriendService
				.query("select obj from ChattingFriend obj where obj.user.id=:uid order by addTime desc",
						params, 0, 15);
		mv.addObject("Contactings", Contactings);
		// 未读信息
		params.clear();
		params.put("mark", 0);
		params.put("uid", SecurityUserHolder.getCurrentUser().getId());
		List<ChattingLog> unreads = this.chattinglogService
				.query("select obj from ChattingLog obj where obj.chatting.user1.id=:uid and obj.mark=:mark or obj.chatting.user2.id=:uid and obj.mark=:mark ",
						params, -1, -1);
		mv.addObject("unreads", unreads);
		List list = new ArrayList();
		for (int i = 1; i <= 60; i++) {
			list.add(i);
		}
		mv.addObject("emoticons", list);
		return mv;
	}

	/**
	 * 聊天记录刷新
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/chatting_refresh.htm")
	public ModelAndView chatting_refresh(HttpServletRequest request,
			HttpServletResponse response, String user_id) {
		ModelAndView mv = new JModelAndView("chatting_logs.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Chatting chatting = null;
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		if (SecurityUserHolder.getCurrentUser() != null
				&& !SecurityUserHolder.getCurrentUser().equals("")) {
			Map map = new HashMap();
			map.put("uid", SecurityUserHolder.getCurrentUser().getId());
			map.put("user_id", CommUtil.null2Long(user_id));
			List<Chatting> chattings = this.chattingService
					.query("select obj from Chatting obj where obj.user1.id=:uid and obj.user2.id=:user_id or obj.user1.id=:user_id and obj.user2.id=:uid",
							map, -1, -1);
			if (chattings.size() > 0) {
				chatting = chattings.get(0);
				// 查询对方发送未读聊天记录
				map.clear();
				map.put("chat_id", chatting.getId());
				map.put("mark", 0);
				map.put("user_id", CommUtil.null2Long(user_id));
				List<ChattingLog> logs = this.chattinglogService
						.query("select obj from ChattingLog obj where obj.chatting.id=:chat_id and obj.mark=:mark and obj.user.id=:user_id order by addTime asc",
								map, -1, -1);
				mv.addObject("logs", logs);
				for (ChattingLog log : logs) {
					if (log.getUser().getId() != SecurityUserHolder
							.getCurrentUser().getId()) {
						log.setMark(1);
						this.chattinglogService.update(log);
					}
				}
			}
		}
		return mv;
	}

	/**
	 * 聊天记录查询，每次可查看10条记录
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/chatting_ShowHistory.htm")
	public ModelAndView chatting_ShowHistory(HttpServletRequest request,
			HttpServletResponse response, String user_id, String currentPage) {
		ModelAndView mv = new JModelAndView("chatting_logs.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Chatting chatting = null;
		if (SecurityUserHolder.getCurrentUser() != null
				&& !SecurityUserHolder.getCurrentUser().equals("")) {
			Map map = new HashMap();
			map.put("uid", SecurityUserHolder.getCurrentUser().getId());
			map.put("user_id", CommUtil.null2Long(user_id));
			List<Chatting> chattings = this.chattingService
					.query("select obj from Chatting obj where obj.user1.id=:uid and obj.user2.id=:user_id or obj.user1.id=:user_id and obj.user2.id=:uid",
							map, -1, -1);
			if (chattings.size() > 0) {
				chatting = chattings.get(0);
				// 分页查询聊天记录
				ChattingLogQueryObject qo = new ChattingLogQueryObject(
						currentPage, mv, null, null);
				qo.addQuery("obj.chatting.id", new SysMap("chatting_id",
						chatting.getId()), "=");
				qo.setOrderBy("addTime");
				qo.setOrderType("desc");
				qo.setPageSize(10);
				IPageList pList = this.chattinglogService.list(qo);
				// System.out.println(pList.getResult().size());
				mv.addObject("historys", pList.getResult());
				String Ajax_url = CommUtil.getURL(request)
						+ "/chatting_ShowHistory.htm";
				mv.addObject(
						"gotoPageAjaxHTML",
						CommUtil.showPageAjaxHtml(Ajax_url, "",
								pList.getCurrentPage(), pList.getPages()));
			}
		}
		return mv;
	}

	/**
	 * 聊天记录保存，保存之前查看是否存在两个人的对话，存在的话在原有对话基础上保存聊天记录，没有新建
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 *            ：与用户对话的人
	 * @param content
	 * @return
	 */
	@RequestMapping("/chatting_save.htm")
	public ModelAndView chatting_save(HttpServletRequest request,
			HttpServletResponse response, String user_id, String content) {
		ModelAndView mv = new JModelAndView("chatting_logs.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Chatting chatting = null;
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		Map map = new HashMap();
		map.put("uid", SecurityUserHolder.getCurrentUser().getId());
		map.put("user_id", CommUtil.null2Long(user_id));
		List<Chatting> chattings = this.chattingService
				.query("select obj from Chatting obj where obj.user1.id=:uid and obj.user2.id=:user_id or obj.user1.id=:user_id and obj.user2.id=:uid",
						map, -1, -1);
		if (chattings.size() > 0) {
			chatting = chattings.get(0);
		} else {
			chatting = new Chatting();
			chatting.setAddTime(new Date());
			chatting.setUser1(SecurityUserHolder.getCurrentUser());
			chatting.setUser2(user);
			this.chattingService.save(chatting);
		}
		ChattingLog log = new ChattingLog();
		log.setAddTime(new Date());
		log.setUser(SecurityUserHolder.getCurrentUser());
		log.setContent(content);
		log.setChatting(chatting);
		this.chattinglogService.save(log);
		// 更新我的最近联系人
		map.clear();
		map.put("uid", SecurityUserHolder.getCurrentUser().getId());
		map.put("user_id", CommUtil.null2Long(user_id));
		List<ChattingFriend> ChattingFriends = this.chattingFriendService
				.query("select obj from ChattingFriend obj where obj.user.id=:uid and obj.friendUser.id=:user_id",
						map, -1, -1);
		if (ChattingFriends.size() == 0) {
			ChattingFriend contact = new ChattingFriend();
			contact.setAddTime(new Date());
			contact.setUser(SecurityUserHolder.getCurrentUser());
			contact.setFriendUser(user);
			this.chattingFriendService.save(contact);
		}
		// 更新TA的最近联系人
		map.clear();
		map.put("uid", CommUtil.null2Long(user_id));
		map.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		List<ChattingFriend> ChattingFriends2 = this.chattingFriendService
				.query("select obj from ChattingFriend obj where obj.user.id=:uid and obj.friendUser.id=:user_id",
						map, -1, -1);
		if (ChattingFriends2.size() == 0) {
			ChattingFriend contact = new ChattingFriend();
			contact.setAddTime(new Date());
			contact.setUser(user);
			contact.setFriendUser(SecurityUserHolder.getCurrentUser());
			this.chattingFriendService.save(contact);
		}

		// 保存完会话信息后立刻将该会话信息显示在会话窗口中
		map.clear();
		map.put("chat_id", chatting.getId());
		map.put("uid", SecurityUserHolder.getCurrentUser().getId());
		List<ChattingLog> logs = this.chattinglogService
				.query("select obj from ChattingLog obj where obj.chatting.id=:chat_id  and obj.user.id=:uid order by addTime desc",
						map, 0, 1);
		mv.addObject("logs", logs);
		return mv;
	}
}
