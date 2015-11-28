package com.lakecloud.core.tools;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
/**
 * 
* <p>Title: PopupAuthenticator.java</p>

* <p>Description:邮件发送权限管理类 </p>

 
 */
public class PopupAuthenticator extends Authenticator {
	private String username;
	private String password;

	public PopupAuthenticator(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(this.username, this.password);
	}

}
