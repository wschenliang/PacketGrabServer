package com.jiangnan;

import com.jiangnan.filter.PcapFileFilter;
import com.jiangnan.model.PacketData;
import com.jiangnan.model.PacketQueue;
import com.jiangnan.receiver.PacketReceiverImpl;
import com.jiangnan.utils.JpcapUtil;
import com.jiangnan.utils.PacketUtil;
import jpcap.JpcapCaptor;
import jpcap.JpcapWriter;
import jpcap.NetworkInterface;
import jpcap.packet.Packet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

/**
 *
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class JpcapJFrame {
    private static final Font GLOBAL_FONT = new Font("微软雅黑", Font.PLAIN, 20);
    private static JpcapCaptor captor = null;
    private static PacketQueue queue = new PacketQueue();

    public static void main(String[] args) {
        //设置主题
        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFrame jf = new JFrame("Wireshark");
        jf.setFont(GLOBAL_FONT);
        jf.setSize(1000,1000);
        jf.setLayout(new BorderLayout());
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //设置面板 borderLayout分为东南西北中
        JPanel mainPanel = new JPanel(new BorderLayout(2,2));

        //开始按钮
        JButton startButton = new JButton();
        startButton.setBackground(Color.BLACK);
        startButton.setIcon(new ImageIcon("src/main/resources/image/wireshark_start.png"));

        //停止按钮
        JButton stopButton = new JButton();
        stopButton.setIcon(new ImageIcon("src/main/resources/image/wireshark_stop.png"));
        stopButton.setBackground(Color.BLACK);
        stopButton.setEnabled(false);//停止不可用

        //读取按钮
        JButton readButton = new JButton();
        readButton.setIcon(new ImageIcon("src/main/resources/image/wireshark_open.png"));
        readButton.setBackground(Color.BLACK);

        //保存按钮
        JButton saveButton = new JButton();
        saveButton.setIcon(new ImageIcon("src/main/resources/image/wireshark_save.png"));
        saveButton.setBackground(Color.BLACK);
        saveButton.setEnabled(false);//停止不可用

        //网卡选择
        JComboBox<String> deviceSelect = new JComboBox<>(JpcapUtil.getDeviceIp());

        //表格
        DefaultTableModel model = new DefaultTableModel(null, PacketData.getTitle());
        JTable jtable = new JTable(model);
        jtable.setRowHeight(25);//每条数据的单行高度
        jtable.setPreferredScrollableViewportSize(new Dimension(800,200));
        jtable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);//设置表格，每次点击选择一个连续得范围

        //开始按钮点击事件
        startButton.addActionListener(new AbstractAction() {
            private static final long serialVersionUID = -5422233935591297014L;
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                //数据清空启动
                model.setRowCount(0);
                PacketUtil.init();
                //根据选择的网卡名称，来启动对应网卡的监听
                String selectedItem = (String) deviceSelect.getSelectedItem();
                NetworkInterface device = JpcapUtil.findDeviceByIP(selectedItem);
                captor = JpcapUtil.getCaptor(device);
                //开启线程2执行循环监听捕获数据，注意线程2是无法使用线程1的本地线程栈内容的，因此，想要保存数据得传递过去
                new Thread(() -> captor.loopPacket(-1, new PacketReceiverImpl(model, queue))).start();
            }
        });

        //停止按钮点击事件
        stopButton.addActionListener(new AbstractAction() {
            private static final long serialVersionUID = 4447805863352575674L;
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                if (captor != null) {
                    captor.breakLoop();
                }
                //判断有没有数据，如果有数据，保存按钮要亮起来
                if (model.getRowCount() > 0) {
                    saveButton.setEnabled(true);
                }
            }
        });

        //读取按钮添加事件
        readButton.addActionListener(new AbstractAction() {
            private static final long serialVersionUID = -1138468420354500040L;

            @Override
            public void actionPerformed(ActionEvent e) {
                //文件选择器
                JFileChooser fileChooser = new JFileChooser(".");
                fileChooser.addChoosableFileFilter(new PcapFileFilter());
                int val = fileChooser.showOpenDialog(jf);
                if (val != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                File file = fileChooser.getSelectedFile();
                //文件读取操作
                String absolutePath = file.getAbsolutePath();
                try {
                    captor = JpcapCaptor.openFile(absolutePath);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
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

        //保存按钮添加事件
        saveButton.addActionListener(new AbstractAction() {
            private static final long serialVersionUID = -3192698988668486101L;

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(".");
                fileChooser.addChoosableFileFilter(new PcapFileFilter());
                int val = fileChooser.showSaveDialog(jf);
                if (val != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                File file = fileChooser.getSelectedFile();
                //文件写入操作
                String absolutePath = file.getAbsolutePath();
                //获取插入的所有文件
                JpcapWriter writer = null;
                try {
                    writer = JpcapWriter.openDumpFile(captor, absolutePath);
                    LinkedList<Packet> packets = queue.getPackets();
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

        //上top层 添加按钮和网卡选择框
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.black);
        topPanel.add(startButton);
        topPanel.add(stopButton);
        topPanel.add(deviceSelect);
        topPanel.add(readButton);
        topPanel.add(saveButton);

        //mid层 添加表格
        JScrollPane midPane = new JScrollPane(jtable);

        //底部层 文本域 显示数据
        JTextArea area = new JTextArea();
        JScrollPane bottomPane = new JScrollPane(area);
        bottomPane.setPreferredSize(new Dimension(500,400));
        JScrollBar verticalScrollBar = bottomPane.getVerticalScrollBar();
        verticalScrollBar.setValue(0);

        //表格点击事件 底部层显示数据
        jtable.addMouseListener(new MouseAdapter() {//鼠标单机事件
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = jtable.getSelectedRow();
                Object jsonInfo = jtable.getValueAt(selectedRow, 6);
                String text = jsonInfo.toString();
                area.setText(text);
            }
        });

        //主布局管理容器添加上中下
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(midPane,BorderLayout.CENTER);
        mainPanel.add(bottomPane,BorderLayout.SOUTH);

        jf.setContentPane(mainPanel);
        jf.setVisible(true);
        mainPanel.setVisible(true);

    }
}
