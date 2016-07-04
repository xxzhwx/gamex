package com.ljzh.gamex.worker;

import com.ljzh.gamex.CommonLogger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TaskWorkerGroup {
    private String name;
    private TaskWorker[] workers;
    private CountDownLatch shutdownSignal;

    public TaskWorkerGroup(String name, int workerCount, int workerCapacity) {
        this.name = name;
        this.initWorkers(workerCount, workerCapacity);
    }

    private void initWorkers(int workerCount, int workerCapacity) {
        workers = new TaskWorker[workerCount];
        for (int i = 0; i < workerCount; i++) {
            workers[i] = new TaskWorker(name + "-" + i, workerCapacity);
            workers[i].start();
        }
    }

    public boolean addTask(int index, ITask task) {
        TaskWorker worker = workers[index];
        return worker.addTask(task);
    }

    public int getTaskSize() {
        int size = 0;
        for (TaskWorker w : workers) {
            size += w.getTaskSize();
        }
        return size;
    }

    public void shutdown() {
        shutdownSignal = new CountDownLatch(getWorkerCount());
        for (TaskWorker w : workers) {
            w.shutdown(shutdownSignal);
        }
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) {
        try {
            return shutdownSignal.await(timeout, unit);
        } catch (InterruptedException e) {
            CommonLogger.error(e, "InterruptedException of %s", getName());
            return false;
        } finally {
            logShutOk();
        }
    }

    public void shutdownNow() {
        for (TaskWorker w : workers) {
            w.shutdownNow();
        }

        for (TaskWorker w : workers) {
            try {
                w.join();
            } catch (InterruptedException e) {
                CommonLogger.error(e, "InterruptedException of %s", getName());
            }
        }

        logShutOk();
    }

    private void logShutOk() {
        CommonLogger.info("TaskWorkerGroup[%s] shut down.", getName());
    }

    public String getName() {
        return name;
    }

    public int getWorkerCount() {
        return workers.length;
    }
}
