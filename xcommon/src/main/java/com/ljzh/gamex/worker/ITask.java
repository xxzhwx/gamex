package com.ljzh.gamex.worker;

public interface ITask extends Runnable {
    default String getName() {
        return this.getClass().getSimpleName();
    }
}
