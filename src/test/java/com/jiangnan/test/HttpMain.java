package com.jiangnan.test;


import com.jiangnan.jpcap.JpcapCaptor;
import com.jiangnan.jpcap.NetworkInterface;
import com.jiangnan.jpcap.NetworkInterfaceAddress;
import com.jiangnan.jpcap.packet.EthernetPacket;
import com.jiangnan.jpcap.packet.Packet;
import com.jiangnan.jpcap.packet.TCPPacket;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * 当前仅能监听http而不能监听https，https请求下来是通过tsl加密后的数据，
 * 想要解析，需要使用代理安装受信任的证书进行解密，fillder和charles也是一样的原理。
 * @author Administrator
 *
 */
public class HttpMain {
	
	public static final char[] hexCode = "0123456789ABCDEF".toCharArray();
	public static String[] HttpInfoStr = {"0","0","0","0","0","0","0","0"};
	public static final String[] HTTPStart = {"GET","POST","OPTIONS"}; 	//HTTP协议有效信息开始的三个标志
	public static final String[] StrLabel = { "TimeOnLine", "SRC_MAC","DST_MAC", "SRC_IP", "DST_IP", "GETInfo", "RefererInfo", "HostInfo" };
	
    //显示所有网络设备信息
    private static void showDeviceList(NetworkInterface[] devices) {
        System.out.println("本机上所有适配器如下：");
        for (int i = 0; i < devices.length; i++) {
            //网络适配器名称
            System.out.println("Adapter " + (i + 1) + "：" + devices[i].description);
            //MAC地址
            System.out.print("    MAC address: ");
            for (byte b : devices[i].mac_address) {
                System.out.print(Integer.toHexString(b & 0xff) + ":");
            }
            System.out.println();
            //IP地址
            for (NetworkInterfaceAddress a : devices[i].addresses) {
                System.out.println("    IPv6/IPv4 address: " + a.address);
            }
            System.out.println();
        }
    }

    //网络接口监听
    private static JpcapCaptor openDevice(NetworkInterface[] devices, int choice) throws IOException{
        JpcapCaptor captor = null;
        try{
            captor = JpcapCaptor.openDevice(devices[choice], 65535, false, 3000);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("打开网络接口失败！");

        }
        return captor;
    }

    //数据包捕获线程
    private static class AThread implements Runnable{
        JpcapCaptor captor;
        Packet packet;

        AThread(JpcapCaptor captor) throws IOException{
            this.captor = captor;
        }

        @Override
        public void run() {
        	while(true) {
        		packet = captor.getPacket();
        		receivePacket(packet);
        	}
        }
    }
    

    public static void main(String[] args) throws IOException{
        //获取用户输入
        Scanner scanner = new Scanner(System.in);
        //初始化数据包捕获的线程
        AThread t = null;

        //获取网络设备并显示
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        showDeviceList(devices);

        //输入选择的监控的网卡
        System.out.print("输入选择监听的适配器序号:");
        int card = scanner.nextInt();
        card = card -1;
        System.out.println();

        //打开选择的网络接口
        JpcapCaptor captor = openDevice(devices, card);
        captor.setFilter("tcp", true);    //设置过滤规则，只抓取tcps数据包

        t = new AThread(captor);
        Thread capThread = new Thread(t);
        capThread.start();
    }
    public static void receivePacket(Packet packet) {			
		try {							
			String TimeOnLine = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
			HttpInfoStr[0] = TimeOnLine;//时间戳			
			TCPPacket tcpPacket = (TCPPacket) packet;
			EthernetPacket ethernetPacket = (EthernetPacket) packet.datalink;
			HttpInfoStr[1] = ethernetPacket.getSourceAddress();//SRC_MAC：
			HttpInfoStr[2] = ethernetPacket.getDestinationAddress();//DST_MAC
			HttpInfoStr[3] = tcpPacket.src_ip.toString().substring(1);//SRC_IP
			HttpInfoStr[4] = tcpPacket.dst_ip.toString().substring(1);//DST_IP					
			String HTTPData = new String(tcpPacket.data,"utf-8");
			//对TCP数据进行解析
			if(HTTPData.startsWith(HTTPStart[0]) || HTTPData.startsWith(HTTPStart[1]) || HTTPData.startsWith(HTTPStart[2])){
				System.out.println("-------------------------------------" );
	            System.out.println("时间戳:"+HttpInfoStr[0]);
	            System.out.println("SRC_MAC:"+HttpInfoStr[1]);
	            System.out.println("DST_MAC:"+HttpInfoStr[2]);
	            System.out.println("SRC_IP:"+HttpInfoStr[3]);
	            System.out.println("DST_IP:"+HttpInfoStr[4]);
	            System.out.println();
	            System.out.println("HTTPData:"+HTTPData);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public static String printHexBinary(byte[] bytes) {
    	char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexCode[v >>> 4];
            hexChars[j * 2 + 1] = hexCode[v & 0x0F];
        }
        return new String(hexChars);

    }
}
