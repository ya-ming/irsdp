package com.yaming.irsdp.ch02.JobHandlerMain;

import com.yaming.irsdp.ch02.JobHandler.ParentAsyncJobHandler;
import se.sics.kompics.Kompics;

public class MainAsync {

    public static void main(String[] args) {
        Kompics.createAndStart(ParentAsyncJobHandler.class, 1);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            System.exit(1);
        }
        Kompics.shutdown();
        System.exit(0);
    }
}
