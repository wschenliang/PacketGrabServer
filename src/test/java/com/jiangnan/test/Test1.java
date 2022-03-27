package com.jiangnan.test;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;

import java.io.IOException;

/**
 * Jpcap 直接从数据链路层上读取数据，并向数据链路层中发送数据
 *
 */
public class Test1 {

	public static void main(String[] args) {
		listNetworkInterface();
//		openNetworkInterface();
//		capturePacketsByCallBack();
	}

	public static void listNetworkInterface() {
		// Obtain the list of network interfaces
		NetworkInterface[] devices = JpcapCaptor.getDeviceList();
		// for each network interface
		for (int i = 0; i < devices.length; i++) {
			// 网卡名称、描述
			System.out.println(i + ": " + devices[i].name + "(" + devices[i].description + ")");

			// 数据链路层名称、描述
			System.out.println(" datalink: " + devices[i].datalink_name + "(" + devices[i].datalink_description + ")");

			// print out its MAC address
			System.out.print(" MAC address:");
			for (byte b : devices[i].mac_address)
				System.out.print(Integer.toHexString(b & 0xff) + ":");
			System.out.println();

			// print out its IP address, subnet mask and broadcast address
			for (NetworkInterfaceAddress a : devices[i].addresses)
				System.out.println(" address:" + a.address + " " + a.subnet + " " + a.broadcast);
		}
	}

	/**
	 * 通过网卡，打开一个捕捉器
	 */
	public static JpcapCaptor openNetworkInterface() {
		JpcapCaptor jc = null;
		// Open an interface with openDevice(NetworkInterface intrface, int
		// snaplen, boolean promics, int to_ms)
		// (需要监听的网卡, 每次捕获的数据包最大长度<设置为IP包最大长度即可>, 是否过滤, 超时时间)
		try {
			jc = JpcapCaptor.openDevice(JpcapCaptor.getDeviceList()[0], 65535, false, 20);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jc;
	}

	/**
	 * 通过callBack回调方式捕捉数据
	 */
	public static void capturePacketsByCallBack() {
		JpcapCaptor captor = openNetworkInterface();
		/* 第一个参数：需要捕获的IP数据包的个数 */
		captor.loopPacket(10, new PacketReceiver() {
			@Override
			public void receivePacket(Packet packet) {
				System.out.println(packet);
			}
		});
		captor.close();
	}
}
