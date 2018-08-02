package com.yaming.irsdp.ch02.TransformHandler;

import com.yaming.irsdp.ch02.Events.Confirm;
import com.yaming.irsdp.ch02.Events.Submit;
import se.sics.kompics.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.timer.*;

import java.util.UUID;

//Implements:
//        JobHandler, instance jh.
//
//        upon event <jh, submitHandler | job> do
//        process(job);
//        trigger <jh, Confirm | job>;
//
//
//
//        Module 1.1: Interface and properties of a job handler
//        Module:
//        Name: JobHandler, instance jh.
//
//        Events:
//        Request:<jh, submitHandler | job>: Requests a job to be processed.
//        Indication:<jh, Confirm | job>: Confirms that the given job has been (or will be) processed.
//
//        Properties:
//        JH1: Guaranteed response: Every submitted job is eventually confirmed.

public class JobRequester extends ComponentDefinition {

	private static final Logger LOG = LoggerFactory.getLogger(JobRequester.class);

	final Positive<TransformationPort> tp = requires(TransformationPort.class);
    final Positive<Timer> timer = requires(Timer.class);

    private long counter = 0;
    private UUID timerId;

	public JobRequester() {
		subscribe(startHandler, control);
		subscribe(responseHandler, tp);
        subscribe(timeoutHandler, timer);
	}

	Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(0, 100);
            JobTimeout timeout = new JobTimeout(spt);
            spt.setTimeoutEvent(timeout);
            trigger(spt, timer);
            timerId = timeout.getTimeoutId();
        }
	};

    Handler<JobTimeout> timeoutHandler = new Handler<JobTimeout>() {
        @Override
        public void handle(JobTimeout event) {
            counter++;

            if (counter <= 50) {
                trigger(new Submit(counter), tp);
            }
        }
    };

    @Override
    public void tearDown() {
        trigger(new CancelPeriodicTimeout(timerId), timer);
    }

    public static class JobTimeout extends Timeout {
        public JobTimeout(SchedulePeriodicTimeout spt) {
            super(spt);
        }
    }

	Handler<Confirm> responseHandler = new Handler<Confirm>() {
		@Override
		public void handle(Confirm event) {
			LOG.info("JobRequester received Confirm event #{}.", ((Confirm)(event)).getId());
		}
	};
}