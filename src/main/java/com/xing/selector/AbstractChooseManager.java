package com.xing.selector;

import com.xing.lifecycle.LifeCycle;
import com.xing.strategy.ChooseStrategy;
import com.xing.strategy.DefaultChooseStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;


public abstract class AbstractChooseManager<T> implements ChooseManager<T> {

    private List<T> candidates;
    private ChooseStrategy<T> chooseStrategy;

    protected final ExecutorService executor;
    private final int candidatesSize;

    public AbstractChooseManager(int candidatesSize, ExecutorService executor) {
        this(candidatesSize, executor, null);
    }

    public AbstractChooseManager(int candidatesSize, ExecutorService executor, ChooseStrategy<T> chooseStrategy) {
        if (candidatesSize < 1) {
            throw new IllegalArgumentException("The candidates count cant't be less than 1.");
        }
        candidates = new ArrayList<>(candidatesSize);
        this.candidatesSize = candidatesSize;
        this.executor = executor;
        if (chooseStrategy != null) {
            this.chooseStrategy = chooseStrategy;
        } else {
            this.chooseStrategy = new DefaultChooseStrategy<>();
        }
    }

    @Override
    public void start() {
        IntStream.range(0, candidatesSize).forEach(i -> {
            T candidate = newCandidate();
            candidates.add(candidate);
            if (candidate instanceof LifeCycle) {
                LifeCycle lifeCycle = (LifeCycle) candidate;
                lifeCycle.start();
            }
        });
        chooseStrategy.setCandidates(candidates);
    }

    /**
     * 生成一个候选人.
     *
     * @return <T> 候选者
     */
    protected abstract T newCandidate();

    @Override
    public T chooseOne(Object param) {
        return chooseStrategy.choose(param);
    }

}
