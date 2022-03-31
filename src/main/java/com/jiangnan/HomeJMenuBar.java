package com.jiangnan;

import com.jiangnan.filter.PcapFileFilter;
import com.jiangnan.model.PacketData;
import com.jiangnan.model.PacketQueue;
import com.jiangnan.utils.PacketUtil;
import jpcap.JpcapCaptor;
import jpcap.JpcapWriter;
import jpcap.packet.Packet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

/**
 *  工具栏配置
 *
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class HomeJMenuBar extends JMenuBar {

    private final JFrame jFrame;
    private final HomeJPanel jPanel;

    private final JMenu fileMenu, editMenu, toolMenu, aboutMenu;
    private final JMenuItem openItem, saveItem;

    public HomeJMenuBar(JFrame jFrame, HomeJPanel jPanel) {
        this.jFrame = jFrame;
        this.jPanel = jPanel;
        this.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        this.setPreferredSize(new Dimension(30,30));
        fileMenu = new JMenu("文件");
        editMenu = new JMenu("编辑");
        toolMenu = new JMenu("工具");
        aboutMenu = new JMenu("关于");

        openItem = new JMenuItem("打开");
        saveItem = new JMenuItem("保存");
        openItemAction();
        saveItemAction();

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();//添加分割线

        this.add(fileMenu);
        this.add(editMenu);
        this.add(toolMenu);
        this.add(aboutMenu);
    }

    private void saveItemAction() {
        saveItem.addActionListener(new AbstractAction("保存") {
            private static final long serialVersionUID = -8866231964669855500L;

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(".");
                fileChooser.addChoosableFileFilter(new PcapFileFilter());
                int val = fileChooser.showSaveDialog(jFrame);
                if (val != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                File file = fileChooser.getSelectedFile();
                //文件写入操作
                String absolutePath = file.getAbsolutePath();
                //获取插入的所有文件
                JpcapWriter writer = null;
                try {
                    writer = JpcapWriter.openDumpFile(jPanel.getCaptor(), absolutePath);
                    LinkedList<Packet> packets = PacketQueue.getPackets();
                    for (Packet packet : packets) {
                        writer.writePacket(packet);
                    }
                } catch (IOException exception) {
                    System.out.println(exception.getCause().toString());
                }finally {
                    if (writer != null) {
                        writer.close();
                    }
                }
            }
        });
    }

    private void openItemAction() {
        openItem.addActionListener(new AbstractAction("打开") {
            private static final long serialVersionUID = 3282234629826064532L;

            @Override
            public void actionPerformed(ActionEvent e) {
                //文件选择器
                JFileChooser fileChooser = new JFileChooser(".");
                fileChooser.addChoosableFileFilter(new PcapFileFilter());
                int val = fileChooser.showOpenDialog(jFrame);
                if (val != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                File file = fileChooser.getSelectedFile();
                //文件读取操作
                String absolutePath = file.getAbsolutePath();
                JpcapCaptor captor = null;
                try {
                    captor = JpcapCaptor.openFile(absolutePath);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                if (captor == null) {
                    return;
                }
                DefaultTableModel model = (DefaultTableModel) jPanel.getjTable().getModel();
                model.setRowCount(0);
                PacketUtil.init();
                //将packets输出到jTable上
                while (true) {
                    Packet packet = captor.getPacket();
                    if (packet == null || packet.data == null) break;
                    PacketData packetData = PacketUtil.convertPacket2Data(packet);
                    model.addRow(packetData.getDataArrays());
                }
            }
        });

    }
}
