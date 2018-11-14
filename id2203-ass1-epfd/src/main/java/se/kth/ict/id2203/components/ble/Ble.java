package se.kth.ict.id2203.components.ble;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.components.epfd.CheckTimeout;
import se.kth.ict.id2203.ports.ble.BallotLeaderElection;
import se.kth.ict.id2203.ports.ble.BallotLeader;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.kth.ict.id2203.tools.MessageLogger;
import se.sics.kompics.*;
import se.sics.kompics.address.Address;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

import java.util.HashSet;
import java.util.Set;


public class Ble extends ComponentDefinition {

    private static final Logger logger = LoggerFactory.getLogger(Ble.class);

    private Negative<BallotLeaderElection> ble = provides(BallotLeaderElection.class);
    private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
    private Positive<Timer> timer = requires(Timer.class);

    private Address self;
    private Set<Address> allAddresses;

    private Long round;
    private HashSet<AddressBallotPair> ballots;
    private Ballot ballot;
    private Ballot ballotMax;
    private AddressBallotPair leader;


    private long initialDelay;
    private long deltaDelay;

    public Ble(BleInit init) {
        logger.debug("Ble, Construct, self: {}", init.getSelfAddress());
        logger.debug("Ble, Construct, all: {}", init.getAllAddresses());

        this.self = init.getSelfAddress();
        this.allAddresses = new HashSet<Address>(init.getAllAddresses());
        this.initialDelay = init.getInitialDelay();
        this.deltaDelay = init.getDeltaDelay();

        round = new Long(0);
        leader = null;

        ballots = new HashSet<>();
        ballot = new Ballot(0, self.getId());
        ballot = increment(ballot);
        ballotMax = ballot;

        subscribe(handleStart, control);
        subscribe(handleTimeoutMessage, timer);
        subscribe(handleHeartBeatRequest, pp2p);
        subscribe(handleHeartBeatResponse, pp2p);
    }

    private void logLocalVariables() {
        logger.trace(String.format("%d rnote over Ble%d@@" +
                        "r:%d, ballots:%s, ballot:%s, leader:%s, ballotMax:%s" +
                        "@@endrnote",
                System.nanoTime(),
                self.getId(), round, ballots, ballot, leader, ballotMax)
        );
    }

    private AddressBallotPair maxByBallot(HashSet<AddressBallotPair> set) {
        AddressBallotPair maxAddressBallotPair = null;
        for (AddressBallotPair addressBallotPair:set
             ) {
            if (maxAddressBallotPair == null) {
                maxAddressBallotPair = addressBallotPair;
            } else if (addressBallotPair.getBallot().getN() > maxAddressBallotPair.getBallot().getN()){
                maxAddressBallotPair = addressBallotPair;
            }
        }
        return maxAddressBallotPair;
    }

    private Integer rank(Address s) {
        return s.getId();
    }

    private Ballot increment(Ballot ballot) {
        ballot.setN(ballot.getN() * allAddresses.size() + rank(self));

        return ballot;
    }

    /*
     * Key function of Ballot Leader Election
     * On every timeout, it select the Peer with the maximum Ballot number as the 'top' from the received keep alive list
     * If the known MaxBallot leader is not in the list (maybe dropped from the system),
     * increase the ballot of myself and set current leader to null.
     * If top not equal to current leader, select new leader with max ballot and notify the user
     */
    private void checkLeader() {
        HashSet<AddressBallotPair> tempSet = new HashSet<>(ballots);
        tempSet.add(new AddressBallotPair(self, ballot));
        AddressBallotPair top = maxByBallot(tempSet);

        logger.debug("Ble, checkLeader, top: " + top + " ballotMax: " + ballotMax);

        if (top.getBallot().isLessThan(ballotMax)) {
            logger.debug("Ble, checkLeader, top isLessThan(ballotMax)");
            while (ballot.isLessThanOrEqualTo(ballotMax)) {
                logger.debug("Ble, checkLeader, increment");

                // two ways to increase the n of ballot but not sure which way is correct
                // 1. n += 1
                // 2. n = n * N + rank(self)
                // ballot.increment();
                ballot = increment(ballot); // looks like should use the 2nd method which created an unique ballot number
            }
            leader = null;
        } else {
            if (top.equals(leader) == false) {
                ballotMax = top.getBallot();
                leader = top;

                logger.debug("Ble, checkLeader, new leader!");
                BallotLeader ballotLeader = new BallotLeader(top.getAddress(), top.getBallot());
//                MessageLogger.logMessageOut(logger, "Ble", self, "Client", self, ballotLeader);

                trigger(ballotLeader, ble);
            }
        }
    }

    private Handler<Start> handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            logger.debug("Ble, handleStart");

            ScheduleTimeout st = new ScheduleTimeout(initialDelay);
            st.setTimeoutEvent(new CheckTimeout(st));
            trigger(st, timer);
        }
    };

    private Handler<CheckTimeout> handleTimeoutMessage = new Handler<CheckTimeout>() {
        @Override
        public void handle(CheckTimeout event) {

            logger.debug("Ble, handleTimeoutMessage");
            logLocalVariables();

            if ((ballots.size() + 1) >= (allAddresses.size()/2)) {
                checkLeader();
            }

            ballots.clear();
            round++;

            for (Address p:allAddresses
                 ) {
                if (p != self) {
//                    logger.debug("Ble, handleTimeoutMessage, send HeartbeatRequestBleMessage to " + p);
                    HeartbeatRequestBleMessage heartbeatRequestBleMessage = new HeartbeatRequestBleMessage(self, round, ballotMax);
//                    MessageLogger.logMessageOut(logger, "Ble", self, "Ble", p, heartbeatRequestBleMessage);
                    trigger(new Pp2pSend(p, heartbeatRequestBleMessage), pp2p);
                }
            }

            ScheduleTimeout st = new ScheduleTimeout(initialDelay);
            st.setTimeoutEvent(new CheckTimeout(st));
            trigger(st, timer);

            logLocalVariables();
        }
    };

    private Handler<HeartbeatRequestBleMessage> handleHeartBeatRequest = new Handler<HeartbeatRequestBleMessage>() {
        @Override
        public void handle(HeartbeatRequestBleMessage event) {
            logger.debug("Ble, handleHeartBeatRequest " + event);
            logLocalVariables();
//            MessageLogger.logMessageIn(logger, "Ble", event.getSource(), "Ble", self, event);

            if (ballotMax.isLessThan(event.getBallotMax())) {
                ballotMax = event.getBallotMax();
            }

            HeartbeatResponseBleMessage heartbeatResponseBleMessage = new HeartbeatResponseBleMessage(self, event.getRound(), ballot);
//            MessageLogger.logMessageOut(logger, "Ble", self, "Ble", event.getSource(), heartbeatResponseBleMessage);
            trigger(new Pp2pSend(event.getSource(), heartbeatResponseBleMessage), pp2p);
            logLocalVariables();
        }
    };

    private Handler<HeartbeatResponseBleMessage> handleHeartBeatResponse = new Handler<HeartbeatResponseBleMessage>() {
        @Override
        public void handle(HeartbeatResponseBleMessage event) {
            logger.debug("Ble, handleHeartBeatResponse " + event);
            logLocalVariables();
//            MessageLogger.logMessageIn(logger, "Ble", event.getSource(), "Ble", self, event);

            Address address = event.getSource();
            Long r = event.getRound();

            if (r.equals(round)) {
                ballots.add(new AddressBallotPair(address, event.getBallot()));
            } else {
                initialDelay += deltaDelay;
            }

            logLocalVariables();
        }
    };
}
