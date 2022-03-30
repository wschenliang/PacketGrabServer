package com.jiangnan.filter;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class PcapFileFilter extends FileFilter {
    @Override
    public boolean accept(File f) {
        String name = f.getName();
        return name.toLowerCase().endsWith("pcap");
    }

    @Override
    public String getDescription() {
        return "*.pcap";
    }
}
