package com.xing.handler;

import com.xing.context.ChannelHandlerContext;

/**
 * 除了向下转发什么也不做，自己实现的InBoundHandlerAdapter可继承此类.
 */
public class InBoundHandlerAdapter implements InBoundHandler {

    @Override
    public void channelActive(ChannelHandlerContext context) {
        context.fireChannelActive();
    }

    @Override
    public void channelInActive(ChannelHandlerContext context) {
        context.fireChannelInActive();
    }

    @Override
    public void channelRead(Object message, ChannelHandlerContext context) {
        context.fireChannelRead(message);
    }

}
