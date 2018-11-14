package se.kth.ict.id2203.components.hc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.fc.Decide;
import se.kth.ict.id2203.ports.fc.Propose;
import se.kth.ict.id2203.ports.hc.HierarchicalConsensus;
import se.kth.ict.id2203.ports.pfd.Crash;
import se.kth.ict.id2203.ports.pfd.PerfectFailureDetector;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

import java.util.HashSet;
import java.util.Vector;

//Algorithm 5.2: Hierarchical Consensus
//        Implements:
//            Consensus, instance c.
//
//        Uses:
//            BestEffortBroadcast, instance beb;
//            PerfectFailureDetector, instance P.
//
//        upon event < c, Init > do
//            detectedranks := ∅;
//            round := 1;
//            proposal := ⊥; proposer := 0;
//            delivered := [FALSE]N;
//            broadcast := FALSE;
//            upon event < P, Crash | p > do
//            detectedranks := detectedranks ∪ {rank(p)};
//
//        upon event < c, Propose | v > such that proposal = ⊥ do
//            proposal := v;
//
//        upon round = rank(self) ∧ proposal = ⊥∧broadcast = FALSE do
//            broadcast := TRUE;
//            trigger < beb, Broadcast | [DECIDED, proposal] >;
//            trigger < c, Decide | proposal >;
//
//        upon round ∈ detectedranks ∨ delivered[round] = TRUE do
//            round := round + 1;
//
//        upon event < beb, Deliver | p,[DECIDED, v] > do
//            r := rank(p);
//            if r < rank(self) ∧r > proposer then
//                proposal := v;
//                proposer := r;
//            delivered[r] := TRUE;

public class Hc extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(Hc.class);

	private Negative<HierarchicalConsensus> Hc = provides(HierarchicalConsensus.class);
	private Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
	private Positive<PerfectFailureDetector> pfd = requires(PerfectFailureDetector.class);

    private HashSet<Address> correct;

    private HashSet<Integer> detectedRanks;
    private Integer round;
    private Object proposal;
    private Integer proposer;
    private Vector<Boolean> delivered;
    private Boolean broadcast;

    private Address self;
    private final Integer N;

	public Hc(HcInit init) {

        N = init.getAllAddresses().size();
        self = init.getSelfAddress();

        correct = new HashSet<>(init.getAllAddresses());

        detectedRanks = new HashSet<>();
        round = 1;
        proposal = null;
        proposer = 0;
        delivered = new Vector<>();
        for (int i = 0; i <= N; ++i) {
            delivered.addElement(Boolean.FALSE);
        }
        broadcast = Boolean.FALSE;

        subscribe(handlePropose, Hc);
        subscribe(handleHcDecided, beb);
        subscribe(handleCrash, pfd);
    }

    private Integer rank(Address p) {
	    return p.getId();
    }

    private Handler<Crash> handleCrash = new Handler<Crash>() {
        @Override
        public void handle(Crash event) {
            logger.debug("Hc, handleCrash ");

            logger.info(String.format("    node crashed(address = %s)",
                    event.getSource()));

            correct.remove(event.getSource());

            detectedRanks.add(rank(event.getSource()));

            tryMoveToTheNextRound();
        }
    };

    private Handler<Propose> handlePropose = new Handler<Propose>() {
        @Override
        public void handle(Propose event) {
            logger.debug("Hc, Propose ");

            logger.info(String.format("    Propose message(v = %s)",
                    event.getValue()));

            if (proposal == null) {
                proposal = event.getValue();
                tryDecide();
            }
        }
    };

    private void tryDecide() {
        logger.debug("Hc, tryDecide");
        logger.debug("    tryDecide" + " round: " + round +
        " rank(self): " + rank(self) + " proposal: " + proposal + " broadcast: " + broadcast);

        if (round == rank(self) && proposal != null && broadcast == false) {
            broadcast = true;
            trigger(new BebBroadcast(new HcDecided(self, proposal)), beb);
            trigger(new Decide(proposal), Hc);
        }
    }

    private void tryMoveToTheNextRound() {
        logger.debug("Hc, tryMoveToTheNextRound");
        logger.debug("    tryMoveToTheNextRound" + " detectedRanks: " + detectedRanks +
                " delivered: " + delivered);

        if (round < N)
            if (detectedRanks.contains(round) || delivered.get(round) == true) {
                round++;
            }

        tryDecide();
    }

    private Handler<HcDecided> handleHcDecided = new Handler<HcDecided>() {
        @Override
        public void handle(HcDecided event) {
            logger.debug("Hc, handleHcDecided ");
            logger.info(String.format("    handleHcDecided message(source = %s, proposal = %s)",
                    event.getSource(), event.getProposal()));

            Integer r = rank(event.getSource());
            if (r < rank(self) && r > proposer) {
                proposal = event.getProposal();
                proposer = r;
            }
            delivered.set(r, true);

            tryDecide();
            tryMoveToTheNextRound();
        }
    };
}
