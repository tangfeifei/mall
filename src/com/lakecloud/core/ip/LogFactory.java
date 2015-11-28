package com.lakecloud.core.ip;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
* <p>Title: LogFactory.java</p>

* <p>Description: 纯真ip查询日志记录</p>

 */
public class LogFactory {

	private static final Logger logger;
	static {
		logger = Logger.getLogger("stdout");
		logger.setLevel(Level.INFO);
	}

	public static void log(String info, Level level, Throwable ex) {
		logger.log(level, info, ex);
	}

	public static Level getLogLevel() {
		return logger.getLevel();
	}

}
