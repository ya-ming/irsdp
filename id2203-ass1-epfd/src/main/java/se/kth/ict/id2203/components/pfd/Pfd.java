package se.kth.ict.id2203.components.pfd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.components.epfd.CheckTimeout;
import se.kth.ict.id2203.components.epfd.HeartbeatRequestMessage;
import se.kth.ict.id2203.components.epfd.HeartbeatResponseMessage;
import se.kth.ict.id2203.ports.pfd.Crash;
import se.kth.ict.id2203.ports.pfd.PerfectFailureDetector;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.sics.kompics.*;
import se.sics.kompics.address.Address;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

import java.util.HashSet;
import java.util.Set;

public class Pfd extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(Pfd.class);

	private Negative<PerfectFailureDetector> epfd = provides(PerfectFailureDetector.class);
	private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
	private Positive<Timer> timer = requires(Timer.class);

    private Address selfAddress;
    private Set<Address> allAddresses;
    private Set<Address> alive;
    private Set<Address> crashed;
    private long initialDelay;
    private long deltaDelay;
    private long seqnum = 0;

	public Pfd(PfdInit init) {
        logger.debug("Pfd, Construct, self: {}", init.getSelfAddress());

        this.selfAddress = init.getSelfAddress();
        this.allAddresses = new HashSet<Address>(init.getAllAddresses());
        this.initialDelay = init.getInitialDelay();
        this.deltaDelay = init.getDeltaDelay();

        allAddresses.remove(selfAddress);
        alive = new HashSet<Address>(init.getAllAddresses());
        crashed = new HashSet<Address>();

        subscribe(handleStart, control);
        subscribe(handleTimeoutMessage, timer);
        subscribe(handleHeartBeatRequest, pp2p);
        subscribe(handleHeartBeatResponse, pp2p);
	}

	private void printAlive() {
	    logger.debug("Alive:");
        for (Address a:alive
             ) {
            logger.debug(a.toString());
        }
    }

    private void printCrashed() {
	    logger.debug("Crashed:");
        for (Address a: crashed
                ) {
            logger.debug(a.toString());
        }
    }

    private Handler<Start> handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            logger.debug("Pfd, handleStart");
            ScheduleTimeout st = new ScheduleTimeout(initialDelay);
            st.setTimeoutEvent(new CheckTimeout(st));
            trigger(st, timer);
        }
    };

    private Handler<CheckTimeout> handleTimeoutMessage = new Handler<CheckTimeout>() {
        @Override
        public void handle(CheckTimeout event) {
            seqnum += 1;

            logger.debug("Pfd, handleTimeoutMessage");
            printAlive();
            printCrashed();

            for (Address p : allAddresses
                 ) {
                if (!alive.contains(p) && !crashed.contains(p)) {
                    crashed.add(p);
                    trigger(new Crash(p), epfd);
                }

                trigger(new Pp2pSend(p, new HeartbeatRequestMessage(selfAddress, seqnum)), pp2p);
            }

            alive.clear();

            ScheduleTimeout st = new ScheduleTimeout(initialDelay);
            st.setTimeoutEvent(new CheckTimeout(st));
            trigger(st, timer);
        }
    };

    private Handler<HeartbeatRequestMessage> handleHeartBeatRequest = new Handler<HeartbeatRequestMessage>() {
        @Override
        public void handle(HeartbeatRequestMessage event) {
            logger.debug("Pfd, handleHeartBeatRequest {} sn {}", event.getSource(), event.getSn());

            trigger(new Pp2pSend(event.getSource(), new HeartbeatResponseMessage(selfAddress, event.getSn())), pp2p);
        }
    };

    private Handler<HeartbeatResponseMessage> handleHeartBeatResponse = new Handler<HeartbeatResponseMessage>() {
        @Override
        public void handle(HeartbeatResponseMessage event) {
            logger.debug("Pfd, handleHeartBeatResponse {} sn {}", event.getSource(), event.getSn());

            Address p = event.getSource();

//            printAlive();
//            printCrashed();

            if (event.getSn() == seqnum && alive.contains(p) == false)
            {
                alive.add(p);
            }

//            logger.debug("Pfd, handleHeartBeatResponse {} end", event.getSource());
        }
    };
}