package com.jiangnan.utils;

import com.jiangnan.jpcap.JpcapCaptor;
import com.jiangnan.jpcap.JpcapWriter;
import com.jiangnan.jpcap.packet.*;

import java.io.IOException;
import java.net.InetAddress;
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


    public static IPPacket generateIpPacket(String src, String dst) throws java.io.IOException{

        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入要发送的数据: ");
        String data = scanner.next();

        //构造ether帧（frame）
        EthernetPacket ether = new EthernetPacket();
        //设置帧类型为IP
        ether.frametype = EthernetPacket.ETHERTYPE_IP;
        //设置源、目的MAC地址
        ether.src_mac = "30:52:cb:f0:6f:f6".getBytes();
        ether.dst_mac = "00:0c:29:3c:0a:f1".getBytes();

        //构造IP报文
        IPPacket ipPacket = new IPPacket();
        ipPacket.setIPv4Parameter(0,false,false,false,0,false,false,
                false,0,0,128,230, InetAddress.getByName(src),
                InetAddress.getByName(dst));
        ipPacket.data = (data).getBytes();
        ipPacket.datalink = ether;

        return ipPacket;
    }

    public static TCPPacket generateTcpPacket(String src, String dst) throws java.io.IOException{
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入要发送的数据: ");
        String data = scanner.next();

        //构造ether帧（frame）
        EthernetPacket ether = new EthernetPacket();
        //设置帧类型为IP
        ether.frametype = EthernetPacket.ETHERTYPE_IP;
        //设置源、目的MAC地址
        ether.src_mac = "30:52:cb:f0:6f:f6".getBytes();
        ether.dst_mac = "00:0c:29:3c:0a:f1".getBytes();

        //构造TCP报文
        TCPPacket tcpPacket = new TCPPacket(12, 34, 56, 78, false, false,
                false, false, true, true, true, true, 10, 0);
        //设置IP头
        tcpPacket.setIPv4Parameter(0,false,false,false,0,false,false,
                false,0,65,128,IPPacket.IPPROTO_TCP,InetAddress.getByName(src),
                InetAddress.getByName(dst));
        //设置报文数据
        tcpPacket.data = (data).getBytes();

        //设置数据链路层
        tcpPacket.datalink = ether;

        return tcpPacket;
    }

    public static ARPPacket generateArpPacket(String src, String dst) throws java.io.IOException{

        //构造ether帧（frame）
        EthernetPacket ether = new EthernetPacket();
        //设置帧类型为IP
        ether.frametype = EthernetPacket.ETHERTYPE_ARP;
        //设置源、目的MAC地址
        ether.src_mac = "30:52:cb:f0:6f:f6".getBytes();
        ether.dst_mac = new byte[]{(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255};

        //构造ARP报文
        ARPPacket arpPacket = new ARPPacket();
        arpPacket.hardtype = ARPPacket.HARDTYPE_ETHER;//硬件类型
        arpPacket.prototype = ARPPacket.PROTOTYPE_IP;//协议类型
        arpPacket.operation = ARPPacket.ARP_REQUEST;//指明为ARP请求报文(另一种为回复报文)
        arpPacket.hlen = 6;//物理地址长度
        arpPacket.plen = 4;//协议地址长度
        arpPacket.sender_hardaddr = ether.src_mac;//发送端为本机mac地址
        arpPacket.sender_protoaddr = InetAddress.getByName(src).getAddress();//本机IP地址
        arpPacket.target_hardaddr = ether.dst_mac; //目的端mac地址为广播地址
        arpPacket.target_protoaddr = InetAddress.getByName(dst).getAddress();//目的IP地址
        arpPacket.datalink = ether;//设置arp报文数据链路层

        return arpPacket;
    }

    public static UDPPacket generateUdpPacket(String src, String dst) throws java.io.IOException{

        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入要发送的数据: ");
        String data = scanner.next();

        //构造以太帧（frame）
        EthernetPacket ether = new EthernetPacket();
        //设置帧类型为IP
        ether.frametype = EthernetPacket.ETHERTYPE_IP;
        //设置源、目的MAC地址
        ether.src_mac = "30:52:cb:f0:6f:f6".getBytes();
        ether.dst_mac = "00:0c:29:3c:0a:f1".getBytes();

        //构造UDP报文
        UDPPacket udpPacket = new UDPPacket(12, 34);
        udpPacket.src_ip = InetAddress.getByName(src);
        udpPacket.dst_ip = InetAddress.getByName(dst);
        udpPacket.data = data.getBytes();

        //设置IP头
        udpPacket.setIPv4Parameter(0,false,false,false,0,false,false,
                false,0,65,128,IPPacket.IPPROTO_UDP,InetAddress.getByName(src),
                InetAddress.getByName(dst));
        udpPacket.datalink = ether;

        return udpPacket;
    }

    public static ICMPPacket generateIcmpPacket(String src, String dst) throws java.io.IOException{

        //构造以太帧（frame）
        EthernetPacket ether = new EthernetPacket();
        //设置帧类型为IP
        ether.frametype = EthernetPacket.ETHERTYPE_IP;
        //设置源、目的MAC地址
        ether.src_mac = "30:52:cb:f0:6f:f6".getBytes();
        ether.dst_mac = new byte[]{(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255};

        //生成ICMP报文
        ICMPPacket icmpPacket = new ICMPPacket();
        icmpPacket.type = ICMPPacket.ICMP_ECHO;//发送回显请求报文
        icmpPacket.data = "test".getBytes();

        //设置IPV4头
        icmpPacket.setIPv4Parameter(0,false,false,false,0,false,false,
                false,0,65,128,IPPacket.IPPROTO_ICMP,InetAddress.getByName(src),
                InetAddress.getByName(dst));

        //设置以太帧头部
        icmpPacket.datalink = ether;

        return icmpPacket;

    }

}
