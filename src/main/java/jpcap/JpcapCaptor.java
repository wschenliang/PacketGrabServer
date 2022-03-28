package jpcap;

import java.io.IOException;
import jpcap.packet.Packet;

/**
 *
 * 该类用于捕获包或从捕获的文件中读取包。
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class JpcapCaptor extends JpcapInstance {

    //接收报文数
    public int received_packets;
    //丢包数
    public int dropped_packets;

    private native String nativeOpenLive(String device, int snaplen, int promisc, int to_ms);

    private native String nativeOpenOffline(String filename);

    private native void nativeClose();

    private JpcapCaptor() throws IOException {
        if (this.reserveID() < 0) {
            throw new IOException("Unable to open a device: 255 devices are already opened.");
        }
    }

    /**
     * 返回可用于捕获的接口。
     * @return 接口对象列表
     */
    public static native NetworkInterface[] getDeviceList();

    /**
     * 打开指定的网络接口，并返回该类的实例。
     *
     * @param intrface 用于抓包的网络接口
     * @param snaplen 一次捕获的最大字节数
     * @param promisc 如果为真，推理将变成混杂模式
     * @param to_ms Timeout of 不是所有的平台都支持超时;在不允许超时的平台上，超时将被忽略。
     *              {@link #processPacket(int, PacketReceiver) processPacket()}.
     *              在支持超时的平台上，0值将导致Jpcap永远等待，以允许足够多的数据包到达，而没有超时。
     * @return Jpcap类的实例。
     * @throws IOException 当无法打开指定的接口时将被引发
     */
    public static JpcapCaptor openDevice(NetworkInterface intrface, int snaplen, boolean promisc, int to_ms) throws IOException {
        JpcapCaptor jpcap = new JpcapCaptor();
        String ret = jpcap.nativeOpenLive(intrface.name, snaplen, promisc ? 1 : 0, to_ms);
        if (ret != null) {
            throw new IOException(ret);
        } else {
            return jpcap;
        }
    }

    /**
     * 打开由tcpdump或Ethereal创建的转储文件，并返回该类的实例。
     * @param filename 转储文件的名称
     * @return Jpcap实例
     * @throws java.io.IOException 如果文件不能被打开
     */
    public static JpcapCaptor openFile(String filename) throws IOException {
        JpcapCaptor jpcap = new JpcapCaptor();
        String var2 = jpcap.nativeOpenOffline(filename);
        if (var2 != null) {
            throw new IOException(var2);
        } else {
            return jpcap;
        }
    }

    /**
     * 关闭打开的转储文件接口
     */
    public void close() {
        this.nativeClose();
        this.unreserveID();
    }

    /**
     * 捕获单个包。
     * 如果发生错误或超时，则为null <br>
     * Packet.EOF 当从脱机文件中读取只能获取EOF
     */
    public native Packet getPacket();

    /**
     * 连续捕获指定的包数.<br/>
     * <p/>
     * Unlike loopPacket(), this method returns (althrough not guaranteed)
     * 当超时超时时。此外，在“非阻塞”模式下，当没有数据包要捕获时，该方法立即返回。
     * @param count   需要捕获的包数 可以指定-1方式捕获数据包，直到出现timeour、EOF或错误。
     * @param handler JpcapHandler的一个实例，用于分析捕获的数据包
     * @return 抓包数
     */
    public native int processPacket(int count, PacketReceiver handler);

    /**
     * 连续捕获指定的包数。与processPacket()不同，此方法忽略超时。
     * 此方法也不支持“非阻塞”模式。
     * 每个线程启动loop方法，loop才是真正的无限循环方法，即使后面关闭线程也无效，线程做的只是开启loop，开启了就灭亡了，breakLoop可以关闭
     * @param count   需要捕获的包数 可以指定-1方式捕获数据包，直到出现timeour、EOF或错误。
     * @param handler JpcapHandler的一个实例，用于分析捕获的数据包
     * @return 抓包数
     */
    public native int loopPacket(int count, PacketReceiver handler);

    /** @deprecated */
    @Deprecated
    public int dispatchPacket(int var1, PacketReceiver var2) {
        return this.processPacket(var1, var2);
    }

    /**
     * 设置非阻塞模式
     *
     * @param nonblocking TRUE 非阻塞模式. FALSE 阻塞模式
     */
    public native void setNonBlockingMode(boolean nonblocking);

    /**
     * 检查当前是否设置为非阻塞模式
     *
     * @return TRUE 非阻塞
     */
    public native boolean isNonBlockinMode();

    public native void breakLoop();

    public native boolean setPacketReadTimeout(int var1);

    public native int getPacketReadTimeout();

    /**
     * 设置一个过滤器。此过滤器与tcpdump相同。
     *
     * @param condition 代表过滤器的字符串
     * @param optimize  如果为true，则对过滤器进行优化
     * @throws java.io.IOException 如果无法编译或安装筛选器条件，则会引发
     */
    public native void setFilter(String condition, boolean optimize) throws IOException;

    public native void updateStat();

    public native String getErrorMessage();

    /**
     * 获取使用相同接口发送报文的JpcapSender实例。只有在使用openDevice()方法打开接口时，才能使用此方法。
     *
     * @return 返回一个JpcapSender实例
     */
    public JpcapSender getJpcapSenderInstance() {
        return new JpcapSender(this.ID);
    }

    static {
        System.loadLibrary("jpcap");
    }
}
