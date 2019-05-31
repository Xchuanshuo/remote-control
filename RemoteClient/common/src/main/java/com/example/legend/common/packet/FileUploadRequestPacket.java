package com.example.legend.common.packet;

import com.example.legend.common.Constants;

/**
 * @author Legend
 * @data by on 19-5-13.
 * @description
 */
public class FileUploadRequestPacket extends AbstractPacket<String> {

    public FileUploadRequestPacket(String name, long offset, long length) {
        super(name, offset, length);
    }

    @Override
    public String type() {
        return Constants.FILE_UPLOAD_REQUEST;
    }
}
