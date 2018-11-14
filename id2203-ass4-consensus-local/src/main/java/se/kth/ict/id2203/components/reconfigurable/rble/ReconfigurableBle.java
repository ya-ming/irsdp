package se.kth.ict.id2203.components.reconfigurable.rble;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.components.epfd.CheckTimeout;
import se.kth.ict.id2203.ports.reconfigurable.cfg.ConfigPort;
import se.kth.ict.id2203.ports.reconfigurable.cfg.Configuration;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.kth.ict.id2203.ports.reconfigurable.rble.ReconfigurableBallotLeader;
import se.kth.ict.id2203.ports.reconfigurable.rble.ReconfigurableBallotLeaderElection;
import se.sics.kompics.*;
import se.sics.kompics.address.Address;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

import java.util.HashSet;
import java.util.Set;


public class ReconfigurableBle extends ComponentDefinition {

    private static final Logger logger = LoggerFactory.getLogger(ReconfigurableBle.class);

    private Negative<ReconfigurableBallotLeaderElection> ble = provides(ReconfigurableBallotLeaderElection.class);
    private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
    private Positive<Timer> timer = requires(Timer.class);
    private Positive<ConfigPort> cfgPort = requires(ConfigPort.class);

    private Address self;
    private Configuration configuration;
    private Set<Address> allAddresses;

    private Long round;
    private HashSet<AddressBallotPair> ballots;
    private ReBallot reBallot;
    private ReBallot reBallotMax;
    private AddressBallotPair leader;


    private long initialDelay;
    private long deltaDelay;

    public ReconfigurableBle(ReconfigurableBleInit init) {
        logger.debug(" Construct, self: {}", init.getSelfAddress());
        logger.debug(" Construct, all: {}", init.getAllAddresses());

        this.self = init.getSelfAddress();
        this.configuration = init.getConfiguration();
        // this.allAddresses = new HashSet<Address>(init.getAllAddresses());
        this.allAddresses = init.getConfiguration().getAddresses();
        this.initialDelay = init.getInitialDelay();
        this.deltaDelay = init.getDeltaDelay();

        round = new Long(0);
        leader = null;

        ballots = new HashSet<>();
        reBallot = new ReBallot(0, 0, self.getId());
        reBallot = increment(reBallot);
        reBallotMax = reBallot;

        subscribe(handleStart, control);
        subscribe(handleTimeoutMessage, timer);
        subscribe(handleHeartBeatRequest, pp2p);
        subscribe(handleHeartBeatResponse, pp2p);
        subscribe(handleCfg, cfgPort);
    }

    private void logLocalVariables() {
        logger.trace(String.format("%d rnote over ReconfigurableBle%d@@" +
                        "r:%d, ballots:%s, reBallot:%s, leader:%s, reBallotMax:%s" +
                        "@@endrnote",
                System.nanoTime(),
                self.getId(), round, ballots, reBallot, leader, reBallotMax)
        );
    }

    private AddressBallotPair maxByBallot(HashSet<AddressBallotPair> set) {
        AddressBallotPair maxAddressBallotPair = null;
        for (AddressBallotPair addressBallotPair:set
             ) {
            if (maxAddressBallotPair == null) {
                maxAddressBallotPair = addressBallotPair;
            } else if (addressBallotPair.getReBallot().getCfg() > maxAddressBallotPair.getReBallot().getCfg()) {
                maxAddressBallotPair = addressBallotPair;
            } else if (addressBallotPair.getReBallot().getCfg().equals( maxAddressBallotPair.getReBallot().getCfg())) {
                if(addressBallotPair.getReBallot().getN() > maxAddressBallotPair.getReBallot().getN()) {
                    maxAddressBallotPair = addressBallotPair;
                }
            }
        }
        return maxAddressBallotPair;
    }

    private Integer rank(Address s) {
        return s.getId();
    }

    private ReBallot increment(ReBallot reBallot) {
        reBallot.setN(reBallot.getN() * allAddresses.size() + rank(self));

        return reBallot;
    }

    /*
     * Key function of ReBallot Leader Election
     * On every timeout, it select the Peer with the maximum ReBallot number as the 'top' from the received keep alive list
     * If the known MaxBallot leader is not in the list (maybe dropped from the system),
     * increase the reBallot of myself and set current leader to null.
     * If top not equal to current leader, select new leader with max reBallot and notify the user
     */
    private void checkLeader() {
        HashSet<AddressBallotPair> tempSet = new HashSet<>(ballots);
        tempSet.add(new AddressBallotPair(self, reBallot));
        AddressBallotPair top = maxByBallot(tempSet);

        logger.debug(" checkLeader, top: " + top + " reBallotMax: " + reBallotMax);

        if (top.getReBallot().isLessThan(reBallotMax)) {
            logger.debug(" checkLeader, top isLessThan(reBallotMax)");
            while (reBallot.isLessThanOrEqualTo(reBallotMax)) {
                logger.debug(" checkLeader, increment");

                // two ways to increase the n of reBallot but not sure which way is correct
                // 1. n += 1
                // 2. n = n * N + rank(self)
                // reBallot.increment();
                reBallot = increment(reBallot); // looks like should use the 2nd method which created an unique reBallot number
            }
            leader = null;
        } else {
            if (top.equals(leader) == false) {
                reBallotMax = top.getReBallot();
                leader = top;

                logger.debug(" checkLeader, new leader!");
                ReconfigurableBallotLeader ballotLeader = new ReconfigurableBallotLeader(top.getAddress(), top.getReBallot());
//                MessageLogger.logMessageOut(logger, "Ble", self, "Client", self, ballotLeader);

                trigger(ballotLeader, ble);
            }
        }
    }

    private Handler<Start> handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            logger.debug(" handleStart");

            ScheduleTimeout st = new ScheduleTimeout(initialDelay);
            st.setTimeoutEvent(new CheckTimeout(st));
            trigger(st, timer);
        }
    };

    private Handler<CheckTimeout> handleTimeoutMessage = new Handler<CheckTimeout>() {
        @Override
        public void handle(CheckTimeout event) {

            logger.debug(" handleTimeoutMessage");
            logLocalVariables();

            if ((ballots.size() + 1) >= (allAddresses.size()/2)) {
                checkLeader();
            }

            ballots.clear();
            round++;

            for (Address p:allAddresses
                 ) {
                //if (p != self)
                {
//                    logger.debug("Ble, handleTimeoutMessage, send HeartbeatRequestBleMessage to " + p);
                    HeartbeatRequestBleMessage heartbeatRequestBleMessage =
                            new HeartbeatRequestBleMessage(self, configuration.getCfg(), round, reBallotMax);
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
            logger.debug(" handleHeartBeatRequest " + event);
            logLocalVariables();
//            MessageLogger.logMessageIn(logger, "Ble", event.getSource(), "Ble", self, event);

            if (reBallotMax.isLessThan(event.getReBallotMax())) {
                reBallotMax = event.getReBallotMax();
            }

            HeartbeatResponseBleMessage heartbeatResponseBleMessage =
                    new HeartbeatResponseBleMessage(self, event.getCfg(), event.getRound(), reBallot);
//            MessageLogger.logMessageOut(logger, "Ble", self, "Ble", event.getSource(), heartbeatResponseBleMessage);
            trigger(new Pp2pSend(event.getSource(), heartbeatResponseBleMessage), pp2p);
            logLocalVariables();
        }
    };

    private Handler<HeartbeatResponseBleMessage> handleHeartBeatResponse = new Handler<HeartbeatResponseBleMessage>() {
        @Override
        public void handle(HeartbeatResponseBleMessage event) {
            logger.debug(" handleHeartBeatResponse " + event);
            logLocalVariables();
//            MessageLogger.logMessageIn(logger, "Ble", event.getSource(), "Ble", self, event);

            Address address = event.getSource();
            Long r = event.getRound();

            if (configuration.getCfg().equals(event.getCfg()) && r.equals(round)) {
                ballots.add(new AddressBallotPair(address, event.getReBallot()));
            } else {
                initialDelay += deltaDelay;
            }

            logLocalVariables();
        }
    };

    private Handler<Configuration> handleCfg = new Handler<Configuration>() {
        @Override
        public void handle(Configuration event) {
            logger.debug(" handleCfg " + event);
            allAddresses = event.getAddresses();
            configuration.setCfg(event.getCfg());
            configuration.setAddresses(event.getAddresses());

            round = new Long(0);
            leader = null;

            reBallot.setN(0);
            reBallot = increment(reBallot);
            reBallot.setCfg(event.getCfg());

            ballots.clear();
        }
    };
}
