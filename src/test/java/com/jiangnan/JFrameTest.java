package com.jiangnan;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class JFrameTest {
    JFrame jFrame = new JFrame("测试JFileChooser");
    //创建菜单条
    JMenuBar jmb = new JMenuBar();
    //创建菜单
    JMenu jMenu = new JMenu("文件");
    JMenuItem open = new JMenuItem(new AbstractAction("打开") {
        @Override
        public void actionPerformed(ActionEvent e) {
            //显示一个文件选择器
            JFileChooser fileChooser = new JFileChooser(".");
            fileChooser.showOpenDialog(jFrame);
            File file = fileChooser.getSelectedFile();
            //进行显示
            try {
                image = ImageIO.read(file);
                drawArea.repaint();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    });
    JMenuItem save = new JMenuItem(new AbstractAction("保存") {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser(".");
            fileChooser.showOpenDialog(jFrame);
            File file = fileChooser.getSelectedFile();
            try {
                ImageIO.write(image, "jpg", file);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    });
    BufferedImage image;

    private class MyCanvas extends JPanel {
        @Override
        public void paint(Graphics g) {
            g.drawImage(image, 0, 0, null);
        }
    }

    MyCanvas drawArea = new MyCanvas();

    public void init() {
        jMenu.add(open);
        jMenu.add(save);
        jmb.add(jMenu);
        jFrame.setJMenuBar(jmb);
        drawArea.setPreferredSize(new Dimension(800, 600));
        jFrame.add(drawArea);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);
    }

    public static void main(String[] args) {
        new JFrameTest().init();
    }
}
