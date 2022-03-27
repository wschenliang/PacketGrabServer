package com.jiangnan.SWTGetNetPack.netcap;

import java.io.IOException;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import jpcap.JpcapCaptor;
import jpcap.JpcapWriter;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;

public class Netcaptor {

	JpcapCaptor jpcap = null;
	JFrameMain frame;
	LinkedList<Packet> packets = new LinkedList<Packet>();

	public void setJFrame(JFrameMain frame) {
		this.frame = frame;
	}

	public void capturePacketsFromDevice() {

		if (jpcap != null)
			jpcap.close();

		jpcap = Jcapturedialog.getJpcap(frame);

		if (jpcap != null) {
			startCaptureThread();
		}

	}

	private Thread captureThread;

	private void startCaptureThread() {

		if (captureThread != null)
			return;
		captureThread = new Thread(new Runnable() {
			public void run() {
				while (captureThread != null) {
					jpcap.processPacket(1, handler);
				}
			}
		});
		captureThread.setPriority(Thread.MIN_PRIORITY);
		captureThread.start();
	}

	void stopcaptureThread() {
		captureThread = null;
	}

	public void stopCapture() {
		stopcaptureThread();
	}

	private PacketReceiver handler = new PacketReceiver() {
		public void receivePacket(Packet packet) {
			// System.out.println(packet);
			packets.add(packet);
			frame.dealPacket(packet);
		}
	};
	private JpcapWriter writer;

	// ���沶������ļ���
	/**
	 * 
	 * @param fileName
	 * @param packetLinkedList
	 */
	public void saveFile(String fileName,LinkedList<Packet> packetLinkedList ) {
		if (jpcap == null) {
			JOptionPane.showMessageDialog(null, "No Packet yet!", "NO-PACKETS", JOptionPane.INFORMATION_MESSAGE);
		} else {
			boolean flag = false;//�Ƿ���ȫ�洢
			try {
				if (JFrameMain.isStartRecord == true) {
					if (writer == null) {
						writer = JpcapWriter.openDumpFile(jpcap, fileName);
					}
				}
				if (packetLinkedList == null || packetLinkedList.size() <= 0) {
					flag = true;
					writer = JpcapWriter.openDumpFile(jpcap, fileName);
					packetLinkedList = packets;
				}
				while (packetLinkedList.size() > 0) {
					writer.writePacket(packetLinkedList.removeFirst());
				}
				// writer.close();
				if (JFrameMain.isStartRecord == false ) {
						writer = null;
				}
				if (flag == true) {
					writer = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// �򿪴����ļ��еİ�
	public void openFile(String fileName) {
		try {
			jpcap = JpcapCaptor.openFile(fileName);
			while (true) {
				Packet packet = jpcap.getPacket();
				if (packet == null || packet.len == -1) {
					break;
				}
				frame.dealPacket(packet);
				/*try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (jpcap != null) {
				jpcap.close();
			}
		}
	}
	
	/*//���ù��˹���
		public static boolean TestFilter(Packet packet){
			if(filterMsg.contains("sip")){
				String sip = filterMsg.substring(4, filterMsg.length());
				if(new PacketAnalyze(packet).packetClass().get("ԴIP").equals(sip)){
					return true;
				}
			}else if(filterMsg.contains("dip")){
				String dip = filterMsg.substring(4, filterMsg.length());
				if(new PacketAnalyze(packet).packetClass().get("Ŀ��IP").equals(dip)){
					return true;
				}
			}else if(filterMsg.contains("ICMP")){
				if(new PacketAnalyze(packet).packetClass().get("Э��").equals("ICMP")){
					return true;
				}
			}
			else if(filterMsg.contains("UDP")){
				if(new PacketAnalyze(packet).packetClass().get("Э��").equals("UDP")){
					return true;
				}
			}else if(filterMsg.contains("TCP")){
				if(new PacketAnalyze(packet).packetClass().get("Э��").equals("TCP")){
					return true;
				}
			}else if(filterMsg.contains("keyword")){
				String keyword = filterMsg.substring(8, filterMsg.length());
				if(new PacketAnalyze(packet).packetClass().get("����").contains(keyword)){
					return true;
				}
			}else if(filterMsg.equals("")){
				return true;
			}
			return false;
		}*/
		
}