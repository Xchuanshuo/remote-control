package com.example.legend.common.packet;

import com.example.legend.common.Constants;

/**
 * @author Legend
 * @data by on 19-5-12.
 * @description
 */
public class FileDownloadResponsePacket extends AbstractPacket<String> {

    public FileDownloadResponsePacket(String name, long offset, long length) {
        super(name, offset, length);
    }

    @Override
    public String type() {
        return Constants.FILE_DOWNLOAD_RESPONSE;
    }
}
