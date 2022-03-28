package com.jiangnan.utils;

import jpcap.JpcapCaptor;
import jpcap.JpcapWriter;
import jpcap.packet.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * 数据包常用工具
 *
 * @author chenliang
 * @version 1.0
 * @email wschenliang@aliyun.com
 */
public class PacketUtil {
    //定义默认最大抓包数
    private static final int max = 4096;

    public static Packet[] getNewPacket(){
        return new Packet[max];
    }

    public static void showPacket(Packet[] packet){
        for(int i = 0; packet[i] != null && i < max; i++){
            System.out.println("Packet " + (i+1) + " : " + packet[i]);
        }
    }

    public static Packet[] readPacket(JpcapCaptor captor, String filename){
        Packet[] packet = new Packet[max];
        try {
            captor = JpcapCaptor.openFile(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i = 0;;i++){
            packet[i] = captor.getPacket();
            if(packet[i] == null)
                break;
        }
        return packet;
    }

    public static void savePacket(JpcapCaptor capter, Packet[] packet) {
        JpcapWriter writer = null;
        try {
            writer = JpcapWriter.openDumpFile(capter, "./savePacket");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i = 0 ; packet[i] != null; i++){
            writer.writePacket(packet[i]);
        }

        writer.close();
    }

    public static void analyzePacket(Packet[] packet){

        ArrayList<UDPPacket> udpPacketArray = new ArrayList<UDPPacket>();
        ArrayList<ICMPPacket> icmpPacketArray = new ArrayList<ICMPPacket>();
        ArrayList<ARPPacket> arpPacketArray = new ArrayList<ARPPacket>();
        ArrayList<TCPPacket> tcpPacketArray = new ArrayList<TCPPacket>();
        ArrayList<Packet> unknownPacketArray = new ArrayList<Packet>();

        int count, count1, count2, count3, count4, count5;
        count = count1 = count2 = count3 = count4 = count5 = 0;

        for(int i = 0; packet[i] != null && i < max; i++) {
            count++;

            if (packet[i] instanceof UDPPacket) {
                UDPPacket udp = (UDPPacket) packet[i];
                udpPacketArray.add(udp);
                count1++;
            }else if(packet[i] instanceof ICMPPacket){
                ICMPPacket icmp = (ICMPPacket) packet[i];
                icmpPacketArray.add(icmp);
                count2++;
            }else if(packet[i] instanceof ARPPacket){
                ARPPacket arp = (ARPPacket) packet[i];
                arpPacketArray.add(arp);
                count3++;
            }else if(packet[i] instanceof TCPPacket){
                TCPPacket tcp = (TCPPacket) packet[i];
                tcpPacketArray.add(tcp);
                count4++;
            }else{
                unknownPacketArray.add(packet[i]);
                count5++;
            }
        }

        System.out.println();
        System.out.println("所有数据包数：" + count);
        System.out.println("UDP数据包数：" + count1);
        System.out.println("ICMP数据包数：" + count2);
        System.out.println("ARP数据包数：" + count3);
        System.out.println("TCP数据包数：" + count4);
        System.out.println("其他数据包数：" + count5);

    }

    public static void showPacketDetail(Packet[] packet){
        for(int i = 0; packet[i] != null && i < max; i++) {
            if(packet[i] instanceof UDPPacket){
                UDPPacket udp = (UDPPacket) packet[i];
                String data = new String(udp.data);
                System.out.println("Packet " + (i+1) + " : UDP" );
                System.out.println("    source ip : " + udp.src_ip.toString());
                System.out.println("    destination ip : " + udp.dst_ip.toString());
                System.out.println("    source port : " + String.valueOf(udp.src_port));
                System.out.println("    destination port : " + String.valueOf(udp.dst_port));
                System.out.println("    offset : " + String.valueOf(udp.offset));
                System.out.println("    data : " + data);
            }else if(packet[i] instanceof TCPPacket){
                TCPPacket tcp = (TCPPacket) packet[i];
                String data = new String(tcp.data);
                System.out.println("Packet " + (i+1) + " : TCP" );
                System.out.println("    source ip : " + tcp.src_ip.toString());
                System.out.println("    destination ip : " + tcp.dst_ip.toString());
                System.out.println("    source port : " + String.valueOf(tcp.src_port));
                System.out.println("    destination port : " + String.valueOf(tcp.dst_port));
                System.out.println("    offset : " + String.valueOf(tcp.offset));
                System.out.println("    data : " + data );
            }else if(packet[i] instanceof ARPPacket){
                ARPPacket arp = (ARPPacket) packet[i];
                byte[] b = new byte[4];
                String s1 = "";
                String s2 = "";

                b = arp.target_protoaddr;
                s1 += String.valueOf((b[0] & 0xff) + "." + ( b[1] & 0xff) + "." +
                        (b[2] & 0xff) + "." + (b[3] & 0xff));
                b = arp.sender_protoaddr;
                s2 += String.valueOf((b[0] & 0xff) + "." + ( b[1] & 0xff) + "." +
                        (b[2] & 0xff) + "." + (b[3] & 0xff));

                System.out.println("Packet " + (i+1) + " : ARP" );
                System.out.println("    sender address: " + s2);
                System.out.println("    target address: " + s1);
            }else if(packet[i] instanceof ICMPPacket){
                ICMPPacket icmp = (ICMPPacket) packet[i];

                System.out.println("Packet " + (i+1) + " : ICMP");
                System.out.println("    ICMP packet.");
            }else{
                System.out.println("Packet " + (i+1) + " : " );
                System.out.println("    no information.");
            }

        }
    }

}
