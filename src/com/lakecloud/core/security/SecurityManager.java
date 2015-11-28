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
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012-2014
 * </p>
 * 
 * <p>
 * Company: 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-4-27
 * 
 * @version LakeCloud_C2C 1.3
 */
public interface SecurityManager {

	public Map<String, String> loadUrlAuthorities();

}
