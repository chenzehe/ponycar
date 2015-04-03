<p># ponycar</p>
<p>使用Netty5构造的一个小型服务器，目前只实现了Http服务</p>
<p>&nbsp;</p>
<p>Http服务使用步骤：</p>
<p>&nbsp;</p>
<p>1、实现Handler接口，String invoke(Request request, Response response)方法返回值为Http响应值</p>
<p>&nbsp;</p>
<p>2、使用注解，标识该类和URL的映射关系</p>
<p>@RequestMapping("/example")</p>
<p>public class HttpServerTest implements Handler {</p>
<p>&nbsp;</p>
<p>}</p>
<p>&nbsp;</p>
<p>3、Spring配置文件配置HttpServer对象</p>
<p>&lt;bean name="httpServer" class="com.ponycar.httpserver.server.HttpServer"&gt;</p>
<p>&lt;property name="port" value="${httpserver.port}" /&gt;</p>
<p>&lt;property name="compress" value="${httpserver.compress}" /&gt;</p>
<p>&lt;property name="keepalive" value="${httpserver.keepalive}" /&gt;</p>
<p>&lt;property name="contentType" value="${httpserver.contenttype}" /&gt;</p>
<p>&lt;property name="charset" value="${httpserver.charset}" /&gt;</p>
<p>&lt;/bean&gt;</p>
<p>&nbsp;</p>
<p>4、.properties文件配置Http服务器参数</p>
<p>httpserver.port=8090</p>
<p>httpserver.compress=true</p>
<p>httpserver.keepalive=false</p>
<p>httpserver.contenttype=text/json</p>
<p>httpserver.charset=UTF-8</p>
<p>&nbsp;</p>
<p>4、启动Http服务</p>
<p>public static void main(String[] args) {</p>
<p>try {</p>
<p>new ClassPathXmlApplicationContext("spring/applicationContext.xml");</p>
<p>} catch (Exception e) {</p>
<p>e.printStackTrace();</p>
<p>}</p>
<p>}</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>