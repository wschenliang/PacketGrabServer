package jpcap.packet;

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

    public String toString() {
        return super.toString() + " UDP " + src_port + " > " + dst_port;
    }
}
