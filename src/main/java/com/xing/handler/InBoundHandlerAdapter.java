package com.xing.handler;

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
