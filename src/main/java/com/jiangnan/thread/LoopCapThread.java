package com.jiangnan.thread;

import com.jiangnan.receiver.PacketReceiverImpl;
import com.jiangnan.utils.JpcapUtil;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;

import javax.swing.*;

/**
 *
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class LoopCapThread implements Runnable{
    private final JpcapCaptor captor;
    private final NetworkInterface device;
    private final JTable jtable;

    public LoopCapThread(JpcapCaptor captor, NetworkInterface device, JTable jtable) {
        this.captor = captor;
        this.device = device;
        this.jtable = jtable;
    }

    @Override
    public void run() {
        captor.loopPacket(-1, new PacketReceiverImpl(jtable, device));
    }
}
