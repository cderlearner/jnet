package com.xing.handler.decoder;

import com.xing.handler.ChannelHandlerContext;
import com.xing.handler.InBoundHandlerAdapter;
import com.xing.util.log.LogManager;
import com.xing.util.log.api.ILog;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DelimiterBasedDecoder extends InBoundHandlerAdapter {
    private static final ILog logger = LogManager.getLogger(DelimiterBasedDecoder.class);

    private final byte delimiter;
    //上次处理的剩余
    private byte[] todo;
    private static final int defaultMaxLength = 1024;
    private final int maxLength;

    public DelimiterBasedDecoder(char delimiter) {
        this(delimiter, 0);
    }

    public DelimiterBasedDecoder(char delimiter, int maxLength) {
        if (delimiter > 127) {
            throw new IllegalArgumentException("We support ASCII code only.");
        }
        this.delimiter = (byte) delimiter;
        if (maxLength > 0) {
            this.maxLength = maxLength;
        } else {
            this.maxLength = defaultMaxLength;
        }
    }

    @Override
    public void channelRead(Object message, ChannelHandlerContext context) {
        if (message instanceof ByteBuffer) {
            ByteBuffer buffer = (ByteBuffer) message;
            if (buffer.hasArray()) {
                buffer.flip();
                byte[] array = buffer.array();
                List<byte[]> out = new ArrayList<>();
                int start = 0, i = 0;
                for (int l = buffer.limit(); i < l; i++) {
                    if (array[i] == delimiter) {
                        byte[] result = null;
                        if (start == 0 && todo != null) {
                            //处理上次读取的剩余
                            int tl = todo.length, length = tl + i;
                            if (check(length)) return;
                            result = new byte[length];
                            System.arraycopy(todo, 0, result, 0, tl);
                            System.arraycopy(array, 0, result, tl, i);
                            todo = null;
                        } else {
                            int length = i - start;
                            if (check(length)) return;
                            result = new byte[length];
                            System.arraycopy(array, start, result, 0, length);
                        }
                        out.add(result);
                        start = i + 1;
                    }
                }
                if (array[i - 1] != delimiter) {
                    int length = i - start;
                    if (check(length)) return;
                    todo = new byte[length];
                    System.arraycopy(array, start, todo, 0, length);
                }
                context.fireChannelReads(out);
            } else {
                logger.debug("We support heap buffer only.");
            }
        } else {
            context.fireChannelRead(message);
        }
    }

    /**
     * 检查内容长度是否达到最大值.
     *
     * @param i 内容长度
     * @return true, 如果达到
     */
    private boolean check(int i) {
        boolean result = i > maxLength;
        if (result) {
            logger.debug("The content length " + i + "exceeds the max length " + maxLength);
        }
        return result;
    }

}
