package com.xing.strategy;

import java.util.List;

public interface ChooseStrategy<T> {
    T choose(Object param);

    void setCandidates(List<T> candidates);
}
