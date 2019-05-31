package com.example.legend.common.core;


import com.example.legend.common.Constants;
import com.example.legend.common.packet.FileDownloadResponsePacket;
import com.example.legend.common.task.BaseFileDownloadTask;
import com.example.legend.common.task.MakeConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Legend
 * @data by on 19-5-30.
 * @description 文件下载调度器
 *  主要是保证多线程写的时候socket通道 绑定到对应的Packet
 */
public class DownloadScheduler {

    private Binder binder = Binder.getInstance();
    private Queue<FileDownloadResponsePacket> queue = new LinkedList<>();
    private TaskHandleCenter handleCenter = TaskHandleCenter.getInstance();
    private volatile AtomicBoolean isEnd = new AtomicBoolean(true);

    public void doTask(FileDownloadResponsePacket responsePacket) {
        queue.offer(responsePacket);
        new MakeConnection(this::bindTo);
    }

    public void doTask(FileDownloadResponsePacket responsePacket, Socket socket) {
        queue.offer(responsePacket);
        bindTo(socket);
    }

    private void bindTo(Socket socket) {
        try {
            InputStream stream = socket.getInputStream();
            int seq = stream.read();
            binder.bind(seq, socket, stream);
            if (isEnd.compareAndSet(true, false)) {
                schedule();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void schedule() {
        handleCenter.commit(new DealDownloadTask());
    }

    class DealDownloadTask implements Runnable {

        @Override
        public void run() {
            while (!isEnd.get() || !queue.isEmpty()) {
                if (queue.isEmpty()) {
                    try {
                        Thread.sleep(500);
                        continue;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                FileDownloadResponsePacket packet = queue.peek();
                Socket socket = binder.getSocketBySeq(packet.seq);
                if (socket != null && !socket.isClosed()) {
                    try {
                        OutputStream outputStream = socket.getOutputStream();
                        outputStream.write(Constants.OK.getBytes());
                        InputStream inputStream = binder.getInputStreamBySeq(packet.seq);
                        new BaseFileDownloadTask(packet, inputStream).start();
                        queue.poll();
                        System.out.println("size: " + queue.size());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
