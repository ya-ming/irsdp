package se.kth.ict.id2203.components.sp2p;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.ports.flp2p.FairLossPointToPointLink;
import se.kth.ict.id2203.ports.flp2p.Flp2pSend;
import se.kth.ict.id2203.ports.sp2p.Sp2pSend;
import se.kth.ict.id2203.ports.sp2p.StubbornPointToPointLink;
import se.kth.ict.id2203.appFairLoss.Flp2pMessage;
import se.kth.ict.id2203.appStubborn.Sp2pMessage;

import se.sics.kompics.*;
import se.sics.kompics.address.Address;
import se.sics.kompics.launch.Topology;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

import java.util.ArrayList;
import java.util.Random;

public final class StubbornLink extends ComponentDefinition {

    private static final Logger logger = LoggerFactory.getLogger(StubbornLink.class);

	private Negative<StubbornPointToPointLink> sp2p = provides(StubbornPointToPointLink.class);
	private Positive<FairLossPointToPointLink> flp2p = requires(FairLossPointToPointLink.class);
	private Positive<Timer> timer = requires(Timer.class);

	private Address self;
	private Topology topology;
	private long sigma;

	private Random random = new Random(0);

    ArrayList<Sp2pSend> sent_list = new ArrayList<Sp2pSend>();

	public StubbornLink(StubbornLinkInit event) {
	    subscribe(handleStart, control);
		subscribe(handleSp2pSend, sp2p);
		subscribe(handleFlp2pMessage, flp2p);
		subscribe(handleTimeoutMessage, timer);

		topology = event.getTopology();
		sigma = event.getSigma();
		self = topology.getSelfAddress();
	}

    private Handler<Start> handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            ScheduleTimeout st = new ScheduleTimeout(1000);
            st.setTimeoutEvent(new TimeoutMessage(st));
            trigger(st, timer);
        }
    };


	private Handler<Sp2pSend> handleSp2pSend = new Handler<Sp2pSend>() {
		@Override
		public void handle(Sp2pSend event) {
            logger.info("Sp2pSend message {}", ((Sp2pMessage)event.getDeliverEvent()).getMessage());
            sent_list.add(event);
			trigger(new Flp2pSend(event.getDestination(), new Flp2pMessage(self,
                            ((Sp2pMessage)event.getDeliverEvent()).getMessage())),
                    flp2p);
		}
	};

	private Handler<TimeoutMessage> handleTimeoutMessage = new Handler<TimeoutMessage>() {
		@Override
		public void handle(TimeoutMessage event) {
            for (Sp2pSend s: sent_list
                 ) {
                trigger(new Flp2pSend(s.getDestination(), new Flp2pMessage(self,
                                ((Sp2pMessage)s.getDeliverEvent()).getMessage())),
                        flp2p);
            }

            ScheduleTimeout st = new ScheduleTimeout(1000);
            st.setTimeoutEvent(new TimeoutMessage(st));
            trigger(st, timer);
		}
	};

    private Handler<Flp2pMessage> handleFlp2pMessage = new Handler<Flp2pMessage>() {
        @Override
        public void handle(Flp2pMessage event) {
            logger.info("Sp2pSend Received lossy message {}", event.getMessage());
            trigger(new Sp2pMessage(self, event.getMessage()), sp2p);
        }
    };
}
