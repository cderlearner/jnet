package com.xing.strategy;

import com.xing.log.LogManager;
import com.xing.log.api.ILog;
import com.xing.worker.RWWorker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 *同一个连接的事件被分发到同一个线程.
 *
 */
public class PortChooseStrategy extends AbstractChooseStrategy<RWWorker> {

    private static final ILog logger = LogManager.getLogger(PortChooseStrategy.class);

    @Override
    public RWWorker doChoose(Object param) {
        if (!(param instanceof SocketChannel)) {
            throw new IllegalArgumentException(
                    "The param must be SocketChannel.");
        }
        RWWorker worker;
        try {
            SocketChannel channel = (SocketChannel) param;
            InetSocketAddress address = (InetSocketAddress) channel.getRemoteAddress();
            int port = address.getPort();
            worker = candidates.get(port % length);
        } catch (IOException e) {
            logger.debug("Remote connection has closed.");
            // 采用简单的递增策略
            worker = candidates.get(index);
        }
        return worker;
    }

}
