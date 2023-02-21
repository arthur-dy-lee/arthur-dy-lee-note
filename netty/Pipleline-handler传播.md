NioServerSocketChannel#register

```java
public ChannelFuture register(ChannelPromise promise) {
    return pipeline.register(promise);
}
@Override
public final ChannelFuture register(final ChannelPromise promise) {
    return tail.register(promise);
}
@Override
public ChannelFuture register(final ChannelPromise promise) {
    //...
    EventExecutor executor = executor();
    if (executor.inEventLoop()) {
        findAndInvokeRegister(promise);
    } else {
        safeExecute(executor, () -> findAndInvokeRegister(promise), promise, null);
    }
    return promise;
}

private void findAndInvokeRegister(ChannelPromise promise) {
    //只关心是MASK_REGISTER的，从后往前找
    //找到的context是DefaultChannelPipeline$HeadHandler#0，即HeadHandler
    DefaultChannelHandlerContext context = findContextOutbound(MASK_REGISTER);
    if (context.isProcessOutboundDirectly()) {
        context.invokeRegister(promise);
    } else {
        executeOutboundReentrance(context, () -> context.invokeRegister0(promise), promise, null);
    }
}
private DefaultChannelHandlerContext findContextOutbound(int mask) {
    DefaultChannelHandlerContext ctx = this;
    do {
        ctx = ctx.prev;  //从tail往前找，mask是register类型的
    } while ((ctx.executionMask & mask) == 0 && ctx.isProcessOutboundDirectly());
    return ctx;
}
@Override
public void register(ChannelHandlerContext ctx, ChannelPromise promise) {
    ctx.channel().unsafe().register(promise);
}
@Override
public final void register(final ChannelPromise promise) {
    assertEventLoop();
    //...
        doRegister(); //  <--  注册，前面已经讲过
        neverRegistered = false;
        registered = true;
        //此处为promise-B,是initAndRegister上传进来的 channel.register(promise)，在这里set值 
        safeSetSuccess(promise);  
        pipeline.fireChannelRegistered(); // <---- 
        // Only fire a channelActive if the channel has never been registered. This prevents firing
        // multiple channel actives if the channel is deregistered and re-registered.
        if (isActive()) {
            if (firstRegistration) {
                pipeline.fireChannelActive();
            }
            readIfIsAutoRead();
        }
    //...
}
```



AbstractUnsafe#register

DefaultChannelPipeline$HeadHandler#register



​				-> AbstractUnsafe#register

​			-> DefaultChannelPipeline$HeadHandler#register

​		-> #invokeRegister0

​	-> #invokeRegister【context.invokeRegister(promise);】

​	-> #incrementOutboundOperations

DefaultChannelHandlerContext#findAndInvokeRegister

结果找到的是DefaultChannelPipeline$HeadHandler#0，即ChannelContextHandler。

MASK_REGISTER = 1 << 13。其实是找mask二进制为 10000000000000 的handler，并且context为outBound类型的handler。

只关心是MASK_REGISTER的，从后往前找，找到的context是DefaultChannelHandlerContext【DefaultChannelPipeline$HeadHandler#0】

DefaultChannelHandlerContext#findContextOutbound

DefaultChannelHandlerContext#register

DefaultChannelPipeline#register