package com.jiangnan;

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
        this.setIconImage(new ImageIcon("src/main/resources/image/wireshark_start.png").getImage());
        //设置主题
        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.setTitle("陈亮版Wireshark");
        this.setSize(1200,1000);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    //启动入口
    public static void main(String[] args) {
        JFrame jf = new JpcapJFrame();
        jf.setJMenuBar(new HomeJMenuBar(jf));
        JPanel mainPanel = new HomeJPanel(jf);
        jf.setContentPane(mainPanel);
        jf.setVisible(true);
    }
}
