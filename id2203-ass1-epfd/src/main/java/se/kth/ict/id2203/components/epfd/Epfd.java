package se.kth.ict.id2203.components.epfd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.ports.epfd.EventuallyPerfectFailureDetector;
import se.kth.ict.id2203.ports.epfd.Restore;
import se.kth.ict.id2203.ports.epfd.Suspect;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.sics.kompics.*;
import se.sics.kompics.address.Address;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

import java.util.HashSet;
import java.util.Set;

public class Epfd extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(Epfd.class);

	private Negative<EventuallyPerfectFailureDetector> epfd = provides(EventuallyPerfectFailureDetector.class);
	private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
	private Positive<Timer> timer = requires(Timer.class);

    private Address selfAddress;
    private Set<Address> allAddresses;
    private Set<Address> alive;
    private Set<Address> suspected;
    private long initialDelay;
    private long deltaDelay;
    private long seqnum = 0;

	public Epfd(EpfdInit init) {
        logger.debug("Epfd, Construct, self: {}", init.getSelfAddress());

        this.selfAddress = init.getSelfAddress();
        this.allAddresses = new HashSet<Address>(init.getAllAddresses());
        this.initialDelay = init.getInitialDelay();
        this.deltaDelay = init.getDeltaDelay();

        allAddresses.remove(selfAddress);
        alive = new HashSet<Address>(init.getAllAddresses());
        suspected = new HashSet<Address>();

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

    private void printSuspected() {
	    logger.debug("Suspected:");
        for (Address a:suspected
                ) {
            logger.debug(a.toString());
        }
    }

    private Handler<Start> handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            logger.debug("Epfd, handleStart");
            ScheduleTimeout st = new ScheduleTimeout(initialDelay);
            st.setTimeoutEvent(new CheckTimeout(st));
            trigger(st, timer);
        }
    };

    private Handler<CheckTimeout> handleTimeoutMessage = new Handler<CheckTimeout>() {
        @Override
        public void handle(CheckTimeout event) {
//            for (Sp2pSend s: sent_list
//                    ) {
//                trigger(new Flp2pSend(s.getDestination(), new Flp2pMessage(self,
//                                ((Sp2pMessage)s.getDeliverEvent()).getMessage())),
//                        flp2p);
//            }

            logger.debug("Epfd, handleTimeoutMessage");
            printAlive();
            printSuspected();

            HashSet<Address> intersection = new HashSet<Address>();
            intersection.addAll(alive);
            intersection.retainAll(suspected);
            if (intersection.size() > 0) {
                initialDelay += deltaDelay;
                logger.warn("Epfd, increased delay to {}", initialDelay);
            }

            seqnum += 1;

            for (Address p : allAddresses
                 ) {
                if (!alive.contains(p) && !suspected.contains(p)) {
                    suspected.add(p);
                    trigger(new Suspect(p), epfd);
                }
                else if (alive.contains(p) && suspected.contains(p)) {
                    suspected.remove(p);
                    trigger(new Restore(p), epfd);
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
            logger.debug("Epfd, handleHeartBeatRequest {} sn {}", event.getSource(), event.getSn());

            trigger(new Pp2pSend(event.getSource(), new HeartbeatResponseMessage(selfAddress, event.getSn())), pp2p);
        }
    };

    private Handler<HeartbeatResponseMessage> handleHeartBeatResponse = new Handler<HeartbeatResponseMessage>() {
        @Override
        public void handle(HeartbeatResponseMessage event) {
            logger.debug("Epfd, handleHeartBeatResponse {} sn {}", event.getSource(), event.getSn());

            Address p = event.getSource();

//            printAlive();
//            printSuspected();

            if (event.getSn() == seqnum || suspected.contains(p))
            {
                alive.add(p);
            }

//            logger.debug("Epfd, handleHeartBeatResponse {} end", event.getSource());
        }
    };
}