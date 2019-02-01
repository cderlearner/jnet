package com.xing.handler;

import com.xing.util.log.LogManager;
import com.xing.util.log.api.ILog;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class DefaultOutBoundHandler implements OutBoundHandler {
    private static final ILog logger = LogManager.getLogger(DefaultOutBoundHandler.class);

    @Override
    public void channelWrite(Object message, ChannelHandlerContext context) {
        if (message == null) return;
        SocketChannel channel = context.getChannel();
        try {
            ByteBuffer result = null;
            if (message instanceof ByteBuffer) {
                result = (ByteBuffer) message;
            } else if (message instanceof byte[]) {
                result = ByteBuffer.wrap((byte[]) message);
            } else if (message instanceof String) {
                result = ByteBuffer.wrap(message.toString().getBytes());
            }
            if (result == null) {
                logger.debug("Unsupported type: " + message.getClass().getName());
            } else {
                channel.write(result);
            }
        } catch (IOException e) {
            try {
                logger.error("Write to " + channel.getRemoteAddress().toString() + " failed.");
            } catch (IOException e1) {
                logger.error(e1.getMessage());
            }
        }
    }
}
