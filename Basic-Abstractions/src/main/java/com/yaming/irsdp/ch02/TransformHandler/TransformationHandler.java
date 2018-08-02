package com.yaming.irsdp.ch02.TransformHandler;

import com.yaming.irsdp.ch02.Events.*;
import com.yaming.irsdp.ch02.Events.Error;
import com.yaming.irsdp.ch02.JobHandler.JobPort;
import com.yaming.irsdp.ch02.JobHandler.JobRequester;
import se.sics.kompics.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Thread.sleep;

//Algorithm 1.3: Job-Transformation by Buffering
//Implements:
//    TransformationHandler, instance th.
//
//Uses:
//    JobHandler, instance jh.
//
//upon event < th, Init > do
//    top := 1;
//    bottom := 1;
//    handling := FALSE;
//    buffer := [⊥] M ;
//
//upon event < th, submitHandler | job > do
//    if bottom + M = top then
//        trigger < th, Error | job >;
//    else
//        buffer[top mod M +1 ]: =job;
//        top := top +1 ;
//        trigger < th, Confirm | job >;
//
//upon bottom < top ∧ handling = FALSE do
//    job := buffer[bottom mod M +1 ] ;
//    bottom := bottom +1 ;
//    handling := TRUE;
//    trigger < jh, submitHandler | job >;
//
//upon event < jh, Confirm | job > do
//    handling := FALSE;

public class TransformationHandler extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(JobRequester.class);

    final Negative<TransformationPort> tp = provides(TransformationPort.class);
    final Positive<JobPort> jp = requires(JobPort.class);

    private int top = 1;
    private int bottom = 1;
    private boolean handling = false;
    private Event[] buffer = new Event[5];

    public TransformationHandler() {
        System.out.println("TransformationHandler created.");
        subscribe(startHandler, control);
        subscribe(submitHandler, tp);
        subscribe(confirmHandler, jp);
    }

    Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {
        }
    };

    Handler<Submit> submitHandler = new Handler<Submit>() {
        @Override
        public void handle(Submit event) {
            LOG.info("TransformationHandler received Submit event #{}.", ((Submit)event).getId());

            if ((bottom + 5) == top) {
                LOG.info("TransformationHandler Error(botton+M==top) event #{}.", ((Submit)event).getId());
                trigger(new Error(((Submit)event).getId()), tp);
            }
            else {
//                buffer[top % (5 + 1)] = event;
                top++;
                try {
                    sleep(300); // simulate processing delay
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                trigger(event, jp);
            }
        }
    };

    Handler<Confirm> confirmHandler = new Handler<Confirm>() {
        @Override
        public void handle(Confirm event) {
            LOG.info("TransformationHandler received Confirm event #{}.", ((Confirm)event).getId());
            bottom++;
            trigger(event, tp);
        }
    };
}