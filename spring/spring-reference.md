# spring-ref
spring-framework-reference-5.5M 笔记
======================

# I. Overview of Spring Framework
---------------

## 2.2. Framework Modules

![https://farm5.staticflickr.com/4296/35319885603_80b1ab3175_b.jpg](https://farm5.staticflickr.com/4296/35319885603_80b1ab3175_b.jpg)



### 2.2.1. Core Container
The Core Container consists of the spring-core, spring-beans, spring-context, springcontext-support, and spring-expression (Spring Expression Language) modules.

The spring-core and spring-beans modules provide the fundamental parts of the framework,
including the IoC and Dependency Injection features.

The Context (spring-context) module builds on the solid base provided by the Core and Beans
modules: it is a means to access objects in a framework-style manner that is similar to a JNDI registry.
The Context module inherits its features from the Beans module and adds support for internationalization
(using, for example, resource bundles), event propagation, resource loading, and the transparent
creation of contexts by, for example, a Servlet container. The Context module also supports Java EE
features such as EJB, JMX, and basic remoting. The ApplicationContext interface is the focal
point of the Context module. spring-context-support provides support for integrating common
third-party libraries into a Spring application context, in particular for caching (EhCache, JCache) and
scheduling (CommonJ, Quartz).

The spring-expression module provides a powerful Expression Language for querying and
manipulating an object graph at runtime.

### 2.2.2. AOP and Instrumentation

The separate spring-aspects module provides integration with AspectJ.

The spring-instrument module provides class instrumentation support and classloader
implementations to be used in certain application servers.


### 2.2.3. Messaging

Spring Framework 4 includes a spring-messaging module with key abstractions from the Spring
Integration project such as Message, MessageChannel, MessageHandler, and others to serve as a
foundation for messaging-based applications.

### 2.2.4. Data Access/Integration

The Data Access/Integration layer consists of the JDBC, ORM, OXM, JMS, and Transaction modules.

The spring-tx module supports programmatic and declarative transaction management for classes
that implement special interfaces and for all your POJOs (Plain Old Java Objects).

The spring-orm module provides integration layers for popular object-relational mapping APIs

The spring-oxm module provides an abstraction layer that supports Object/XML mapping
implementations such as JAXB, Castor, JiBX and XStream.

The spring-jms module (Java Messaging Service) contains features for producing and consuming
messages. Since Spring Framework 4.1, it provides integration with the spring-messaging module.

### 2.2.5. Web

The Web layer consists of the spring-web, spring-webmvc and spring-websocket modules.

The spring-web module provides basic web-oriented integration features such as multipart file upload
functionality and the initialization of the IoC container using Servlet listeners and a web-oriented
application context. It also contains an HTTP client and the web-related parts of Spring’s remoting
support.Spring Framework Reference Documentation

The spring-webmvc module (also known as the Web-Servlet module) contains Spring’s modelview-controller (MVC) and REST Web Services implementation for web applications. Spring’s MVC
framework provides a clean separation between domain model code and web forms and integrates with
all of the other features of the Spring Framework.

## 2.3. Usage scenarios

<a data-flickr-embed="true"  href="https://www.flickr.com/photos/151075642@N04/36690763002/in/dateposted-public/" title="Typical full-fledged Spring web application"><img src="https://farm5.staticflickr.com/4413/36690763002_dcc12a4986_b.jpg" width="971" height="726" alt="Typical full-fledged Spring web application"></a><script async src="//embedr.flickr.com/assets/client-code.js" charset="utf-8"></script>

<a data-flickr-embed="true"  href="https://www.flickr.com/photos/151075642@N04/36465083670/in/dateposted-public/" title="Remoting usage scenario"><img src="https://farm5.staticflickr.com/4433/36465083670_858e46d34f_b.jpg" width="1024" height="758" alt="Remoting usage scenario"></a><script async src="//embedr.flickr.com/assets/client-code.js" charset="utf-8"></script>

### 2.3.1. Dependency Management and Naming Conventions
Spring Dependencies and Depending on Spring
spring-context : Application context runtime,including scheduling and remoting abstractions

spring-context-support: Support classes for integrating common third-party libraries into a Spring application context

spring-instrument: Instrumentation agent for JVM bootstrapping

spring-instrument-tomcat: Instrumentation agent forTomcat

spring-orm: Object/Relational Mapping, including JPA and Hibernate support

# III. Core Technologies
-------------------

## 7. The IoC container
---------------
### 7.1. Introduction to the Spring IoC container and beans

类定义他的依赖，通过构造参数、工作方法参数、属性的set方法，在类创建后或者通过工厂方法返回后，注入到类实例中，这一过程称为依赖反转。容器在创建类时，由容器去注入这些依赖。

It is a process whereby objects define their dependencies, that is, the other objects they work with, only through constructor arguments, arguments to a factory method, or properties that are set on the object instance after it is constructed or returned from a factory method. The container then injects those dependencies when it creates the bean.
This process is fundamentally the inverse, hence the name Inversion of Control (IoC), of the bean itself controlling the instantiation or location of its dependencies by using direct construction of classes, or a mechanism such as the Service Locator pattern.

The org.springframework.beans and org.springframework.context packages are the basis for Spring Framework’s IoC container.
The BeanFactory interface provides an advanced configuration mechanism capable of managing any type of object. ApplicationContext is a subinterface of BeanFactory. It adds easier integration with Spring’s AOP features;

## 7.2. Container overview

ApplicationContext represents the Spring IoC
container and is responsible for instantiating, configuring, and assembling the aforementioned beans.

The configuration metadata is represented in XML, Java annotations, or Java code.

In standalone applications it is common to create an instance of ClassPathXmlApplicationContext or FileSystemXmlApplicationContext.

7.2.1. Configuration metadata
### 7.2.2. Instantiating a container
```xml
ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"services.xml", "daos.xml"});
```

### 7.2.3. Using the container
The ApplicationContext enables you to read bean definitions and access them as follows:
```java
// create and configure beans
ApplicationContext context = new ClassPathXmlApplicationContext("services.xml", "daos.xml");
// retrieve configured instance
PetStoreService service = context.getBean("petStore", PetStoreService.class);
// use configured instance
List<String> userList = service.getUsernameList();
```

With Groovy configuration, bootstrapping looks very similar, just a different context implementation class which is Groovy-aware (but also understands XML bean definitions):
```java
ApplicationContext context = new GenericGroovyApplicationContext("services.groovy", "daos.groovy");
```

The most flexible variant is GenericApplicationContext in combination with reader delegates, e.g. with XmlBeanDefinitionReader for XML files:
```java
GenericApplicationContext context = new GenericApplicationContext();
new XmlBeanDefinitionReader(ctx).loadBeanDefinitions("services.xml", "daos.xml");
context.refresh();
```
Or with GroovyBeanDefinitionReader for Groovy files:
```java
GenericApplicationContext context = new GenericApplicationContext();
new GroovyBeanDefinitionReader(ctx).loadBeanDefinitions("services.groovy", "daos.groovy");
context.refresh();
```

## 7.3. Bean overview
### 7.3.1. Naming beans
Aliasing a bean outside the bean definition
```xml
<alias name="fromName" alias="toName"/>

<alias name="subsystemA-dataSource" alias="subsystemB-dataSource"/>
<alias name="subsystemA-dataSource" alias="myApp-dataSource" />
```

## 7.4. Dependencies
### 7.4.1. Dependency Injection
Constructor-based dependency injection

##### Use the index attribute
```xml
<bean id="exampleBean" class="examples.ExampleBean">
<constructor-arg type="int" value="7500000"/>
<constructor-arg type="java.lang.String" value="42"/>
</bean>
```

##### using the type attribute
```xml
<bean id="exampleBean" class="examples.ExampleBean">
<constructor-arg index="0" value="7500000"/>
<constructor-arg index="1" value="42"/>
</bean>
```

##### constructor parameter name
```xml
<bean id="exampleBean" class="examples.ExampleBean">
<constructor-arg name="years" value="7500000"/>
<constructor-arg name="ultimateAnswer" value="42"/>
</bean>
```

##### @ConstructorProperties JDK annotation to explicitly name your constructor arguments.
```java
package examples;

public class ExampleBean {
  // Fields omitted
  @ConstructorProperties({"years", "ultimateAnswer"})
  public ExampleBean(int years, String ultimateAnswer) {
    this.years = years;
    this.ultimateAnswer = ultimateAnswer;
  }
}
```

##### Dependency resolution process
Circular dependencies
spring bean 循环依赖会抛出此类异常，可以用setter替代构造函数
>One possible solution is to edit the source code of some classes to be configured by setters rather than constructors. Alternatively, avoid constructor injection and use setter injection only.

### 7.4.2. Dependencies and configuration in detail

##### Straight values (primitives, Strings, and so on)
conversion service 可以自定义标签，并将xml中配置的String类型的value值转换

使用java.util.Properties配置
```xml
<bean id="mappings"
class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
<!-- typed as a java.util.Properties -->
<property name="properties">
<value>
jdbc.driver.className=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/mydb
</value>
</property>
</bean>
```

##### References to other beans (collaborators)
第一种方式，和下面的第二种方式，功能相同，但优于第二种方式写法
因为：idref允许窗口在部署时检查相关的依赖、bean是否存在。
不过在4.0bean xsd后，idref就不再被使用

第一种
```xml
<bean id="theTargetBean" class="..."/>
<bean id="theClientBean" class="...">
  <property name="targetName">
    <idref bean="theTargetBean"/>
  </property>
</bean>
```
第二种
```xml
<bean id="theTargetBean" class="..."/>
<bean id="client" class="...">
  <property name="targetName" value="theTargetBean"/>
</bean>
```
> The local attribute on the ref element is no longer supported in the 4.0 beans xsd since it
does not provide value over a regular bean reference anymore

##### Inner beans
```xml
<bean id="outer" class="...">
<!-- instead of using a reference to a target bean, simply define the target bean inline -->
  <property name="target">
    <bean class="com.example.Person"> <!-- this is the inner bean -->
      <property name="name" value="Fiona Apple"/>
      <property name="age" value="25"/>
    </bean>
  </property>
</bean>
```
内部类不可能注入到协同的bean中，而是注入到封闭的bean中，或者单独的访问内问类。

An inner bean definition does not require a defined id or name;
It is not possible to inject inner beans into collaborating beans other than into the enclosing bean or to access them independently


##### Collections (List, Set, Map的用法)
```xml
<bean id="moreComplexObject" class="example.ComplexObject">
  <!-- results in a setAdminEmails(java.util.Properties) call -->
  <property name="adminEmails">
    <props>
      <prop key="administrator">administrator@example.org</prop>
      <prop key="support">support@example.org</prop>
      <prop key="development">development@example.org</prop>
    </props>
  </property>
  <!-- results in a setSomeList(java.util.List) call -->
  <property name="someList">
    <list>
      <value>a list element followed by a reference</value>
      <ref bean="myDataSource" />
    </list>
  </property>
    <!-- results in a setSomeMap(java.util.Map) call -->
  <property name="someMap">
    <map>
      <entry key="an entry" value="just some string"/>
      <entry key ="a ref" value-ref="myDataSource"/>
    </map>
  </property>
    <!-- results in a setSomeSet(java.util.Set) call -->
  <property name="someSet">
    <set>
      <value>just some string</value>
      <ref bean="myDataSource" />
    </set>
  </property>
</bean>
```

##### Null and empty string values
The <null/> element handles null values
```xml
<bean class="ExampleBean">
  <property name="email">
    <null/>
  </property>
</bean>
```
等于：
```java
exampleBean.setEmail(null)
```

### 7.4.3. Using depends-on

depends-on attribute can explicitly force one or more beans to be initialized before the bean using this element is initialized.
```xml
<bean id="beanOne" class="ExampleBean" depends-on="manager"/>
<bean id="manager" class="ManagerBean" />
```

### 7.4.4. Lazy-initialized beans
A lazy-initialized bean tells the IoC container to create a bean instance when it is first requested, rather than at startup.
```xml
<bean id="lazy" class="com.foo.ExpensiveToCreateBean" lazy-init="true"/>
<bean name="not.lazy" class="com.foo.AnotherBean"/>
```
```text
You can also control lazy-initialization at the container level by using the default-lazy-init attribute on the <beans/> element; for example:
```
```xml
<beans default-lazy-init="true">
<!-- no beans will be pre-instantiated... -->
</beans>
```

### 7.4.6. Method injection
##### Lookup method injection
抽象方法注入，但继承的实现类要prototype类型的
```java
package fiona.apple;
// no more Spring imports!
public abstract class CommandManager {
  public Object process(Object commandState) {
  // grab a new instance of the appropriate Command interface
  Command command = createCommand();
  // set the state on the (hopefully brand new) Command instance
  command.setState(commandState);
    return command.execute();
  }
// okay... but where is the implementation of this method?
  protected abstract Command createCommand();
  }
```

```xml
<!-- a stateful bean deployed as a prototype (non-singleton) -->
<bean id="myCommand" class="fiona.apple.AsyncCommand" scope="prototype">
  <!-- inject dependencies here as required -->
</bean>
<!-- commandProcessor uses statefulCommandHelper -->
<bean id="commandManager" class="fiona.apple.CommandManager">
  <lookup-method name="createCommand" bean="myCommand"/>
</bean>
```
You must be careful to deploy the myCommand bean as a prototype, if that is actually what is needed.

如果是用注解的话，可以使用@Lookup替代xml配置
```java
public abstract class CommandManager {
  public Object process(Object commandState) {
    Command command = createCommand();
    command.setState(commandState);
    return command.execute();
  }
  @Lookup("myCommand")
  protected abstract Command createCommand();
}
```

##### Arbitrary method replacement
方法替换的配置，使用类继承 org.springframework.beans.factory.support.MethodReplacer

```java
public class MyValueCalculator {
  public String computeValue(String input) {
  // some real code...
  }
  // some other methods...
}
```

```java
/**
* meant to be used to override the existing computeValue(String)
* implementation in MyValueCalculator
*/
public class ReplacementComputeValue implements MethodReplacer {
  public Object reimplement(Object o, Method m, Object[] args) throws Throwable {
    // get the input value, work with it, and return a computed result
    String input = (String) args[0];
    ...
    return ...;
  }
}
```

```xml
<bean id="myValueCalculator" class="x.y.z.MyValueCalculator">
  <!-- arbitrary method replacement -->
  <replaced-method name="computeValue" replacer="replacementComputeValue">
    <arg-type>String</arg-type>
  </replaced-method>
</bean>
<bean id="replacementComputeValue" class="a.b.c.ReplacementComputeValue"/>
```

## 7.5. Bean scopes (bean作用域)
| Scope        | Description           |
| ------------- |:------------- |
| singleton | (Default) Scopes a single bean definition to a single object instance per Spring IoC container |
| prototype | Scopes a single bean definition to any number of object instances |
| request | Scopes a single bean definition to the lifecycle of a single HTTP request; that is, each HTTP request has its own instance of a bean created off the back of a single bean definition. Only valid in the context of a web-aware Spring ApplicationContext. |
| session | Scopes a single bean definition to the lifecycle of an HTTP Session. Only valid in the context of a web-aware Spring ApplicationContext. |
| application | Scopes a single bean definition to the lifecycle of a ServletContext. Only valid in the context of a web-aware Spring ApplicationContext. |
|websocket | Scopes a single bean definition to the lifecycle of a WebSocket. Only valid in the context of a web-aware Spring ApplicationContext. |

> As of Spring 3.0, a thread scope is available, but is not registered by default.

###7.5.1. The singleton scope
This single instance is stored in a cache of such singleton beans, and all subsequent requests and references for that named bean return the cached object.

### 7.5.2. The prototype scope

prototype一般用于有状态bean，singleton一般用于无状态的bean.

比较适合用单例模式的就是dao/service，因为他们不包含变化的成员变量，方法调用不会改变这个对象的状态（它也没有状态可言）

spring不管理prototype bean的全生命周期：容器实例化、配置和组装一个原型对象,并把它递给客户端，并不记录prototype实例。
尽管在对象创建时，初始化方法(init-method)被调用；即使配置了destory回调方法(destroy-method)，但并不会被调用，即destory方法无效。

如果想要清除prototype bean，可以使用"bean prost-processor"去清除它持有beans的引用，

use the prototype scope for all stateful beans and the singleton scope for stateless beans

Spring does not manage the complete lifecycle of a prototype bean: the container instantiates, configures, and otherwise assembles a prototype object, and hands it to the client, with no further record of that prototype instance.

### 7.5.3. Singleton beans with prototype-bean dependencies

如果将一个prototype-scoped bean注入到sinleton-scoped bean，那么prototype bean只会初始化一次。
如果想每次得到一个新的prototype bean，那么可以通过getBean的方法，去每次创建一个新的prototype bean.
```xml
ApplicationContext context = new ClassPathXmlApplicationContext("services.xml", "daos.xml");
PetStoreService service = context.getBean("petStore", PetStoreService.class);
```

### 7.5.4. Request, session, global session, application, and WebSocket scopes
##### Request scope
@RequestScope annotation can be
used to assign a component to the request scope.

```java
@RequestScope
@Component
public class LoginAction {
// ...
}
```

##### Session scope
When the HTTP Session is eventually discarded, the bean that is scoped
to that particular HTTP Session is also discarded
```xml
<bean id="userPreferences" class="com.foo.UserPreferences" scope="session"/>
```
@SessionScope annotation can be
used to assign a component to the session scope

```java
@SessionScope
@Component
public class UserPreferences {
// ...
}
```

##### Application scope
The Spring container creates a new instance of the AppPreferences bean by using the appPreferences bean definition once for the entire web application. That is, the appPreferences bean is scoped at the ServletContext level, stored as a regular ServletContext attribute. This is somewhat similar to a Spring singleton bean but differs in two important ways: It is a singleton per ServletContext, not per Spring 'ApplicationContext' (for which there may be several in any given web application), and it is actually exposed and therefore visible as a ServletContext attribute.

```xml
<bean id="appPreferences" class="com.foo.AppPreferences" scope="application"/>
```

```java
@ApplicationScope
@Component
public class AppPreferences {
// ...
}
```

##### Scoped beans as dependencies
The Spring IoC container manages not only the instantiation of your objects (beans), but also the wiring up of collaborators (or dependencies). If you want to inject (for example) an HTTP request scoped bean into another bean of a longer-lived scope, you may choose to inject an AOP proxy in place of the scoped bean. That is, you need to inject a proxy object that exposes the same public interface as the scoped object but that can also retrieve the real target object from the relevant scope (such as an HTTP request)
and delegate method calls onto the real object.

```text
You may also use <aop:scoped-proxy/> between beans that are scoped as singleton, with the reference then going through an intermediate proxy that is serializable and therefore able to re-obtain the target singleton bean on deserialization.

When declaring <aop:scoped-proxy/> against a bean of scope prototype, every method call on the shared proxy will lead to the creation of a new target instance which the call is then being forwarded to.
```
<aop:scoped-proxy/>示例
<aop:scoped-proxy/>标签功能：当session过期后，userService的属性userPreferences自然也不能再使用。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xmlns:aop="http://www.springframework.org/schema/aop"
xsi:schemaLocation="http://www.springframework.org/schema/beans
http://www.springframework.org/schema/beans/spring-beans.xsd
http://www.springframework.org/schema/aop
http://www.springframework.org/schema/aop/spring-aop.xsd">
  <!-- an HTTP Session-scoped bean exposed as a proxy -->
  <bean id="userPreferences" class="com.foo.UserPreferences" scope="session">
    <!-- instructs the container to proxy the surrounding bean -->
    <aop:scoped-proxy/>
  </bean>
  <!-- a singleton-scoped bean injected with a proxy to the above bean -->
  <bean id="userService" class="com.foo.SimpleUserService">
    <!-- a reference to the proxied userPreferences bean -->
    <property name="userPreferences" ref="userPreferences"/>
  </bean>
</beans>
```

下面的这个例子是如果不用的话：
userManager是单例的，userPreferences是session，在每个容器中，单例只会被初始化一次，相应的uerPreferences也只会被注入一次。当session失效后，userManager仍将保留userPreferences实例，想要userPreferences在seesion失效后也过期，可以使用：<aop:scoped-proxy/>

The salient point here is that the userManager bean is a singleton: it will be instantiated exactly once per container, and its dependencies (in this case only one, the userPreferences bean) are also injected only once. This means that the userManager bean will only operate on the exact same userPreferences object, that is, the one that it was originally injected with.

```xml
<bean id="userPreferences" class="com.foo.UserPreferences" scope="session"/>

<bean id="userManager" class="com.foo.UserManager">
  <property name="userPreferences" ref="userPreferences"/>
</bean>
```
加入 <aop:scoped-proxy/>后，userPreferences在seesion失效后也过期
```xml
<bean id="userPreferences" class="com.foo.UserPreferences" scope="session">
  <aop:scoped-proxy/>
</bean>

<bean id="userManager" class="com.foo.UserManager">
  <property name="userPreferences" ref="userPreferences"/>
</bean>
```

###### Choosing the type of proxy to create
CGLIB只能拦截调用公共的方法！
>CGLIB proxies only intercept public method calls!

也可以使用jdk代理，将proxy-target-class设置为false
```xml
<!-- DefaultUserPreferences implements the UserPreferences interface -->
<bean id="userPreferences" class="com.foo.DefaultUserPreferences" scope="session">
  <aop:scoped-proxy proxy-target-class="false"/>
</bean>
<bean id="userManager" class="com.foo.UserManager">
  <property name="userPreferences" ref="userPreferences"/>
</bean>
```

### 7.5.5. Custom scopes
#### Creating a custom scope
可以通过实现 org.springframework.beans.factory.config.Scope 接口，自定义自己的scope，但不能覆盖singleton和prototype.

#### Using a custom scope
> The example below uses SimpleThreadScope which is included with Spring, but not registered
by default.

```java
Scope threadScope = new SimpleThreadScope();
beanFactory.registerScope("thread", threadScope);
```

```xml
<bean id="..." class="..." scope="thread">
```

## 7.6. Customizing the nature of a bean
### 7.6.1. Lifecycle callbacks
> The JSR-250 @PostConstruct and @PreDestroy annotations are generally considered best
practice for receiving lifecycle callbacks in a modern Spring application.

> If you don’t want to use the JSR-250 annotations but you are still looking to remove coupling consider the use of init-method and destroy-method object definition metadata

Internally, the Spring Framework uses BeanPostProcessor implementations to process any callback interfaces it can find and call the appropriate methods. If you need custom features or other lifecycle behavior Spring does not offer out-of-the-box, you can implement a BeanPostProcessor yourself.

#### Initialization callbacks
do not use the InitializingBean interface

use the @PostConstruct annotation or init-method attribute on the <bean/>

三种方法对bean进行初始化：
1、继承InitializingBean，实实afterPrpertiesSet方法。（不推荐）
2、使用@PostConstruct注解
3、在spring xml使用init-method
4、实现BeanPostProcessor接口，自定义自己的初始化。

#### Destruction callbacks
use the @PreDestroy annotation or destroy-method attribute on the <bean/>

三种方法destory bean
1、实现DisposableBean接口的destory方法，此种不推荐
2、使用注解@PreDestroy
3、xml中使用destory-method方法
4、实现java.lang.AutoCloseable或java.io.Closeable接口

> java.lang.AutoCloseable or java.io.Closeable

> The destroy-method attribute of a <bean> element can be assigned a special (inferred) value which instructs Spring to automatically detect a public close or shutdown method
on the specific bean class (any class that implements java.lang.AutoCloseable or java.io.Closeable would therefore match). This special (inferred) value can also be set
on the default-destroy-method attribute of a <beans> element to apply this behavior to an entire set of beans (see the section called “Default initialization and destroy methods”). Note that this is the default behavior with Java config.

#### Default initialization and destroy methods
在xml中，可以使用<beans default-init-method="init">来定义默认规则，只要有类中含有init方法，默认初始化时就会进行调用。

default-init-method attribute on the top-level <beans/> element attribute causes the Spring IoC container to recognize a method called init on beans as the initialization method

```xml
<beans default-init-method="init">
  <bean id="blogService" class="com.foo.DefaultBlogService">
    <property name="blogDao" ref="blogDao" />
  </bean>
</beans>
```

#### Combining lifecycle mechanisms
如果初始化/destory几种方法混合调用，优先级顺序如下：
 - Methods annotated with @PostConstruct
 - afterPropertiesSet() as defined by the InitializingBean callback interface
 - A custom configured init() method

Destroy methods are called in the same order:
 - Methods annotated with @PreDestroy
 - destroy() as defined by the DisposableBean callback interface
 - A custom configured destroy() method

#### Startup and shutdown callbacks
```java
public interface Lifecycle {
  void start();
  void stop();
  boolean isRunning();
}
```
```java
public interface LifecycleProcessor extends Lifecycle {
  void onRefresh();
  void onClose();
}
```
```java
public interface Phased {
  int getPhase();
}
```
```java
public interface SmartLifecycle extends Lifecycle, Phased {
  boolean isAutoStartup();
  void stop(Runnable callback);
}
```
实现SmartLifecycle接口，通过getPhase方法返回一个整数值，值越小，越先启动。

When starting, the objects with the lowest phase start first, and when stopping, the reverse order is
followed.

DefaultLifecycleProcessor会执行回调函数，直到超过默认的30秒等待时间。如果想要修改过期时间，可以覆盖liefcycleProcessor，方法如下：

DefaultLifecycleProcessor, will wait up to its timeout value
for the group of objects within each phase to invoke that callback. The default per-phase timeout
is 30 seconds. You can override the default lifecycle processor instance by defining a bean named
"lifecycleProcessor" within the context. If you only want to modify the timeout, then defining the following
would be sufficient:

```xml
<bean id="lifecycleProcessor" class="org.springframework.context.support.DefaultLifecycleProcessor">
  <!-- timeout value in milliseconds -->
  <property name="timeoutPerShutdownPhase" value="10000"/>
</bean>
```

### 7.6.3. Other Aware interfaces
Besides ApplicationContextAware and BeanNameAware discussed above, Spring offers a range of Aware interfaces that allow beans to indicate to the container that they require a certain infrastructure dependency

| Name        | Injected Dependency           |
| ------------- |:-------------|
| ApplicationContextAware | Declaring ApplicationContext |
| ApplicationEventPublisherAware | ware Event publisher of the enclosing ApplicationContext |
| BeanClassLoaderAware | Class loader used to load the bean classes |
| BeanFactoryAware | Declaring BeanFactory |
| BeanNameAware | Name of the declaring bean |
| BootstrapContextAware | Resource adapter BootstrapContext the container runs in. Typically available only in JCA aware ApplicationContexts |
| LoadTimeWeaverAware | Defined weaver for processing class definition at load time |
| MessageSourceAware | Configured strategy for resolving messages (with support for parametrization and internationalization) |
| NotificationPublisherAwareSpring | Spring JMX notification publisher |
| ResourceLoaderAware | Configured loader for low-level access to resources |
| ServletConfigAware | Current ServletConfig the container runs in. Valid only in a web-aware Spring ApplicationContext |
| ServletContextAware | Current ServletContext the container runs in. Valid only in a web-aware Spring ApplicationContext |



## 7.7. Bean definition inheritance
1、如果父类是抽象类型，可以通过声明：abstract="true"的方式，spring容器就不会对它进行初始化，preInstantiateSingletons()会忽略它；同时，如果通过getBean(beanId)方式想得到它的实例，也是会抛错的。 2、如果想要子类继承父类，那么在子类类，可以这样声明：parent="inheritedTestBean"
```xml
<bean id="inheritedTestBean" abstract="true" class="org.springframework.beans.TestBean">
  <property name="name" value="parent"/>
  <property name="age" value="1"/>
</bean>
<bean id="inheritsWithDifferentClass" class="org.springframework.beans.DerivedTestBean"
      parent="inheritedTestBean" init-method="initialize">
  <property name="name" value="override"/>
  <!-- the age property value of 1 will be inherited from parent -->
</bean>
```

## 7.8. Container Extension Points
### 7.8.1. Customizing beans using a BeanPostProcessor

BeanPostProcessor接口定义了回调方法, 你可以实现你自己的(或覆盖容器的默认)实例化逻辑,依赖性解析逻辑。
The BeanPostProcessor interface defines callback methods that you can implement to provide your own (or override the container’s default) instantiation logic, dependency-resolution logic, and so forth

如果配置了多个BeanPostProcessor实例，可以通过实现Ordered接口，来实现实例化的顺序
You can configure multiple BeanPostProcessor instances, and you can control the order in which these BeanPostProcessors execute by setting the order property.

spring IOC容器先实例化一个bean，然后BeanPostProcessor再工作
the Spring IoC container instantiates a bean instance and then BeanPostProcessors do their work

BeanFactory和ApplicationContext对待bean后置处理器稍有不同。ApplicationContext会自动检测在配置文件中实现了BeanPostProcessor接口的所有bean， 并把它们注册为后置处理器，然后在容器创建bean的适当时候调用它，因此部署一个后置处理器同部署其他的bean并没有什么区别。而使用BeanFactory实现的时候，bean 后置处理器必须通过代码显式地去注册，在IoC容器继承体系中的ConfigurableBeanFactory接口中定义了注册方法: addBeanPostProcessor
An ApplicationContext automatically detects any beans that are defined in the configuration metadata which implement the BeanPostProcessor interface. The ApplicationContext registers these beans as post-processors so that they can be called later upon bean creation.

BeanPostProcessor 的bean注册可以通过推荐的方法ApplicationContext自动侦测(如上所述),也可以通过addBeanPostProcessor的ConfigurableBeanFactory方法

不要将BeanPostProcessor标记为延迟初始化。因为如果这样做，Spring容器将不会注册它们，自定义逻辑也就无法得到应用。假如你在<beans />元素的定义中使用了'default-lazy-init'属性，请确定你的各个BeanPostProcessor标记为'lazy-init="false"'

的BeanPostProcessor中使用@Resource时，当@Resource没有显式提供名字的时候，如果根据默认名字找不到对应的Spring管理对象，注入机制会回滚至类型匹配（type-match）。如果刚好只有一个Spring管理对象符合该依赖的类型，那么它会被注入。


### 7.8.2. Customizing configuration metadata with a BeanFactoryPostProcessor
BeanFactoryPostProcessor和BeanPostProcessor的区别: BeanFactoryPostProcessor可以对bean的定义（配置元数据）进行处理。也就是说，Spring IoC容器允许BeanFactoryPostProcessor在容器实际实例化任何其它的bean之前读取配置元数据，并有可能修改它。
The next extension point that we will look at is the org.springframework.beans.factory.config.BeanFactoryPostProcessor. The semantics of this interface are similar to those of the BeanPostProcessor, with one major difference: BeanFactoryPostProcessor operates on the bean configuration metadata; that is,  the Spring IoC container allows a BeanFactoryPostProcessor to read the configuration metadata and potentially change it before the container instantiates any beans other than BeanFactoryPostProcessors.

通过beanFactory可以获取bean的示例或定义等。同时可以修改bean的属性，这是和BeanPostProcessor最大的区别。还有一点区别就是BeanFactoryPostProcessor的回调比BeanPostProcessor要早。
While it is technically possible to work with bean instances within a BeanFactoryPostProcessor (e.g., using BeanFactory.getBean()), doing so causes premature bean instantiation, violating the standard container lifecycle.

即使在spring xml配置文件中设置了default-lazy-init=true，对于bean(Factory)PostProcessor也会忽视它，而对类进行初始化。
marking it for lazy initialization will be ignored, and the Bean(Factory)PostProcessor will be instantiated eagerly even if you set the default-lazy-init attribute to true on the declaration of your <beans /> element.


#### Example: the Class name substitution PropertyPlaceholderConfigurer
在Spring中，使用PropertyPlaceholderConfigurer可以在XML配置文件中加入外部属性文件，当然也可以指定外部文件的编码

At runtime, a PropertyPlaceholderConfigurer is applied to the metadata that will replace some properties of the DataSource.The values to replace are specified as placeholders of the form ${property-name} which follows the Ant / log4j / JSP EL style

```xml
<bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
  <property name="locations" value="classpath:com/foo/jdbc.properties"/>
</bean>
<bean id="dataSource" destroy-method="close" class="org.apache.commons.dbcp.BasicDataSource">
  <property name="driverClassName" value="${jdbc.driverClassName}"/>
  <property name="url" value="${jdbc.url}"/>
  <property name="username" value="${jdbc.username}"/>
  <property name="password" value="${jdbc.password}"/>
</bean>
```

PropertyPlaceholderConfigurer是个bean工厂后置处理器的实现，也就是 BeanFactoryPostProcessor接口的一个实现。PropertyPlaceholderConfigurer可以将上下文（配置文 件）中的属性值放在另一个单独的标准java Properties文件中去。在XML文件中用${key}替换指定的properties文件中的值。这样的话，只需要对properties文件进 行修改，而不用对xml配置文件进行修改。

### 7.8.3. Customizing instantiation logic with a FactoryBean
BeanFactory： 以Factory结尾，表示它是一个工厂类，是用于管理Bean的一个工厂

FactoryBean：以Bean结尾，表示它是一个Bean，不同于普通Bean的是：它是实现了FactoryBean<T>接口的Bean

FactoryBean界面的可插入性Spring IoC容器实例化的逻辑。

如果需要在java中比在xml在更方便的进行复杂的初始化代码，你可以创建自己的FactoryBean,这个类中编写复杂的初始化代码,然后定制FactoryBean插入容器中。
The FactoryBean interface is a point of pluggability into the Spring IoC container’s instantiation logic.If you have complex initialization code that is better expressed in Java as opposed to a (potentially) verbose amount of XML, you can create your own FactoryBean, write the complex initialization inside that class, and then plug your custom FactoryBean into the container.

FacotryBean创建的Bean,即它通过getObject()创建的Bean.

我们要想得到FactoryBean本身，必须通过&FactoryBeanName得到，即在BeanFactory中通过getBean(&FactoryBeanName)来得到 FactoryBean

## 7.9. Annotation-based container configuration
注解会比xml配置文件更好注入，如果有重复，那么配置文件会覆盖注解。
> Annotation injection is performed before XML injection, thus the latter configuration will override the former for properties wired through both approaches

<context:annotation-config/> means:
(The implicitly registered post-processors include AutowiredAnnotationBeanPostProcessor, CommonAnnotationBeanPostProcessor, PersistenceAnnotationBeanPostProcessor, as well as the aforementioned RequiredAnnotationBeanPostProcessor.)

<context:annotation-config/>只在被定义的应用上下文中去找相应的beans的注解，如果把它放到WebApplicationContext中，它只检查Controllers beans，而不检查你的services。
<context:annotation-config/> only looks for annotations on beans in the same application context in which it is defined. This means that, if you put <context:annotation-config/> in a WebApplicationContext for a DispatcherServlet, it only checks for @Autowired beans in your controllers, and not your services.

### 7.9.1. @Required && @Autowired

@Autowired，@Inject，@Resource和@Value注解是通过Spring BeanPostProcessor实现处理，这反过来意味着你不能在你自己的BeanPostProcessor或BeanFactoryPostProcessor中应用这些注解（如果有的话）。这些类型必须显式的通过XML或使用Spring的@Bean方法来’wired up’。
> @Autowired, @Inject, @Resource, and @Value annotations are handled by Spring BeanPostProcessor implementations which in turn means that you cannot apply these annotations within your own BeanPostProcessor or BeanFactoryPostProcessor types (if any). These types must be 'wired up' explicitly via XML or using a Spring @Bean method.

### 7.9.3. Fine-tuning annotation-based autowiring with @Primary
Because autowiring by type may lead to multiple candidates, it is often necessary to have more control over the selection process. One way to accomplish this is with Spring’s @Primary annotation. @Primary indicates that a particular bean should be given preference when multiple beans are candidates to be autowired to a single-valued dependency. If exactly one 'primary' bean exists among the candidates, it will be the autowired value.

```java
@Configuration
public class MovieConfiguration {
  @Bean
  @Primary
  public MovieCatalog firstMovieCatalog() { ... }
  @Bean
  public MovieCatalog secondMovieCatalog() { ... }
  // ...
}
public class MovieRecommender {
  @Autowired
  private MovieCatalog movieCatalog;
  // ...
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xmlns:context="http://www.springframework.org/schema/context"
xsi:schemaLocation="http://www.springframework.org/schema/beans
http://www.springframework.org/schema/beans/spring-beans.xsd
http://www.springframework.org/schema/context
http://www.springframework.org/schema/context/spring-context.xsd">
<context:annotation-config/>
<bean class="example.SimpleMovieCatalog" primary="true">
<!-- inject any dependencies required by this bean -->
</bean>
<bean class="example.SimpleMovieCatalog">
<!-- inject any dependencies required by this bean -->
</bean>
<bean id="movieRecommender" class="example.MovieRecommender"/>
</beans>
```

### 7.9.4. Fine-tuning annotation-based autowiring with qualifiers
@Primary is an effective way to use autowiring by type with several instances when one primary candidate can be determined. When more control over the selection process is required, Spring’s @Qualifier annotation can be used.

```java
public class MovieRecommender {
  @Autowired
  @Qualifier("main")
  private MovieCatalog movieCatalog;
  // ...
}
```
The @Qualifier annotation can also be specified on individual constructor arguments or method parameters:
```java
public class MovieRecommender {
  private MovieCatalog movieCatalog;
  private CustomerPreferenceDao customerPreferenceDao;
  @Autowired
  public void prepare(@Qualifier("main")MovieCatalog movieCatalog,
                      CustomerPreferenceDao  customerPreferenceDao) {
    this.movieCatalog = movieCatalog;
    this.customerPreferenceDao = customerPreferenceDao;
  }
  // ...
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xmlns:context="http://www.springframework.org/schema/context"
xsi:schemaLocation="http://www.springframework.org/schema/beans
http://www.springframework.org/schema/beans/spring-beans.xsd
http://www.springframework.org/schema/context
http://www.springframework.org/schema/context/spring-context.xsd">
  <context:annotation-config/>
  <bean class="example.SimpleMovieCatalog">
    <qualifier value="main"/>
    <!-- inject any dependencies required by this bean -->
  </bean>
  <bean class="example.SimpleMovieCatalog">
    <qualifier value="action"/>
    <!-- inject any dependencies required by this bean -->
  </bean>
  <bean id="movieRecommender" class="example.MovieRecommender"/>
</beans>
```

@Resource注解，它在语义上被定义为通过组件唯一的名字来识别特定的目标组件，声明的类型与匹配过程无关。
@Resource仅支持字段和bean属性的带有单个参数的setter方法
@Autowired有不同的语义：先通过类型选择候选beans后，再从这些候选的beans中，被指定的字符串的值才会被注入。
@Autowired可以应用到字段，构造函数和多参数方法上，允许通过@Qualifier注解在参数层面上缩减候选目标。

@Autowired是根据类型(byType)进行自动装配的。如果在spring中有同一个接口类，但是是2个或多个不同的实现类，那么可以用@Qualifier来区别。@Autowired和@Qualifier结合使用时，自动注入的策略就从byType转变成byName了。
例：如果当spring上下文中存在不止一个UserDao类型的bean时，就会抛出BeanCreationException异常;如果Spring上下文中不存在UserDao类型的bean，也会抛出BeanCreationException异常。我们可以使用@Qualifier配合@Autowired来解决这些问题。
可能不存在UserDao实例时，可以这样使用：@Autowired(required = false)
@Qualifier 只能和@Autowired 结合使用，是对@Autowired有益的补充。
@Resource annotation, which is semantically defined to identify a specifictarget component by its unique name, with the declared type being irrelevant for the matching process. @Autowired has rather different semantics: After selecting candidate beans by type, the specified String qualifier value will be considered within those type-selected candidates only

你可以添加<qualifier/>标记作为<bean/>标记的子元素，然后指定匹配你的定制限定符注解的类型和值。
You can add <qualifier/> tags as sub-elements of the <bean/> tag and then specify the type and value to match your custom qualifier annotations.

### 7.9.6. CustomAutowireConfigurer
如果不能使用Spring的@Qualifier注解进行注解，CustomAutowireConfigurer是BeanFactoryPostProcessor的实现，它能让你注册自定义的qualifier注解类型。
The CustomAutowireConfigurer is a BeanFactoryPostProcessor that enables you to register your own custom qualifier annotation types even if they are not annotated with Spring’s @Qualifier annotation.

### 7.9.7. @Resource
@Resource首先查找名字为customerPreferenceDao的bean，如果找不到，然后会再去查询Class类型为CustomerPreferenceDao的类，进行类型匹配。

@Autowired为Spring提供的注解，需要导入包org.springframework.beans.factory.annotation.Autowired;只按照byType注入。@Autowired注解是按照类型（byType）装配依赖对象，默认情况下它要求依赖对象必须存在，如果允许null值，可以设置它的required属性为false。如果我们想使用按照名称（byName）来装配，可以结合@Qualifier注解一起使用。

@Resource默认按照ByName自动注入，由J2EE提供，需要导入包javax.annotation.Resource。@Resource有两个重要的属性：name和type，而Spring将@Resource注解的name属性解析为bean的名字，而type属性则解析为bean的类型。所以，如果使用name属性，则使用byName的自动注入策略，而使用type属性时则使用byType自动注入策略。如果既不制定name也不制定type属性，这时将通过反射机制使用byName自动注入策略。

@Resource装配顺序：
 - 1、如果同时指定了name和type，则从Spring上下文中找到唯一匹配的bean进行装配，找不到则抛出异常。
 - 2、如果指定了name，则从上下文中查找名称（id）匹配的bean进行装配，找不到则抛出异常。
 - 3、如果指定了type，则从上下文中找到类似匹配的唯一bean进行装配，找不到或是找到多个，都会抛出异常。
 - 4、如果既没有指定name，又没有指定type，则自动按照byName方式进行装配；如果没有匹配，则回退为一个原始类型进行匹配，如果匹配则自动装配。
@Resource的作用相当于@Autowired，只不过@Autowired按照byType自动注入

### 7.10.4. Using filters to customize scanning
| Filter Type | Example Expression | Description |
| ----------- | :------------ | :--------------|
| aspectj | org.example..*Service+ | An AspectJ type expression to be matched by the target components. |
| regex | org\.example\.Default.* | A regex expression to be matched by the target components class names. |

```java
@Configuration
@ComponentScan(basePackages = "org.example",
includeFilters = @Filter(type = FilterType.REGEX, pattern = ".*Stub.*Repository"),
excludeFilters = @Filter(Repository.class))
public class AppConfig {
...
}
```
```xml
<beans>
  <context:component-scan base-package="org.example">
  <context:include-filter type="regex" expression=".*Stub.*Repository"/>
  <context:exclude-filter type="annotation"
    expression="org.springframework.stereotype.Repository"/>
  </context:component-scan>
</beans>
```
You can also disable the default filters by setting useDefaultFilters=false on the annotation or providing use-default-filters="false" as an attribute of the <component-scan/> element. This will in effect disable automatic detection of classes annotated with @Component, @Repository, @Service, @Controller, or @Configuration.

### 7.10.5. Defining bean metadata within components
@Bean和@configuration实现一个和@component相同的功能，都是注册一个bean到spring容器中。
Spring components can also contribute bean definition metadata to the container. You do this with the same @Bean annotation used to define bean metadata within @Configuration annotated classes

@Bean放在方法的注释上，它很明确地告诉被注释的方法，你给我产生一个Bean，然后交给Spring容器。也就是说，@Bean注册Bean到spring容器中，把注解放到方法上面，告诉spring, 从下面的方法去拿到一个Bean。

@Component被用在要被自动扫描和装配的类上。
@Bean主要被用在方法上，来显式声明要用生成的类。它可以让类的定义和声明一个Bean解耦。

**区别**
@component类不使用CGLIB增强去拦截方法和字段的调用；@Configuration中的@Bean则使用CGLIB代理
The difference is that @Component classes are not enhanced with CGLIB to intercept the invocation of methods and fields. CGLIB proxying is the means by which invoking methods or fields within @Bean methods in @Configuration classes creates bean metadata references to collaborating objects;

Note that calls to static @Bean methods will never get intercepted by the container, not even within @Configuration classes (see above). This is due to technical limitations: CGLIB subclassing can only override non-static methods.
regular @Bean methods in @Configuration classes need to be overridable, i.e. they must not be declared as private or final.

note that a single class may hold multiple @Bean methods for the same bean, as an arrangement of multiple factory methods to use depending on available dependencies at runtime. This is the same algorithm as for choosing the "greediest" constructor or factory method in other configuration scenarios: The variant with the largest number of satisfiable dependencies will be picked at construction time, analogous to how the container selects between multiple @Autowired constructors

### 7.10.7. Providing a scope for autodetected components
实现ScopeMetadataResolver接口类，可以自定义自己的scope

### 7.10.8. Providing qualifier metadata with annotations
相比起通过组件的类路径扫描， 更好的方式是在编译时生成索引，ApplicationContext检测索引比扫描包路径要好。尤其是在大型的应用中，这样可以减少刷新ApplicationContext的时间。使用方法，把下面的包依赖放到每个module中。

Rather than scanning the classpath to find components, it is also possible to generate an index at compilation time.
To generate the index, simply add an additional dependency to each module that contains components that are target for component scan directives
```xml
<dependencies>
  <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context-indexer</artifactId>
    <version>5.0.0.M5</version>
    <optional>true</optional>
  </dependency>
</dependencies>
```

> The index is enabled automatically when a META-INF/spring.components is found on the classpath.

## 7.11. Using JSR 330 Standard Annotations
Starting with Spring 3.0, Spring offers support for JSR-330 standard annotations (Dependency Injection). Those annotations are scanned in the same way as the Spring annotations. You just need to have the relevant jars in your classpath
```xml
<dependency>
  <groupId>javax.inject</groupId>
  <artifactId>javax.inject</artifactId>
  <version>1</version>
</dependency>
```

@Inject支持构造函数、方法和字段注解，也可能使用于静态实例成员。可注解成员可以是任意修饰符（private,package-private,protected,public）。注入顺序：构造函数、字段，然后是方法。父类的字段和方法注入优先于子类的字段和方法，同一类中的字段和方法是没有顺序的。
@Inject注解的构造函数可以是无参或多个参数的构造函数。@Inject每个类中最多注解一个构造函数。

用@Inject注解在字段注解：1、字段不能是final的。2、拥有一个合法的名称
用@Inject注解在方法上注解：1、不能是抽象方法。2、不能声明自身参数类型 3、可以有返回结果。4、拥有一个合法的名称。5、可以有0个或多个参数

### 7.11.1. Dependency Injection with @Inject and @Named
As with @Autowired, it is possible to use @Inject at the field level, method level and constructorargument level

### 7.11.2. @Named and @ManagedBean: standard equivalents to the @Component annotation
@Named和Spring的@Component功能相同。@Named可以有值，如果没有值生成的Bean名称默认和类名相同。

Instead of @Component, @javax.inject.Named or javax.annotation.ManagedBean may be used as follows:
```java
import javax.inject.Inject;
import javax.inject.Named;
@Named("movieListener") // @ManagedBean("movieListener") could be used as well
public class SimpleMovieLister {
  private MovieFinder movieFinder;
  @Inject
  public void setMovieFinder(MovieFinder movieFinder) {
    this.movieFinder = movieFinder;
  }
  // ...
}
```

It is very common to use @Component without specifying a name for the component. @Named can be used in a similar fashion

spring自带的@Autowired的缺省情况等价于JSR-330的@Inject注解；
Spring自带的@Qualifier的缺省的根据Bean名字注入情况等价于JSR-330的@Named注解；
Spring自带的@Qualifier的扩展@Qualifier限定描述符注解情况等价于JSR-330的@Qualifier注解。

### 7.11.3. Limitations of JSR-330 standard annotations

| Spring | javax.inject.* | javax.inject restrictions / comments |
| :------ | :------ | :------ |
| @Autowired | @Inject | @Inject has no 'required' attribute; can be used with Java 8’s Optional instead. |
| @Component | @Named / @ManagedBean | JSR-330 does not provide a composable model, just a way to identify named components. |
| @Scope("singleton") | @Singleton | The JSR-330 default scope is like Spring’s prototype. However, in order to keep it consistent with Spring’s general defaults, a JSR-330 bean declared in the Spring container is a singleton by default. In order to use a scope other than singleton, you should use Spring’s @Scope annotation. javax.inject also provides a @Scope annotation. Nevertheless, this one is only intended to be used for creating your own annotations. |
| @Qualifier | @Qualifier / @Named | javax.inject.Qualifier is just a meta-annotation for building custom qualifiers. Concrete String qualifiers (like Spring’s @Qualifier with a value) can be associated through javax.inject.Named |
| @Value | - | no equivalent |
| @Required | - | no equivalent |
| @Lazy | - | no equivalent |
| ObjectFactory | Provider | javax.inject.Provider is a direct alternative to Spring’s ObjectFactory, just with a shorter get() method name. It can also be used in combination with Spring’s @Autowired or with nonannotated constructors and setter methods. |

## 7.12. Java-based container configuration
### 7.12.1. Basic concepts: @Bean and @Configuration
@Bean和在xml中使用<bean></bean>的功能是一样的；@Bean可以和@Compent一起使用，但更多的是和@Configuration一起使用。
The @Bean annotation is used to indicate that a method instantiates, configures and initializes a new object to be managed by the Spring IoC container. For those familiar with Spring’s <beans/> XML configuration the @Bean annotation plays the same role as the <bean/> element. You can use @Bean annotated methods with any Spring @Component, however, they are most often used with @Configuration beans.
@Configuration classes allow inter-bean dependencies to be defined by simply calling other @Bean methods in the same class
```java
@Configuration
public class AppConfig {
  @Bean
  public MyService myService() {
    return new MyServiceImpl();
  }
}
```
equivalent
```xml
<beans>
  <bean id="myService" class="com.acme.services.MyServiceImpl"/>
</beans>
```
如果是@Component和@Bean组合使用，或者@Bean单独使用，被认为是'简化模式'，简化模式不能在@Bean中声明内部bean依赖，不能在一个@Bean上再次使用@Bean去调用其它的Bean。spring推荐@Bean和Configuration一起使用。
When @Bean methods are declared within classes that are not annotated with @Configuration they are referred to as being processed in a 'lite' mode.
Unlike full @Configuration, lite @Bean methods cannot easily declare inter-bean dependencies. Usually one @Bean method should not invoke another @Bean method when operating in 'lite' mode.
Only using @Bean methods within @Configuration classes is a recommended approach of ensuring that 'full' mode is always used.

### 7.12.2. Instantiating the Spring container using AnnotationConfigApplicationContext
#### Simple construction
In much the same way that Spring XML files are used as input when instantiating a ClassPathXmlApplicationContext, @Configuration classes may be used as input when instantiating an AnnotationConfigApplicationContext. This allows for completely XML-free usage of the Spring container:
```java
public static void main(String[] args) {
  ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
  MyService myService = ctx.getBean(MyService.class);
  myService.doStuff();
}
```
AnnotationConfigApplicationContext is not limited to working only with @Configuration classes. Any @Component or JSR-330 annotated class may be supplied as input to the constructor. For example:
```java
public static void main(String[] args) {
  ApplicationContext ctx = new AnnotationConfigApplicationContext(MyServiceImpl.class, Dependency1.class, Dependency2.class);
  MyService myService = ctx.getBean(MyService.class);
  myService.doStuff();
}
```
The above assumes that MyServiceImpl, Dependency1 and Dependency2 use Spring dependency injection annotations such as @Autowired.

#### Building the container programmatically using register(Class<?>…​)
```java
public static void main(String[] args) {
  AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
  ctx.register(AppConfig.class, OtherConfig.class);
  ctx.register(AdditionalConfig.class);
  ctx.refresh();
  MyService myService = ctx.getBean(MyService.class);
  myService.doStuff();
}
```
#### Enabling component scanning with scan(String…​)
To enable component scanning, just annotate your @Configuration class as follows
```java
@Configuration
@ComponentScan(basePackages = "com.acme")
public class AppConfig {
  ...
}
```
Experienced Spring users will be familiar with the XML declaration equivalent from Spring’s context: namespace
```xml
<beans>
  <context:component-scan base-package="com.acme"/>
</beans>
```
```java
public static void main(String[] args) {
  AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
  ctx.scan("com.acme");
  ctx.refresh();
  MyService myService = ctx.getBean(MyService.class);
}
```

### 7.12.3. Using the @Bean annotation

#### Specifying bean scope
```java
@Configuration
public class MyConfiguration {
  @Bean
  @Scope("prototype")
  public Encryptor encryptor() {
    // ...
  }
}
```

#### Bean description
```java
@Configuration
public class AppConfig {
  @Bean
  @Description("Provides a basic example of a bean")
  public Foo foo() {
    return new Foo();
  }
}
```

### 7.12.4. Using the @Configuration annotation
@Configuration is a class-level annotation indicating that an object is a source of bean definitions.
@Configuration classes declare beans via public @Bean annotated methods.

#### Further information about how Java-based configuration works internally
Note that as of Spring 3.2, it is no longer necessary to add CGLIB to your classpath because CGLIB classes have been repackaged under org.springframework.cglib and included directly within the spring-core JAR

### 7.12.5. Composing Java-based configurations
#### Using the @Import annotation
@Import annotation allows for loading @Bean definitions from another configuration class
```java
@Configuration
public class ConfigA {
  @Bean
  public A a() {
    return new A();
  }
}
@Configuration
@Import(ConfigA.class)
public class ConfigB {
  @Bean
  public B b() {
    return new B();
  }
}
```

Remember that @Configuration classes are ultimately just another bean in the container: This means that they can take advantage of @Autowired and @Value injection etc just like any other bean!

> be particularly careful with BeanPostProcessor and BeanFactoryPostProcessor definitions via @Bean. Those should usually be declared as static @Bean methods, not triggering the instantiation of their containing configuration class. Otherwise, @Autowired and @Value won’t work on the configuration class itself since it is being created as a bean instance too early


#### Conditionally include @Configuration classes or @Bean methods
@Conditional定义：意思是只有满足一些列条件之后创建一个bean。继承Condition接口类，并需要实现matches方法，并返回boolean值。

The @Conditional annotation indicates specific org.springframework.context.annotation.Condition implementations that should be consulted before a @Bean is registered.

Implementations of the Condition interface simply provide a matches(…) method that returns true or false. For example, here is the actual Condition implementation used for @Profile:
```java
@Override
public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
  if (context.getEnvironment() != null) {
    // Read the @Profile annotation attributes
    MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(Profile.class.getName());
    if (attrs != null) {
      for (Object value : attrs.get("value")) {
        if (context.getEnvironment().acceptsProfiles(((String[]) value))) {
          return true;
        }
      }
      return false;
    }
  }
  return true;
}
```

#### Combining Java and XML configuration
<context:annotation-config/>等价于<context:component-scan base-package="****"/>

@Configuration class-centric use of XML with @ImportResource
```java
@Configuration
@ImportResource("classpath:/com/acme/properties-config.xml")
public class AppConfig {
  @Value("${jdbc.url}")
  private String url;
  @Value("${jdbc.username}")
  private String username;
  @Value("${jdbc.password}")
  private String password;
  @Bean
  public DataSource dataSource() {
    return new DriverManagerDataSource(url, username, password);
  }
}
```
properties-config.xml
```xml
<beans>
<context:property-placeholder location="classpath:/com/acme/jdbc.properties"/>
</beans>
```
```properties
jdbc.properties
jdbc.url=jdbc:hsqldb:hsql://localhost/xdb
jdbc.username=sa
jdbc.password=
```
```java
public static void main(String[] args) {
  ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
  TransferService transferService = ctx.getBean(TransferService.class);
  // ...
}
```

## 7.13. Environment abstraction
### 7.13.1. Bean definition profiles
#### @Profile
例如在开发环境与生产环境使用不同的参数，可以配置两套配置文件，通过@profile来激活需要的环境。
比如：开发时进行一些数据库测试，希望链接到一个测试的数据库，以避免对开发数据库的影响。可以使用@profile来切换不同的环境。
The @Profile annotation allows you to indicate that a component is eligible for registration when one or more specified profiles are active
Using our example above, we can rewrite the dataSource configuration as follows:

```java
@Configuration
@Profile("dev")
public class StandaloneDataConfig {
  @Bean
  public DataSource dataSource() {
    return new EmbeddedDatabaseBuilder()
              .setType(EmbeddedDatabaseType.HSQL)
              .addScript("classpath:com/bank/config/sql/schema.sql")
              .addScript("classpath:com/bank/config/sql/test-data.sql")
              .build();
  }
}
@Configuration
@Profile("production")
public class JndiDataConfig {
  @Bean(destroyMethod="")
  public DataSource dataSource() throws Exception {
    Context ctx = new InitialContext();
    return (DataSource) ctx.lookup("java:comp/env/jdbc/datasource");
  }
}
```

#### XML bean definition profiles
```xml
<beans xmlns="http://www.springframework.org/schema/beans"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xmlns:jdbc="http://www.springframework.org/schema/jdbc"
xmlns:jee="http://www.springframework.org/schema/jee"
xsi:schemaLocation="...">
<!-- other bean definitions -->
  <beans profile="dev">
    <jdbc:embedded-database id="dataSource">
      <jdbc:script location="classpath:com/bank/config/sql/schema.sql"/>
      <jdbc:script location="classpath:com/bank/config/sql/test-data.sql"/>
    </jdbc:embedded-database>
  </beans>
  <beans profile="production">
    <jee:jndi-lookup id="dataSource" jndi-name="java:comp/env/jdbc/datasource"/>
  </beans>
</beans>
```
#### Activating a profile
If a @Component or @Configuration class is marked with @Profile({"p1", "p2"}), that class will not be registered/processed unless profiles 'p1' and/or 'p2' have been activated.

```java
AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
ctx.getEnvironment().setActiveProfiles("dev");
ctx.register(SomeConfig.class, StandaloneDataConfig.class, JndiDataConfig.class);
ctx.refresh();
```

```java
ctx.getEnvironment().setActiveProfiles("profile1", "profile2");
```
Declaratively, spring.profiles.active may accept a comma-separated list of profile names:
```text
-Dspring.profiles.active="profile1,profile2"
```

### 7.13.3. @PropertySource
@PropertySource 和 @Value 两个注解都可以从配置文件properties中读取值

通过 @PropertySource 注解将properties配置文件中的值存储到Spring的 Environment 中，Environment接口提供方法去读取配置文件中的值，参数是properties文件中定义的key值。常配合Environment env使用:  env.getProperty("testbean.name")
@PropertySources ,从名字就可以猜测到它是为多配置文件而准备的。
@PropertySource 允许忽略不存在的配置文件。ignoreResourceNotFound=true

Given a file "app.properties" containing the key/value pair testbean.name=myTestBean, the following @Configuration class uses @PropertySource in such a way that a call to testBean.getName() will return "myTestBean".

```java
@Configuration
@PropertySource("classpath:/com/myco/app.properties")
public class AppConfig {
  @Autowired
  Environment env;
  @Bean
  public TestBean testBean() {
    TestBean testBean = new TestBean();
    testBean.setName(env.getProperty("testbean.name"));
    return testBean;
  }
}
```

Any ${…} placeholders present in a @PropertySource resource location will be resolved against the set of property sources already registered against the environment. For example:
```java
@Configuration
@PropertySource("classpath:/com/${my.placeholder:default/path}/app.properties")
public class AppConfig {
  @Autowired
  Environment env;
  @Bean
  public TestBean testBean() {
  TestBean testBean = new TestBean();
    testBean.setName(env.getProperty("testbean.name"));
    return testBean;
  }
}
```
Assuming that "my.placeholder" is present in one of the property sources already registered, e.g. system properties or environment variables, the placeholder will be resolved to the corresponding value. If not, then "default/path" will be used as a default.

## 7.14. Registering a LoadTimeWeaver
spring使用LoadTimeWeaver动态的转换classes，并把它们加载到JVM中。
The LoadTimeWeaver is used by Spring to dynamically transform classes as they are loaded into the
Java virtual machine (JVM).

在Java 语言中，从织入切面的方式上来看，存在三种织入方式：编译期织入、类加载期织入和运行期织入。
 - 编译期织入是指在Java编译期，采用特殊的编译器，将切面织入到Java类中；
 - 而类加载期织入则指通过特殊的类加载器，在类字节码加载到JVM时，织入切面；
 - 运行期织入则是采用CGLib工具或JDK动态代理进行切面的织入。
AspectJ采用编译期织入和类加载期织入的方式织入切面，是语言级的AOP实现，提供了完备的AOP支持。它用AspectJ语言定义切面，在编译期或类加载期将切面织入到Java类中。

同一份代码、同一份配置，只需要在VM启动参数中稍加变化，即可实现同一个应用包在不同环境下可以自由选择使用使用AOP功能：
http://www.cnblogs.com/davidwang456/p/5633609.html（沙箱环境AOP拦截，生产环境不予拦截。）

## 7.15. Additional Capabilities of the ApplicationContext

### 7.15.2. Standard and Custom Events

Event handling in the ApplicationContext is provided through the ApplicationEvent class and ApplicationListener interface. If a bean that implements the ApplicationListener interface is deployed into the context, every time an ApplicationEvent gets published to the ApplicationContext, that bean is notified. Essentially, this is the standard Observer design pattern

Spring provides the following standard events −

| Event | Explanation |
| ------- | :----- |
| ContextRefreshedEvent |Published when the ApplicationContext is initialized or refreshed, for example, using the refresh() method on the ConfigurableApplicationContext interface. "Initialized" here means that all beans are loaded, post-processor beans are detected and activated, singletons are preinstantiated, and the ApplicationContext object is ready for use. As long as the context has not been closed, a refresh can be triggered multiple times, provided that the chosen ApplicationContext actually supports such "hot" refreshes. For example, XmlWebApplicationContext supports hot refreshes, but GenericApplicationContext does not. |
| ContextStartedEvent |  Published when the ApplicationContext is started, using the start() method on the ConfigurableApplicationContext interface. "Started" here means that all Lifecycle beans receive an explicit start signal. Typically this signal is used to restart beans after an explicit stop, but it may also be used to start components that have not been configured for autostart , for example, components that have not already started on initialization. |
| ContextStoppedEvent | Published when the ApplicationContext is stopped, using the stop() method on the ConfigurableApplicationContext interface. "Stopped" here means that all Lifecycle beans receive an explicit stop signal. A stopped context may be restarted through a start() call. |
| ContextClosedEvent | Published when the ApplicationContext is closed, using the close() method on the ConfigurableApplicationContext interface. "Closed" here means that all singleton beans are destroyed. A closed context reaches its end of life; it cannot be refreshed or restarted. |
| RequestHandledEvent | A web-specific event telling all beans that an HTTP request has been serviced. This event is published after the request is complete. This event is only applicable to web applications using Spring’s DispatcherServlet. |

You can also create and publish your own custom events.

```java
public class BlackListEvent extends ApplicationEvent {
  private final String address;
  private final String test;
  public BlackListEvent(Object source, String address, String test) {
    super(source);
    this.address = address;
    this.test = test;
  }
  // accessor and other methods...
}
```
To publish a custom ApplicationEvent, call the publishEvent() method on an ApplicationEventPublisher. Typically this is done by creating a class that implements ApplicationEventPublisherAware and registering it as a Spring bean
```java
public class EmailService implements ApplicationEventPublisherAware {
  private List<String> blackList;
  private ApplicationEventPublisher publisher;
  public void setBlackList(List<String> blackList) {
    this.blackList = blackList;
  }
  public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
    this.publisher = publisher;
  }
  public void sendEmail(String address, String text) {
    if (blackList.contains(address)) {
      BlackListEvent event = new BlackListEvent(this, address, text);
      publisher.publishEvent(event);
      return;
    }
    // send email...
  }
}
```
To receive the custom ApplicationEvent, create a class that implements ApplicationListener and register it as a Spring bean.
```java
public class BlackListNotifier implements ApplicationListener<BlackListEvent> {
  private String notificationAddress;
  public void setNotificationAddress(String notificationAddress) {
    this.notificationAddress = notificationAddress;
  }
  public void onApplicationEvent(BlackListEvent event) {
    // notify appropriate parties via notificationAddress...
  }
}
```
You may register as many event listeners as you wish, but note that by default event listeners receive events synchronously. This means the publishEvent() method blocks until all listeners have finished processing the event

> Spring’s eventing mechanism is designed for simple communication between Spring beans within the same application context

#### Annotation-based Event Listeners
As of Spring 4.2, an event listener can be registered on any public method of a managed bean via the EventListener annotation. The BlackListNotifier can be rewritten as follows: @EventListener
```java
public class BlackListNotifier {
  private String notificationAddress;
  public void setNotificationAddress(String notificationAddress) {
    this.notificationAddress = notificationAddress;
  }
  @EventListener // <------
  public void processBlackListEvent(BlackListEvent event) {
    // notify appropriate parties via notificationAddress...
  }
}
```
If your method should listen to several events or if you want to define it with no parameter at all, the event type(s) can also be specified on the annotation itself:
```java
@EventListener({ContextStartedEvent.class, ContextRefreshedEvent.class})
public void handleContextStart() {

}
```

#### Asynchronous Listeners
If you want a particular listener to process events asynchronously, simply reuse the regular @Async support:
```java
@EventListener
@Async
public void processBlackListEvent(BlackListEvent event) {
  // BlackListEvent is processed in a separate thread
}
```
Be aware of the following limitations when using asynchronous events:
1. If the event listener throws an Exception it will not be propagated to the caller, check AsyncUncaughtExceptionHandler for more details.
2. Such event listener cannot send replies. If you need to send another event as the result of the processing, inject ApplicationEventPublisher to send the event manually.

#### Ordering Listeners
If you need the listener to be invoked before another one, just add the @Order annotation to the method declaration:
```java
@EventListener
@Order(42)
public void processBlackListEvent(BlackListEvent event) {
  // notify appropriate parties via notificationAddress...
}
```

### 7.15.3. Convenient access to low-level resources
An application context is a ResourceLoader, which can be used to load Resources

You can configure a bean deployed into the application context to implement the special callback interface, ResourceLoaderAware, to be automatically called back at initialization time with the application context itself passed in as the ResourceLoader.

### 7.15.5. Deploying a Spring ApplicationContext as a Java EE RAR file
可以把Spring ApplicationContext打成一个rar文件，把context以及需要的bean，jars等，放到Java EE rar中，作为一个部署单元。
It is possible to deploy a Spring ApplicationContext as a RAR file, encapsulating the context and all of its required bean classes and library JARs in a Java EE RAR deployment unit.

> Such RAR deployment units are usually self-contained; they do not expose components to the outside world, not even to other modules of the same application.

## 7.16. The BeanFactory
### 7.16.1. BeanFactory or ApplicationContext?
Because the ApplicationContext includes all functionality of the BeanFactory, it is generally recommended over the BeanFactory

If you use only a plain BeanFactory, a fair amount of support such as transactions and AOP will not take effect, at least not without some extra steps on your part.

The following table lists features provided by the BeanFactory and ApplicationContext interfaces and implementations.

| Feature | BeanFactory | ApplicationContext |
| -------- | :---------- | :--------- |
| Bean instantiation/wiring | Yes | Yes |
| Automatic BeanPostProcessor registration | No | Yes |
| Automatic BeanFactoryPostProcessor registration | No | No |
| Convenient MessageSource access (for i18n) | No | No |
| ApplicationEvent publication | No | No |

ApplicationContext实现要比plain BeanFactory要好，尤其是在使用beanfactorypostprocessor和BeanPostProcessors时，如果只是使用plain BeanFactory,那么它是不支持事物和AOP的。
ApplicationContext implementations are preferred above plain BeanFactory implementations in the vast majority of Spring-backed applications, especially when using BeanFactoryPostProcessors and BeanPostProcessors.


# 8. Resources
---------------
## 8.2. The Resource interface
```java
public interface Resource extends InputStreamSource {
  boolean exists();
  boolean isOpen();
  URL getURL() throws IOException;
  File getFile() throws IOException;
  Resource createRelative(String relativePath) throws IOException;
  String getFilename();
  String getDescription();
}
public interface InputStreamSource {
  InputStream getInputStream() throws IOException;
}
```

Some of the most important methods from the Resource interface are:
 - getInputStream(): locates and opens the resource, returning an InputStream for reading from the resource. It is expected that each invocation returns a fresh InputStream. It is the responsibility
of the caller to close the stream.
 - exists(): returns a boolean indicating whether this resource actually exists in physical form.
 - isOpen(): returns a boolean indicating whether this resource represents a handle with an open stream. If true, the InputStream cannot be read multiple times, and must be read once only and then closed to avoid resource leaks. Will be false for all usual resource implementations, with the exception of InputStreamResource.
 - getDescription(): returns a description for this resource, to be used for error output when working with the resource. This is often the fully qualified file name or the actual URL of the resource.

 the Resource abstraction does not replace functionality: it wraps it where possible. For example, a UrlResource wraps a URL, and uses the wrapped URL to do its work.

## 8.3. Built-in Resource implementations

### 8.3.1. UrlResource
The UrlResource wraps a java.net.URL, and may be used to access any object that is normally accessible via a URL, such as files, an HTTP target, an FTP target, etc.

### 8.3.2. ClassPathResource
This class represents a resource which should be obtained from the classpath. This uses either the thread context class loader, a given class loader, or a given class for loading resources.

This Resource implementation supports resolution as java.io.File if the class path resource resides in the file system, but not for classpath resources which reside in a jar and have not been expanded (by the servlet engine, or whatever the environment is) to the filesystem.

### 8.3.3. FileSystemResource
This is a Resource implementation for java.io.File handles. It obviously supports resolution as a File, and as a URL

### 8.3.4. ServletContextResource
This is a Resource implementation for ServletContext resources, interpreting relative paths within the relevant web application’s root directory

## 8.4. The ResourceLoader
classpath
```java
Resource template = ctx.getResource("classpath:some/resource/path/myTemplate.txt");
```
使用前缀比如calsspath/file等，可以指定使用相关的**Resource去解析。
Similarly, one can force a UrlResource to be used by specifying any of the standard java.net.URL prefixes:
file
```java
Resource template = ctx.getResource("file:///some/resource/path/myTemplate.txt");
```
http

```java
Resource template = ctx.getResource("http://myhost.com/resource/path/myTemplate.txt");
```

The following table summarizes the strategy for converting Strings to Resources:

| Prefix | Example | Explanation |
| -------- | :---------- | :--------- |
| classpath: | classpath:com/myapp/config.xml | Loaded from the classpath |
| file: | file:///data/config.xml | Loaded as a URL, from the filesystem. |
| http: | http://myserver/logo.png | Loaded as a URL |
| (none) | /data/config.xml | Depends on the underlying ApplicationContext. |


## 8.5. The ResourceLoaderAware interface
When a class implements ResourceLoaderAware and is deployed into an application context (as a Spring-managed bean), it is recognized as ResourceLoaderAware by the application context. The application context will then invoke the setResourceLoader(ResourceLoader), supplying itself as the argument (remember, all application contexts in Spring implement the ResourceLoader interface).

since an ApplicationContext is a ResourceLoader, the bean could also implement the ApplicationContextAware interface and use the supplied application context directly to load resources, but in general, it’s better to use the specialized ResourceLoader interface if that’s all that’s needed.

```java
public interface ResourceLoaderAware {
  void setResourceLoader(ResourceLoader resourceLoader);
}
```

## 8.6. Resources as dependencies
So if myBean has a template property of type Resource, it can be configured with a simple string for that resource, as follows:
```xml
<bean id="myBean" class="...">
  <property name="template" value="some/resource/path/myTemplate.txt"/>
</bean>
```
The following two examples show how to force a ClassPathResource and a UrlResource (the latter being used to access a filesystem file)
```xml
<property name="template" value="classpath:some/resource/path/myTemplate.txt">
```
```xml
<property name="template" value="file:///some/resource/path/myTemplate.txt"/>
```

## 8.7. Application contexts and Resource paths

### 8.7.1. Constructing application contexts

When such a location path doesn’t have a prefix, the specific Resource type built from that path and used to load the bean definitions, depends on and is appropriate to the specific application context.

```xml
ApplicationContext ctx = new ClassPathXmlApplicationContext("conf/appContext.xml");
```
```xml
ApplicationContext ctx = new FileSystemXmlApplicationContext("conf/appContext.xml");
```
```xml
ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath:conf/appContext.xml");
```

#### Constructing ClassPathXmlApplicationContext instances - shortcuts

如图所示，目录下有2个xml文件，通过ClassPathXmlApplicationContext加载时，即使不写xml的路径，只要把MessengerService.class放进去，ClassPathXmlApplicationContext就能通过MessagerService.class找到 services.xml和daos.xml。
The basic idea is that one supplies merely a string array containing just the filenames of the XML files themselves (without the leading path information), and one also supplies a Class; the ClassPathXmlApplicationContext will derive the path information from the supplied class
```text
com/
    foo/
  services.xml
  daos.xml
    MessengerService.class
```
获得的方法如下面的代码所示。
```java
ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] {"services.xml", "daos.xml"}, MessengerService.class);
```

### 8.7.2. Wildcards in application context constructor resource paths
#### Ant-style Patterns
如果指定的是一个文件的url路径，那么通配符没有问题；如果指定的是一个classpath路径，也没有问题；如果通过末尾是非通配符去获取一个jar的URL，那么就要好好测一下，再依赖它。
If the specified path is already a file URL (either explicitly, or implicitly because the base ResourceLoader is a filesystem one, then wildcarding is guaranteed to work in a completely portable fashion.
If the specified path is a classpath location, then the resolver must obtain the last non-wildcard path segment URL via a Classloader.getResource() call.

#### The Classpath*: portability classpath*: prefix
When constructing an XML-based application context, a location string may use the special classpath*: prefix:
```xml
ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:conf/appContext.xml");
```

### 8.7.3. FileSystemResource caveats
FileSystemResource 并不附属于 FileSystemApplicationContext ，也就是说，它不是ResourceLoader 的实现类。
A FileSystemResource that is not attached to a FileSystemApplicationContext (that is, a FileSystemApplicationContext is not the actual ResourceLoader)
```xml
ApplicationContext ctx = new FileSystemXmlApplicationContext("conf/context.xml");
```
equivalent (多了一个"/")
```xml
ApplicationContext ctx = new FileSystemXmlApplicationContext("/conf/context.xml");
```

# 9. Validation, Data Binding, and Type Conversion
---------------

## 9.4. Bean manipulation and the BeanWrapper
the BeanWrapper offers functionality to set and get property values (individually or in bulk), get property descriptors, and to query properties to determine if they are readable or writable. Also, the BeanWrapper offers support for nested properties, enabling the setting of properties on sub-properties to an unlimited depth. Then, the BeanWrapper supports the ability to add standard JavaBeans PropertyChangeListeners and VetoableChangeListeners, without the need for supporting code in the target class. Last but not least, the BeanWrapper provides support for the setting of indexed properties. The BeanWrapper usually isn’t used by application code directly, but by the DataBinder and the BeanFactory.

### 9.4.1. Setting and getting basic and nested properties

```java
public class Company {
  private String name;
  private Employee managingDirector;
  public String getName() {
    return this.name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public Employee getManagingDirector() {
    return this.managingDirector;
  }
  public void setManagingDirector(Employee managingDirector) {
    this.managingDirector = managingDirector;
  }
}
public class Employee {
  private String name;
  private float salary;
  public String getName() {
    return this.name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public float getSalary() {
    return salary;
  }
  public void setSalary(float salary) {
    this.salary = salary;
  }
}
```

```java
BeanWrapper company = new BeanWrapperImpl(new Company());
// setting the company name..
company.setPropertyValue("name", "Some Company Inc.");
// ... can also be done like this:
PropertyValue value = new PropertyValue("name", "Some Company Inc.");
company.setPropertyValue(value);
// ok, let's create the director and tie it to the company:
BeanWrapper jim = new BeanWrapperImpl(new Employee());
jim.setPropertyValue("name", "Jim Stravinsky");
company.setPropertyValue("managingDirector", jim.getWrappedInstance());
// retrieving the salary of the managingDirector through the company
Float salary = (Float) company.getPropertyValue("managingDirector.salary");
```


### 9.4.2. Built-in PropertyEditor implementations
PropertyEditors是属性编辑器，将spring IOC容器中使用的字符串名字转成相关的java类型，比如class类等。所以属性编辑器其实就是一个类型转换器。它负责String与Object之间的转换。

#### Registering additional custom PropertyEditors
如果使用BeanFactory，用户需要手工调用registerCustomEditor(Class requiredType, PropertyEditor propertyEditor)方法注册自定义属性编辑器；如果使用ApplicationContext，则只需要在配置文件通过CustomEditorConfigurer注册就可以了。一般情况下，我们当然使用ApplicationContext。
it is strongly recommended that it is used with the ApplicationContext, where it may be deployed in similar fashion to any other bean, and automatically detected and applied.

## 9.5. Spring Type Conversion
### 9.5.4. ConversionService API
ConversionService定义了一个统一的API在运行时执行类型转换逻辑，ConversionService通常用Facade模式
The ConversionService defines a unified API for executing type conversion logic at runtime. Converters are often executed behind this facade interface

###　9.5.5. Configuring a ConversionService
ConversionService是无状态的，被设计成在应用启动时被实例化，并被多线程共享
A ConversionService is a stateless object designed to be instantiated at application startup, then shared between multiple threads.

> If no ConversionService is registered with Spring, the original PropertyEditor-based system is used.

## 9.8. Spring Validation
### 9.8.1. Overview of the JSR-303 Bean Validation API
JSR-303 allows you to define declarative validation constraints against such properties:
```java
public class PersonForm {
  @NotNull
  @Size(max=64)
  private String name;

  @Min(0)
  private int age;
}
```

# 10. Spring Expression Language (SpEL)
---------------
## 10.1. Introduction
spring表达式语言模块（Spring Expression Language，简称SpEL）在运行时提供了查询和操作一个对象图的强大的表达式语言。
SpEL能够在运行时构建复杂表达式，存取对象属性、对象方法调用等。
所有的SpEL都支持XML和Annotation两种方式，格式：#{ SpEL expression }
The Spring Expression Language (SpEL for short) is a powerful expression language that supports querying and manipulating an object graph at runtime.

## 10.4. Expression support for defining bean definitions
### 10.4.1. XML based configuration
```xml
<bean id="numberGuess" class="org.spring.samples.NumberGuess">
  <property name="randomNumber" value="#{ T(java.lang.Math).random() * 100.0 }"/>
  <!-- other properties -->
</bean>
<bean id="shapeGuess" class="org.spring.samples.ShapeGuess">
  <property name="initialShapeSeed" value="#{ numberGuess.randomNumber }"/>
  <!-- other properties -->
</bean>
```
### 10.4.2. Annotation-based configuration
```java
public static class FieldValueTestBean{
  @Value("#{ systemProperties['user.region'] }")
  private String defaultLocale;

  public void setDefaultLocale(String defaultLocale) {
    this.defaultLocale = defaultLocale;
  }
  public String getDefaultLocale() {
    return this.defaultLocale;
  }
}
```
## 10.5. Language Reference

### 10.5.7. Operators

#### Relational operators

SpEl 支持instanceof和基于matches的正则表达式
In addition to standard relational operators SpEL supports the instanceof and regular expression based matches operator.

# 11. Aspect Oriented Programming with Spring
---------------
## 11.1. Introduction
One of the key components of Spring is the AOP framework. While the Spring IoC container does not depend on AOP, meaning you do not need to use AOP if you don’t want to, AOP complements Spring IoC to provide a very capable middleware solution

### 11.1.1. AOP concepts
Pointcut: 用来指定join point（通俗来讲就是描述的一组符合某个条件的join point）。通常使用pointcut表达式来限定joint point，Spring默认使用AspectJ pointcut expression language
Introduction：给对象增加方法或者属性。
Target object: Advice起作用的那个对象
AOP proxy: 为实现AOP所生成的代理。在Spring中有两种方式生成代理:JDK代理和CGLIB代理。
Weaving：将Advice织入join point的这个过程

 - Join point: a modularization of a concern that cuts across multiple classes. Transaction management is a good example of a crosscutting concern in enterprise Java applications. In Spring AOP, aspects are implemented using regular classes (the schema-based approach) or regular classes annotated with the @Aspect annotation (the @AspectJ style)
 - Advice: a point during the execution of a program, such as the execution of a method or the handling of an exception. In Spring AOP, a join point always represents a method execution.
 - Advice: action taken by an aspect at a particular join point. Different types of advice include "around," "before" and "after" advice. (Advice types are discussed below.) Many AOP frameworks, including Spring, model an advice as an interceptor, maintaining a chain of interceptors around the join point.
 - Pointcut: a predicate that matches join points. Advice is associated with a pointcut expression and runs at any join point matched by the pointcut (for example, the execution of a method with a certain name). The concept of join points as matched by pointcut expressions is central to AOP, and Spring uses the AspectJ pointcut expression language by default.
 - Introduction: declaring additional methods or fields on behalf of a type. Spring AOP allows you to introduce new interfaces (and a corresponding implementation) to any advised object. For example, you could use an introduction to make a bean implement an IsModified interface, to simplify caching. (An introduction is known as an inter-type declaration in the AspectJ community.)
 - Target object: object being advised by one or more aspects. Also referred to as the advised object. Since Spring AOP is implemented using runtime proxies, this object will always be a proxied object.
 - AOP proxy: an object created by the AOP framework in order to implement the aspect contracts (advise method executions and so on). In the Spring Framework, an AOP proxy will be a JDK dynamic proxy or a CGLIB proxy.
 - Weaving: linking aspects with other application types or objects to create an advised object. This can be done at compile time (using the AspectJ compiler, for example), load time, or at runtime. Spring AOP, like other pure Java AOP frameworks, performs weaving at runtime.


 - Before advice:  执行在join point之前的advice,但是它不能阻止joint point的执行流程，除非抛出了一个异常（exception）
 - After returning advice: 执行在join point这个方法返回之后的advice。
 - After throwing advice: 执行在join point抛出异常之后的advice
 - After(finally) advice: 执行在join point返回之后或者抛出异常之后的advice，通常用来释放所使用的资源
 - Around advice: 执行在join point这个方法执行之前与之后的advice

### 11.1.2. Spring AOP capabilities and goals
Spring AOP is implemented in pure Java. There is no need for a special compilation process. Spring AOP does not need to control the class loader hierarchy, and is thus suitable for use in a Servlet container or application server.

Spring AOP currently supports only method execution join points (advising the execution of methods on Spring beans).
If you need to advise field access and update join points, consider a language such as AspectJ.
There are some things you cannot do easily or efficiently with Spring AOP, such as advise very fine-grained objects (such as domain objects typically): AspectJ is the best choice in such cases.

### 11.1.3. AOP Proxies
Spring AOP can also use CGLIB proxies. This is necessary to proxy classes rather than interfaces. CGLIB is used by default if a business object does not implement an interface. As it is good practice
to program to interfaces rather than classes; business classes normally will implement one or more business interfaces.

## 11.2. @AspectJ support
aspectjweaver.jar library is on the classpath of your application

### 11.2.1. Enabling @AspectJ Support

@AspectJ support with Java @Configuration add the @EnableAspectJAutoProxy annotation:

#### Enabling @AspectJ Support with Java configuration

```java
@Configuration
@EnableAspectJAutoProxy
public class AppConfig {
}
```

#### Enabling @AspectJ Support with XML configuration
```xml
<aop:aspectj-autoproxy/>
```

### 11.2.2. Declaring an aspect
A regular bean definition in the application context, pointing to a bean class that has the @Aspect annotation:
```xml
<bean id="myAspect" class="org.xyz.NotVeryUsefulAspect">
<!-- configure properties of aspect here as normal -->
</bean>
```
And the NotVeryUsefulAspect class definition, annotated with org.aspectj.lang.annotation.Aspect annotation;
```java
package org.xyz;
import org.aspectj.lang.annotation.Aspect;
@Aspect
public class NotVeryUsefulAspect {
}
```

> Autodetecting aspects through component scanning

> @Aspect annotation is not sufficient for autodetection in the classpath: For that purpose, you need to add a separate @Component annotation

> Advising aspects with other aspects?

> In Spring AOP, it is not possible to have aspects themselves be the target of advice from other aspects. The @Aspect annotation on a class marks it as an aspect, and hence excludes it from auto-proxying.

### 11.2.3. Declaring a pointcut
#### Supported Pointcut Designators
 - execution：用于匹配方法执行的连接点
 - within：用于匹配指定类型内的方法执行
 - this：用于匹配当前AOP代理对象类型的执行方法；注意是AOP代理对象的类型匹配，这样就可能包括引入接口也类型匹配
 - target：用于匹配当前目标对象类型的执行方法；注意是目标对象的类型匹配，这样就不包括引入接口也类型匹配
 - args：用于匹配当前执行的方法传入的参数为指定类型的执行方法
 - @target：用于匹配当前目标对象类型的执行方法，其中目标对象持有指定的注解
 - @args：用于匹配当前执行的方法传入的参数持有指定注解的执行
 - @within：用于匹配所以持有指定注解类型内的方法
 - @annotation：用于匹配当前执行方法持有指定注解的方法

protected方法不会被spring AOP框架拦截，也不会JDK代理或者CGLIB代理拦截。如果需要对protected/private/contructor方法拦截，可以考虑使用 Spring-driven native AspectJ weaving 替代 Spring’s proxy-based AOP framework
Due to the proxy-based nature of Spring’s AOP framework, protected methods are by definition not intercepted, neither for JDK proxies (where this isn’t applicable) nor for CGLIB proxies (where this is technically possible but not recommendable for AOP purposes). As a consequence, any given pointcut will be matched against public methods only!

If your interception needs include protected/private methods or even constructors, consider the use of Spring-driven native AspectJ weaving instead of Spring’s proxy-based AOP framework.

the bean PCD(切入点指示符) is only supported in Spring AOP - and not in native AspectJ weaving.

#### Examples
the execution of any method defined in the service package:
```java
execution(* com.xyz.service.*.*(..))
```
the execution of any method defined in the service package or a sub-package:
```java
execution(* com.xyz.service..*.*(..))
```
any join point (method execution only in Spring AOP) within the service package:
```java
within(com.xyz.service..*)
```
any join point (method execution only in Spring AOP) where the proxy implements the AccountService interface:
```java
this(com.xyz.service.AccountService)
```
any join point (method execution only in Spring AOP) where the target object implements the AccountService interface:
```java
target(com.xyz.service.AccountService)
```

此处省略若干.....


###　11.2.4. Declaring advice
#### Before advice
#### After returning advice
#### After throwing advice
After throwing advice runs when a matched method execution exits by throwing an exception. It is declared using the @AfterThrowing annotation:
```java
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.AfterThrowing;
@Aspect
public class AfterThrowingExample {
  @AfterThrowing("com.xyz.myapp.SystemArchitecture.dataAccessOperation()")
  public void doRecoveryActions() {
    // ...
  }
}
```
Often you want the advice to run only when exceptions of a given type are thrown, and you also often need access to the thrown exception in the advice body. Use the throwing attribute to both restrict matching (if desired, use Throwable as the exception type otherwise) and bind the thrown exception to an advice parameter.
```java
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.AfterThrowing;
@Aspect
public class AfterThrowingExample {
  @AfterThrowing(
    pointcut="com.xyz.myapp.SystemArchitecture.dataAccessOperation()",
    throwing="ex")
  public void doRecoveryActions(DataAccessException ex) {
    // ...
  }
}
```
#### After (finally) advice
After advice must be prepared to handle both normal and exception return conditions. It is typically used for releasing resources, etc.
```java
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.After;
@Aspect
public class AfterFinallyExample {
  @After("com.xyz.myapp.SystemArchitecture.dataAccessOperation()")
  public void doReleaseLock() {
    // ...
  }
}
```
#### Around advice
#### Advice parameters
##### Access to the current JoinPoint
getArgs() (returns the method arguments),
getThis() (returns the proxy object),
getTarget() (returns the target object),
getSignature() (returns a description of the method that is being advised) and toString() (prints a useful description of the method being advised).

##### Passing parameters to advice

Suppose you want to advise the execution of dao operations that take an Account object as the first parameter, and you need access to the account in the advice body.
You could write the following:
```java
@Before("com.xyz.myapp.SystemArchitecture.dataAccessOperation() && args(account,..)")
public void validateAccount(Account account) {
// ...
}
```
The args(account,..) part of the pointcut expression serves two purposes: firstly, it restricts matching to only those method executions where the method takes at least one parameter, and the argument passed to that parameter is an instance of Account; secondly, it makes the actual Account object available to the advice via the account parameter.

##### Advice parameters and generics
it’s worth pointing out that this won’t work for generic collections. So you cannot define a pointcut like this:

```java
@Before("execution(* ..Sample+.sampleGenericCollectionMethod(*)) && args(param)")
public void beforeSampleMethod(Collection<MyType> param) {
// Advice implementation
}
```
To make this work we would have to inspect every element of the collection, which is not reasonable as we also cannot decide how to treat null values in general.

#### Advice ordering
When two pieces of advice defined in different aspects both need to run at the same join point, unless you specify otherwise the order of execution is undefined.
You can control the order of execution by specifying precedence. This is done in the normal Spring way by either implementing the org.springframework.core.Ordered interface in the aspect class or annotating it with the Order annotation. Given two aspects, the aspect returning the lower value from Ordered.getValue() (or the annotation value) has the higher precedence.

### 11.2.5. Introductions
我们可以不侵入性的改变现有的实现，对现有实现类无侵入性的增加方法。

引入（Introductions）（在AspectJ中被称为inter-type声明）使得一个切面可以定义被通知对象实现一个给定的接口，并且可以代表那些对象提供具体实现。
> Introductions (known as inter-type declarations in AspectJ) enable an aspect to declare that advised objects implement a given interface, and to provide an implementation of that interface on behalf of those objects. An introduction is made using the @DeclareParents annotation. This annotation is used to declare that matching types have a new parent (hence the name).

对于Introduction这个词，个人认为理解成引入是最合适的，其目标是对于一个已有的类引入新的接口（有人可能会问：有什么用呢？简单的说，你可以把当前对象转型成另一个对象，那么很显然，你就可以调用另一个对象的方法了）

使用 @DeclareParents注解来定义引入。这个注解被用来定义匹配的类型拥有一个新的父亲。 比如，给定一个接口 UsageTracked，然后接口的具体实现 DefaultUsageTracked 类， 接下来的切面声明了所有的service接口的实现都实现了 UsageTracked 接口。（比如为了通过JMX输出统计信息）。
```java
@Aspect
public class UsageTracking {
  @DeclareParents(value="com.xzy.myapp.service.*+", defaultImpl=DefaultUsageTracked.class)
  public static UsageTracked mixin;

  @Before("com.xyz.myapp.SystemArchitecture.businessService() && this(usageTracked)")
  public void recordUsage(UsageTracked usageTracked) {
    usageTracked.incrementUseCount();
  }
}

```
实现的接口通过被注解的字段类型来决定。@DeclareParents 注解的 value 属性是一个AspectJ的类型模式：- 任何匹配类型的bean都会实现 UsageTracked 接口。 请注意，在上面的前置通知（before advice）的例子中，service beans 可以直接用作 UsageTracked 接口的实现。 如果需要编程式的来访问一个bean，你可以这样写：
```java
UsageTracked usageTracked = (UsageTracked) context.getBean("myService");
```

### 11.2.6. Aspect instantiation models
It is possible to define aspects with alternate lifecycles :- Spring supports AspectJ’s perthis and pertarget instantiation models ( percflow, percflowbelow, and pertypewithin are not currently supported).

```java
@Aspect("perthis(com.xyz.myapp.SystemArchitecture.businessService())")
public class MyAspect {
  private int someState;
  @Before(com.xyz.myapp.SystemArchitecture.businessService())
  public void recordServiceUsage() {
    // ...
  }
}
```
The effect of the 'perthis' clause is that one aspect instance will be created for each unique service object executing a business service (each unique object bound to 'this' at join points matched by the pointcut expression).

这个perthis子句的效果是每个独立的service对象执行时都会创建一个切面实例（切入点表达式所匹配的连接点上的每一个独立的对象都会绑定到'this'上）。 service对象的每个方法在第一次执行的时候创建切面实例。切面在service对象失效的同时失效。 在切面实例被创建前，所有的通知都不会被执行，一旦切面对象创建完成，定义的通知将会在匹配的连接点上执行，但是只有当service对象是和切面关联的才可以。 如果想要知道更多关于per-clauses的信息，请参阅 AspectJ 编程指南。

pertarget：每个切入点表达式匹配的连接点对应的目标对象都会创建一个新的切面实例，使用@Aspect(“pertarget(切入点表达式)”)指定切入点表达式；

singleton：即切面只会有一个实例；

### 11.2.7. Example
因为乐观锁的关系，有时候business services可能会失败（有人甚至在一开始运行事务的时候就失败了）。如果重新尝试一下，很有可能就会成功。 对于business services来说，重试几次是很正常的（Idempotent操作不需要用户参与，否则会得出矛盾的结论） 我们可能需要透明的重试操作以避免让客户看见 OptimisticLockingFailureException 例外被抛出。 很明显，在一个横切多层的情况下，这是非常有必要的，因此通过切面来实现是很理想的。

因为我们想要重试操作，我们会需要使用到环绕通知，这样我们就可以多次调用proceed()方法。下面是简单的切面实现：

```java
@Aspect
public class OptimisticOperationExecutor implements Ordered {

   private static final int DEFAULT_MAX_RETRIES = 2;

   private int maxRetries = DEFAULT_MAX_RETRIES;
   private int order = 1;

   public void setMaxRetries(int maxRetries) {
	  this.maxRetries = maxRetries;
   }

   public int getOrder() {
	  return this.order;
   }

   public void setOrder(int order) {
	  this.order = order;
   }

   @Around("com.xyz.myapp.SystemArchitecture.businessService()")
   public Object doOptimisticOperation(ProceedingJoinPoint pjp) throws Throwable {
	  int numAttempts = 0;
	  OptimisticLockingFailureException lockFailureException;
	  do {
		 numAttempts++;
		 try {
			return pjp.proceed();
		 }
		 catch(OptimisticLockingFailureException ex) {
			lockFailureException = ex;
		 }
	  }
	  while(numAttempts <= this.maxRetries);
	  throw lockFailureException;
   }

}
```
注意切面实现了 Ordered 接口，这样我们就可以把切面的优先级设定为高于事务通知（我们每次重试的时候都想要在一个全新的事务中进行）。 maxRetries 和 order 属性都可以在Spring中配置。 主要的动作在 doOptimisticOperation 这个环绕通知中发生。 请注意这个时候我们所有的 businessService() 方法都会使用这个重试策略。 我们首先会尝试处理，然后如果我们得到一个 OptimisticLockingFailureException 意外，我们只需要简单的重试，直到我们耗尽所有预设的重试次数。
对应的Spring配置如下：
```xml
<aop:aspectj-autoproxy/>

<bean id="optimisticOperationExecutor"
  class="com.xyz.myapp.service.impl.OptimisticOperationExecutor">
	 <property name="maxRetries" value="3"/>
	 <property name="order" value="100"/>
</bean>
```
## 11.3. Schema-based AOP support
### 11.3.2. Declaring a pointcut
When combining pointcut sub-expressions, '&&' is awkward within an XML document, and so the keywords 'and', 'or' and 'not' can be used in place of '&&', '||' and '!' respectively.For example, the previous pointcut may be better written as:
```xml
<aop:config>
  <aop:aspect id="myAspect" ref="aBean">
    <aop:pointcut id="businessService"
      expression="execution(* com.xyz.myapp.service.*.*(..)) **and** this(service)"/>
    <aop:before pointcut-ref="businessService" method="monitor"/>
    ...
  </aop:aspect>
</aop:config
```

## 11.4. Choosing which AOP declaration style to use

### 11.4.2. @AspectJ or XML for Spring AOP?
the XML style is slightly more limited in what it can express than the @AspectJ style: only the "singleton" aspect instantiation model is supported, and it is not possible to combine named pointcuts declared in XML.

## 11.6. Proxying mechanisms
final methods cannot be advised, as they cannot be overridden.

To force the use of CGLIB proxies set the value of the proxy-target-class attribute of the <aop:config> element to true:
```xml
<aop:config proxy-target-class="true">
  <!-- other beans defined here... -->
</aop:config>
```
To force CGLIB proxying when using the @AspectJ autoproxy support, set the 'proxy-targetclass' attribute of the <aop:aspectj-autoproxy> element to true:
```xml
<aop:aspectj-autoproxy proxy-target-class="true"/>
```
> Multiple <aop:config/> sections are collapsed into a single unified auto-proxy creator at runtime, which applies the strongest proxy settings that any of the <aop:config/> sections (typically from different XML bean definition files) specified. This also applies to the <tx:annotation-driven/> and <aop:aspectj-autoproxy/> elements.

>To be clear: using proxy-target-class="true" on <tx:annotation-driven/>, <aop:aspectj-autoproxy/> or <aop:config/> elements will force the use of CGLIB proxies for all three of them.


## 11.7. Programmatic creation of @AspectJ Proxies
using either <aop:config> or <aop:aspectjautoproxy>, it is also possible programmatically to create proxies that advise target objects.

## 11.8. Using AspectJ with Spring applications
Spring为管理容器外创建的对象提供了一个AspectJ语法编写的切面类：org.springframework.beans.factory.aspectj.AnnotationBeanConfigurerAspect，它位于spring-aspects.jar包中。spring-aspects.jar类包没有随Spring标准版一起发布，但你可以在完整版中找到它，位于Spring项目的dist目录下。该切面类匹配所有标注@Configurable的类，该注解类org.springframework.beans.factory.annotation.Configurable则位于spring.jar中。
AspectJ在类加载时，将AnnotationBeanConfigurerAspect切面将织入到标注有@Configurable注解的类中。
AnnotationBeanConfigurerAspect将这些类和Spring IoC容器进行了关联，AnnotationBeanConfigurerAspect本身实现了BeanFactoryAware的接口。
这样，标注了@Configurable的类通过AspectJ LTW织入器织入AnnotationBeanConfigurerAspect切面后，就和Spring IoC容器间接关联起来了，实现了Spring管理容器外对象的功能。

In addition to the @Configurable aspect, spring-aspects.jar contains an AspectJ aspect that can be used to drive Spring’s transaction management for types and methods annotated with the @Transactional annotation.

### 11.8.2. Other Spring aspects for AspectJ
### 11.8.3. Configuring AspectJ aspects using Spring IoC
### 11.8.4. Load-time weaving with AspectJ in the Spring Framework
加载时织入（Load-time weaving（LTW））指的是在虚拟机载入字节码文件时动态织入AspectJ切面。 本 节关注于在Spring Framework中特的定context下配置和使用LTW：并没有LTW的介绍。 关于LTW和仅使用AspectJ配置LTW的详细信息（根本不涉及Spring），请查看 LTW section of the AspectJ Development Environment Guide。
Spring框架的值添加为AspectJ LTW在动态织入过程中提供了更细粒度的控制。使用Java（5+）的代理 能使用一个叫‘Vanilla’的AspectJ LTW，这需要在启动JVM的时候将某个VM参数设置为开。 这种JVM范围的设置在一些情况下或许不错，但通常情况下显得有些粗颗粒。而用Spring的LTW能让你在 per-ClassLoader的基础上打开LTW， 这显然更加细粒度并且对“单JVM多应用”的环境更具意义（例如在一个典型应用服务器环境中一样）。
另外，在某些环境下，这能让你使用LTW而不对应用服务器的启动脚本做任何改动，不然则需要添加 -javaagent:path/to/aspectjweaver.jar或者(以下将会提及的)-javaagent:path/to/spring-agent.jar。 开发人员只需简单修改应用上下文的一个或几个文件就能使用LTW，而不需依靠那些管理着部署配置,比如启动脚本的系统管理员。

#### Environment-specific configuration
##### Tomcat
Historically, Apache Tomcat's default class loader did not support class transformation which is why Spring provides an enhanced implementation that addresses this need. Named
TomcatInstrumentableClassLoader, the loader works on Tomcat 6.0 and above.

> Do not define TomcatInstrumentableClassLoader anymore on Tomcat 8.0 and higher. Instead, let Spring automatically use Tomcat’s new native InstrumentableClassLoader facility through the TomcatLoadTimeWeaver strategy

# 12. Spring AOP APIs
----------
## 12.1. Introduction
## 12.2. Pointcut API in Spring
### 12.2.1. Concepts
> If possible, try to make pointcuts static, allowing the AOP framework to cache the results of pointcut
evaluation when an AOP proxy is created

### 12.2.2. Operations on pointcuts
### 12.2.3. AspectJ expression pointcuts
### 12.2.4. Convenience pointcut implementations
#### Static pointcuts
静态的切入点是基于方法和目标类,而不能考虑方法的参数。可以evaluate一个静态的切入点只有一次,当第一次调用一个方法:在那之后,不需要evaluate切入点与每个方法调用
Static pointcuts are based on method and target class, and cannot take into account the method’s arguments. Static pointcuts are sufficient - and best - for most usages. It’s possible for Spring to evaluate a static pointcut only once, when a method is first invoked: after that, there is no need to evaluate the pointcut again with each method invocation.

##### Regular expression pointcuts
JdkRegexpMethodPointcut来定义正则表达式切点
```xml
<bean id="settersAndAbsquatulatePointcut"
  class="org.springframework.aop.support.JdkRegexpMethodPointcut">
  <property name="patterns">
    <list>
      <value>.*set.*</value>
      <value>.*absquatulate</value>
    </list>
  </property>
</bean>
```
Spring provides a convenience class, RegexpMethodPointcutAdvisor, that allows us to also reference an Advice (remember that an Advice can be an interceptor, before advice, throws advice etc.).
```xml
<bean id="settersAndAbsquatulateAdvisor"
class="org.springframework.aop.support.RegexpMethodPointcutAdvisor">
  <property name="advice">
    <ref bean="beanNameOfAopAllianceInterceptor"/>
  </property>
  <property name="patterns">
    <list>
      <value>.*set.*</value>
      <value>.*absquatulate</value>
    </list>
  </property>
</bean>
```

#### Dynamic pointcuts
### 12.2.5. Pointcut superclasses
### 12.2.6. Custom pointcuts
## 12.3. Advice API in Spring
### 12.3.1. Advice lifecycles
### 12.3.2. Advice types in Spring
#### Interception around advice
#### Before advice
#### Throws advice
#### After Returning advice
#### Introduction advice
引介(Introduction)是指在不更改源代码的情况，给一个现有类增加属性、方法，以及让现有类实现其它接口或指定其它父类等，从而改变类的静态结构。Spring AOP通过采代理加拦截器的方式来实现的，可以通过拦截器机制使一个实有类实现指定的接口。
在实际应用中可以使用DefaultIntroductionAdvisor来配置引介，也可以直接继承DefaultIntroductionAdvisor来实现引介。

## 12.4. Advisor API in Spring
It is possible to mix advisor and advice types in Spring in the same AOP proxy. For example, you could use a interception around advice, throws advice and before advice in one proxy configuration: Spring will automatically create the necessary interceptor chain

## 12.5. Using the ProxyFactoryBean to create AOP proxies
The basic way to create an AOP proxy in Spring is to use the **org.springframework.aop.framework.ProxyFactoryBean**. This gives complete control over the pointcuts
and advice that will apply, and their ordering. However, there are simpler options that are preferable if you don’t need such control.

### 12.5.1. Basics
One of the most important benefits of using a ProxyFactoryBean or another IoC-aware class to create AOP proxies, is that it means that advices and pointcuts can also be managed by IoC.

### 12.5.2. JavaBean properties
In common with most FactoryBean implementations provided with Spring, the ProxyFactoryBean class is itself a JavaBean. Its properties are used to:

• Specify the target you want to proxy.
• Specify whether to use CGLIB (see below and also the section called “JDK- and CGLIB-based
proxies”).

Some key properties are inherited from org.springframework.aop.framework.ProxyConfig (the superclass for all AOP proxy factories in Spring). These key properties include:

• proxyTargetClass: true if the target class is to be proxied, rather than the target class' interfaces. If this property value is set to true, then CGLIB proxies will be created (but see also the section called “JDK- and CGLIB-based proxies”).
• optimize: controls whether or not aggressive optimizations are applied to proxies created via CGLIB. One should not blithely use this setting unless one fully understands how the relevant AOP proxy handles optimization. This is currently used only for CGLIB proxies; it has no effect with JDK dynamic proxies.
• frozen: if a proxy configuration is frozen, then changes to the configuration are no longer allowed. This is useful both as a slight optimization and for those cases when you don’t want callers to be able to manipulate the proxy (via the Advised interface) after the proxy has been created. The default
value of this property is false, so changes such as adding additional advice are allowed.
• exposeProxy: determines whether or not the current proxy should be exposed in a ThreadLocal so that it can be accessed by the target. If a target needs to obtain the proxy and the exposeProxy property is set to true, the target can use the AopContext.currentProxy() method.

Other properties specific to ProxyFactoryBean include:

• proxyInterfaces: array of String interface names. If this isn’t supplied, a CGLIB proxy for the target class will be used (but see also the section called “JDK- and CGLIB-based proxies”).
• interceptorNames: String array of Advisor, interceptor or other advice names to apply. Ordering is significant, on a first come-first served basis. That is to say that the first interceptor in the list will be the first to be able to intercept the invocation

### 12.5.3. JDK- and CGLIB-based proxies
If the class of a target object that is to be proxied (hereafter simply referred to as the target class) doesn’t implement any interfaces, then a CGLIB-based proxy will be created. This is the easiest scenario, because JDK proxies are interface based, and no interfaces means JDK proxying isn’t even possible. One simply plugs in the target bean, and specifies the list of interceptors via the interceptorNames property. Note that a CGLIB-based proxy will be created even if the proxyTargetClass property of the ProxyFactoryBean has been set to false. (Obviously this makes no sense, and is best removed from the bean definition because it is at best redundant, and at worst confusing.)

If the proxyTargetClass property of the ProxyFactoryBean has been set to true, then a CGLIBbased proxy will be created.

If the proxyInterfaces property of the ProxyFactoryBean has been set to one or more fully qualified interface names, then a JDK-based proxy will be created.

If the proxyInterfaces property of the ProxyFactoryBean has not been set, but the target class does implement one (or more) interfaces, then the ProxyFactoryBean will auto-detect the fact that the target class does actually implement at least one interface, and a JDK-based proxy will be created.

### 12.5.4. Proxying interfaces
### 12.5.5. Proxying classes
By appending an asterisk to an interceptor name, all advisors with bean names matching the part before the asterisk, will be added to the advisor chain.
```xml
<bean id="proxy" class="org.springframework.aop.framework.ProxyFactoryBean">
  <property name="target" ref="service"/>
  <property name="interceptorNames">
    <list>
    <value>global*</value>
    </list>
  </property>
</bean>
<bean id="global_debug" class="org.springframework.aop.interceptor.DebugInterceptor"/>
<bean id="global_performance" class="org.springframework.aop.interceptor.PerformanceMonitorInterceptor"/>
```
### 12.6. Concise proxy definitions
### 12.5.6. Using 'global' advisors
### 12.7. Creating AOP proxies programmatically with the ProxyFactory
The following listing shows creation of a proxy for a target object, with one interceptor and one advisor.
The interfaces implemented by the target object will automatically be proxied:
```java
ProxyFactory factory = new ProxyFactory(myBusinessInterfaceImpl);
factory.addAdvice(myMethodInterceptor);
factory.addAdvisor(myAdvisor);
MyBusinessInterface tb = (MyBusinessInterface) factory.getProxy();
```
### 12.8. Manipulating advised objects
The addAdvisor() methods can be used to add any Advisor. Usually the advisor holding pointcut and advice will be the generic DefaultPointcutAdvisor, which can be used with any advice or pointcut (but not for introductions).

A simple example of casting an AOP proxy to the Advised interface and examining and manipulating its advice:
```java
Advised advised = (Advised) myObject;
Advisor[] advisors = advised.getAdvisors();
int oldAdvisorCount = advisors.length;
System.out.println(oldAdvisorCount + " advisors");
// Add an advice like an interceptor without a pointcut
// Will match all proxied methods
// Can use for interceptors, before, after returning or throws advice
advised.addAdvice(new DebugInterceptor());
// Add selective advice using a pointcut
advised.addAdvisor(new DefaultPointcutAdvisor(mySpecialPointcut, myAdvice));
assertEquals("Added two advisors", oldAdvisorCount + 2, advised.getAdvisors().length);
```

## 12.9. Using the "auto-proxy" facility
Spring also allows us to use "auto-proxy" bean definitions, which can automatically proxy selected bean definitions. This is built on Spring "bean post processor" infrastructure, which enables modification of any bean definition as the container loads.
### 12.9.1. Autoproxy bean definitions
#### BeanNameAutoProxyCreator
Spring also allows us to use "auto-proxy" bean definitions, which can automatically proxy selected bean definitions. This is built on Spring "bean post processor" infrastructure, which enables modification of any bean definition as the container loads.
```xml
<bean class="org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator">
  <property name="beanNames" value="jdk*,onlyJdk"/>
  <property name="interceptorNames">
    <list>
      <value>myInterceptor</value>
    </list>
  </property>
</bean>
```
#### DefaultAdvisorAutoProxyCreator
#### AbstractAdvisorAutoProxyCreator
### 12.9.2. Using metadata-driven auto-proxying
## 12.10. Using TargetSources
TargetSource（目标源）是被代理的target（目标对象）实例的来源。TargetSource被用于获取当前MethodInvocation（方法调用）所需要的target（目标对象），这个target通过反射的方式被调用（如：method.invode(target,args)）。换句话说，proxy（代理对象）代理的不是target，而是TargetSource，这点非常重要！！！

通常情况下，一个proxy（代理对象）只能代理一个target，每次方法调用的目标也是唯一固定的target。但是，如果让proxy代理TargetSource，可以使得每次方法调用的target实例都不同（当然也可以相同，这取决于TargetSource实现）。这种机制使得方法调用变得灵活，可以扩展出很多高级功能，如：target pool（目标对象池）、hot swap（运行时目标对象热替换），等等

Spring offers the concept of a TargetSource, expressed in the org.springframework.aop.TargetSource interface. This interface is responsible for returning the "target object" implementing the join point. The TargetSource implementation is asked for a target instance each time the AOP proxy handles a method invocation.

Developers using Spring AOP don’t normally need to work directly with TargetSources, but this provides a powerful means of supporting pooling, hot swappable and other sophisticated targets. For example, a pooling TargetSource can return a different target instance for each invocation, using a pool to manage instances.

If you do not specify a TargetSource, a default implementation is used that wraps a local object. The same target is returned for each invocation (as you would expect)

>When using a custom target source, your target will usually need to be a prototype rather than a
singleton bean definition. This allows Spring to create a new target instance when required.

接下来要说的另外一点，可能会颠覆你的既有认知：TargetSource组件本身与SpringIoC容器无关，换句话说，target的生命周期不一定是受spring容器管理的，我们以往的XML中的AOP配置，只是对受容器管理的bean而言的，我们当然可以手动创建一个target，同时使用Spring的AOP框架（而不使用IoC容器）

### 12.10.1. Hot swappable target sources
### 12.10.2. Pooling target sources
### 12.10.3. Prototype target sources
Setting up a "prototype" target source is similar to a pooling TargetSource. In this case, a new instance of the target will be created on every method invocation.
Although the cost of creating a new object isn’t high in a modern JVM, the cost of wiring up the new object (satisfying its IoC dependencies) may be more expensive. Thus you shouldn’t use this approach without very good reason

```xml
<bean id="prototypeTargetSource" class="org.springframework.aop.target.PrototypeTargetSource">
  <property name="targetBeanName" ref="businessObjectTarget"/>
</bean>
```
### 12.10.4. ThreadLocal target sources
ThreadLocal target sources are useful if you need an object to be created for each incoming request(per thread that is).
```xml
<bean id="threadlocalTargetSource" class="org.springframework.aop.target.ThreadLocalTargetSource">
  <property name="targetBeanName" value="businessObjectTarget"/>
</bean>
```
ThreadLocals come with serious issues (**potentially resulting in memory leaks**) when incorrectly using them in a multi-threaded and multi-classloader environments. One should always consider wrapping a threadlocal in some other class and never directly use the ThreadLocal itself (except of course in the wrapper class). Also, one should always remember to correctly set and unset (where the latter simply involved a call to **ThreadLocal.set(null)**) the resource local to the thread.
## 12.11. Defining new Advice types
## 12.12. Further resources
