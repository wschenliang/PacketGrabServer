package com.jiangnan.utils;


import javax.swing.*;
import java.io.Serializable;

/**
 *
 *  日志工具类
 *
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class LogUtils {

    private static JTextArea log;

    public static void log(String text) {
        log.append("\n");
        log.append(text);
    }

    public static <T extends Serializable> void log(String text, T t) {
        log(text);
        log(FastjsonUtils.toJSONStringPretty(t));
    }

    public static void setLogArea(JTextArea logArea) {
        log = logArea;
    }

    public static JTextArea getLogArea() {
        return log;
    }
}
