package com.jiangnan;

import com.jiangnan.constants.IconUtils;
import com.jiangnan.filter.PcapFileFilter;
import com.jiangnan.model.PacketData;
import com.jiangnan.model.PacketQueue;
import com.jiangnan.receiver.PacketReceiverImpl;
import com.jiangnan.utils.JpcapUtil;
import com.jiangnan.utils.LogUtils;
import com.jiangnan.utils.PacketUtil;
import com.jiangnan.utils.StringUtils;
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
    private static String selectIp;
    private JpcapCaptor captor = null;
    private final IconUtils iconUtils = new IconUtils();
    private final JFrame jFrame;
    private final JPanel topPanel;
    private final JScrollPane midPanel, bottomPanel, rightPanel;
    private final JButton startButton, stopButton, openButton, saveButton, restartButton, optionButton, clearButton;
    private final JLabel infoLabel;
    private final JTable jTable;
    private final JTextArea infoArea, logArea;


    public HomeJPanel(JFrame jFrame) {
        this.jFrame = jFrame;
        this.setLayout(new BorderLayout(2,2));//设置面板 borderLayout分为东南西北中

        //----------------------------------顶部按钮和标签------------------------------------------------------------
        startButton = getStartButton();//开始按钮
        stopButton = getStopButton();//停止按钮
        openButton = getOpenButton();//读取按钮
        saveButton = getSaveButton();//保存按钮
        restartButton = getRestartButton();//重启按钮
        optionButton = getOptionButton();//设置按钮
        clearButton = getClearButton();//清除按钮
        infoLabel = getInfoLabel();//信息标签

        //-----------------------------------中间表格-----------------------------------------------------------
        jTable = getJTable();//表格

        //----------------------------------------------------------------------------------------------
        infoArea = getInfoArea();//底部输出文本域

        //----------------------------------------------------------------------------------------------
        logArea = getLogArea();//侧面输出文本域

        //--------------------------------------点击事件--------------------------------------------------------
        updateInfoLabel();//更新标签信息
        startButtonAction();//开始按钮点击事件
        stopButtonAction();//停止按钮点击事件
        openButtonAction();//读取按钮添加事件
        saveButtonAction();//保存按钮添加事件
        optionButtonAction();//设置按钮添加事件
        restartButtonAction();//重启按钮点击事件
        clearButtonAction();//清除按钮点击事件
        jTableAction();//表格点击事件
        infoLabelAction();//label标签点击事件

        //----------------------------------------------------------------------------------------------
        topPanel = getTopPanel(); //顶部区域
        midPanel = getMidPanel();//中间区域
        bottomPanel = getBottomPanel();//底部区域
        rightPanel = getRightPanel();//侧面区域

        this.add(topPanel, BorderLayout.NORTH);
        this.add(midPanel,BorderLayout.CENTER);
        this.add(bottomPanel,BorderLayout.SOUTH);
        this.add(rightPanel, BorderLayout.EAST);

        this.setVisible(true);
    }

    private void infoLabelAction() {
        infoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String[] deviceIp = JpcapUtil.getDeviceIp();
                String defaultSelect = deviceIp[0];
                if (StringUtils.isNotBlank(selectIp)) {
                    defaultSelect = selectIp;
                }
                selectIp = (String) JOptionPane.showInputDialog(jFrame,  "设备ip", "请选择网卡设备",1, null, deviceIp, defaultSelect);
                updateInfoLabel();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                infoLabel.setBackground(new Color(186,186,186));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                updateInfoLabel();
            }
        });
    }

    private void log(String text){
        logArea.append("\n");
        logArea.append(text);
    }

    private JScrollPane getRightPanel() {
        JScrollPane jScrollPane = new JScrollPane(logArea);
        jScrollPane.setPreferredSize(new Dimension(200, 200));
        return jScrollPane;
    }

    private JScrollPane getMidPanel() {
        return new JScrollPane(jTable);
    }

    //bottomPanel设置
    private JScrollPane getBottomPanel() {
        JScrollPane jScrollPane = new JScrollPane(infoArea);
        jScrollPane.setPreferredSize(new Dimension(400,400));
        jScrollPane.getVerticalScrollBar().setValue(0);
        return jScrollPane;
    }

    //topPanel设置
    private JPanel getTopPanel() {
        JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jPanel.setBackground(Color.WHITE);
        jPanel.add(startButton);
        jPanel.add(stopButton);
        jPanel.add(restartButton);
        jPanel.add(openButton);
        jPanel.add(saveButton);
        jPanel.add(clearButton);
        jPanel.add(optionButton);
        jPanel.add(infoLabel);
        return jPanel;
    }

    //更新infoLabel
    private void updateInfoLabel() {
        if (StringUtils.isBlank(selectIp)) {
            infoLabel.setText("未设置网卡");
            infoLabel.setBackground(new Color(255,182,182));
        } else {
            infoLabel.setText(selectIp);
            infoLabel.setBackground(new Color(176,255,206));
        }
    }

    private JLabel getInfoLabel() {
        JLabel jLabel = new JLabel();
        jLabel.setFont(new Font("黑体", Font.PLAIN, 17));
        jLabel.setOpaque(true);//设置不透明，只有这样才能添加背景色
        return jLabel;
    }

    //设置按钮点击事件
    private void optionButtonAction() {
        optionButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] deviceIp = JpcapUtil.getDeviceIp();
                String defaultSelect = deviceIp[0];
                if (StringUtils.isNotBlank(selectIp)) {
                    defaultSelect = selectIp;
                }
                selectIp = (String) JOptionPane.showInputDialog(jFrame,  "设备ip", "请选择网卡设备",1, null, deviceIp, defaultSelect);
                updateInfoLabel();
            }
        });
    }

    //获取侧边文本域
    private JTextArea getLogArea() {
        JTextArea jTextArea = new JTextArea("log:") {
            @Override
            public boolean isEditable() {
                return false;
            }
        };
        jTextArea.setFont(new Font("微软雅黑", Font.PLAIN, 15));
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
        jTextArea.setFont(new Font("黑体", Font.PLAIN, 15));
        jTextArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));//设置鼠标变为文本鼠标
        jTextArea.setBackground(Color.LIGHT_GRAY);
        return jTextArea;
    }

    //表格点击事件
    private void jTableAction() {
        //表格点击事件 底部层显示第七列数据
        jTable.addMouseListener(new MouseAdapter() {//鼠标单机事件
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = jTable.getSelectedRow();
                Object jsonInfo = jTable.getValueAt(selectedRow, 6);
                String text = jsonInfo.toString();
                infoArea.setText("info: \n" + text);
            }
        });
    }

    //保存按钮点击事件
    private void saveButtonAction() {
        saveButton.addActionListener(new AbstractAction() {
            private static final long serialVersionUID = -3192698988668486101L;

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(".");
                fileChooser.addChoosableFileFilter(new PcapFileFilter());
                int val = fileChooser.showSaveDialog(jFrame);
                if (val != JFileChooser.APPROVE_OPTION) {
                    log("保存文件取消");
                    return;
                }
                File file = fileChooser.getSelectedFile();
                //文件写入操作
                String absolutePath = file.getAbsolutePath();
                //获取插入的所有文件
                JpcapWriter writer = null;
                try {
                    writer = JpcapWriter.openDumpFile(captor, absolutePath);
                    LinkedList<Packet> packets = PacketQueue.getPackets();
                    for (Packet packet : packets) {
                        writer.writePacket(packet);
                    }
                } catch (IOException exception) {
                    System.out.println(exception.getCause().toString());
                }finally {
                    log("保存文件成功");
                    if (writer != null) {
                        writer.close();//注意，调用close方法程序会自动关闭
                    }
                }

            }
        });
    }

    //读取按钮点击事件
    private void openButtonAction() {
        openButton.addActionListener(new AbstractAction() {
            private static final long serialVersionUID = -1138468420354500040L;

            @Override
            public void actionPerformed(ActionEvent e) {
                //文件选择器
                JFileChooser fileChooser = new JFileChooser(".");
                fileChooser.addChoosableFileFilter(new PcapFileFilter());
                int val = fileChooser.showOpenDialog(jFrame);
                if (val != JFileChooser.APPROVE_OPTION) {
                    log("打开文件取消");
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
                DefaultTableModel model = (DefaultTableModel) jTable.getModel();
                model.setRowCount(0);
                PacketUtil.init();
                //将packets输出到jTable上
                while (true) {
                    Packet packet = captor.getPacket();
                    if (packet == null || packet.data == null) break;
                    PacketData packetData = PacketUtil.convertPacket2Data(packet);
                    model.addRow(packetData.getDataArrays());
                }
                if (model.getRowCount() > 0) {
                    clearButton.setEnabled(true);//可以清除
                }
                log("读取文件成功");
            }
        });

    }

    //清除按钮点击事件
    private void clearButtonAction() {
        clearButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) jTable.getModel();
                model.setRowCount(0);
                PacketUtil.init();
                saveButton.setEnabled(false);//点击清除按钮，保存按钮就失效了
            }
        });
    }

    //停止按钮点击事件
    private void stopButtonAction() {
        stopButton.addActionListener(new AbstractAction() {
            private static final long serialVersionUID = 4447805863352575674L;
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                openButton.setEnabled(true);//停止可以打开文件
                if (captor == null) {
                    LogUtils.log("captor为null");
                    return;
                }
                captor.breakLoop();
                DefaultTableModel model = (DefaultTableModel) jTable.getModel();
                //判断有没有数据，如果有数据，保存按钮要亮起来
                if (model.getRowCount() > 0) {
                    saveButton.setEnabled(true);
                    restartButton.setEnabled(true);
                    clearButton.setEnabled(true);
                }
                log("停止");
            }
        });

    }

    //重启按钮点击事件
    private void restartButtonAction() {
        restartButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log("重启中...");

                start();
            }
        });
    }

    private void start() {
        log("启动中...");
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        restartButton.setEnabled(false);
        saveButton.setEnabled(false);
        DefaultTableModel model = (DefaultTableModel) jTable.getModel();
        //数据清空启动
        model.setRowCount(0);
        PacketUtil.init();
        if (selectIp == null) {
            //未选择ip
            log("启动失败，未设置网卡设备");
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            return;
        }
        openButton.setEnabled(false);//打开文件按钮失效
        clearButton.setEnabled(false);//清除按钮失效
        NetworkInterface device = JpcapUtil.findDeviceByIP(selectIp);
        if (device == null) {
            return;
        }
        captor = JpcapUtil.getCaptor(device);
        //开启线程2执行循环监听捕获数据，注意线程2是无法使用线程1的本地线程栈内容的，因此，想要保存数据得传递过去
        new Thread(() -> captor.loopPacket(-1, new PacketReceiverImpl(model))).start();
        log("启动成功");
    }

    //开始按钮的点击事件
    private void startButtonAction() {
        startButton.addActionListener(new AbstractAction() {
            private static final long serialVersionUID = -5422233935591297014L;
            @Override
            public void actionPerformed(ActionEvent e) {
                start();
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
        jButton.setIcon(iconUtils.saveIcon());
        jButton.setBackground(Color.WHITE);
        jButton.setToolTipText("保存");
        jButton.setEnabled(false);//停止不可用
        jButton.setBorderPainted(false);//不打印边框
        jButton.setBorder(null);//除去边框
        jButton.setFocusPainted(false);//除去焦点边框
        jButton.setContentAreaFilled(false);//除去默认填充
        jButton.setMargin(new Insets(0,0,0,0));
        return jButton;
    }

    //读取按钮配置
    private JButton getOpenButton() {
        JButton jButton = new JButton();
        jButton.setIcon(iconUtils.openIcon());
        jButton.setBackground(Color.WHITE);
        jButton.setToolTipText("打开");
        jButton.setBorderPainted(false);//不打印边框
        jButton.setBorder(null);//除去边框
        jButton.setFocusPainted(false);//除去焦点边框
        jButton.setContentAreaFilled(false);//除去默认填充
        jButton.setMargin(new Insets(0,0,0,0));
        return jButton;
    }

    //停止按钮配置
    private JButton getStopButton() {
        JButton jButton = new JButton();
        jButton.setIcon(iconUtils.stopIcon());
        jButton.setBackground(Color.WHITE);
        jButton.setToolTipText("停止");
        jButton.setBorderPainted(false);//不打印边框
        jButton.setBorder(null);//除去边框
        jButton.setFocusPainted(false);//除去焦点边框
        jButton.setContentAreaFilled(false);//除去默认填充
        jButton.setMargin(new Insets(0,0,0,0));
        jButton.setEnabled(false);//停止不可用
        return jButton;
    }

    //开始按钮配置
    private JButton getStartButton() {
        JButton jButton = new JButton();
        jButton.setBackground(Color.WHITE);
        jButton.setToolTipText("开始");
        jButton.setBorderPainted(false);//不打印边框
        jButton.setBorder(null);//除去边框
        jButton.setFocusPainted(false);//除去焦点边框
        jButton.setContentAreaFilled(false);//除去默认填充
        jButton.setMargin(new Insets(0,0,0,0));
        jButton.setIcon(iconUtils.startIcon());
        return jButton;
    }

    //重启按钮配置
    private JButton getRestartButton() {
        JButton jButton = new JButton();
        jButton.setBackground(Color.WHITE);
        jButton.setEnabled(false);
        jButton.setToolTipText("重启");
        jButton.setBorderPainted(false);//不打印边框
        jButton.setBorder(null);//除去边框
        jButton.setFocusPainted(false);//除去焦点边框
        jButton.setContentAreaFilled(false);//除去默认填充
        jButton.setMargin(new Insets(0,0,0,0));
        jButton.setIcon(iconUtils.restartIcon());
        return jButton;
    }

    //设置按钮配置
    private JButton getOptionButton() {
        JButton jButton = new JButton();
        jButton.setBackground(Color.WHITE);
        jButton.setToolTipText("设置");
        jButton.setBorderPainted(false);//不打印边框
        jButton.setBorder(null);//除去边框
        jButton.setFocusPainted(false);//除去焦点边框
        jButton.setContentAreaFilled(false);//除去默认填充
        jButton.setMargin(new Insets(0,0,0,0));
        jButton.setIcon(iconUtils.optionsIcon());
        return jButton;
    }

    //清除按钮配置
    private JButton getClearButton() {
        JButton jButton = new JButton();
        jButton.setBackground(Color.WHITE);
        jButton.setToolTipText("清除");
        jButton.setEnabled(false);
        jButton.setBorderPainted(false);//不打印边框
        jButton.setBorder(null);//除去边框
        jButton.setFocusPainted(false);//除去焦点边框
        jButton.setContentAreaFilled(false);//除去默认填充
        jButton.setMargin(new Insets(0,0,0,0));
        jButton.setIcon(iconUtils.closeIcon());
        return jButton;
    }

    public JpcapCaptor getCaptor() {
        return captor;
    }

    public JTable getjTable() {
        return jTable;
    }
}
