package com.jiangnan.test;

import com.jiangnan.jpcap.JpcapCaptor;
import com.jiangnan.jpcap.JpcapSender;
import com.jiangnan.jpcap.NetworkInterface;
import com.jiangnan.jpcap.NetworkInterfaceAddress;
import com.jiangnan.jpcap.packet.ARPPacket;
import com.jiangnan.jpcap.packet.EthernetPacket;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * ARP(Address Resolution Protocol) 发送arp数据包，询问192.168.1.206的mac地址 Who has
 * 192.168.1.206? Tell 192.168.1.66 // 本机IP 1.66发起广播，询问1.206的mac 192.168.1.206
 * is at 00:0c:29:59:99:76 // 本机收到1.206的arp应答，告知1.206的mac是00:0c:29:59:99:76
 */
public class AprTest {
	public static byte[] arp(InetAddress ip) throws java.io.IOException {
		// find network interface
		NetworkInterface[] devices = JpcapCaptor.getDeviceList();
		NetworkInterface device = null;

		loop: for (NetworkInterface d : devices) {
			for (NetworkInterfaceAddress addr : d.addresses) {
				if (!(addr.address instanceof Inet4Address))
					continue;
				byte[] bip = ip.getAddress();
				byte[] subnet = addr.subnet.getAddress();
				byte[] bif = addr.address.getAddress();
				for (int i = 0; i < 4; i++) {
					bip[i] = (byte) (bip[i] & subnet[i]);
					bif[i] = (byte) (bif[i] & subnet[i]);
				}
				if (Arrays.equals(bip, bif)) {
					device = d;
					break loop;
				}
			}
		}

		// 本机网卡
		if (device == null)
			throw new IllegalArgumentException(ip + " is not a local address");

		// open Jpcap
		JpcapCaptor captor = JpcapCaptor.openDevice(device, 2000, false, 3000);
		// 过滤arp包
		captor.setFilter("arp", true);

		// 包发送器
		JpcapSender sender = captor.getJpcapSenderInstance();

		// 本机IP
		InetAddress srcip = null;
		for (NetworkInterfaceAddress addr : device.addresses)
			if (addr.address instanceof Inet4Address) {
				srcip = addr.address;
				break;
			}

		// 广播地址
		byte[] broadcast = new byte[] { (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255 };
		// arp包
		ARPPacket arp = new ARPPacket();
		// Hardware type: Ethernet (1)
		arp.hardtype = ARPPacket.HARDTYPE_ETHER;
		// Protocol type: IP (0x0800)
		arp.prototype = ARPPacket.PROTOTYPE_IP;
		// Opcode: request (1)
		arp.operation = ARPPacket.ARP_REQUEST;
		// Hardware size: 6
		arp.hlen = 6;
		// Protocol size: 4
		arp.plen = 4;
		// Sender MAC address: HonHaiPr_36:49:e0 (44:37:e6:36:49:e0)
		arp.sender_hardaddr = device.mac_address;
		// Sender IP address: 192.168.1.66 (192.168.1.66)
		arp.sender_protoaddr = srcip.getAddress();
		// Target MAC address: Broadcast (ff:ff:ff:ff:ff:ff)
		arp.target_hardaddr = broadcast;
		// Target IP address: 192.168.1.206 (192.168.1.206)
		arp.target_protoaddr = ip.getAddress();

		// 以太网包
		EthernetPacket ether = new EthernetPacket();
		// Type: ARP (0x0806)
		ether.frametype = EthernetPacket.ETHERTYPE_ARP;
		// Source: HonHaiPr_36:49:e0 (44:37:e6:36:49:e0)
		ether.src_mac = device.mac_address;
		// Destination: Broadcast (ff:ff:ff:ff:ff:ff)
		ether.dst_mac = broadcast;
		// 数据链
		arp.datalink = ether;
		
		// 发送arp数据包
		sender.sendPacket(arp);

		while (true) {
			/* 循环接收arp包 */
			ARPPacket p = (ARPPacket) captor.getPacket();
			if (p == null) {
				throw new IllegalArgumentException(ip + " is not a local address");
			}
			/* 当收到的arp包的目标地址和本地地址一致时，认为是得到的arp应答 */
			if (Arrays.equals(p.target_protoaddr, srcip.getAddress())) {
				return p.sender_hardaddr;
			}
		}
	}

	public static void main(String[] args) throws Exception {
		// 要询问mac地址的ip地址
		args = new String[] { "192.168.1.99" };
		if (args.length < 1) {
			System.out.println("Usage: java ARP <ip address>");
		} else {
			byte[] mac = AprTest.arp(InetAddress.getByName(args[0]));
			for (byte b : mac)
				System.out.print(Integer.toHexString(b & 0xff) + ":");
			System.out.println();
			System.exit(0);
		}
	}
}
