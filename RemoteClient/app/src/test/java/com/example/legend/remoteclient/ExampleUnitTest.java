package com.example.legend.remoteclient;

import org.junit.Test;

import java.io.IOException;
import java.net.Socket;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void connectTest() throws IOException {
        Socket socket = new Socket("192.168.43.21", 9999);
        System.out.println(socket);
    }
}