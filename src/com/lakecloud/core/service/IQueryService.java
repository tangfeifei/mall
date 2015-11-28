package com.lakecloud.core.service;

import java.util.List;
import java.util.Map;
/**
 * 
* <p>Title: IQueryService.java</p>

* <p>Description:基础查询service接口 </p>

* <p>Copyright: Copyright (c) 2012-2014</p>
 

* @author erikzhang

* @date 2014-4-27

* @version LakeCloud_C2C 1.3
 */
public interface IQueryService {
	List query(String scope, Map params, int page, int pageSize);

}
