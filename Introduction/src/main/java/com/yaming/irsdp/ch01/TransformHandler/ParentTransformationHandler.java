package com.yaming.irsdp.ch01.TransformHandler;

import com.yaming.irsdp.ch01.JobHandler.JobPort;
import com.yaming.irsdp.ch01.JobHandler.SyncJobHandler;
import se.sics.kompics.*;
import se.sics.kompics.timer.Timer;
import se.sics.kompics.timer.java.JavaTimer;

public class ParentTransformationHandler extends ComponentDefinition {
    Component jobRequester = create(JobRequester.class, Init.NONE);
    Component jobHandler = create(SyncJobHandler.class, Init.NONE);
//    Component jobHandler = create(AsyncJobHandler.class, Init.NONE);
    Component transformationHandler = create(TransformationHandler.class, Init.NONE);
    Component timer = create(JavaTimer.class, Init.NONE);

    {
        connect(jobRequester.getNegative(TransformationPort.class), transformationHandler.getPositive(TransformationPort.class));
        connect(transformationHandler.getNegative(JobPort.class), jobHandler.getPositive(JobPort.class));
        connect(jobRequester.getNegative(Timer.class), timer.getPositive(Timer.class));
    }
}
