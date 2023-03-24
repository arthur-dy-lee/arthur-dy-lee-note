# FactoryBean、BeanFactory与ApplicationContext区别

## 一、 FactotyBean

-----

### 1.1 FactoryBean 定义/作用

> The FactoryBean interface is a point of pluggability into the Spring IoC container’s instantiation logic. If you have complex initialization code that is better expressed in Java as opposed to a (potentially) verbose amount of XML, you can create your own FactoryBean, write the complex initialization inside that class, and then plug your custom FactoryBean into the container. 

> FactoryBean接口是Spring IOC容器的实例化逻辑的可插拔点。如果有复杂的bean初始化，相对于冗长的xml方式，期望通过java编码的方式来表达，就可以通过创建自定义的FactoryBean来实现并将FactoryBean插入到IOC容器中。 

一般情况下Spring通过bean中的class属性，通过反射创建Bean的实例。但在某些情况下，实例化Bean的过程比较复杂，如果按照传统的方式，则需要在bean标签中提供大量的配置信息，配置方式的灵活性是受限的，这时采用编码的方式可能会得到一个简单的方案。Spring为此提供了一个org.springframework.beans.factory.FactoryBean的接口。用户可以实例化该接口，实现定制化bean实例创建逻辑。 

从Spring3.0开始，FactoryBean开始支持泛型。

```java
public interface FactoryBean<T> {
    // 返回factoryBean创建的实例，如果isSingleton为true，则该实例会放到Spring容器中
    // 单实例缓存池中
    T getObject() throws Exception;
    // 返回FactoryBean创建的bean的类型
    Class<?> getObjectType();
    // 返回FactoryBean创建的是作用域是singleton还是prototype。
    boolean isSingleton();
}
```

以Bean结尾，表示它是一个Bean，不同于普通Bean的是：它是实现了`FactoryBean<T>`接口的Bean，根据该Bean的ID从`BeanFactory`中获取的实际上是`FactoryBean`的`getObject()`返回的对象，而不是`FactoryBean`本身，如果要获取`FactoryBean`对象，请在id前面加一个&符号来获取。

### 1.2 FactoryBean的实现类

FactoryBean接口对应Spring框架来说占有重要的地位，Spring本身就提供了70多个FactoryBean的实现。在Spring中，通过FactoryBean来扩展的遍地都是：AOP,ORM,事务管理，JMX,Remoting，Freemarker,Velocity等等。他们隐藏了实例化一些复杂的细节，给上层应用带来了便利。

```xml
MBeanProxyFactoryBean (org.springframework.jmx.access)
JobDetailFactoryBean (org.springframework.scheduling.quartz)
LocalStatelessSessionProxyFactoryBean (org.springframework.ejb.access)
PropertyPathFactoryBean (org.springframework.beans.factory.config)
HibernateJpaSessionFactoryBean (org.springframework.orm.jpa.vendor)
ServletServerContainerFactoryBean (org.springframework.web.socket.server.standard)
TimerManagerFactoryBean (org.springframework.scheduling.commonj)
ContentNegotiationManagerFactoryBean (org.springframework.web.accept)
HessianProxyFactoryBean (org.springframework.remoting.caucho)
MBeanServerConnectionFactoryBean (org.springframework.jmx.support)
ServiceLocatorFactoryBean (org.springframework.beans.factory.config)
ScheduledExecutorFactoryBean (org.springframework.scheduling.concurrent)
RmiProxyFactoryBean (org.springframework.remoting.rmi)
AbstractSingletonProxyFactoryBean (org.springframework.aop.framework)
    TransactionProxyFactoryBean (org.springframework.transaction.interceptor)
    CacheProxyFactoryBean (org.springframework.cache.interceptor)
CompositeUriComponentsContributorFactoryBean in AnnotationDrivenBeanDefinitionParser (org.springframework.web.servlet.config)
EmbeddedDatabaseFactoryBean (org.springframework.jdbc.datasource.embedded)
WebSphereMBeanServerFactoryBean (org.springframework.jmx.support)
JndiRmiProxyFactoryBean (org.springframework.remoting.rmi)
DateTimeFormatterFactoryBean (org.springframework.format.datetime.standard)
MBeanServerFactoryBean (org.springframework.jmx.support)
JaxWsPortProxyFactoryBean (org.springframework.remoting.jaxws)
WebSocketContainerFactoryBean (org.springframework.web.socket.client.standard)
ServletContextParameterFactoryBean (org.springframework.web.context.support)
ThreadPoolExecutorFactoryBean (org.springframework.scheduling.concurrent)
AbstractFactoryBean (org.springframework.beans.factory.config)
    SortedResourcesFactoryBean (org.springframework.jdbc.config)
    MapFactoryBean (org.springframework.beans.factory.config)
    ListFactoryBean (org.springframework.beans.factory.config)
    SetFactoryBean (org.springframework.beans.factory.config)
    ObjectFactoryCreatingFactoryBean (org.springframework.beans.factory.config)
    ProviderCreatingFactoryBean (org.springframework.beans.factory.config)
    AbstractServiceLoaderBasedFactoryBean (org.springframework.beans.factory.serviceloader)
        ServiceListFactoryBean (org.springframework.beans.factory.serviceloader)
        ServiceLoaderFactoryBean (org.springframework.beans.factory.serviceloader)
        ServiceFactoryBean (org.springframework.beans.factory.serviceloader)
SimpleRemoteStatelessSessionProxyFactoryBean (org.springframework.ejb.access)
LocalConnectionFactoryBean (org.springframework.jca.support)
JtaTransactionManagerFactoryBean (org.springframework.transaction.config)
ProxyFactoryBean (org.springframework.aop.framework)
YamlMapFactoryBean (org.springframework.beans.factory.config)
FreeMarkerConfigurationFactoryBean (org.springframework.ui.freemarker)
MethodInvokingJobDetailFactoryBean (org.springframework.scheduling.quartz)
ResourceAdapterFactoryBean (org.springframework.jca.support)
MethodInvokingFactoryBean (org.springframework.beans.factory.config)
YamlPropertiesFactoryBean (org.springframework.beans.factory.config)
Jackson2ObjectMapperFactoryBean (org.springframework.http.converter.json)
EhCacheManagerFactoryBean (org.springframework.cache.ehcache)
FormattingConversionServiceFactoryBean (org.springframework.format.support)
SmartFactoryBean (org.springframework.beans.factory)
TaskExecutorFactoryBean (org.springframework.scheduling.config)
JmsInvokerProxyFactoryBean (org.springframework.jms.remoting)
EhCacheFactoryBean (org.springframework.cache.ehcache)
LocalJaxWsServiceFactoryBean (org.springframework.remoting.jaxws)
JCacheManagerFactoryBean (org.springframework.cache.jcache)
MethodLocatingFactoryBean (org.springframework.aop.config)
SchedulerFactoryBean (org.springframework.scheduling.quartz)
CronTriggerFactoryBean (org.springframework.scheduling.quartz)
ConnectorServerFactoryBean (org.springframework.jmx.support)
SimpleTriggerFactoryBean (org.springframework.scheduling.quartz)
AbstractEntityManagerFactoryBean (org.springframework.orm.jpa)
    LocalEntityManagerFactoryBean (org.springframework.orm.jpa)
    LocalContainerEntityManagerFactoryBean (org.springframework.orm.jpa)
ScopedProxyFactoryBean (org.springframework.aop.scope)
ProxyFactoryBean (org.springframework.http.client.support)
GsonFactoryBean (org.springframework.http.converter.json)
HttpInvokerProxyFactoryBean (org.springframework.remoting.httpinvoker)
ConcurrentMapCacheFactoryBean (org.springframework.cache.concurrent)
SimpleHttpServerFactoryBean (org.springframework.remoting.support)
DateTimeFormatterFactoryBean (org.springframework.format.datetime.joda)
RmiRegistryFactoryBean (org.springframework.remoting.rmi)
PropertiesFactoryBean (org.springframework.beans.factory.config)
ForkJoinPoolFactoryBean (org.springframework.scheduling.concurrent)
ServletContextAttributeFactoryBean (org.springframework.web.context.support)
FieldRetrievingFactoryBean (org.springframework.beans.factory.config)
SharedEntityManagerBean (org.springframework.orm.jpa.support)
JndiObjectFactoryBean (org.springframework.jndi)
LocalSessionFactoryBean (org.springframework.orm.hibernate5)
DecoratingFactoryBean in MessageBrokerBeanDefinitionParser (org.springframework.web.socket.config)
ConversionServiceFactoryBean (org.springframework.context.support)
```

### 1.3 FactoryBean入口调用类AbstractBeanFactory#doGetBean 方法中调用getObjectForBeanInstance 

```java
protected Object getObjectForBeanInstance(
      Object beanInstance, String name, String beanName, @Nullable RootBeanDefinition mbd) {

   // Don't let calling code try to dereference the factory if the bean isn't a factory.
   //判断如果请求一个&前缀的beanName
   //BeanFactoryUtils.isFactoryDereference(name)方法判断name是否以&前缀 
   if (BeanFactoryUtils.isFactoryDereference(name)) {
      if (beanInstance instanceof NullBean) {
         return beanInstance;
      }
      // 判断如果请求一个&前缀的beanName，而实例化的对象不是FactoryBean的子类，则抛出BeanIsNotAFactoryException异常 
      if (!(beanInstance instanceof FactoryBean)) {
         throw new BeanIsNotAFactoryException(transformedBeanName(name), beanInstance.getClass());
      }
   }

   // Now we have the bean instance, which may be a normal bean or a FactoryBean.
   // If it's a FactoryBean, we use it to create a bean instance, unless the
   // caller actually wants a reference to the factory.
   //如果bean实例对象不是FactoryBean的子类，或者请求的beanName以&前缀，则直接返回bean实例对象 
   if (!(beanInstance instanceof FactoryBean) || BeanFactoryUtils.isFactoryDereference(name)) {
      return beanInstance;
   }

   Object object = null;
   //mbd==null说明FactoryBean实例对象是单例，且从单例缓存中取出，则从缓存中查询FactoryBean创建的bean实例对象 
   if (mbd == null) {
      object = getCachedObjectForFactoryBean(beanName);
   }
   if (object == null) {
      // Return bean instance from factory.
      // 强制转换beanInstance为FactoryBean 
      FactoryBean<?> factory = (FactoryBean<?>) beanInstance;
      // Caches object obtained from FactoryBean if it is a singleton.
      // 缓存不存在且mbd==null，则根据beanName获得RootBeanDefinition 
      if (mbd == null && containsBeanDefinition(beanName)) {
         mbd = getMergedLocalBeanDefinition(beanName);
      }
      // bean是否为合成的，合成bean在获得FactoryBean创建好的bean对象实例后，不需要后置处理 
      boolean synthetic = (mbd != null && mbd.isSynthetic());
      // FactoryBean创建bean实例对象
      //----------> 重点看一下怎么取object <---------------
      object = getObjectFromFactoryBean(factory, beanName, !synthetic);
   }
   return object;
}
```

从factoryBean中获取bean实例

getObjectFromFactoryBean是实际操作的入口 

```java
protected Object getObjectFromFactoryBean(FactoryBean<?> factory, String beanName, boolean shouldPostProcess) {
   // FactoryBean是单例，且已存在单例对象 
   if (factory.isSingleton() && containsSingleton(beanName)) {
      synchronized (getSingletonMutex()) {
         Object object = this.factoryBeanObjectCache.get(beanName);
         if (object == null) {
            object = doGetObjectFromFactoryBean(factory, beanName);
            // Only post-process and store if not put there already during getObject() call above
            // (e.g. because of circular reference processing triggered by custom getBean calls)
            Object alreadyThere = this.factoryBeanObjectCache.get(beanName);
            if (alreadyThere != null) {
               object = alreadyThere;
            }
            else {
               if (shouldPostProcess) {
                  if (isSingletonCurrentlyInCreation(beanName)) {
                     // Temporarily return non-post-processed object, not storing it yet..
                     return object;
                  }
                  beforeSingletonCreation(beanName);
                  try {
                     // 调用FactoryBean后置处理 
                     object = postProcessObjectFromFactoryBean(object, beanName);
                  }//....
                  finally {
                     afterSingletonCreation(beanName);
                  }
               }
               if (containsSingleton(beanName)) {
                  // 加入缓存 
                  this.factoryBeanObjectCache.put(beanName, object);
               }
            }
         }
         return object;
      }
   }
   // FactoryBean为多例，直接调用getObject方法获取bean实例对象 
   else {
      Object object = doGetObjectFromFactoryBean(factory, beanName);
      if (shouldPostProcess) {
         try {
            // FactoryBean后置处理 
            object = postProcessObjectFromFactoryBean(object, beanName);
         }
         catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Post-processing of FactoryBean's object failed", ex);
         }
      }
      return object;
   }
}
```



doGetObjectFromFactoryBean方法为实际获取FactoryBean创建的bean实例对象的触发点，核心方法就是调用FactoryBean的getObject方法。在方法中，FactoryBean.getObject()方法被调用。

```java
/**
 * Obtain an object to expose from the given FactoryBean.
 */
private Object doGetObjectFromFactoryBean(final FactoryBean<?> factory, final String beanName)
    throws BeanCreationException {

    Object object;
    try {
        if (System.getSecurityManager() != null) {
            AccessControlContext acc = getAccessControlContext();
            try {
                object = AccessController.doPrivileged((PrivilegedExceptionAction<Object>) factory::getObject, acc);
            }
            catch (PrivilegedActionException pae) {
                throw pae.getException();
            }
        }
        else {
            // 调用FactoryBean.getObject()方法 <------------------
            object = factory.getObject();
        }
    }
    catch (FactoryBeanNotInitializedException ex) {
        throw new BeanCurrentlyInCreationException(beanName, ex.toString());
    }
    catch (Throwable ex) {
        throw new BeanCreationException(beanName, "FactoryBean threw exception on object creation", ex);
    }

    // Do not accept a null value for a FactoryBean that's not fully
    // initialized yet: Many FactoryBeans just return null then.
    if (object == null) {
        if (isSingletonCurrentlyInCreation(beanName)) {
            throw new BeanCurrentlyInCreationException(
                beanName, "FactoryBean which is currently in creation returned null from getObject");
        }
        object = new NullBean();
    }
    return object;
}
```

对于单例的FactoryBean，生产出的bean对象实例也是单例的并有缓存，而多例的也是遵循每请求一次就创建一个新对象。 

### 1.4 扩展

在看了许多spring的FactoryBean源码后，有许多都是实现了`InitializingBean`接口，在实现中，使用`afterPropertiesSet()`方法，进行一些初始化动作。

### 1.5  实现自定义的FactoryBean

实现自定义的FactoryBean，有2种方法：

1、直接实现FactoryBean 接口，然后实现它的接口方法，如果需要在getObject()前，需要进行一些实例化，则可以实现`InitializingBean`接口或者使用`@PostConstruct`注解。

2、继承`AbstractFactoryBean`类，`AbstractFactoryBean`是一个模板类，	它本身实现了以下的接口

```java
public abstract class AbstractFactoryBean<T>
		implements FactoryBean<T>, BeanClassLoaderAware, BeanFactoryAware, InitializingBean, DisposableBean{}
```

继承`AbstractFactoryBean`后，只需要实现2个方法即可: 返回实例类的class类型，`getObjectType()`，创建类实例`createInstance()`。默认是单例，如果想要返回原型实例，则可以在构造方法中``setSingleton(``false``);` `即使可。

更多例子可以查看下面的链接：

[How to use the Spring FactoryBean?](https://www.baeldung.com/spring-factorybean)



### 1.5 工厂模式和模板模式

FactoryBean是工厂的接口，接口中的getObject方法返回的产品默认是一个Object。PropertiesFactoryBean是工厂的一个实现，生产出来的是Object的子类Properties。对于不同的工厂实现，生产出来的产品也是不同的。比如JndiObjectFactoryBean返回的是JNDI对象，RmiProxyFactoryBean返回的是RMI对象。

工厂方法模式，在编写代码时并不清楚要创建的对象是什么，因而只定义接口及通用方法，把具体的实现交给子类来处理，因为不同的子类所创建的对象并不一致。

`FactoryBean`是工厂模式。

`AbstractFactoryBean`用的是模板模式。

## 二、 beanFactory

-----

`BeanFactory`，以Factory结尾，表示它是一个工厂类(接口)，用于管理Bean的一个工厂。在Spring中，`BeanFactory`是IOC容器的核心接口，它的职责包括：实例化、定位、配置应用程序中的对象及建立这些对象间的依赖。 

`BeanFactory` 是 Spring 的“心脏”。它就是 Spring IoC 容器的真面目。Spring 使用 `BeanFactory` 来实例化、配置和管理 Bean。

`BeanFactory`：是IOC容器的核心接口， 它定义了IOC的基本功能，我们看到它主要定义了getBean方法。getBean方法是IOC容器获取bean对象和引发依赖注入的起点。方法的功能是返回特定的名称的Bean。

`BeanFactory` 是初始化 Bean 和调用它们生命周期方法的“吃苦耐劳者”。注意，`BeanFactory` 只能管理单例（Singleton）Bean 的生命周期。它不能管理原型(prototype,非单例)Bean 的生命周期。这是因为原型 Bean 实例被创建之后便被传给了客户端,容器失去了对它们的引用。

```java
Resource resource = new FileSystemResource("beans.xml");
BeanFactory factory = new XmlBeanFactory(resource);
```

```java
ClassPathResource resource = new ClassPathResource("beans.xml");
BeanFactory factory = new XmlBeanFactory(resource);
```

```java
ApplicationContext context = new ClassPathXmlApplicationContext(new String[{"applicationContext.xml", "applicationContext-part2.xml"});
BeanFactory factory = (BeanFactory) context;
```

1. XmlBeanFactory通过Resource装载Spring配置信息冰启动IoC容器，然后就可以通过factory.getBean从IoC容器中获取Bean了。
2. 通过`BeanFactory`启动IoC容器时，并不会初始化配置文件中定义的Bean，初始化动作发生在第一个调用时。 
3. 对于单实例（singleton）的Bean来说，`BeanFactory`会缓存Bean实例，所以第二次使用getBean时直接从IoC容器缓存中获取Bean。 

## 三、 ApplicationContext

如果说`BeanFactory`是Spring的心脏，那么ApplicationContext就是完整的躯体了，ApplicationContext由`BeanFactory`派生而来，提供了更多面向实际应用的功能。在`BeanFactory`中，很多功能需要以编程的方式实现，而在ApplicationContext中则可以通过配置实现。

BeanFactorty接口提供了配置框架及基本功能，但是无法支持spring的aop功能和web应用。而ApplicationContext接口作为`BeanFactory`的派生，因而提供`BeanFactory`所有的功能。而且ApplicationContext还在功能上做了扩展，相较于BeanFactorty，ApplicationContext还提供了以下的功能： 

（1）MessageSource, 提供国际化的消息访问  
（2）资源访问，如URL和文件  
（3）事件传播特性，即支持aop特性
（4）载入多个（有继承关系）上下文 ，使得每一个上下文都专注于一个特定的层次，比如应用的web层 

ApplicationContext：是IOC容器另一个重要接口， 它继承了`BeanFactory`的基本功能， 同时也继承了容器的高级功能，如：MessageSource（国际化资源接口）、ResourceLoader（资源加载接口）、ApplicationEventPublisher（应用事件发布接口）等。

### 3.1 beanFactory与ApplicationContext二者区别

1 BeanFactroy采用的是延迟加载形式来注入Bean的，即只有在使用到某个Bean时(调用getBean())，才对该Bean进行加载实例化，这样，我们就不能发现一些存在的Spring的配置问题。而ApplicationContext则相反，它是在容器启动时，一次性创建了所有的Bean。这样，在容器启动时，我们就可以发现Spring中存在的配置错误。 相对于基本的BeanFactory，ApplicationContext 唯一的不足是占用内存空间。当应用程序配置Bean较多时，程序启动较慢。

BeanFacotry延迟加载,如果Bean的某一个属性没有注入，BeanFacotry加载后，直至第一次使用调用getBean方法才会抛出异常；而ApplicationContext则在初始化自身是检验，这样有利于检查所依赖属性是否注入；所以通常情况下我们选择使用 ApplicationContext。
应用上下文则会在上下文启动后预载入所有的单实例Bean。通过预载入单实例bean ,确保当你需要的时候，你就不用等待，因为它们已经创建好了。

2 `BeanFactory`和ApplicationContext都支持BeanPostProcessor、BeanFactoryPostProcessor的使用，但两者之间的区别是：`BeanFactory`需要手动注册，而ApplicationContext则是自动注册。（Applicationcontext比 beanFactory 加入了一些更好使用的功能。而且 beanFactory 的许多功能需要通过编程实现而 Applicationcontext 可以通过配置实现。比如后处理 bean ， Applicationcontext 直接配置在配置文件即可而 beanFactory 这要在代码中显示的写出来才可以被容器识别。 ）

3.beanFactory主要是面对与 spring 框架的基础设施，面对 spring 自己。而 Applicationcontex 主要面对与 spring 使用的开发者。基本都会使用 Applicationcontext 并非 beanFactory 。

### 3.2 总结

作用：

1. `BeanFactory`负责读取bean配置文档，管理bean的加载，实例化，维护bean之间的依赖关系，负责bean的声明周期。

2. ApplicationContext除了提供上述BeanFactory所能提供的功能之外，还提供了更完整的框架功能：

   a. 国际化支持

   b. 资源访问：Resource rs = ctx. getResource(“classpath:config.properties”), “file:c:/config.properties”

   c. 事件传递：通过实现ApplicationContextAware接口

3. 常用的获取ApplicationContext

FileSystemXmlApplicationContext：从文件系统或者url指定的xml配置文件创建，参数为配置文件名或文件名数组，有相对路径与绝对路径。

```java
ApplicationContext factory=new FileSystemXmlApplicationContext("src/applicationContext.xml");
ApplicationContext factory=new FileSystemXmlApplicationContext("E:/Workspaces/MyEclipse 8.5/Hello/src/applicationContext.xml");
```

ClassPathXmlApplicationContext：从classpath的xml配置文件创建，可以从jar包中读取配置文件。ClassPathXmlApplicationContext 编译路径总有三种方式： 

```java
ApplicationContext factory = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
ApplicationContext factory = new ClassPathXmlApplicationContext("applicationContext.xml"); 
ApplicationContext factory = new ClassPathXmlApplicationContext("file:E:/Workspaces/MyEclipse 8.5/Hello/src/applicationContext.xml");
```

XmlWebApplicationContext：从web应用的根目录读取配置文件，需要先在web.xml中配置，可以配置监听器或者servlet来实现 

```java
<listener>
<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
```

或 

```java
<servlet>
<servlet-name>context</servlet-name>
<servlet-class>org.springframework.web.context.ContextLoaderServlet</servlet-class>
<load-on-startup>1</load-on-startup>
</servlet>
```

这两种方式都默认配置文件为web-inf/applicationContext.xml，也可使用context-param指定配置文件 

```java
<context-param>
<param-name>contextConfigLocation</param-name>
<param-value>/WEB-INF/myApplicationContext.xml</param-value>
</context-param>
```





## 参考

[How to use the Spring FactoryBean?](https://www.baeldung.com/spring-factorybean)

