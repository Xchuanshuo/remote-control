package com.example.legend.common.packet;


import com.example.legend.common.Constants;

/**
 * @author Legend
 * @data by on 19-5-12.
 * @description
 */
public class FileDownloadRequestPacket extends Packet<String> {

    public FileDownloadRequestPacket(String path) {
        super(path);
    }

    @Override
    public String type() {
        return Constants.FILE_DOWNLOAD_REQUEST;
    }

}
