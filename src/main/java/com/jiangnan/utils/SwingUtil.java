package com.jiangnan.utils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 *
 *  swing组件工具类
 *
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class SwingUtil {

    /**
     * 设置表格的某一行的背景色
     * @param table
     */
    public static void setOneRowBackgroundColor(JTable table, int rowIndex,
                                                Color color) {
        try {
            DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {

                public Component getTableCellRendererComponent(JTable table,
                                                               Object value, boolean isSelected, boolean hasFocus,
                                                               int row, int column) {
                    if (row == rowIndex) {
                        setBackground(color);
                        setForeground(Color.WHITE);
                    }
                    else if(row > rowIndex){
                        setBackground(Color.WHITE);
                        setForeground(Color.BLACK);
                    }else{
                        setBackground(Color.WHITE);
                        setForeground(Color.BLACK);
                    }

                    return super.getTableCellRendererComponent(table, value,
                            isSelected, hasFocus, row, column);
                }
            };
            int columnCount = table.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                table.getColumn(table.getColumnName(i)).setCellRenderer(tcr);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static void setTableColor(JTable jTable) {
        try {
            DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {

                public Component getTableCellRendererComponent(JTable table,
                                                               Object value, boolean isSelected, boolean hasFocus,
                                                               int row, int column) {

                    DefaultTableModel model = (DefaultTableModel) jTable.getModel();
                    int rowCount = model.getRowCount();
                    for (int i = 0; i < rowCount; i++) {
                        if (model.getValueAt(i, 4).equals("UDP")) {
                            setBackground(Color.CYAN);
                            setForeground(Color.WHITE);
                        } else if (model.getValueAt(i, 4).equals("ARP")){
                            setBackground(Color.YELLOW);
                            setForeground(Color.WHITE);
                        } else {
                            setBackground(Color.WHITE);
                            setForeground(Color.BLACK);
                        }
                    }

                    return super.getTableCellRendererComponent(table, value,
                            isSelected, hasFocus, row, column);
                }
            };
            int columnCount = jTable.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                jTable.getColumn(jTable.getColumnName(i)).setCellRenderer(tcr);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
