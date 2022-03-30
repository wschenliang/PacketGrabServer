package com.jiangnan.model;

import jpcap.packet.Packet;

import java.io.Serializable;
import java.util.LinkedList;

/**
 *
 * 每次接收一个包都放入队列中
 *
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class PacketQueue implements Serializable {
    private static final long serialVersionUID = 7057534927419848726L;

    private final LinkedList<Packet> packets;

    public PacketQueue() {
        packets = new LinkedList<>();
    }

    public void addPacket(Packet p) {
        packets.add(p);
    }

    public LinkedList<Packet> getPackets() {
        return packets;
    }
}
