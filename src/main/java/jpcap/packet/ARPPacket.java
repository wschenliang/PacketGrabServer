package jpcap.packet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/**
 * 这个类代表ARP/RARP报文。
 */
public class ARPPacket extends Packet {
    private static final long serialVersionUID = 3271911802471786372L;

    /**
     * 硬件类型
     */
    public short hardtype;
    /**
     * 硬件类型:以太网
     */
    public static final short HARDTYPE_ETHER = 1;
    /**
     * 硬件类型: Token ring
     */
    public static final short HARDTYPE_IEEE802 = 6;
    /**
     * 硬件类型: Frame relay
     */
    public static final short HARDTYPE_FRAMERELAY = 15;

    /**
     * 协议类型
     */
    public short prototype;
    /**
     * IP协议
     */
    public static final short PROTOTYPE_IP = 2048;

    /**
     * 硬件地址长度
     */
    public short hlen;

    /**
     * 协议地址长度
     */
    public short plen;

    /**
     * 操作
     */
    public short operation;
    /**
     * ARP请求
     */
    public static final short ARP_REQUEST = 1;
    /**
     * ARP应答
     */
    public static final short ARP_REPLY = 2;
    /**
     * 反向ARP请求
     */
    public static final short RARP_REQUEST = 3;
    /**
     * 反向ARP应答
     */
    public static final short RARP_REPLY = 4;
    /**
     * 识别peer请求
     */
    public static final short INV_REQUEST = 8;
    /**
     * 识别peer回复
     */
    public static final short INV_REPLY = 9;


    /**
     * Sender hardware address
     */
    public byte[] sender_hardaddr;
    /**
     * Sender protocol address
     */
    public byte[] sender_protoaddr;
    /**
     * Target hardware address
     */
    public byte[] target_hardaddr;
    /**
     * Target protocol address
     */
    public byte[] target_protoaddr;

    void setValue(short hardtype, short prototype, short hlen, short plen,
                  short operation, byte[] sha, byte[] spa, byte[] tha, byte[] tpa) {
        this.hardtype = hardtype;
        this.prototype = prototype;
        this.hlen = hlen;
        this.plen = plen;
        this.operation = operation;
        sender_hardaddr = sha;
        sender_protoaddr = spa;
        target_hardaddr = tha;
        target_protoaddr = tpa;
    }

    /**
     * Returns the hardware address (MAC address) of the sender.
     *
     * @return Hardware address of the sender
     */
    public Object getSenderHardwareAddress() {
        switch (hardtype) {
            case HARDTYPE_ETHER:
                char[] adr = new char[17];

                for (int i = 0; i < 5; i++) {
                    adr[i * 3] = hexUpperChar(sender_hardaddr[i]);
                    adr[i * 3 + 1] = hexLowerChar(sender_hardaddr[i]);
                    adr[i * 3 + 2] = ':';
                }
                adr[15] = hexUpperChar(sender_hardaddr[5]);
                adr[16] = hexLowerChar(sender_hardaddr[5]);

                return new String(adr);
            default:
                return "Unknown Protocol";
        }
    }

    /**
     * Returns the hardware address (MAC address) of the target.
     *
     * @return Hardware address of the target
     */
    public Object getTargetHardwareAddress() {
        switch (hardtype) {
            case HARDTYPE_ETHER:
                char[] adr = new char[17];

                for (int i = 0; i < 5; i++) {
                    adr[i * 3] = hexUpperChar(target_hardaddr[i]);
                    adr[i * 3 + 1] = hexLowerChar(target_hardaddr[i]);
                    adr[i * 3 + 2] = ':';
                }
                adr[15] = hexUpperChar(target_hardaddr[5]);
                adr[16] = hexLowerChar(target_hardaddr[5]);

                return new String(adr);
            default:
                return "Unknown Protocol";
        }
    }

    /**
     * Returns the protocol address of the sender.
     *
     * @return Protocol address of the sender
     */
    public Object getSenderProtocolAddress() {
        switch (prototype) {
            case PROTOTYPE_IP:
                try {
                    return InetAddress.getByAddress(sender_protoaddr);
                } catch (UnknownHostException e) {
                    return "Unknown Address";
                }
            default:
                return "Unknown Protocol";
        }
    }

    /**
     * Returns the protocol address of the target.
     *
     * @return Protocol address of the target
     */
    public Object getTargetProtocolAddress() {
        switch (prototype) {
            case PROTOTYPE_IP:
                try {
                    return InetAddress.getByAddress(target_protoaddr);
                } catch (UnknownHostException e) {
                    return "Unknown Address";
                }
            default:
                return "Unknown Protocol";
        }
    }

    /**
     * Returns a string representation of this ARP/RARP packet.<BR>
     * <p/>
     * <BR>
     * Format: ARP(hardtype:prototype)
     *
     * @return a string representation of this ARP/RARP packet
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();

        switch (operation) {
            case ARP_REQUEST:
                buf.append("ARP REQUEST ");
                break;
            case ARP_REPLY:
                buf.append("ARP REPLY ");
                break;
            case RARP_REQUEST:
                buf.append("RARP REQUEST ");
                break;
            case RARP_REPLY:
                buf.append("RARP REPLY ");
                break;
            case INV_REQUEST:
                buf.append("IDENTIFY REQUEST ");
                break;
            case INV_REPLY:
                buf.append("IDENTIFY REPLY ");
                break;
            default:
                buf.append("UNKNOWN ");
                break;
        }

        return buf.toString() + getSenderHardwareAddress() + "(" + getSenderProtocolAddress() + ") -> " +
                getTargetHardwareAddress() + "(" + getTargetProtocolAddress() + ")";
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

    @Override
    public Packet defaultPacket(String data, String src_mac, String dst_mac, String src, String dst) throws UnknownHostException {
        InetAddress srcAddress = InetAddress.getByName(src);
        InetAddress dstAddress = InetAddress.getByName(dst);
        //构造ARP报文
        ARPPacket arpPacket = new ARPPacket();
        arpPacket.hardtype = ARPPacket.HARDTYPE_ETHER;//硬件类型
        arpPacket.prototype = ARPPacket.PROTOTYPE_IP;//协议类型
        arpPacket.operation = ARPPacket.ARP_REQUEST;//指明为ARP请求报文(另一种为回复报文)
        arpPacket.hlen = 6;//物理地址长度
        arpPacket.plen = 4;//协议地址长度
        arpPacket.sender_hardaddr = src_mac.getBytes();//发送端为本机mac地址
        arpPacket.sender_protoaddr = srcAddress.getAddress();//本机IP地址
        arpPacket.target_hardaddr = dst_mac.getBytes(); //目的端mac地址为广播地址
        arpPacket.target_protoaddr = dstAddress.getAddress();//目的IP地址
        arpPacket.datalink = new EthernetPacket(src_mac, dst_mac, EthernetPacket.ETHERTYPE_ARP);
        arpPacket.data = data.getBytes();
        return arpPacket;
    }
}
