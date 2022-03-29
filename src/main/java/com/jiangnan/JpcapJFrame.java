package com.jiangnan;

import com.jiangnan.model.PacketData;
import com.jiangnan.receiver.PacketReceiverImpl;
import com.jiangnan.utils.ContextUtil;
import com.jiangnan.utils.FastjsonUtils;
import com.jiangnan.utils.JpcapUtil;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;

/**
 *
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class JpcapJFrame {
    private static final Font GLOBAL_FONT = new Font("微软雅黑", Font.PLAIN, 20);

    public static void main(String[] args) {
        //设置主题
        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //设置窗体
        JFrame frame = new JFrame("Wireshark");
        FontUIResource fontRes = new FontUIResource(GLOBAL_FONT);
        for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontRes);
            }
        }
        frame.setSize(1500,1000);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //设置面板 borderLayout分为东南西北中
        JPanel mainPanel = new JPanel(new BorderLayout(2,2));
        //顶部 采用流式面板，自动排版
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(Color.black);

        //依次 单选框选择网卡，启动按钮，停止按钮，读取按钮，保存按钮
        JButton startButton = new JButton("go");
        startButton.setBackground(Color.GREEN);
        //startButton.setIcon(new ImageIcon("image/close.svg"));
        JButton stopButton = new JButton("stop");
        stopButton.setBackground(Color.RED);
        stopButton.setEnabled(false);//停止不可用
        JButton readButton = new JButton("read");
        JButton saveButton = new JButton("save");
        JComboBox<String> deviceSelect = new JComboBox<>(JpcapUtil.getDeviceIp());
        DefaultTableModel model = new DefaultTableModel(null, PacketData.getTitle());
        JTable jtable = new JTable(model);
        jtable.setRowHeight(25);//每条数据的单行高度
        jtable.setPreferredScrollableViewportSize(new Dimension(800,200));
        //开始点击事件
        startButton.addActionListener(new AbstractAction() {
            private static final long serialVersionUID = -5422233935591297014L;
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                //数据清空启动
                DefaultTableModel model = (DefaultTableModel)jtable.getModel();
                model.setRowCount(0);
                //根据选择的网卡名称，来启动对应网卡的监听
                String selectedItem = (String) deviceSelect.getSelectedItem();
                NetworkInterface device = JpcapUtil.findDeviceByIP(selectedItem);
                //开启线程2执行循环监听捕获数据
                new Thread(() -> {
                    //启动时候创建本地线程栈
                    ContextUtil.start(device);
                    JpcapCaptor captor = ContextUtil.getJpcapCaptor();
                    long startTime = System.currentTimeMillis();
                    ContextUtil.setStartTime(startTime);//设置启动时间
                    captor.loopPacket(-1, new PacketReceiverImpl(model));
                }).start();
            }
        });
        //停止按钮监听
        stopButton.addActionListener(new AbstractAction() {
            private static final long serialVersionUID = 4447805863352575674L;
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                ContextUtil.finish();
            }
        });

        //top层
        topPanel.add(startButton);
        topPanel.add(stopButton);
        topPanel.add(readButton);
        topPanel.add(saveButton);
        topPanel.add(deviceSelect);

        //mid层
        JScrollPane midPane = new JScrollPane(jtable);
        midPane.setBackground(Color.BLACK);

        //底部层
        final JTextArea area = new JTextArea(10,50);
        JScrollPane buttom = new JScrollPane(area);
        buttom.setPreferredSize(new Dimension(500,400));
        JScrollBar verticalScrollBar = buttom.getVerticalScrollBar();
        verticalScrollBar.setValue(0);

        //布局管理容器添加上中下
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(midPane,BorderLayout.CENTER);
        mainPanel.add(buttom,BorderLayout.SOUTH);

        frame.setContentPane(mainPanel);
        mainPanel.setVisible(true);
        frame.setVisible(true);

        //设置表格，每次点击选择一个连续得范围
        jtable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jtable.addMouseListener(new MouseAdapter() {//鼠标单机事件
            @Override
            public void mouseClicked(MouseEvent e) {
                String text = afterTableClick(jtable);
                area.setText(text);
            }
        });
    }

    private static String afterTableClick(JTable jtable) {
        int selectedRow = jtable.getSelectedRow();
        Object jsonInfo = jtable.getValueAt(selectedRow, 6);
        return jsonInfo.toString();
    }
}
