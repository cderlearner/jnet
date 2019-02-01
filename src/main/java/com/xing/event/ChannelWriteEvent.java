package com.xing.event;

import com.xing.handler.ChannelHandlerContext;

public class ChannelWriteEvent extends Event {

    public ChannelWriteEvent(ChannelHandlerContext context, Object message) {
        super(context, message);
    }

    @Override
    protected void doRun(ChannelHandlerContext context, Object message) {
        context.fireChannelWrite(message);
    }

}
