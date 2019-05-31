package com.example.legend.common.packet;


import com.example.legend.common.Constants;

/**
 * @author Legend
 * @data by on 19-5-12.
 * @description
 */
public class FileListRequestPacket extends AbstractPacket<String> {

    public FileListRequestPacket(String path) {
        super(path);
    }

    public FileListRequestPacket(String path, int identifier) {
        super(path, identifier);
    }

    @Override
    public String type() {
        return Constants.FILE_LIST_REQUEST;
    }

}
