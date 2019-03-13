package com.xing.server;

import com.xing.handler.ChannelHandlerChain;
import com.xing.handler.Handler;
import com.xing.lifecycle.LifeCycle;
import com.xing.selector.SelectorChooseManager;
import com.xing.strategy.PortChooseStrategy;
import com.xing.util.NamedThreadFactory;
import com.xing.util.log.LogManager;
import com.xing.util.log.api.ILog;
import com.xing.selector.worker.RWWorkerChooseManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class AcceptorServer implements LifeCycle {
    private final static ILog logger = LogManager.getLogger(AcceptorServer.class);

    private final ExecutorService executor;

    private ServerSocketChannel serverSocketChannel;
    private final SelectorChooseManager selectorChooseManager;
    private final RWWorkerChooseManager rwWorkerChooseManager;

    // 默认以阻塞的方式监听
    private boolean block = true;
    private final int acceptors;

    private final ChannelHandlerChain handlerChain;

    public AcceptorServer() {
        this(1, Runtime.getRuntime().availableProcessors() * 2);
    }

    public AcceptorServer(int acceptors, int workers) {
        executor = Executors.newFixedThreadPool(acceptors + workers, new NamedThreadFactory("jnet-eventLoop"));
        // 监听连接管理器
        selectorChooseManager = new SelectorChooseManager(acceptors, executor);
        // 读写事件处理管理器
        rwWorkerChooseManager = new RWWorkerChooseManager(workers, executor, new PortChooseStrategy());

        this.acceptors = acceptors;
        this.handlerChain = new ChannelHandlerChain();

        selectorChooseManager.setHandlerChain(handlerChain);
        selectorChooseManager.setWorkerManager(rwWorkerChooseManager);
    }

    /**
     * 监听到指定的端口.
     */
    public AcceptorServer bind(int port) {
        if (port < 1)
            throw new IllegalArgumentException(
                    "The param port can't be negative.");
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().setReuseAddress(true);
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(block);
        } catch (IOException e) {
            logger.error("The port " + port + "bind failed.");
            System.exit(1);
        }
        return this;
    }

    public AcceptorServer withHandlers(Handler... handlers) {
        Stream.of(handlers).forEach(handlerChain::addHandler);
        return this;
    }

    @Override
    public void start() {
        rwWorkerChooseManager.start();
        selectorChooseManager.start();
        // 支持多个selector监听连接
        IntStream.range(0, acceptors).forEach(i -> {
            executor.execute(new Acceptor());
        });
        logger.info("AcceptorServer starts successfully.");
    }

    private class Acceptor implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    SocketChannel channel = serverSocketChannel.accept();
                    channel.configureBlocking(false);
                    boolean result = selectorChooseManager
                            .chooseOne(null)
                            .register(channel, SelectionKey.OP_READ);
                    if (!result) {
                        // register operation failed
                        logger.debug("Register channel to selector failed.");
                    }
                } catch (IOException e) {
                    logger.debug("Client accepted failed: " + e.getMessage());
                }
            }
        }
    }

}
