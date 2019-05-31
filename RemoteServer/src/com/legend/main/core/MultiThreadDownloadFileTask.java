package com.legend.main.core;

import com.example.legend.common.packet.FileResponsePacket;
import com.example.legend.common.task.BaseMultiThreadDownloadTask;

/**
 * @author Legend
 * @data by on 19-6-1.
 * @description
 */
public class MultiThreadDownloadFileTask extends BaseMultiThreadDownloadTask {

    private int lastProgress = 0;

    public MultiThreadDownloadFileTask(String basePath, FileResponsePacket packet) {
        super(basePath, packet);
    }

    @Override
    public void preExecute() {
        System.out.println("Download Start....");
    }

    @Override
    public void updateProgress(int value) {
        if (lastProgress != value) {
            System.out.println("file download progress: " + value);
            lastProgress = value;
        }
    }

    @Override
    public void postExecute() {
        super.postExecute();
        System.out.println("Download finished...");
    }
}
