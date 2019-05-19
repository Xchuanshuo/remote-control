package com.example.legend.common.packet;

import com.example.legend.common.Constants;

/**
 * @author Legend
 * @data by on 19-5-13.
 * @description
 */
public class FileUploadRequestPacket extends Packet<String> {

    public FileUploadRequestPacket(String name, long length) {
        super(name);
        this.length = length;
    }

    @Override
    public String type() {
        return Constants.FILE_UPLOAD_REQUEST;
    }
}
