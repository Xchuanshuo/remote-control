package com.example.legend.common.packet;

import com.example.legend.common.Constants;

/**
 * @author Legend
 * @data by on 19-5-13.
 * @description
 */
public class FileUploadResponsePacket extends AbstractPacket<String> {

    public FileUploadResponsePacket(String path, long offset, long length) {
        super(path, offset, length);
    }

    @Override
    public String type() {
        return Constants.FILE_UPLOAD_RESPONSE;
    }
}
