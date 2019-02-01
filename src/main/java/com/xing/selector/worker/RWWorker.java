package com.xing.selector.worker;

import com.xing.lifecycle.LifeCycle;
import com.xing.util.log.LogManager;
import com.xing.util.log.api.ILog;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

public class RWWorker implements Runnable, LifeCycle{
    private ILog logger = LogManager.getLogger(RWWorker.class);

    private final BlockingQueue<Runnable> jobs;
    private final ExecutorService executors;

    RWWorker(ExecutorService executors) {
        this(0, executors);
    }

    private RWWorker(int queueSize, ExecutorService executorService) {
        if (queueSize < 1) {
            queueSize = 1000;
        }
        this.jobs = new ArrayBlockingQueue<>(queueSize);
        this.executors = executorService;
    }

    @Override
    public void start() {
        executors.execute(this);
    }

    public void submit(Runnable task) {
        jobs.offer(task);
    }

    @Override
    public void run() {
        try {
            while (true) {
                Runnable task = jobs.take();
                task.run();
            }
        } catch (InterruptedException e) {
            logger.error("Thread was interrupted.");
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

}
