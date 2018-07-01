package com.xing.event;


import com.xing.context.ChannelHandlerContext;

public class ChannelReadEvent extends Event {

    public ChannelReadEvent(ChannelHandlerContext context, Object message) {
        super(context, message);
    }

    @Override
    protected void doRun(ChannelHandlerContext context, Object message) {
        context.fireChannelRead(message);
    }

}
