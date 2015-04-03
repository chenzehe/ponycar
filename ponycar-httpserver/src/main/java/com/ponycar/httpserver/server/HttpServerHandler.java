/**
 * Wasu.com Inc.
 * Copyright (c) 2014-2015 All Rights Reserved.
 */

package com.ponycar.httpserver.server;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ponycar.httpserver.handler.Handler;
import com.ponycar.httpserver.handler.HandlerMap;

/**
 * @description http请求处理类
 *              如果为长连接，则在没有CLOSE的情况下，每次请求时该类都为同一个实例
 *              如果为短连接，则每次请求都不同实例
 * 
 *              HTTP也可以建立长连接的，使用Connection:keep-alive，HTTP 1.1默认进行持久连接。
 *              HTTP1.1和HTTP1.0相比较而言，最大的区别就是增加了持久连接支持(貌似最新的 http1.0 可以显示的指定
 *              keep-alive),但还是无状态的，或者说是不可以信任的
 * 
 *              长连接多用于操作频繁，点对点的通讯，而且连接数不能太多情况。
 *              每个TCP连接都需要三步握手，这需要时间， 如果每个操作都是先连接，
 *              再操作的话那么处理速度会降低很多 ，所以每个操作完后都不断开，
 *              次处理时直接发送数据包就OK了，不用建立TCP连接。例如：数据库的连接用长连接，
 *              如果用短连接频繁的通信会造成socket错误，而且频繁的socket 创建也是对资源的浪费。
 * 
 *              而像WEB网站的http服务一般都用短链接，因为长连接对于服务端来说会耗费一定的资源，
 *              而像WEB网站这么频繁的成千上万甚至上亿客户端的连接用短连接会更省一些资源
 *              ，如果用长连接，而且同时有成千上万的用户，如果每个用户都占用一个连接的话
 *              ，那可想而知吧。所以并发量大，但每个用户无需频繁操作情况下需用短连好。
 * 
 * @author chenzehe
 * @email chenzehe@wasu.com
 * @create 2015年3月16日 下午7:31:21
 */

public class HttpServerHandler extends SimpleChannelInboundHandler<Object> {
	private static Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);
	private Response response;
	private Request request;
	/**
	 * 是否设置为长连接
	 */
	private boolean keepalive;
	/**
	 * 返回值类型
	 */
	private String contentType;
	/**
	 * 返回字符值
	 */
	private String charset;

	public HttpServerHandler(boolean keepalive, String contentType, String charset) {
		this.keepalive = keepalive;
		this.contentType = contentType;
		this.charset = charset;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof HttpRequest) {
			HttpRequest nettyRequest = (HttpRequest) msg;
			request = Request.builderRequest(nettyRequest);
			response = new Response(contentType, charset);
			Handler handle = HandlerMap.getHandle(request.getPath());
			if (handle != null) {
				response.setContent(handle.invoke(request, response));
			} else {
				response.setStatus(HttpResponseStatus.NOT_FOUND);
				response.setContent(HttpResponseStatus.NOT_FOUND.toString());
			}
			writeResponse(ctx);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
	}

	/**
	 * 将Response写入到客户端
	 * 如果为长连接就在Response head里设置Connection值为keep-alive
	 * 否则就关闭连接
	 * 
	 * @param ctx
	 *            ChannelHandlerContext
	 */
	private void writeResponse(ChannelHandlerContext ctx) {
		FullHttpResponse fullHttpResponse = response.toFullHttpResponse();
		if (keepalive && request != null && request.isKeepAlive()) {
			fullHttpResponse.headers().set(Names.CONNECTION, Values.KEEP_ALIVE);
			ctx.writeAndFlush(fullHttpResponse);
			logger.info("ChannelFuture {} is keep-alive , not close......", this);
		} else {
			ctx.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
			logger.info("ChannelFuture {} is closed......", this);
		}
	}

}
