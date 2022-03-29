package com.jiangnan.model;

import com.jiangnan.enums.Protocol;
import com.jiangnan.utils.FastjsonUtils;
import jpcap.packet.*;

import java.io.Serializable;

/**
 *
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class PacketData implements Serializable {
    private static final long serialVersionUID = -3813876386951514859L;
    //返回的是表格的第一行
    public static String[] getTitle() {
        return new String[]{"No.","Time","Source","Destination","Protocol","Length", "info"};
    }
    //获取数据数组，与上面的title一一对应
    public Object[] getDataArrays() {
        return new Object[]{num,time,src,dest,protocol.getName(),length,jsonInfo};
    }

    private void tcpPacketHandle(TCPPacket p) {
        setSrc(p.src_ip.toString()).setDest(p.dst_ip.toString()).setProtocol(Protocol.TCP).setLength(p.len);
        String jsonInfo = FastjsonUtils.toJSONString(p);
        setJsonInfo(jsonInfo);
    }
    private void arpPacketHandle(ARPPacket p) {
        setProtocol(Protocol.ARP);
        String jsonInfo = FastjsonUtils.toJSONString(p);
        setJsonInfo(jsonInfo);
        Object senderProtocolAddress = p.getSenderProtocolAddress();
        Object targetProtocolAddress = p.getTargetProtocolAddress();
        Object senderHardwareAddress = p.getSenderHardwareAddress();
        Object targetHardwareAddress = p.getTargetHardwareAddress();
        setSrc(senderProtocolAddress.toString());
        setDest(targetProtocolAddress.toString());
    }

    private void udpPacketHandle(UDPPacket p) {
        setSrc(p.src_ip.toString()).setDest(p.dst_ip.toString()).setProtocol(Protocol.UDP).setLength(p.len);
        String jsonInfo = FastjsonUtils.toJSONString(p);
        setJsonInfo(jsonInfo);
    }

    private int num;
    private long time;
    private String src;
    private String dest;
    private Protocol protocol;
    private int length;
    //将数据包所有信息放入
    private String jsonInfo;
    private Packet packet;

    /**
     * 创建packetData对象必须要通过传回来的packet数据
     * @param p 返回来的数据包
     */
    public PacketData(Packet p) {
        setPacket(p);
        if (p instanceof TCPPacket) {
            tcpPacketHandle((TCPPacket) p);
        } else if (p instanceof UDPPacket) {
            udpPacketHandle((UDPPacket) p);
        } else if (p instanceof ARPPacket) {
            arpPacketHandle((ARPPacket) p);
        }
    }

    public int getNum() {
        return num;
    }

    public PacketData setNum(int num) {
        this.num = num;
        return this;
    }

    public long getTime() {
        return time;
    }

    public PacketData setTime(long time) {
        this.time = time;
        return this;
    }

    public String getSrc() {
        return src;
    }

    public PacketData setSrc(String src) {
        this.src = src;
        return this;
    }

    public String getDest() {
        return dest;
    }

    public PacketData setDest(String dest) {
        this.dest = dest;
        return this;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public PacketData setProtocol(Protocol protocol) {
        this.protocol = protocol;
        return this;
    }

    public int getLength() {
        return length;
    }

    public PacketData setLength(int length) {
        this.length = length;
        return this;
    }

    public String getJsonInfo() {
        return jsonInfo;
    }

    public PacketData setJsonInfo(String jsonInfo) {
        this.jsonInfo = jsonInfo;
        return this;
    }

    public Packet getPacket() {
        return packet;
    }

    public PacketData setPacket(Packet packet) {
        this.packet = packet;
        return this;
    }
}
