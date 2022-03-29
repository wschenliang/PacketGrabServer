package com.jiangnan.utils;

import com.jiangnan.model.CatchPacketContext;
import com.jiangnan.model.PacketData;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;

/**
 *  创建本地线程栈来保持流程产生数据
 *
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class ContextUtil {
    private static final ThreadLocal<CatchPacketContext> threadLocal = new ThreadLocal<>();
    private static final AutoRemove<CatchPacketContext> autoRemove = new AutoRemove<>(threadLocal);

    //启动本地线程栈，当启动网卡监听时候，就启动了本地线程栈
    public static AutoRemove<CatchPacketContext> start(NetworkInterface device) {
        CatchPacketContext catchPacketContext = new CatchPacketContext(device);
        //捕获器设置
        JpcapCaptor captor = JpcapUtil.getCaptor(device);
        catchPacketContext.setJpcapCaptor(captor);
        threadLocal.set(catchPacketContext);
        return autoRemove;
    }

    public static CatchPacketContext context() {
        return threadLocal.get();
    }

    public static JpcapCaptor getJpcapCaptor() {
        return context().getJpcapCaptor();
    }

    public static void addPacketData(PacketData packetData) {
        context().setPacketData(packetData);
    }

    //完成时，先把捕获器停了，再把本地线程栈清空
    public static void finish() {
        if (context() == null) {
            return;
        }
        context().getJpcapCaptor().breakLoop();
        threadLocal.remove();
    }

    public static long getStartTime() {
        return context().getStartTime();
    }

    public static String getDeviceName() {
        return context().getDevice().name;
    }

    public static void setStartTime(long startTime) {
        context().setStartTime(startTime);
    }
}
