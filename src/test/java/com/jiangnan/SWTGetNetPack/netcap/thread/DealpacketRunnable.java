package com.jiangnan.SWTGetNetPack.netcap.thread;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import com.jiangnan.SWTGetNetPack.netcap.JFrameMain;
import com.jiangnan.jpcap.packet.Packet;


public class DealpacketRunnable implements Runnable {
	private JFrameMain frame;
	private Packet packet;
	Vector< Object> vec = new Vector<Object>();
	
	public DealpacketRunnable(JFrameMain frame, Packet packet) {
		super();
		this.frame = frame;
		this.packet = packet;
	}
	@Override
	public void run() {
		frame.dealPacket(packet);
	}

}
