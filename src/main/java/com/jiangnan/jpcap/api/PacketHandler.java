package com.jiangnan.jpcap.api;

import com.jiangnan.jpcap.packet.Packet;

public interface PacketHandler {
    void handle(Packet packet);
}
