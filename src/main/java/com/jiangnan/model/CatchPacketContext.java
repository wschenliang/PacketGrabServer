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

    //捕获器数据
    private JpcapCaptor jpcapCaptor;

    //线程2开始时间数据
    private long startTime;

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


    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

}
