package jpcap.packet;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 这个类代表UDP报文。
 */
public class UDPPacket extends IPPacket {
    private static final long serialVersionUID = -3170544240823207254L;

    /**
     * 源端口号
     */
    public int src_port;
    /**
     * 目的端口号
     */
    public int dst_port;
    /**
     * 包长度
     */
    public int length;

    public UDPPacket() {
    }

    /**
     * 创建UDP报文。
     *
     * @param src_port 源端口号
     * @param dst_port 目的端口号
     */
    public UDPPacket(int src_port, int dst_port) {
        this.src_port = src_port;
        this.dst_port = dst_port;
    }

    void setValue(int src, int dst, int len) {
        src_port = src;
        dst_port = dst;
        length = len;
    }

    @Override
    public Packet defaultPacket(String data, String src_mac, String dst_mac, String src, String dst) throws UnknownHostException {
        InetAddress srcAddress = InetAddress.getByName(src);
        InetAddress dstAddress = InetAddress.getByName(dst);
        //构造UDP报文
        UDPPacket udpPacket = new UDPPacket(12, 34);
        udpPacket.src_ip = srcAddress;
        udpPacket.dst_ip = dstAddress;
        //设置IP头
        udpPacket.setIPv4Parameter(0,false,false,false,0,false,false,
                false,0,65,128,IPPacket.IPPROTO_UDP,srcAddress,
                dstAddress);
        udpPacket.datalink = new EthernetPacket(src_mac, dst_mac, EthernetPacket.ETHERTYPE_IP);
        udpPacket.data = data.getBytes();
        return udpPacket;
    }

    public String toString() {
        return super.toString() + " UDP " + src_port + " > " + dst_port;
    }
}
