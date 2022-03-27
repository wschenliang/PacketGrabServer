package jpcap.packet;

import jpcap.JpcapCaptor;

import java.io.Serializable;

/**
 * 这是被捕获的所有数据包的一个根类 {@link JpcapCaptor Jpcap}.
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class Packet implements Serializable {

    private static final long serialVersionUID = 6341660141824194723L;
    /**
     * 获取时间戳 (sec)
     */
    public long sec;

    /**
     * 获取时间戳 (micro sec)
     */
    public long usec;

    /**
     * 捕获的长度
     */
    public int caplen;

    /**
     * 数据包的长度
     */
    public int len;

    /**
     * 数据链接层头部
     */
    public DatalinkPacket datalink;

    /**
     * 数据头部
     */
    public byte[] header;

    /**
     * 数据包包含头部
     */
    public byte[] data;

    /**
     * 由JpcapCaptor.getPacket()返回
     * 当读取脱机文件时到达EOF。
     */
    public static final Packet EOF = new Packet();

    void setPacketValue(long sec, long usec, int caplen, int len) {
        this.sec = sec;
        this.usec = usec;
        this.caplen = caplen;
        this.len = len;
    }

    void setDatalinkPacket(DatalinkPacket p) {
        datalink = p;
    }

    void setPacketData(byte[] data) {
        this.data = data;
    }

    void setPacketHeader(byte[] header) {
        this.header = header;
    }

    /**
     * 返回此包的字符串表示形式<BR>
     * Format: sec:usec
     *
     * @return 此包的字符串表示形式
     */
    public String toString() {
        return sec + ":" + usec;
    }
}