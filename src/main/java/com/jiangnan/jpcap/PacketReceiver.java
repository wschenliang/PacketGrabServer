package com.jiangnan.jpcap;

import com.jiangnan.jpcap.packet.Packet;

/**
 * 该接口用于定义一个方法来分析捕获的数据包，该方法在JpcapCaptor.handlePacket()或JpcapCaptor.processPacket()中使用。
 *
 * @see JpcapCaptor#processPacket(int, PacketReceiver)
 * @see JpcapCaptor#loopPacket(int, PacketReceiver)
 */
public interface PacketReceiver {
    /**
     * 分析一个数据包，每次捕获包时都调用此方法。
     * @param p 待分析的报文
     */
    void receivePacket(Packet p);
}
