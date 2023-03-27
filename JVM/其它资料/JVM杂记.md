

**有一个疑问，消息进入老年代，出现堆积，为何会导致YGC时间过长呢？**

按着文章中的叙述，回答这个问题。

1. 在YGC阶段，涉及到垃圾标记的过程，从GCRoot开始标记。
2. 因为YGC不涉及到老年代的回收，一旦从GCRoot扫描到引用了老年代对象时，就中断本次扫描。这样做可以减少扫描范围，加速YGC。
3. 存在被老年代对象引用的年轻代对象，它们没有被GCRoot直接或者间接引用。
4. YGC阶段中的old-gen scanning即用于扫描被老年代引用的年轻代对象。
5. old-gen scanning扫描时间与老年代内存占用大小成正比。
6. 得到结论，老年代内存占用增大会导致YGC时间变长。

总的来说，将消息缓存在JVM内存会对垃圾回收造成一定影响：

1. 消息最初缓存到年轻代，会增加YGC的频率。
2. 消息被提升到老年代，会增加FGC的频率。
3. 老年代的消息增长后，会延长old-gen scanning时间，从而增加YGC耗时。

文章使用「堆外内存」减少了消息对JVM内存的占用，并使用基于Netty的网络层框架，达到了理想的YGC时间。

注：Netty中也使用了堆外内存。

**在JAVA中，可以通过Unsafe和NIO包下的ByteBuffer来操作堆外内存。**

考虑堆外内存的垃圾回收机制，需要了解以下两个问题：

1. 堆外内存会溢出么？
2. 什么时候会触发堆外内存回收？

### 问题一

通过修改JVM参数：-XX:MaxDirectMemorySize=40M，将最大堆外内存设置为40M。

既然堆外内存有限，则必然会发生内存溢出。

为模拟内存溢出，可以设置JVM参数：-XX:+DisableExplicitGC，禁止代码中显式调用System.gc()。

可以看到出现OOM。

得到的结论是，堆外内存会溢出，并且其垃圾回收依赖于代码显式调用System.gc()。

参考：[JAVA堆外内存](https://link.jianshu.com?t=http%3A%2F%2Fwww.cnblogs.com%2Fmoonandstar08%2Fp%2F5107648.html)

### 问题二

关于堆外内存垃圾回收的时机，首先考虑堆外内存的分配过程。

JVM在堆内只保存堆外内存的引用，用DirectByteBuffer对象来表示。

每个DirectByteBuffer对象在初始化时，都会创建一个对应的Cleaner对象。

这个Cleaner对象会在合适的时候执行unsafe.freeMemory(address)，从而回收这块堆外内存。

当DirectByteBuffer对象在某次YGC中被回收，只有Cleaner对象知道堆外内存的地址。

当下一次FGC执行时，Cleaner对象会将自身Cleaner链表上删除，并触发clean方法清理堆外内存。

此时，堆外内存将被回收，Cleaner对象也将在下次YGC时被回收。

如果JVM一直没有执行FGC的话，无法触发Cleaner对象执行clean方法，从而堆外内存也一直得不到释放。

其实，在ByteBuffer.allocateDirect方式中，会主动调用System.gc()强制执行FGC。

JVM觉得有需要时，就会真正执行GC操作。



# 三：为什么用堆外内存？

堆外内存的使用场景非常巧妙。

第三方堆外缓存管理包ohc(**o**ff-**h**eap-**c**ache)给出了详细的解释。

摘了其中一段。

> When using a very huge number of objects in a very large heap, Virtual machines will suffer from increased GC pressure since it basically has to inspect each and every object whether it can be collected and has to access all memory pages. A cache shall keep a hot set of objects accessible for fast access (e.g. omit disk or network roundtrips). The only solution is to use native memory - and there you will end up with the choice either to use some native code (C/C++) via JNI or use direct memory access.

大概的意思如下：

考虑使用缓存时，本地缓存是最快速的，但会给虚拟机带来GC压力。

使用硬盘或者分布式缓存的响应时间会比较长，这时候「堆外缓存」会是一个比较好的选择。

参考：[OHC - An off-heap-cache — Github](https://link.jianshu.com?t=https%3A%2F%2Fgithub.com%2Fsnazy%2Fohc)



# 四：如何用堆外内存？

那是否有一个包，支持分配堆外内存，又支持KV操作，还无需关心GC。

答案当然是有的。

有一个很知名的包，**Ehcache**。

**总体而言，使用堆外内存可以减少GC的压力，从而减少GC对业务的影响。**



作者：阿菜的博客
链接：https://www.jianshu.com/p/17e72bb01bf1



-------------



### G1收集器

G1是目前技术发展的最前沿成果之一，HotSpot开发团队赋予它的使命是未来可以替换掉JDK1.5中发布的CMS收集器。与CMS收集器相比G1收集器有以下特点：

1. **空间整合**，G1收集器采用标记整理算法，不会产生内存空间碎片。分配大对象时不会因为无法找到连续空间而提前触发下一次GC。
2. **可预测停顿**，这是G1的另一大优势，降低停顿时间是G1和CMS的共同关注点，但G1除了追求低停顿外，还能建立可预测的停顿时间模型，能让使用者明确指定在一个长度为N毫秒的时间片段内，消耗在垃圾收集上的时间不得超过N毫秒，这几乎已经是实时Java（RTSJ）的垃圾收集器的特征了。

上面提到的垃圾收集器，收集的范围都是整个新生代或者老年代，而G1不再是这样。使用G1收集器时，Java堆的内存布局与其他收集器有很大差别，它将整个Java堆划分为多个大小相等的独立区域（Region），虽然还保留有新生代和老年代的概念，但新生代和老年代不再是物理隔阂了，它们都是一部分（可以不连续）Region的集合。

![img](https://images2018.cnblogs.com/blog/774371/201808/774371-20180821141943623-188206691.jpg)

G1的新生代收集跟ParNew类似，当新生代占用达到一定比例的时候，开始出发收集。和CMS类似，G1收集器收集老年代对象会有短暂停顿。

收集步骤：

1、标记阶段，首先初始标记(Initial-Mark),这个阶段是停顿的(Stop the World Event)，并且会触发一次普通Mintor GC。对应GC log:GC pause (young) (inital-mark)

2、Root Region Scanning，程序运行过程中会回收survivor区(存活到老年代)，这一过程必须在young GC之前完成。

3、Concurrent Marking，在整个堆中进行并发标记(和应用程序并发执行)，此过程可能被young GC中断。在并发标记阶段，若发现区域对象中的所有对象都是垃圾，那个这个区域会被立即回收(图中打X)。同时，并发标记过程中，会计算每个区域的对象活性(区域中存活对象的比例)。

![img](https://images2018.cnblogs.com/blog/774371/201808/774371-20180821141957803-715983842.jpg)

4、Remark, 再标记，会有短暂停顿(STW)。再标记阶段是用来收集 并发标记阶段 产生新的垃圾(并发阶段和应用程序一同运行)；G1中采用了比CMS更快的初始快照算法:snapshot-at-the-beginning (SATB)。

5、Copy/Clean up，多线程清除失活对象，会有STW。G1将回收区域的存活对象拷贝到新区域，清除Remember Sets，并发清空回收区域并把它返回到空闲区域链表中。

![img](https://images2018.cnblogs.com/blog/774371/201808/774371-20180821142011764-2146649376.jpg)

6、复制/清除过程后。回收区域的活性对象已经被集中回收到深蓝色和深绿色区域。

![img](https://images2018.cnblogs.com/blog/774371/201808/774371-20180821142025937-1724945710.jpg)

 

 

 



----------------



在Java语言中，GC Roots包括：

- 虚拟机栈中引用的对象
- 方法区中类静态属性引用的对象
- 方法区中常量引用的对象
- 本地方法栈引用的对象







**7、收集器设置推荐**

```
    关于收集器的选择 JVM 给了三种选择：串行收集器、并行收集器、并发收集器，但是串行收集器只适用于小数据量的情况，所以这里的选择主要针对并行收集器和并发收集器。 默认情况下，JDK5.0 以前都是使用串行收集器，如果想使用其他收集器需要在启动时加入相 应参数。JDK5.0 以后，JVM 会根据当前系统配置进行判断。 

    
    常见配置： 

        并行收集器主要以到达一定的吞吐量为目标，适用于科学计算和后台处理等。 

        -Xmx3800m -Xms3800m -Xmn2g -Xss128k -XX:+UseParallelGC -XX:ParallelGCThreads=20 

        使用 ParallelGC 作为并行收集器， GC 线程为 20（CPU 核心数>=20 时），内存问题根据硬件配置具体提供。建议使用物理内存的 80%左右作为 JVM 内存容量。 


        -Xmx3550m -Xms3550m -Xmn2g -Xss128k -XX:+UseParallelGC -XX:ParallelGCThreads=20 -XX:+UseParallelOldGC 

        指定老年代收集器，在JDK5.0之后的版本，ParallelGC对应的全收集器就是ParallelOldGC。 可以忽略 

        
        -Xmx3550m -Xms3550m -Xmn2g -Xss128k -XX:+UseParallelGC -XX:MaxGCPauseMillis=100 

        指定 GC 时最大暂停时间。单位是毫秒。每次 GC 最长使用 100 毫秒。可以尽可能提高工作线程的执行资源。 


        -Xmx3550m -Xms3550m -Xmn2g -Xss128k -XX:+UseParallelGC -XX:MaxGCPauseMillis=100 -XX:+UseAdaptiveSizePolicyUseAdaptiveSizePolicy 
        是提高年轻代 GC 效率的配置。次收集器执行效率。 

        并发收集器主要是保证系统的响应时间，减少垃圾收集时的停顿时间。适用于应用服务器、电信领域、互联网领域等。 

        -Xmx3550m -Xms3550m -Xmn2g -Xss128k -XX:ParallelGCThreads=20 -XX:+UseConcMarkSweepGC -XX:+UseParNewGC 

        指定年轻代收集器为 ParNew，年老代收集器 ConcurrentMarkSweep，并发 GC 线程数为 20（CPU 核心>=20），并发 GC 的线程数建议使用（CPU 核心数+3）/4 或 CPU 核心数【不推荐使用】。 

        
        -Xmx3550m -Xms3550m -Xmn2g -Xss128k -XX:+UseConcMarkSweepGC -XX:CMSFullGCsBeforeCompaction=5 -XX:+UseCMSCompactAtFullCollection 

        CMSFullGCsBeforeCompaction=5 执行5次 GC 后，运行一次内存的整理。 UseCMSCompactAtFullCollection 执行老年代内存整理。可以避免内存碎片，提高 GC 过 

程中的效率，减少停顿时间。
```

 

  **8、简单总结** 

```
    年轻代大小选择 
        响应时间优先的应用：尽可能设大，直到接近系统的最低响应时间限制（根据实际情况选择）。在此种情况下，年轻代收集发生的频率也是最小的。同时，减少到达年老代的对象。 

    吞吐量优先的应用：尽可能的设置大，可能到达 Gbit 的程度。因为对响应时间没有要求，垃圾收集可以并行进行，一般适合 8CPU 以上的应用。
    
    年老代大小选择 
        响应时间优先的应用：年老代使用并发收集器，所以其大小需要小心设置，一般要考虑并发会话率和会话持续时间等一些参数。如果堆设置小了，可以会造成内存碎片、高回收频率以及应用暂停而使用传统的标记清除方式；如果堆大了，则需要较长的收集时间。最优化的方案，一般需要参考以下数据获得： 
        并发垃圾收集信息 
        持久代并发收集次数 
        传统 GC 信息 
        花在年轻代和年老代回收上的时间比例 
        减少年轻代和年老代花费的时间，一般会提高应用的效率 

    吞吐量优先的应用：一般吞吐量优先的应用都有一个很大的年轻代和一个较小的年老代。原因是，这样可以尽可能回收掉大部分短期对象，减少中期的对象，而年老代存放长期存活对象。 

    较小堆引起的碎片问题：
        因为年老代的并发收集器使用标记、清除算法，所以不会对堆进行压缩。当收集器回收时，他会把相邻的空间进行合并，这样可以分配给较大的对象。但是，当堆空间较小时，运行一段时间以后，就会出现“碎片”，如果并发收集器找不到足够的空间，那么并发收集器将会停止，然后使用传统的标记、整理方式进行回收。如果出现“碎 
片”，可能需要进行如下配置：
            -XX:+UseCMSCompactAtFullCollection：使用并发收集器时，开启对年老代的压缩。 
            -XX:CMSFullGCsBeforeCompaction=0：上面配置开启的情况下，这里设置多少次 Full GC 后，对年老代进行压缩
```



--------

发生这样的情况的概率并不高, 一般情况下有两种类型的 oom kill。

1.由于 pod 内进程超出了 pod 指定 Limit 限制的值, 将导致 oom kill, 此时 pod 退出的 Reason 会显示 OOMKilled。

2.另一种情况是 pod 内的进程给自己设置了可用内存, 比如 jvm 内存限制设置为2G, pod Limit 设置为6G, 此时由于程序的原因导致内存使用超过2G 时, 也会引发 oom kill。

这两种内存溢出的 kill 区别是第一种原因直接显示在 pod 的 Event 里; 第二种你在 Event 里找不到, 在宿主机的 dmesg 里面可以找到 invoked oom-killer 的日志

