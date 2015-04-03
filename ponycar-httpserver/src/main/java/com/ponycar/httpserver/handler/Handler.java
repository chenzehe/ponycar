/**
 * Wasu.com Inc.
 * Copyright (c) 2014-2015 All Rights Reserved.
 */

package com.ponycar.httpserver.handler;

import com.ponycar.httpserver.server.Request;
import com.ponycar.httpserver.server.Response;

/**
 * @description 暴露给用户的接口，用户自定义类实现该接口即可被Server调用，
 *              返回用户响应结果，注意该类的对象为单例
 * @author chenzehe
 * @email chenzehe@wasu.com
 * @create 2015年3月16日 上午8:02:46
 */

public interface Handler {
	String invoke(Request request, Response response);
}
