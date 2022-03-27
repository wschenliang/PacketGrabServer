package com.jiangnan.arpattact.test;

import com.jiangnan.jpcap.*;
import com.jiangnan.jpcap.packet.*;

import java.io.IOException;

public class Test {
	
	public static void main(String[] args) throws IOException {
		/**
		 * ��ȡ���ص������豸�б�����������������������win7������������
		 * NetworkInterface��jpcap�����һ����
		 */
		NetworkInterface[] devices = JpcapCaptor.getDeviceList();
		
		/**
		 * ����һ����ָ���豸�����Ӳ����ظ����ӡ�
		 * ��һ��������ָ��һ���豸
		 * �ڶ�������������ÿһ���յ�һ�����ݰ���ֻ��ȡ�����ݰ���ǰ�����ֽڣ�
		 * ����������������ģʽ��falseΪ����ģʽ������ģʽ��ץȡ���о������������豸�����ݰ�
		 * ���ĸ���������ʱʱ�䡣����һ��ʱ�䲶�񲻵��ͷ��ء�
		 */
		JpcapCaptor jpcap = JpcapCaptor.openDevice(devices[0], 65535, false, 20);	//65535bit
		
		/**
		 * ����ָ����Ŀ�İ�����һ��������ץ������Ŀ��-1��ʾ��ͣ��ץ�����ڶ�������Ϊʵ����PacketReceiver�ӿڵ��࣬���ص��ǲ��������Ŀ
		 * loopPacket�Զ���ץ���İ����������������ע����ÿץ��һ�����ʹ���һ��
		 * �˷������ܳ�ʱ����Ӱ��
		 */
		jpcap.loopPacket(-1, new MyJpcap());
		
		/**
		 * ���ܺ�����ķ���һ�������ǲ��ܳ�ʱӰ��
		 */
		jpcap.processPacket(10, new MyJpcap());
		
		/**
		 * ����һ�����񵽵�Packet
		 */
		Packet packet = jpcap.getPacket();	
		
		/**
		 * ��ֹ����ץ��������
		 */
		jpcap.breakLoop();
		
		/**
		 * �Ͽ����豸������
		 */
		jpcap.close();
	}
	
	
	
	
	public static String macToString(byte[] s){
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
}
