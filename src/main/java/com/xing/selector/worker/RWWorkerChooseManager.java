package com.xing.selector.worker;

import com.xing.selector.AbstractChooseManager;
import com.xing.strategy.ChooseStrategy;
import java.util.concurrent.ExecutorService;

public class RWWorkerChooseManager extends AbstractChooseManager<RWWorker> {

    public RWWorkerChooseManager(int s, ExecutorService executor) {
        super(s, executor);
    }

    public RWWorkerChooseManager(int s, ExecutorService executor,
                                 ChooseStrategy<RWWorker> chooseStrategy) {
        super(s, executor, chooseStrategy);
    }

    @Override
    protected RWWorker newCandidate() {
        return new RWWorker(executor);
    }
}
