package se.kth.ict.id2203.components.ele;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.components.epfd.CheckTimeout;
import se.kth.ict.id2203.ports.ele.EventualLeaderElectionPort;
import se.kth.ict.id2203.ports.ele.Trust;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.kth.ict.id2203.tools.ObjectToFile;
import se.sics.kompics.*;
import se.sics.kompics.address.Address;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//Algorithm 2.9: Elect Lower Epoch
//        Implements:
//            EventualLeaderDetector, instance Ω.
//
//        Uses:
//            FairLossPointToPointLinks, instance fll.
//
//        upon event < Ω, Init > do
//            epoch := 0;
//            store(epoch);
//            candidates := ∅;
//            trigger < Ω, Recovery >; // recovery procedure completes the initialization
//
//        upon event < Ω, Recovery > do
//            leader := maxrank(Π);
//            trigger < Ω, Trust | leader >;
//            delay := Δ;
//            retrieve(epoch);
//            epoch := epoch + 1;
//            store(epoch);
//            forall p ∈ Π do
//                trigger < fll, Send | p, [HEARTBEAT, epoch] >;
//            candidates := ∅;
//            starttimer (delay);
//
//        upon event < Timeout > do
//            newleader := select(candidates);
//            if newleader = leader then
//                delay := delay + Δ;
//                leader := newleader;
//                trigger < Ω, Trust | leader >;
//            forall p ∈ Π do
//                trigger < fll, Send | p, [HEARTBEAT, epoch] >;
//            candidates := ∅;
//            starttimer (delay);
//
//        upon event < fll, Deliver | q, [HEARTBEAT, ep] > do
//            if exists (s, e) ∈ candidates such that s = q ∧ e < ep then
//                candidates := candidates \ {(q, e)};
//            candidates := candidates ∪ (q, ep);

public class Eled extends ComponentDefinition {

    private static final Logger logger = LoggerFactory.getLogger(se.kth.ict.id2203.components.ele.Eled.class);

    private Negative<EventualLeaderElectionPort> elep = provides(EventualLeaderElectionPort.class);
    private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
    private Positive<Timer> timer = requires(Timer.class);

    private Address selfAddress;
    private Set<Address> allAddresses;
    private Long epoch = new Long(0);
    private Map<Address, Long> candidates;
    private Address leader;

    private long initialDelay;
    private long deltaDelay;
    private long seqnum = 0;

    ObjectToFile objectToFile = new ObjectToFile();

    public Eled(EledInit init) {
        logger.debug("Eled, Construct, self: {}", init.getSelfAddress());
        logger.debug("Eled, Construct, all: {}", init.getAllAddresses());

        this.selfAddress = init.getSelfAddress();
        this.allAddresses = new HashSet<Address>(init.getAllAddresses());
        this.initialDelay = init.getInitialDelay();
        this.deltaDelay = init.getDeltaDelay();

        candidates = new HashMap<>();

        leader = null;

        subscribe(handleStart, control);
        subscribe(handleTimeoutMessage, timer);
        subscribe(handleHeartBeatRequest, pp2p);
        subscribe(handleHeartBeatResponse, pp2p);
    }

    private void printCandidates() {
        logger.debug("Candidates:");
        for (Map.Entry<Address,Long> entry : candidates.entrySet())
            logger.debug("Key = " + entry.getKey() +
                    ", Value = " + entry.getValue());
    }

    private Handler<Start> handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            logger.debug("Eled, handleStart");

            Object temp = objectToFile.readObject(String.valueOf(selfAddress.getId()));
            if (temp != null)
                epoch = (Long)temp;

            leader = maxRank(allAddresses);
            trigger(new Trust(leader), elep);

            epoch = epoch + 1;
            objectToFile.writeObject(String.valueOf(selfAddress.getId()), epoch);

            for (Address p : allAddresses
                    ) {
                trigger(new Pp2pSend(p, new HeartbeatRequestEleMessage(selfAddress, epoch)), pp2p);
            }

            candidates.clear();

            ScheduleTimeout st = new ScheduleTimeout(initialDelay);
            st.setTimeoutEvent(new CheckTimeout(st));
            trigger(st, timer);
        }
    };

    private Handler<CheckTimeout> handleTimeoutMessage = new Handler<CheckTimeout>() {
        @Override
        public void handle(CheckTimeout event) {

            logger.debug("Eled, handleTimeoutMessage");
            printCandidates();

            Address newLeader = select(candidates);
            if (newLeader != null && newLeader.equals(leader) == false) {
                initialDelay += deltaDelay;
                leader = newLeader;
                logger.warn("Eled, increased delay to {}", initialDelay);
                trigger(new Trust(leader), elep);
            }

            seqnum += 1;

            for (Address p : allAddresses
                    ) {
                trigger(new Pp2pSend(p, new HeartbeatRequestEleMessage(selfAddress, epoch)), pp2p);
            }

            candidates.clear();

            ScheduleTimeout st = new ScheduleTimeout(initialDelay);
            st.setTimeoutEvent(new CheckTimeout(st));
            trigger(st, timer);
        }
    };

    private Handler<HeartbeatRequestEleMessage> handleHeartBeatRequest = new Handler<HeartbeatRequestEleMessage>() {
        @Override
        public void handle(HeartbeatRequestEleMessage event) {
            logger.debug("Eled, handleHeartBeatRequest {} epoch {}", event.getSource(), event.getEpoch());

            trigger(new Pp2pSend(event.getSource(), new HeartbeatResponseEleMessage(selfAddress, event.getEpoch())), pp2p);
        }
    };

    private Handler<HeartbeatResponseEleMessage> handleHeartBeatResponse = new Handler<HeartbeatResponseEleMessage>() {
        @Override
        public void handle(HeartbeatResponseEleMessage event) {
            logger.debug("Eled, handleHeartBeatResponse {} epoch {}", event.getSource(), event.getEpoch());

            Address address = event.getSource();
            Long ep = event.getEpoch();

//            printAlive();
//            printSuspected();

            if (candidates.containsKey(address)) {
                Long e = candidates.get(address);
                if (e < ep)
                    candidates.remove(address);
            }
            else {
                candidates.put(address, ep);
            }

//            logger.debug("Eled, handleHeartBeatResponse {} end", event.getSource());
        }
    };

    private Address maxRank(Set<Address> addresses) {
        Address temp_leader = null;
        for (Address a: addresses
                ) {
//            System.out.println("processing " + a);
            if (temp_leader == null) {
//                System.out.println("templeader == null, " + a);
                temp_leader = a;
            } else if (temp_leader.getId() > a.getId()) {
//                System.out.println("templeader != null, " + temp_leader + ", " + temp_leader.getId());
//                System.out.println("templeader != null, " + a + ", " + a.getId());
                temp_leader = a;
            }
        }
        return temp_leader;
    }

    private Address select(Map<Address, Long> map) {
        Set<Address> addresses = new HashSet<>();
        for (Map.Entry<Address,Long> entry : candidates.entrySet())
            addresses.add(entry.getKey());

        return maxRank(addresses);
    }
}
