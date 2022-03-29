package com.jiangnan.thread;

import com.jiangnan.receiver.PacketReceiverImpl;
import jpcap.JpcapCaptor;

import javax.swing.*;

/**
 *
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class LoopCapThread implements Runnable{
    private final JpcapCaptor captor;
    private final JTable jtable;

    public LoopCapThread(JpcapCaptor captor, JTable jtable) {
        this.captor = captor;
        this.jtable = jtable;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        captor.loopPacket(-1, new PacketReceiverImpl(jtable, startTime));
    }
}
