
# Spring-Aware接口说明以及应用
-------

## 一、引言
Spring中提供一些Aware结尾相关接口，像是BeanFactoryAware、 BeanNameAware、ApplicationContextAware、ResourceLoaderAware、ServletContextAware等等。

实现这些 Aware接口的Bean在被实例化之后，可以取得一些相对应的资源，例如实现BeanFactoryAware的Bean在实例化后，Spring容器将会注入BeanFactory的实例，而实现ApplicationContextAware的Bean，在Bean被实例化后，将会被注入 ApplicationContext的实例等等。



## 二、Aware相关接口说明 

**1 BeanNameAware接口**

如果某个bean需要访问配置文件中本身bean的id属性，这个bean类通过实现BeanNameAware接口，在依赖关系确定之后，初始化方法之前，提供回调自身的能力，从而获得本身bean的id属性，该接口提供了 void setBeanName(String name)方法，需要指出的时该方法的name参数就是该bean的id属性。回调该setBeanName方法可以让bean获取自身的id属性

**2.BeanFactoryAware接口**

>  官方解释：实现这个接口的bean其实是希望知道自己属于哪一个beanfactory

实现了BeanFactoryAware接口的bean，可以直接通过beanfactory来访问spring的容器，当该bean被容器创建之后，会有一个相应的beanfactory的实例引用。该 接口有一个方法void setBeanFactory(BeanFactory beanFactory)方法通过这个方法的参数创建它的BeanFactory实例，实现了BeanFactoryAware接口，就可以让Bean拥有访问Spring容器的能力。

**3 ApplicationContextAware接口**

在Bean类被初始化后，将会被注入applicationContext实例，该接口有一个方法，setApplicationContext(ApplicationContext context),使用其参数context用来创建它的applicationContext实例



## 三、接口说明
标记Aware超级接口，意为在spring容器中，一个bean有资格被一个特定框架的对象通过回调的方式被通知。实际的方法签名是由单独的子接口决定的，但是通常只包含一个返回的方法，该方法可以用一个参数来实现。

[有网友认为](http://blog.csdn.net/peerless_hero/article/details/53243172)

>空接口的意义就是给实现该接口的所有类打上一个标识，利用这个标识就可以对这些类做统一的处理。

我觉得基本上没错，spring对于实现了BeanFactoryAware、 BeanNameAware接口的实现类，在容器启动时，利用反射将beanFactory、applicationContext赋值给了实现类，以便给实现类一个机会去得到beanFactory、applicationContext的实例。

```java
package org.springframework.beans.factory;

/**
 * Marker superinterface indicating that a bean is eligible to be
 * notified by the Spring container of a particular framework object
 * through a callback-style method. Actual method signature is
 * determined by individual subinterfaces, but should typically
 * consist of just one void-returning method that accepts a single
 * argument.
 *
 * <p>Note that merely implementing {@link Aware} provides no default
 * functionality. Rather, processing must be done explicitly, for example
 * in a {@link org.springframework.beans.factory.config.BeanPostProcessor BeanPostProcessor}.
 * Refer to {@link org.springframework.context.support.ApplicationContextAwareProcessor}
 * and {@link org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory}
 * for examples of processing {@code *Aware} interface callbacks.
 *
 * @author Chris Beams
 * @since 3.1
 */
public interface Aware {

}
```

## 四、应用
在使用spring编程时，常常会遇到想根据bean的名称来获取相应的bean对象，这时候，就可以通过实现BeanFactoryAware来满足需求，代码很简单：

```java
@Service
public class BeanFactoryHelper implements BeanFactoryAware {    
    private static BeanFactory beanFactory;
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public static Object getBean(String beanName){
　　　　 if(beanFactory == null){
            throw new NullPointerException("BeanFactory is null!");
        }
　　　　 return beanFactory.getBean(beanName);
　　}
}
```
还有一种方式是实现ApplicationContextAware接口，代码也很简单：

```java
@Service
public class ApplicationContextHelper implements ApplicationContextAware {    
    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }    
    public static Object getBean(String beanName){
        if(applicationContext == null){
            throw new NullPointerException("ApplicationContext is null!");
        }
        return applicationContext.getBean(beanName);
    }
}
```
上面两种方法，<font color=red size=4>只有容器启动的时候，才会把BeanFactory和ApplicationContext注入到自定义的helper类中</font>，如果在本地junit测试的时候，如果需要根据bean的名称获取bean对象，则可以通过ClassPathXmlApplicationContext来获取一个ApplicationContext，代码如下：

## 五、最后附上BeanFactoryAware和ApplicationContextAware源码


```java
package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

/**
 * Interface to be implemented by beans that wish to be aware of their
 * owning {@link BeanFactory}.
 *
 * <p>For example, beans can look up collaborating beans via the factory
 * (Dependency Lookup). Note that most beans will choose to receive references
 * to collaborating beans via corresponding bean properties or constructor
 * arguments (Dependency Injection).
 *
 * <p>For a list of all bean lifecycle methods, see the
 * {@link BeanFactory BeanFactory javadocs}.
 *
 * @author Rod Johnson
 * @author Chris Beams
 * @since 11.03.2003
 * @see BeanNameAware
 * @see BeanClassLoaderAware
 * @see InitializingBean
 * @see org.springframework.context.ApplicationContextAware
 */
public interface BeanFactoryAware extends Aware {

	/**
	 * Callback that supplies the owning factory to a bean instance.
	 * <p>Invoked after the population of normal bean properties
	 * but before an initialization callback such as
	 * {@link InitializingBean#afterPropertiesSet()} or a custom init-method.
	 * @param beanFactory owning BeanFactory (never {@code null}).
	 * The bean can immediately call methods on the factory.
	 * @throws BeansException in case of initialization errors
	 * @see BeanInitializationException
	 */
	void setBeanFactory(BeanFactory beanFactory) throws BeansException;

}

```

```java
/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;

/**
 * Interface to be implemented by any object that wishes to be notified
 * of the {@link ApplicationContext} that it runs in.
 *
 * <p>Implementing this interface makes sense for example when an object
 * requires access to a set of collaborating beans. Note that configuration
 * via bean references is preferable to implementing this interface just
 * for bean lookup purposes.
 *
 * <p>This interface can also be implemented if an object needs access to file
 * resources, i.e. wants to call {@code getResource}, wants to publish
 * an application event, or requires access to the MessageSource. However,
 * it is preferable to implement the more specific {@link ResourceLoaderAware},
 * {@link ApplicationEventPublisherAware} or {@link MessageSourceAware} interface
 * in such a specific scenario.
 *
 * <p>Note that file resource dependencies can also be exposed as bean properties
 * of type {@link org.springframework.core.io.Resource}, populated via Strings
 * with automatic type conversion by the bean factory. This removes the need
 * for implementing any callback interface just for the purpose of accessing
 * a specific file resource.
 *
 * <p>{@link org.springframework.context.support.ApplicationObjectSupport} is a
 * convenience base class for application objects, implementing this interface.
 *
 * <p>For a list of all bean lifecycle methods, see the
 * {@link org.springframework.beans.factory.BeanFactory BeanFactory javadocs}.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 * @see ResourceLoaderAware
 * @see ApplicationEventPublisherAware
 * @see MessageSourceAware
 * @see org.springframework.context.support.ApplicationObjectSupport
 * @see org.springframework.beans.factory.BeanFactoryAware
 */
public interface ApplicationContextAware extends Aware {

	/**
	 * Set the ApplicationContext that this object runs in.
	 * Normally this call will be used to initialize the object.
	 * <p>Invoked after population of normal bean properties but before an init callback such
	 * as {@link org.springframework.beans.factory.InitializingBean#afterPropertiesSet()}
	 * or a custom init-method. Invoked after {@link ResourceLoaderAware#setResourceLoader},
	 * {@link ApplicationEventPublisherAware#setApplicationEventPublisher} and
	 * {@link MessageSourceAware}, if applicable.
	 * @param applicationContext the ApplicationContext object to be used by this object
	 * @throws ApplicationContextException in case of context initialization errors
	 * @throws BeansException if thrown by application context methods
	 * @see org.springframework.beans.factory.BeanInitializationException
	 */
	void setApplicationContext(ApplicationContext applicationContext) throws BeansException;

}

```

作者：arthur.dy.lee
时间：2017.10.12
