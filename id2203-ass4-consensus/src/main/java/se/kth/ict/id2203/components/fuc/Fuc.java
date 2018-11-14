package se.kth.ict.id2203.components.fuc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.components.fc.FcDecided;
import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.fc.Decide;
import se.kth.ict.id2203.ports.fc.Propose;
import se.kth.ict.id2203.ports.fuc.FloodingUniformConsensus;
import se.kth.ict.id2203.ports.pfd.Crash;
import se.kth.ict.id2203.ports.pfd.PerfectFailureDetector;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

import java.util.HashSet;
import java.util.Objects;

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
//            proposalset := [∅]N;
//            receivedfrom[0] := Π;
//
//        upon event < P, Crash | p > do
//            correct := correct \ {p};
//
//        upon event < c, Propose | v > do
//            proposalset[1] := proposalset[1] ∪ {v};
//            trigger < beb, Broadcast | [PROPOSAL, 1, proposalset[1]] >;
//
//        upon event < beb, Deliver | p, [PROPOSAL, r, ps] > do
//            receivedfrom[r] := receivedfrom[r] ∪ {p};
//            proposalset[r] := proposalset[r] ∪ ps;
//
//        upon correct ⊆ receivedfrom[round] ∧ decision = ⊥ do
//            if receivedfrom[round] = receivedfrom[round − 1] then
//                decision := min(proposalset[round]);
//                trigger < beb, Broadcast | [DECIDED, decision] >;
//                trigger < c, Decide | decision >;
//            else
//                round := round + 1;
//                trigger < beb, Broadcast | [PROPOSAL, round, proposalset[round − 1]] >;
//
//        upon event < beb, Deliver | p, [DECIDED, v] > such that p ∈ correct ∧ decision = ⊥ do
//            decision := v;
//            trigger < beb, Broadcast | [DECIDED, decision] >;
//            trigger < c, Decide | decision >;

public class Fuc extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(Fuc.class);

	private Negative<FloodingUniformConsensus> fuc = provides(FloodingUniformConsensus.class);
	private Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
	private Positive<PerfectFailureDetector> pfd = requires(PerfectFailureDetector.class);

    private HashSet<Address> correct;
    private Integer round;
    private Object decision;
    private HashSet<Address> receivedFrom;
    private HashSet<Object> proposalset;

    private Address self;
    private final Integer N;

	public Fuc(FucInit init) {

        N = init.getAllAddresses().size();
        self = init.getSelfAddress();

        correct = new HashSet<>(init.getAllAddresses());
        round = 1;
        decision = null;
        proposalset = new HashSet<>();
        receivedFrom = new HashSet<>();

        subscribe(handlePropose, fuc);
        subscribe(handleFcProposal, beb);
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
            logger.debug("Fuc, handleCrash ");

            logger.info(String.format("    node crashed(address = %s)",
                    crash.getSource()));

            correct.remove(crash.getSource());

            DecideOrMoveToTheNextRound();
        }
    };

    private Handler<Propose> handlePropose = new Handler<Propose>() {
        @Override
        public void handle(Propose event) {
            logger.debug("Fuc, Propose ");

            logger.info(String.format("    Propose message(v = %s)",
                    event.getValue()));

            proposalset.add(event.getValue());

            trigger(new BebBroadcast(new FucProposal(self, 1, proposalset)), beb);
        }
    };

    private void DecideOrMoveToTheNextRound() {
        logger.debug("Fuc, DecideOrMoveToTheNextRound ");

        if (receivedFrom.containsAll(correct) && decision == null){
            if (Objects.equals(round, N)) {
                decision = min(proposalset);
                trigger(new Decide(decision), fuc);
            }
            else {
                logger.debug("Fuc, FucProposal, can't decide in this round: " + round + " moving to round: " + (round + 1));
                round++;
                receivedFrom.clear();
                trigger(new BebBroadcast(new FucProposal(self, round, proposalset)), beb);
            }

        }
    }

    private Handler<FucProposal> handleFcProposal = new Handler<FucProposal>() {
        @Override
        public void handle(FucProposal event) {
            logger.debug("Fuc, FucProposal ");

            logger.info(String.format("    FucProposal message(source = %s, r = %d, ps = %s)",
                    event.getSource(), event.getRound(), event.getPs()));
            logger.info(String.format("    FucProposal message(round = %d, receivedFrom = %s, proposalset = %s)",
                    round, receivedFrom, proposalset));

            Integer r = event.getRound();
            if (Objects.equals(r, round)) {
                receivedFrom.add(event.getSource());
                proposalset.addAll(event.getPs());
            }

            DecideOrMoveToTheNextRound();
        }
    };

}
