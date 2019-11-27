# netty promise和future详解



## 一、代码

为更好的解理promise，下面跑一个小程序

```java
package io.netty.example.echo;

import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * Created by arthur.dy.lee on 2019-10-20.
 */
public class NettyFutureDemo {

    public static void main(String[] args) throws InterruptedException {
        long l = System.currentTimeMillis();
        EventExecutorGroup group = new DefaultEventExecutorGroup(4);//创建执行线程池SingleThreadEventExecutor
        Future<Integer> f = group.submit(new Callable<Integer>() { //创建Callable
            @Override
            public Integer call() throws Exception {
                System.out.println("执行耗时操作...");
                timeConsumingOperation();
                return 100;
            }
        });
        f.addListener(new FutureListener<Object>() { //添加匿名监听者
            @Override
            public void operationComplete(Future<Object> objectFuture) throws Exception {
                System.out.println("计算结果:：" + objectFuture.get()); //promise.get获取集果
            }
        });
        System.out.println("主线程运算耗时:" + (System.currentTimeMillis() - l)+ "ms");
        new CountDownLatch(1).await();
    }

    static void timeConsumingOperation() {
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```



## 二、时序图

下面的时序图可点击看大图

![](https://live.staticflickr.com/65535/48929922962_b12a149c95_k.jpg)

## 三、调用过程

#### 1. 创建线程池SingleThreadEventExecutor

`EventExecutorGroup group = new DefaultEventExecutorGroup(4);`创建4个线程：SingleThreadEventExecutor。以下是创建的关键代码。

```java
children = new EventExecutor[nThreads];
powerOfTwo = isPowerOfTwo(children.length);
for (int i = 0; i < nThreads; i ++) {
    boolean success = false;
    try {
        children[i] = newChild(executor, maxPendingTasks, rejectedHandler, args);
        success = true;
```

```java
protected EventExecutor newChild(Executor executor,  int maxPendingTasks,
                                 RejectedExecutionHandler rejectedExecutionHandler,
                                 Object... args) {
    assert args.length == 0;
    return new SingleThreadEventExecutor(executor, maxPendingTasks, rejectedExecutionHandler); 
}
```

#### 2. 创建Callable匿名类

```java
new Callable<Integer>() {
    @Override
    public Integer call() throws Exception {
        System.out.println("执行耗时操作...");
        timeConsumingOperation();
        return 100;
    }
}
```

#### 3. submit提交Callable执行

`Future<Integer> f = group.submit(new Callable<Integer>() {...`

看时序图可知，submit主要是3个任务：创建`DefaultPromise`、创建`RunnableFutureAdapter`和执行`Callable`匿名类。

RunnableFutureAdapter其实就是一个future和Runnable的实现类，创建RunnableFutureAdapter后，返回的就是`Future<Integer> f`。创建RunnableFutureAdapter时，将DefaultPromise和Callable传入RunnableFutureAdapter。

当使用f.addListener时，RunnableFutureAdapter就是一个future；当调用Runnable#run方法时，它就是一个task。

![](blogpic/RunnableFutureAdapter.png)

执行`Callable`的过程：submit后，通过SingleThreadEventExecutor来执行，先将task(Callable)放到队列中，后startThread，doStartThread会启动一下新的线程，代码如下：

```java
private void doStartThread() {
    assert thread == null;
    executor.execute(() -> {
        thread = Thread.currentThread();
        if (interrupted) {
            thread.interrupt();
        }

        boolean success = false;
        updateLastExecutionTime();
        try {
            SingleThreadEventExecutor.this.run(); // 执行任务
            success = true;
```

时序图中，22和23反了。run方法中，先取出任务task(RunnableFutureAdapter)

```java
protected void run() {
    assert inEventLoop();
    do {
        Runnable task = takeTask();
        if (task != null) {
            task.run(); //RunnableFutureAdapter#run
            updateLastExecutionTime();
        }
    } while (!confirmShutdown());
}
```

RunnableFutureAdapter#run后运行任务Callable#run

```java
@Override
public void run() {
    try {
        if (promise.setUncancellable()) {
            V result = task.call(); //Callable#run
            promise.setSuccess(result); //promise获取结果
        }
    } catch (Throwable e) {
        promise.setFailure(e);
    }
}
```

此时已完成Callable的调用

#### 4. 添加监听者Listener

listener不需要等待主线程执行。当Callable执行完后，会通知到监听的listener类，对Callable的结果进行处理。

此处的`Future<Object> objectFuture`其实是`DefaultPromise`

```java
f.addListener(new FutureListener<Object>() { //添加匿名监听者
  @Override
  public void operationComplete(Future<Object> objectFuture) throws Exception {
    System.out.println("计算结果:：" + objectFuture.get()); //promise.get获取集果
  }
});
```

此处addListener入参为：一个匿名类，它是接口FutureListener的实现类。因为接口只有一个抽象方法，所以也可以这样写：

```java
f.addListener((FutureListener)objectFuture -> {
        System.out.println("计算结果:：" + objectFuture.get());
});
```

在看一下promise如何获取结果前，先看一下addListener过程。

RunnableFutureAdapter#addListener

```java
@Override
public RunnableFuture<V> addListeners(GenericFutureListener<? extends Future<? super V>>... listeners) {
    promise.addListeners(listeners);
    return this;
}
```

DefaultPromise#addListeners，将匿名listener放起来

```java
@Override
public Promise<V> addListener(GenericFutureListener<? extends Future<? super V>> listener) {
    requireNonNull(listener, "listener");

    synchronized (this) {
        addListener0(listener);
    }
    if (isDone()) {//不执行，此时Callable还未执行完
        notifyListeners();
    }
    return this;
}
```

#### 5. promise获取结果

下面继续任务执行后，DefaultPromise的获取结果的执行过程`promise.setSuccess(result);`，如何通知listener。

promise#setSuccess会调用notifyListeners通知所有的listeners。

```java
private void notifyListenersNow() {
    Object listeners;
    synchronized (this) {
        // Only proceed if there are listeners to notify.
        if (this.listeners == null) {
            return;
        }
        listeners = this.listeners;
        this.listeners = null;
    }
    for (;;) {
        if (listeners instanceof DefaultFutureListeners) {
            notifyListeners0((DefaultFutureListeners) listeners);
        } else {
            notifyListener0(this, (GenericFutureListener<?>) listeners); // this是DefaultPromise实例，listeners就是代码中的匿名listeners
        }
        synchronized (this) {
            if (this.listeners == null) {
                return;
            }
            listeners = this.listeners;
            this.listeners = null;
        }
    }
}
```

`notifyListener0(this, (GenericFutureListener<?>) listeners);`this是DefaultPromise实例，listeners就是代码中的匿名listeners。

notifyListener0入参future就是this，而this就是DefaultPromise实例。

```java
private static void notifyListener0(Future future, GenericFutureListener l) {
    try {
        l.operationComplete(future);
    } catch (Throwable t) {
        if (logger.isWarnEnabled()) {
            logger.warn("An exception was thrown by " + l.getClass().getName() + ".operationComplete()", t);
        }
    }
}
```

`l.operationComplete(future);`相当于匿名类listener调用operationComplete方法，而入参是DefaultPromise，即调用

```java
new FutureListener<Object>() { 
  @Override
  public void operationComplete(Future<Object> objectFuture) throws Exception {
    System.out.println("计算结果:：" + objectFuture.get()); //promise.get获取集果
  }
}
```

而DefaultPromise中保存着callable返回的结果，所以可以通过objectFuture.get()得到值为100。