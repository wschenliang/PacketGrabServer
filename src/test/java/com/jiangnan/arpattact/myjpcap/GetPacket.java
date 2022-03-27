package com.jiangnan.arpattact.myjpcap;


import com.jiangnan.arpattact.ui.Main;
import jpcap.PacketReceiver;
import jpcap.packet.EthernetPacket;
import jpcap.packet.Packet;

/**
 * ����ʵ��PacketReceiver�ӿ�
 * @author �����
 * @since 2014/4/19
 */
public class GetPacket implements PacketReceiver {
	Main main;	//�����ھ��
	
	public GetPacket(Main main){
		this.main = main;
	}
	
	@Override
	public void receivePacket(Packet packet) {
		EthernetPacket ethernet = (EthernetPacket)packet.datalink;
		this.main.addPacket(packet);
	}

}
