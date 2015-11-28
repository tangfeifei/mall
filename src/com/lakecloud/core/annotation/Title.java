package com.lakecloud.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 
* <p>Title: Title.java</p>

* <p>Description:系统自动生成模板标签，通过该标签控制POJO中的自动和html模板中的中文列名对应 </p>

 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Title {
	/**
	 * 
	 * @return
	 */
	public String value() default "";
}
