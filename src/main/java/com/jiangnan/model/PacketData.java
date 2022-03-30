package com.jiangnan.model;

import com.jiangnan.enums.Protocol;
import java.io.Serializable;

/**
 *
 *  当前数据仅仅只是做展示用
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
        return new Object[]{num, sec,src,dest,protocol.getName(),length,jsonInfo};
    }

    private int num;
    //sec相减 + usec相减
    private String sec;
    private String src;
    private String dest;
    private Protocol protocol;
    private int length;
    private String jsonInfo;//将数据包所有信息放入

    public int getNum() {
        return num;
    }

    public PacketData setNum(int num) {
        this.num = num;
        return this;
    }

    public String getSec() {
        return sec;
    }

    public PacketData setSec(String sec) {
        this.sec = sec;
        return this;
    }

    public String getSrc() {
        return src;
    }

    public PacketData setSrc(String src) {
        if (src.startsWith("/")){
            this.src = src.substring(1);
        }else {
            this.src = src;
        }
        return this;
    }

    public String getDest() {
        return dest;
    }

    public PacketData setDest(String dest) {
        if (dest.startsWith("/")){
            this.dest = dest.substring(1);
        } else {
            this.dest = dest;
        }
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
}
