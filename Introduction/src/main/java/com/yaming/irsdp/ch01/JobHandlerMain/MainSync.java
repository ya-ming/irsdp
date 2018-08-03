package com.yaming.irsdp.ch01.JobHandlerMain;

import com.yaming.irsdp.ch01.JobHandler.ParentSyncJobHandler;
import se.sics.kompics.Kompics;

public class MainSync {

	public static void main(String[] args) {
		Kompics.createAndStart(ParentSyncJobHandler.class, 1);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException ex) {
			System.exit(1);
		}
		Kompics.shutdown();
		System.exit(0);
	}
}