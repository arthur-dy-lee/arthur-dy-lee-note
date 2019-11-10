# Netty笔记



### 关系





## 二、创建



### SingleThreadEventExecutor创建



#### SingleThreadEventExecutor添加任务

```java
@Override
public void execute(Runnable task) {
    requireNonNull(task, "task");

    boolean inEventLoop = inEventLoop();
    addTask(task);
    if (!inEventLoop) {
        startThread();
        //....
    }

    if (!addTaskWakesUp && wakesUpForTask(task)) {
        wakeup(inEventLoop);
    }
}
```

SingleThreadEventExecutor#execute -> startThread -> doStartThread -> SingleThreadEventLoop#run

```java
private void doStartThread() {
    assert thread == null;
    executor.execute(() -> {
        thread = Thread.currentThread();
        //...
        try {
            run();
            success = true;
        //....
```



```java
protected void run() {
    assert inEventLoop();
    do {
        Runnable task = takeTask(); // 执行任务  <--------
        if (task != null) {
            task.run();
            updateLastExecutionTime();
        }
    } while (!confirmShutdown());
}
```



### Channel创建



![](https://live.staticflickr.com/65535/48944455878_c590e8cbed_w.jpg)

```java
ServerBootstrap b = new ServerBootstrap();
b.group(bossGroup, workerGroup)
 .channel(NioServerSocketChannel.class)
ChannelFuture f = b.bind(PORT).sync();
```

先绑定。通过`.channel(NioServerSocketChannel.class)`绑定NioServerSocketChannel

```java
public ServerBootstrap channel(Class<? extends ServerChannel> channelClass) {
    requireNonNull(channelClass, "channelClass");
    return channelFactory(new ReflectiveServerChannelFactory<ServerChannel>(channelClass));
}
```

```java
public ReflectiveServerChannelFactory(Class<? extends T> clazz) {
    requireNonNull(clazz, "clazz");
    try {
        this.constructor = clazz.getConstructor(EventLoop.class, EventLoopGroup.class);
    } catch (NoSuchMethodException e) {
        //...
    }
}
```

再通过NioServerSocketChannel创建Channel

```java
ChannelFuture f = b.bind(PORT).sync();
```

```java
public ChannelFuture bind(int inetPort) {
    return bind(new InetSocketAddress(inetPort));
}
```

```java
final ChannelFuture initAndRegister() {
    EventLoop loop = group.next();
    final Channel channel;
    try {
        channel = newChannel(loop);
   //..
```

```java
ServerChannel newChannel(EventLoop eventLoop) throws Exception {
    return channelFactory.newChannel(eventLoop, childGroup);
}
```

```java
public T newChannel(EventLoop eventLoop, EventLoopGroup childEventLoopGroup) {
    try {
        return constructor
                .newInstance(eventLoop, childEventLoopGroup);
   //...
```

反射调用NioServerSocketChannel构造方法创建Channel： NioServerSocketChannel

```java
public NioServerSocketChannel(EventLoop eventLoop, EventLoopGroup childEventLoopGroup) {
    this(eventLoop, childEventLoopGroup, newSocket(DEFAULT_SELECTOR_PROVIDER));
}
```

NioServerSocketChannel实现了Channel接口。看一下它的类继承关系

![](https://live.staticflickr.com/65535/48961696213_df34dc41ea_b.jpg)

### ChannelPipeline创建

```java
public NioServerSocketChannel(
        EventLoop eventLoop, EventLoopGroup childEventLoopGroup, ServerSocketChannel channel) {
    super(null, eventLoop, channel, SelectionKey.OP_ACCEPT);
    this.childEventLoopGroup = requireNonNull(childEventLoopGroup, "childEventLoopGroup");
    config = new NioServerSocketChannelConfig(this, javaChannel().socket());
}
```

继续NioServerSocketChannel构造函数，看可以看到，在创建NioServerSocketChannel的时候，会先调用父类super创建父类AbstractChannel

![](https://live.staticflickr.com/65535/48961709768_d86c2f29c5_o.png)

下面可以看到创建了newChannelPipeline: `pipeline = newChannelPipeline();`

```java
protected AbstractChannel(Channel parent, EventLoop eventLoop) {
    this.parent = parent;
    this.eventLoop = validateEventLoop(eventLoop);
    closeFuture = new CloseFuture(this, eventLoop);
    succeedFuture = new SucceededChannelFuture(this, eventLoop);
    id = newId();
    unsafe = newUnsafe();
    pipeline = newChannelPipeline();
}
```

Channel（NioServerSocketChannel）创建时，每个channel都会有的属性：

- ChannelFuture succeedFuture （新创建）
- EventLoop eventLoop
- ChannelId id（新创建）
- ChannelPipeline pipeline（新创建）





# #

-------

###  Bootstrap



![](http://pxysxbscs.bkt.clouddn.com/ServerBootstrap&Bootstrap.png)

#### ServerBootstrap

ServerBootstrap#init代码片段

```java
@Override
ChannelFuture init(Channel channel) {
    final ChannelPromise promise = channel.newPromise();
    setChannelOptions(channel, options0().entrySet().toArray(newOptionArray(0)), logger);
    setAttributes(channel, attrs0().entrySet().toArray(newAttrArray(0)));

    ChannelPipeline p = channel.pipeline();

    final ChannelHandler currentChildHandler = childHandler;
    final Entry<ChannelOption<?>, Object>[] currentChildOptions =
            childOptions.entrySet().toArray(newOptionArray(0));
    final Entry<AttributeKey<?>, Object>[] currentChildAttrs = childAttrs.entrySet().toArray(newAttrArray(0));

    p.addLast(new ChannelInitializer<Channel>() {
        @Override
        public void initChannel(final Channel ch) {
            final ChannelPipeline pipeline = ch.pipeline();
            ChannelHandler handler = config.handler();
            if (handler != null) {
                pipeline.addLast(handler);
            }

            ch.eventLoop().execute(() -> {
                pipeline.addLast(new ServerBootstrapAcceptor(
                        ch, currentChildHandler, currentChildOptions, currentChildAttrs));
                promise.setSuccess();
            });
        }
    });
    return promise;
}
```





`MultithreadEventExecutorGroup`

Channel_、_EventLoop_、_Thread_以及_EventLoopGroup_之间的关系，如下图所示。

![](http://pxysxbscs.bkt.clouddn.com/Channel_、_EventLoop_、_Thread_以及_EventLoopGroup_之间的关系5.png)

EventLoop 定义了 Netty 的核心抽象，用于处理连接的生命周期中所发生的事件。

- 一个 EventLoopGroup 包含一个或者多个 EventLoop ；
- 一个 EventLoop在它的生命周期内只和一个Thread绑定；
- 所有由 EventLoop 处理的 I/O 事件都将在它专有的 Thread 上被处理；
- 所有由 EventLoop 处理的 I/O 事件都将在它专有的 Thread 上被处理；
- 一个 EventLoop 可能会被分配给一个或多个 Channel 。

注意，在这种设计中，一个给定Channel的 I/O 操作都是由相同的Thread执行的，实际上消除了对于同步的需要。



创建MultithreadEventExecutorGroup时（`new MultithreadEventLoopGroup(1, NioHandler.newFactory())`）做了：

1. 创建Executor executor：`ThreadPerTaskExecutor`。`ThreadPerTaskExecutor`中是`DefaultThreadFactory`。

   执行时执行时：executor.execute(Runnable command)，是为执行DefaultThreadFactory.newThread(Runnable r)。

2. 创建`RejectedExecutionHandlers`

3. 如果默认传空nThreads，则默认创建个数：`NettyRuntime.availableProcessors() * 2`，我的是值是32

4. 创建数组EventExecutor[] children。数组值等于nThreads。

   `new SingleThreadEventLoop(executor, ioHandler, maxPendingTasks, rejectedExecutionHandler, maxTasksPerRun)`

干活儿的还是SingleThreadEventLoop和它的的父类SingleThreadEventExecutor

![](http://pxysxbscs.bkt.clouddn.com/SingleThreadEventLoop.png)

### Channel

![](http://pxysxbscs.bkt.clouddn.com/Channel-2.png)

```java
ServerBootstrap b = new ServerBootstrap();
b.channel(NioServerSocketChannel.class)
```

通过反射创建`NioServerSocketChannel`



### Handler

```java
ServerBootstrap b = new ServerBootstrap();
b.handler(new LoggingHandler(LogLevel.INFO))
 .childHandler(new ChannelInitializer<SocketChannel>() {
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
      ChannelPipeline p = ch.pipeline();
      if (sslCtx != null) {
        p.addLast(sslCtx.newHandler(ch.alloc()));
      }
      //p.addLast(new LoggingHandler(LogLevel.INFO));
      p.addLast(serverHandler);
    }
  });
```

`b.handler`被调用关系链：

AbstractBootstrap#handler(new LoggingHandler(LogLevel.INFO)) -> AbstractBootstrap#handler -> AbstractBootstrapConfig#handler() -> ServerBootstrap#init

b.childHandler`被调用关系链：

ServerBootstrap#childHandler(new ChannelInitializer()) -> ServerBootstrap#childHandler(childHandler) -> ServerBootstrap#init



`.childHandler(new ChannelInitializer<SocketChannel>() {};` debug展示的内容是：`EchoServer$1@1463`，看到`$1`，说明是匿名内部类。通过反射查看ChannelInitializer的子类，也可以看到Anonymous in main() in EchoServer。



![](http://pxysxbscs.bkt.clouddn.com/childHandler-ChannelInitializer.png)





### ChannelInitializer

netty提供了一种将多个ChannelHandler添加到一个ChannelPipeline中的简便方法。你只需要简单地向Bootstrap或ServerBootstrap的实例提供你的ChannelInitializer实现即可，并且一旦Channel被注册到了它的EventLoop之后，就会调用你的initChannel()版本。在该方法返回之后，ChannelInitializer的实例将会从ChannelPipeline中移除它自己。

在大部分的场景下，如果你不需要使用只存在于SocketChannel上的方法，使用ChannelInitializer就可以了，否则你可以使用ChannelInitializer，其中SocketChannel扩展了Channel。 如果你的应用程序使用了多个ChannelHandler，请定义你自己的ChannelInitializer实现来将它们安装到ChannelPipeline中。





### ChannelFuture

实例化NioServerSocketChannel

```java
ServerBootstrap b = new ServerBootstrap();
//....
ChannelFuture f = b.bind(PORT).sync();
```

AbstractBootstrap#bind -> doBind

1. ChannelFuture regFuture = initAndRegister()。创建Channel和ChannelPipeline: NioServerSocketChannel和DefaultChannelPipeline 
2. 创建ChannelPromise：DefaultChannelPromise。
3. 判断是否regFuture完成。……..

```java
private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();
private static ServerSocketChannel newSocket(SelectorProvider provider) {
  return provider.openServerSocketChannel();
}
private final ServerSocketChannelConfig config;
private final EventLoopGroup childEventLoopGroup;

public NioServerSocketChannel(EventLoop eventLoop, EventLoopGroup childEventLoopGroup, SelectorProvider provider) {
    this(eventLoop, childEventLoopGroup, newSocket(provider));
}

public NioServerSocketChannel(
        EventLoop eventLoop, EventLoopGroup childEventLoopGroup, ServerSocketChannel channel) {
    super(null, eventLoop, channel, SelectionKey.OP_ACCEPT);
    this.childEventLoopGroup = requireNonNull(childEventLoopGroup, "childEventLoopGroup");
    config = new NioServerSocketChannelConfig(this, javaChannel().socket());
}
```



#### initAndRegister

1. AbstractBootstrap#initAndRegister。**创建Channel和ChannelPipeline: NioServerSocketChannel和DefaultChannelPipeline**。从MultithreadEventLoopGroup中取出EventLoop，通过NioServerSocketChannel将EventLoop注册到AbstractChannel（closeFuture和succeedFuture）。过程如下：newChannel -> ServerBootstrap#newChannel -> channelFactory#newChannel -> ReflectiveServerChannelFactory#newChannel -> constructor#newInstance -> NioServerSocketChannel(eventLoop, childEventLoopGroup)

   DefaultChannelPipeline是在NioServerSocketChannel的抽象类AbstractChannel中实例化。

2. 创建ChannelPromise。channel.newPromise() -> new DefaultChannelPromise(this, eventLoop)

3. 

4. loop.execute -> SingleThreadEventLoop.execute(Runnable task)

   1. SingleThreadEventExecutor#execute -> startThread -> doStartThread -> `SingleThreadEventExecutor.this.run()` -> SingleThreadEventLoop#run -> runIo -> `NioHandler.run(IoExecutionContext context)`
   2. init(channel)。
   3. promise#addListener，

   



```java
final ChannelFuture initAndRegister() {
    EventLoop loop = group.next();
    final Channel channel;
    try {
        channel = newChannel(loop);
    } catch (Throwable t) {
        return new FailedChannel(loop).newFailedFuture(t);
    }

    final ChannelPromise promise = channel.newPromise();
    loop.execute(() -> init(channel).addListener((ChannelFutureListener) future -> {
        if (future.isSuccess()) {
            channel.register(promise);
        } else {
            channel.unsafe().closeForcibly();
            promise.setFailure(future.cause());
        }
    }));

    return promise;
}
```



AbstractChannel#AbstractChannel

```java
protected AbstractChannel(Channel parent, EventLoop eventLoop) {
    this.parent = parent;
    this.eventLoop = validateEventLoop(eventLoop);
    closeFuture = new CloseFuture(this, eventLoop);
    succeedFuture = new SucceededChannelFuture(this, eventLoop);
    id = newId();
    unsafe = newUnsafe();
    pipeline = newChannelPipeline();
}
```



##### init(Channel channel)

创建ChannelPromise，new DefaultChannelPromise(this, eventLoop)。





```java
ChannelFuture init(Channel channel) {
    final ChannelPromise promise = channel.newPromise();
    setChannelOptions(channel, options0().entrySet().toArray(newOptionArray(0)), logger);
    setAttributes(channel, attrs0().entrySet().toArray(newAttrArray(0)));

    ChannelPipeline p = channel.pipeline();

    final ChannelHandler currentChildHandler = childHandler;
    final Entry<ChannelOption<?>, Object>[] currentChildOptions =
            childOptions.entrySet().toArray(newOptionArray(0));
    final Entry<AttributeKey<?>, Object>[] currentChildAttrs = childAttrs.entrySet().toArray(newAttrArray(0));

    p.addLast(new ChannelInitializer<Channel>() {
        @Override
        public void initChannel(final Channel ch) {
            final ChannelPipeline pipeline = ch.pipeline();
            ChannelHandler handler = config.handler();
            if (handler != null) {
                pipeline.addLast(handler);
            }

            ch.eventLoop().execute(() -> {
                pipeline.addLast(new ServerBootstrapAcceptor(
                        ch, currentChildHandler, currentChildOptions, currentChildAttrs));
                promise.setSuccess();
            });
        }
    });
    return promise;
}
```



### `ChannelPipeline`



ChannelPipeline是一个拦截流经Channel的入站和出站事件的ChannelHandler实例链。

每一个新创建的Channel都将会被分配一个新的ChannelPipeline。这项关联是永久性的；Channel既不能附加另外一个ChannelPipeline，也不能分离其当前的。在Netty组件的生命周期中，这是一项固定的操作，不需要开发人员的任何干预。 根据事件的起源，事件将会被ChannelInboundHandler或者ChannelOutbboundHandler处理。随后，通过调用ChannelHandlerContext实现，它将被转发给同一超类型的下一个ChannelHandler。

### ChannelPipeline添加顺序

对于进站事件来说，先添加的先执行。 对于出站事件来说，后添加的先执行。









### SimpleChannelInboundHandler

当某个ChannelInboundHandler的实现重写channelRead()方法时，它将负责显式地释放与池化的ByteBuf实例相关的内存。Netty为此提供了一个实用方法ReferenceCountUtil.release() 但是以这种方式管理资源可能很繁琐。

一个更加简单的方式是使用SimpleChannelInboundHandler。 由于SimpleChannelInboundHandler会自动释放资源，所以你不应该存储指向任何消息的引用供将来使用，因为这些引用都将会失效。







## 顺一下类关系

```java
public class DefaultChannelPromise extends DefaultPromise<Void> implements ChannelPromise, FlushCheckpoint {

    private final Channel channel;
    private long checkpoint;
```





`AbstractEventExecutor`创建`RunnableFutureAdapter`

`new RunnableFutureAdapter<>(promise, Executors.callable(task, value));`

`AbstractExecutorService.submit(Callable<T> task)`-> `AbstractExecutorService.newTaskFor(Callable<T> callable)`



AbstractEventExecutorGroup#submit -> AbstractEventExecutor#submit -> AbstractEventExecutor#newTaskFor ->new RunnableFutureAdapter

```java
protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
    return newRunnableFuture(this.newPromise(), callable);
}
private static <V> RunnableFuture<V> newRunnableFuture(Promise<V> promise, Callable<V> task) {
  return new RunnableFutureAdapter<>(promise, task);
}
```

