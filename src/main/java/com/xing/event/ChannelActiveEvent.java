package com.xing.event;

import com.xing.handler.ChannelHandlerContext;

public class ChannelActiveEvent extends Event {

	public ChannelActiveEvent(ChannelHandlerContext ctx) {
		super(ctx);
	}

	@Override
	protected void doRun(ChannelHandlerContext context, Object message) {
		context.fireChannelActive();
	}

}
