package com.jiangnan.SWTGetNetPack.netcap;

import java.awt.BorderLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.jiangnan.SWTGetNetPack.netcap.button.StartRecordButton;
import com.jiangnan.SWTGetNetPack.netcap.button.StopRecordButton;
import jpcap.packet.ARPPacket;
import jpcap.packet.ICMPPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
import jpcap.packet.UDPPacket;


public class JFrameMain extends javax.swing.JFrame implements ActionListener {

	private JMenuItem exitMenuItem;
	private JSeparator jSeparator2;
	private JMenuItem saveAsMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem stopMenuItem;
	private JMenuItem startMenuItem;
	private JMenuItem clearMenuItem;
	private JMenu menu;
	private JMenuBar jMenuBar1;
	private JButton startButton,stopButton;

	JTable tabledisplay = null;
	Vector<Object> rows, columns;
	DefaultTableModel tabModel;
	// JScrollPane scrollPane;
	JScrollPane dataPane;
	JLabel statusLabel;

	Netcaptor captor = new Netcaptor();
	private JMenuItem openMenuItem;
	private TextArea textArea;
	private JPopupMenu m_popupMenu;
	//Ҫ�����packet
	LinkedList<Packet> pLinkedList = new LinkedList<Packet>();
	private JMenuItem restartMenuItem;
	//��¼�ļ���
	private static String startRecordFileName = null;
	//�Ƿ�ʼ��¼
	public static boolean isStartRecord = false;

	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(String[] args) {
		JFrameMain inst = new JFrameMain();
		inst.setVisible(true);
		inst.captor.setJFrame(inst);
		inst.setSize(800, 500);
		inst.setBounds(500, 200, 800, 500);
	}

	public JFrameMain() {
		super();
		initGUI();
		createPopupMenu();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}

		});
	}

	@Override
	public void dispose() {

		super.dispose();
		System.exit(0);
	}

	private void initGUI() {
		try {
			setSize(400, 300);
			{
				jMenuBar1 = new JMenuBar();
				setJMenuBar(jMenuBar1);
				{
					menu = new JMenu();
					jMenuBar1.add(menu);
					menu.setText("\u6293\u5305");
					menu.setPreferredSize(new java.awt.Dimension(35, 21));
					{
						startMenuItem = new JMenuItem();
						menu.add(startMenuItem);
						startMenuItem.setText("��ʼ");
						startMenuItem.setActionCommand("start");
						startMenuItem.addActionListener(this);
					}
					
					{
						stopMenuItem = new JMenuItem();
						menu.add(stopMenuItem);
						stopMenuItem.setText("ֹͣ");
						stopMenuItem.setActionCommand("stop");
						stopMenuItem.addActionListener(this);
					}
					{
						openMenuItem = new JMenuItem();
						menu.add(openMenuItem);
						openMenuItem.setText("��");
						openMenuItem.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent arg0) {
								JFileChooser chooser = new JFileChooser();

								int returnType = chooser.showOpenDialog(null);

								if (returnType == JFileChooser.APPROVE_OPTION) {

									File file = chooser.getSelectedFile();

									String fileName = file.getAbsolutePath();

									// CatchPacket.sb.delete(0, CatchPacket.sb.length());
									captor.openFile(fileName);

								}
							}
						});
					}
					{
						saveMenuItem = new JMenuItem();
						menu.add(saveMenuItem);
						saveMenuItem.setText("����");
						saveMenuItem.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent arg0) {
								captor.saveFile(System.currentTimeMillis() + ".pcap",null);
							}
						});
					}
					{
						saveAsMenuItem = new JMenuItem();
						menu.add(saveAsMenuItem);
						saveAsMenuItem.setText("����Ϊ ...");
						saveAsMenuItem.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								JFileChooser chooser = new JFileChooser();

								int returnType = chooser.showSaveDialog(null);

								if (returnType == JFileChooser.APPROVE_OPTION) {

									File file = chooser.getSelectedFile();

									String fileName = file.getAbsolutePath();

									captor.saveFile(fileName,null);

								}
							}
						});
					}
					{
						jSeparator2 = new JSeparator();
						menu.add(jSeparator2);
					}
					
					{
						clearMenuItem = new JMenuItem();
						menu.add(clearMenuItem);
						clearMenuItem.setText("clear");
						clearMenuItem.setActionCommand("clear");
						clearMenuItem.addActionListener(this);
					}
					{
						restartMenuItem = new JMenuItem();
						menu.add(restartMenuItem);
						restartMenuItem.setText("����");
						restartMenuItem.setActionCommand("restart");
						restartMenuItem.addActionListener(this);
					}
					{
						exitMenuItem = new JMenuItem();
						menu.add(exitMenuItem);
						exitMenuItem.setText("Exit");
						exitMenuItem.setActionCommand("exit");
						exitMenuItem.addActionListener(this);
					}
				}
				
				{
					startButton = new StartRecordButton("��ʼ��¼");
					jMenuBar1.add(startButton);
					startButton.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							System.err.println("start =========>"+isStartRecord);
							if (isStartRecord == false) {
								isStartRecord = true;
							}
						}
					});
				}
				{
					stopButton = new StopRecordButton("������¼");
					jMenuBar1.add(stopButton);
					stopButton.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							System.err.println("stop =========>" + isStartRecord);
							if (isStartRecord == true) {
								isStartRecord = false;
							}
						}
					});
				}
			}

			rows = new Vector<Object>();
			columns = new Vector<Object>();

			columns.addElement("���");
			columns.addElement("���ݱ�ʱ��");
			columns.addElement("Э��");
			columns.addElement("ԴIP��ַ:�˿�");
			columns.addElement("Ŀ��IP��ַ:�˿�");
			columns.addElement("�Ƿ�ֶ�");
			columns.addElement("�Ƿ��и���Ƭ");
			columns.addElement("�ֶ�ƫ����");
			columns.addElement("id");
			columns.addElement("�ײ�����");
			columns.addElement("���ݳ���");
			columns.addElement("�ײ�����");
			columns.addElement("��������");

			tabModel = new DefaultTableModel() {
				private static final long serialVersionUID = -4939561478944288908L;

				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}

			};

			tabModel.setDataVector(rows, columns);
			/*
			 * tabModel.addTableModelListener(new TableModelListener() {
			 * 
			 * @Override public void tableChanged(TableModelEvent arg0) { int lastRow =
			 * arg0.getLastRow(); Vector v = (Vector) rows.get(lastRow);
			 * textArea.setText("�ײ����ݣ�\n"+v.get(8) + "\r\n�������ݣ�\n"+v.get(9)); } });
			 */
			tabledisplay = new JTable(tabModel);
			tabledisplay.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent event) {
					// System.err.println("================>"+event.getButton());
					if (event.getButton() == 1) {// ���
						int selectedRow = tabledisplay.getSelectedRow();
						@SuppressWarnings("unchecked")
						Vector<Object> v = (Vector<Object>) rows.get(selectedRow);
						textArea.setText("�ײ����ݣ�\n" + v.get(11) + "\r\n�������ݣ�\n" + v.get(12));
					}
					if (event.getButton() == 3) {// �Ҽ�
						 //ͨ�����λ���ҵ����Ϊ����е���
			            int focusedRowIndex = tabledisplay.rowAtPoint(event.getPoint());
			            if (focusedRowIndex == -1) {
			                return;
			            }
			            //�������ѡ����Ϊ��ǰ�Ҽ��������
			            tabledisplay.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
			            //�����˵�
			            m_popupMenu.show(tabledisplay, event.getX(), event.getY());

					}
				}

			});
			// scrollPane = new JScrollPane(tabledisplay);
			this.getContentPane().add(new JScrollPane(tabledisplay), BorderLayout.CENTER);
			tabledisplay.validate();
			tabledisplay.updateUI();
			textArea = new TextArea(5, 10);
			textArea.setEditable(false);
			textArea.setColumns(20);

			this.getContentPane().add(textArea, BorderLayout.SOUTH);
			/*
			 * statusLabel = new JLabel("admin"); this.getContentPane().add(statusLabel,
			 * BorderLayout.SOUTH);
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static int index = 1;

	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();

		if (cmd.equals("start")) {
			captor.capturePacketsFromDevice();
			captor.setJFrame(this);
		} else if (cmd.equals("stop")) {
			captor.stopCapture();
		} else if (cmd.equals("exit")) {
			System.exit(0);
		} else if (cmd.equals("clear")) {
			rows.clear();
			index = 1;
			tabledisplay.addNotify();
			textArea.setText("");
		}else if (cmd.equals("restart")) {
			System.exit(23);
		}
	}

	public void dealPacket(Packet packet) {
		/*
		 * if (! (packet instanceof UDPPacket)) { return ; }
		 */
		try {
			Vector<Object> r = new Vector<Object>();
			String strtmp;
			Timestamp timestamp = new Timestamp((packet.sec * 1000) + (packet.usec / 1000));
			r.addElement(index);
			index++;
			r.addElement(timestamp.toString()); // ���ݱ�ʱ��
			if (packet instanceof ARPPacket) {
				r.addElement("ARP");
				r.addElement(((ARPPacket) packet).sec); // ԴIP��ַ
				r.addElement(((ARPPacket) packet).usec); // Ŀ��IP��ַ
				r.addElement("δ֪"); // �Ƿ񲻷ֶ�
				r.addElement("δ֪"); // �Ƿ��и���Ƭ
				r.addElement("δ֪"); // ����ƫ����
				r.addElement("δ֪"); // ����ƫ����
			} else if (packet instanceof ICMPPacket) {
				r.addElement("ICMP");
				r.addElement(((IPPacket) packet).src_ip.toString()); // ԴIP��ַ
				r.addElement(((IPPacket) packet).dst_ip.toString()); // Ŀ��IP��ַ
				r.addElement(((IPPacket) packet).dont_frag == true ? "�ֶ�" : "���ֶ�"); // �Ƿ񲻷ֶ�
				r.addElement(((IPPacket) packet).more_frag == true ? "��" : "��"); // �Ƿ��и���Ƭ
				r.addElement(((IPPacket) packet).offset); // ����ƫ����
				r.addElement(((IPPacket) packet).ident); // ����ƫ����
			} else if (packet instanceof IPPacket) {
				// IPPacket ipPacket = ((IPPacket) packet);
				if (packet instanceof UDPPacket) {
					r.addElement("UDP");
					r.addElement(((UDPPacket) packet).src_ip.toString() + ":" + ((UDPPacket) packet).src_port); // ԴIP��ַ
					r.addElement(((UDPPacket) packet).dst_ip.toString() + ":" + ((UDPPacket) packet).dst_port); // Ŀ��IP��ַ
					r.addElement(((IPPacket) packet).dont_frag == true ? "�ֶ�" : "���ֶ�"); // �Ƿ񲻷ֶ�
					r.addElement(((IPPacket) packet).more_frag == true ? "��" : "��"); // �Ƿ��и���Ƭ
					r.addElement(((IPPacket) packet).offset); // ����ƫ����
					r.addElement(((IPPacket) packet).ident); // ����ƫ����
				} else if (packet instanceof TCPPacket) {
					r.addElement("TCP");
					r.addElement(((TCPPacket) packet).src_ip.toString() + ":" + ((TCPPacket) packet).src_port); // ԴIP��ַ
					r.addElement(((TCPPacket) packet).dst_ip.toString() + ":" + ((TCPPacket) packet).dst_port); // Ŀ��IP��ַ
					r.addElement(((IPPacket) packet).dont_frag == true ? "�ֶ�" : "���ֶ�"); // �Ƿ񲻷ֶ�
					r.addElement(((IPPacket) packet).more_frag == true ? "��" : "��"); // �Ƿ��и���Ƭ
					r.addElement(((IPPacket) packet).offset); // ����ƫ����
					r.addElement(((IPPacket) packet).ident); // ����ƫ����
				} else {
					byte version = ((IPPacket) packet).version;
					r.addElement("IP" + version);
					r.addElement(((IPPacket) packet).src_ip.toString()); // ԴIP��ַ
					r.addElement(((IPPacket) packet).dst_ip.toString()); // Ŀ��IP��ַ
					r.addElement(((IPPacket) packet).dont_frag == true ? "�ֶ�" : "���ֶ�"); // �Ƿ񲻷ֶ�
					r.addElement(((IPPacket) packet).more_frag == true ? "��" : "��"); // �Ƿ��и���Ƭ
					r.addElement(((IPPacket) packet).offset); // ����ƫ����
					r.addElement(((IPPacket) packet).ident); // ����ƫ����
				}
			} else if (packet instanceof IPPacket) {
				byte version = ((IPPacket) packet).version;
				r.addElement("IP" + version);
				r.addElement(((IPPacket) packet).src_ip.toString()); // ԴIP��ַ
				r.addElement(((IPPacket) packet).dst_ip.toString()); // Ŀ��IP��ַ
				r.addElement(((IPPacket) packet).dont_frag == true ? "�ֶ�" : "���ֶ�"); // �Ƿ񲻷ֶ�
				r.addElement(((IPPacket) packet).more_frag == true ? "��" : "��"); // �Ƿ��и���Ƭ
				r.addElement(((IPPacket) packet).offset); // ����ƫ����
				r.addElement(((IPPacket) packet).ident); // ����ƫ����
			}else {
				r.addElement("Another");
				r.addElement( packet.sec); // ԴIP��ַ
				r.addElement(packet.usec); // Ŀ��IP��ַ
				r.addElement("δ֪"); // �Ƿ񲻷ֶ�
				r.addElement("δ֪"); // �Ƿ��и���Ƭ
				r.addElement("δ֪"); // ����ƫ����
				r.addElement("δ֪"); // ����ƫ����
			}

			r.addElement(packet.header.length); // �ײ�����
			r.addElement(packet.data.length); // ���ݳ���
			strtmp = "";
			for (int i = 0; i < packet.header.length; i++) {
				// strtmp += Byte.toString(packet.header[i])+ " ";
				String hexString = Integer.toHexString(packet.header[i]);
				if (hexString.length() < 2) {
					hexString = "0"+hexString;
				}
				strtmp += hexString + " ";
			}
			r.addElement(strtmp.replaceAll("ffffff", "")); // �ײ�����
			String hearder = strtmp;
			strtmp = "";
			for (int i = 0; i < packet.data.length; i++) {
				// strtmp += Byte.toString(packet.data[i])+" ";
				String hexString = Integer.toHexString(packet.data[i]);
				if (hexString.length() < 2) {
					hexString = "0"+hexString;
				}
				strtmp += Integer.toHexString(packet.data[i]) + " ";
			}
			r.addElement(strtmp.replaceAll("ffffff", "")); // ��������
			r.addElement(packet);
			rows.addElement(r);
			tabledisplay.addNotify();
			textArea.setText(
					"�ײ����ݣ�\n" + hearder.replaceAll("ffffff", "") + "\r\n�������ݣ�\n" + strtmp.replaceAll("ffffff", ""));
			if (isStartRecord == true) {
				if (startRecordFileName == null || "".equals(startRecordFileName)) {
					long currentTimeMillis = System.currentTimeMillis();
					startRecordFileName = currentTimeMillis+".pacp";
				}
				//File recordFile = new File("D://log/"+currentTimeMillis+".pcap",true);
				pLinkedList.add(packet);
				System.err.println("Running......");
				captor.saveFile("D://log/"+startRecordFileName,pLinkedList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * �����Ҽ��˵�
	 */
	private void createPopupMenu() {
		m_popupMenu = new JPopupMenu();

		JMenuItem savePopItem = new JMenuItem();
		savePopItem.setText("���浱ǰ");
		savePopItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// �ò�����Ҫ������
				int selectedRow = tabledisplay.getSelectedRow();
				@SuppressWarnings("unchecked")
				Vector<Object> v = (Vector<Object>) rows.get(selectedRow);
				Packet packet = (Packet) v.get(13);
				JFileChooser chooser = new JFileChooser();
				pLinkedList.add(packet);
				int returnType = chooser.showSaveDialog(null);

				if (returnType == JFileChooser.APPROVE_OPTION) {

					File file = chooser.getSelectedFile();

					String fileName = file.getAbsolutePath();

					captor.saveFile(fileName,pLinkedList);

				}
			}
		});
		m_popupMenu.add(savePopItem);
	}
}