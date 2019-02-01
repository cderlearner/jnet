package com.xing.handler;


import java.util.Objects;
import java.util.stream.Stream;

/**
 * 用以在客户端Channel建立时想HandlerChain添加Handler.
 * <p>这样可以实现对于每一次客户端调用，HandlerChain中的Handler对象都是不同的, 否则都是同一个对象.</p>
 *
 */
public abstract class HandlerInitializer extends InBoundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext context) {
        Handler[] handlers = init();
        Objects.requireNonNull(handlers);
        // 移除初始化handler
        context.removeHandlerInitializer(this);
        Stream.of(handlers).forEach(context::addHandler);
        context.fireChannelActive();
    }

    public abstract Handler[] init();
}
