package com.example.legend.common.packet;


import com.example.legend.common.Constants;

import java.io.Serializable;

/**
 * @author Legend
 * @data by on 19-5-12.
 * @description
 */
public abstract class Packet<T> implements Serializable {

    private T data;
    private String attach;
    /** 0表示本地 1表示远程 **/
    public int identifier;
    public long length = 0;

    public Packet(T data) {
        this(data, Constants.REMOTE);
    }

    public Packet(T data, int identifier) {
        this.data = data;
        this.identifier = identifier;
    }

    abstract public String type();

    public void setData(T data) {
        this.data = data;
    }

    public T data() {
        return data;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getAttach() {
        return attach;
    }
}
