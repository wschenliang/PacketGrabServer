package com.jiangnan.utils;

import com.jiangnan.jpcap.JpcapCaptor;
import com.jiangnan.jpcap.NetworkInterface;
import com.jiangnan.jpcap.NetworkInterfaceAddress;

import java.io.IOException;

/**
 *
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class DeviceUtil {

    //获取所有的设备列表
    public static NetworkInterface[] getDeviceList() {
        return JpcapCaptor.getDeviceList();
    }

    //显示所有网络设备信息
    public static void printDevices() {
        NetworkInterface[] devices = getDeviceList();
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
    public static JpcapCaptor openDevice(NetworkInterface[] devices, int choice){
        JpcapCaptor captor = null;
        try{
            captor = JpcapCaptor.openDevice(devices[choice], 65535, false, 3000);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("打开网络接口失败！");

        }
        return captor;
    }

    public static void main(String[] args) {
        printDevices();
    }



}
