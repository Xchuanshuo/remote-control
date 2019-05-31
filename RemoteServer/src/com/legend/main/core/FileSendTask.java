package com.legend.main.core;

import com.example.legend.common.core.TaskHandleCenter;
import com.example.legend.common.packet.FileDownloadRequestPacket;
import com.example.legend.common.packet.FileDownloadResponsePacket;

import java.io.*;
import java.net.Socket;

/**
 * @author Legend
 * @data by on 19-5-18.
 * @description
 */
public class FileSendTask extends Thread {

    TaskHandleCenter handleCenter = TaskHandleCenter.getInstance();
    private Socket socket;
    private String path;
    private long startPos, length;
    private int seq;

    public FileSendTask(FileDownloadRequestPacket packet, Socket socket) {
        this.path = packet.data();
        this.seq = packet.seq;
        this.startPos = packet.offset;
        this.length = packet.length;
        this.socket = socket;
        System.out.println(seq + " startPos: "+ startPos + " length: " + length);
    }

    @Override
    public void run() {
        try {
            OutputStream outputStream = socket.getOutputStream();
            // 方便客户端进行绑定
            outputStream.write(seq);
            byte[] buffer = new byte[1024 * 1024];
            InputStream inputStream = socket.getInputStream();
            int len = inputStream.read(buffer);
            if (len == -1) {
                return;
            }
            String msg = new String(buffer, 0, len);
            System.out.println("client state: " + msg);
            if (!"OK".equals(msg)) {
                return;
            }

            File file = new File(path);
            RandomAccessFile sourceRAF = new RandomAccessFile(file, "rw");
            sourceRAF.seek(startPos);
            long remaining = length;
            int read = 0;
            while (remaining > 0 && (read = sourceRAF.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
                System.out.println("seq " + seq +" rm: " + remaining + ", read: " + read);
                remaining -= read;
                outputStream.write(buffer, 0, read);
            }
            System.out.println("written: rm: " + remaining + " read: " + read);
            sourceRAF.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
