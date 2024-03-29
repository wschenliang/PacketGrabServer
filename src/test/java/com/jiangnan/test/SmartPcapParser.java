package com.jiangnan.test;

import jpcap.*;
import jpcap.JpcapCaptor;
import jpcap.packet.*;

import java.util.Date;
import java.util.Vector;

class PacketInfo
{
	String src_ip;
	String dst_ip;
	String time;
	String flag;
	int src_port;
	int dst_port;
	int pro;
	int id;
	long seq;
	long ack;
 
	public PacketInfo()
	{
		src_ip = new String();
		dst_ip = new String();
		time = new String();
		flag = new String();
	}
}

public class SmartPcapParser {
 
 
	public Boolean ip_check(String ip1, String ip2){
		if(ip1.equals(ip2)){
			return true;
		}
 
		return false;
	}
 
	public void tcp_miss_check(PacketInfo packet_info, Vector<PacketInfo> packets){
	}
 
    public static void main(String[] args) {
		SmartPcapParser parser = new SmartPcapParser();
		IPPacket lastIp = null;
		Vector<PacketInfo> packets = new Vector<PacketInfo>();
		int f = 0;
		int i = 0;
		String[] pro_name = new String[60];
		pro_name[0] = "ip";
		pro_name[1] = "icmp";
		pro_name[6] = "tcp";
		pro_name[17] = "udp";
		int count = 0;
		String file_name = new String();
		Boolean only_parse = true;
		int packets_count = 0;
		String srcip_filter = new String();
		Boolean filt_srcip = false;
		String dstip_filter = new String();
		Boolean filt_dstip = false;
		Boolean filter_or = false;
		Boolean filter_result = false;
		Boolean filt_tcp_miss = false;
		Vector<String> ip_filter = new Vector<String>();
 
		if(args.length == 0){
			System.out.println("parament error!!!");
			return;
		}
 
		if(args[0].equals("-f"))
		{
			file_name = args[1];
		}
 
		for(i = 0; i < args.length; i++){
			if(args[i].equals("srcip") && (i+1 < args.length)){
				srcip_filter = args[i+1];
				filt_srcip = true;
			}
 
			if(args[i].equals("dstip") && (i+1 < args.length)){
				dstip_filter = args[i+1];
				filt_dstip = true;
			}
 
			if(args[i].equals("filter-or"))
				filter_or = true;
 
			if(args[i].equals("tcp-miss"))
				filt_tcp_miss = true;
 
			if(args[i].equals("ipaddr") && (i+1 < args.length))
				ip_filter.add(args[i+1]);
		}
 
		System.out.println("开始分析文件:"+file_name);
 
		JpcapCaptor captor = null;
		try{
			captor = JpcapCaptor.openFile(file_name);
			captor.setPacketReadTimeout(10000);
			captor.setNonBlockingMode(false);
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
 
		int j = 0;
 
		i = 0;
		while(true){
			//read a packet from the opened file
			Packet packet = captor.getPacket();
			packets_count++;
			i++;
			//if some error occurred or EOF has reached, break the loop
			if(packet==null || packet == Packet.EOF) {
				System.out.println("分析结束");
				break;
			}
 
			String flag = new String();
			long seq = 0;
			long ack = 0;
 
			//read ip packet and check fifo
			try{
				IPPacket ippacket = (IPPacket)packet;
				PacketInfo packet_info = new PacketInfo();
 
				packet_info.src_ip = ippacket.src_ip.getHostAddress();
				packet_info.dst_ip = ippacket.dst_ip.getHostAddress();
				packet_info.id = ippacket.ident;
				packet_info.pro = ippacket.protocol;
 
				if(filt_srcip){
					if(false == parser.ip_check(packet_info.src_ip, srcip_filter)){
						if(false == filter_or)
							continue;
					}
					else
					{
						if(filter_or)
							filter_result = true;
					}
				}
 
				if(filt_dstip){
					if(false == parser.ip_check(packet_info.dst_ip, dstip_filter)){
						if(false == filter_or)
							continue;
					}
					else{
						if(filter_or)
							filter_result = true;
					}
				}
 
				if(filter_or && false == filter_result)
					continue;
 
				if(ip_filter.size() > 0){
					filter_result = false;
					int c = 0;
					for(i = 0; i < ip_filter.size(); i++){
						String tmp = (String)(ip_filter.get(i));
						if(tmp.equals(packet_info.src_ip) || tmp.equals(packet_info.dst_ip)){
							filter_result = true;
							c++;
							break;
						}
					}
 
					if(false == filter_result || c < 2){
						continue;
					}
				}
 
				packet_info.time = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date((long)(packet.sec * 1000)));
				packet_info.time = packet_info.time + "." + new Long(packet.usec).toString();
 
				//TCP
				if (6 == ippacket.protocol){
					TCPPacket tcp_packet = (TCPPacket)packet;
					packet_info.src_port = tcp_packet.src_port;
					packet_info.dst_port = tcp_packet.dst_port;
					if(tcp_packet.psh)packet_info.flag = packet_info.flag + "psh";
					if(tcp_packet.rst)packet_info.flag = packet_info.flag + "rst";
					if(tcp_packet.syn)packet_info.flag = packet_info.flag + "syn";
					if(tcp_packet.ack)packet_info.flag = packet_info.flag + "ack";
					packet_info.seq = tcp_packet.sequence;
					packet_info.ack = tcp_packet.ack_num;
				}
 
				//UDP
				else if(17 == ippacket.protocol){
					UDPPacket udp_packet = (UDPPacket)packet;
					packet_info.src_port = udp_packet.src_port;
					packet_info.dst_port = udp_packet.dst_port;
				}
 
				//ICMP
				else if(1 == ippacket.protocol){
					ICMPPacket icmp_packet = (ICMPPacket)packet;
					packet_info.src_port = icmp_packet.seq;
				}
				else{
				}
 
				if(filt_tcp_miss){
					Boolean find = false;
					for(i = 0; i < packets.size(); i++){
						PacketInfo tmp = (PacketInfo)(packets.get(i));
						if(tmp.src_ip.equals(packet_info.dst_ip)
							&& tmp.dst_ip.equals(packet_info.src_ip)
							&& tmp.src_port == packet_info.dst_port
							&& tmp.dst_port == packet_info.src_port
							&& (tmp.seq + 1) == packet_info.ack){
							find = true;
 
							break;
						}
					}
 
					if(find){
						packets.removeElementAt(i);
					}
 
					/*过滤重传包*/
					find = false;
					for(i = 0; i < packets.size(); i++){
						PacketInfo tmp = (PacketInfo)(packets.get(i));
						if(tmp.src_ip.equals(packet_info.src_ip)
							&& tmp.dst_ip.equals(packet_info.dst_ip)
							&& tmp.src_port == packet_info.src_port
							&& tmp.dst_port == packet_info.dst_port
							&& tmp.ack == packet_info.ack
							&& tmp.seq == packet_info.seq){
							find = true;
							break;
						}
					}
					if(false == find)
						packets.add(packet_info);
 
					only_parse = false;
					//System.out.println(packets.size());
				}
 
				if(only_parse){
					System.out.print(packet_info.time+"."+packet.usec+" ip.id:"+packet_info.id+" "+pro_name[packet_info.pro] + " " + packet_info.src_ip + ":" + packet_info.src_port + " -> " + packet_info.dst_ip + ":" + packet_info.dst_port);
					if (6 == ippacket.protocol){
						System.out.println(" tcp.flags:" + packet_info.flag + " seq:" + packet_info.seq + " ack:" + packet_info.ack);
					}
					else{
					}
				}
			}catch(Exception e1){
				continue;
			}
		}
 
		if(filt_tcp_miss){
			for(i = 0; i < packets.size(); i++){
				PacketInfo tmp = (PacketInfo)(packets.get(i));
				System.out.print(tmp.time + " ip.id:"+tmp.id+" "+pro_name[tmp.pro] + " " + tmp.src_ip + ":" + tmp.src_port + " -> " + tmp.dst_ip + ":" + tmp.dst_port);
				System.out.println(" tcp.flags:" + tmp.flag + " seq:" + tmp.seq + " ack:" + tmp.ack);
			}
		}
 
		System.out.println("读取到报文" + packets_count + "个:");
		captor.close();
    }
}