package com.yaming.irsdp.ch02.JobHandler;

import com.yaming.irsdp.ch02.Events.Confirm;
import com.yaming.irsdp.ch02.Events.Submit;
import se.sics.kompics.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Algorithm 1.1: Synchronous Job Handler
//Implements:
//        JobHandler, instance jh.
//
//        upon event <jh, Submit | job> do
//        process(job);
//        trigger <jh, Confirm | job>;

public class SyncJobHandler extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(JobRequester.class);

    final Negative<JobPort> jp = provides(JobPort.class);

    public SyncJobHandler() {
        System.out.println("SyncJobHandler created.");
        subscribe(startHandler, control);
        subscribe(jobHandler, jp);
    }

    Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {
        }
    };

    Handler<Submit> jobHandler = new Handler<Submit>() {
        @Override
        public void handle(Submit event) {
            long counter = event.getId();
            LOG.info("SyncJobHandler received Submit event #{}.", counter);

            trigger(new Confirm(counter), jp);
        }
    };
}