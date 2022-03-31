package com.jiangnan.constants;

import javax.swing.*;

/**
 *  此类保存图片链接
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class IconUtils {

    public static final String MAIN_ICON = "src/main/java/image/wireshark.png";
    public static final String START = "src/main/java/image/wireshark_start.png";
    public static final String STOP = "src/main/java/image/wireshark_stop.png";
    public static final String SAVE = "src/main/java/image/wireshark_save.png";
    public static final String OPEN = "src/main/java/image/wireshark_open.png";
    public static final String OPTIONS = "src/main/java/image/wireshark_options.png";
    public static final String CLOSE = "src/main/java/image/wireshark_close.png";
    public static final String RESTART = "src/main/java/image/wireshark_restart.png";


    public static ImageIcon mainIcon() {
        return new ImageIcon(MAIN_ICON);
    }

    public static Icon startIcon() {
        return new ImageIcon(START);
    }

    public static Icon stopIcon() {
        return new ImageIcon(STOP);
    }

    public static Icon saveIcon() {
        return new ImageIcon(SAVE);
    }

    public static Icon openIcon() {
        return new ImageIcon(OPEN);
    }

    public static Icon optionsIcon() {
        return new ImageIcon(OPTIONS);
    }

    public static Icon closeIcon() {
        return new ImageIcon(CLOSE);
    }

    public static Icon restartIcon() {
        return new ImageIcon(RESTART);
    }
}
