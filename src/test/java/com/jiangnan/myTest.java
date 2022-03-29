package com.jiangnan;


import com.jiangnan.utils.JpcapUtil;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.packet.Packet;

import javax.swing.*;

public class myTest {

    public static void main(String[] args) {
        // 可跨平台的默认风格
        String lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame("抓包工具");
        frame.setLocation(1000,500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(200,200);
        frame.setVisible(true);

    }

}
