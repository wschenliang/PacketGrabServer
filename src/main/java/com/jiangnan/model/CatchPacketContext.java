package com.jiangnan.model;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;


/**
 *  抓包上下文数据，用于整个流程产生数据
 *
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class CatchPacketContext {

    //网络接口
    private NetworkInterface device;

    private JpcapCaptor jpcapCaptor;

    //开始时间
    private long startTime;

    private PacketData packetData;

    public CatchPacketContext(NetworkInterface device) {
        this.device = device;
    }

    public NetworkInterface getDevice() {
        return device;
    }

    public void setDevice(NetworkInterface device) {
        this.device = device;
    }

    public JpcapCaptor getJpcapCaptor() {
        return jpcapCaptor;
    }

    public void setJpcapCaptor(JpcapCaptor jpcapCaptor) {
        this.jpcapCaptor = jpcapCaptor;
    }

    public PacketData getPacketData() {
        return packetData;
    }

    public void setPacketData(PacketData packetData) {
        this.packetData = packetData;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
