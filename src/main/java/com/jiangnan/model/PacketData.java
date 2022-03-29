package com.jiangnan.model;

import com.jiangnan.enums.Protocol;

import java.io.Serializable;

/**
 *
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class PacketData implements Serializable {
    private static final long serialVersionUID = -3813876386951514859L;
    private int num;
    private long time;
    private String src;
    private String dest;
    private Protocol protocol;
    //数据包长度
    private int length;
    private String deviceName;
    //帧类型
    private short ethernetFrameType;
    private String macSrc;
    private String macDest;
    private String srcPort;
    private String destPort;
    //ip version
    private String ipVersion;
    //片偏移
    private int offset;
    // IP数据包长度
    private int ipLength;
    //生存时间 IP
    private short hopLimit;
    //DF标志位 IP
    private boolean dontFrag;
    //MF标志位 IP
    private boolean moreFrag;
    //RF标志位 IP
    private boolean rsvFrag;
    //服务类型 IP
    private byte rsvTos;
    //分组标识 IP
    private int ident;
    //UDP包长度
    private int udpLen;
    //ack_num TCP
    private long ackNum;
    //紧急指针
    private short urgentPointer;
    //窗口大小
    private int window;
    //序号
    private long sequence;
    //保留标志1
    private boolean rsv1;
    //保留标志2
    private boolean rsv2;
    //urg
    private boolean urg;
    //ack
    private boolean ack;
    //psh
    private boolean psh;
    //rst
    private boolean rst;
    //syn
    private boolean syn;
    //fin
    private boolean fin;
    //数据包二进制
    private byte[] dataBin;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public short getEthernetFrameType() {
        return ethernetFrameType;
    }

    public void setEthernetFrameType(short ethernetFrameType) {
        this.ethernetFrameType = ethernetFrameType;
    }

    public String getMacSrc() {
        return macSrc;
    }

    public void setMacSrc(String macSrc) {
        this.macSrc = macSrc;
    }

    public String getMacDest() {
        return macDest;
    }

    public void setMacDest(String macDest) {
        this.macDest = macDest;
    }

    public String getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(String srcPort) {
        this.srcPort = srcPort;
    }

    public String getDestPort() {
        return destPort;
    }

    public void setDestPort(String destPort) {
        this.destPort = destPort;
    }

    public String getIpVersion() {
        return ipVersion;
    }

    public void setIpVersion(String ipVersion) {
        this.ipVersion = ipVersion;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getIpLength() {
        return ipLength;
    }

    public void setIpLength(int ipLength) {
        this.ipLength = ipLength;
    }

    public short getHopLimit() {
        return hopLimit;
    }

    public void setHopLimit(short hopLimit) {
        this.hopLimit = hopLimit;
    }

    public boolean isDontFrag() {
        return dontFrag;
    }

    public void setDontFrag(boolean dontFrag) {
        this.dontFrag = dontFrag;
    }

    public boolean isMoreFrag() {
        return moreFrag;
    }

    public void setMoreFrag(boolean moreFrag) {
        this.moreFrag = moreFrag;
    }

    public boolean isRsvFrag() {
        return rsvFrag;
    }

    public void setRsvFrag(boolean rsvFrag) {
        this.rsvFrag = rsvFrag;
    }

    public byte getRsvTos() {
        return rsvTos;
    }

    public void setRsvTos(byte rsvTos) {
        this.rsvTos = rsvTos;
    }

    public int getIdent() {
        return ident;
    }

    public void setIdent(int ident) {
        this.ident = ident;
    }

    public int getUdpLen() {
        return udpLen;
    }

    public void setUdpLen(int udpLen) {
        this.udpLen = udpLen;
    }

    public long getAckNum() {
        return ackNum;
    }

    public void setAckNum(long ackNum) {
        this.ackNum = ackNum;
    }

    public short getUrgentPointer() {
        return urgentPointer;
    }

    public void setUrgentPointer(short urgentPointer) {
        this.urgentPointer = urgentPointer;
    }

    public int getWindow() {
        return window;
    }

    public void setWindow(int window) {
        this.window = window;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public boolean isRsv1() {
        return rsv1;
    }

    public void setRsv1(boolean rsv1) {
        this.rsv1 = rsv1;
    }

    public boolean isRsv2() {
        return rsv2;
    }

    public void setRsv2(boolean rsv2) {
        this.rsv2 = rsv2;
    }

    public boolean isUrg() {
        return urg;
    }

    public void setUrg(boolean urg) {
        this.urg = urg;
    }

    public boolean isAck() {
        return ack;
    }

    public void setAck(boolean ack) {
        this.ack = ack;
    }

    public boolean isPsh() {
        return psh;
    }

    public void setPsh(boolean psh) {
        this.psh = psh;
    }

    public boolean isRst() {
        return rst;
    }

    public void setRst(boolean rst) {
        this.rst = rst;
    }

    public boolean isSyn() {
        return syn;
    }

    public void setSyn(boolean syn) {
        this.syn = syn;
    }

    public boolean isFin() {
        return fin;
    }

    public void setFin(boolean fin) {
        this.fin = fin;
    }

    public byte[] getDataBin() {
        return dataBin;
    }

    public void setDataBin(byte[] dataBin) {
        this.dataBin = dataBin;
    }
}
