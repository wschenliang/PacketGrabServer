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
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

/**
 *
 * 主面板
 *
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class HomeJPanel extends JPanel {

    private static JpcapCaptor captor = null;
    private static final PacketQueue queue = new PacketQueue();

    public HomeJPanel(JFrame jFrame) {
        //设置面板 borderLayout分为东南西北中
        this.setLayout(new BorderLayout(2,2));
        JButton startButton = getStartButton();//开始按钮
        JButton stopButton = getStopButton();//停止按钮
        JButton readButton = getReadButton();//读取按钮
        JButton saveButton = getSaveButton();//保存按钮
        JButton restartButton = getRestartButton();//重启按钮
        JButton optionButton = getOptionButton();//设置按钮
        JButton clearButton = getClearButton();//清除按钮
        JComboBox<String> deviceSelect = new JComboBox<>(JpcapUtil.getDeviceIp());//网卡选择框
        JTable jtable = getJTable();//表格
        JTextArea area = getInfoArea();
        JTextArea area2 = getArea2();

        startButtonAction(startButton, stopButton, jtable, deviceSelect);//开始按钮点击事件
        stopButtonAction(startButton,stopButton,jtable,saveButton);//停止按钮点击事件
        readButtonAction(readButton, jFrame, jtable);//读取按钮添加事件
        saveButtonAction(saveButton, jFrame);//保存按钮添加事件
        jTableAction(jtable, area);

        //上top层 添加按钮和网卡选择框
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.black);
        topPanel.add(startButton);
        topPanel.add(stopButton);
        topPanel.add(restartButton);
        topPanel.add(optionButton);
        topPanel.add(readButton);
        topPanel.add(saveButton);
        topPanel.add(clearButton);
        topPanel.add(deviceSelect);

        //mid层 添加表格
        JScrollPane midPanel = new JScrollPane(jtable);

        //底部层 显示数据
        JScrollPane bottomPanel = new JScrollPane(area);
        bottomPanel.setPreferredSize(new Dimension(400,400));
        JScrollBar verticalScrollBar = bottomPanel.getVerticalScrollBar();
        verticalScrollBar.setValue(0);

        JScrollPane rightPanel = new JScrollPane(area2);
        rightPanel.setPreferredSize(new Dimension(200, 200));

        this.add(topPanel, BorderLayout.NORTH);
        this.add(midPanel,BorderLayout.CENTER);
        this.add(bottomPanel,BorderLayout.SOUTH);
        this.add(rightPanel, BorderLayout.EAST);
        this.setVisible(true);
    }

    //获取侧边文本域
    private JTextArea getArea2() {
        JTextArea jTextArea = new JTextArea() {
            @Override
            public boolean isEditable() {
                return false;
            }
        };
        jTextArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));//设置鼠标变为文本鼠标
        return jTextArea;
    }

    //获取底部文本域
    private JTextArea getInfoArea() {
        JTextArea jTextArea = new JTextArea("info:") {
            @Override
            public boolean isEditable() {
                return false;
            }
        };
        jTextArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));//设置鼠标变为文本鼠标
        jTextArea.setBackground(Color.LIGHT_GRAY);
        return jTextArea;
    }

    //表格点击事件
    private void jTableAction(JTable jtable, JTextArea area) {
        //表格点击事件 底部层显示第七列数据
        jtable.addMouseListener(new MouseAdapter() {//鼠标单机事件
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = jtable.getSelectedRow();
                Object jsonInfo = jtable.getValueAt(selectedRow, 6);
                String text = jsonInfo.toString();
                area.setText(text);
            }
        });
    }

    //保存按钮点击事件
    private void saveButtonAction(JButton saveButton, JFrame jFrame) {
        saveButton.addActionListener(new AbstractAction() {
            private static final long serialVersionUID = -3192698988668486101L;

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
    }

    //读取按钮点击事件
    private void readButtonAction(JButton readButton, JFrame jFrame, JTable jtable) {
        readButton.addActionListener(new AbstractAction() {
            private static final long serialVersionUID = -1138468420354500040L;

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
                try {
                    captor = JpcapCaptor.openFile(absolutePath);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                DefaultTableModel model = (DefaultTableModel) jtable.getModel();
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

    //停止按钮点击事件
    private void stopButtonAction(JButton startButton, JButton stopButton, JTable jtable, JButton saveButton) {
        stopButton.addActionListener(new AbstractAction() {
            private static final long serialVersionUID = 4447805863352575674L;
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                if (captor != null) {
                    captor.breakLoop();
                }
                DefaultTableModel model = (DefaultTableModel) jtable.getModel();
                //判断有没有数据，如果有数据，保存按钮要亮起来
                if (model.getRowCount() > 0) {
                    saveButton.setEnabled(true);
                }
            }
        });
    }

    //开始按钮的点击事件
    private void startButtonAction(JButton startButton, JButton stopButton, JTable jtable, JComboBox<String> deviceSelect) {
        startButton.addActionListener(new AbstractAction() {
            private static final long serialVersionUID = -5422233935591297014L;
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                DefaultTableModel model = (DefaultTableModel) jtable.getModel();
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
    }

    private JTable getJTable() {
        DefaultTableModel model = new DefaultTableModel(null, PacketData.getTitle());
        JTable jTable = new JTable(model){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;//不允许被编辑
            }
        };
        jTable.setRowHeight(25);//每条数据的单行高度
        jTable.setPreferredScrollableViewportSize(new Dimension(800,200));
        jTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);//设置表格，每次点击选择一个连续得范围
        return jTable;
    }

    //保存按钮配置
    private JButton getSaveButton() {
        JButton jButton = new JButton();
        jButton.setIcon(new ImageIcon("src/main/resources/image/wireshark_save.png"));
        jButton.setBackground(Color.BLACK);
        jButton.setEnabled(false);//停止不可用
        return jButton;
    }

    //读取按钮配置
    private JButton getReadButton() {
        JButton jButton = new JButton();
        jButton.setIcon(new ImageIcon("src/main/resources/image/wireshark_open.png"));
        jButton.setBackground(Color.BLACK);
        return jButton;
    }

    //停止按钮配置
    private JButton getStopButton() {
        JButton jButton = new JButton();
        jButton.setIcon(new ImageIcon("src/main/resources/image/wireshark_stop.png"));
        jButton.setBackground(Color.BLACK);
        jButton.setEnabled(false);//停止不可用
        return jButton;
    }

    //开始按钮配置
    private JButton getStartButton() {
        JButton jButton = new JButton();
        jButton.setBackground(Color.BLACK);
        jButton.setIcon(new ImageIcon("src/main/resources/image/wireshark_start.png"));
        return jButton;
    }

    //重启按钮配置
    private JButton getRestartButton() {
        JButton jButton = new JButton();
        jButton.setBackground(Color.BLACK);
        jButton.setEnabled(false);
        jButton.setIcon(new ImageIcon("src/main/resources/image/wireshark_restart.png"));
        return jButton;
    }

    //设置按钮配置
    private JButton getOptionButton() {
        JButton jButton = new JButton();
        jButton.setBackground(Color.BLACK);
        jButton.setIcon(new ImageIcon("src/main/resources/image/wireshark_options.png"));
        return jButton;
    }

    //清除按钮配置
    private JButton getClearButton() {
        JButton jButton = new JButton();
        jButton.setBackground(Color.BLACK);
        jButton.setEnabled(false);
        jButton.setIcon(new ImageIcon("src/main/resources/image/wireshark_close.png"));
        return jButton;
    }
}
