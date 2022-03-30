package com.jiangnan;


import com.jiangnan.utils.JpcapUtil;
import jpcap.JpcapCaptor;
import jpcap.packet.Packet;

public class myTest {

    public static void main(String[] args) {
        double d = 6666666 * 0.000001;
        String format = String.format("%.6f", d);
        System.out.println(d);
        System.out.println(format);


    }

}
