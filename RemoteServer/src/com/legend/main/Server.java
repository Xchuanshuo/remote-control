package com.legend.main;


import com.example.legend.common.Constants;
import com.example.legend.common.ReceiveCallback;
import com.example.legend.common.Utils;
import com.example.legend.common.core.DownloadScheduler;
import com.example.legend.common.core.TaskHandleCenter;
import com.example.legend.common.packet.*;
import com.example.legend.common.task.BaseFileSendTask;
import com.example.legend.common.task.GetLocalFiles;
import com.legend.main.core.*;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static com.example.legend.common.Constants.*;

/**
 * @author Legend
 * @data by on 19-5-18.
 * @description
 */
public class Server implements ReceiveCallback<AbstractPacket> {

    TaskHandleCenter handleCenter = TaskHandleCenter.getInstance();
    private ServerSocket serverSocket;
    private DealStringMsgTask dealStringMsgTask = new DealStringMsgTask();
    private DownloadScheduler scheduler = new DownloadScheduler();

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
    public void receiveMessage(AbstractPacket packet) {
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
                try {
                    FileDownloadRequestPacket fdPacket = (FileDownloadRequestPacket) packet;
                    File file = new File(fdPacket.data());
                    // 构造响应包
                    FileDownloadResponsePacket responsePacket =
                            new FileDownloadResponsePacket(file.getName(), fdPacket.offset, fdPacket.length);
                    responsePacket.seq = fdPacket.seq;
                    handleCenter.sendPacket(responsePacket);

                    Socket socket = serverSocket.accept();
                    new BaseFileSendTask((FileDownloadRequestPacket) packet, socket).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case FILE_DOWNLOAD_RESPONSE:
                try {
                    Socket socket = serverSocket.accept();
                    packet.setAttach(FileApi.getHomeDirectoryPath() + "/RemoteControl/");
                    scheduler.doTask((FileDownloadResponsePacket) packet, socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case STRING_MSG_REQUEST:
                dealStringMsgTask.run((StringRequestPacket) packet);
                break;
            case FILE_REQUEST:
                // 响应文件请求 客户端收到该响应后才可以开始请求下载
                FileRequestPacket fileRequestPacket = (FileRequestPacket) packet;
                File file = new File(fileRequestPacket.data());
                FileResponsePacket fileResponsePacket = new FileResponsePacket(fileRequestPacket.data(),
                        file.length());
                fileResponsePacket.setAttach(file.getName());
                handleCenter.sendPacket(fileResponsePacket);
                break;
            case FILE_RESPONSE:
                new MultiThreadDownloadFileTask(FileApi.getHomeDirectoryPath() +"/RemoteControl/" ,
                        (FileResponsePacket) packet).start();
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
                socket.getOutputStream().write(-1);
                socket.close();
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
