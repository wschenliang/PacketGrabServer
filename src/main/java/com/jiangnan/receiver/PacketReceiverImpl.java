package com.jiangnan.receiver;

import com.jiangnan.model.PacketData;
import com.jiangnan.model.PacketQueue;
import com.jiangnan.utils.PacketUtil;
import jpcap.packet.*;

import javax.swing.table.DefaultTableModel;


/**
 * 接收到packet后的处理动作，将记录插进model
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class PacketReceiverImpl implements jpcap.PacketReceiver {

    private final DefaultTableModel model;
    private final PacketQueue queue;

    public PacketReceiverImpl(DefaultTableModel model, PacketQueue queue) {
        this.model = model;
        this.queue = queue;
    }

    @Override
    public void receivePacket(Packet p) {
        //每接收一个数据包都用于存放线程栈，便于保存
        queue.addPacket(p);
        PacketData packetData = PacketUtil.convertPacket2Data(p);
        model.addRow(packetData.getDataArrays());
    }
}
