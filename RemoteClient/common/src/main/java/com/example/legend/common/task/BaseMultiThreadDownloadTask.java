package com.example.legend.common.task;

import com.example.legend.common.core.Binder;
import com.example.legend.common.core.TaskHandleCenter;
import com.example.legend.common.packet.FileDownloadRequestPacket;
import com.example.legend.common.packet.FileResponsePacket;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.concurrent.CountDownLatch;

/**
 * @author Legend
 * @data by on 19-5-28.
 * @description
 */
public class BaseMultiThreadDownloadTask extends Thread
        implements StateCallback {

    private TaskHandleCenter handleCenter = TaskHandleCenter.getInstance();
    private String filePath, fileName;
    private String targetFilePath;
    private String tempFilePath;
    private long fileLength;
    private int threadNum = 4;
    private long fileSliceLength;
    private long[] startPos, endPos;
    private long totalWritten = 0;
    private CountDownLatch latch;

    public BaseMultiThreadDownloadTask(String basePath, FileResponsePacket packet) {
        this.targetFilePath = packet.data();
        this.fileName = packet.getAttach();
        this.filePath = basePath + fileName;
        this.tempFilePath = filePath + "_tmp";
        this.fileLength = packet.length;
        this.fileSliceLength = fileLength / threadNum;
        this.startPos = new long[threadNum];
        this.endPos = new long[threadNum];
        this.latch = new CountDownLatch(threadNum);
    }

    @Override
    public void run() {
        try {
            preExecute();
            download();
            postExecute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void download() throws Exception {
        File file = new File(filePath);
        File tempFile = new File(tempFilePath);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        System.out.println("filePath: " + filePath + " ," + "fileLength= "
                + fileLength + " the fileSliceLength= " + fileSliceLength);
        if (file.exists() && file.length() == fileLength) {
            System.out.println("the file you want to download has existed!");
            return;
        } else {
            setBreakPoint(startPos, endPos, tempFile);
            // 把下载任务调度类存储起来
            handleCenter.saveDownloadTask(fileName, this);
            for (int i = 0;i < threadNum;i++) {
                System.out.println("unchecked : thread " + i + " startPos: " + startPos[i] +
                        " endPos: " + endPos[i]);
                if (startPos[i] < endPos[i]) {
                    System.out.println("thread " + i + " startPos: " + startPos[i] +
                            " endPos: " + endPos[i]);
                    FileDownloadRequestPacket requestPacket =
                            new FileDownloadRequestPacket(targetFilePath, startPos[i],
                                     endPos[i]-startPos[i]);
                    requestPacket.seq = i;
                    handleCenter.sendPacket(requestPacket);
                } else {
                    latch.countDown();
                }
            }
        }
        latch.await();
        if (file.length() == fileLength && tempFile.exists()) {
            tempFile.delete();
        }
    }

    public void setBreakPoint(long[] startPos, long[] endPos, File tempFile) throws Exception {
        RandomAccessFile raf;
        if (tempFile.exists()) {
            raf = new RandomAccessFile(tempFile, "rw");
            for (int i = 0;i < threadNum;i++) {
                raf.seek(8 * i + 8);
                startPos[i] = raf.readLong();

                raf.seek(8 * (i + 1000) + 16);
                endPos[i] = raf.readLong();
                totalWritten += startPos[i] - (i*fileSliceLength);
            }
            System.out.println("totalWritten: " + totalWritten + " fileLength: " + fileLength);
        } else {
            raf = new RandomAccessFile(tempFile, "rw");
            for (int i = 0;i < threadNum;i++) {
                startPos[i] = i * fileSliceLength;
                if (i == threadNum - 1) {
                    endPos[i] = fileLength;
                } else {
                    endPos[i] = (i + 1) * fileSliceLength;
                }
                raf.seek(8 * i + 8);
                raf.writeLong(startPos[i]);

                raf.seek(8 * (i + 1000) + 16);
                raf.writeLong(endPos[i]);
            }
        }

        raf.close();
    }

    @Override
    public void preExecute() {
    }

    @Override
    public void updateProgress(int value) {

    }

    @Override
    public void postExecute() {
        Binder.getInstance().clear();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void receiveLength(long len) {
        totalWritten += len;
        updateProgress((int)(((double)totalWritten/fileLength)*100));
    }
}
