/**
 * Wasu.com Inc.
 * Copyright (c) 2014-2015 All Rights Reserved.
 */

package com.ponycar.httpserver.server;

import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ponycar.httpserver.utils.StringUtils;

/**
 * @description 对Request对象的封装，暴露给用户
 * @author chenzehe
 * @email chenzehe@wasu.com
 * @create 2015年3月16日 上午8:20:45
 */

public class Request {

	private String httpVersion;
	private String uri;
	private String path;
	private String method;
	private Map<String, String> headers = new HashMap<String, String>();
	private Map<String, List<String>> params = new HashMap<String, List<String>>();
	private Map<String, Cookie> cookies = new HashMap<String, Cookie>();

	private Request() {
	}

	/**
	 * 构建Request对象
	 * 
	 * @param nettyRequest
	 *            Netty的HttpRequest
	 * @return Request
	 */
	public static Request builderRequest(HttpRequest nettyRequest) {
		final Request request = new Request();
		request.uri = nettyRequest.getUri();
		request.path = buildPath(request.uri);
		request.httpVersion = nettyRequest.getProtocolVersion().text();
		request.method = nettyRequest.getMethod().name();
		request.buildHeadersAndCookies(nettyRequest.headers());
		request.buildParams(new QueryStringDecoder(request.uri));
		return request;
	}

	/**
	 * @return 是否为长连接
	 */
	public boolean isKeepAlive() {
		final String connection = getHeader(Names.CONNECTION.toString());
		// 无论任何版本Connection为close时都关闭连接
		if (Values.CLOSE.toString().equalsIgnoreCase(connection)) {
			return false;
		}

		// HTTP/1.0只有Connection为Keep-Alive时才会保持连接
		if (HttpVersion.HTTP_1_0.text().equals(getHttpVersion()) && (!Values.KEEP_ALIVE.toString().equalsIgnoreCase(connection))) {
			return false;
		}
		// HTTP/1.1默认打开Keep-Alive
		return true;
	}

	/**
	 * 获得请求参数<br>
	 * 数组类型值，常用于表单中的多选框
	 * 
	 * @param name
	 *            参数名
	 * @return 数组
	 */
	public List<String> getArrayParam(String name) {
		return params.get(name);
	}

	/**
	 * 获得所有请求参数
	 * 
	 * @return Map
	 */
	public Map<String, List<String>> getParams() {
		return params;
	}

	/**
	 * @param name
	 *            参数名
	 * @return 获得请求参数
	 */
	public String getParam(String name) {
		final List<String> values = params.get(name);
		if (values == null || values.size() < 1) {
			return null;
		}
		return values.get(0);
	}

	/**
	 * 获得指定的Cookie
	 * 
	 * @param name
	 *            cookie名
	 * @return Cookie对象
	 */
	public Cookie getCookie(String name) {
		return cookies.get(name);
	}

	/**
	 * @return 获得所有Cookie信息
	 */
	public Map<String, Cookie> getCookies() {
		return this.cookies;
	}

	/**
	 * 获得所有头信息
	 * 
	 * @return 头信息Map
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}

	/**
	 * 使用ISO8859_1字符集获得Header内容<br>
	 * 由于Header中很少有中文，故一般情况下无需转码
	 * 
	 * @param headerKey
	 *            头信息的KEY
	 * @return 值
	 */
	public String getHeader(String headerKey) {
		return headers.get(headerKey);
	}

	/**
	 * 获得版本信息
	 * 
	 * @return 版本
	 */
	public String getHttpVersion() {
		return httpVersion;
	}

	/**
	 * 获得URI（带参数的路径）
	 * 
	 * @return URI
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @return 获得path（不带参数的路径）
	 */
	public String getPath() {
		return path;
	}

	/**
	 * 获得Http方法
	 * 
	 * @return Http method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @return 是否为普通表单（application/x-www-form-urlencoded）
	 */
	public boolean isXWwwFormUrlencoded() {
		return "application/x-www-form-urlencoded".equals(getHeader("Content-Type"));
	}

	/**
	 * 填充头部信息和Cookie信息
	 * 
	 * @param headers
	 *            HttpHeaders
	 */
	private void buildHeadersAndCookies(HttpHeaders headers) {
		for (Entry<String, String> entry : headers) {
			this.headers.put(entry.getKey(), entry.getValue());
		}
		final String cookieString = this.headers.get(Names.COOKIE.toString());
		if (StringUtils.isNotBlank(cookieString)) {
			final Set<Cookie> cookies = CookieDecoder.decode(cookieString);
			for (Cookie cookie : cookies) {
				this.cookies.put(cookie.getName(), cookie);
			}
		}
	}

	/**
	 * 填充参数
	 * 
	 * @param decoder
	 *            QueryStringDecoder
	 */
	private void buildParams(QueryStringDecoder decoder) {
		if (null != decoder) {
			for (Entry<String, List<String>> entry : decoder.parameters().entrySet()) {
				this.params.put(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * 从uri中获得path
	 * 
	 * @param uriStr
	 *            uri
	 * @return path
	 */
	private final static String buildPath(String uriStr) {
		URI uri = null;
		try {
			uri = new URI(uriStr);
		} catch (URISyntaxException e) {
			return null;
		}

		return uri.getPath();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("\r\nhttpVersion: ").append(httpVersion).append("\r\n");
		sb.append("uri: ").append(uri).append("\r\n");
		sb.append("method: ").append(method).append("\r\n");
		sb.append("headers: ").append(headers).append("\r\n");
		sb.append("params: \r\n");
		for (Entry<String, List<String>> entry : params.entrySet()) {
			sb.append("    ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
		}
		return sb.toString();
	}

}
