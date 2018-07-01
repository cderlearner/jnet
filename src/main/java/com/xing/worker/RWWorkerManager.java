package com.xing.worker;

import com.xing.manager.AbstractManager;
import com.xing.strategy.ChooseStrategy;

import java.util.concurrent.ExecutorService;

public class RWWorkerManager extends AbstractManager<RWWorker>{

    public RWWorkerManager(int s, ExecutorService executor) {
        super(s, executor);
    }

    public RWWorkerManager(int s, ExecutorService executor,
                         ChooseStrategy<RWWorker> chooseStrategy) {
        super(s, executor, chooseStrategy);
    }

    @Override
    protected RWWorker newCandidate() {
        return new RWWorker(executor);
    }
}
