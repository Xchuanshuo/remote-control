package com.example.legend.common.task;

import com.example.legend.common.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author legend
 */
public class MsgSendTask implements Runnable {

    private DatagramSocket socket;
    private byte[] data;

    public MsgSendTask(DatagramSocket socket, byte[] bytes) {
        this.socket = socket;
        this.data = bytes;
    }

    @Override
    public void run() {
        try {
            InetAddress inetAddress = InetAddress.getByName(Constants.TARGET_IP);
            DatagramPacket packet = new DatagramPacket(data, data.length,
                    inetAddress, Constants.UDP_PORT);
            socket.send(packet);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
