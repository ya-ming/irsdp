package com.yaming.irsdp.ch02.JobHandler;

import com.yaming.irsdp.ch02.Events.Confirm;
import com.yaming.irsdp.ch02.Events.Submit;
import se.sics.kompics.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.timer.CancelPeriodicTimeout;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;

import java.util.UUID;
import java.util.Vector;

//Algorithm 1.2: Asynchronous Job Handler
//        Implements:
//        JobHandler, instance jh.
//
//        upon event<jh, Init>do
//        buffer := ∅;
//
//        upon event<jh, Submit | job>do
//        buffer := buffer ∪{ job};
//        trigger<jh, Confirm | job>;
//
//        upon buffer != ∅ do
//        job := selectjob(buffer);
//        process(job);
//        buffer := buffer \{ job};

public class AsyncJobHandler extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(JobRequester.class);

    final Negative<JobPort> jp = provides(JobPort.class);
    final Positive<Timer> timer = requires(Timer.class);

    private UUID timerId;
    private Vector vector = new Vector();

    public AsyncJobHandler() {
        System.out.println("AsyncJobHandler created.");
        subscribe(startHandler, control);
        subscribe(jobHandler, jp);
        subscribe(timeoutHandler, timer);

        vector.clear();
    }

    Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(0, 1000);
            JobRequester.JobTimeout timeout = new JobRequester.JobTimeout(spt);
            spt.setTimeoutEvent(timeout);
            trigger(spt, timer);
            timerId = timeout.getTimeoutId();
        }
    };

    Handler<JobRequester.JobTimeout> timeoutHandler = new Handler<JobRequester.JobTimeout>() {
        @Override
        public void handle(JobRequester.JobTimeout event) {
            if (vector.size() > 0) {
                for (Object obj: vector
                     ) {
                    Submit job = (Submit)obj;
                    LOG.info("AsyncJobHandler processing Submit event #{}.", job.getId());
                }
                vector.clear();
            }
        }
    };

    @Override
    public void tearDown() {
        trigger(new CancelPeriodicTimeout(timerId), timer);
    }

    public static class PingTimeout extends Timeout {
        public PingTimeout(SchedulePeriodicTimeout spt) {
            super(spt);
        }
    }

    Handler<Submit> jobHandler = new Handler<Submit>() {
        @Override
        public void handle(Submit event) {
            long counter = event.getId();
            LOG.info("AsyncJobHandler received Submit event #{}.", counter);

            vector.add(event);
            trigger(new Confirm(counter), jp);
        }
    };
}