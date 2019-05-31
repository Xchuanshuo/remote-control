package com.example.legend.common.packet;


import com.example.legend.common.Constants;

import java.io.Serializable;

/**
 * @author Legend
 * @data by on 19-5-12.
 * @description
 */
public abstract class AbstractPacket<T> implements Serializable {

    public int seq;
    private T data;
    private String attach;
    /** 0表示本地 1表示远程 **/
    public int identifier;
    public long length;
    public long offset;

    public AbstractPacket(T data) {
        this(data, Constants.REMOTE);
    }

    public AbstractPacket(T data, int identifier) {
        this(data, identifier, 0, 0);
    }

    public AbstractPacket(T data, long length) {
        this(data, 0, length);
    }

    public AbstractPacket(T data, long offset, long length) {
        this(data, Constants.REMOTE, offset, length);
    }

    public AbstractPacket(T data, int identifier, long offset, long length) {
        this.data = data;
        this.identifier = identifier;
        this.offset = offset;
        this.length = length;
    }

    /**
     * 当前包的类型
     * @return
     */
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
