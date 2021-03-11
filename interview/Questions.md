### 滴普
日志归并树LSM，和B+树区别
LSM树最主要是用在哪些场景里面
列存储和行存储有什么区别
倒排索引
raft协议、zab协议，以及它们的区别，它们的相同和不同点
java的生产者和消费者模型，多线程模型
jvm内存优化
分布式锁
mvcc

### 之江实验室
从spring到springboot，到springcloud为什么做的二次升级
springcloud的组件有哪些，常用的组件有哪些
和springcloud类似的框架有接触过哪些
k8s有哪些核心组件
对docker有了解吗
对java涉及到的框架有没有系统的分析过它的源码
IOC原理，注入是怎么来做的
netty是一个高效的网络中间件，netty实现网络IO高性能的原理
netty零拷贝


缓存存在什么风险
缓存你们的过期时间是怎么设置的
如果设置缓存1分钟，同时大面积缓存过期怎么办
缓存穿透是怎么做的？如果返回null放缓存后，访问的资源都是随机的，那岂不是请求全部都会打到后端
你了解限流算法有哪些？令牌桶和漏斗应用在什么场景？
你对netty了解吗
netty为什么说是高性能的？从哪些方面保证？在数据传输过程中做发哪些优化？
传输层是否会对数据包拆包吗？大数据多大的时候，会进行拆包？
poll,epoll,select这3个区别？jdk有个bug，记得是什么吗？
消息队列用在什么地方？它都有哪些应用场景？
中奖和奖品分配分开有什么优点吗
Java的随机算法你了解吗？底层是怎么实现的？知道伪随机吗？在选择Random和SecureRandom时是如何做选择的，以及为什么这样选择？
有对比过Kafka和rocketMQ吗
rabbitMQ和rocketMQ底层的实现能介绍一下吗，底层实现的对比能说一下吗。
消费者从broker中拉取消息，如果有很多消费者，但服务器上都没有消息，这不会对服务器造成很大的压力吗？不会浪费很多的资源吗？
rocketMQ推和拉的方式是怎么实现的?消费速度是由谁来控制？
有这么一个情景，要做一个在线教育平台，我们希望通过一些用户的行为，比如在什么时候点击了什么东西，做了什么操作。我们希望通过用户的操作来分析出用户的行为习惯，然后基于行为习惯得到一个有推荐价值的结果，在这么一种需求下，用户数大约达到10w左右。如果要做这样一个系统，如果是由你来做的话，如何从架构、技术选型、安全性、高性能、高可用、缓存等方面做一个设计？
数据中台和数据仓库之前有接触过吗？（上面的问题也可以从这个角度来思考下）
创建VM在每个流程涉及到的概念能说一下么？比如安全组。安全组是做什么的？
k8s有哪些组件，这些组件是用来干嘛的



### 维灵
spring微服务支撑什么东西？和阿里dubbo、hsf体系的区别?
rpc这块有没有了解过
mq中间件在这里面做什么作用
在多写少读的场景怎么用好各种中间件
redis有什么特色
redis你用过集群吗？哨兵和codis集群方案对比？
关系型数据库优化都做哪些东西
数据库中有很多大的字段？有时候1秒种会有一千条数据，这时候我会合并1k条数据一次存储，这时候在数据库设计的时候有什么要注意的地方？怎么样在关系型数据库中把大的字段设计好
分布式CAP能说一下么，如果是ap模型，那么数据一致性应该怎么做呢
除了java语言外其它语言有涉及吗



### 恒生
HFS的技术特点
hsf服务除了服务注册发现，还有什么别的能力吗
hsf注册发现和市面上其它的注册发现框架相对有什么优势吗，比如说consol，nacos，eruka等有什么优点
分布式事务
对于订单支付和库存减少，如果扣款完成了，发现库存不足了，怎么通过分布式事务解决问题，怎么通过消息队列实现事务控制
延时消息
如果使用框架的话，如何使用分布式事务
redis除了分布式锁，还在什么业务情况下用过它
springboot能讲讲网关是怎么用的，网关的源代码有看过吗
springboot configuration的实现原理有看过吗
有用到过mysql的高级特性吗？比如json的特性。
是怎么把应用部署的
灰度升级
Java本来的JVM调优有涉及到吗
pod启动，有没有偿试过最小内存能启动起来
k8s中，request（资源的配额声明）很多资源的话，这个资源（内存）是被占用的
springboot 2.0以后，内存做了一些优化


### 阿里新零售
序列化能介绍下么？序列化的过程中要怎么写才可能序列化呢？这个序列化serialVersionUID可以不写吗？
营销活动来的时候，你怎么快速支撑营销？
公有云和私有云平台架构是什么样的？趋势是什么样的？
平台多租户是怎么处理的？数据库的资源是怎么通过多租户隔离开？应用程序如何进行多租房的识别和隔离？
IASS、PASS、SAAS
平台计费是怎么做？如果一个主机的规格是8U32G，用的时候突然流量上来了；有的是CPU密集型，有的是内存或IO密集型，会通过不同的组合生成不同的订单吗？
监控是怎么做的
openstack和k8s，他们都可以做资源隔离，是怎么做的

### 数梦工厂子公司
对开发计划、进度、质量是怎么进行管控的
redis有降级吗？
线程、线程池接触多不多
mybatis用的多吗？深度怎么样？一级缓存和二级缓存了解吗？
mybatis分页是怎么做的？
spring事务的隔离级别
jdk源码会不会看，比如并发包
线上调优，是初始的参数去优化，还是有问题后再去调优？
springcloud和hsf架构选型，你会怎么选，理由是什么
配置中心有没有用Nacos（Nacos也是服务注册中心）
ES有没有接触过
NoSql的数据库了解吗


### coupon
算法
1, 输入：数字字符串：input；个数：n，从input中删除n个数字，留下的数字最小。例如：142293, 2 -> 1223;241293, 2 -> 2123;

2, Given a grid m*n ,(m > 0 & n > 0); each cell has integer value, and two points A(0, 0), B(a, b) (m>a>0,n>b>0); Write an executable program to calculate the shortest path from A to B; you can only route down or right(Program language: Any anguag) 

3, Give a link, A->B->C->D->E->F->-G->H_; Write a program to check whether the link has a loop ...

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























