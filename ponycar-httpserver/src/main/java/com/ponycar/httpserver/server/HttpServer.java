/**
 * Wasu.com Inc.
 * Copyright (c) 2014-2015 All Rights Reserved.
 */

package com.ponycar.httpserver.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.ponycar.httpserver.annotation.RequestMapping;
import com.ponycar.httpserver.handler.Handler;
import com.ponycar.httpserver.handler.HandlerMap;

/**
 * @description
 * @author chenzehe
 * @email chenzehe@wasu.com
 * @create 2015年3月16日 下午7:44:43
 */

public class HttpServer implements ApplicationContextAware, InitializingBean {
	private static Logger logger = LoggerFactory.getLogger(HttpServer.class);
	/**
	 * 运行端口
	 */
	private int port;
	/**
	 * 是否压缩
	 */
	private boolean compress;

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

	public HttpServer() {
	}

	public HttpServer(int port, boolean compress, boolean keepalive, String contentType, String charset) {
		this.port = port;
		this.compress = compress;
		this.keepalive = keepalive;
		this.contentType = contentType;
		this.charset = charset;
	}

	/**
	 * 初始化操作，解析RequestMapping
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RequestMapping.class);
		if (serviceBeanMap != null) {
			for (String path : serviceBeanMap.keySet()) {
				Handler handle = (Handler) serviceBeanMap.get(path);
				HandlerMap.addHandle(path, handle);
				logger.info("init path [{}] ----> {}", path, handle);
			}
		}
	}

	/**
	 * 这个方法将在所有的属性被初始化后调用。afterPropertiesSet
	 * 和init-method之间的执行顺序是afterPropertiesSet 先执行，init-method 后执行。
	 * afterPropertiesSet 必须实现 InitializingBean接口。实现
	 * InitializingBean接口必须实现afterPropertiesSet方法。
	 * 在此启动服务
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.start();
	}

	/**
	 * 启动Http服务
	 */
	public void start() {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap b = new ServerBootstrap();
			b.option(ChannelOption.SO_BACKLOG, 1024);
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) {
					ChannelPipeline pipeline = ch.pipeline();
					/**
					 * http-request解码器
					 * http服务器端对request解码
					 */
					pipeline.addLast(new HttpRequestDecoder());
					/**
					 * http-response解码器
					 * http服务器端对response编码
					 */
					pipeline.addLast(new HttpResponseEncoder());

					/**
					 * HttpObjectAggregator会把多个消息转换为一个单一的FullHttpRequest或是FullHttpResponse
					 */
					pipeline.addLast(new HttpObjectAggregator(65536));
					/**
					 * 压缩
					 * Compresses an HttpMessage and an HttpContent in gzip or
					 * deflate encoding
					 * while respecting the "Accept-Encoding" header.
					 * If there is no matching encoding, no compression is done.
					 */
					if (compress) {
						pipeline.addLast("deflater", new HttpContentCompressor());
					}
					/**
					 * ChunkedWriteHandler主要解决的是在异步发送大规模数据时，
					 * 会导致OutOfMemoryError的问题。简单而言，使用ChunkedWriteHandler，
					 * 我们可以随意发送任意大的数据，而不需要特别制作自己的数据发送协议，
					 * 也不需要编写复杂的实现
					 */
					// pipeline.addLast(new ChunkedWriteHandler());
					pipeline.addLast(new HttpServerHandler(keepalive, contentType, charset));
				}
			});
			b.bind(port).sync().channel().closeFuture().sync();
		} catch (Exception e) {
			logger.error("http服务启动异常 {} " + e.getMessage());
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setCompress(boolean compress) {
		this.compress = compress;
	}

	public void setKeepalive(boolean keepalive) {
		this.keepalive = keepalive;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

}
