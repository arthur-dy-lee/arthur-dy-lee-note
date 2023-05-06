#学习笔记

-------
一些知识总结，还在不断汇总中

## 一、读书笔记

[《从Paxos到Zookeeper》笔记](读书笔记/《从Paxos到Zookeeper》笔记.md)

 [《深入分布式缓存-从原理到实践》笔记](《深入分布式缓存-从原理到实践》笔记.md)

[《EffectiveJava》笔记](《EffectiveJava》笔记.md)

[《Java性能优化》笔记](读书笔记/《Java性能优化》笔记.md)

## 二、软件方法

### 2.1 管理和软技能

- [软技能](软件方法/-软技能.md)

### 2.2 软件过程

 - [DDD](DDD.md)

更多待完善

## 三、设计模式

[设计模式](设计模式/设计模式.md)

## 四、 分布式

### 4.1 高并发高可用

高并发(SS缓数异池NGR)

高可用（超负压限降隔回灰故故）

### 4.2 分布式理论
- [分布式](distributed/-分布式.md)

### 4.3 raft
- [raft](raft/raft.md)
- [raft原论文中文翻译](raft/raft-zh_cn/raft-zh_cn.md)
- [raft英文(小)论文: In Search of an Understandable Consensus Algorithm](https://ramcloud.atlassian.net/wiki/download/attachments/6586375/raft.pdf)
- [raft英文(大)论文: consensus-bridging-theory-and-practice](raft/consensus-bridging-theory-and-practice.pdf)


## 五、中间件和框架
### 5.1 Kafka
- [Kafka笔记](kafka/kafka_note.md)

### 5.2 redis

- [redis](redis/redis.md)

### 5.2 netty
- [netty笔记](netty/-netty.md)
- [netty-promise-future](netty/netty-promise-future.md)

### 5.3 spring
#### [spring](spring/-spring.md)

#### 5.3.1 IOC

 - [spring-IOC-Bean-Life-Cycle](spring/spring-IOC/spring-IOC-Bean-Life-Cycle.md)
 - [spring-IOC#finishBeanFactoryInitialization](spring/spring-IOC/spring-IOC#finishBeanFactoryInitialization&getBean.md)
 - [spring-IOC#invokeBeanFactoryPostProcessors](spring/spring-IOC/spring-IOC#invokeBeanFactoryPostProcessors.md)
 - [spring-IOC#obtainFreshBeanFactory](spring/spring-IOC/spring-IOC#obtainFreshBeanFactory.md)

#### 5.3.2 AOP
 - [1.spring-AOP-IOC的启动](spring/spring-AOP/1.spring-AOP-IOC的启动.md)
 - [2.spring-AOP-ConfigBeanDefinitionParser解析器](spring/spring-AOP/2.spring-AOP-ConfigBeanDefinitionParser解析器.md)
 - [3.AspectJAwareAdvisorAutoProxyCreator创建代理对象](spring/spring-AOP/3.AspectJAwareAdvisorAutoProxyCreator创建代理对象.md)
 - [4.spring-AOP-invoke调用](spring/spring-AOP/4.spring-AOP-invoke调用.md)
 - [AspectJAwareAdvisorAutoProxyCreator](spring/spring-AOP/AspectJAwareAdvisorAutoProxyCreator.md)
 - [AOP相关.md](spring/spring-AOP/AOP相关.md)

#### 5.3.3 springmvc
 - [spring](spring/spring-mvc/spring-mvc-note.md)
 - [简述Spring容器与SpringMVC的容器的联系与区别](简述Spring容器与SpringMVC的容器的联系与区别 - CSDN博客.mhtml)

#### 5.3.4 spring-transaction
 - [spring-transaction](spring/spring-transaction/spring-transaction.md)

#### 5.3.5 spring-boot
 - [springboot使用笔记](spring/spring-boot/springboot使用笔记.md)

 - [简单摘要记录](spring/spring-boot/简单摘要记录.md)

#### 5.3.6 spring-design-patterns

 - [spring源码设计模式分析-单例模式](spring/spring-design-patterns/spring源码设计模式分析-单例模式.md)

 - [Spring-Design-Patterns](spring/spring-design-patterns/Spring-Design-Patterns.md)

 - [学以致用之NamespaceHandlerSupports](spring/spring-design-patterns/学以致用之NamespaceHandlerSupport.md)

#### 5.3.7 spring其它
 - [Spring-Aware接口](spring/Spring-Aware接口.md)

### 5.4 Mybatis

- [mybatis](mybatis/mybatis源码分析以及整合spring过程.md)

### 5.5 kubernetes

### 5.6 hbase

- [hbase](hbase/-hbase.md)

## 六、数据库

### Mysql
 - [Mysql](Mysql/-Mysql.md)
 - [mysql锁](Mysql/mysql锁.md)
 - [MySQL索引背后的数据结构及算法原理](Mysql/MySQL索引背后的数据结构及算法原理.md)

## 七、Linux

[Linux](Linux/-Linux.md)

## 八、coreJava

- [coreJava](coreJava-coreJava.md)
- [JVM](JVM/JVM调优.md)
- [JVM调优参数](JVM/JVM参数.xlsx)

## 九、业务相关

- [秒杀](biz/秒杀.md)

## 十、面试

1. [面试准备](interview/2020.07.24.md)
2. [-分布式](distributed/-分布式.md)
3. 中间件
   - [Kafka笔记](kafka/kafka_note.md)
   - [netty笔记](netty/Netty_note.md)
   - [mybatis](mybatis/mybatis源码分析以及整合spring过程.md)
   - [中间件](interview/-中间件.md)
4. coreJava
   - [coreJava](coreJava/-coreJava.md)
   - [Java线程池实现原理及其在美团业务中的实践](coreJava/Java线程池实现原理及其在美团业务中的实践.md)
   - [java多线程系列_让主线程等待子任务执行的各种方式](coreJava/java多线程系列_让主线程等待子任务执行的各种方式.md)
   - [IO - 同步，异步，阻塞，非阻塞](coreJava/IO - 同步，异步，阻塞，非阻塞.md)
5. [spring](spring/-spring.md)
6. 应用
   [-秒杀](biz/秒杀.md)
7. 数据库
   - [Mysql](Mysql/-Mysql.md)
   - [Mysql锁原理](Mysql/mysql锁.md)
   - [MySQL索引背后的数据结构及算法原理](Mysql/MySQL索引背后的数据结构及算法原理.md)
8. [-线上问题定位](interview/-线上问题定位.md)
9. [-JVM](JVM/-JVM.md)
10. [-Linux](Linux/-Linux.md)
11. [-网络](interview/-网络.md)
12. 其它
    - [客户端发起一次请求都经历了什么](interview/客户端发起一次请求都经历了什么.xmind)
13. [-算法](interview/-算法.md)



