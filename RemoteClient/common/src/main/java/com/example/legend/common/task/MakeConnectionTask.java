package com.example.legend.common.task;

import com.example.legend.common.Constants;
import com.example.legend.common.ReceiveCallback;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Legend
 * @data by on 19-5-18.
 * @description
 */
public class MakeConnectionTask implements Runnable {

    private ReceiveCallback<Socket> receiveCallback;

    public MakeConnectionTask(ReceiveCallback<Socket> receiveCallback) {
        this.receiveCallback = receiveCallback;
    }

    @Override
    public void run() {
        Socket socket = null;
        try {
            socket = new Socket(Constants.TARGET_IP, Constants.TCP_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (receiveCallback != null && socket != null) {
            receiveCallback.receiveMessage(socket);
        }
    }
}
