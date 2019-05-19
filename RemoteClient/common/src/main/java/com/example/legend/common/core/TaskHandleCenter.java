package com.example.legend.common.core;

import com.example.legend.common.Constants;
import com.example.legend.common.ReceiveCallback;
import com.example.legend.common.packet.Packet;
import com.example.legend.common.task.MsgSendTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Legend
 * @data by on 19-4-13.
 * @description
 */
public class TaskHandleCenter {

    private static class Holder {
        private static TaskHandleCenter center = new TaskHandleCenter();
    }

    public static TaskHandleCenter getInstance() {
        return Holder.center;
    }

    private ExecutorService service;
    private DatagramSocket datagramSocket;
    private ReceiveCallback<Packet> receiveCallback;
    private Socket socket;

    private TaskHandleCenter() {
        this.service = new ThreadPoolExecutor(8, 200, 3, TimeUnit.MILLISECONDS
                , new LinkedBlockingQueue<>(1024), new NameableThreadFactory("demo")
                , new ThreadPoolExecutor.AbortPolicy());
        try {
            this.datagramSocket = new DatagramSocket(Constants.UDP_PORT);
            new MsgReceiveThread(datagramSocket).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setReceiveCallback(ReceiveCallback<Packet> receiveCallback) {
        this.receiveCallback = receiveCallback;
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    public void setSocket(Socket socket) {
        closeSocket();
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public void commit(Runnable runnable) {
        service.execute(runnable);
    }

    public void sendPacket(Packet packet) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(packet);
            oos.flush();
            byte[] bytes = baos.toByteArray();
            commit(new MsgSendTask(datagramSocket, bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class MsgReceiveThread extends Thread {

        private DatagramSocket ds;
        private byte[] data = new byte[1024 * 200];
        private ObjectInputStream inputStream;

        public MsgReceiveThread(DatagramSocket socket) {
            this.ds = socket;
        }

        @Override
        public void run() {
            while (ds != null) {
                try {
                    DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
                    ds.receive(datagramPacket);
                    inputStream = new ObjectInputStream(new ByteArrayInputStream(data));
                    Packet packet = (Packet) inputStream.readObject();

                    if (receiveCallback != null) {
                        receiveCallback.receiveMessage(packet);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void closeSocket() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void exit() {
        if (service != null) {
            service.shutdownNow();
        }
        if (datagramSocket != null) {
            datagramSocket.close();
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
