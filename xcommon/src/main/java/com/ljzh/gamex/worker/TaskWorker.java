package com.ljzh.gamex.worker;

import com.ljzh.gamex.CommonLogger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskWorker extends Thread {
    private volatile boolean running = true;
    private volatile boolean shutting = false;
    private BlockingQueue<ITask> taskQueue;

    public TaskWorker(String name, int capacity) {
        super(name);
        this.taskQueue = new LinkedBlockingQueue<>(capacity);
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (running) {
            try {
                ITask task = taskQueue.take();
                task.run();
            } catch (InterruptedException e) {
                CommonLogger.error(e, "InterruptedException of %s", getName());
            }
        }
    }

    public boolean addTask(ITask task) {
        if (shutting) {
            CommonLogger.debug("TaskWorker[%s] reject task[%s] will shutting down.", getName(), task.getName());
            return false;
        }

        boolean ok = taskQueue.offer(task);
        if (!ok) {
            CommonLogger.debug("TaskWorker[%s] drop task[%s].", getName(), task.getName());
        }
        return ok;
    }

    public int getTaskSize() {
        return taskQueue.size();
    }

    public void shutdown(CountDownLatch doneSignal) {
        shutting = true;
        try {
            taskQueue.put(() -> {
                running = false;
                doneSignal.countDown();
            });
        } catch (InterruptedException e) {
            CommonLogger.error(e, "InterruptedException of %s", getName());
        }
    }

    public void shutdownNow() {
        shutting = true;
        running = false;
        this.interrupt();
    }
}
