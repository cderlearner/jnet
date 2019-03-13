package com.xing.selector;

import com.xing.lifecycle.LifeCycle;

/**
 * 对一组线程进行管理.
 **/
public interface ChooseManager<T> extends LifeCycle {

    /**
     * 从管理的线程中选取一个.
     */
    T chooseOne(Object param);
}
