package se.kth.ict.id2203.components.fc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.ports.fc.Decide;
import se.kth.ict.id2203.ports.fc.Propose;
import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.fc.FloodingConsensus;
import se.kth.ict.id2203.ports.pfd.Crash;
import se.kth.ict.id2203.ports.pfd.PerfectFailureDetector;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

import java.util.*;

//Algorithm 5.1: Flooding Consensus
//        Implements:
//            Consensus, instance c.
//        Uses:
//            BestEffortBroadcast, instance beb;
//            PerfectFailureDetector, instance P.
//
//        upon event < c, Init > do
//            correct := Π;
//            round := 1;
//            decision := ⊥;
//            receivedfrom := [∅]N;
//            proposals := [∅]N;
//            receivedfrom[0] := Π;
//
//        upon event < P, Crash | p > do
//            correct := correct \ {p};
//
//        upon event < c, Propose | v > do
//            proposals[1] := proposals[1] ∪ {v};
//            trigger < beb, Broadcast | [PROPOSAL, 1, proposals[1]] >;
//
//        upon event < beb, Deliver | p, [PROPOSAL, r, ps] > do
//            receivedfrom[r] := receivedfrom[r] ∪ {p};
//            proposals[r] := proposals[r] ∪ ps;
//
//        upon correct ⊆ receivedfrom[round] ∧ decision = ⊥ do
//            if receivedfrom[round] = receivedfrom[round − 1] then
//                decision := min(proposals[round]);
//                trigger < beb, Broadcast | [DECIDED, decision] >;
//                trigger < c, Decide | decision >;
//            else
//                round := round + 1;
//                trigger < beb, Broadcast | [PROPOSAL, round, proposals[round − 1]] >;
//
//        upon event < beb, Deliver | p, [DECIDED, v] > such that p ∈ correct ∧ decision = ⊥ do
//            decision := v;
//            trigger < beb, Broadcast | [DECIDED, decision] >;
//            trigger < c, Decide | decision >;

public class Fc extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(Fc.class);

	private Negative<FloodingConsensus> fc = provides(FloodingConsensus.class);
	private Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
	private Positive<PerfectFailureDetector> pfd = requires(PerfectFailureDetector.class);

    private HashSet<Address> correct;
    private Integer round;
    private Object decision;
    private HashMap<Integer, HashSet<Address>> receivedFrom;
    private HashMap<Integer, HashSet<Object>> proposals;

    private Address self;
    private final Integer N;

	public Fc(FcInit init) {

        N = init.getAllAddresses().size();
        self = init.getSelfAddress();

        correct = new HashSet<>(init.getAllAddresses());
        round = 1;
        decision = null;
        proposals = new HashMap<>();
        receivedFrom = new HashMap<>();
        receivedFrom.put(0, new HashSet<Address>(init.getAllAddresses()));

        subscribe(handlePropose, fc);
        subscribe(handleFcProposal, beb);
        subscribe(handleFcDecided, beb);
        subscribe(handleCrash, pfd);
    }

    private Object min(HashSet<Object> ps) {
	    Object min = null;
        for (Object o : ps
             ) {
            if (min == null)
                min = o;
            else if (min.hashCode() > o.hashCode())
                min = o;
        }
        return min;
    }

    private Handler<Crash> handleCrash = new Handler<Crash>() {
        @Override
        public void handle(Crash crash) {
            logger.debug("Fc, handleCrash ");

            logger.info(String.format("    node crashed(address = %s)",
                    crash.getSource()));

            correct.remove(crash.getSource());

            DecideOrMoveToTheNextRound();
        }
    };

    private Handler<Propose> handlePropose = new Handler<Propose>() {
        @Override
        public void handle(Propose event) {
            logger.debug("Fc, Propose ");

            logger.info(String.format("    Propose message(v = %s)",
                    event.getValue()));

            HashSet<Object> set = new HashSet<>();
            set.add(event.getValue());
            proposals.put(1, set);

            trigger(new BebBroadcast(new FcProposal(self, 1, proposals.get(1))), beb);
        }
    };

    private void DecideOrMoveToTheNextRound() {
        logger.debug("Fc, DecideOrMoveToTheNextRound ");
        HashSet<Address> receivedFromSet = receivedFrom.get(round);
        HashSet<Object> proposalsSet = proposals.get(round);

        if (receivedFromSet.containsAll(correct) && decision == null) {
            if (receivedFromSet.equals(receivedFrom.get(round - 1))) {
                logger.debug("Fc, FcProposal, can decide in this round: " + round);
                decision = min(proposalsSet);
                trigger(new BebBroadcast(new FcDecided(self, decision)), beb);
                trigger(new Decide(decision), fc);
            }
            else {
                round++;
                trigger(new BebBroadcast(new FcProposal(self, round, proposals.get(round - 1))), beb);
                logger.debug("Fc, FcProposal, can't decide in this round: " + round + " moving to round: " + round);
            }
        }
    }

    private Handler<FcProposal> handleFcProposal = new Handler<FcProposal>() {
        @Override
        public void handle(FcProposal event) {
            logger.debug("Fc, FcProposal ");

            logger.info(String.format("    FcProposal message(source = %s, r = %d, ps = %s)",
                    event.getSource(), event.getRound(), event.getPs()));

            Integer r = event.getRound();
            if (receivedFrom.get(r) == null) {
                HashSet<Address> set = new HashSet<>();
                receivedFrom.put(r, set);
            }

            HashSet<Address> receivedFromSet = receivedFrom.get(r);
            receivedFromSet.add(event.getSource());

            if (proposals.get(r) == null) {
                HashSet<Object> set = new HashSet<>();
                proposals.put(r, set);
            }

            HashSet<Object> proposalsSet = proposals.get(r);
            if (event.getPs() != null)
                proposalsSet.addAll(event.getPs());

            DecideOrMoveToTheNextRound();
        }
    };

    private Handler<FcDecided> handleFcDecided = new Handler<FcDecided>() {
        @Override
        public void handle(FcDecided event) {
            logger.debug("Fc, handleFcDecided ");
            logger.info(String.format("    handleFcDecided message(source = %s, decision = %s)",
                    event.getSource(), event.getDecision()));

            if (correct.contains(event.getSource()) && decision == null) {
                decision = event.getDecision();
                trigger(new BebBroadcast(new FcDecided(self, decision)), beb);
                trigger(new Decide(decision), fc);
            }
        }
    };
}
