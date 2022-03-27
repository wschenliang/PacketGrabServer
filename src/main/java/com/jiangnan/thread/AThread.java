package com.jiangnan.thread;

import jpcap.JpcapCaptor;
import jpcap.packet.Packet;

public class AThread implements Runnable{

    //定义默认最大抓包数
    private static final int max = 4096;

    Thread thread;
    JpcapCaptor captor;
    Packet[] packet;
    //线程中断标志
    volatile boolean cancel;

    public AThread(JpcapCaptor captor) {
        this.captor = captor;
        this.packet = new Packet[max];
        this.cancel = false;
        thread = new Thread(this);
    }

    @Override
    public void run() {
        packet = new Packet[max];
        for(int i = 0; i < max && !cancel; i++){
            packet[i] = captor.getPacket();
        }
    }

    public void cancel(){
        cancel = true;
    }

    public Packet[] getPacket(){
        return packet;
    }
}
