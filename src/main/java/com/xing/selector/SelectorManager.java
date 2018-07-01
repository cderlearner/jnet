package com.xing.selector;

import com.xing.handler.ChannelHandlerChain;
import com.xing.manager.AbstractManager;
import com.xing.strategy.ChooseStrategy;
import com.xing.worker.RWWorkerManager;
import java.util.concurrent.ExecutorService;

/**
 * Selector管理器，负责对Selector的启动、负载均衡处理.
 */
public class SelectorManager extends AbstractManager<QueuedSelector> {

    private ChannelHandlerChain handlerChain;
    private RWWorkerManager workerManager;

    public SelectorManager(int s, ExecutorService executor) {
        super(s, executor);
    }

    public SelectorManager(int s, ExecutorService executor,
                           ChooseStrategy<QueuedSelector> chooseStrategy) {
        super(s, executor, chooseStrategy);
    }

    public void setHandlerChain(ChannelHandlerChain handlerChain) {
        this.handlerChain = handlerChain;
    }

    public ChannelHandlerChain getHandlerChain() {
        return handlerChain;
    }

    public void setWorkerManager(RWWorkerManager workerManager) {
        this.workerManager = workerManager;
    }

    @Override
    protected QueuedSelector newCandidate() {
        QueuedSelector selector = new QueuedSelector(executor);
        selector.setSelectorManager(this);
        selector.setWorkerManager(workerManager);
        return selector;
    }

}
