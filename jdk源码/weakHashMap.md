# WeakHashMap 以及ThreadLocal的几点思考

## 1. WeakHashMap的key是用的WeakReference，在没有其它强引用的情况下，下一次GC时才会被垃圾回收
WeakReference 被垃圾回收是有前置条件的，很多书或博客上，经常把它落下。

### 1.1 WeakReference 证明代码
![https://farm1.staticflickr.com/808/26289383757_9bb3c80a39_b.jpg](https://farm1.staticflickr.com/808/26289383757_9bb3c80a39_b.jpg)

如果B的强引用设为null，那么B的弱引用将被垃圾回收
#### WeakReferenceExample.Java
```Java
package arthur.dy.lee.weak.refeance.base.example;

import java.lang.ref.WeakReference;

/**
 * http://www.javarticles.com/2016/10/java-weakreference-example.html
 *
 * Created by arthur.dy.lee on 2018/3/29.
 */
public class WeakReferenceExample {
    public static void main(String[] args) {
        B b = new B();
        WeakReference<B> bRef = new WeakReference<B>(b);
        C c = new C(b);
        A a = new A(c);
        b = null;

        System.out.println("Run gc");
        Runtime.getRuntime().gc();

        System.out.println("bRef's referent:" + bRef.get());
        System.out.println("bRef's referent thru a->c->d->bRef:" + a.getC().getD().getB());
    }

}

```
#### B.java
```Java
package arthur.dy.lee.weak.refeance.base.example;

/**
 * Created by arthur.dy.lee on 2018/3/29.
 */
public class B {

    @Override
    public void finalize() {
        System.out.println("B cleaned");
    }
}
```
#### A.java
```Java
package arthur.dy.lee.weak.refeance.base.example;

/**
 * Created by arthur.dy.lee on 2018/3/29.
 */
public class A {
    private C c;

    public A(C c) {
        this.c = c;
    }

    public C getC() {
        return c;
    }

    @Override
    public void finalize() {
        System.out.println("A cleaned");
    }
}
```
#### C.java
```Java
package arthur.dy.lee.weak.refeance.base.example;

import java.lang.ref.WeakReference;

/**
 * Created by arthur.dy.lee on 2018/3/29.
 */

public class C {
    private D d;

    public C(B b) {
        d = new D(new WeakReference<B>(b));
    }

    public D getD() {
        return d;
    }

    @Override
    public void finalize() {
        System.out.println("C cleaned");
    }
}
```
#### D.java
```Java
package arthur.dy.lee.weak.refeance.base.example;

import java.lang.ref.WeakReference;


/**
 * Created by arthur.dy.lee on 2018/3/29.
 */

public class D {
    private WeakReference<B> bRef;

    public D(WeakReference<B> bRef) {
        this.bRef = bRef;
    }

    public B getB() {
        return bRef.get();
    }

    @Override
    public void finalize() {
        System.out.println("D cleaned");
    }
}
```

### 1.2 结果
```txt
bRef's referent:null
B cleaned
bRef's referent thru a-<c-<d-<bRef:null
```

## 2. ThreadLocal 为什么会内存溢出
ThreadLocal 使用内部类ThreadLocalMap来存set()的值，key为弱引用。

以下摘自网上一段，我认为有错误的地方

> ThreadLocalMap内部Entry中key使用的是对ThreadLocal对象的弱引用，这为避免内存泄露是一个进步，因为如果是强引用，那么即使其他地方没有对ThreadLocal对象的引用，ThreadLocalMap中的ThreadLocal对象还是不会被回收，而如果是弱引用则这时候ThreadLocal引用是会被回收掉的，虽然对于的value还是不能被回收，这时候ThreadLocalMap里面就会存在key为null但是value不为null的entry项，虽然ThreadLocalMap提供了set,get,remove方法在一些时机下会对这些Entry项进行清理，但是这是不及时的，也不是每次都会执行的，所以一些情况下还是会发生内存泄露，所以在使用完毕后即使调用remove方法才是解决内存泄露的王道。

前半部分和后半部分是对的，中间是错误的。

### 2.1 其中我认为错误的地方：
>这时候ThreadLocalMap里面就会存在key为null但是value不为null的entry项

### 2.2 ThreadLocal的key是存的自己。
```java
Thread t = Thread.currentThread();
```
```java
public void set(T value) {
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null)
        map.set(this, value);
    else
        createMap(t, value);
}
ThreadLocalMap getMap(Thread t) {
    return t.threadLocals;
}
public T get() {
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null) {
        ThreadLocalMap.Entry e = map.getEntry(this);
        if (e != null) {
            @SuppressWarnings("unchecked")
            T result = (T)e.value;
            return result;
        }
    }
    return setInitialValue();
}
private T setInitialValue() {
    T value = initialValue();
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null)
        map.set(this, value);
    else
        createMap(t, value);
    return value;
}
```

tomcat 使用的是线程池，在请求后，线程并不收回，所以ThreadLocal的key也没有被收回，因为key没有被收回，value也不会被收回。下面开始证明。

## 3. WeakHashMap的key和value垃圾回收测试
下面三个例子来源:
[https://www.logicbig.com/tutorials/core-java-tutorial/java-collections/weak-hash-map.html](https://www.logicbig.com/tutorials/core-java-tutorial/java-collections/weak-hash-map.html)
### 3.1 准备key value对象
```java
package arthur.dy.lee.weak.refeance.weakhashmap;

/**
 * Created by arthur.dy.lee on 2018/3/30.
 */
public class Key {
    private static int keyFinalizeCount;

    public static int getKeyFinalizeCount() {
        return keyFinalizeCount;
    }

    @Override
    protected void finalize() throws Throwable {
        keyFinalizeCount++;
    }
}

```
```java
package arthur.dy.lee.weak.refeance.weakhashmap;

/**
 * Created by arthur.dy.lee on 2018/3/30.
 */
public class MyObject {
    private static int valueFinalizeCount;
    private int[] bigArray = new int[10000];

    public static int getValueFinalizeCount() {
        return valueFinalizeCount;
    }

    @Override
    protected void finalize() throws Throwable {
        valueFinalizeCount++;
    }
}


```
### 3.2 key和value都没有引用的时候
key和value大部分都被回收了，如果加上Thread.sleep(1000);，那么所有的key都被回收了
```java
package arthur.dy.lee.weak.refeance.weakhashmap;

import java.util.WeakHashMap;

/**
 * Created by arthur.dy.lee on 2018/3/30.
 */
public class WeakHashMapExample1 {
    public static void main(String[] args) throws InterruptedException {
        WeakHashMap<Key, MyObject> map = new WeakHashMap<>();
        int size = 10000;
        for (int i = 0; i < size; i++) {
            Key key = new Key();
            MyObject value = new MyObject();
            map.put(key, value);
        }

        System.gc();
        Thread.sleep(1000);
        System.out.println("keys gced: " + Key.getKeyFinalizeCount());
        System.out.println("values gced: " + MyObject.getValueFinalizeCount());

        System.out.println("Map initial size: " + size);
        System.out.println("Map current size: " + map.size());


    }
}
/**
 Output
 keys gced: 10000
values gced: 7212
Map initial size: 10000
Map current size: 0
 **/
```
### 3.3 key如果有强引用的话，key是不会被回收的，value也不会被回收

```java
package arthur.dy.lee.weak.refeance.weakhashmap;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * Created by arthur.dy.lee on 2018/3/30.
 */
public class WeakHashMapExample2 {
    public static void main(String[] args) throws InterruptedException {
        WeakHashMap<Key, MyObject> map = new WeakHashMap<>();
        List<Key> keys = new ArrayList<>();
        int size = 10000;
        for (int i = 0; i < size; i++) {
            Key key = new Key();
            MyObject value = new MyObject();
            map.put(key, value);
            keys.add(key);
        }

        System.gc();
        System.out.println("keys gced: " + Key.getKeyFinalizeCount());
        System.out.println("values gced: " + MyObject.getValueFinalizeCount());

        System.out.println("Map initial size: " + size);
        System.out.println("Map current size: " + map.size());
    }
}
/**
 * Output
 keys gced: 0
 values gced: 0
 Map initial size: 10000
 Map current size: 10000
 */
```

### 3.4 如果value有强引用的话，key也是照样可以被回收的
```java
package arthur.dy.lee.weak.refeance.weakhashmap;

import org.apache.commons.collections.map.LRUMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * https://www.logicbig.com/tutorials/core-java-tutorial/java-collections/weak-hash-map.html
 *
 * Created by arthur.dy.lee on 2018/3/30.
 */
public class WeakHashMapExample3 {
    public static void main(String[] args) throws InterruptedException {
        WeakHashMap<Key, MyObject> map = new WeakHashMap<>();
        List<MyObject> values = new ArrayList<>();
        int size = 10000;

        for (int i = 0; i < size; i++) {
            Key key = new Key();
            MyObject value = new MyObject();
            map.put(key, value);
            values.add(value);
        }

        System.gc();
        Thread.sleep(2000);
        System.out.println("keys gced: " + Key.getKeyFinalizeCount());
        System.out.println("values gced: " + MyObject.getValueFinalizeCount());

        System.out.println("Map initial size: " + size);
        System.out.println("Map current size: " + map.size());

        Map lruMap = new LRUMap();

    }
}
/**

keys gced: 10000
values gced: 0
Map initial size: 10000
Map current size: 0
**/
```

## 4. springboot写一个threadLocal溢出的小例子
### 4.1 先往数据库中插入记录，每个5M
```java
byte[] b = new byte[1024 * 1024 * 5];
for (int i = 11; i < 21; i++) {
    BigObject bigObject = new BigObject();
    bigObject.setContent(b);
    bigObject.setBak("5M");
    service.insertBigObject(bigObject);
}
```
### 4.2 将jvm参数堆调小，最大为400M
```xml
-Xms400M
-Xmx400M
-XX:PermSize=64M
-XX:MaxPermSize=128M
-XX:HeapDumpPath=D:\\jvmlogs.tdump
-XX:+PrintGCDetails
-XX:+PrintGCDateStamps
-XX:+PrintHeapAtGC
```
```java
package arthur.dy.lee.controller;

import arthur.dy.lee.model.BigObject;
import arthur.dy.lee.service.BigObjectService;
import arthur.dy.lee.weak.refeance.ArthurWeakHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by arthur.dy.lee on 2018/4/1.
 */
@RestController
@EnableAutoConfiguration
public class HelloWorld {

    public static ThreadLocal<List<BigObject>> threadLocal = new ThreadLocal<>();

    @Autowired
    private BigObjectService service;

    @RequestMapping("/hello")
    public String index() {
        Map<String, BigObject> map = new WeakHashMap<>();
        List<BigObject> list = service.listBigObject(0, 10);
        threadLocal.set(list);
        for (BigObject bo : list) {
            map.put(UUID.randomUUID().toString(), bo);
        }
        System.out.println("map size: " + map.size());
        System.out.println("BigObject gced: " + BigObject.getBigObjectFinalizeCount());

        StringBuilder s = new StringBuilder(256);
        int i = 0;
        for (BigObject bo : map.values()) {
            s.append(bo.getId()).append("\r\n").append("</br>");
            i++;
        }
        s.append("</br>").append("\r\n").append("\r\n").append("total count = ").append(i);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        s.append(format.format(new Date())).append("</br>");;
        //threadLocal.remove();
        return s.toString();
    }

}

```
### 4.3 往复请求url链接：http://localhost:8888/hello ，2次后，即会内存溢出。OOM
以下为GC日志
object space 273408K, 99% used
```
Heap after GC invocations=31 (full 13):
 PSYoungGen      total 94208K, used 51201K [0x00000000f7b00000, 0x0000000100000000, 0x0000000100000000)
  eden space 56320K, 90% used [0x00000000f7b00000,0x00000000fad00468,0x00000000fb200000)
  from space 37888K, 0% used [0x00000000fdb00000,0x00000000fdb00000,0x0000000100000000)
  to   space 39936K, 0% used [0x00000000fb200000,0x00000000fb200000,0x00000000fd900000)
 ParOldGen       total 273408K, used 270909K [0x00000000e7000000, 0x00000000f7b00000, 0x00000000f7b00000)
  object space 273408K, 99% used [0x00000000e7000000,0x00000000f788f448,0x00000000f7b00000)
 Metaspace       used 39266K, capacity 39740K, committed 40448K, reserved 1085440K
  class space    used 4852K, capacity 4982K, committed 5120K, reserved 1048576K
}
2018-04-01 23:33:31.091 ERROR 18644 --- [nio-8888-exec-2] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Handler dispatch failed; nested exception is java.lang.OutOfMemoryError: Java heap space] with root cause

```

### 4.4 在返回前，加上 threadLocal.remove(); 后，内存不再溢出。

## 5. WeakHashMap做缓存存大对象
JVM设置和上面的相同，都是400M
### 5.1 测试代码

```Java
package arthur.dy.lee.controller;

import arthur.dy.lee.model.BigObject;
import arthur.dy.lee.service.BigObjectService;
import arthur.dy.lee.weak.refeance.ArthurWeakHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Created by arthur.dy.lee on 2018/4/1.
 */
@RestController
@EnableAutoConfiguration
public class HelloWorld {

    public static ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(new Supplier<Integer>() {
        @Override
        public Integer get() {
            return 0;
        }
    });

    @Autowired
    private BigObjectService service;

    @RequestMapping("/hello")
    public String index() {
        /*byte[] b = new byte[1024 * 1024 * 5];
        for (int i = 11; i < 21; i++) {
            BigObject bigObject = new BigObject();
            bigObject.setContent(b);
            bigObject.setBak("5M");
            service.insertBigObject(bigObject);
        }*/
        Map<String, BigObject> map = new ArthurWeakHashMap<>();
        List<BigObject> list = service.listBigObject(0, 10);
        for (BigObject bo : list) {
            map.put(UUID.randomUUID().toString(), bo);
        }
        threadLocal.set(threadLocal.get() + 1);
        //System.gc();
        System.out.println("map size: " + map.size());
        System.out.println("BigObject gced: " + BigObject.getBigObjectFinalizeCount());

        StringBuilder s = new StringBuilder(256);
        int i = 0;
        for (BigObject bo : map.values()) {
            s.append(bo.getId()).append("\r\n").append("</br>");
            i++;
        }
        s.append("</br>").append("\r\n").append("\r\n").append("total count = ").append(i);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        s.append(format.format(new Date())).append("</br>");;
        s.append(threadLocal.get());
        return s.toString();
    }

}

```
### 5.2 无论请求多少次url，都不会报OutOfMemory。
以下是用visualvm监视内存的变化图
![https://farm1.staticflickr.com/818/41160921031_4dc35ec61d_h.jpg](https://farm1.staticflickr.com/818/41160921031_4dc35ec61d_h.jpg)

可以看到内存，初始启动时是占用100M，后多次请求url，峰值250-300M之间，随后不操作，内存慢慢回收。

### 5.3 上面代码中threadLocal只是存读取数据库次数
结果以下图所示：
![https://farm1.staticflickr.com/870/41161012381_ab492bdb08_z.jpg](https://farm1.staticflickr.com/870/41161012381_ab492bdb08_z.jpg)

其中每次请求，下面的值都会有变化，有时是+1，但有时跳跃比较大，如果线程被回收的话，应该每次请求都是1才对。从侧面测试出，ThreadLoacl在线程池中并没有被回收，而且还进行了+1。

### 5.4 GC日志
连续刷新网页请求5~10分钟，慢点刷的请，是不回OOM的，如果刷的太快的话，也会有。
```txt
2018-04-01 23:56:57.025 ERROR 17584 --- [nio-8888-exec-2] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is org.springframework.jdbc.UncategorizedSQLException:
### Error querying database.  Cause: java.sql.SQLException: Error
### The error may exist in file [D:\git-project-self\springbootmybaits\springboot\target\classes\mapping\BigObjectMapper.xml]
### The error may involve arthur.dy.lee.dao.BigObjectMapper.selectByExampleWithBLOBs-Inline
### The error occurred while setting parameters
### SQL: select                      id, name, bak         ,                content         from t_big_object
### Cause: java.sql.SQLException: Error
; uncategorized SQLException for SQL []; SQL state [null]; error code [0]; Error; nested exception is java.sql.SQLException: Error] with root cause

java.lang.OutOfMemoryError: Java heap space
```
以下是经过了300多次GC后，仍旧可以正常运行。即使出现OOM，再刷一下网页就没有了。因为刷的太快，还来不及回收。JVM给的内存又少，所以导致内存溢出。

```text
Heap after GC invocations=12 (full 2):
 PSYoungGen      total 113664K, used 18032K [0x00000000f7b00000, 0x0000000100000000, 0x0000000100000000)
  eden space 94208K, 0% used [0x00000000f7b00000,0x00000000f7b00000,0x00000000fd700000)
  from space 19456K, 92% used [0x00000000fed00000,0x00000000ffe9c240,0x0000000100000000)
  to   space 20992K, 0% used [0x00000000fd700000,0x00000000fd700000,0x00000000feb80000)
 ParOldGen       total 273408K, used 69920K [0x00000000e7000000, 0x00000000f7b00000, 0x00000000f7b00000)
  object space 273408K, 25% used [0x00000000e7000000,0x00000000eb448008,0x00000000f7b00000)
 Metaspace       used 40335K, capacity 41118K, committed 41344K, reserved 1085440K
  class space    used 5009K, capacity 5195K, committed 5248K, reserved 1048576K
}
{Heap before GC invocations=13 (full 2):
 PSYoungGen      total 113664K, used 107297K [0x00000000f7b00000, 0x0000000100000000, 0x0000000100000000)
  eden space 94208K, 94% used [0x00000000f7b00000,0x00000000fd22c570,0x00000000fd700000)
  from space 19456K, 92% used [0x00000000fed00000,0x00000000ffe9c240,0x0000000100000000)
  to   space 20992K, 0% used [0x00000000fd700000,0x00000000fd700000,0x00000000feb80000)
 ParOldGen       total 273408K, used 69920K [0x00000000e7000000, 0x00000000f7b00000, 0x00000000f7b00000)
  object space 273408K, 25% used [0x00000000e7000000,0x00000000eb448008,0x00000000f7b00000)
 Metaspace       used 40335K, capacity 41118K, committed 41344K, reserved 1085440K
  class space    used 5009K, capacity 5195K, committed 5248K, reserved 1048576K
  ........
  2018-04-01T23:57:37.220+0800: [GC (Allocation Failure) [PSYoungGen: 67550K->25696K(91648K)] 253443K->232093K(365056K), 0.0105353 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
Heap after GC invocations=910 (full 322):
 PSYoungGen      total 91648K, used 25696K [0x00000000f7b00000, 0x0000000100000000, 0x0000000100000000)
  eden space 46592K, 0% used [0x00000000f7b00000,0x00000000f7b00000,0x00000000fa880000)
  from space 45056K, 57% used [0x00000000fd400000,0x00000000fed18050,0x0000000100000000)
  to   space 44544K, 0% used [0x00000000fa880000,0x00000000fa880000,0x00000000fd400000)
 ParOldGen       total 273408K, used 206397K [0x00000000e7000000, 0x00000000f7b00000, 0x00000000f7b00000)
  object space 273408K, 75% used [0x00000000e7000000,0x00000000f398f580,0x00000000f7b00000)
 Metaspace       used 39992K, capacity 40540K, committed 41088K, reserved 1085440K
  class space    used 4973K, capacity 5098K, committed 5248K, reserved 1048576K
}
```

## 6. 结论
### 如果使用ThreadLocal的话，用不好会内存溢出的，最好要配合remove()方法来用。具体问题具体分析。

### WeakHashMap的key是用的WeakReference，在没有其它强引用的情况下，下一次GC时才会被垃圾回收。无论是key还是map，有其它强引用的情况下，是不会被回收的。
