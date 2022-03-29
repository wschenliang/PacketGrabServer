package com.jiangnan.receiver;

import com.jiangnan.model.PacketData;
import com.jiangnan.utils.ContextUtil;
import jpcap.packet.*;

import javax.swing.table.DefaultTableModel;


/**
 * 接收到packet后的处理动作，将记录插进model
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class PacketReceiverImpl implements jpcap.PacketReceiver {
    private static int NUM = 1; // 行编号
    private final DefaultTableModel model;

    public PacketReceiverImpl(DefaultTableModel model) {
        this.model = model;
    }

    @Override
    public void receivePacket(Packet p) {
        long startTime = ContextUtil.getStartTime();
        long time = System.currentTimeMillis() - startTime;//总共耗时
        PacketData packetData = new PacketData(p);
        packetData.setTime(time);
        packetData.setNum(NUM++);
        model.addRow(packetData.getDataArrays());
        ContextUtil.addPacketData(packetData);
    }
}
