/**
 * Wasu.com Inc.
 * Copyright (c) 2014-2015 All Rights Reserved.
 */

package com.ponycar.httpserver.handler;

import java.util.HashMap;
import java.util.Map;

/**
 * @description
 * @author chenzehe
 * @email chenzehe@wasu.com
 * @create 2015年3月17日 下午7:45:44
 */

public class HandlerMap {
	/** Handle映射表 */
	private static Map<String, Handler> handleMap = new HashMap<String, Handler>();

	public static void addHandle(String key, Class<? extends Handler> handleClass) throws Exception {
		handleMap.put(key, (Handler) handleClass.newInstance());
	}

	public static void addHandle(String key, Handler handle) {
		handleMap.put(key, handle);
	}

	public static Handler getHandle(String key) {
		return handleMap.get(key);
	}
}
