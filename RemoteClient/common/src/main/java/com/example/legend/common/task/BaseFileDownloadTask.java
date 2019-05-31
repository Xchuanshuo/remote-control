package com.example.legend.common.task;

import com.example.legend.common.core.TaskHandleCenter;
import com.example.legend.common.packet.FileDownloadResponsePacket;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.CountDownLatch;

/**
 * @author Legend
 * @data by on 19-5-12.
 * @description
 */
public class BaseFileDownloadTask extends Thread {

    private TaskHandleCenter handleCenter = TaskHandleCenter.getInstance();
    private InputStream inputStream;
    private String fileName, basePath;
    private long fileSize;
    private BaseMultiThreadDownloadTask downloadTask;
    private int seq;
    private long startPos;
    private CountDownLatch latch;

    public BaseFileDownloadTask(FileDownloadResponsePacket packet, InputStream inputStream) {
        this.seq = packet.seq;
        this.fileName = packet.data();
        this.startPos = packet.offset;
        this.fileSize = packet.length;
        this.basePath = packet.getAttach();
        this.downloadTask = handleCenter.getDownloadTask(fileName);
        this.latch = downloadTask.getLatch();
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        RandomAccessFile downloadRAF = null;
        RandomAccessFile tempFileRAF = null;
        String downloadFilePath = basePath + fileName;
        String tempFilepath = downloadFilePath + "_tmp";
        try {
            downloadRAF = new RandomAccessFile(downloadFilePath, "rw");
            downloadRAF.seek(startPos);
            System.out.println("seq " + seq + " Writer startPos: " + startPos + " target: " + (startPos + fileSize));
            tempFileRAF = new RandomAccessFile(tempFilepath, "rw");
        } catch (Exception e) {
            e.printStackTrace();
        }
        long remaining = fileSize;
        assert downloadRAF != null;
        assert tempFileRAF != null;
        int read = 0;
        byte[] buffer = new byte[1024 * 1024];
        try {
            while (remaining > 0 && (read = inputStream.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1){
                remaining -= read;
                // 写入实际文件
                downloadRAF.write(buffer, 0, read);

                downloadTask.receiveLength(read);

                // 长度信息写入临时文件
                startPos += read;
                tempFileRAF.seek(seq * 8 + 8);
                tempFileRAF.writeLong(startPos);
            }
            System.out.println("seq " + seq + " Written : " + startPos);
            downloadRAF.close();
            tempFileRAF.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            latch.countDown();
        }
    }
}
