package com.xing.handler;

import com.xing.context.ChannelHandlerContext;

public class OutBoundHandlerAdapter implements OutBoundHandler {

    @Override
    public void channelWrite(Object message, ChannelHandlerContext context) {
        context.fireChannelWrite(message);
    }

}
