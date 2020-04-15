比如，deployment.yaml的部分内容如下：

...
env:
  - name: XXX_JAVA_OPTS
    value: "-Xms500m -Xmx950m -XX:MaxNewSize=250m -XX:+UseConcMarkSweepGC"
...
那镜像里的处理脚本如下：

if [ "$XXX_JAVA_OPTS" ];then
    JAVA_OPTS="$JAVA_OPTS $XXX_JAVA_OPTS"
else
    JAVA_OPTS="$JAVA_OPTS -Xms800m -Xmx900m -XX:MaxNewSize=256m"
    JAVA_OPTS="$JAVA_OPTS -XX:+UseConcMarkSweepGC"
fi
 

也可如下：

复制代码
IFS_old=$IFS
IFS= $'\n'
if [ $XXX_JAVA_OPTS ];then
    JAVA_OPTS="$JAVA_OPTS $XXX_JAVA_OPTS"
else
    JAVA_OPTS="$JAVA_OPTS -Xms800m -Xmx900m -XX:MaxNewSize=256m"
    JAVA_OPTS="$JAVA_OPTS -XX:+UseConcMarkSweepGC"
Fi
IFS=$IFS_old
复制代码

------

-Xms512m -Xmx1500m -XX:ReservedCodeCacheSize=240m -XX:+UseConcMarkSweepGC -XX:SoftRefLRUPolicyMSPerMB=50 -ea -Dsun.io.useCanonCaches=false -Djava.net.preferIPv4Stack=true -XX:+HeapDumpOnOutOfMemoryError -XX:-OmitStackTraceInFastThrow -XX:MaxJavaStackTraceDepth=-1 -agentlib:yjpagent64=probe_disable=*,disablealloc,disabletracing,onlylocal,disableexceptiontelemetry,delay=10000,sessionname=IntelliJIdea2017.2 -Xverify:none



在Java虚拟机中，方法区是可供各线程共享的运行时内存区域。
在Java虚拟机规范中，不限定实现方法区的内存位置和编译代码的管理策略。所以不同的JVM厂商，针对自己的JVM可能有不同的方法区实现方式。HotSpot虚拟机使用者更愿意将方法区称为老年代。方法区和永久代的关系很像Java中接口和类的关系，类实现了接口，而永久代就是HotSpot虚拟机对虚拟机规范中方法区的一种实现方式。

在不同的JDK版本中，方法区中存储的数据是不一样的。
1、在JDK1.6及之前，运行时常量池是方法区的一个部分，同时方法区里面存储了类的元数据信息、静态变量、即时编译器编译后的代码（比如spring使用IOC或者AOP创建bean时，或者使用cglib，反射的形式动态生成class信息等）等。
2、在JDK1.7及以后，JVM已经将运行时常量池从方法区中移了出来，在JVM堆开辟了一块区域存放常量池。
3、在1.8之后已经取消了永久代，改为元空间，类的元信息被存储在元空间中。元空间没有使用堆内存，而是与堆不相连的本地内存区域。所以，理论上系统可以使用的内存有多大，元空间就有多大，所以不会出现永久代存在时的内存溢出问题。

常量池指的是在编译期被确定，并被保存在已编译的.class文件中的一些数据。
除了包含代码中所定义的各种基本类型(如：int、long等等)和对象型(如String及数组)的常量值(final)还包含一些以文本形式出现的符号引用，比如：
#类和接口的全限定名
#字段的名称的描述符
#方法和名称的描述符


Class Loader 类加载器类加载器的作用是加载类文件到内存，Class Loader只管加载，只要符合文件结构就加载，至于说能不能运行，则不是它负责的，那是
由Execution Engine负责的。
Execution Engine 执行引擎执行引擎也叫做解释器(Interpreter)，负责解释命令，提交操作系统执行。
Native Interface本地接口本地接口的作用是融合不同的编程语言为Java所用。在内存中专门开辟了一块区域处理标记为native的代码，它的具体做法是Native Method Stack中登记native方法，在Execution Engine执行时加载native libraies。



-XX:G1MixedGCLive ThresholdPercent	会被MixGC的Region中存活对象占比



-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.port=1100 -Dcom.sun.management.jmxremote.rmi.port=1100 -Djava.rmi.server.hostname=localhost


创建和同步容灾云主机.png
故障切换和重保护.png
切换和计划性切换.png
容灾演练.png
总体架构.png