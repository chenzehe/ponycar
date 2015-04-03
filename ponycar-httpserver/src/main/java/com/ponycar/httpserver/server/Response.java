/**
 * Wasu.com Inc.
 * Copyright (c) 2014-2015 All Rights Reserved.
 */

package com.ponycar.httpserver.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.DefaultCookie;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.ServerCookieEncoder;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

/**
 * @description 对Response对象的封装，暴露给用户
 * @author chenzehe
 * @email chenzehe@wasu.com
 * @create 2015年3月16日 上午8:21:01
 */

public class Response {
	private HttpVersion httpVersion = HttpVersion.HTTP_1_1;
	private HttpResponseStatus status = HttpResponseStatus.OK;
	private HttpHeaders headers = new DefaultHttpHeaders();
	private Set<Cookie> cookies = new HashSet<Cookie>();
	private ByteBuf content = Unpooled.EMPTY_BUFFER;

	/**
	 * 返回值类型
	 */
	private String contentType;
	/**
	 * 返回字符值
	 */
	private String charset;

	public Response(String contentType, String charset) {
		this.contentType = contentType;
		this.charset = charset;
	}

	/**
	 * 设置响应的Http版本号
	 * 
	 * @param httpVersion
	 *            http版本号对象
	 */
	public void setHttpVersion(HttpVersion httpVersion) {
		this.httpVersion = httpVersion;
	}

	/**
	 * 响应状态码<br>
	 * 使用io.netty.handler.codec.http.HttpResponseStatus对象
	 * 
	 * @param status
	 *            状态码
	 */
	public void setStatus(HttpResponseStatus status) {
		this.status = status;
	}

	/**
	 * 设置Content-Type
	 * 
	 * @param contentType
	 *            Content-Type
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * 返回Content-Type
	 * 
	 * @param contentType
	 *            Content-Type
	 */
	public String getContentType() {
		return this.contentType;
	}

	/**
	 * 响应状态码
	 * 
	 * @param status
	 *            状态码
	 */
	public void setStatus(int status) {
		setStatus(HttpResponseStatus.valueOf(status));
	}

	/**
	 * 设置返回内容的字符集编码
	 * 
	 * @param charset
	 *            编码
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * 返回内容的字符集编码
	 * 
	 * @param charset
	 *            编码
	 */
	public String getCharset() {
		return this.charset;
	}

	/**
	 * 设置响应的Header
	 * 
	 * @param name
	 *            名
	 * @param value
	 *            值，可以是String，Date， int
	 */
	public void addHeader(String name, Object value) {
		headers.add(name, value);
	}

	/**
	 * 设定返回给客户端的Cookie
	 * 
	 * @param cookie
	 */
	public void addCookie(Cookie cookie) {
		cookies.add(cookie);
	}

	/**
	 * 设定返回给客户端的Cookie
	 * 
	 * @param name
	 *            Cookie名
	 * @param value
	 *            Cookie值
	 */
	public void addCookie(String name, String value) {
		addCookie(new DefaultCookie(name, value));
	}

	/**
	 * 设定返回给客户端的Cookie
	 * 
	 * @param name
	 *            cookie名
	 * @param value
	 *            cookie值
	 * @param maxAgeInSeconds
	 *            -1: 关闭浏览器清除Cookie. 0: 立即清除Cookie. n>0 : Cookie存在的秒数.
	 * @param path
	 *            Cookie的有效路径
	 * @param domain
	 *            the domain name within which this cookie is visible; form is
	 *            according to RFC 2109
	 */
	public void addCookie(String name, String value, int maxAgeInSeconds, String path, String domain) {
		Cookie cookie = new DefaultCookie(name, value);
		if (domain != null) {
			cookie.setDomain(domain);
		}
		cookie.setMaxAge(maxAgeInSeconds);
		cookie.setPath(path);
		addCookie(cookie);
	}

	/**
	 * 设定返回给客户端的Cookie<br>
	 * Path: "/"<br>
	 * No Domain
	 * 
	 * @param name
	 *            cookie名
	 * @param value
	 *            cookie值
	 * @param maxAgeInSeconds
	 *            -1: 关闭浏览器清除Cookie. 0: 立即清除Cookie. n>0 : Cookie存在的秒数.
	 */
	public void addCookie(String name, String value, int maxAgeInSeconds) {
		addCookie(name, value, maxAgeInSeconds, "/", null);
	}

	/**
	 * 删除Cookie，设置Cookie的超时时间为0即为删除
	 * 
	 * @param name
	 */
	public void removeCookie(String name) {
		addCookie(name, "", 0);
	}

	/**
	 * 设置响应文本内容
	 * 
	 * @param contentText
	 *            响应的文本
	 */
	public void setContent(String contentText) {
		this.content = Unpooled.copiedBuffer(contentText, Charset.forName(charset));
	}

	/**
	 * 设置响应文本内容
	 * 
	 * @param contentBytes
	 *            响应的字节
	 */
	public void setContent(byte[] contentBytes) {
		this.content = Unpooled.copiedBuffer(contentBytes);
	}

	/**
	 * 转换为Netty所用Response
	 * 
	 * @return FullHttpResponse
	 */
	public FullHttpResponse toFullHttpResponse() {
		final FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpVersion, status, content);

		// headers
		final HttpHeaders httpHeaders = fullHttpResponse.headers().add(headers);
		httpHeaders.set(Names.CONTENT_TYPE, contentType + "; charset=" + charset);
		httpHeaders.set(Names.CONTENT_ENCODING, charset);
		httpHeaders.set(Names.CONTENT_LENGTH, content.readableBytes());

		// Cookies
		for (Cookie cookie : cookies) {
			httpHeaders.add(Names.SET_COOKIE, ServerCookieEncoder.encode(cookie));
		}

		return fullHttpResponse;
	}

}
