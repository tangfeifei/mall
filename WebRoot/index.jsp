<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="net.sf.ehcache.CacheManager"%>
<%@ page import="net.sf.ehcache.Ehcache"%>
<%@ page import="net.sf.ehcache.constructs.blocking.BlockingCache"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
  
  <body>
    <%
    	CacheManager manager = CacheManager.create();
		BlockingCache cache = new BlockingCache(manager
				.getEhcache("SimplePageFragmentCachingFilter"));
		int data_cache_size = 0;
		long cache_memory_size = 0;
		for (String name : manager.getCacheNames()) {
			data_cache_size = data_cache_size
					+ (manager.getCache(name) != null ? manager.getCache(name)
							.getSize() : 0);
			cache_memory_size = cache_memory_size
					+ (manager.getCache(name) != null ? manager.getCache(name)
							.getMemoryStoreSize() : 0);
		}
    
     %>
     <li>数据缓存:<%= data_cache_size%></li>
     <li>页面缓存：<%=cache.getSize() %></li>
     <li>缓存占用内存：<%=cache.getSize() %></li>
  </body>
</html>
