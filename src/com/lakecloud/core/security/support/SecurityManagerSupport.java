package com.lakecloud.core.security.support;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;

import com.lakecloud.core.security.SecurityManager;
import com.lakecloud.foundation.domain.Res;
import com.lakecloud.foundation.domain.Role;
import com.lakecloud.foundation.domain.User;
import com.lakecloud.foundation.service.IResService;
import com.lakecloud.foundation.service.IUserService;
/**
 * 
* <p>Title: SecurityManagerSupport.java</p>

* <p>Description:用户登录管理器，用户名、密码验证完成后进入该验证器，该验证器用来获取用户权限获取及外部系统同步登录,该控制用来控制用户权限加载，用户从前台登录，
 *       默认登陆时候loginRole为user
 *       ，此时只载入用户非Admin类型权限,用户从超级管理登陆页面登录，页面中包含表单字段loginRole=
 *       ADMIN，此时加载用户所有角色权限，也就是说从前端登陆的用户无法操作超级管理内容
 *       ，只有从超级管理登录页面登录的用户且该用户拥有超级管理权限，登录后才可以操作超级管理内容 </p>

* <p>Copyright: Copyright (c) 2012-2014</p>
 
 */
public class SecurityManagerSupport implements UserDetailsService,
		SecurityManager {
	@Autowired
	private IUserService userService;
	@Autowired
	private IResService resService;

	public UserDetails loadUserByUsername(String data)
			throws UsernameNotFoundException, DataAccessException {
		String[] list = data.split(",");
		String userName = list[0];
		String loginRole = "user";
		if (list.length == 2) {
			loginRole = list[1];
		}
		Map params = new HashMap();
		params.put("userName", userName);
		List<User> users = this.userService.query(
				"select obj from User obj where obj.userName =:userName ",
				params, -1, -1);
		if (users.isEmpty()) {
			throw new UsernameNotFoundException("User " + userName
					+ " has no GrantedAuthority");
		}
		User user = users.get(0);
		Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		if (!user.getRoles().isEmpty() && user.getRoles() != null) {
			Iterator<Role> roleIterator = user.getRoles().iterator();
			while (roleIterator.hasNext()) {
				Role role = roleIterator.next();
				if (loginRole.equalsIgnoreCase("ADMIN")) {
					GrantedAuthority grantedAuthority = new GrantedAuthorityImpl(
							role.getRoleCode().toUpperCase());
					// System.out.println(role.getRoleCode().toUpperCase()+"-------------"+grantedAuthority.getAuthority());
					authorities.add(grantedAuthority);
				} else {
					if (!role.getType().equals("ADMIN")) {
						GrantedAuthority grantedAuthority = new GrantedAuthorityImpl(
								role.getRoleCode().toUpperCase());
						authorities.add(grantedAuthority);
						// System.out.println(role.getRoleCode().toUpperCase()+"-------------"+grantedAuthority.getAuthority());
					}
				}
			}
		}
		GrantedAuthority[] auths = new GrantedAuthority[authorities.size()];
		user.setAuthorities(authorities.toArray(auths));
		return user;

	}

	public Map<String, String> loadUrlAuthorities() {
		Map<String, String> urlAuthorities = new HashMap<String, String>();
		Map params = new HashMap();
		params.put("type", "URL");
		List<Res> urlResources = this.resService.query(
				"select obj from Res obj where obj.type = :type", params, -1,
				-1);
		for (Res res : urlResources) {
			urlAuthorities.put(res.getValue(), res.getRoleAuthorities());
		}
		return urlAuthorities;
	}

}
