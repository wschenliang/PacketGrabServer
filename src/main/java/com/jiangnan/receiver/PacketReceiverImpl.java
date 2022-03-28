package com.jiangnan.receiver;

import jpcap.NetworkInterface;
import jpcap.packet.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Arrays;


/**
 *
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class PacketReceiverImpl implements jpcap.PacketReceiver {
    private static int number = 1; // 行编号
    private static final long START_TIME = System.currentTimeMillis();
    private DefaultTableModel jTable;
    private NetworkInterface device;

    public PacketReceiverImpl(JTable jTable, NetworkInterface device) {
        this.jTable = (DefaultTableModel) jTable.getModel();
        this.device = device;
    }

    @Override
    public void receivePacket(Packet p) {
        if (p instanceof TCPPacket) {
            tcpPacketHandle((TCPPacket) p);
        } else if (p instanceof UDPPacket) {
            udpPacketHandle((UDPPacket) p);
        } else if (p instanceof ARPPacket) {
            arpPacketHandle((ARPPacket) p);
        }
    }

    private void arpPacketHandle(ARPPacket p) {
        //Returns the hardware address (MAC address) of the sender
        Object  saa=   p.getSenderHardwareAddress();
        Object  taa=p.getTargetHardwareAddress();
        String src = ((String)p.getSenderHardwareAddress()).substring(1);
        String dst = ((String)p.getTargetProtocolAddress()).substring(1);
        jTable.addRow(new String[]{number+++"",(System.currentTimeMillis()-START_TIME)+"",src,dst,"ARP",p.len+""
                ,device.name,saa+"",taa+"","","","","","","","","","","","","","","","","","","","","","","","","",""
        });
    }

    private void udpPacketHandle(UDPPacket p) {
        String src = p.src_ip.toString();
        String dst = p.dst_ip.toString();
        byte[] data = p.data;
        EthernetPacket ethernetPacket = (EthernetPacket) p.datalink;
        String[][] dataBianary = new String[data.length/16+1][16];
        for (int i = 0; i < data.length; i++) {
            StringBuilder string = new StringBuilder(Integer.toBinaryString(Math.abs(data[i])));
            int len = 8- string.length();
            for (int j = 0; j < len; j++) {
                string.insert(0,'0');
            }
            if (data[i]<0){
                string.delete(0,1);
                string.insert(0,'1');
            }
            dataBianary[i/16][i%16] = string.toString();

        }
        jTable.addRow(new String[]{number++ + "",(System.currentTimeMillis()-START_TIME)+"",src,dst,"UDP",p.len+"",
                ethernetPacket.frametype + "",device.name,ethernetPacket.getDestinationAddress(),ethernetPacket.getSourceAddress(),p.src_port+"",p.dst_port+"",p.version+"",p.offset+"",
                p.length+"",p.hop_limit+"",p.dont_frag+"",p.more_frag+"",p.rsv_frag+"","","","","",p.length+"","","","","","","","","","","", "",Arrays.deepToString(dataBianary),
        });
    }

    private void tcpPacketHandle(TCPPacket p) {
        String src = p.src_ip.toString();
        String dst = p.dst_ip.toString();
        byte[] data = p.data;
        EthernetPacket ethernetPacket = (EthernetPacket) p.datalink;
        String[][] dataBianary = new String[data.length/16 + 1][16];
        for (int i = 0; i < data.length; i++) {
            StringBuilder string = new StringBuilder(Integer.toBinaryString(Math.abs(data[i])));
            int len = 8- string.length();
            for (int j = 0; j < len; j++) {
                string.insert(0,'0');
            }
            if (data[i]<0){
                string.delete(0,1);
                string.insert(0,'1');
            }
            dataBianary[i/16][i%16] = string.toString();

        }
        jTable.addRow(new String[]{number+++"",(System.currentTimeMillis()-START_TIME)+"",src,dst,"TCP",p.len+"", ethernetPacket.frametype + "",device.name,
                ethernetPacket.getDestinationAddress(),ethernetPacket.getSourceAddress(),p.src_port+"",p.dst_port+"",p.version+"",p.offset+"",p.length+"",p.hop_limit+"",p.dont_frag+""
                ,p.more_frag+"",p.rsv_frag+"",p.ack+"",p.ack_num+"",p.urgent_pointer+"",p.window+"","",p.sequence+"",p.rsv1+"",p.rsv2+"",
                p.urg+"",p.ack+"",p.psh+"",p.rst+"",p.syn+"",p.fin+"",p.rsv_tos+"",p.ident+"", Arrays.deepToString(dataBianary)
        });
    }





}
