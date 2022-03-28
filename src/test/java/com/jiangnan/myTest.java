package com.jiangnan;


import com.jiangnan.utils.JpcapUtil;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.packet.Packet;

public class myTest {

    public static void main(String[] args) {

        JpcapCaptor captor = JpcapUtil.getDefaultCaptor();
        Packet packet = captor.getPacket();
        System.out.println(packet);


    }

}
