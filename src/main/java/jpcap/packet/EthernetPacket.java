package jpcap.packet;

import java.nio.charset.StandardCharsets;

/**
 * 这个类代表以太网数据包。
 */
public class EthernetPacket extends DatalinkPacket {
    private static final long serialVersionUID = 817080364073605844L;

    /**
     * 目的MAC地址 (6byte)
     */
    public byte[] dst_mac;

    /**
     * 源MAC地址 (6byte)
     */
    public byte[] src_mac;

    /**
     * 框架类型
     */
    public short frametype;
    /**
     * PUP协议
     */
    public static final short ETHERTYPE_PUP = 0x0200;
    /**
     * IP协议
     */
    public static final short ETHERTYPE_IP = 0x0800;
    /**
     * Addr. resolution protocol
     */
    public static final short ETHERTYPE_ARP = 0x0806;
    /**
     * reverse Addr. resolution protocol
     */
    public static final short ETHERTYPE_REVARP = (short) 0x8035;
    /**
     * IEEE 802.1Q VLAN tagging
     */
    public static final short ETHERTYPE_VLAN = (short) 0x8100;
    /**
     * IPv6
     */
    public static final short ETHERTYPE_IPV6 = (short) 0x86dd;
    /**
     * 用来测试接口
     */
    public static final short ETHERTYPE_LOOPBACK = (short) 0x9000;

    void setValue(byte[] dst, byte[] src, short frame) {
        this.dst_mac = dst;
        this.src_mac = src;
        this.frametype = frame;
    }

    public EthernetPacket() {
    }

    public EthernetPacket(String src_mac, String dst_mac, short frametype) {
        this.src_mac = src_mac.getBytes();
        this.dst_mac = dst_mac.getBytes();
        this.frametype = frametype;
    }

    /**
     * @return 源MAC地址
     */
    public String getSourceAddress() {
        char[] src = new char[17];

        for (int i = 0; i < 5; i++) {
            src[i * 3] = hexUpperChar(src_mac[i]);
            src[i * 3 + 1] = hexLowerChar(src_mac[i]);
            src[i * 3 + 2] = ':';
        }
        src[15] = hexUpperChar(src_mac[5]);
        src[16] = hexLowerChar(src_mac[5]);

        return new String(src);
    }

    /**
     * 返回目的MAC地址。
     * @return 目的MAC地址。
     */
    public String getDestinationAddress() {
        char[] dst = new char[17];

        for (int i = 0; i < 5; i++) {
            dst[i * 3] = hexUpperChar(dst_mac[i]);
            dst[i * 3 + 1] = hexLowerChar(dst_mac[i]);
            dst[i * 3 + 2] = ':';
        }
        dst[15] = hexUpperChar(dst_mac[5]);
        dst[16] = hexLowerChar(dst_mac[5]);

        return new String(dst);
    }


    public String toString() {
        return super.toString() + " " + getSourceAddress() + "->" +
                getDestinationAddress() + " (" + frametype + ")";
    }

    private char hexUpperChar(byte b) {
        b = (byte) ((b >> 4) & 0xf);
        if (b == 0) return '0';
        else if (b < 10) return (char) ('0' + b);
        else return (char) ('a' + b - 10);
    }

    private char hexLowerChar(byte b) {
        b = (byte) (b & 0xf);
        if (b == 0) return '0';
        else if (b < 10) return (char) ('0' + b);
        else return (char) ('a' + b - 10);
    }
}
