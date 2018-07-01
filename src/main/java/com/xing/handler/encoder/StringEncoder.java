package com.xing.handler.encoder;

import com.xing.context.ChannelHandlerContext;
import com.xing.handler.OutBoundHandlerAdapter;
import java.nio.ByteBuffer;

public class StringEncoder extends OutBoundHandlerAdapter {

    @Override
    public void channelWrite(Object message, ChannelHandlerContext context) {
        if (message instanceof String) {
            context.fireChannelWrite(ByteBuffer.wrap(((String) message).getBytes()));
        } else {
            context.fireChannelWrite(message);
        }
    }

}
