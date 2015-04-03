# ponycar
使用Netty5构造的一个小型服务器，目前只实现了Http服务

Http服务使用步骤：
	
1、实现Handler接口，String invoke(Request request, Response response)方法返回值为Http响应值

2、使用注解，标识该类和URL的映射关系
@RequestMapping("/example")
public class HttpServerTest implements Handler {

}

3、Spring配置文件配置HttpServer对象
	<bean name="httpServer" class="com.ponycar.httpserver.server.HttpServer">
		<property name="port" value="${httpserver.port}" />
		<property name="compress" value="${httpserver.compress}" />
		<property name="keepalive" value="${httpserver.keepalive}" />
		<property name="contentType" value="${httpserver.contenttype}" />
		<property name="charset" value="${httpserver.charset}" />
	</bean>
	
4、.properties文件配置Http服务器参数
httpserver.port=8090
httpserver.compress=true
httpserver.keepalive=false
httpserver.contenttype=text/json
httpserver.charset=UTF-8

4、启动Http服务
	public static void main(String[] args) {
		try {
			new ClassPathXmlApplicationContext("spring/applicationContext.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
