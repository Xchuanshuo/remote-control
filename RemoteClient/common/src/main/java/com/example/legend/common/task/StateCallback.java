package com.example.legend.common.task;

/**
 * @author Legend
 * @data by on 19-5-29.
 * @description
 */
public interface StateCallback {

    void preExecute();

    void updateProgress(int value);

    void postExecute();
}
