package com.jiangnan.semple;

import jpcap.*;
import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.EthernetPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.TCPPacket;

import java.net.InetAddress;

class SendTCP
{
	public static void main(String[] args) throws java.io.IOException{
		NetworkInterface[] devices = JpcapCaptor.getDeviceList();
		if(args.length<1){
			System.out.println("Usage: java SentTCP <device index (e.g., 0, 1..)>");
			for(int i=0;i<devices.length;i++)
				System.out.println(i+":"+devices[i].name+"("+devices[i].description+")");
			System.exit(0);
		}
		int index=Integer.parseInt(args[0]);
		JpcapSender sender=JpcapSender.openDevice(devices[index]);

		TCPPacket p=new TCPPacket(12,34,56,78,false,false,false,false,true,true,true,true,10,10);
		p.setIPv4Parameter(0,false,false,false,0,false,false,false,0,1010101,100,IPPacket.IPPROTO_TCP,
			InetAddress.getByName("www.microsoft.com"),InetAddress.getByName("www.google.com"));
		p.data=("data").getBytes();
		
		EthernetPacket ether=new EthernetPacket();
		ether.frametype=EthernetPacket.ETHERTYPE_IP;
		ether.src_mac=new byte[]{(byte)0,(byte)1,(byte)2,(byte)3,(byte)4,(byte)5};
		ether.dst_mac=new byte[]{(byte)0,(byte)6,(byte)7,(byte)8,(byte)9,(byte)10};
		p.datalink=ether;

		for(int i=0;i<10;i++)
			sender.sendPacket(p);
	}
}