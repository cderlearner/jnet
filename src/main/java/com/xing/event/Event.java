package com.xing.event;

import com.xing.handler.ChannelHandlerContext;

public abstract class Event implements Runnable {

    private ChannelHandlerContext ctx;
    private Object message;

    Event(ChannelHandlerContext context, Object message) {
        this.ctx = context;
        this.message = message;
    }

    Event(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public final void run() {
        ctx.reset();
        doRun(ctx, message);
    }

    /**
     * 子类真正的运行方法.
     */
    protected abstract void doRun(ChannelHandlerContext context, Object message);

}
