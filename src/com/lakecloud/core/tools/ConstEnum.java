package com.lakecloud.core.tools;

import java.util.regex.Pattern;


/***********
 * 用于维护系统中常量键名
 * @author tangf
 * 
 */
public class ConstEnum {
	public final static String MOBILE_VALIDATE_CODE ="MOBILE_VALIDATE_CODE" ;//手机验证码信息
	public final static long   VALICODE_INVALIATE_TIME = -1; //手机验证码失效时间 ， 负数表示永不失效  ， 按毫秒计时；
	public final static String TOKENKEY = "TOKENKEY_DARUNWORLD" ;//用于设置系统中的tokenKEY
	public final static Pattern MOBILEPATTER = Pattern.compile("^\\d{11}$"); //手机号码验证表达式
	
}
