package com.jiangnan.constants;

import javax.swing.*;
import java.net.URL;

/**
 *  此类保存图片链接
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class IconUtils {

    //用这种方式打包才会显示图片
    private final URL MAIN_ICON = getClass().getResource("/image/wireshark.png");
    private final URL START = getClass().getResource("/image/wireshark_start.png");
    private final URL STOP = getClass().getResource("/image/wireshark_stop.png");
    private final URL SAVE = getClass().getResource("/image/wireshark_save.png");
    private final URL OPEN = getClass().getResource("/image/wireshark_open.png");
    private final URL OPTIONS = getClass().getResource("/image/wireshark_options.png");
    private final URL CLOSE = getClass().getResource("/image/wireshark_close.png");
    private final URL RESTART = getClass().getResource("/image/wireshark_restart.png");

    public ImageIcon mainIcon() {
        return new ImageIcon(MAIN_ICON);
    }

    public Icon startIcon() {
        return new ImageIcon(START);
    }

    public Icon stopIcon() {
        return new ImageIcon(STOP);
    }

    public Icon saveIcon() {
        return new ImageIcon(SAVE);
    }

    public Icon openIcon() {
        return new ImageIcon(OPEN);
    }

    public Icon optionsIcon() {
        return new ImageIcon(OPTIONS);
    }

    public Icon closeIcon() {
        return new ImageIcon(CLOSE);
    }

    public Icon restartIcon() {
        return new ImageIcon(RESTART);
    }
}
