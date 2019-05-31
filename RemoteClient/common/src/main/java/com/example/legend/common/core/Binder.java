package com.example.legend.common.core;

import java.io.InputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Legend
 * @data by on 19-5-30.
 * @description
 */
public class Binder {

    private Map<Integer, Socket> socketMap = new ConcurrentHashMap<>();
    private Map<Integer, InputStream> inputStreamMap = new ConcurrentHashMap<>();

    public void bind(int seq, Socket socket, InputStream inputStream) {
        socketMap.put(seq, socket);
        inputStreamMap.put(seq, inputStream);
    }

    public Socket getSocketBySeq(int seq) {
        return socketMap.get(seq);
    }

    public InputStream getInputStreamBySeq(int seq) {
        return inputStreamMap.get(seq);
    }

    public static Binder getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static Binder INSTANCE = new Binder();
    }


    public void clear() {
        socketMap.clear();
        inputStreamMap.clear();
    }
}
