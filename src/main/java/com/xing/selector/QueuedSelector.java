package com.xing.selector;

import com.xing.event.ChannelActiveEvent;
import com.xing.event.ChannelInActiveEvent;
import com.xing.event.ChannelReadEvent;
import com.xing.handler.ChannelHandlerChain;
import com.xing.lifecycle.LifeCycle;
import com.xing.log.LogManager;
import com.xing.log.api.ILog;
import com.xing.worker.RWWorkerManager;
import com.xing.context.ChannelHandlerContext;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class QueuedSelector implements Runnable, LifeCycle {

    private static final ILog logger = LogManager
            .getLogger(QueuedSelector.class);

    private static final int defaultAllocateSize = 1024;

    private Selector selector;
    private final ArrayDeque<Runnable> jobs;
    private final ExecutorService executor;
    private boolean closed = false;

    private final Runnable eventProcessor = new EventProcessor();
    private final Lock lock = new ReentrantLock();
    private SelectorManager selectorManager;
    private RWWorkerManager workerManager;

    public QueuedSelector(ExecutorService executor) {
        this(0, executor);
    }

    public QueuedSelector(int capacity, ExecutorService executor) {
        if (capacity < 1)
            capacity = 1000;
        jobs = new ArrayDeque<>(1000);
        this.executor = executor;
    }

    public void setSelectorManager(SelectorManager selectorManager) {
        this.selectorManager = selectorManager;
    }

    public void setWorkerManager(RWWorkerManager workerManager) {
        this.workerManager = workerManager;
    }

    /**
     * 启动Selector.
     */
    @Override
    public void start() {
        try {
            selector = Selector.open();
            executor.execute(this);
        } catch (IOException e) {
            logger.error("Selector open failed: " + e.getMessage());
        }
    }

    /**
     * Register the channel to the selector with the interested ops.
     *
     * @return boolean 是否提交成功
     */
    public boolean register(SocketChannel channel, int ops) {
        Register register = new Register(channel, ops);
        boolean result = false;
        lock.lock();
        try {
            result = jobs.offer(register);
        } finally {
            lock.unlock();
        }
        if (result) {
            // 唤醒Selector
            selector.wakeup();
        }
        return result;
    }

    @Override
    public void run() {
        while (!closed) {
            Runnable task;
            lock.lock();
            try {
                task = jobs.poll();
            } finally {
                lock.unlock();
            }
            if (task == null)
                eventProcessor.run();
            else
                task.run();
        }
    }

    /**
     * 向Selector注册感兴趣的事件.
     *
     * @author skywalker
     */
    private class Register implements Runnable {

        private final SocketChannel channel;
        private final int ops;

        public Register(SocketChannel channel, int ops) {
            this.channel = channel;
            this.ops = ops;
        }

        @Override
        public void run() {
            try {
                SelectionKey key = channel.register(selector, ops);
                // fire the ChannelActiveEvent
                ChannelHandlerChain handlerChain = selectorManager.getHandlerChain();
                ChannelHandlerContext context = new ChannelHandlerContext(
                        handlerChain.getInBoundHandlers(),
                        handlerChain.getOutBoundHandlers(), true);
                context.setChannel(channel);
                context.setWorkerManager(workerManager);
                key.attach(context);
                workerManager.one(channel).submit(new ChannelActiveEvent(context));
            } catch (ClosedChannelException e) {
                logger.error("Channel register failed: " + e.getMessage());
            }
        }

    }

    /**
     * 处理Selector事件.
     *
     * @author skywalker
     */
    private class EventProcessor implements Runnable {

        @Override
        public void run() {
            try {
                int i = selector.select();
                if (i > 0) {
                    Set<SelectionKey> keys = selector.selectedKeys();
                    for (SelectionKey key : keys) {
                        if (key.isReadable()) {
                            SocketChannel channel = (SocketChannel) key
                                    .channel();
                            ByteBuffer buffer = ByteBuffer
                                    .allocate(defaultAllocateSize);
                            int n = channel.read(buffer);
                            if (n == -1) {
                                processInActive(key);
                            } else {
                                processRead(buffer, key);
                            }
                        }
                    }
                    keys.clear();
                }
            } catch (IOException e) {
                logger.debug("Selector was closed.");
                closed = true;
            }
        }

        /**
         * 如果SelectionKey.attachment()返回空，那么重新构造一个HandlerContext, 否则使用原有的.
         *
         * @param attachment {@link Object}
         * @param channel    {@link SocketChannel}
         * @return {@link ChannelHandlerContext}
         */
        private ChannelHandlerContext checkAttachmentGetContext(Object attachment, SocketChannel channel) {
            ChannelHandlerContext context;
            if (attachment == null) {
                ChannelHandlerChain handlerChain = selectorManager.getHandlerChain();
                context = new ChannelHandlerContext(handlerChain.getInBoundHandlers(),
                        handlerChain.getOutBoundHandlers(), true);
                context.setChannel(channel);
                context.setWorkerManager(workerManager);
            } else {
                context = (ChannelHandlerContext) attachment;
            }
            return context;
        }

        /**
         * 客户端断开连接.
         */
        private void processInActive(SelectionKey key) {
            ChannelHandlerContext context = checkAttachmentGetContext(key.attachment(), (SocketChannel) key.channel());
            workerManager.one(context.getChannel()).submit(new ChannelInActiveEvent(context));
            key.cancel();
        }

        /**
         * 处理读事件.
         *
         * @param buffer {@link ByteBuffer} 读取的数据
         * @param key    {@link SelectionKey}
         * @throws IOException
         */
        private void processRead(ByteBuffer buffer, SelectionKey key)
                throws IOException {
            ChannelHandlerContext context = checkAttachmentGetContext(key.attachment(), (SocketChannel) key.channel());
            workerManager.one(context.getChannel()).submit(new ChannelReadEvent(context, buffer));
        }
    }

}
