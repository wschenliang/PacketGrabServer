package jpcap;


import java.io.Serializable;
import java.util.Arrays;

/**
 * 这个类表示一个网络接口。
 */
public class NetworkInterface implements Serializable {
    private static final long serialVersionUID = 6871509045581846694L;
    /**
     * 网口名称
     */
    public String name;
    /**
     * 网口的描述信息 (e.g., "3Com ..."). 可能为null.
     */
    public String description;
    /**
     * 如果是环回接口，则为TRUE
     */
    public boolean loopback;
    /**
     * 网口数据链的名称
     */
    public String datalink_name;
    /**
     * 网口数据链的描述信息。可能是null。
     */
    public String datalink_description;
    /**
     * 网口的以太网MAC地址
     */
    public byte[] mac_address;
    /**
     * 为网络接口分配的网络地址。可能是空，如果它是一个非ip(e.g. NetBios)地址.
     */
    public NetworkInterfaceAddress[] addresses;

    public NetworkInterface(String name, String description, boolean loopback,
                            String datalink_name, String datalink_description, byte[] mac, NetworkInterfaceAddress[] addresses) {
        this.name = name;
        this.description = description;
        this.loopback = loopback;
        this.datalink_name = datalink_name;
        this.datalink_description = datalink_description;
        this.mac_address = mac;
        this.addresses = addresses;
    }

    /**
     * 获取物理地址，转化为16进制
     * @return 返回物理地址
     */
    public String getMacAddr() {
        if (mac_address == null || mac_address.length == 0) {
            return " ";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0, len = mac_address.length; i < len; i++) {
            builder.append(Integer.toHexString(mac_address[i] & 0xff));
            if (i != len - 1) {
                builder.append(":");
            }
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return "name:" + name +  "\n" +
                "description:" + description +  "\n" +
                "datalink_name:" + datalink_name +  "\n" +
                "datalink_desc:" + datalink_description +  "\n" +
                "mac_address:" + getMacAddr()  +'\n' +
                "addresses:" + Arrays.toString(addresses);
    }
}
