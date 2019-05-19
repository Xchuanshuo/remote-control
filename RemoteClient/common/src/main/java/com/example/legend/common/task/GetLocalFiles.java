package com.example.legend.common.task;

import com.example.legend.common.ReceiveCallback;
import com.example.legend.common.core.TaskHandleCenter;
import com.example.legend.common.packet.FileListRequestPacket;
import com.example.legend.common.packet.FileListResponsePacket;

/**
 * @author Legend
 * @data by on 19-5-19.
 * @description
 */
public class GetLocalFiles {

    public GetLocalFiles(FileListRequestPacket packet, ReceiveCallback<FileListResponsePacket> callback) {
        TaskHandleCenter handleCenter = TaskHandleCenter.getInstance();
        handleCenter.commit(new GetLocalFilesTask(packet, callback));
    }
}
