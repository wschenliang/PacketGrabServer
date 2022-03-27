package com.jiangnan.demo;

import com.jiangnan.jpcap.JpcapCaptor;
import com.jiangnan.jpcap.NetworkInterface;
import com.jiangnan.jpcap.PacketReceiver;
import com.jiangnan.jpcap.packet.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author yinjiale
 * @create 2020/10/25 2:44
 * 说明:
 */
public class TestMethod {
    private static List<JpcapCaptor> list = new ArrayList<>();
    private static JTable jtable=null;
    public void start(JTable table,JComboBox jComboBox){
        {
            jtable=table;
            DefaultTableModel model = (DefaultTableModel)jtable.getModel();
            model.setRowCount(0);
            try{
                //获取的网络接口对象数组
                // TODO 分接口  源数据和协议分析 调整框体大小
                Object selectedItem = jComboBox.getSelectedItem();

                final  NetworkInterface[] devices = JpcapCaptor.getDeviceList();
                for(int i=0;i<devices.length;i++){
                    NetworkInterface nc=devices[i];
                    if (nc.name.equals(selectedItem)){
                        JpcapCaptor jpcap = JpcapCaptor.openDevice(nc, 1000, true, 20);
                        list.add(jpcap);
                        startCapThread(jpcap,nc);
                        System.out.println("开始抓取第"+i+"个卡口上的数据");
                        break;
                    }
                }
            }catch(Exception ef){
                ef.printStackTrace();
                System.out.println("启动失败:  "+ef);
            }

        }
    }
    public static void startCapThread(final JpcapCaptor jpcap, final NetworkInterface device){
        Runnable rnner=new Runnable(){
            public void run(){
                //使用接包处理器循环抓包
                jpcap.loopPacket(-1, new TestPacketReceiver(jtable,device));
            }    //TODO 每个线程启动loop方法，loop才是真正的无限循环方法，即使后面关闭线程也无效，线程做的只是开启loop，开启了就灭亡了，breakLoop可以关闭
        };

        Thread thread = new Thread(rnner);
        thread.start();//启动抓包线程

    }
    public static void shutDown(){
        int i = 0;
        for (JpcapCaptor jpcapCaptor : list) {
            jpcapCaptor.breakLoop();
            System.out.println("进程"+i+++"已经关闭");
        }
    }

}

class TestPacketReceiver  implements PacketReceiver {
    private static int number; // 编号
    private DefaultTableModel jTable = null;
    private NetworkInterface Device = null;
    private static long START_TIME; //抓包开始时间

    static {
            number =1;
            START_TIME = System.currentTimeMillis();
    }
    public TestPacketReceiver(JTable table,NetworkInterface device){
        jTable = (DefaultTableModel) table.getModel();
        Device = device;

    }
    public List<String> datalink_show(EthernetPacket packet){
        List<String> list = new ArrayList<>();
        list.add(packet.frametype+"");
        list.add(packet.getDestinationAddress());
        list.add(packet.getSourceAddress());
        list.add(Device.name);
        return list;
    }
    /**
     *
     */
    public void receivePacket(Packet packet) {
        //Tcp包
        if(packet instanceof TCPPacket){
            TCPPacket p=(TCPPacket)packet;
            String src = String.valueOf(p.src_ip).substring(1);
            String dst = String.valueOf(p.dst_ip).substring(1);
            List<String> list = datalink_show((EthernetPacket) packet.datalink);
            byte[] data = p.data;
            String[][] dataBianary = new String[data.length/16+1][16];
            for (int i = 0; i < data.length; i++) {
                StringBuilder string = new StringBuilder(Integer.toBinaryString(Math.abs(data[i])));
                int len = 8- string.length();
                for (int j = 0; j < len; j++) {
                    string.insert(0,'0');
                }
                if (data[i]<0){
                    string.delete(0,1);
                    string.insert(0,'1');
                }
                dataBianary[i/16][i%16] = string.toString();

            }
            jTable.addRow(new String[]{number+++"",(System.currentTimeMillis()-START_TIME)+"",src,dst,"TCP",p.len+"", list.get(0),list.get(3),
                    list.get(1),list.get(2),p.src_port+"",p.dst_port+"",p.version+"",p.offset+"",p.length+"",p.hop_limit+"",p.dont_frag+""
                    ,p.more_frag+"",p.rsv_frag+"",p.ack+"",p.ack_num+"",p.urgent_pointer+"",p.window+"","",p.sequence+"",p.rsv1+"",p.rsv2+"",
                    p.urg+"",p.ack+"",p.psh+"",p.rst+"",p.syn+"",p.fin+"",p.rsv_tos+"",p.ident+"",Arrays.deepToString(dataBianary)
            });

        }
        //UDP包
        else if(packet instanceof UDPPacket){
            UDPPacket p=(UDPPacket)packet;
            String src = String.valueOf(p.src_ip).substring(1);
            String dst = String.valueOf(p.dst_ip).substring(1);
            List<String> list = datalink_show((EthernetPacket) packet.datalink);
            byte[] data = p.data;
            String[][] dataBianary = new String[data.length/16+1][16];
            for (int i = 0; i < data.length; i++) {
                StringBuilder string = new StringBuilder(Integer.toBinaryString(Math.abs(data[i])));
                int len = 8- string.length();
                for (int j = 0; j < len; j++) {
                    string.insert(0,'0');
                }
                if (data[i]<0){
                    string.delete(0,1);
                    string.insert(0,'1');
                }
                dataBianary[i/16][i%16] = string.toString();

            }
            jTable.addRow(new String[]{number+++"",(System.currentTimeMillis()-START_TIME)+"",src,dst,"UDP",p.len+"",
                list.get(0),list.get(3),list.get(1),list.get(2),p.src_port+"",p.dst_port+"",p.version+"",p.offset+"",
                    p.length+"",p.hop_limit+"",p.dont_frag+"",p.more_frag+"",p.rsv_frag+"","","","","",p.length+"","","","","","","","","","","", "",Arrays.deepToString(dataBianary),
            });
        }
        //是否地址转换协议请求包
        else if(packet instanceof ARPPacket){
            ARPPacket p=(ARPPacket)packet;
            //Returns the hardware address (MAC address) of the sender
            Object  saa=   p.getSenderHardwareAddress();
            Object  taa=p.getTargetHardwareAddress();
            String src = ((String)p.getSenderHardwareAddress()).substring(1);
            String dst = ((String)p.getTargetProtocolAddress()).substring(1);
            jTable.addRow(new String[]{number+++"",(System.currentTimeMillis()-START_TIME)+"",src,dst,"ARP",p.len+""
            ,Device.name,saa+"",taa+"","","","","","","","","","","","","","","","","","","","","","","","","",""
            });

        }
    }

}

