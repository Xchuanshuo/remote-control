package com.example.legend.common.packet;

import com.example.legend.common.Constants;

/**
 * @author Legend
 * @data by on 19-5-28.
 * @description
 */
public class FileRequestPacket extends AbstractPacket<String> {

    public FileRequestPacket(String path) {
        super(path);
    }

    public FileRequestPacket(String path, long length) {
        super(path, length);
    }

    @Override
    public String type() {
        return Constants.FILE_REQUEST;
    }
}
