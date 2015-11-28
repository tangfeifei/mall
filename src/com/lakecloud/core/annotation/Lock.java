package com.lakecloud.core.annotation;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
* <p>Title: Lock.java</p>

* <p>Description: 使用webForm toPO方法时候，需要保护的字段使用该标签控制，避免非法用户使用自定义表单修改数据</p>

 */
@Target({ METHOD, CONSTRUCTOR, FIELD })
@Retention(RUNTIME)
public @interface Lock {
	

}
