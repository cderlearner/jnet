package com.xing.event;

import com.xing.handler.ChannelHandlerContext;

public class ChannelInActiveEvent extends Event {

    public ChannelInActiveEvent(ChannelHandlerContext ctx) {
        super(ctx);
    }

    @Override
    protected void doRun(ChannelHandlerContext context, Object message) {
        context.fireChannelInActive();
    }

}
