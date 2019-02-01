package com.xing.handler;

public class OutBoundHandlerAdapter implements OutBoundHandler {

    @Override
    public void channelWrite(Object message, ChannelHandlerContext context) {
        context.fireChannelWrite(message);
    }
}
