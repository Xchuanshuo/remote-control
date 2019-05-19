package com.example.legend.common;

/**
 * @author Legend
 * @data by on 19-5-12.
 * @description
 */
public interface ReceiveCallback<T> {

    /**
     * 接收数据回调
     * @param t 接收到的数据包
     */
    void receiveMessage(T t);
}
