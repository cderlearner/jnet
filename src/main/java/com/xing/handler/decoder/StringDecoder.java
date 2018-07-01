package com.xing.handler.decoder;

import com.xing.context.ChannelHandlerContext;
import com.xing.handler.InBoundHandlerAdapter;
import com.xing.log.LogManager;
import com.xing.log.api.ILog;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class StringDecoder extends InBoundHandlerAdapter {

    private static final Charset defaultCharSet = Charset.forName("UTF-8");
    private static final ILog logger = LogManager.getLogger(StringDecoder.class);
    private final Charset charset;

    public StringDecoder() {
        this(null);
    }

    public StringDecoder(Charset charset) {
        if (charset != null) {
            this.charset = charset;
        } else {
            this.charset = defaultCharSet;
        }
    }

    @Override
    public void channelRead(Object message, ChannelHandlerContext context) {
        if (message == null) return;
        byte[] array = null;
        if (message instanceof ByteBuffer) {
            ByteBuffer buffer = (ByteBuffer) message;
            if (buffer.hasArray()) {
                array = buffer.array();
            } else {
                logger.debug("We support heap ByteBuffer only.");
            }
        } else if (message instanceof byte[]) {
            array = (byte[]) message;
        }
        if (array != null) {
            message = new String(array, charset);
        }
        context.fireChannelRead(message);
    }

}
