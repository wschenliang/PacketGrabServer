package com.jiangnan.enums;

/**
 *
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public enum Protocol {
    IP("IP"),
    TCP("TCP"),
    UDP("UDP"),
    ICMP("ICMP"),
    ARP("ARP"),
    ;

    private String name;

    Protocol(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
