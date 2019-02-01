package com.xing.selector;

import com.xing.handler.ChannelHandlerChain;
import com.xing.strategy.ChooseStrategy;
import com.xing.selector.worker.RWWorkerChooseManager;
import java.util.concurrent.ExecutorService;

/**
 * Selector管理器，负责对Selector的启动、负载均衡处理.
 */
public class SelectorChooseManager extends AbstractChooseManager<MultiAcceptSelector> {

    private ChannelHandlerChain handlerChain;
    private RWWorkerChooseManager workerManager;

    public SelectorChooseManager(int s, ExecutorService executor) {
        super(s, executor);
    }

    public SelectorChooseManager(int s, ExecutorService executor,
                                 ChooseStrategy<MultiAcceptSelector> chooseStrategy) {
        super(s, executor, chooseStrategy);
    }

    public void setHandlerChain(ChannelHandlerChain handlerChain) {
        this.handlerChain = handlerChain;
    }

    public ChannelHandlerChain getHandlerChain() {
        return handlerChain;
    }

    public void setWorkerManager(RWWorkerChooseManager workerManager) {
        this.workerManager = workerManager;
    }

    @Override
    protected MultiAcceptSelector newCandidate() {
        MultiAcceptSelector selector = new MultiAcceptSelector(executor);
        selector.setSelectorChooseManager(this);
        selector.setRwWorkerChooseManager(workerManager);
        return selector;
    }

}
