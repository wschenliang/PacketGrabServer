package com.jiangnan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 *  工具栏配置
 *
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class HomeJMenuBar extends JMenuBar {
    private static final Font GLOBAL_FONT = new Font("微软雅黑", Font.PLAIN, 20);
    public HomeJMenuBar(JFrame jFrame) {
        this.setFont(GLOBAL_FONT);
        this.setPreferredSize(new Dimension(30,30));
        JMenu fileMenu = new JMenu("文件");
        JMenu editMenu = new JMenu("编辑");
        JMenu toolMenu = new JMenu("工具");
        JMenu aboutMenu = new JMenu("关于");
        JMenu exitMenu = new JMenu("退出");

        JMenuItem open = new JMenuItem("打开");
        open.addActionListener(new AbstractAction("打开") {
            private static final long serialVersionUID = 3282234629826064532L;

            @Override
            public void actionPerformed(ActionEvent e) {
                //显示一个文件选择器
                JFileChooser fileChooser = new JFileChooser(".");
                fileChooser.showOpenDialog(jFrame);
                File file = fileChooser.getSelectedFile();
            }
        });

        JMenuItem save = new JMenuItem("保存");
        save.addActionListener(new AbstractAction("保存") {
            private static final long serialVersionUID = -8866231964669855500L;

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(".");
                fileChooser.showOpenDialog(jFrame);
                File file = fileChooser.getSelectedFile();
            }
        });

        fileMenu.add(open);
        fileMenu.add(save);
        fileMenu.addSeparator();//添加分割线
        fileMenu.add(editMenu);

        this.add(fileMenu);
        this.add(editMenu);
        this.add(toolMenu);
        this.add(aboutMenu);
    }
}
