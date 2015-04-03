/**
 * Wasu.com Inc.
 * Copyright (c) 2014-2015 All Rights Reserved.
 */

package com.ponycar.httpserver.test;

import java.util.List;
import java.util.Map;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ponycar.httpserver.annotation.RequestMapping;
import com.ponycar.httpserver.handler.Handler;
import com.ponycar.httpserver.server.Request;
import com.ponycar.httpserver.server.Response;

/**
 * @description
 * @author chenzehe
 * @email chenzehe@wasu.com
 * @create 2015年3月17日 下午9:24:53
 */

@RequestMapping("/example")
public class HttpServerTest implements Handler {

	public String invoke(Request request, Response response) {
		StringBuilder bulider = new StringBuilder();
		Map<String, List<String>> maps = request.getParams();
		for (String key : maps.keySet()) {
			bulider.append(key + ":" + request.getParam(key) + "\n");
		}
		bulider.append("cookie:" + request.getCookie("kk"));
		return bulider.toString();
	}

	public static void main(String[] args) {
		try {
			new ClassPathXmlApplicationContext("spring/applicationContext.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
