package com.legend.main;


import com.example.legend.common.Constants;
import com.example.legend.common.ReceiveCallback;
import com.example.legend.common.Utils;
import com.example.legend.common.core.TaskHandleCenter;
import com.example.legend.common.packet.*;
import com.example.legend.common.task.GetLocalFiles;
import com.example.legend.common.task.GetLocalFilesTask;
import com.legend.main.core.DealStringMsgTask;
import com.legend.main.core.FileApi;
import com.legend.main.core.FileDownloadTask;
import com.legend.main.core.FileSendTask;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static com.example.legend.common.Constants.*;

/**
 * @author Legend
 * @data by on 19-5-18.
 * @description
 */
public class Server implements ReceiveCallback<Packet> {

    TaskHandleCenter handleCenter = TaskHandleCenter.getInstance();
    private ServerSocket serverSocket;
    private DealStringMsgTask dealStringMsgTask = new DealStringMsgTask();

    public Server() {
        try {
            handleCenter.setReceiveCallback(this);
            this.serverSocket = new ServerSocket(Constants.TCP_PORT);
            System.out.println(serverSocket.getLocalSocketAddress());
            new ListenerThread(serverSocket).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receiveMessage(Packet packet) {
        System.out.println(packet + " : " + packet.data());
        String type = packet.type();
        switch (type) {
            case FILE_LIST_REQUEST:
                FileListRequestPacket requestPacket = (FileListRequestPacket) packet;
                if ("/".equals(requestPacket.data())) requestPacket.setData(FileApi.getHomeDirectoryPath());
                new GetLocalFiles(requestPacket, fileListResponsePacket -> {
                    if (requestPacket.data().equals(FileApi.getHomeDirectoryPath())) {
                        fileListResponsePacket.setAttach(FileApi.getHomeDirectoryPath());
                    }
                    handleCenter.sendPacket(fileListResponsePacket);
                });
                break;
            case FILE_DOWNLOAD_REQUEST:
                new FileSendTask((FileDownloadRequestPacket) packet, serverSocket).start();
                break;
            case FILE_UPLOAD_REQUEST:
                new FileDownloadTask((FileUploadRequestPacket) packet, serverSocket).start();
                break;
            case STRING_MSG_REQUEST:
                dealStringMsgTask.run((StringRequestPacket) packet);
                break;
            default: break;
        }
    }

    class ListenerThread extends Thread {
        private ServerSocket serverSocket;
        public ListenerThread(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            try {
                Socket socket = serverSocket.accept();
                socket.setKeepAlive(false);
                Constants.TARGET_IP = String.valueOf(socket.getInetAddress()).substring(1);
                System.out.println(socket.getInetAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        System.out.println(Utils.getLocalAddress());
        new Server();
    }
}
