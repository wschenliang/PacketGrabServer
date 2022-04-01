package com.jiangnan.utils;

import com.jiangnan.enums.Protocol;
import com.jiangnan.thread.AThread;
import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;
import jpcap.packet.*;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 *  Jpcap工具类
 *
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class JpcapUtil {

    private static final String IPv4_REGEX = "\\d+\\.\\d+\\.\\d+\\.\\d+";
    private static int NET_INDEX = 0;

    //获取网络链接的网卡索引
    public static int findNetInterface() {
        NetworkInterface[] deviceList = JpcapCaptor.getDeviceList();
        int netIndex = 0;
        for (int i = 0; i < deviceList.length; i++) {
            if ("Microsoft".equals(deviceList[i].description) &&
                    "Ethernet".equals(deviceList[i].datalink_description)) {
                NetworkInterfaceAddress[] addresses = deviceList[i].addresses;
                if (addresses.length < 2) {
                    continue;
                }
                if (addresses[1].address.getHostAddress().matches(IPv4_REGEX)) {
                    netIndex = i;
                    break;
                }
            }
        }
        return netIndex;
    }

    //获取默认网卡捕获器
    public static JpcapCaptor getDefaultCaptor() {
        if (NET_INDEX == 0) {
            NET_INDEX = findNetInterface();
        }
        return getCaptor(JpcapCaptor.getDeviceList()[NET_INDEX]);
    }

    //获取默认网卡发送器
    public static JpcapSender getDefaultSender() {
        if (NET_INDEX == 0) {
            NET_INDEX = findNetInterface();
        }
        return getSender(JpcapCaptor.getDeviceList()[NET_INDEX]);
    }

    //获取指定网卡发送器
    public static JpcapSender getSender(NetworkInterface device) {
        JpcapSender sender = null;
        try {
            sender = JpcapSender.openDevice(device);
        } catch (IOException e) {
            LogUtils.log("打开网络发送失败！ \n" + e.getMessage());
        }
        return sender;
    }

    //获取指定网卡捕获器
    public static JpcapCaptor getCaptor(NetworkInterface device){
        JpcapCaptor captor = null;
        try{
            captor = JpcapCaptor.openDevice(device, 65535, false, 3000);

        } catch (IOException e) {
            LogUtils.log("打开网络接口失败！ \n" + e.getMessage());
        }
        return captor;
    }

    public static void asynCapDefaultPacket() {
        if (NET_INDEX == 0) {
            NET_INDEX = findNetInterface();
        }
        asynCapPacket(JpcapCaptor.getDeviceList()[NET_INDEX]);
    }

    public static void asynCapPacket(NetworkInterface device) {
        JpcapCaptor captor = getCaptor(device);
        AThread t = new AThread(captor);
        Thread capThread = new Thread(t);
        capThread.start();
    }

    //在默认网卡上发送数据包
    public static void sendDefaultPacket(Protocol protocol, String data, String src, String dst) {
        if (NET_INDEX == 0) {
            NET_INDEX = findNetInterface();
        }
        sendPacket(JpcapCaptor.getDeviceList()[NET_INDEX], protocol, data, src, dst);
    }

    //发送数据包
    public static void sendPacket(NetworkInterface device, Protocol protocol, String data, String src, String dst) {
        if (StringUtils.hasBlank(src, dst)) {
            LogUtils.log("请输入正确的源地址和目的地址");
            return;
        }
        String src_mac = "30:52:cb:f0:6f:f6";
        String dst_mac = "00:0c:29:3c:0a:f1";
        JpcapSender sender = getSender(device);
        try {
            Packet packet;
            switch (protocol) {
                case ARP:
                    packet = new ARPPacket().defaultPacket(data, src_mac, dst_mac, src, dst);
                    break;
                case TCP:
                    packet = new TCPPacket().defaultPacket(data, src_mac, dst_mac, src, dst);
                    break;
                case UDP:
                    packet = new UDPPacket().defaultPacket(data, src_mac, dst_mac, src, dst);
                    break;
                case ICMP:
                    packet = new ICMPPacket().defaultPacket(data, src_mac, dst_mac, src, dst);
                    break;
                case IP:
                default:
                    packet = new IPPacket().defaultPacket(data, src_mac, dst_mac, src, dst);
                    break;
            }
            sender.sendPacket(packet);
        }catch (UnknownHostException e){
            System.out.println(e.getMessage());
        } finally {
            sender.close();
        }

    }

    public static String[] getDeviceIp() {
        NetworkInterface[] deviceList = JpcapCaptor.getDeviceList();
        String[] deviceIP = new String[deviceList.length];
        for (int i = 0; i < deviceList.length; i++) {
            NetworkInterfaceAddress[] addresses = deviceList[i].addresses;
            String ipv4 = addresses[1].toString();
            deviceIP[i] = ipv4.substring(ipv4.indexOf("/") + 1);
        }
        return deviceIP;
    }

    public static String[] getDeviceName() {
        NetworkInterface[] deviceList = JpcapCaptor.getDeviceList();
        String[] deviceName = new String[deviceList.length];
        for (int i = 0; i < deviceList.length; i++) {
            deviceName[i] = deviceList[i].name;
        }
        return deviceName;
    }

    public static JpcapCaptor findCaptorByName(String name) {
        NetworkInterface device = findDeviceByName(name);
        return getCaptor(device);
    }

    public static NetworkInterface findDeviceByName(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        NetworkInterface device = null;
        for (NetworkInterface networkInterface : devices) {
            if (networkInterface.name.contains(name)) {
                device = networkInterface;
                break;
            }
        }
        return device;
    }

    public static NetworkInterface findDeviceByIP(String IP) {
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        NetworkInterface device = null;
        for(int i=0; i < devices.length; i++){
            NetworkInterfaceAddress[] addresses = devices[i].addresses;
            if (addresses[1].toString().contains(IP)){
                device = devices[i];
                break;
            }
        }
        return device;
    }

    public static JpcapCaptor findCaptorByIp(String IP) {
        NetworkInterface device = findDeviceByIP(IP);
        if (device == null) {
            return null;
        }
        return getCaptor(device);
    }
}
