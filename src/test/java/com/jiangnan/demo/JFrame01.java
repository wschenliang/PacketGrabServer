package com.jiangnan.demo;

import com.jiangnan.jpcap.JpcapCaptor;
import com.jiangnan.jpcap.NetworkInterface;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;

public class JFrame01 {

    public static void main(String[] args) {
        JFrame frame = new JFrame("抓包工具");
        InitGlobalFont(new Font("微软雅黑", Font.PLAIN, 20));
        frame.setSize(1300,900);
        frame.setLayout(new BorderLayout());
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                System.exit(0);
            }
        });
        Panel panel = new Panel(new BorderLayout());
        Panel topPanel = new Panel(); //顶部开始，停止按钮布局
        topPanel.setSize(90,400);
        topPanel.setBackground(Color.darkGray);
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        Button start = new Button("start");
        start.setEnabled(false);
        Button end = new Button("end");
        NetworkInterface[] deviceList = JpcapCaptor.getDeviceList();
        String[] deviceName = new String[deviceList.length+1];
        deviceName[0] = "";
        for (int i = 0; i < deviceList.length; i++) {
            deviceName[i+1] = deviceList[i].name;
        }
        final JComboBox jComboBox = new JComboBox(deviceName);
        start.setBounds(0,0,40,420);
        end.setBounds(0,0,40,420);
        String[] title = {"No.","Time","Source","Destination","Protocol","Length","帧类型","硬件信息","源物理地址","目标物理地址","源地址端口号","目的地址端口号","IPv","片偏移","数据包长度",
                "生存时间","DF标志位","MF标志位","RF标志位","ack","ack_num","紧急指针","窗口大小","UDP包长度","序号","保留标志1","保留标志2","urg","ack","psh","rst","syn","fin","服务类型","分组标识","数据包二进制"};
        String[][] testData = {

        };
        DefaultTableModel model = new DefaultTableModel(testData,title){
            public boolean isCellEditable(int row,int column){
                return false;
            }
        };
        final JTable jtable = new JTable(model);
        jtable.setRowHeight(25);
        jtable.setSize(900,200);
        jtable.setPreferredScrollableViewportSize(new Dimension(900,200));
        TableColumnModel columnModel = jtable.getColumnModel();
        for (int i = 6; i <=35 ; i++) {
            Invisible(columnModel.getColumn(i));
        }
        final TestMethod testMethod = new TestMethod();
        start.setEnabled(true);
        start.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testMethod.start(jtable,jComboBox);
            }
        });
        end.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TestMethod.shutDown();
            }
        });
        topPanel.add(start);
        topPanel.add(end);
        topPanel.add(jComboBox);
        JScrollPane mid = new JScrollPane(jtable);
        mid.setBackground(Color.BLACK);
        final JTextArea area = new JTextArea(10,50);
        JScrollPane buttom = new JScrollPane(area);
        buttom.setPreferredSize(new Dimension(500,400));
        JScrollBar verticalScrollBar = buttom.getVerticalScrollBar();
        verticalScrollBar.setValue(0);
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(mid,BorderLayout.CENTER);
        panel.add(buttom,BorderLayout.SOUTH);
        frame.setContentPane(panel);
        panel.setVisible(true);
        frame.setVisible(true);
        jtable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jtable.addMouseListener(new MouseAdapter() { //添加选中行监听
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = jtable.getSelectedRow();
                Object frame_type = jtable.getValueAt(selectedRow,6);
                Object hardware_name = jtable.getValueAt(selectedRow,7);
                Object SrchardAddress = jtable.getValueAt(selectedRow,8);
                Object DsthardAddress = jtable.getValueAt(selectedRow,9);
                Object SrcDuan = jtable.getValueAt(selectedRow,10);
                Object DstDuan = jtable.getValueAt(selectedRow,11);
                Object IPv = jtable.getValueAt(selectedRow,12);
                Object Offset = jtable.getValueAt(selectedRow,13);
                Object data_len = jtable.getValueAt(selectedRow,14);
                Object TTL = jtable.getValueAt(selectedRow,15);
                Object DF = jtable.getValueAt(selectedRow,16);
                Object MF = jtable.getValueAt(selectedRow,17);
                Object RF = jtable.getValueAt(selectedRow,18);
                Object ack = jtable.getValueAt(selectedRow,19);
                Object ack_num = jtable.getValueAt(selectedRow,20);
                Object urgent_pointer = jtable.getValueAt(selectedRow,21);
                Object window = jtable.getValueAt(selectedRow,22);
                Object UDP_LEN = jtable.getValueAt(selectedRow,23);
                Object Sequence = jtable.getValueAt(selectedRow,24);
                Object rsv1 = jtable.getValueAt(selectedRow,25);
                Object rsv2 = jtable.getValueAt(selectedRow,26);
                Object urg = jtable.getValueAt(selectedRow,27);
                Object ack2 = jtable.getValueAt(selectedRow,28);
                Object psh= jtable.getValueAt(selectedRow,29);
                Object rst = jtable.getValueAt(selectedRow,30);
                Object syn = jtable.getValueAt(selectedRow,31);
                Object fin = jtable.getValueAt(selectedRow,32);
                Object rsv_tos = jtable.getValueAt(selectedRow,33);
                Object ident = jtable.getValueAt(selectedRow,34);
                Object bianry = jtable.getValueAt(selectedRow,35);
                StringBuilder sb = new StringBuilder();
                if (ack.equals("")){
                     sb = new StringBuilder("          帧类型:" + frame_type + "\n          硬件信息:  " + hardware_name + "\n" +
                             "          源物理地址:  " + SrchardAddress + "\n          目标物理地址: " + DsthardAddress + "\n------------------------------------------------------------------------\n    " +
                             "      源地址端口号: " + SrcDuan + "\n          目的地址端口号:" + DstDuan + "\n          UDP包长度: " + UDP_LEN+"\n");
                }else {
                     sb = new StringBuilder("          帧类型:  " + frame_type + "\n          硬件信息:  " + hardware_name + "\n" +
                             "          源物理地址:  " + SrchardAddress + "\n          目标物理地址: " + DsthardAddress + "\n--------------------------------------------------------------------------------\n      " +
                             "    源地址端口号: " + SrcDuan + "\n           目的地址端口号:" + DstDuan + "\n          序号: " + Sequence + "\n          确认序号: " + ack_num
                             + " \n          片偏移: "
                             + Offset + "          保留标志1: " + rsv1 + "          保留标志2: " + rsv2 + "\n          URG标志: " + urg + "\n     " +
                             "     ACK标志: " + ack + "\n          PSH标志: " + psh + "\n          RST标志: " + psh + "\n          SYN标志: " + syn + "\n          FIN标志: " + fin +
                             "\n          窗口大小: " + window + "\n          紧急指针: " + urgent_pointer + "\n---------------------------------------------------------------------------------\n" + "\n    " +
                             "      IPV" + IPv + "\n    " +
                             "      服务类型: " + rsv_tos + "\n          数据包长度: " + data_len + "\n          分组标识" + ident + "\n          生存时间: " +
                             TTL + "\n          DF标志位: " + DF + "\n          MF标志位: " + MF + "\n          RF标志位: " + RF+"\n");


                }
                String binayArray = (String) bianry;
                String[] split = binayArray.split(",");
                for (int i = 0; i < split.length; i++) {
                    if (!split[i].equals(" null")&&!split[i].equals(" null]]")&&!split[i].equals("[[null")) sb.append(split[i]);

                    if (i%7==6) {
                        sb.append("\n");
                    }
                }
                String string = sb.toString();
                string.replaceAll("\\[", "");
                area.setText(string);

            }
        });

    }
    private static void InitGlobalFont(Font font) {
        FontUIResource fontRes = new FontUIResource(font);
        for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontRes);
            }
        }
    }
    public static void Invisible(TableColumn column){
        column.setMaxWidth(0);
        column.setMinWidth(0);
        column.setPreferredWidth(0);
    }
}
