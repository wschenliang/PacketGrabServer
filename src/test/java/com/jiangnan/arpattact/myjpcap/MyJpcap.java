package com.jiangnan.arpattact.myjpcap;


import com.jiangnan.arpattact.ui.Main;
import com.jiangnan.jpcap.*;
import com.jiangnan.jpcap.packet.*;

import java.io.IOException;

/**
 * ����̳�Thread���ö��̱߳���������������Ӧ�ò�İ����
 * @author �����
 * @since 2014/4/18
 */
public class MyJpcap extends Thread {
	private int device = 0;	//Ĭ��ѡ�е��豸
	JpcapCaptor jpcap;
	Main main;	//�����ھ��
	
	public MyJpcap(int device, Main main){
		this.device = device;
		this.main = main;
	}
	
	@Override
	public void run() {
		try {
			jpcap = JpcapCaptor.openDevice(JpcapCaptor.getDeviceList()[device], 65535, false, 20);	//�豸���ƣ�ÿ�β���IP����󳤶ȣ�falseΪ����ģʽ����ʱʱ��
			jpcap.loopPacket(-1, new GetPacket(this.main));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("wrong!");
		
		}	
	}
	
	//ֹͣץ��
	public void breakLoop(){
		jpcap.breakLoop();
	}
}
