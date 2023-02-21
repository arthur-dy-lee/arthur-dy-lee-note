#### http servlet生命周期，在springmvc对原生servlet做了一个怎么样的包装来实现一个自己的mvc能力的？

1.加载和实例化。Servlet容器负责加载和实例化Servlet。当Servlet容器启动时，或者在容器检测到需要这个Servlet来响应第一个请求时，创建Servlet实例。
2.初始化。在Servlet实例化之后，容器将调用Servlet的init()方法初始化这个对象。初始化的目的是为了让Servlet对象在处理客户端请求前完成一些初始化的工作，
3.请求处理。Servlet容器调用Servlet的service()方法对请求进行处理。service()方法为Servlet的核心方法，客户端的业务逻辑应该在该方法内执行，典型的服务方法的开发流程为：解析客户端请求-〉执行业务逻辑-〉输出响应页面到客户端
4.服务终止。容器就会调用实例的destroy()方法，以便让该实例可以释放它所使用的资源
在整个Servlet的生命周期过程中，创建Servlet实例、调用实例的init()和destroy()方法都只进行一次，当初始化完成后，Servlet容器会将该实例保存在内存中，通过调用它的service()方法，为接收到的请求服务。如建立数据库的连接，获取配置信息等。对于每一个Servlet实例，init()方法只被调用一次。

Spring web MVC框架提供了MVC(模型 - 视图 - 控制器)架构和用于开发灵活和松散耦合的Web应用程序的组件。MVC模式导致应用程序的不同方面(输入逻辑，业务逻辑和UI逻辑)分离，同时提供这些元素之间的松散耦合
模型（Model）：封装了应用程序的数据，通常由POJO类组成
视图（View）：负责渲染模型数据，一般来说它生成客户端浏览器可以解释HTML输出
控制器(Controller)：负责处理用户请求并构建适当的模型，并将其传递给视图进行渲染

Spring MVC框架是围绕DispatcherServlet设计的，它处理所有的请求和响应。
DispatcherServlet处理HTTP请求的工作流程：(DispatcherServlet，HandlerMapping，Controller，ViewResolver。简称DHCV)
1/接受HTTP请求后，DispatcherServlet
2/会查询HandlerMapping以调用相应的Controller（根据请求的url）
3/Controller接受请求并根据请求的类型Get/Post调用相应的服务方法，服务方法进行相应的业务处理，并设置模型数据，最后将视图名称返回给DispatcherServlet
4/DispatcherServlet根据返回的视图名称从ViewResolver获取对应的视图
5/DispatcherServlet将模型数据传递到最终的视图，并将视图返回给浏览器。

DispatcherServlet extends FrameworkServlet
FrameworkServlet extends HttpServletBean
HttpServletBean extends HttpServlet

FrameworkServlet重载了doGet、doPost、doPut、doDelete等方法
DispatcherServlet重载了FrameworkServlet#onRefresh方法，在HttpServletBean初始化init的时候被调用

HttpServletBean#init->initServletBean()->DispatcherServlet#initServletBean->initFrameworkServlet->initWebApplicationContext->onRefresh->DispatcherServlet#onRefresh->initStrategies



#### session和cookie区别，在服务端session有没有什么别的替代方案，来达到和session同样的效果？

cookie数据保存在客户端，session数据保存在服务器端。session 的运行依赖 session id，而 session id 是存在 cookie中的
为了解决禁用cookie后的页面处理，通常采用url重写技术

用Cookies有个底线。这个底线一般来说，遵循以下原则。
不要保存私人信息。
任何重要数据，最好通过加密形式来保存数据
是否保存登陆信息，需有用户自行选择。
单个cookie保存的数据不能超过4K。

cookie数据结构÷
生命周期
名字
编码
限制访问 Secure，HttpOnly
Domain 属性
Path 属性

session数据结构
sessionId
过期时间
超时时间

可以使用JWT（JSON WEB TOKEN）来替代session【HPS】
jwt的第三部分是一个签证信息，这个签证信息由三部分组成：header (base64后的)、payload (base64后的)、secret，将这三部分用.连接成一个完整的字符串,构成了最终的jw
1/header
jwt的头部承载两部分信息：
{
  'typ': 'JWT',
  'alg': 'HS256'
}
声明类型，这里是jwt
声明加密的算法 通常直接使用 HMAC SHA256
2/playload
签发者、签发时间、过期时间、唯一身份标识，主要用来作为一次性token、
3/signature
base64加密后的header和base64加密后的payload使用.连接组成的字符串，然后通过header中声明的加密方式进行加盐secret组合加密，然后就构成了jwt的第三部分。









#### 1.spring框架中需要引用哪些jar包，以及这些jar包的用途  

#### 2.springMVC的原理  

#### 3.springMVC注解的意思  

#### 4.spring中beanFactory和ApplicationContext的联系和区别  

#### 5.spring注入的几种方式  

#### 6.spring如何实现事物管理的  

#### 7.springIOC和AOP的原理  



