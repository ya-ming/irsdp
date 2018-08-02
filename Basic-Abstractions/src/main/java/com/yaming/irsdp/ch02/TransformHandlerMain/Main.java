package com.yaming.irsdp.ch02.TransformHandlerMain;

import com.yaming.irsdp.ch02.TransformHandler.ParentTransformationHandler;
import se.sics.kompics.Kompics;

public class Main {

	public static void main(String[] args) {
		Kompics.createAndStart(ParentTransformationHandler.class, 1);
		try {
			Thread.sleep(8000);
		} catch (InterruptedException ex) {
			System.exit(1);
		}
		Kompics.shutdown();
		System.exit(0);
	}
}