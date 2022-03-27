package com.jiangnan.arpattact.ui;


import com.jiangnan.arpattact.myjpcap.MyJpcap;
import com.jiangnan.arpattact.util.NetUtil;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.packet.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * ץ����������ࡣ��������PPT�ϵĳ����ͼ�е㲻һ������Ϊ��ȥ���ˡ����桱��ť��������ѡ��ԭ�����ǻ�û���ü�����
 * ��PPT��ʱ������ˣ�����ȥ�����ǡ������޹ؽ�Ҫ�ˣ�ʵ��Ŀ�Ĳ��������
 * ������ʹ�õ�jpcap��0.7�汾��ע�⿴jar��������������jpcap��jpcap.packet���汾�������޷�����.
 * �������е�ʱ������ѡ���豸�������ʼ����������ʾ����˵��ѡ����������ԡ����������0��������������1�������ߣ�2�������⡣
 * ע��jpcap.packet���ڵ����ݽṹ�����ϵͼ��PPT���������jpcap��̺���Ҫ
 * @author �����
 * @since 2014/4/18
 */
public class Main {

	private JFrame frame;
	private NetworkInterface[] devices = JpcapCaptor.getDeviceList();
	private int selectedDevice = 0; 	//�û�ѡ����豸˳��Ĭ��Ϊ0
	private long index = 0; 	//	���ݰ����
	
	
	public JButton btnStart;
	public Thread getPacketThread;	//ץ���߳�
	public JButton btnStop;
	private JTable table;
	JTextArea textAreaData;
	JTextArea textAreaString;
	private JTree packetTree;
	private DefaultTreeModel treeModel;
	
	public Main main = this;
	
	DefaultTableModel packetModel;	//tableModel
	ArrayList<Packet> packetList = new ArrayList<Packet>();	//Packet���飬�������packet��

	/**
	 * �������ڡ���
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * ע��������ǳ������main�������������ʱ���ֶ���ȡ�˸�Main������������
	 */
	public Main() {
		initialize();
	}

	/**
	 * ������Ҫ�ǽ���Swing��̣�����дע���ˡ�������Ĳ��ֻ���ץ���ǿ�~~
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("\u8BA1\u7B97\u673A\u7F51\u7EDC\u5B9E\u9A8C\u4E09");	//�����װٶ���unicode~
		frame.setBounds(100, 100, 1100, 714);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		btnStart = new JButton("\u5F00\u59CB");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getPacketThread = new MyJpcap(selectedDevice, main);
				getPacketThread.start();
				btnStart.setEnabled(false);
				btnStop.setEnabled(true);
			}
		});
		btnStart.setBounds(171, 0, 93, 23);
		frame.getContentPane().add(btnStart);
		
		JComboBox cbBoxDevice = new JComboBox();
		
		String model[] = new String[devices.length];
		for(int i = 0; i < model.length; i++){
			model[i] = i+"";
		}
		cbBoxDevice.setModel(new DefaultComboBoxModel(model));
		cbBoxDevice.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == e.SELECTED){	//�����ѡ���¼�
					selectedDevice = Integer.parseInt((String) e.getItem());	//����ѡ�е��豸
				}
				
			}
		});
		cbBoxDevice.setBounds(75, 1, 58, 21);
		frame.getContentPane().add(cbBoxDevice);
		
		btnStop = new JButton("\u505C\u6B62");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((MyJpcap) getPacketThread).breakLoop();
				getPacketThread.stop();
				btnStart.setEnabled(true);
				btnStop.setEnabled(false);
			}
		});
		btnStop.setEnabled(false);
		btnStop.setBounds(289, 0, 93, 23);
		frame.getContentPane().add(btnStop);
		
		JLabel lblDevice = new JLabel("\u9009\u62E9\u8BBE\u5907");
		lblDevice.setBounds(10, 4, 54, 15);
		frame.getContentPane().add(lblDevice);
		packetModel = new  DefaultTableModel(
				new Object[][] {},
				new String[] {
					"���", "ʱ��", "Դ��ַ", "Ŀ�ĵ�ַ", "Э��", "����", "��Ϣ"
				});
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 74, 1064, 268);
		frame.getContentPane().add(scrollPane);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		table.setModel(packetModel);
		
		//���table�¼�
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int index = table.getSelectedRow();
				Packet packet = packetList.get(index);
				setTree(packet);
				setDataArea(packet);
			}
		});
			
		JButton btnClear = new JButton("\u6E05\u7A7A");
		btnClear.addActionListener(new ActionListener() {	//�����Ϣ
			public void actionPerformed(ActionEvent e) {
				packetList.clear();
				long length = packetModel.getRowCount();
				for(int i = (int) (length - 1); i >= 0; i--){
					packetModel.removeRow(i);
				}
			}
		});
		btnClear.setBounds(415, 0, 93, 23);
		frame.getContentPane().add(btnClear);
		
		DefaultMutableTreeNode t1 = new DefaultMutableTreeNode("Packet",true);
		t1.add(new DefaultMutableTreeNode("null"));
		treeModel = new DefaultTreeModel(t1);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 352, 297, 310);
		frame.getContentPane().add(scrollPane_1);
		packetTree = new JTree(treeModel);
		scrollPane_1.setViewportView(packetTree);
		
		JScrollPane scrollPaneData = new JScrollPane();
		scrollPaneData.setBounds(315, 352, 373, 310);
		frame.getContentPane().add(scrollPaneData);
		
		//ʮ�����Ƶ�
		textAreaData = new JTextArea();
		scrollPaneData.setViewportView(textAreaData);
		textAreaData.setEditable(false);
		
		JScrollPane scrollPaneString = new JScrollPane();
		scrollPaneString.setBounds(698, 352, 376, 310);
		frame.getContentPane().add(scrollPaneString);
		
		//�����ַ��ı���
		textAreaString = new JTextArea();
		scrollPaneString.setViewportView(textAreaString);
		textAreaString.setEditable(false);
		
		table.getColumnModel().getColumn(0).setMinWidth(30);
		table.getColumnModel().getColumn(0).setMaxWidth(45);
		
		table.getColumnModel().getColumn(1).setMinWidth(120);
		table.getColumnModel().getColumn(1).setMaxWidth(180);
		
		table.getColumnModel().getColumn(2).setMinWidth(100);
		table.getColumnModel().getColumn(2).setMaxWidth(120);
		
		table.getColumnModel().getColumn(3).setMinWidth(100);
		table.getColumnModel().getColumn(3).setMaxWidth(120);
		
		table.getColumnModel().getColumn(4).setMinWidth(50);
		table.getColumnModel().getColumn(4).setMaxWidth(70);
		
		table.getColumnModel().getColumn(5).setMinWidth(40);
		table.getColumnModel().getColumn(5).setMaxWidth(50);
	}
	
	/**
	 * ���������packetList��������²��񵽵�packet���������������JTable�����һ���е���Ϣ.��������ǹ�GetPacket���õ�.
	 * @param packet ���񵽵İ�
	 */
	public void addPacket(Packet packet){
		packetList.add(packet);
		index++;
		
		//�����IP��
		if(packet instanceof IPPacket){
			packet = (IPPacket)packet;
			packetModel.addRow(new Object[]{
					index, 
					new Date(), 
					((IPPacket) packet).src_ip.getHostAddress(),
					((IPPacket) packet).dst_ip.getHostAddress(),
					NetUtil.getProtocol(((IPPacket) packet).protocol),
					packet.len,
					packet.toString()});
		}
		//�����ARP��
		else if(packet instanceof ARPPacket){
			ARPPacket arp = (ARPPacket)packet;
			packetModel.addRow(new Object[]{
					index,
					new Date(),
					arp.getSenderProtocolAddress().toString(),
					arp.getTargetProtocolAddress().toString(),
					"ARP",
					arp.len,
					arp.toString()
			});
		}
	}
	
	/**
	 * ������table�����һ��packet���򴴽�һ�����������������Ϣ
	 * @param packet �����û��������ţ���packet�����л�ȡ��Ӧ�İ�����ʾ
	 */
	public void setTree(Packet packet){
		//������
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("����Ϣ");
		root.add(new DefaultMutableTreeNode("���񳤶ȣ�"+packet.len));	//������Ϣ
		
		//��̫֡��Ϣ�ڵ�
		DefaultMutableTreeNode ethernetNode = new DefaultMutableTreeNode("��̫֡", true);
		EthernetPacket ethernet = (EthernetPacket) packet.datalink;
		ethernetNode.add(new DefaultMutableTreeNode("֡����"+ethernet.frametype));
		ethernetNode.add(new DefaultMutableTreeNode("ԴMAC��ַ��"+NetUtil.macToString(ethernet.src_mac)));
		ethernetNode.add(new DefaultMutableTreeNode("Ŀ��MAC��ַ��"+NetUtil.macToString(ethernet.dst_mac)));
		root.add(ethernetNode);
		
		//�����IP���ݰ�
		if(packet instanceof IPPacket){
			IPPacket ip = (IPPacket)packet;
			DefaultMutableTreeNode ipNode = new DefaultMutableTreeNode("IP���ݱ�", true);
			ipNode.add(new DefaultMutableTreeNode("�汾��"+ip.version));
			ipNode.add(new DefaultMutableTreeNode("����Ȩ��"+ip.priority));
			ipNode.add(new DefaultMutableTreeNode("�����ȣ�"+ip.length));
			ipNode.add(new DefaultMutableTreeNode("Э�飺"+NetUtil.getProtocol(ip.protocol)));
			ipNode.add(new DefaultMutableTreeNode("TTL��"+ip.hop_limit));
			ipNode.add(new DefaultMutableTreeNode("��ʶ��"+ip.ident));
			ipNode.add(new DefaultMutableTreeNode("���ַ������������"+ip.t_flag));
			ipNode.add(new DefaultMutableTreeNode("���ַ�����߿ɿ���"+ip.r_flag));
			ipNode.add(new DefaultMutableTreeNode("DF(���ܷ�Ƭ)��"+ip.dont_frag));	//ֻ�е�DF=0ʱ�������Ƭ��
			ipNode.add(new DefaultMutableTreeNode("MF(���з�Ƭ)��"+ip.more_frag));	//MF=0��ʾ�������������ݱ�Ƭ�е����һ����
			ipNode.add(new DefaultMutableTreeNode("Ƭλ�ƣ�"+ip.offset));
			ipNode.add(new DefaultMutableTreeNode("ԴIP��"+ip.src_ip.getHostAddress()));
			//ipNode.add(new DefaultMutableTreeNode("Դ������"+ip.src_ip.getHostName()));
			ipNode.add(new DefaultMutableTreeNode("Ŀ��IP��"+ip.dst_ip.getHostAddress()));
			//ipNode.add(new DefaultMutableTreeNode("Ŀ��������"+ip.dst_ip.getHostName()));
			root.add(ipNode);
			
			//�����UDP���ݱ�
			if(ip instanceof UDPPacket){
				UDPPacket udp = (UDPPacket)ip;
				DefaultMutableTreeNode udpNode = new DefaultMutableTreeNode("UDP����",true);
				udpNode.add(new DefaultMutableTreeNode("Դ�˿ڣ�" + udp.src_port));
				udpNode.add(new DefaultMutableTreeNode("Ŀ�Ķ˿ڣ�" + udp.dst_port));
				udpNode.add(new DefaultMutableTreeNode("���ĳ��ȣ�" + udp.length));
				root.add(udpNode);
			}
			/**
			 * TCP�������ֵ�ץ����������뿴PPT
			 */
			else if(ip instanceof TCPPacket){
				TCPPacket tcp = (TCPPacket)ip;
				DefaultMutableTreeNode tcpNode = new DefaultMutableTreeNode("TCP����",true);
				tcpNode.add(new DefaultMutableTreeNode("Դ�˿ڣ�" + tcp.src_port));
				tcpNode.add(new DefaultMutableTreeNode("Ŀ�Ķ˿ڣ�" + tcp.dst_port));
				tcpNode.add(new DefaultMutableTreeNode("���ĳ��ȣ�" + tcp.length));
				tcpNode.add(new DefaultMutableTreeNode("SYN��" + tcp.syn));	//һ��SYN�����ǽ�SYN�����Ϊ1��TCP��
				tcpNode.add(new DefaultMutableTreeNode("ACK��" + tcp.ack));	//SYN/ACK����ACK��SYNλ����true,Ҳ���ǵ�������λ����true��ʱ�����Ϊ���������еĵڶ���
				tcpNode.add(new DefaultMutableTreeNode("ȷ�Ϻţ�" + tcp.ack_num));	//ACK�����ǽ�ACK�����Ϊ1��TCP��.��Ҫע����ǵ������������.���ӽ����Ժ�TCP���ӵ�ÿ������������ACKλ
				tcpNode.add(new DefaultMutableTreeNode("��ţ�" + tcp.sequence));
				tcpNode.add(new DefaultMutableTreeNode("FIN��" + tcp.fin));	//�Ͽ�����
				tcpNode.add(new DefaultMutableTreeNode("URG��" + tcp.urg));	//��URG��1ʱ����������ָ���ֶ���Ч��������ϵͳ�˱��Ķ����н������ݣ�Ӧ���촫��(�൱�ڸ����ȼ�������)��
				tcpNode.add(new DefaultMutableTreeNode("URGָ�룺" + tcp.urgent_pointer));
				tcpNode.add(new DefaultMutableTreeNode("����(PSH)��" + tcp.psh));	//��TCP�Ƿ������ݴ���
				tcpNode.add(new DefaultMutableTreeNode("����ƫ�ƣ�" + tcp.offset));//ռ4bit����ָ��TCP���Ķε�������ʼ������TCP���Ķε���ʼ���ж�Զ��������ƫ�ơ��ĵ�λ�����ֽڶ���32bit�֣�4�ֽ�Ϊ���㵥λ����
				tcpNode.add(new DefaultMutableTreeNode("��������(RST)��" + tcp.rst));	//�������ã�һ����FIN֮��Ż����true.��RST��1ʱ������TCP�����г������ز����������������������ԭ�򣩣������ͷ����ӣ�Ȼ�������½����������ӡ�
				tcpNode.add(new DefaultMutableTreeNode("���ڣ�" + tcp.window));	//ռ2�ֽڡ������ֶ��������ƶԷ����͵�����������λΪ�ֽڡ�TCP���ӵ�һ�˸������õĻ���ռ��Сȷ���Լ��Ľ��մ��ڴ�С��Ȼ��֪ͨ�Է���ȷ���Է��ķ��ʹ��ڵ����ޡ�
				//tcpNode.add(new DefaultMutableTreeNode("ѡ�" + tcp.option));	//ѡ��
				root.add(tcpNode);
			}
			else if(ip instanceof ICMPPacket){
				ICMPPacket icmp = (ICMPPacket)ip;
				DefaultMutableTreeNode icmpNode = new DefaultMutableTreeNode("ICMP",true);
				icmpNode.add(new DefaultMutableTreeNode("���ͣ�" + icmp.type));
				icmpNode.add(new DefaultMutableTreeNode("���룺" + icmp.code));
				icmpNode.add(new DefaultMutableTreeNode("У��ͣ�" + icmp.checksum));
				root.add(icmpNode);
			}
		}
		else if(packet instanceof ARPPacket){
			ARPPacket arp = (ARPPacket)packet;
			DefaultMutableTreeNode arpNode = new DefaultMutableTreeNode("ARP���ݱ�", true);
			arpNode.add(new DefaultMutableTreeNode("Ӳ�����ͣ�" + NetUtil.getHardType(arp.hardtype)));
			arpNode.add(new DefaultMutableTreeNode("Э�����ͣ�" + ((arp.prototype==2048)?"IP":"Other")));
			arpNode.add(new DefaultMutableTreeNode("Э�鳤�ȣ�" + arp.plen + "�ֽ�"));
			arpNode.add(new DefaultMutableTreeNode("Ӳ�����ȣ�" + arp.hlen + "�ֽ�"));
			arpNode.add(new DefaultMutableTreeNode("�������ͣ�" + NetUtil.getOperationType(arp.operation)));
			root.add(arpNode);
		}
		
		treeModel.setRoot(root);
		
	}
	
	/**
	 * ������table�����һ��packet������ʾ�������16�������ݡ��ַ�����(������ʾ)
	 * @param packet �����û��������ţ���packet�����л�ȡ��Ӧ�İ�����ʾ
	 */
	public void setDataArea(Packet packet){
		this.textAreaData.setText("");
		this.textAreaData.append("ʮ���������ݣ�\n");
		this.textAreaString.setText("");
		this.textAreaString.append("�ַ�����(ͨ��ASCIIת��)��\n");
		if(packet instanceof TCPPacket){
			TCPPacket tcp = (TCPPacket)packet;
			this.textAreaData.append("���ݳ���" + tcp.data.length+"\n");
			this.textAreaString.append("���ݳ���" + tcp.data.length+"\n");
			int num = 20; 	//ÿ����ʾ���ٸ�
			for(int i=0; i < tcp.data.length; i++){
	   			String hex = Integer.toHexString((tcp.data[i]&0xff));
	   			if(hex.length() == 1){
	   				hex = "0" + hex;
	   			}
	   			
	   			this.textAreaData.append(hex+" ");
	   			this.textAreaString.append((char)Integer.parseInt(hex, 16)+"");
	   			if(num == 1){
	   				num = 20;
	   				this.textAreaData.append("\n");
	   			}
	   			num--;
	   		}
		}
		else if(packet instanceof UDPPacket){
			UDPPacket udp = (UDPPacket)packet;
			this.textAreaData.append("���ݳ���" + udp.data.length+"\n");
			this.textAreaString.append("���ݳ���" + udp.data.length+"\n");
			int num = 20; 	//ÿ����ʾ���ٸ��ַ�
			for(int i=0; i < udp.data.length; i++){
	   			String hex = Integer.toHexString((udp.data[i]&0xff));
	   			if(hex.length() == 1){
	   				hex = "0" + hex;
	   			}
	   			this.textAreaData.append(hex+" ");
	   			this.textAreaString.append((char)Integer.parseInt(hex, 16)+"");
	   			if(num == 1){
	   				num = 20;
	   				this.textAreaData.append("\n");
	   			}
	   			num--;
	   		}
		}
		else if(packet instanceof ARPPacket){
			ARPPacket arp = (ARPPacket)packet;
			this.textAreaData.append("���ݳ���" + arp.data.length+"\n");
			this.textAreaString.append("���ݳ���" + arp.data.length+"\n");
			int num = 20; 	//ÿ����ʾ���ٸ��ַ�
			for(int i=0; i < arp.data.length; i++){
	   			String hex = Integer.toHexString((arp.data[i]&0xff));
	   			if(hex.length() == 1){
	   				hex = "0" + hex;
	   			}
	   			this.textAreaData.append(hex+" ");
	   			this.textAreaString.append((char)Integer.parseInt(hex, 16)+"");
	   			if(num == 1){
	   				num = 20;
	   				this.textAreaData.append("\n");
	   			}
	   			num--;
	   		}
		}
	}
}
