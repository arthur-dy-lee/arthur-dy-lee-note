

分布式事务的一致性是如何解决的
TCC、Sagas、二阶段、三阶段提交优缺点
springcloud的nacos有没有用过
假设业务一个接口，它有2个实现类：classA和classB，在dev环境上，自动注入classA，在生产自动注入classB，spring是如何实现的？
spring bean的生命周期
如何通过spring实现数据库的读写分离[LazyConnectionDataSourceProxy:https://www.bbsmax.com/A/rV57bGvaJP/]
springcloud中如何实现全链路跟踪
mysql由DBA配置，主从完全同步后，再返回成功写入，解决读写分离异步问题
用java时，如何防止死锁（ReentrantLock设置过期时间）
做优惠活动时，如何防止超卖
redis分布式是怎么实现的
如何保证事务的一致性
项目在部署的时候采用什么样的垃圾回收算法
是否能举一个实际的例子，是如何优化JVM的
Java中类的加载模型是怎么样的
用过docker吗
在公司中做的比较有亮点的项目是什么
在生产环境中，redis在某一段时间内，数据量太大的话，它崩溃了，这时候怎么处理？如何尽快的恢复？
大数据有了解过吗

你做的项目带了哪些value，直接的、间接的效益。从技术上的体系来说，你觉得有亮点的地方？
原来的系统是有状态的吗？如何将有状态的转成无状态的？
在需要记录校验器的状态，才能做后续的工作，这是一个工作流，这需要一个状态机的机制，这个是怎么做的？
工作流是用什么技术方案实现的？
工作流的状态机？？
如果简单定义一个fork的话，你怎么样去定义？校验器是如何定义一个工作流的？
校验器有一个基类/接口，你有众多的校验器实现类，在这么多类中，你是如何实现校验器间与或的关系，如果是代码思维去实现的话，你会想到一个什么样的设计模式，或者想到一个什么样的设计方法，去可以把它实现出来。
一个校验器并不一定是一个耗时很短的操作，你是否能给我一个方案，可以很快的组合成一个引擎组的校验器，这个方案的类设计如何去做,如果要去做简单的关联，你应该怎么样去做？
如果设计一个GroupA的校验器组，你是怎么来设计？
如何将一个活动的validate组合起来，方便为每一个活动进行校验和使用？这里有一个常见的模式去做的。
如何去做一个通用的代码，以后拿来校验器的配置，无论是数据库的配置，还是xml，还是json格式，只要是和activityId的关系在了，我立刻就是把这套组合引擎实例化，通过代码的方式实例化出来。
其实最后面试官问的是责任链模型，可以把校验器的动态应用串联起来。
如果是责任链模型实现这样一个功能的话，你应该怎么去做。责任链有一个比较关键的因素是，你怎么通过上一个去管理下一个？其实这里面有一个指针指向的概念，next下一个栈是谁的概念。
从业这么多年，你有没有这么一个项目，可以拿出来，值得跟大家分享的一个项目？

延伸一下，netty是怎么找到下一个contextHandler的？

### 阿里云伯乐系统
垃圾回收器有哪些种类，以及它们的优劣，讲一下CMS原理
并归排序1~10000
linux的内核都你关注哪些参数
如何限制1分钟内10个线程在执行，如何实现
tpc/ip为什么是稳定可靠的，tpc头都包括哪些
连接池原理，如何实现
Mybaits原理
设计一个秒杀系统，如何做到30分钟不成交自动取消订单
dubbo 如何做服务注册、服务发现、注册中心、重试、快速失败


### 阿里云
============================================
1、能体现你技术优势和水平的是哪个项目
2、这个项目都有哪些功能，它是做什么的，你又做了什么
3、这个项目在系统上的划分是怎么划分的，整体上的一个大的架构
4、在这个比较复杂的活动中，比较复杂的技术点在哪里？流程编排
5、在这个流转的过程中，如果流转失败，异常情况是怎么处理的
6、如果在流程流转时，失败了，可能由于网络原因失败，这时怎么处理
7、如果通过报警的方式，开发人员修改会比较滞后，有没有比较好的方式
8、除了服务的降级，有个别的掉单情况，怎么处理比较合适
9、有没有对异常有个整体的处理，配置缺失、网络原因、系统的bug等等，针对不同的场景有不用的应对机制。对于异常在设计上有什么健全的保障体系。举个例子，如果是系统bug，怎么发现它，怎么提前发现，怎么去定位到bug原因，定位到后怎么去做一个快速的恢复；如果是配置丢失败，是不属于系统的bug，对于这种怎么解；对于网络上下流交付问题又该怎么解
10、用的是什么分布式缓存进行的落地，tair(redis)使用过程中，有没有遇到一些比较大的问题
11、分布式缓存的失效机制是怎么处理的
12、缓存是如何更新的，如何保证数据的一致性。如果更新数据库失败的，缓存更新失败了怎么办。你刚才提到，先更新数据库后更新缓存，我想听到的是一个完整的操作顺序，不是简单的两句话就能保证数据的一致性的，我理解。在删除缓存失败的空档期，对外提供的服务是旧的缓存，该怎么办？Fackbook是如何保证缓存的一致性的。cache aside描述下
13、在官网活动中还有没有比较复杂的技术场景，或者遇到比较大的挑战
14、分布式锁有几种实现方式？
15、数据库可以实现分布锁吗？
数据库插入一条数据代表锁
16、如何删除数据库分布式锁？删除数据时失败了怎么办
17、在这个项目过程还有没有遇到其它的挑战
18、你最近有在研究什么东西吗
19、数据库悲观锁和乐观锁
20、cookie和session有什么区别。什么场景适合用cookie什么场景适合用session，考虑到安全的时候用session，考虑到安全的时候用cookie
21、堆跟栈有什么区别
22、如果自己写一个消息中间件的话，比较难的点在哪里。如果做为使用方的话，如何保证消息不丢失。
【rocketmq在投消息时，如果没有ack消息返回，会怎么样？】
23、消息的时候，如何保证去重。
24、在工作或学习过程中最棘手的问题是什么
25、让你写一篇设计文档的话，你会怎么来写这个目录。我理解的技术文档分了应用部分、存储部分、还有一些物理的部署部分。


### 浙江中控
JMV内存优化大概做哪些工作?
JVM内存具体做哪些优化？
JVM对内存的优化，不管是年轻代、永久代，对runtime内存的曲线，跟代码，系统的性状是有很大关系的，比如说对象的大小，创建对象的数量，包括系统运行过程中上下文的一些的开销会有关系，在优化的过程中，有没有跟代码问题关联起来，去做一些优化一个系统架构升级？还是说针对一个现象去调优，针对现有的系统最优化的系统配置呢？

JVM表现的一个形态，比如说你去做一个JVM的跟踪，比如说你JConsole或者说阿里的开源工具对吧？你可以看到一些曲线对吧？还有内存增长情况，就堆内存的一些情况对吧？这个曲线其实跟你的代码有关系的，比如说你的你的系统的一个容量，对吧？你代码你系统启动的时候，你初始化类的个数对吧？包括你有没有大对象，包括你的一些系统常量池面的一些内容，这些东西其实跟你的代码是有关系的。如果你在优化JVM的同时有没有去针对你的前面的现象去反过来去优化代码，两个形成一个互相优化的过程，对吧？

微服务应用的话，一般大概是一个什么样的场景？就是说你的架构设计是怎么做的，为什么？
按照你们现在这样的这种架构设计，你们的代码分层是怎么做的？你分了哪些成级？
服务间调用的话，用的是什么方案？服务之间的API调用。
如果你们使用k8s做投治理，你这位的对内的API跟对外的API，你的权限怎么去分配权限怎么管理？
对大数据这块有了解过吗？没有做过相关的应用。
你是觉得你的优势在哪些方面？你个人觉得现在你比较有突出的地方是在哪里？
看你项目经验也挺多的，而且我觉得有些项目还不错，能不能跟我讲讲你觉得比较拿得出手的项目，或者说比较有比亚迪技术含量的？
接到这个项目以后，你们团队的核心的工作是什么？
这个系统面临的最大的一个流量压力是在一个什么业务上呢？
抽奖这块你的最大的并发大概是在多少？
抽奖活动主要的架构是怎么样的？能不能跟我讲一下？
比如说你的他的请求过来以后，你怎么就数据流是怎么样的？首先经过哪一个经过哪里，能不能介绍一下？首先从前端系统我在官网发起了一个请求，对吧？



### 网易
JVM调优有主要是有哪些工作吗？解决什么问题？
JDK1.8里面JVM对内存的管理，它有什么样的优化？
JVM原数据空间的参数是什么？你们配置的是多大？







### 翼信
redis的数据结构介绍下?
现在我们用Java分别去实现 redis的这几种数据结构可以分别怎么对应？就举个例子，Java的String和release的String可以对应，其他的类型是可以分别怎么对应？
我有1000个帖子，然后每个帖子它都有点击量，然后现在是要求找出点击量最多的100个帖子，然后并从这100个帖子中随机的挑10个推推给用户，就这种用redis可以去怎么做？
redis有没有随机的一些函数可以用的？
redis分布式锁原理？


### 菜鸟
我们平时代码里面 object的两个方法equal和has code的，然后一般来说，我们假如说要重写这一个方法，这两个方法的时候都是需要同时去重写的。我能不能问一下，假如有一个人他写只重写了一头子，没有重写还是错的？他在什么情况下会有问题？
我们有用到Java的外部容器，比如说 tomcat，它去加载两个war包的时候，它两个war包之间它有可能去引的同源的类，然后你能不能说一下他的类隔离是可以怎么样去做？
平时网络排查的时候经常会用到的那个东西，假如说假如说你想去你想去看一下你当前的这台仪器，去访问一个域名，中间都经过了那些路由器的ip是多少？你说我怎么样去看这个东西？比如说我机房里面有这么多路由器，然后我去访问一下百度，然后我到底是从哪家过去的，要去查一查这个东西我怎么查
mysql的事物隔离级别都有哪些吗？你能不能说一下可重复读这个级别上面它相当于级别最高的完全探寻化的级别，它有什么隔离问题？
先说一个场景，假如说我的一个 Java的程序，然后在线上运行的时候，它的CPU突然跑得非常高，然后把一个CPA的河占满了，然后这个时候我想去排查一下它是什么原因，你能不能说一下排查的思路大概是什么样子的？





### zoom
在最近说两三年中有没有什么觉得可以分享的一些项目中碰到的问题，经验，比如说解决了一个什么难点，或者是做了觉得可以分享一下东西。
那就是说在我们的系统开发中间有没有碰到过一些什么有意思的东西？



算法：

1、数据，下标相加等于target。例如：
int data[] = new int[3]; data[0]=3;data[1]=5;data[2]=6; target=8,则输出0，1

2、进门向左向右走，8步有多少种可能?

其实就是一2叉颗树嘛，有多少条路径。

LSM、MappedByteBuffer、Memory Mapped Files

#### Memory Mapped Files

Memory Mapped Files(后面简称mmap)也被翻译成 内存映射文件 ，在64位操作系统中一般可以表示20G的数据文件，它的工作原理是直接利用操作系统的Page来实现文件到物理内存的直接映射。完成映射之后你对物理内存的操作会被同步到硬盘上（操作系统在适当的时候）。

### kakfa读取数据基于sendfile实现Zero Copy

 所谓零拷贝，指的是应用内存与内核内存不存在拷贝。 对应零拷贝技术有mmap及sendfile。



传统模式下，当需要对一个文件进行传输的时候，其具体流程细节如下：

1. 调用read函数，文件数据被copy到内核缓冲区
2. read函数返回，文件数据从内核缓冲区copy到用户缓冲区
3. write函数调用，将文件数据从用户缓冲区copy到内核与socket相关的缓冲区。
4. 数据从socket缓冲区copy到相关协议引擎。

>硬盘—>内核buf—>用户buf—>socket相关缓冲区—>协议引擎

而sendfile系统调用则提供了一种减少以上多次copy，提升文件传输性能的方法。
在内核版本2.1中，引入了sendfile系统调用，以简化网络上和两个本地文件之间的数据传输。 sendfile的引入不仅减少了数据复制，还减少了上下文切换。

```
sendfile(socket, file, len);
```

运行流程如下：

1. sendfile系统调用，文件数据被copy至内核缓冲区
2. 再从内核缓冲区copy至内核中socket相关的缓冲区
3. 最后再socket相关的缓冲区copy到协议引擎

相较传统read/write方式，2.1版本内核引进的sendfile已经减少了内核缓冲区到user缓冲区，再由user缓冲区到socket相关缓冲区的文件copy，而在内核版本2.4之后，文件描述符结果被改变，sendfile实现了更简单的方式，再次减少了一次copy操作。

#### kakfa批量压缩

在很多情况下，系统的瓶颈不是CPU或磁盘，而是网络IO，对于需要在广域网上的数据中心之间发送消息的数据流水线尤其如此。进行数据压缩会消耗少量的CPU资源,不过对于kafka而言,网络IO更应该需要考虑。

- 如果每个消息都压缩，但是压缩率相对很低，所以Kafka使用了批量压缩，即将多个消息一起压缩而不是单个消息压缩
- Kafka允许使用递归的消息集合，批量的消息可以通过压缩的形式传输并且在日志中也可以保持压缩格式，直到被消费者解压缩
- Kafka支持多种压缩协议，包括Gzip和Snappy压缩协议

# 合并两个有序链表

https://leetcode-cn.com/problems/merge-two-sorted-lists/solution/he-bing-liang-ge-you-xu-lian-biao-by-leetcode-solu/

```java
class Solution {
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        if (l1 == null) {
            return l2;
        } else if (l2 == null) {
            return l1;
        } else if (l1.val < l2.val) {
            l1.next = mergeTwoLists(l1.next, l2);
            return l1;
        } else {
            l2.next = mergeTwoLists(l1, l2.next);
            return l2;
        }
    }
}

```





间隙锁等
什么锁解决了什么读
AQS
熔断 Hystrix原理，2种？
kafka, 和rocketMQ对比
epoll
Unsafe
CompletionService
MCS队列锁
ThreadLocalMap
“子线程”正常拿到父线程传递过来的变量
InheritableThreadLocal TransmittableThreadLocal
miniIO 原理

springcloud是怎么通信的
springlcoud Fegin原理
rockmq顺序消费
Java序列化为什么性能比较低
netty之五责任链模式，ChannelHandler 是怎么找到下一个的？
为什么ZAB崩溃后需要双向同步？
为什么G1不建议32G以上
为什么https://leetcode-cn.com/problems/remove-nth-node-from-end-of-list/solution/shan-chu-lian-biao-de-dao-shu-di-nge-jie-dian-b-61/，它的时间复杂度：O(L)
Java线程死锁
https://blog.csdn.net/hchaoh/article/details/103903410
规则引擎
线程池线程存活？
reentrantlock公平锁和非公平锁
serializable,hessian,protobuf， serializable为什么慢
2个千万级大表怎么关联取数https://zhuanlan.zhihu.com/p/147371283
epoll原理 https://blog.csdn.net/armlinuxww/article/details/92803381
explain命令关注哪些字段
socket和websocket的区别？
银行事务补偿机制
springboot怎么向k8s注册中心

jdk代理和CGLIB代理区别

redis怎么做热点监控

怎么做一个大型的网络设计？k8s的，公云和私有云网络如何结合？

RPC 与HTTP2区别

JVM调优通用方法论

perf热力图

Hbase布隆过滤器设置的多大？

netty为什么快？

netty中channel是怎么知道被响应的？

rocketMQ如何顺序消费？有没有其它顺序消费的解决方案？

Http和RPC区别和关系？gRPC如何封装http2的？https://www.zhihu.com/question/41609070

布隆过滤器大小如何设置？HBase是设置的多少？

类加载器如何卸载字节码？

Google开源工具包Guava提供了限流工具类RateLimiter，该类基于令牌桶算法来完成限流。有空看看啊。



#### caffeine 

[caffeine官网说明](https://github.com/ben-manes/caffeine/wiki/Design-zh-CN)

createExpensiveGraph
同时配置了expireAfterWrite和expireAfterAccess

localcache和分布式缓存使用法测，多热的key？

Jackson、hession2、protobuf序列化反序列化应用场景和区别？

FIFO，LRU和LFU存储的数据结构？

Count-Min Sketch？

Guava cache是LRU吗？Caffeine是使用w-tinylfu 算法，有使用到LRU吗？

Guava cache优秀在哪里？

Guava cache和Caffeine的数据结构分别是什么？

Guava cache为什么定义类的key为WeakReference，values为WeakReference or SoftReference soft 【https://blog.csdn.net/lijunhuayc/article/details/48714015】

Guava cache和ConcurrentHashMap区别，或者比较大的区别？


>@Cacheable(cacheNames="foos", sync="true")
在多线程环境下，某些操作可能使用相同参数同步调用。默认情况下，缓存不锁定任何资源，可能导致多次计算，而违反了缓存的目的。对于这些特定的情况，属性 sync 可以指示底层将缓存锁住，使只有一个线程可以进入计算，而其他线程堵塞，直到返回结果更新到缓存中。 





RocketMQ的高性能在于顺序写盘(CommitLog)、零拷贝和跳跃读(尽量命中PageCache)

数据库死锁排查？

redis更新失败/删除失败怎么办？

如果是删除方法如evict和clear等，需要先删掉二级缓存的数据，再去删掉一级缓存的数据，否则有并发问题。

在写入缓存前先比较数据的版本号或者修改时间，禁止向缓存中写入更旧的版本。

采用这种同步淘汰策略，吞吐量降低怎么办？改为异步。





如果下面的list中有10个item，删除后，会剩下几个？这种情况还可以用于分页吗？缓存分页是怎么做的？

```java
@CacheConfig(cacheNames = { "userFocus" })
public interface UserFocusMapper {

@CacheEvict(value="userFocusList",key = "#p0.userCode")
int insert(UserFocus userFocus);

@Cacheable(key = "#p0.userCode+'_'+#p0.focusName+'_'+#p0.focusType",unless="#result == null")
UserFocus findFocus(UserFocus userFocus);

@Cacheable(value="userFocusList",key = "#p0.userCode",unless="#result == null||#result.size() == 0")
List<UserFocus> findFocusList(UserFocus userCode);
//利用@Caching中evict指定两个value下的不同key删除。
@Caching(evict = {@CacheEvict(value="userFocusList",key = "#p0.userCode"),
@CacheEvict(value="userFocus",key = "#p0.userCode+'_'+#p0.focusName+'_'+#p0.focusType")})
void updateUserFocus(UserFocus userFocus);

}
```





### 待整理

guava 的单线程回源

```java
LoadingCache<String, String> cache = CacheBuilder
           .newBuilder()
           .refreshAfterWrite(4L, TimeUnit.SECONDS)
           .expireAfterWrite(5L, TimeUnit.SECONDS)
           .build(loader);
```



guava 后台异步刷新

https://www.dazhuanlan.com/li_xl/topics/986612

