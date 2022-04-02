
## 软件功能

- 捕获网卡数据包
- 生产pcap文件
- 分析数据包

## 环境安装

1. windows安装环境：
    
    - windows环境下安装winpcap
  
    - 将jpcap.dll放入jdk得bin目录下，放入时请区分好jdk是32位还还是64位

2. liunx安装环境：

    - 下载依赖

    ```shell
    sudo apt-get install gcc flex bison
    ```

    - liunx安装libpcap

    解压
    ```shell
    tar -zxvf libpcap-1.10.1.tar.gz
    ```

    - 进入libpcap里进行编译
    ```shell
    cd libpcap-1.10.1
    ./configure
    make
    sudo make install
    ```

    - 下载jpcap源码
    
    ```shell
    git clone http
    ```
    进入c文件夹下变异
    ```shell
    cd jpcap/src/c
    make
    ```
    产生一个libjpcap.so文件，将文件复制到jdk/jre/lib/amd64下面