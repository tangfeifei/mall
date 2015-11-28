package com.lakecloud.core.security;

import java.util.Map;

/**
 * 
 * <p>
 * Title: SecurityManager.java
 * </p>
 * 
 * <p>
 * Description: 权限管理接口
  
 */
public interface SecurityManager {

	public Map<String, String> loadUrlAuthorities();

}
