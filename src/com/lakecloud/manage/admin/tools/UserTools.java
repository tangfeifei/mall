package com.lakecloud.manage.admin.tools;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.concurrent.SessionInformation;
import org.springframework.security.concurrent.SessionRegistry;
import org.springframework.stereotype.Component;

import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.IUserService;

/**
 * @info 在线用户查询管理工具类，查询所有登录用户，判断用户是否在线
 * @since V1.3
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Component
public class UserTools {
	@Autowired
	private SessionRegistry sessionRegistry;
	@Autowired
	private IUserService userSerivce;

	/**
	 * 获取所有在线用户列表
	 * 
	 * @return
	 */
	public List<User> query_user() {
		List<User> users = new ArrayList<User>();
		Object[] objs = this.sessionRegistry.getAllPrincipals();
		for (int i = 0; i < objs.length; i++) {
			User user = this.userSerivce.getObjByProperty("userName", CommUtil
					.null2String(objs[i]));
			// System.out.println(user.getUsername());
			users.add(user);
			// 第二个参数是否单点登录时，第二个登录用户被弹出
			// SessionInformation[] ilist = this.sessionRegistry.getAllSessions(
			// objs[i], true);
			// System.out.println(user.getUsername() + ilist.length + "处登录");
			// for (int j = 0; j < ilist.length; j++) {
			// SessionInformation sif = ilist[i];
			// user = (User) this.userSerivce.getObjByProperty("userName",
			// CommUtil.null2String(sif.getPrincipal()));
			// // 以下两种方法踢出用户
			// sif.expireNow();
			// this.sessionRegistry.removeSessionInformation(sif
			// .getSessionId());
			// }
		}
		return users;
	}

	/**
	 * 根据用户名判断用户是否在线
	 * 
	 * @param userName
	 * @return
	 */
	public boolean userOnLine(String userName) {
		boolean ret = false;
		List<User> users = this.query_user();
		for (User user : users) {
			if (user != null && user.getUsername().equals(userName.trim())) {
				ret = true;
			}
		}
		return ret;
	}
}
