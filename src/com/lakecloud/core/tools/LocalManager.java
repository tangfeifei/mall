package com.lakecloud.core.tools;

import java.util.Locale;
/**
 * 
* <p>Title: LocalManager.java</p>

* <p>Description: 管理线程的本地化对象服务，和国际化功能配合使用。</p>

* <p>Copyright: Copyright (c) 2012-2014</p>

* <p>Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net</p>

* @author erikzhang

* @date 2014-4-27

* @version LakeCloud_C2C 1.3
 */
public class LocalManager {
	private static Locale defaultLocal=Locale.getDefault();
	private static ThreadLocal<Locale> locale;
	private static ThreadLocal<Locale> customLocale;
	/**
	 * 得到当前线程的本地化信息对象
	 * @return 当前local
	 */
	public static Locale getCurrentLocal()
	{		
		//if(customLocale)
		return locale!=null?locale.get():Locale.getDefault();
	}
	/**
	 * 设置当前本地化对象。
	 * 应该在调用getCurrentLocal（）方法之前设置，否则得到的将是服务器端的本地化对象。
	 * @param newLocale
	 */
	public static void setLocale(Locale newLocale) {
		if(locale==null)locale=new ThreadLocal<Locale>();
		locale.set(newLocale);
	}	
	public static void setCustomLocale(Locale newLocale)
	{
		if(customLocale==null)customLocale=new  ThreadLocal<Locale>();
		customLocale.set(newLocale);
	}
	public static Locale getDefaultLocal() {
		return defaultLocal;
	}
	public static void setDefaultLocal(Locale defaultLocal) {
		LocalManager.defaultLocal = defaultLocal;
	}

}
