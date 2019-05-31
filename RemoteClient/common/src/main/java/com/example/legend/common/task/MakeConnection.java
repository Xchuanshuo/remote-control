package com.example.legend.common.task;

import com.example.legend.common.ReceiveCallback;
import com.example.legend.common.core.TaskHandleCenter;

import java.net.Socket;

/**
 * @author Legend
 * @data by on 19-5-19.
 * @description
 */
public class MakeConnection {

    public MakeConnection(ReceiveCallback<Socket> receiveCallback) {
        TaskHandleCenter handleCenter = TaskHandleCenter.getInstance();
        handleCenter.commit(new MakeConnectionTask(receiveCallback));
    }

}
