/**
 * Wasu.com Inc.
 * Copyright (c) 2014-2015 All Rights Reserved.
 */

package com.ponycar.httpserver.test;

import java.util.List;
import java.util.Map;

import com.ponycar.httpserver.annotation.RequestMapping;
import com.ponycar.httpserver.handler.Handler;
import com.ponycar.httpserver.server.Request;
import com.ponycar.httpserver.server.Response;

/**
 * @description
 * @author chenzehe
 * @email chenzehe@wasu.com
 * @create 2015年3月31日 下午9:57:33
 */
@RequestMapping("/student")
public class StudentHandle implements Handler {

	@Override
	public String invoke(Request request, Response response) {
		StringBuilder bulider = new StringBuilder();
		Map<String, List<String>> maps = request.getParams();
		for (String key : maps.keySet()) {
			bulider.append(key + ":" + request.getParam(key) + "\n");
		}
		return bulider.toString();
	}
	
}
