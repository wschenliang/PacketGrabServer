package com.jiangnan.jpcap;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 该类表示分配给网络接口的网络地址。
 */
public class NetworkInterfaceAddress {
    /**
     * 网络接口地址
     */
    public InetAddress address;
    /**
     * 网口的子网掩码
     */
    public InetAddress subnet;
    /**
     * 网络接口的广播地址。可能是null。
     */
    public InetAddress broadcast;
    /**
     * 网络接口的目的地址(用于P2P连接)。可能是null。
     */
    public InetAddress destination;

    public NetworkInterfaceAddress(byte[] address, byte[] subnet, byte[] broadcast, byte[] destination) {
        try {
            if (address != null)
                this.address = InetAddress.getByAddress(address);
            if (subnet != null)
                this.subnet = InetAddress.getByAddress(subnet);
            if (broadcast != null)
                this.broadcast = InetAddress.getByAddress(broadcast);
            if (destination != null)
                this.destination = InetAddress.getByAddress(destination);
        } catch (UnknownHostException e) {
        }
    }

    @Override
    public String toString() {
        return "NetworkInterfaceAddress{" +
                "address=" + address +
                ", subnet=" + subnet +
                ", broadcast=" + broadcast +
                ", destination=" + destination +
                '}';
    }
}