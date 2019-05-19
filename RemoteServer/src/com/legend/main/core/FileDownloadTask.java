package com.legend.main.core;

import com.example.legend.common.core.TaskHandleCenter;
import com.example.legend.common.packet.FileUploadRequestPacket;
import com.example.legend.common.packet.FileUploadResponsePacket;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Legend
 * @data by on 19-5-18.
 * @description
 */
public class FileDownloadTask extends Thread {

    TaskHandleCenter handleCenter = TaskHandleCenter.getInstance();
    private ServerSocket serverSocket;
    private long fileSize = 0;
    private String fileName;
    private String path;

    public FileDownloadTask(FileUploadRequestPacket packet, ServerSocket serverSocket) {
        this.fileSize = packet.length;
        this.fileName = packet.getAttach();
        this.path = packet.data();
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try {
            handleCenter.sendPacket(new FileUploadResponsePacket(path, fileSize));
            String path = FileApi.getHomeDirectoryPath() +"/RemoteControl/" + fileName;
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024 * 1024];
            int read = 0, remaining = (int) fileSize;
            long totalRead = 0;
            while (totalRead < fileSize && (read = inputStream.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                totalRead += read;
                remaining -= read;
                fos.write(buffer, 0, read);
            }
            fos.flush();
            fos.close();
            inputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
