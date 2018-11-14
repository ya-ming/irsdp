package se.kth.ict.id2203.components.huc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.erb.EagerReliableBroadcast;
import se.kth.ict.id2203.ports.erb.RbBroadcast;
import se.kth.ict.id2203.ports.fc.Decide;
import se.kth.ict.id2203.ports.fc.Propose;
import se.kth.ict.id2203.ports.huc.HierarchicalUniformConsensus;
import se.kth.ict.id2203.ports.pfd.Crash;
import se.kth.ict.id2203.ports.pfd.PerfectFailureDetector;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

import java.util.HashSet;
import java.util.Vector;

//Algorithm 5.4: Hierarchical Uniform Consensus
//        Implements:
//            UniformConsensus, instance uc.
//
//        Uses:
//            PerfectPointToPointLinks, instance pl;
//            BestEffortBroadcast, instance beb;
//            ReliableBroadcast, instance rb;
//            PerfectFailureDetector, instance P.
//
//        upon event < uc, Init > do
//            detectedranks := ∅;
//            ackranks := ∅;
//            round := 1;
//            proposal := ⊥; decision := ⊥;
//            proposed := [⊥]N;
//
//        upon event < P, Crash | p > do
//            detectedranks := detectedranks ∪ {rank(p)};
//
//        upon event < uc, Propose | v > such that proposal = ⊥ do
//            proposal := v;
//
//        upon round = rank(self) ∧ proposal = ⊥∧decision = ⊥ do
//            trigger < beb, Broadcast | [PROPOSAL, proposal] >;
//
//        upon event < beb, Deliver | p, [PROPOSAL, v] > do
//            proposed[rank(p)] := v;
//            if rank(p) ≥ round then
//                trigger < pl, Send | p, [ACK] >;
//
//        upon round ∈ detectedranks do
//            if proposed[round] = ⊥ then
//                proposal := proposed[round];
//            round := round + 1;
//
//        upon event < pl, Deliver | q, [ACK] > do
//            ackranks := ackranks ∪ {rank(q)};
//
//        upon detectedranks ∪ ackranks = {1, . . . ,N} do
//            trigger < rb, Broadcast | [DECIDED, proposal] >;
//
//        upon event < rb, Deliver | p, [DECIDED, v] > such that decision = ⊥ do
//            decision := v;
//            trigger < uc, Decide | decision >;

public class Huc extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(Huc.class);

	private Negative<HierarchicalUniformConsensus> Huc = provides(HierarchicalUniformConsensus.class);
	private Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
    private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
	private Positive<PerfectFailureDetector> pfd = requires(PerfectFailureDetector.class);
    private Positive<EagerReliableBroadcast> erb = requires(EagerReliableBroadcast.class);

    private HashSet<Integer> all;

    private HashSet<Integer> detectedRanks;
    private HashSet<Integer> ackRanks;
    private Integer round;
    private Object proposal;
    private Object decision;
    private Vector<Object> proposed;

    private Address self;
    private final Integer N;

	public Huc(HucInit init) {

        N = init.getAllAddresses().size();
        self = init.getSelfAddress();

        all = new HashSet<>();
        for (Address address : init.getAllAddresses()
             ) {
            all.add(rank(address));
        }

        detectedRanks = new HashSet<>();
        ackRanks = new HashSet<>();
        round = 1;
        proposal = null;
        decision = null;
        proposed = new Vector<>();
        for (int i = 0; i <= N; ++i) {
            proposed.addElement(null);
        }

        subscribe(handlePropose, Huc);
        subscribe(handleHucDecided, erb);
        subscribe(handleHucProposal, beb);
        subscribe(handleHucAck, pp2p);
        subscribe(handleCrash, pfd);
    }

    private Integer rank(Address p) {
	    return p.getId();
    }

    private Handler<Crash> handleCrash = new Handler<Crash>() {
        @Override
        public void handle(Crash event) {
            logger.debug("Huc, handleCrash ");

            logger.info(String.format("    node crashed(address = %s)",
                    event.getSource()));

            detectedRanks.add(rank(event.getSource()));

            tryMoveToTheNextRound();
            tryAcks();
        }
    };

    private Handler<Propose> handlePropose = new Handler<Propose>() {
        @Override
        public void handle(Propose event) {
            logger.debug("Huc, Propose ");

            logger.info(String.format("    Propose message(v = %s)",
                    event.getValue()));

            if (proposal == null) {
                proposal = event.getValue();
                tryProposal();
            }
        }
    };

    private void tryProposal() {
        logger.debug("Huc, tryProposal");
        logger.debug("     tryProposal" + " round: " + round +
                " rank(self): " + rank(self) + " proposal: " + proposal);

        if (round == rank(self) && proposal != null && decision == null) {
            trigger(new BebBroadcast(new HucProposal(self, proposal)), beb);
        }
    }

    private Handler<HucProposal> handleHucProposal = new Handler<HucProposal>() {
        @Override
        public void handle(HucProposal event) {
            logger.debug("Huc, handleHucProposal ");
            logger.info(String.format("    handleHucProposal message(source = %s, proposal = %s, r = %s, round = %s)",
                    event.getSource(), event.getProposal(), rank(event.getSource()), round));

            Integer r = rank(event.getSource());
            proposed.set(r, event.getProposal());
            if (r >= round) {
                trigger(new Pp2pSend(event.getSource(), new HucAck(self)), pp2p);
            }

            tryMoveToTheNextRound();
        }
    };


    private void tryMoveToTheNextRound() {
        logger.debug("Huc, tryMoveToTheNextRound");
        logger.debug("     tryMoveToTheNextRound" + " detectedRanks: " + detectedRanks +
                " proposed: " + proposed);
        if (detectedRanks.contains(round)) {
            if (proposed.get(round) != null) {
                proposal = proposed.get(round);
            }
            round++;
            tryProposal();
        }
    }

    private Handler<HucAck> handleHucAck = new Handler<HucAck>() {
        @Override
        public void handle(HucAck event) {
            logger.debug("Huc, handleHucAck");
            logger.debug("     handleHucAck" + " source: " + event.getSource() +
                    " ackRanks: " + ackRanks);
            ackRanks.add(rank(event.getSource()));

            tryAcks();
        }
    };

    private void tryAcks() {
        logger.debug("Huc, tryAcks");
        HashSet<Integer> mergeSet = new HashSet<>(detectedRanks);
        mergeSet.addAll(ackRanks);

        logger.debug("     tryAcks, detectedRanks: " + detectedRanks +  " ackRanks: " + ackRanks + " mergeSet: " + mergeSet);

        if (all.containsAll(mergeSet)) {
            trigger(new RbBroadcast(new HucDecided(self, proposal)), erb);
        }
    }

    private Handler<HucDecided> handleHucDecided = new Handler<HucDecided>() {
        @Override
        public void handle(HucDecided event) {
            logger.debug("Huc, handleHucDecided ");
            logger.info(String.format("    handleHucDecided message(source = %s, proposal = %s)",
                    event.getSource(), event.getProposal()));

            if (decision == null) {
                decision = event.getProposal();
                trigger(new Decide(decision), Huc);
            }
        }
    };
}
