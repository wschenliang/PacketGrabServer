package com.jiangnan.arpattact.test;

import jpcap.JpcapCaptor;
import jpcap.packet.ARPPacket;
import jpcap.packet.Packet;

import java.net.InetAddress;
import java.util.Arrays;

public class AcceptThread implements Runnable {
	InetAddress senderIP; // = InetAddress.getByName("10.96.69.74");
	JpcapCaptor jc;
	public AcceptThread(InetAddress senderIP, JpcapCaptor jc){
		this.senderIP = senderIP;
		this.jc = jc;
	}
	
	@Override
	public void run(){
		long i = 1;
		//JpcapSender sender = jc.getJpcapSenderInstance();
		while(true){                                                //��ȡARP�ظ�����������ȡ��Ŀ��������MAC��ַ
				Packet packet = jc.getPacket();
				   if(packet instanceof ARPPacket){
					   ARPPacket p=(ARPPacket)packet;
					   if(Arrays.equals(p.target_protoaddr,senderIP.getAddress())){
						   System.out.println(Attack.macToString(p.sender_hardaddr));
						   System.out.println(Attack.ipToString(p.sender_protoaddr));
						   //System.out.println(p.operation);
						   System.out.println(i++);
						   //return p.sender_hardaddr;
					   }
				}
		}
	}

}
