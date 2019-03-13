package com.xing.selector;

import com.xing.event.ChannelActiveEvent;
import com.xing.event.ChannelInActiveEvent;
import com.xing.event.ChannelReadEvent;
import com.xing.handler.ChannelHandlerChain;
import com.xing.lifecycle.LifeCycle;
import com.xing.util.log.LogManager;
import com.xing.util.log.api.ILog;
import com.xing.selector.worker.RWWorkerChooseManager;
import com.xing.handler.ChannelHandlerContext;

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

public final class MultiAcceptSelector implements Runnable, LifeCycle {
    private static final ILog logger = LogManager.getLogger(MultiAcceptSelector.class);

    private static final int defaultAllocateSize = 1024;
    private final Lock lock = new ReentrantLock();

    private Selector selector;
    // jobs 限制最大1000
    private final ArrayDeque<Runnable> jobs;
    private final ExecutorService executor;
    private boolean closed = false;

    private SelectorChooseManager selectorChooseManager;
    private RWWorkerChooseManager rwWorkerChooseManager;

    private final Runnable eventProcessor = new EventLoop();

    MultiAcceptSelector(ExecutorService executor) {
        this(0, executor);
    }

    private MultiAcceptSelector(int capacity, ExecutorService executor) {
        if (capacity < 1)
            capacity = 1000;
        jobs = new ArrayDeque<>(capacity);
        this.executor = executor;
    }

    @Override
    public void start() {
        try {
            selector = Selector.open();
            executor.execute(this);
        } catch (IOException e) {
            logger.error("Selector open failed: " + e.getMessage());
        }
    }

    public boolean register(SocketChannel channel, int ops) {
        Register register = new Register(channel, ops);
        boolean result;
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
                eventProcessor.run(); //select and processSelectedKeys
            else
                task.run();  // 处理任务
        }
    }

    void setSelectorChooseManager(SelectorChooseManager selectorManager) {
        this.selectorChooseManager = selectorManager;
    }

    void setRwWorkerChooseManager(RWWorkerChooseManager rwWorkerChooseManager) {
        this.rwWorkerChooseManager = rwWorkerChooseManager;
    }

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
                ChannelHandlerChain handlerChain = selectorChooseManager.getHandlerChain();
                ChannelHandlerContext context = new ChannelHandlerContext(
                        handlerChain.copyInBoundHandlers(),
                        handlerChain.copyOutBoundHandlers(), true);
                context.setChannel(channel);
                context.setRwWorkerChooseManager(rwWorkerChooseManager);
                key.attach(context);
                rwWorkerChooseManager.chooseOne(channel).submit(new ChannelActiveEvent(context));
            } catch (ClosedChannelException e) {
                logger.error("Channel register failed: " + e.getMessage());
            }
        }

    }

    private class EventLoop implements Runnable {

        @Override
        public void run() {
            try {
                int i = selector.select();
                if (i > 0) {
                    Set<SelectionKey> keys = selector.selectedKeys();
                    for (SelectionKey key : keys) {
                        if (key.isReadable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(defaultAllocateSize);
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
                ChannelHandlerChain handlerChain = selectorChooseManager.getHandlerChain();
                context = new ChannelHandlerContext(handlerChain.copyInBoundHandlers(),
                                                    handlerChain.copyOutBoundHandlers(),
                                            true);
                context.setChannel(channel);
                context.setRwWorkerChooseManager(rwWorkerChooseManager);
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
            rwWorkerChooseManager.chooseOne(context.getChannel()).submit(new ChannelInActiveEvent(context));
            key.cancel();
        }

        /**
         * 处理读事件.
         *
         * @param buffer {@link ByteBuffer} 读取的数据
         * @param key    {@link SelectionKey}
         * @throws IOException
         */
        private void processRead(ByteBuffer buffer, SelectionKey key) {
            ChannelHandlerContext context = checkAttachmentGetContext(key.attachment(), (SocketChannel) key.channel());
            rwWorkerChooseManager.chooseOne(context.getChannel()).submit(new ChannelReadEvent(context, buffer));
        }
    }

}
