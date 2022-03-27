package com.jiangnan.arpattact.test;

import com.jiangnan.jpcap.PacketReceiver;
import com.jiangnan.jpcap.packet.*;


public class MyJpcap implements PacketReceiver {
	static int j=0;
    static long p=0,q=0;
    IPPacket ip;
    String s,s1;
    Long s2;
   static long time11,time12;
	String time;     
   	static long num=0; //��һ����̬��������ʾץ�İ�����ţ���Ȼ���Լ�ץ��˳�򶼲�֪��
   	//public static ConnectException conn;
	
   	public void print(byte[] b){
   		for(int i=0; i < b.length; i++){
   			String a = Integer.toHexString((b[i]&0xff));//toBinaryString((b[i]&0xff));
   			if(a.length() == 1){
   				a = "0" + a;
   			}
   			System.out.print(a);
   			System.out.print(" ");
   			System.out.print((char)Integer.parseInt(a, 16));
   		}
   		System.out.println();
   	}
   	
	@Override
	public void receivePacket(Packet packet) {
		
		EthernetPacket ethernet = (EthernetPacket)packet.datalink;
		if(packet instanceof TCPPacket){
			TCPPacket tcp = (TCPPacket)packet;
			System.out.println("ԴIP:"+tcp.src_ip+" �˿�:"+tcp.src_port+" Ŀ��IP:"+tcp.dst_ip+" Ŀ�Ķ˿�:"+tcp.dst_port
				+" Ŀ��MAC:"+ ethernet.dst_mac + " Э��:TCP" + " ����:");
			for(int i = 0; i < tcp.data.length; i++)
				System.out.print((char)tcp.data[i]);
		
			System.out.println("");
		
			print(tcp.data);
		}
	
	else if(packet instanceof UDPPacket) {
		UDPPacket udp = (UDPPacket)packet;
		
			System.out.println("ԴIP:"+udp.src_ip+" �˿�:"+udp.src_port+" Ŀ��IP:"+udp.dst_ip+" Ŀ�Ķ˿�:"+udp.dst_port
					+" Ŀ��MAC:"+ ethernet.dst_mac+ " Э��:UDP" + " ����:");
		
		for(int i = 0; i < udp.data.length; i++)
			System.out.print(udp.data[i]);
		System.out.println("");
		print(udp.data);
		
	}
	
	/*
	//�ж��ǲ�����ip��
	if(packet instanceof IPPacket){ 
		//System.out.println(j+"ok");
		ip=(IPPacket)packet ;
		if(j<10000){ 
  	 
  	         
			try {
				RandomAccessFile rf = new RandomAccessFile("packet.txt", "rw");//��̬�ļ�
				rf.seek(rf.length());//�ļ�β
				rf.writeBytes(num+"\t"+ip.src_ip+"\t"+ip.dst_ip+"\t"+ip.protocol+"\t"+
				ip.length+"\t"+ip.version+"\t"+ip.ident+"\t"+
				ip.rsv_frag+"\t"+ip.offset+"\t"+ip.hop_limit+"\t"+
				ip.rsv_tos+"\r\n");//д��
				rf.close();//�ر��ļ�
				num++;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			
			//�ϲ�Э��TCP(6)��UDP(17)
			System.out.println(
			num+"\tԴ:"+ip.src_ip+"\tĿ:"+ip.dst_ip+"\t�ϲ�Э��:"+ip.protocol+"\t����:"+
			ip.length+"\t�汾:"+ip.version+"\t��ʶ:"+ip.ident+"\t"+
			ip.rsv_frag+"\t"+ip.offset+"\t"+ip.hop_limit+"\t"+
			ip.rsv_tos+"\tTTL:"+ip.hop_limit+"\r\n");
			j++;
			
		}
		*/
    /*   
	else {
    	   time12=System.currentTimeMillis();//��ǰϵͳʱ��
    	   System.out.println(""+time12+"-"+time11+"="+(time12-time11)+"\ncapture count:"+j);
    	   System.exit(0);
    }
    */
	}
}


