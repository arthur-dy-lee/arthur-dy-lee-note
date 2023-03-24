

## 一、redis的部署模式?



#### Redis cluster模式，再看一下哨兵、codis

redis cluster是Redis的分布式解决方案，自动将数据进行分片，每个master上放一部分数据。
支撑N个redis master node，每个master node都可以挂载多个slave node，高可用，因为每个master都有salve节点，那么如果mater挂掉，redis 。cluster这套机制，就会自动将某个slave切换成master。

默认情况下，redis cluster的核心的理念，主要是用slave做高可用的，每个master挂一两个slave，主要是做数据的热备，还有master故障时的主备切换，实现高可用的，redis cluster默认是不支持slave节点读或者写的。

redis cluster的hash slot算法
redis cluster有固定的16384个hash slot，对每个key计算CRC16值，然后对16384取模，可以获取key对应的hash slot

redis cluster缺点
由于n个key是比较均匀地分布在Redis Cluster的各个节点上，因此无法使用mget命令一次性获取

#### 集群模式和哨兵模式的区别

1. 哨兵模式监控权交给了哨兵系统，集群模式中是工作节点自己做监控
2. 哨兵模式发起选举是选举一个leader哨兵节点来处理故障转移，集群模式是在从节点中选举一个新的主节点，来处理故障的转移
3. 即使使用哨兵，redis每个实例也是全量存储，每个redis存储的内容都是完整的数据，浪费内存且有木桶效应。为了最大化利用内存，可以采用集群，就是分布式存储。

Redis-Sentinel(哨兵模式)是官方推荐的高可用解决方案

##### Codis

Codis分片原理
codis最核心的部分就是数据分片，codis将所有的key划分为1024个slot，首先对客户端传来的key进行crc32计算hash，再将hash值对1024取模，取模后的余数就是这个key的slot位置。每个slot都会唯一影射到一个redis实例，codis会在内存中维护slot和redis实例之间的对应关系。

不同codis实例之间如何同步这个slot和redis实例之间的映射关系数据，因为要保证客户端随便访问到一个codis实例都能够查询到key值对应的redis实例，那就要保障每个codis实例在内存中都保留一份映射关系。为了解决这个问题，codis使用了zookeeper和etcd来解决这问题，codis将slot关系存储在zookeeper中，并且提供了codis dashboard来观察和修改slot关系，当slot关系发生变化后，codis proxy会监听到变化并重新同步slot映射关系到所有codis实例，从而实现多个codis节点之间的数据同步。

Codis扩容
大家喜欢使用codis的一个很大的原因就是codis可以根据业务量对于redis做到动态实例扩容。
在有新增redis实例后，codis会扫描出旧节点中所有待迁移slot中的所有key，然后逐个将key迁移到新的redis节点中。在迁移过程中新旧节点中都有同一个slot，但是其中的key可能还没迁移完成，这时候可能有请求待迁移slot中的key，codis使用的方案不是去看这个key有没有完成迁移，而是立即强制对当前单个key进行迁移，迁移完成后，将请求转发到新的redis实例。

sentinel
sentinel是官方的一种主从方案，主要负责监控主从节点的健康状况，当master节点挂掉后，自动选择一个最优的slave节点顶替master节点。当master节点切换后，sentinel会通知所有slave节点新的master节点地址，并且主从节点的数据复制关系也进行了变更，这些信息都会保存在sentinel中，并下发到所有节点。

**sentinel的集群方案主要解决的是HA的问题，并不能解决超大缓存数据的分片和分散访问热点的问题。**



redis哨兵解决了什么问题？codis解决了什么问题？

## 二、redis底层数据结构

### Redis五种数据结构如下

1.String 字符串类型
简单动态字符串SDS
2.Hash （哈希）
redis的哈希对象的底层存储可以使用ziplist（压缩列表）和hashtable。当hash对象可以同时满足一下两个条件时，哈希对象使用ziplist编码。
A、哈希对象保存的所有键值对的键和值的字符串长度都小于64字节
B、哈希对象保存的键值对数量小于512个

3.List 说白了就是链表（redis 使用双端链表实现的 List）
4.Set集合
redis的集合对象set的底层存储结构特别神奇，我估计一般人想象不到，底层使用了intset和hashtable两种数据结构存储的，intset我们可以理解为数组，hashtable就是普通的哈希表（key为set的值，value为null）。是不是觉得用hashtable存储set是一件很神奇的事情。
set的底层存储intset和hashtable是存在编码转换的，使用intset存储必须满足下面两个条件，否则使用hashtable，条件如下：
A、结合对象保存的所有元素都是整数值
B、集合对象保存的元素数量不超过512个
5.zset有序集合
zset底层的存储结构包括ziplist或skiplist，在同时满足以下两个条件的时候使用ziplist，其他时候使用skiplist，两个条件如下：
A、有序集合保存的元素数量小于128个
B、有序集合保存的所有元素的长度小于64字节
当ziplist作为zset的底层存储结构时候，每个集合元素使用两个紧挨在一起的压缩列表节点来保存，第一个节点保存元素的成员，第二个元素保存元素的分值。

### redis的底层实现数据结构

1、简单动态字符串
2、链表
3、字典
4、跳跃表
5、整数集合
6、压缩列表

大多数情况下，Redis使用简单字符串SDS作为字符串的表示，相对于C语言字符串，SDS具有常数复杂度获取字符串长度，杜绝了缓存区的溢出，减少了修改字符串长度时所需的内存重分配次数，以及二进制安全能存储各种类型的文件，并且还兼容部分C函数。
通过为链表设置不同类型的特定函数，Redis链表可以保存各种不同类型的值，除了用作列表键，还在发布与订阅、慢查询、监视器等方面发挥作用（后面会介绍）。
Redis的字典底层使用哈希表实现，每个字典通常有两个哈希表，一个平时使用，另一个用于rehash时使用，使用链地址法解决哈希冲突。
跳跃表通常是有序集合的底层实现之一，表中的节点按照分值大小进行排序。
整数集合是集合键的底层实现之一，底层由数组构成，升级特性能尽可能的节省内存。
压缩列表是Redis为节省内存而开发的顺序型数据结构，通常作为列表键和哈希键的底层实现之一。

## 三、redis原理



### Redis持久化：RDB、AOF

RDB 的优点:
RDB 是一个非常紧凑（compact）的文件，它保存了 Redis 在某个时间点上的数据集。 
RDB 的缺点:
因为RDB 文件需要保存整个数据集的状态， 所以它并不是一个轻松的操作。因此你可能会至少 5 分钟才保存一次 RDB 文件。 在这种情况下， 一旦发生故障停机， 你就可能会丢失好几分钟的数据。
AOF 的优点:
	AOF 的默认策略为每秒钟 fsync 一次，在这种配置下，Redis 仍然可以保持良好的性能，并且就算发生故障停机，也最多只会丢失一秒钟的数据（ fsync 会在后台线程执行，所以主线程可以继续努力地处理命令请求）。
AOF 的缺点:
对于相同的数据集来说，AOF 文件的体积通常要大于 RDB 文件的体积。根据所使用的 fsync 策略，AOF 的速度可能会慢于 RDB 。



### Redis 键的过期删除策略及缓存淘汰策略

**Redis过期删除策略是采用惰性删除和定期删除这两种方式组合进行的**，惰性删除能够保证过期的数据我们在获取时一定获取不到，而定期删除设置合适的频率，则可以保证无效的数据及时得到释放，而不会一直占用内存数据。

但是我们说Redis是部署在物理机上的，内存不可能无限扩充的，**当内存达到我们设定的界限后，便自动触发Redis内存淘汰策略**，而具体的策略方式要根据实际业务情况进行选取。

#### 过期删除策略

##### 定期删除
Redis过期Key清理的机制对清理的频率和最大时间都有限制，在尽量不影响正常服务的情况下，进行过期Key的清理，以达到长时间服务的性能最优。redis会把设置了过期时间的key放在单独的字典中，每隔一段时间执行一次删除(在redis.conf配置文件设置hz，1s刷新的频率)过期key的操作。

具体的算法如下:

1. Redis配置项hz定义了serverCron任务的执行周期，默认为10，即CPU空闲时每秒执行10次;
2. 每次过期key清理的时间不超过CPU时间的25%，即若hz=1，则一次清理时间最大为250ms，若hz=10，则一次清理时间最大为25ms;
3. 清理时依次遍历所有的db;
4. 从db中随机取20个key，判断是否过期，若过期，则逐出;
5. 若有5个以上key过期，则重复步骤4，否则遍历下一个db;
6. 在清理过程中，若达到了25%CPU时间，退出清理过程;

这是一个基于概率的简单算法，基本的假设是抽出的样本能够代表整个key空间，redis持续清理过期的数据直至将要过期的key的百分比降到了25%以下。这也意味着在长期来看任何给定的时刻已经过期但仍占据着内存空间的key的量最多为每秒的写操作量除以4。

由于算法采用的随机取key判断是否过期的方式，故几乎不可能清理完所有的过期Key;
调高hz参数可以提升清理的频率，过期key可以更及时的被删除，但hz太高会增加CPU时间的消耗，为了保证不会循环过度，导致卡顿，扫描时间上限默认不超过25ms。
根据以上原理，系统中应避免大量的key同时过期，给要过期的key设置一个随机范围。

优点：通过限制删除操作的时长和频率，来减少删除操作对CPU时间的占用，处理"定时删除"的缺点，定期删除过期key，处理"惰性删除"的缺点
缺点：在内存友好方面，不如"定时删除" 在CPU时间友好方面，不如"惰性删除"
难点：合理设置删除操作的执行时长（每次删除执行多长时间）和执行频率（每隔多长时间做一次删除），这个要根据服务器运行情况来定了

##### 惰性删除
过期的key并不一定会马上删除，还会占用着内存。 当你真正查询这个key时，redis会检查一下，这个设置了过期时间的key是否过期了? 如果过期了就会删除，返回空。这就是惰性删除。

优点：删除操作只发生在从数据库取出key的时候发生，而且只删除当前key，所以对CPU时间的占用是比较少的，而且此时的删除是已经到了非做不可的地步（如果此时还不删除的话，我们就会获取到了已经过期的key了）
缺点：若大量的key在超出超时时间后，很久一段时间内，都没有被获取过，那么可能发生内存泄露（无用的垃圾占用了大量的内存）

##### 定时删除
在设置key的过期时间的同时，为该key创建一个定时器，让定时器在key的过期时间来临时，对key进行删除。

优点：保证内存被尽快释放
缺点：若过期key很多，删除这些key会占用很多的CPU时间，在CPU时间紧张的情况下，CPU不能把所有的时间用来做要紧的事儿，还需要去花时间删除这些key，定时器的创建耗时，若为每一个设置过期时间的key创建一个定时器（将会有大量的定时器产生），性能影响严重
结论：此方法基本上没人用

#### Redis采用的过期策略

惰性删除+定期删除
持久化对过期key的处理
RDB对过期key的处理
过期key对RDB没有任何影响

1）从内存数据库持久化数据到RDB文件，持久化key之前，会检查是否过期，过期的key不进入RDB文件
2）从RDB文件恢复数据到内存数据库，数据载入数据库之前，会对key先进行过期检查，如果过期，不导入数据库（主库情况）

AOF对过期key的处理
过期key对AOF没有任何影响

1）从内存数据库持久化数据到AOF文件：当key过期后，还没有被删除，此时进行执行持久化操作（该key是不会进入aof文件的，因为没有发生修改命令）当key过期后，在发生删除操作时，程序会向aof文件追加一条del命令（在将来的以aof文件恢复数据的时候该过期的键就会被删掉）
2）AOF重写：重写时，会先判断key是否过期，已过期的key不会重写到aof文件

#### 内存淘汰策略

当redis内存超出物理内存限制时，会和磁盘产生swap，这种情况性能极差，一般是不允许的。通过设置 maxmemory 限制最大使用内存。超出限制时，根据redis提供的几种内存淘汰机制让用户自己决定如何腾出新空间以提供正常的读写服务。
noeviction：当内存使用超过配置的时候会返回错误，不会驱逐任何键（默认策略，不建议使用）
allkeys-lru：加入键的时候，如果过限，首先通过LRU算法驱逐最久没有使用的键
volatile-lru：加入键的时候如果过限，首先从设置了过期时间的键集合中驱逐最久没有使用的键（不建议使用）
allkeys-random：加入键的时候如果过限，从所有key随机删除
volatile-random：加入键的时候如果过限，从过期键的集合中随机驱逐（不建议使用）
volatile-ttl：从配置了过期时间的键中驱逐马上就要过期的键
volatile-lfu：从所有配置了过期时间的键中驱逐使用频率最少的键
allkeys-lfu：从所有键中驱逐使用频率最少的键

#### 

### Redis分布式锁如何续期?

## 四、redis为什么快

#### redis为什么快todo

1. redis是纯内存操作：数据存放在内存中，内存的响应时间大约是100纳秒，这是Redis每秒万亿级别访问的重要基础。
2. 非阻塞I/O：
3. 再加上Redis自身的事件处理模型将epoll中的连接，读写，关闭都转换为了时间，不在I/O上浪费过多的时间。
4. 单线程避免了线程切换和竞态产生的消耗。

IO复用模型（Linux下的select、poll和epoll就是干这个的。将用户socket对应的fd（File Discriptor文件描述符）注册进epoll，然后epoll帮你监听哪些socket上有消息到达，这样就避免了大量的无用操作。此时的socket应该采用非阻塞模式。这样，整个过程只在调用select、poll、epoll这些调用的时候才会阻塞，收发客户消息是不会阻塞的，整个进程或者线程就被充分利用起来，这就是事件驱动，所谓的reactor模式。）



## 五、分布式锁

### redlock分布式锁原理

1. 获取当前时间（毫秒数）。
2. 按顺序依次向N个Redis节点执行获取锁的操作。SETNX
3. 计算整个获取锁的过程总共消耗了多长时间，计算方法是用当前时间减去第1步记录的时间
4. 如果最终获取锁成功了，那么这个锁的有效时间应该重新计算
5. 如果最终获取锁失败了,那么客户端应该立即向所有Redis节点发起释放锁的操作

### Redisson实现的分布式锁原理

RLock lock = redisson.getLock("myLock");

如果该客户端面对的是一个redis cluster集群

1. 首先会根据hash节点选择一台机器。
2. 会发送一段lua脚本到redis上
   if判断语句，就是用“exists myLock”命令判断一下，如果你要加锁的那个锁key不存在的话，你就进行加锁。
   如何加锁呢？很简单，用下面的命令：
   hset myLock
   8743c9c0-0795-4907-87fd-6c719a6b4586:1 1
   "8743c9c0-0795-4907-87fd-6c719a6b4586:1"代表客户端的id
   通过这个命令设置一个hash数据结构，这行命令执行后，会出现一个类似下面的数据结构：

```bash
myLock{
	"8743c9c0-0795-4907-87fd-6c719a6b4586:1":1
}
```

上述就代表“8743c9c0-0795-4907-87fd-6c719a6b4586:1”这个客户端对“myLock”这个锁key完成了加锁。

3. 锁key的默认生存时间，默认30秒。接着会执行“pexpire myLock 30000”命令，设置myLock这个锁key的生存时间是30秒。

锁互斥机制
如果客户端2来尝试加锁，执行了同样的一段lua脚本

1. 第一个if判断会执行“exists myLock”，发现myLock这个锁key已经存在了。
2. 接着第二个if判断，判断一下，myLock锁key的hash数据结构中，是否包含客户端2的ID，但是明显不是的，因为那里包含的是客户端1的ID。
3. 客户端2会获取到pttl myLock返回的一个数字，这个数字代表了myLock这个锁key的剩余生存时间。比如还剩15000毫秒的生存时间。
4. 此时客户端2会进入一个while循环，不停的尝试加锁。

#### watch dog自动延期机制

客户端1加锁的锁key默认生存时间才30秒，如果超过了30秒，客户端1还想一直持有这把锁，怎么办呢？
简单！只要客户端1一旦加锁成功，就会启动一个watch dog看门狗，他是一个后台线程，会每隔10秒检查一下，如果客户端1还持有锁key，那么就会不断的延长锁key的生存时间。

## 六、redis应用

### redis 模糊删除key

edis-cli KEYS "pattern" | xargs redis-cli DEL 

Redis keys命令支持模式匹配，但是del命令不支持模式匹配，有时候需要根据一定的模式来模糊删除key，这时只能结合shell命令来完成了。 具体命令是： 

redis-cli KEYS "pattern" | xargs redis-cli DEL 
其中pattern是keys命令支持的模式，这样就可以模糊删除key了。服务器上测试删除150万条数据的效率也是很高的。 

问题是只能删除单机，集群模式下无法模糊删除；
【解决方案】
第一种方式：
首先通过scan在三个主节点(假设集群是三主三从)扫描出匹配前缀的keys
redis-cli -c -h $host1 -p $port1 --scan --pattern "usertags_uid_*" > /tmp/node1.log
redis-cli -c -h $host2 -p $port2 --scan --pattern "usertags_uid_*" > /tmp/node2.log
redis-cli -c -h $host3 -p $port3 --scan --pattern "usertags_uid_*" > /tmp/node3.log
然后写个简单脚本进行删除
第二种方式：
使用redis的pipeline进行删除
首先通过scan扫描出匹配前缀的keys
redis-cli -c -h $host1 -p $port1 --scan --pattern "usertags_uid_*" > /tmp/node1.log
redis-cli -c -h $host2 -p $port2 --scan --pattern "usertags_uid_*" > /tmp/node2.log
redis-cli -c -h $host3 -p $port3 --scan --pattern "usertags_uid_*" > /tmp/node3.log
然后把这些keys导入mysql中
最后利用redis协议删除

### 分布式缓存Redis之Pipeline

Redis 的 pipeline(管道)功能在命令行中没有，但 redis 是支持 pipeline 的，而且在各个语言版的 client 中都有相应的实现。 由于网络开销延迟，就算 redis server 端有很强的处理能力，也会由于收到的 client 消息少，而造成吞吐量小。当 client 使用 pipelining 发送命令时，redis server 必须将部分请求放到队列中（使用内存），执行完毕后一次性发送结果；如果发送的命令很多的话，建议对返回的结果加标签，当然这也会增加使用的内存；

Pipeline 在某些场景下非常有用，比如有多个 command 需要被“及时的”提交，而且他们对相应结果没有互相依赖，对结果响应也无需立即获得，那么 pipeline 就可以充当这种“批处理”的工具；而且在一定程度上，可以较大的提升性能，性能提升的原因主要是 TCP 连接中减少了“交互往返”的时间。

不过在编码时请注意，pipeline 期间将“独占”链接，此期间将不能进行非“管道”类型的其他操作，直到 pipeline 关闭；如果你的 pipeline 的指令集很庞大，为了不干扰链接中的其他操作，你可以为 pipeline 操作新建 Client 链接，让 pipeline 和其他正常操作分离在2个 client 中。不过 pipeline 事实上所能容忍的操作个数，和 socket-output 缓冲区大小/返回结果的数据尺寸都有很大的关系；同时也意味着每个 redis-server 同时所能支撑的 pipeline 链接的个数，也是有限的，这将受限于 server 的物理内存或网络接口的缓冲能力。

### 命中率?todo

我们就可以有一个大概的公式，来大致计算命中率是否算“低”。
读盘总量/客户机上机总时长*（100%-缓存命中率）≤存放游戏磁盘的随机读取速度。
如果结果是这样的，那么这个命中率就不低，如果结果是相反的，那么说明命中率确实低了。

### 怎么查看缓存集群热点问题？

怎么查看缓存集群热点问题？可以通过哪些工具查看？怎么解决这个问题？

1.客户端收集上报
改动 Redis SDK，记录每个请求，定时把收集到的数据上报，然后由一个统一的服务进行聚合计算。方案直观简单，但没法适应多语言架构，一方面多语言 SDK 对齐是个问题，另外一方面后期 SDK 的维护升级会面临比较大的困难，成本很高。

2.代理层收集上报
如果所有的 Redis 请求都经过代理的话，可以考虑改动 Proxy 代码进行收集，思路与客户端基本类似。该方案对使用方完全透明，能够解决客户端 SDK 的语言异构和版本升级问题，不过开发成本会比客户端高些。
饿了么使用此方案

3.Redis 数据定时扫描
Redis 在 4.0 版本之后添加了 hotkeys 查找特性[1]，可以直接利用 redis-cli --hotkeys 获取当前 keyspace 的热点 key，实现上是通过 scan + object freq 完成的。该方案无需二次开发，能够直接利用现成的工具，但由于需要扫描整个 keyspace，实时性上比较差，另外扫描耗时与 key 的数量正相关，如果 key 的数量比较多，耗时可能会非常长。

4.Redis 节点抓包解析
在可能存在热 key 的节点上(流量倾斜判断)，通过 tcpdump 抓取一段时间内的流量并上报，然后由一个外部的程序进行解析、聚合和计算。该方案无需侵入现有的 SDK 或者 Proxy 中间件，开发维护成本可控，但也存在缺点的，具体是热 key 节点的网络流量和系统负载已经比较高了，抓包可能会情况进一步恶化。

5.storm/spark流式计算，上报给zk
服务节点通过监听zk来将热点key放入到本地缓存中

6.采用LFU，然后使用redis自带的命令即可查看

### redis端口号6379

硫酸气球

## 七、本地缓存

### guava cache

#### gauva cache是用的堆内缓存、还是堆外缓存、还是磁盘缓存

gauva cache堆内内存


#### expireAfterAccess和expireAfterWrite原理是什么？

不管使用哪一种缓存过期策略，guava cache都会帮我们确保，同一个key，同时只有一个线程去执行刷新，避免了热点key的大量请求给后端造成的性能压力。但是这样还不够。

expireAfterAccess和expireAfterWrite在缓存过期后，由一个请求去执行后端查询，其他请求都在阻塞等待结果返回，如果同时有大量的请求阻塞，那么可能会产生大影响。

guava cache总结
    内部数据结构
        类似于ConcurrentHashMap，Segement数组 + Entry链表数组
    如何管理数据
        1、被动清理。只有当访问数据（比如get操作）时，guavacache才会去清理数据.
        2、清理两方面的数据
            非强引用回收
            （1）当key或value为非强引用类型（弱引用或软引用）的对象被GC回收后，其对应的entry会被清理（清理当前segment中<所有(reference queues队列中所有的)>非强引用被回收的数据）。
            有效期判断
            （2）当数据失效时。 当前数据失效时，会清理整个segment中所有失效的数据（不是只清理当前失效的这个key）。
            清理时，线程会尝试获得锁，只有获得锁的线程才会去清理，其他线程得不到锁，则直接返回.
            （3）基于size或weight的LRU清理
            guava cache是基于LRU算法实现的数据清理，guava cache中有一个accessQueue队列和一个recencyQueue队列，当数据被get读取时，数据节点被放入recencyQueue队列的尾部，当数据被加载出来（包括写操作）时，如果对应的key之前已经存在，则会根据recencyQueue更新accessQueue中元素的顺序（数据顺序按时间排序），然后将新值放入accessQueue尾部，如果key不存在，则直接加入到accessQueue尾部。
           当cache中超过阈值需要清理时，则从accessQueue的头部开始清理，这样就实现了LRU
        3、失效判断
            设置了expiresAfterAccess，并且超过expiresAfterAccess时间没有访问数据（读、写），则数据失效。
            设置了expiresAfterWrite，并且超过expiresAfterWrite时间没有更新数据（写），则数据失效。
            数据失效时，清理数据，并去load数据
        4、数据刷新
            设置了refreshAfterWrite，并且超过refreshAfterWrite时间没有更新数据，则调用reload刷新数据（guava内部默认是同步调用，如果要实现异步，需要重写reload操作）
        5、清理刷新数据流程
            1、首先，访问数据时，如果能通过key找到对应的entry，如果entry对象中对应的key或value为null，则表示是由于gc回收（回收非强引用）导致的，此时会触发对cache中这类数据的主动清理
            2、接着判断通过key得到的entry是否超过expiresAfterAccess，如果是则过期，触发主动清理过期数据的操作
            3、然后判断entry是否超过expiresAfterWrite，如果是则过期，触发清理过程
            4、如果经过上面的操作，数据被清理（返回null），则最后调用load()加载数据。
            5、如果经过123，数据不为空，则判断refreshAfterWrite，如果满足，则调用reload()刷新数据

    并发处理
        guava cache内部没有实现多线程回收数据，而是在访问数据时主动去清理（一般以segment为单位清理），目的是为了减小多线程带来的开销
        load()时, 只会有一个线程去执行load()，其他线程会被阻塞，直到数据加载成功。
        reload()时，只会有一个线程去执行reload()，其他线程会返回oldValue()，guava内部默认是同步调用reload。
        因此guava cache推荐重写reload()方法，其默认实现是同步调用load()，需要自己实现多线程处理（比如在reload中搞一个线程池）
    三个时间参数
        cache的时间参数一般用于控制数据占用空间和数据的实时性
        expiresAfterAccess用于管理数据空间，清理不常用的数据
        expiresAfterWrite和refreshAfterWrite则用于管理数据的时效性。
        不同的是当expireAfterWrite过期时，会重新同步的load()数据，而refreshAfterWrite过期时，会reload()数据，reload可以实现异步加载。
        因此在数据时效性上expireAfterWrite和refreshAfterWrite要比expireAfterAccess更灵敏 ， expireAfterAccess可设置的大一些 expireAfterWrite不可设置的太小，否则会造成业务线程同步去拉取数据的频率执行，如果要实现全异步刷新数据，refreshAfterWrite要设置的比前两个值都小，并且实现reload的异步加载。

















**Redis**

- 项目中使用的 Redis 版本
- Redis 在项目中的使用场景
- Redis 怎么保证高可用
- Redis 的选举流程
- Redis 和 Memcache 的区别
- Redis 的集群模式
- Redis 集群要增加分片，槽的迁移怎么保证无损
- Redis 分布式锁的实现
- Redis 删除过期键的策略
- Redis 的内存淘汰策略
- Redis 的 Hash 对象底层结构
- Redis 中 Hash 对象的扩容流程
- Redis 的 Hash 对象的扩容流程在数据量大的时候会有什么问题吗
- Redis 的持久化机制有哪几种
- RDB 和 AOF 的实现原理、优缺点
- AOF 重写的过程
- 哨兵模式的原理
- 使用缓存时，先操作数据库还是先操作缓存
- 为什么是让缓存失效，而不是更新缓存
- 缓存穿透、缓存击穿、缓存雪崩
- 更新缓存的几种设计模式