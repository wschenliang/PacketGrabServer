package com.jiangnan.frame;

import com.jiangnan.thread.LoopCapThread;
import com.jiangnan.utils.JpcapUtil;
import jpcap.JpcapCaptor;

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

    private static JpcapCaptor jpcapCaptor;
    private static final Font GLOBAL_FONT = new Font("微软雅黑", Font.PLAIN, 20);
    private static final String[] title = {"No.","Time","Source","Destination","Protocol","Length",
            "帧类型","源物理地址","目标物理地址","源地址端口号","目的地址端口号","IPv","片偏移","数据包长度",
            "生存时间","DF标志位","MF标志位","RF标志位","ack","ack_num","紧急指针","窗口大小","UDP包长度","序号",
            "保留标志1","保留标志2","urg","ack","psh","rst","syn","fin","服务类型","分组标识","数据包二进制"};

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
        InitGlobalFont();
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
        DefaultTableModel model = new DefaultTableModel(null, title);
        JTable jtable = new JTable(model);
        jtable.setRowHeight(25);//每条数据的单行高度
        //jtable.setPreferredScrollableViewportSize(new Dimension(800,200));
        //7-35数据不可见
        TableColumnModel columnModel = jtable.getColumnModel();
        for (int i = 11; i <=34 ; i++) {
            TableColumn column = columnModel.getColumn(i);
            column.setMaxWidth(0);
            column.setMinWidth(0);
            column.setPreferredWidth(0);
        }
        //开始点击事件
        startButton.addActionListener(new AbstractAction() {
            private static final long serialVersionUID = -5422233935591297014L;
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                DefaultTableModel model = (DefaultTableModel)jtable.getModel();
                model.setRowCount(0);
                String selectedItem = (String) deviceSelect.getSelectedItem();
                jpcapCaptor = JpcapUtil.findCaptorByIp(selectedItem);
                new Thread(new LoopCapThread(jpcapCaptor, jtable)).start();
            }
        });
        //停止按钮监听
        stopButton.addActionListener(new AbstractAction() {
            private static final long serialVersionUID = 4447805863352575674L;
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                jpcapCaptor.breakLoop();
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
        Object ack = jtable.getValueAt(selectedRow, 18);
        StringBuilder sb = new StringBuilder();
        if (ack.equals("")) {
            Object UDP_LEN = jtable.getValueAt(selectedRow, 22);
            sb.append("\nUDP包长度: ").append(UDP_LEN);
        } else {
            Object IPv = jtable.getValueAt(selectedRow, 11);
            Object Offset = jtable.getValueAt(selectedRow, 12);
            Object data_len = jtable.getValueAt(selectedRow, 14);
            Object TTL = jtable.getValueAt(selectedRow, 14);
            Object DF = jtable.getValueAt(selectedRow, 15);
            Object MF = jtable.getValueAt(selectedRow, 16);
            Object RF = jtable.getValueAt(selectedRow, 17);
            Object ack_num = jtable.getValueAt(selectedRow, 19);
            Object urgent_pointer = jtable.getValueAt(selectedRow, 20);
            Object window = jtable.getValueAt(selectedRow, 21);
            Object Sequence = jtable.getValueAt(selectedRow, 23);
            Object rsv1 = jtable.getValueAt(selectedRow, 24);
            Object rsv2 = jtable.getValueAt(selectedRow, 25);
            Object urg = jtable.getValueAt(selectedRow, 26);
            Object psh = jtable.getValueAt(selectedRow, 28);
            Object syn = jtable.getValueAt(selectedRow, 30);
            Object fin = jtable.getValueAt(selectedRow, 31);
            Object rsv_tos = jtable.getValueAt(selectedRow, 32);
            Object ident = jtable.getValueAt(selectedRow, 33);
            sb.append("\n序号:").append(Sequence)
                    .append("\n确认序号:").append(ack_num)
                    .append("\n片偏移:").append(Offset)
                    .append("保留标志1: ").append(rsv1)
                    .append("保留标志2: ").append(rsv2)
                    .append("\nURG标志: ").append(urg)
                    .append("\nACK标志: ").append(ack)
                    .append("\nPSH标志: ").append(psh)
                    .append("\nRST标志: ").append(psh)
                    .append("\nSYN标志: ").append(syn)
                    .append("\nFIN标志: ").append(fin)
                    .append("\n窗口大小: ").append(window)
                    .append("\n紧急指针: ").append(urgent_pointer)
                    .append("\nIPV").append(IPv)
                    .append("\n服务类型: ").append(rsv_tos)
                    .append("\n数据包长度: ").append(data_len)
                    .append("\n分组标识").append(ident)
                    .append("\n生存时间: ").append(TTL)
                    .append("\nDF标志位: ").append(DF)
                    .append("\nMF标志位: ").append(MF)
                    .append("\nRF标志位: ").append(RF);

            //二进制编码
            Object bianry = jtable.getValueAt(selectedRow, 34);
            String binayArray = (String) bianry;
            sb.append("\n").append(binayArray);
        }
        return sb.toString();
    }

    private static void InitGlobalFont() {
        FontUIResource fontRes = new FontUIResource(GLOBAL_FONT);
        for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontRes);
            }
        }
    }
}
