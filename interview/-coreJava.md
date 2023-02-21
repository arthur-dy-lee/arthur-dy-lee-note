#### HashMap是怎么扩展长度？什么时候扩展

当hashmap中的元素个数超过数组大小*loadFactor时，就会进行数组扩容，loadFactor的默认值为0.75，也就是说，默认情况下，数组大小为16，那么当hashmap中元素个数超过16*0.75=12的时候，就把数组的大小扩展为2*16=32，即扩大一倍，然后重新计算每个元素在数组中的位置，而这是一个非常消耗性能的操作。

jdk1.8在链表长度大于等于8的时候，转为红黑树插入；当小于等于6的时候转成链表。

这边也可以引申到一个问题HashMap是先插入还是先扩容：HashMap初始化后首次插入数据时，先发生resize扩容再插入数据，之后每当插入的数据个数达到threshold时就会发生resize，此时是先插入数据再resize。

resize重新计算
h & (length - 1); //hash值&表长度
使用的是2次幂的扩展(指长度扩为原来2倍)，所以，元素的位置要么是在原位置，要么是在原位置再移动2次幂的位置。
扩充HashMap的时候，不需要像JDK1.7的实现那样重新计算hash，只需要看看原来的hash值新增的那个bit是1还是0就好了，是0的话索引没变，是1的话索引变成“原索引+oldCap”。oldCap是扩容的大小
这个设计确实非常的巧妙，既省去了重新计算hash值的时间，而且同时，由于新增的1bit是0还是1可以认为是随机的，因此resize的过程，均匀的把之前的冲突的节点分散到新的bucket了。这一块就是JDK1.8新增的优化点。有一点注意区别，JDK1.7中rehash的时候，旧链表迁移新链表的时候，如果在新表的数组索引位置相同，则链表元素会倒置。
https://blog.csdn.net/paincupid/article/details/107615433









-------------



####  1、java中在参数传递时有2种方式

按值传递：值传递是指在调用函数时将实际参数复制一份传递到函数中，这样在函数中如果对参数进行修改，将不会影响到实际参数。 

按引用传递：引用传递其实就弥补了上面说的不足，如果每次传参数的时候都复制一份的话，如果这个参数占用的内存空间太大的话，运行效率会很底下，所以引用传递就是直接把内存地址传过去，也就是说引用传递时，操作的其实都是源数据。

####  2、corePoolSize 、maximumPoolSize 

线程池的基本大小，即在没有任务需要执行的时候线程池的大小，并且只有在工作队列满了的情况下才会创建超出这个数量的线程。这里需要注意的是：在刚刚创建ThreadPoolExecutor的时候，线程并不会立即启动，而是要等到有任务提交时才会启动，除非调用了prestartCoreThread/prestartAllCoreThreads事先启动核心线程。再考虑到keepAliveTime和allowCoreThreadTimeOut超时参数的影响，所以没有任务需要执行的时候，线程池的大小不一定是corePoolSize。 

如果当前大小已经达到了corePoolSize 大小，就将新提交的任务提交到阻塞队列排队，等候处理workQueue.offer(command)； 

如果队列容量已达上限，并且当前大小poolSize没有达到maximumPoolSize，那么就新增线程来处理任务； 

如果队列已满，并且当前线程数目也已经达到上限，那么意味着线程池的处理能力已经达到了极限，此时需要拒绝新增加的任务。至于如何拒绝处理新增 

####  3、WeakHashMap 以及ThreadLocal

WeakHashMap的key是用的WeakReference，在没有其它强引用的情况下，下一次GC时才会被垃圾回收

- key和value都没有引用的时候，key都会被回收
- key如果有强引用的话，key是不会被回收的，value也不会被回收
- 如果value有强引用的话，key没有被强引用，也是照样可以被回收的

tomcat 使用的是线程池，在请求后，线程并不收回，所以ThreadLocal的key也没有被收回，因为key没有被收回，value也不会被收回。 

ThreadLocal最好要配合remove()方法来用。

####  4、Class.forName和classloader的区别  

java中Class.forName 和ClassLoader.loadClass( 都可用来对类进行加载。 

class.forName()前者除了将类的.class文件加载到jvm中之外，还会对类进行解释，执行类中的static块。

 而classLoader只干一件事情，就是将.class文件加载到jvm中，不会执行static中的内容,只有在newInstance才会去执行static块。 

####  5、session和cookie的区别和联系，session的生命周期，多个服务部署时session管理。  

cookie机制采用的是在客户端保持状态的方案，而session机制采用的是在服务器端保持状态的方案 



一、对于cookie：

①cookie是创建于服务器端

②cookie保存在浏览器端

③cookie的生命周期可以通过cookie.setMaxAge(2000);来设置，如果没有设置setMaxAge,

则cookie的生命周期当浏览器关闭的时候，就消亡了

④cookie可以被多个同类型的浏览器共享  可以把cookie想象成一张表

 

比较：

①存在的位置：

cookie 存在于客户端，临时文件夹中

session：存在于服务器的内存中，一个session域对象为一个用户浏览器服务

②安全性

cookie是以明文的方式存放在客户端的，安全性低，可以通过一个加密[算法](http://lib.csdn.net/base/datastructure)进行加密后存放

session存放于服务器的内存中，所以安全性好

③网络传输量

cookie会传递消息给服务器

session本身存放于服务器，不会有传送流量

④生命周期(以20分钟为例)

(1)cookie的生命周期是累计的，从创建时，就开始计时，20分钟后，cookie生命周期结束，

(2)session的生命周期是间隔的，从创建时，开始计时如在20分钟，没有访问session，那么session生命周期被销毁

但是，如果在20分钟内（如在第19分钟时）访问过session，那么，将重新计算session的生命周期

(3)关机会造成session生命周期的结束，但是对cookie没有影响

⑤访问范围

session为一个用户浏览器独享

cookie为多个用户浏览器共享



####  6、Java中的队列都有哪些，有什么区别。  

1、没有实现的阻塞接口的LinkedList： 实现了java.util.Queue接口和java.util.AbstractQueue接口 　　

内置的不阻塞队列： 

PriorityQueue 和 ConcurrentLinkedQueue 

2)实现阻塞接口的： 　　

java.util.concurrent 中加入了 BlockingQueue 接口和五个阻塞队列类。它实质上就是一种带有一点扭曲的 FIFO 数据结构。不是立即从队列中添加或者删除元素，线程执行操作阻塞，直到有空间或者元素可用。 五个队列所提供的各有不同： 　　

* ArrayBlockingQueue ：一个由数组支持的有界队列。 　　
* LinkedBlockingQueue ：一个由链接节点支持的可选有界队列。 　　
* PriorityBlockingQueue ：一个由优先级堆支持的无界优先级队列。 　　
* DelayQueue ：一个由优先级堆支持的、基于时间的调度队列。 　　
* SynchronousQueue ：一个利用 BlockingQueue 接口的简单聚集（rendezvous）机制。 



1.ArrayDeque, （数组双端队列）  2.PriorityQueue, （优先级队列）  3.ConcurrentLinkedQueue, （基于链表的并发队列）  4.DelayQueue, （延期阻塞队列）（阻塞队列实现了BlockingQueue接口）  5.ArrayBlockingQueue, （基于数组的并发阻塞队列）  6.LinkedBlockingQueue, （基于链表的FIFO阻塞队列）  7.LinkedBlockingDeque, （基于链表的FIFO双端阻塞队列）  8.PriorityBlockingQueue, （带优先级的无界阻塞队列）  9.SynchronousQueue （并发同步阻塞队列） 

####  7、newFixedThreadPool此种线程池如果线程数达到最大值后会怎么办，底层原理 



####  8、Java中两个线程是否可以同是访问一个对象的两个不同的synchronized方法?  

多个线程访问同一个类的synchronized方法时, 都是串行执行的 ! 就算有多个cpu也不例外 ! synchronized方法使用了类java的内置锁, 即锁住的是方法所属对象本身. 同一个锁某个时刻只能被一个执行线程所获取, 因此其他线程都得等待锁的释放. 因此就算你有多余的cpu可以执行, 但是你没有锁, 所以你还是不能进入synchronized方法执行, CPU因此而空闲. 如果某个线程长期持有一个竞争激烈的锁, 那么将导致其他线程都因等待所的释放而被挂起, 从而导致CPU无法得到利用, 系统吞吐量低下. 因此要尽量避免某个线程对锁的长期占有 ! 

为了避免对锁的竞争, 你可以使用锁分解,锁分段以及减少线程持有锁的时间, 如果上诉程序中的syncMethod1和syncMethod2方法是两个不相干的方法(请求的资源不存在关系), 那么这两个方法可以分别使用两个不同的锁 

```java
public class SyncMethod {
    private Object lock1 = new Object();
    private Object lock2 = new Object();

    public void syncMethod2() {
        synchronized (lock1) {
            try {
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@ (syncMethod2, 已经获取内置锁`SyncMethod.this`)");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@ (syncMethod2, 即将释放内置锁`SyncMethod.this`)");
        }
    }

    public void syncMethod1() {
        synchronized (lock2) {
            System.out.println("######################## (syncMethod1, 已经获取内置锁`SyncMethod.this`, 并即将退出)");
        }
    }
}
```



####  一个类的static构造方法加上synchronized之后的锁的影响。 

synchronized是对类的**当前实例（当前对象）**进行加锁，防止其他线程同时访问该类的该实例的所有synchronized块，注意这里是“类的当前实例”， 类的两个不同实例就没有这种约束了。

那么static synchronized恰好就是要控制类的所有实例的并发访问，static synchronized是限制多线程中该类的所有实例同时访问jvm中该类所对应的代码块。实际上，在类中如果某方法或某代码块中有 synchronized，那么在生成一个该类实例后，该实例也就有一个监视块，防止线程并发访问该实例的synchronized保护块，而static synchronized则是所有该类的所有实例公用得一个监视块，这就是他们两个的区别。也就是说synchronized相当于 this.synchronized，而static synchronized相当于AClass.synchronized.

####  9、JVM内存结构

内存结构：

1、指令计数器

2、堆

3、栈

4、方法区，包括类型的常量池、字段，方法信息、方法字节码。通常和永久区(Perm)关联在一起。

5、本地方法栈

####  10、JVM类加载过程





###### 11、java.有几种锁，锁的原理是什么

- 公平锁/非公平锁
- 可重入锁
- 独享锁/共享锁
- 互斥锁/读写锁
- 乐观锁/悲观锁
- 分段锁
- 偏向锁/轻量级锁/重量级锁
- 自旋锁

上面是很多锁的名词，这些分类并不是全是指锁的状态，有的指锁的特性，有的指锁的设计，下面总结的内容是对每个锁的名词进行一定的解释。

#####  公平锁/非公平锁

公平锁是指多个线程按照申请锁的顺序来获取锁。
非公平锁是指多个线程获取锁的顺序并不是按照申请锁的顺序，有可能后申请的线程比先申请的线程优先获取锁。有可能，会造成优先级反转或者饥饿现象。
对于Java `ReentrantLock`而言，通过构造函数指定该锁是否是公平锁，默认是非公平锁。非公平锁的优点在于吞吐量比公平锁大。
对于`Synchronized`而言，也是一种非公平锁。由于其并不像`ReentrantLock`是通过AQS的来实现线程调度，所以并没有任何办法使其变成公平锁。

##### 可重入锁

可重入锁又名递归锁，是指在同一个线程在外层方法获取锁的时候，在进入内层方法会自动获取锁。说的有点抽象，下面会有一个代码的示例。
对于Java `ReentrantLock`而言, 他的名字就可以看出是一个可重入锁，其名字是`Re entrant Lock`重新进入锁。
对于`Synchronized`而言,也是一个可重入锁。可重入锁的一个好处是可一定程度避免死锁。

##### 独享锁/共享锁

独享锁是指该锁一次只能被一个线程所持有。
共享锁是指该锁可被多个线程所持有。

对于Java `ReentrantLock`而言，其是独享锁。但是对于Lock的另一个实现类`ReadWriteLock`，其读锁是共享锁，其写锁是独享锁。
读锁的共享锁可保证并发读是非常高效的，读写，写读 ，写写的过程是互斥的。
独享锁与共享锁也是通过AQS来实现的，通过实现不同的方法，来实现独享或者共享。
对于`Synchronized`而言，当然是独享锁。

##### 互斥锁/读写锁

上面讲的独享锁/共享锁就是一种广义的说法，互斥锁/读写锁就是具体的实现。
互斥锁在Java中的具体实现就是`ReentrantLock`
读写锁在Java中的具体实现就是`ReadWriteLock`

##### 乐观锁/悲观锁

乐观锁与悲观锁不是指具体的什么类型的锁，而是指看待并发同步的角度。
悲观锁认为对于同一个数据的并发操作，一定是会发生修改的，哪怕没有修改，也会认为修改。因此对于同一个数据的并发操作，悲观锁采取加锁的形式。悲观的认为，不加锁的并发操作一定会出问题。
乐观锁则认为对于同一个数据的并发操作，是不会发生修改的。在更新数据的时候，会采用尝试更新，不断重新的方式更新数据。乐观的认为，不加锁的并发操作是没有事情的。

从上面的描述我们可以看出，悲观锁适合写操作非常多的场景，乐观锁适合读操作非常多的场景，不加锁会带来大量的性能提升。
悲观锁在Java中的使用，就是利用各种锁。
乐观锁在Java中的使用，是无锁编程，常常采用的是CAS算法，典型的例子就是原子类，通过CAS自旋实现原子操作的更新。

##### 分段锁

分段锁其实是一种锁的设计，并不是具体的一种锁，对于`ConcurrentHashMap`而言，其并发的实现就是通过分段锁的形式来实现高效的并发操作。
我们以`ConcurrentHashMap`来说一下分段锁的含义以及设计思想，`ConcurrentHashMap`中的分段锁称为Segment，它即类似于HashMap（JDK7与JDK8中HashMap的实现）的结构，即内部拥有一个Entry数组，数组中的每个元素又是一个链表；同时又是一个ReentrantLock（Segment继承了ReentrantLock)。
当需要put元素的时候，并不是对整个hashmap进行加锁，而是先通过hashcode来知道他要放在那一个分段中，然后对这个分段进行加锁，所以当多线程put的时候，只要不是放在一个分段中，就实现了真正的并行的插入。
但是，在统计size的时候，可就是获取hashmap全局信息的时候，就需要获取所有的分段锁才能统计。
分段锁的设计目的是细化锁的粒度，当操作不需要更新整个数组的时候，就仅仅针对数组中的一项进行加锁操作。

##### 偏向锁/轻量级锁/重量级锁

这三种锁是指锁的状态，并且是针对`Synchronized`。在Java 5通过引入锁升级的机制来实现高效`Synchronized`。这三种锁的状态是通过对象监视器在对象头中的字段来表明的。
偏向锁是指一段同步代码一直被一个线程所访问，那么该线程会自动获取锁。降低获取锁的代价。
轻量级锁是指当锁是偏向锁的时候，被另一个线程所访问，偏向锁就会升级为轻量级锁，其他线程会通过自旋的形式尝试获取锁，不会阻塞，提高性能。
重量级锁是指当锁为轻量级锁的时候，另一个线程虽然是自旋，但自旋不会一直持续下去，当自旋一定次数的时候，还没有获取到锁，就会进入阻塞，该锁膨胀为重量级锁。重量级锁会让其他申请的线程进入阻塞，性能降低。

##### 自旋锁

在Java中，自旋锁是指尝试获取锁的线程不会立即阻塞，而是采用循环的方式去尝试获取锁，这样的好处是减少线程上下文切换的消耗，缺点是循环会消耗CPU。

几种常见的复杂度关系

浙江省软件行业协会网上
通知公告-> 3/培训通知 4、审报通知
8971-9267



O(1)<O(log(n))<O(n)<O(nlogn)<O(1)<O(log⁡(n))<O(n)<O(nlog⁡n)<O(n2)<O(2n)<O(n!)<O(nn)O(n2)<O(2n)<O(n!)<O(nn) 

