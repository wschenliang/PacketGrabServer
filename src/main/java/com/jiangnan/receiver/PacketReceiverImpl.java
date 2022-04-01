package com.jiangnan.receiver;

import com.jiangnan.enums.Protocol;
import com.jiangnan.model.PacketData;
import com.jiangnan.model.PacketQueue;
import com.jiangnan.utils.PacketUtil;
import com.jiangnan.utils.SwingUtil;
import jpcap.packet.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;


/**
 * 接收到packet后的处理动作，将记录插进model
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class PacketReceiverImpl implements jpcap.PacketReceiver {

    private final DefaultTableModel model;
    private final JTable jTable;

    public PacketReceiverImpl(JTable jTable, DefaultTableModel model) {
        this.jTable = jTable;
        this.model = model;
    }

    @Override
    public void receivePacket(Packet p) {
        if (p == null || p.data == null) {
            return;
        }
        PacketQueue.addPacket(p);//每接收一个数据包都用于存放
        PacketData packetData = PacketUtil.convertPacket2Data(p);
        model.addRow(packetData.getDataArrays());
    }
}
