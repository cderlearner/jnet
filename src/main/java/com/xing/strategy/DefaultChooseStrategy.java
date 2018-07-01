package com.xing.strategy;

/**
 * 默认线程选取策略--简单的递增.
 */
public class DefaultChooseStrategy<T> extends AbstractChooseStrategy<T> {

    @Override
    public T doChoose(Object param) {
        return candidates.get(index);
    }

}
