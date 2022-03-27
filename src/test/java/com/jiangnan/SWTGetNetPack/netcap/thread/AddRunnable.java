package com.jiangnan.SWTGetNetPack.netcap.thread;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class AddRunnable implements Runnable {
	private DefaultTableModel tableMode;
	Vector< Object> vec = new Vector<Object>();
	
	public AddRunnable(DefaultTableModel tableMode) {
		super();
		this.tableMode = tableMode;
	}
int index = 0;
	@Override
	public void run() {
		while (index <= 30) {
			vec.add("asd-"+index);
			vec.add("asd1-"+index);
			vec.add("asd2-"+index);
			vec.add("asd3-"+index);
			vec.add("asd4-"+index);
			vec.add("asd12-"+index);
			vec.add("asd23-"+index);
			vec.add("asd123-"+index);
			vec.add("asd123-"+index);
			vec.add("asd1212-"+index);
			vec.add("asd121-"+index);
			vec.add("asd121-"+index);
			vec.add("asd122-"+index);
			tableMode.addRow(vec);
			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}

}
