

## 介绍

G1 GC，全称Garbage-First Garbage Collector，通过-XX:+UseG1GC参数来启用，作为体验版随着JDK 6u14版本面世，在JDK 7u4版本发行时被正式推出，相信熟悉JVM的同学们都不会对它感到陌生。在JDK 9中，G1被提议设置为默认垃圾收集器（JEP 248）。在官网中，是这样描述G1的： 

> The Garbage-First (G1) collector is a server-style garbage collector, targeted for multi-processor machines with large memories. It meets garbage collection (GC) pause time goals with a high probability, while achieving high throughput. The G1 garbage collector is fully supported in Oracle JDK 7 update 4 and later releases. The G1 collector is designed for applications that: 
>
> * Can operate concurrently with applications threads like the CMS collector. 
> * Compact free space without lengthy GC induced pause times.
> * Need more predictable GC pause durations. 
> * Do not want to sacrifice a lot of throughput performance. 
> * Do not require a much larger Java heap.

从官网的描述中，我们知道G1是一种服务器端的垃圾收集器，应用在多处理器和大容量内存环境中，在实现高吞吐量的同时，尽可能的满足垃圾收集暂停时间的要求。它是专门针对以下应用场景设计的: 

* 像CMS收集器一样，能与应用程序线程并发执行。 
* 整理空闲空间更快。 
* 需要GC停顿时间更好预测。
* 希望牺牲大量的吞吐性能。
* 不需要更大的Java Heap。

G1收集器的设计目标是取代CMS收集器，它同CMS相比，在以下方面表现的更出色：

* G1是一个有整理内存过程的垃圾收集器，不会产生很多内存碎片。 
* G1的Stop The World(STW)更可控，G1在停顿时间上添加了预测机制，用户可以指定期望停顿时间。





## 评价指标

  1. 吞吐量
    指在应用程序运行周期内，应用程序运行总时间与系统总运行时间的比值。系统总运行时间=应用程序耗时+GC耗时。比如系统运行100分钟，GC耗时1分钟那么系统的吞吐量为99%。
  2. 垃圾回收负载
    与吞吐量相反，即垃圾回收耗时/系统总运行时间。
    3. 停顿时间
    指垃圾回收运行时，应用程序的暂停时间，独占式回收器停段时间会大于并发式收集器的时间，但是其总体效率可能高于并发式收集器，所以一般独占式收集器吞吐量会高于并发式收集器
    4. 垃圾回收频率
    指垃圾回收多长时间执行一次，一边来讲，回收频率越低越好，通常增加堆内存可以有效降低垃圾回收频率，但是此操作可能导致停顿时间增加。
    5. 反应时间
    指当一个对象成为垃圾后，多长时间内会被清理，释放内存。
    6. 堆分配
    不同的垃圾回收器对对内存的分配方式不同，一个良好的垃圾回收器应该有一个合理的堆内存区间划分。

通常情况下，很难让一个应用程序所有指标都达到最优，因此，只能根据自身系统的关注点，选择合理的垃圾回收器和回收策略。



>To find and evaluate performance bottlenecks, we need to know some definitions of performance metrics. For JVM tuning, we need to know the three following definitions and use these metrics as our base of evaluation:
>
>- Throughput: It is one of the important metrics. Throughput refers to the highest possible performance that the garbage collector allows applications to achieve, without considering the pause time or memory consumption caused by garbage collection.
>- Latency: Latency measures how much pause time resulting from garbage collection is reduced to avoid application vibrations during the running process.
>- Memory usage: It refers to the amount of memory required for the garbage collector to run smoothly.





调优工具
一、jps：虚拟机进程状况工具
二、jstat：虚拟机统计信息监视工具
jstat是jvm的实时监控的工具。包括类加载信息，及时编译，gc等等。
三、jmap：Java内存印象工具
四、jhat：虚拟机堆转储快照分析工具
五、jstack：Java堆栈跟踪工具
jstack pid会打印java进程中线程的轨迹，包括线程id，线程栈，状态等等
六、jinfo：Java配置信息工具



## JDK8变化

https://blog.csdn.net/s674334235/article/details/103106482





年轻代收集：存活对象从年轻代晋升到老年代的过程叫做“tenuring”对象，老化的阈值称为“任期阈值”（tenuring threshold），对象晋升到Survivor Region或老年代Region 的过程是在GC线程的晋升本地分配缓冲区（promotion local allocation buffer，PLAB）中进行的。