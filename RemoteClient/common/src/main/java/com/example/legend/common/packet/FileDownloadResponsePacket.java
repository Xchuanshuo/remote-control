package com.example.legend.common.packet;

import com.example.legend.common.Constants;

/**
 * @author Legend
 * @data by on 19-5-12.
 * @description
 */
public class FileDownloadResponsePacket extends Packet<String>{

    public FileDownloadResponsePacket(String name, long length) {
        super(name);
        this.length = length;
    }

    @Override
    public String type() {
        return Constants.FILE_DOWNLOAD_RESPONSE;
    }
}
