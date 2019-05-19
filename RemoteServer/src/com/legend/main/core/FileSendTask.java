package com.legend.main.core;

import com.example.legend.common.core.TaskHandleCenter;
import com.example.legend.common.packet.FileDownloadRequestPacket;
import com.example.legend.common.packet.FileDownloadResponsePacket;
import com.example.legend.common.packet.FileListResponsePacket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Legend
 * @data by on 19-5-18.
 * @description
 */
public class FileSendTask extends Thread {

    TaskHandleCenter handleCenter = TaskHandleCenter.getInstance();
    private ServerSocket serverSocket;
    private String path;

    public FileSendTask(FileDownloadRequestPacket packet, ServerSocket serverSocket) {
        this.path = packet.data();
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try {
            File file = new File(path);
            long fileSize = file.length();
            handleCenter.sendPacket(new FileDownloadResponsePacket(file.getName(), fileSize));
            Socket socket = serverSocket.accept();
            OutputStream outputStream = socket.getOutputStream();
            FileInputStream fis = new FileInputStream(file);
            int remaining = (int) fileSize;
            int totalWritten = 0, read = 0;
            byte[] buffer = new byte[1024 * 1024];
            while (totalWritten < fileSize && (read = fis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                totalWritten += read;
                remaining -= read;
                outputStream.write(buffer, 0, read);
            }
            fis.close();
            outputStream.flush();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
