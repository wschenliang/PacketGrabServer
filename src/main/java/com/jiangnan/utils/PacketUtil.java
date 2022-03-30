package com.jiangnan.utils;

import com.jiangnan.enums.Protocol;
import com.jiangnan.model.PacketData;
import jpcap.JpcapCaptor;
import jpcap.JpcapWriter;
import jpcap.packet.*;

import java.io.IOException;
import java.util.ArrayList;

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


    private static int NUM = 1; // 行编号
    private static long firstPacketUSec = 0;
    private static long firstPacketSec = 0;

    public static PacketData convertPacket2Data(Packet p) {
        PacketData packetData = new PacketData();
        String jsonInfo = FastjsonUtils.toJSONString(p);
        packetData.setJsonInfo(jsonInfo);
        if (p instanceof TCPPacket) {
            tcpPacketHandle((TCPPacket) p, packetData);
        } else if (p instanceof UDPPacket) {
            udpPacketHandle((UDPPacket) p, packetData);
        } else if (p instanceof ARPPacket) {
            arpPacketHandle((ARPPacket) p, packetData);
        }
        packetData.setNum(NUM++);
        //时间处理逻辑
        if (firstPacketUSec == 0 && firstPacketSec == 0) {
            firstPacketUSec = p.usec;
            firstPacketSec = p.sec;
            packetData.setSec("0.000000");
        } else {
            long gap = (p.sec - firstPacketSec) * 1000000 + (p.usec - firstPacketUSec);
            String format = String.format("%.6f", gap * 0.000001);
            packetData.setSec(format);
        }
        return packetData;
    }

    private static void tcpPacketHandle(TCPPacket p, PacketData packetData) {
        packetData.setSrc(p.src_ip.toString())
                .setDest(p.dst_ip.toString())
                .setProtocol(Protocol.TCP)
                .setLength(p.len);

    }
    private static void arpPacketHandle(ARPPacket p, PacketData packetData) {
        packetData.setProtocol(Protocol.ARP);
        Object senderProtocolAddress = p.getSenderProtocolAddress();
        Object targetProtocolAddress = p.getTargetProtocolAddress();
        packetData.setSrc(senderProtocolAddress.toString());
        packetData.setDest(targetProtocolAddress.toString());
    }

    private static void udpPacketHandle(UDPPacket p, PacketData packetData) {
        packetData.setSrc(p.src_ip.toString())
                .setDest(p.dst_ip.toString())
                .setProtocol(Protocol.UDP)
                .setLength(p.len);
    }

    //初始化，将数据还原，这两个数据，一个为了记录自增序号，一个为了记录耗时
    public static void init() {
        NUM = 1;
        firstPacketSec = 0;
        firstPacketUSec = 0;
    }
}
