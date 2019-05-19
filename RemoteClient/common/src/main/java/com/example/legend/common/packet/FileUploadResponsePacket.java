package com.example.legend.common.packet;

import com.example.legend.common.Constants;

/**
 * @author Legend
 * @data by on 19-5-13.
 * @description
 */
public class FileUploadResponsePacket extends Packet<String> {

    public FileUploadResponsePacket(String path, long length) {
        super(path);
        this.length = length;
    }

    @Override
    public String type() {
        return Constants.FILE_UPLOAD_RESPONSE;
    }
}
