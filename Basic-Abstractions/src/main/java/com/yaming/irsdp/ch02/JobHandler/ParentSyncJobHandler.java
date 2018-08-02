package com.yaming.irsdp.ch02.JobHandler;

import se.sics.kompics.*;
import se.sics.kompics.timer.Timer;
import se.sics.kompics.timer.java.JavaTimer;

public class ParentSyncJobHandler extends ComponentDefinition {
    Component jobRequester = create(JobRequester.class, Init.NONE);
    Component syncJobHandler = create(SyncJobHandler.class, Init.NONE);
    Component timer = create(JavaTimer.class, Init.NONE);

    {
        connect(jobRequester.getNegative(JobPort.class), syncJobHandler.getPositive(JobPort.class));
        connect(jobRequester.getNegative(Timer.class), timer.getPositive(Timer.class));
    }
}
