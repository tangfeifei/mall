package com.lakecloud.manage.admin.tools;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.tools.PopupAuthenticator;
import com.lakecloud.core.tools.SmsBase;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserService;

@Component
public class MsgTools {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;

	public boolean sendSMS(String mobile, String content)
			throws UnsupportedEncodingException {
		boolean result = true;
		String url = this.configService.getSysConfig().getSmsURL();
		String userName = this.configService.getSysConfig().getSmsUserName();
		String password = this.configService.getSysConfig().getSmsPassword();
		SmsBase sb = new SmsBase(Globals.DEFAULT_SMS_URL, userName, password);// 固定硬编码短信发送接口
		String ret = sb.SendSms(mobile, content);
		if (!ret.substring(0, 3).equals("000")) {
			result = false;
		}
		return result;
	}

	public boolean sendEmail(String email, String subject, String content) {
		boolean ret = true;
		String username = "";
		String password = "";
		String smtp_server = "";
		String from_mail_address = "";
		username = this.configService.getSysConfig().getEmailUserName();
		password = this.configService.getSysConfig().getEmailPws();
		smtp_server = this.configService.getSysConfig().getEmailHost();
		from_mail_address = this.configService.getSysConfig().getEmailUser();
		String to_mail_address = email;
		if (username != null && password != null && !username.equals("")
				&& !password.equals("") && smtp_server != null
				&& !smtp_server.equals("") && to_mail_address != null
				&& !to_mail_address.trim().equals("")) {
			Authenticator auth = new PopupAuthenticator(username, password);
			Properties mailProps = new Properties();
			mailProps.put("mail.smtp.auth", "true");
			mailProps.put("username", username);
			mailProps.put("password", password);
			mailProps.put("mail.smtp.host", smtp_server);
			Session mailSession = Session.getInstance(mailProps, auth);
			MimeMessage message = new MimeMessage(mailSession);
			try {
				message.setFrom(new InternetAddress(from_mail_address));
				message.setRecipient(Message.RecipientType.TO,
						new InternetAddress(to_mail_address));
				message.setSubject(subject);
				MimeMultipart multi = new MimeMultipart("related");
				BodyPart bodyPart = new MimeBodyPart();
				bodyPart.setDataHandler(new DataHandler(content,
						"text/html;charset=UTF-8"));// 网页格式
				// bodyPart.setText(content);
				multi.addBodyPart(bodyPart);
				message.setContent(multi);
				message.saveChanges();
				Transport.send(message);
				ret = true;
			} catch (AddressException e) {
				// TODO Auto-generated catch block
				ret = false;
				e.printStackTrace();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				ret = false;
				e.printStackTrace();
			}
		} else {
			ret = false;
		}
		return ret;
	}
}
