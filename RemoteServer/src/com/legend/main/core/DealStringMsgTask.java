package com.legend.main.core;

import com.example.legend.common.packet.StringRequestPacket;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

import static com.example.legend.common.Constants.*;

/**
 * @author Legend
 * @data by on 19-5-19.
 * @description 处理普通字符串消息
 */
public class DealStringMsgTask {

    public void run(StringRequestPacket packet) {
        String message = packet.data();
        String[] strings = message.split(":");
        if (strings.length >= 2) {
            String type = strings[0];
            String info = strings[1];
            switch (type) {
                case MOUSE:
                    mouseMove(info);
                    break;
                case LEFT_BUTTON:
                    leftButton(info);
                    break;
                case RIGHT_BUTTON:
                    rightButton(info);
                    break;
                case MOUSE_WHEEL:
                    mouseWheel(info);
                    break;
                case KEYBOARD:
                    try {
                        keyBoard(info);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case POWER:
                    power(info);
                    break;
                default: break;
            }
        }
    }

    private void power(String command) {
        Runtime runtime = Runtime.getRuntime();
        try {
            switch (command) {
                case POWER_TURN_OFF:
                    runtime.exec("shutdown -s");
                    break;
                case POWER_RESTART:
                    runtime.exec("shutdown -r now");
                    break;
                case POWER_HIBERNATE:
                    runtime.exec("shutdown -h");
                    break;
                default: break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void keyBoard(String info) throws Exception {
        String[] args = info.split(",");
        String type = null, key = null;
        String keyState = null;
        Robot robot = new Robot();
        if (args.length == 2) {
            type = args[0];
            key = args[1];
        } else if (args.length == 3) {
            type = args[0];
            key = args[1];
            keyState = args[2];
        }
        if ("message".equals(type)) {
            // 调用剪切板
            paste(robot, Toolkit.getDefaultToolkit().getSystemClipboard(), key);
        } else if ("key".equals(type)) {
            keyClick(robot, key, keyState);
        } else if ("dos_message".equals(type)) {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(key);
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            String value;
            while ((value = reader.readLine() ) != null) {
                paste(robot, clipboard, value + "\n");
            }
            reader.close();
            inputStream.close();
        }
    }

    private void mouseWheel(String info) {
        try {
            Robot robot = new Robot();
            float num = Float.valueOf(info);
            robot.mouseWheel(num > 0 ? 1 : -1);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private void rightButton(String info) {
        try {
            Robot robot = new Robot();
            if ("down".equals(info)) {
                robot.mousePress(InputEvent.BUTTON3_MASK);
            } else if ("release".equals(info)) {
                robot.mouseRelease(InputEvent.BUTTON3_MASK);
            } else if ("up".equals(info)) {
                robot.mouseRelease(InputEvent.BUTTON3_MASK);
            } else if ("click".equals(info)) {
                robot.mousePress(InputEvent.BUTTON3_MASK);
                robot.mouseRelease(InputEvent.BUTTON3_MASK);
            }
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private void leftButton(String info) {
        try {
            Robot robot = new Robot();
            if ("down".equals(info)) {
                robot.mousePress(InputEvent.BUTTON1_MASK);
            } else if ("release".equals(info)) {
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
            } else if ("up".equals(info)) {
                robot.mousePress(InputEvent.BUTTON1_MASK);
            } else if ("click".equals(info)) {
                robot.mousePress(InputEvent.BUTTON1_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
            }
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private void mouseMove(String info) {
        String[] args = info.split(",");
        String x = args[0], y = args[1];
        float px = Float.valueOf(x);
        float py = Float.valueOf(y);

        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        Point point = pointerInfo.getLocation();
        // 得到电脑鼠标当前的坐标
        /* 鼠标坐标 */ /** 鼠标坐标 **/double mx = point.getX();
        double my = point.getY();
        try {
            Robot robot = new Robot();
            robot.mouseMove((int)(mx + px), (int)(my + py));
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private static void keyClick(Robot robot, String key, String keyState) {
        String KEY = "VK_" + key.toUpperCase();
        try {
            Field field = KeyEvent.class.getField(KEY);
            if (field == null) return;
            int keyCode = field.getInt(KEY);
            System.out.println(keyCode);
            if ("click".equals(keyState)) {
                robot.keyPress(keyCode);
                robot.keyRelease(keyCode);
            } else if ("down".equals(keyState)) {
                robot.keyPress(keyCode);
            } else if ("up".equals(keyState)) {
                robot.keyRelease(keyCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void paste(Robot robot, Clipboard clipboard, String value) {
        clipboard.setContents(new StringSelection(null), null);
        clipboard.setContents(new StringSelection(value), null);
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_V);
    }

    public static void main(String[] args) throws AWTException {
        Robot robot = new Robot();
        System.out.println(KeyEvent.VK_UP);
        DealStringMsgTask.keyClick(robot, "Up", null);
    }
}
