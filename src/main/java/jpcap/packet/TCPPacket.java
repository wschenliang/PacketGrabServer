package jpcap.packet;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 这个类代表TCP数据包。
 */
public class TCPPacket extends IPPacket {
    private static final long serialVersionUID = -8856988406589484129L;

    /**
     * 源端口号
     */
    public int src_port;
    /**
     * 目的端口号
     */
    public int dst_port;
    /**
     * 序列号
     */
    public long sequence;
    /**
     * ACK number
     */
    public long ack_num;
    /**
     * URG flag
     */
    public boolean urg;
    /**
     * ACK flag
     */
    public boolean ack;
    /**
     * PSH flag
     */
    public boolean psh;
    /**
     * RST flag
     */
    public boolean rst;
    /**
     * SYN flag
     */
    public boolean syn;
    /**
     * FIN flag
     */
    public boolean fin;

    // added by Damien Daspit 5/7/01
    /**
     * RSV1 flag
     */
    public boolean rsv1;
    /**
     * RSV2 flag
     */
    public boolean rsv2;
    // *****************************

    /**
     * Window size
     */
    public int window;
    /**
     * Urgent pointer
     */
    public short urgent_pointer;

    /**
     * TCP option
     */
    public byte[] option;

    /**
     * Creates a TCP packet.
     *
     * @param rsv1     RSV1 flag
     * @param rsv2     RSV2 flag
     * @param src_port Source port number
     * @param dst_port Destination port number
     * @param sequence sequence number
     * @param ack_num  ACK number
     * @param urg      URG flag
     * @param ack      ACK flag
     * @param psh      PSH flag
     * @param rst      RST flag
     * @param syn      SYN flag
     * @param fin      FIN flag
     * @param window   window size
     * @param urgent   urgent pointer
     */
    public TCPPacket(int src_port, int dst_port, long sequence, long ack_num,
                     boolean urg, boolean ack, boolean psh, boolean rst,
                     boolean syn, boolean fin, boolean rsv1, boolean rsv2,
                     int window, int urgent) {
        this.src_port = src_port;
        this.dst_port = dst_port;
        this.sequence = sequence;
        this.ack_num = ack_num;
        this.urg = urg;
        this.ack = ack;
        this.psh = psh;
        this.rst = rst;
        this.syn = syn;
        this.fin = fin;
        // added by Damien Daspit 5/7/01
        this.rsv1 = rsv1;
        this.rsv2 = rsv2;
        // *****************************
        this.window = window;
        urgent_pointer = (short) urgent;
    }

    public TCPPacket() {
    }

    void setValue(int src, int dst, long seq, long ack_num, boolean urg, boolean ack,
                  boolean psh, boolean rst, boolean syn, boolean fin, boolean rsv1, boolean rsv2,
                  int win, short urp) {
        src_port = src;
        dst_port = dst;
        sequence = seq;
        this.ack_num = ack_num;
        this.urg = urg;
        this.ack = ack;
        this.psh = psh;
        this.rst = rst;
        this.syn = syn;
        this.fin = fin;
        // added by Damien Daspit 5/7/01
        this.rsv1 = rsv1;
        this.rsv2 = rsv2;
        // *****************************
        window = win;
        urgent_pointer = urp;
    }

    void setOption(byte[] option) {
        this.option = option;
    }

    public String toString() {
        return super.toString() + " TCP " +
                src_port + " > " + dst_port + " seq(" + sequence +
                ") win(" + window + ")" + (ack ? " ack " + ack_num : "") + " " +
                (syn ? " S" : "") + (fin ? " F" : "") + (psh ? " P" : "") +
                (rst ? " R" : "") + (urg ? " U" : "");
    }


    @Override
    public Packet defaultPacket(String data, String src_mac, String dst_mac, String src, String dst) throws UnknownHostException {
        //构造TCP报文
        TCPPacket tcpPacket = new TCPPacket(12, 34, 56, 78, false, false,
                false, false, true, true, true, true, 10, 0);
        //设置IP头
        tcpPacket.setIPv4Parameter(0,false,false,false,0,false,false,
                false,0,65,128,IPPacket.IPPROTO_TCP,InetAddress.getByName(dst), InetAddress.getByName(dst));

        //构造ether帧（frame）
        tcpPacket.datalink = new EthernetPacket(src_mac, dst_mac, EthernetPacket.ETHERTYPE_IP);
        tcpPacket.data = data.getBytes();
        return tcpPacket;
    }
}
