package com.yaming.irsdp.ch01.JobHandler;

import se.sics.kompics.*;
import se.sics.kompics.timer.Timer;
import se.sics.kompics.timer.java.JavaTimer;

public class ParentAsyncJobHandler extends ComponentDefinition {
    Component jobRequester = create(JobRequester.class, Init.NONE);
    Component asyncJobHandler = create(AsyncJobHandler.class, Init.NONE);
    Component timer = create(JavaTimer.class, Init.NONE);

    {
        connect(jobRequester.getNegative(JobPort.class), asyncJobHandler.getPositive(JobPort.class));
        connect(jobRequester.getNegative(Timer.class), timer.getPositive(Timer.class));
        connect(asyncJobHandler.getNegative(Timer.class), timer.getPositive(Timer.class));
    }
}
