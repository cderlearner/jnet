package com.xing.manager;

import com.xing.lifecycle.LifeCycle;
import com.xing.strategy.ChooseStrategy;
import com.xing.strategy.DefaultChooseStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public abstract class AbstractManager<T> implements Manager<T> {

    protected List<T> candidates;

    private ChooseStrategy<T> chooseStrategy;

    private final int candidatesSize;

    protected final ExecutorService executor;

    public AbstractManager(int candidatesSize, ExecutorService executor) {
        this(candidatesSize, executor, null);
    }

    public AbstractManager(int candidatesSize, ExecutorService executor, ChooseStrategy<T> chooseStrategy) {
        if (candidatesSize < 1) {
            throw new IllegalArgumentException("The candidates count cant't be less than 1.");
        }
        candidates = new ArrayList<T>(candidatesSize);
        this.candidatesSize = candidatesSize;
        this.executor = executor;
        if (chooseStrategy != null) {
            this.chooseStrategy = chooseStrategy;
        } else {
            this.chooseStrategy = new DefaultChooseStrategy<T>();
        }
    }

    @Override
    public void start() {
        for (int i = 0; i < candidatesSize; i++) {
            T candidate = newCandidate();
            candidates.add(candidate);
            if (candidate instanceof LifeCycle) {
                LifeCycle lifeCycle = (LifeCycle) candidate;
                lifeCycle.start();
            }
        }
        chooseStrategy.setCandidates(candidates);
    }

    /**
     * 生成一个候选人.
     *
     * @return <T> 候选者
     */
    protected abstract T newCandidate();

    @Override
    public T one(Object param) {
        return chooseStrategy.choose(param);
    }

}
