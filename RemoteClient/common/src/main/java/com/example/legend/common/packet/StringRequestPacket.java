package com.example.legend.common.packet;

import com.example.legend.common.Constants;

/**
 * @author Legend
 * @data by on 19-5-18.
 * @description
 */
public class StringRequestPacket extends AbstractPacket<String> {

    public StringRequestPacket(String data) {
        super(data);
    }

    @Override
    public String type() {
        return Constants.STRING_MSG_REQUEST;
    }
}
