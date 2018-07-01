package com.xing.strategy;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractChooseStrategy<T> implements ChooseStrategy<T> {

    protected List<T> candidates;
    protected int length;
    protected int index = 0;
    private final Lock lock = new ReentrantLock();

    public T choose(Object param) {
        lock.lock();
        T result;
        try {
            result = doChoose(param);
            ++index;
            if (index >= length)
                index = 0;
        } finally {
            lock.unlock();
        }
        return result;    }

    public void setCandidates(List<T> candidates) {
        this.candidates = candidates;
        this.length = candidates.size();
    }

    public abstract T doChoose(Object param);

}
