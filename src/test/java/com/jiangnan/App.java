package com.jiangnan;

import com.jiangnan.enums.Protocol;
import jpcap.JpcapCaptor;
import jpcap.packet.Packet;
import com.jiangnan.thread.AThread;
import com.jiangnan.utils.JpcapUtil;
import com.jiangnan.utils.PacketUtil;


import java.io.IOException;
import java.util.Scanner;

public class App {

    //定义发送的报文的源地址
    private static final String src = "10.132.29.197";
    //定义发送的报文的目的地址
    private static final String dst = "192.168.253.134";

    public static void main(String[] args) throws IOException {
        //获取用户输入
        Scanner scanner = new Scanner(System.in);
        Packet[] packet = PacketUtil.getNewPacket();
        AThread t = null;
        JpcapCaptor captor = JpcapUtil.getDefaultCaptor();
        menu:
        while(true) {
            //功能菜单
            System.out.println("请选择使用的功能编号：\n 1. 捕获当前网卡的数据包 \n 2. 停止捕获网络数据包 \n " +
                    "3. 导入本地的网络数据包 \n 4. 显示当前捕获的数据包 \n 5. 保存当前的网络数据包 \n " +
                    "6. 分析数据包的协议分布 \n 7. 查看数据包详细信息 \n 8. 发送数据包给目标主机 \n 9. 退出\n ");
            //用户选择
            int choice = scanner.nextInt();

            //功能执行
            switch (choice){
                case 1: System.out.println("正在捕获数据包...");
                    t = new AThread(captor);
                    Thread capThread = new Thread(t);
                    capThread.start();
                    break;
                case 2: System.out.println("已停止捕获数据包");
                    t.cancel();
                    break;
                case 3: packet = PacketUtil.readPacket(captor, "./savePacket");
                    System.out.println("已导入本地数据包");
                    break;
                case 4: System.out.println("显示当前捕获的数据包如下：");
                    if(t == null){
                        System.out.println("数据包捕获功能未开启");
                        break;
                    }
                    packet = t.getPacket();
                    PacketUtil.showPacket(packet);
                    break;
                case 5: PacketUtil.savePacket(captor, packet);
                    System.out.println("已保存数据包到默认位置");
                    break;
                case 6: System.out.println("数据包的协议分布如下：");
                    PacketUtil.analyzePacket(packet);
                    break;
                case 7: System.out.println("数据包详细信息如下：");
                    PacketUtil.showPacketDetail(packet);
                    break;
                case 8: System.out.print("请选择发送的协议类型(IP、TCP、UDP、ICMP、ARP): ");
                    String type = scanner.next().toUpperCase();
                    String data = "";
                    JpcapUtil.sendDefaultPacket(Protocol.valueOf(type), data, src, dst);
                    break;
                case 9: break menu;
            }
            System.out.println();
        }

        //关闭
        captor.close();

    }

}
