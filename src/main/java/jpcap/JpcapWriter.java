package jpcap;

import jpcap.packet.Packet;

import java.io.IOException;

/**
 * 该类用于将捕获的包保存到文件中。
 */
public class JpcapWriter {
    private native String nativeOpenDumpFile(String filename, int ID);

    private JpcapWriter(JpcapCaptor jpcap, String filename)
            throws java.io.IOException {
        String ret = nativeOpenDumpFile(filename, jpcap.ID);

        if (ret != null) { //error
            throw new java.io.IOException(ret);
        }
    }

    /**
     * 打开文件保存捕获的数据包。
     *
     * @param jpcap    用于捕获(加载)数据包的JpcapCaptor实例
     * @param filename filename
     * @throws IOException 如果文件不能打开
     */
    public static JpcapWriter openDumpFile(JpcapCaptor jpcap, String filename) throws IOException {
        return new JpcapWriter(jpcap, filename);
    }

    /**
     * 关闭打开文件
     */
    public native void close();

    /**
     * 将报文保存到文件中。
     */
    public native void writePacket(Packet packet);

    static {
        System.loadLibrary("jpcap");
    }
}
