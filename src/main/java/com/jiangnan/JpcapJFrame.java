package com.jiangnan;

import com.jiangnan.constants.IconUtils;

import javax.swing.*;
import java.awt.*;

/**
 *  启动框架
 *
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class JpcapJFrame extends JFrame {

    public JpcapJFrame() throws HeadlessException {
        //设置图标
        this.setIconImage(new IconUtils().mainIcon().getImage());
        //设置主题
        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.setTitle("陈亮版Wireshark");
        this.setSize(1400,1000);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //homeJPanel
        HomeJPanel homePanel = new HomeJPanel(this);
        //homeJMenuBar
        HomeJMenuBar homeJMenuBar = new HomeJMenuBar(this, homePanel);

        this.setContentPane(homePanel);
        this.setJMenuBar(homeJMenuBar);

        this.setVisible(true);

    }

    //启动入口
    public static void main(String[] args) {
        new JpcapJFrame();
    }
}
