package se.kth.ict.id2203.components.ac;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.ports.ac.AbortableConsensus;
import se.kth.ict.id2203.ports.ac.AcAbort;
import se.kth.ict.id2203.ports.ac.AcDecide;
import se.kth.ict.id2203.ports.ac.AcPropose;
import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.kth.ict.id2203.tools.MessageLogger;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

public class Paxos extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(Paxos.class);

	private Negative<AbortableConsensus> ac = provides(AbortableConsensus.class);
	private Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
	private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);

	private Integer t;                      // logic clock
	private Integer prepts;                 // prepared timestamp
    private Integer ats;                    // timestamp
    private Object av;                     // value accepted
    private Integer pts;                    // proposer's timestamp
    private Object pv;                     // proposer's value
    private ArrayList<ReadInfo> readlist;
    private Integer acks;
    private final Integer N;

    private Address self;
    private HashSet<Address> all;

	public Paxos(PaxosInit init) {
        this.t = 0;
        this.prepts = 0;
        this.ats = 0;
        this.av = null;
        this.pts = 0;
        this.pv = null;
        this.readlist = new ArrayList<>();
        this.acks = 0;
        N = init.getAllAddresses().size();
        self = init.getSelfAddress();
        all = new HashSet<>(init.getAllAddresses());

        subscribe(handleAcPropose, ac);
        subscribe(handlePrepare, beb);
        subscribe(handleNackMessage, pp2p);
        subscribe(handlPrepareAckMessage, pp2p);
        subscribe(handleAcceptMessage, beb);
        subscribe(handleAcceptAckMessage, pp2p);
	}

	private Integer rank(Address s) {
	    return s.getId();
    }

    private ReadInfo Highest() {
        Collections.sort(readlist, new SortByTs());

        return readlist.get(readlist.size() - 1);
    }

    private void logLocalVariables() {
//        this.t = 0;
//        this.prepts = 0;
//        this.ats = 0;
//        this.av = null;
//        this.pts = 0;
//        this.pv = null;
//        this.acks = 0;

        logger.trace(String.format("%d rnote over Paxos%d@@" +
                        "t:%d, prepts:%d, ats:%d, av:%s, pts:%d, pv:%s, acks:%d" +
                        "@@endrnote",
                System.nanoTime(),
                self.getId(), t, prepts, ats, av, pts, pv, acks)
        );
    }

    class SortByTs implements Comparator {
        public int compare(Object o1, Object o2) {
            ReadInfo s1 = (ReadInfo) o1;
            ReadInfo s2 = (ReadInfo) o2;

            if (s1.getTs() > s2.getTs()) {
                return 1;
            } else if (s1.getTs() < s2.getTs()) {
                return -1;
            } else {
                return s1.getNodeId() > s2.getNodeId() ? 1 : -1;
            }
        }
    }

    private Handler<AcPropose> handleAcPropose = new Handler<AcPropose>() {
        @Override
        public void handle(AcPropose event) {
            logger.debug("Paxos, AcPropose ");

            logger.info(String.format("    AcPropose message(v = %s)",
                    event.getValue()));

            logLocalVariables();
            MessageLogger.logMessageIn(logger, "Client", self, "Paxos", self, event);

            /*
                8: upon event < ac, Propose | v > do
                9:      t := t + 1;
                10:     pts := t * N + rank(self );
                11:     pv := v;
                12:     readlist := [_|_]N;
                13:     acks := 0;
                14:     trigger < beb,Broadcast | [PrepareMessage, pts, t] >;
            */

            // proposer选择一个提案编号n并将prepare请求发送给acceptors中的一个多数派
            t = t + 1;
            pts = t * N + rank(self);
            pv = event.getValue();
            readlist.clear();
            acks = 0;

            PrepareMessage prepareMessage = new PrepareMessage(self, pts, t);
            MessageLogger.logMessageOut(logger, "Paxos", self, "Paxos", all, prepareMessage);
            trigger(new BebBroadcast(prepareMessage), beb);
            logLocalVariables();
        }
    };

    private Handler<PrepareMessage> handlePrepare = new Handler<PrepareMessage>() {
        @Override
        public void handle(PrepareMessage event) {
            logger.debug("Paxos, PrepareMessage ");

            logger.info(String.format("    PrepareMessage message(source = %s, pts = %d, t = %d)",
                    event.getSource(), event.getPts(), event.getT()));

            logLocalVariables();
            MessageLogger.logMessageIn(logger, "Paxos", event.getSource(), "Paxos", self, event);

            /*
                15: upon event < beb;Deliver | q; [PrepareMessage; ts; t'] > do
                16:     t := max(t; t') + 1;
                17:     if ts < prepts then
                18:         trigger < pp2p; Send | q; [Nack; ts; t] >;
                19:     else
                20:         prepts := ts;
                21:         trigger < pp2p; Send | q; [PrepareAck; ats; av; ts; t] >;
            */

            // acceptor收到prepare消息后，如果提案的编号大于它已经回复的所有prepare消息，
            // 则acceptor将自己上次接受的提案回复给proposer，并承诺不再回复小于n的提案；
            if (t < event.getT()) {
                t = event.getT();
            }
            t++;
//            logger.info(String.format("    PrepareMessage message send(source = %s, ts = %d, t = %d)",
//                    self, event.getPts(), t));

            Integer ts = event.getPts();
            if (ts < prepts) {
                NackMessage nackMessage = new NackMessage(self, ts, t);
                MessageLogger.logMessageOut(logger, "Paxos", self, "Paxos", event.getSource(), nackMessage);
                trigger(new Pp2pSend(event.getSource(), nackMessage), pp2p);
            } else {
                prepts = ts;
                PrepareAckMessage prepareAckMessage = new PrepareAckMessage(self, ats, av, ts, t);
                MessageLogger.logMessageOut(logger, "Paxos", self, "Paxos", event.getSource(), prepareAckMessage);
                trigger(new Pp2pSend(event.getSource(), prepareAckMessage), pp2p);
            }
            logLocalVariables();
        }
    };

    private Handler<NackMessage> handleNackMessage = new Handler<NackMessage>() {
        @Override
        public void handle(NackMessage event) {
            logger.debug("Paxos, NackMessage ");
            logger.info(String.format("    NackMessage message(source = %s, ts = %d, t = %d)",
                    event.getSource(), event.getTs(), event.getT()));

            logLocalVariables();
            MessageLogger.logMessageIn(logger, "Paxos", event.getSource(), "Paxos", self, event);
            /*
                22: upon event < pp2p;Deliver | q; [Nack; pts'; t'] > do
                23:     t := max(t; t') + 1;
                24: if pts' = pts then
                25:     pts := 0;
                26:     trigger < ac; Abort >
            */

            if (t < event.getT()) {
                t = event.getT();
            }
            t++;

            Integer ts = event.getTs();
            if (ts.equals(pts)) {
                pts = 0;

                AcAbort acAbort = new AcAbort();
                MessageLogger.logMessageOut(logger, "Paxos", self, "Client", self, acAbort);
                trigger(new AcAbort(), ac);
            }
            logLocalVariables();
        }
    };

    private Handler<PrepareAckMessage> handlPrepareAckMessage = new Handler<PrepareAckMessage>() {
        @Override
        public void handle(PrepareAckMessage event) {
            logger.debug("Paxos, PrepareAckMessage ");

            logger.info(String.format("    PrepareAckMessage message(source = %s, ats = %d, av = %s, ts = %d, t = %d)",
                    event.getSource(), event.getAts(), event.getAv(), event.getTs(), event.getT()));

            logLocalVariables();
            MessageLogger.logMessageIn(logger, "Paxos", event.getSource(), "Paxos", self, event);

            /*
                27: upon event < pp2p;Deliver | q; [PrepareAck; ts; v; pts'; t'] > do
                28:     t := max(t; t') + 1;
                29:     if pts‘ = pts then
                30:         readlist[q] := (ts; v);
                31:         if #(readlist) > N/2 then
                32:             (ts; v) := highest(readlist); . pair with greatest timestamp
                33:             if ts != 0 then
                34:                 pv := v;
                35:             readlist := [_|_]N;
                36:             trigger < beb;Broadcast | [Accept; pts; pv; t] >;
            */

            // 当一个proposer收到了多数acceptors对prepare的回复后，就进入批准阶段。
            // 它要向回复prepare请求的acceptors发送accept请求，
            // 包括编号n和根据P2c决定的value（如果根据P2c没有已经接受的value，那么它可以自由决定value）
            if (t < event.getT()) {
                t = event.getT();
            }
            t++;

            // 这个ack是针对我当前的proposal的(event.getTs() == pts)
            if (event.getTs().equals(pts)) {
                readlist.add(new ReadInfo(event.getAts(), event.getAv(), event.getSource().hashCode()));

                if (readlist.size() > N/2) {
                    ReadInfo highest = Highest();
                    if (!(highest.getTs().equals(0))) {
                        pv = highest.getV();
                    }

                    readlist.clear();

                    AcceptMessage acceptMessage = new AcceptMessage(self, pts, pv, t);
                    MessageLogger.logMessageOut(logger, "Paxos", self, "Paxos", all, acceptMessage);
                    trigger(new BebBroadcast(acceptMessage), beb);
                }
            }
            logLocalVariables();
        }
    };

    private Handler<AcceptMessage> handleAcceptMessage = new Handler<AcceptMessage>() {
        @Override
        public void handle(AcceptMessage event) {
            logger.debug("Paxos, AcceptMessage ");

            logger.info(String.format("    AcceptMessage message(source = %s, pts = %d, pv = %s, t = %d)",
                    event.getSource(), event.getPts(), event.getPv(), event.getT()));

            logLocalVariables();
            MessageLogger.logMessageIn(logger, "Paxos", event.getSource(), "Paxos", self, event);
            /*
                37: upon event < beb;Deliver | q; [Accept; ts; v; t'] > do
                38:     t := max(t; t') + 1;
                39:     if ts < prepts then
                40:         trigger < pp2p; Send | q; [Nack; ts; t] >;
                41:     else
                42:         ats := prepts := ts;
                43:         av := v;
                44:         trigger < pp2p; Send | q; [AcceptAck; ts; t] >;
            */

            // 在不违背自己向其他proposer的承诺的前提下，acceptor收到accept请求后即接受这个请求
            if (t < event.getT()) {
                t = event.getT();
            }
            t++;

            Integer ts = event.getPts();
            if (ts < prepts) {
                NackMessage nackMessage = new NackMessage(self, ts, t);
                MessageLogger.logMessageOut(logger, "Paxos", self, "Paxos", event.getSource(), nackMessage);
                trigger(new Pp2pSend(event.getSource(), nackMessage), pp2p);
            } else {
                ats = prepts = ts;
                av = event.getPv();
                AcceptAckMessage acceptAckMessage = new AcceptAckMessage(self, ts, t);
                MessageLogger.logMessageOut(logger, "Paxos", self, "Paxos", event.getSource(), acceptAckMessage);
                trigger(new Pp2pSend(event.getSource(), acceptAckMessage), pp2p);
            }
            logLocalVariables();
        }
    };

    private Handler<AcceptAckMessage> handleAcceptAckMessage = new Handler<AcceptAckMessage>() {
        @Override
        public void handle(AcceptAckMessage event) {
            logger.debug("Paxos, AcceptAckMessage ");

            logger.info(String.format("    AcceptAckMessage message(source = %s, ts = %d, t = %d)",
                    event.getSource(), event.getTs(), event.getT()));

            logLocalVariables();
            MessageLogger.logMessageIn(logger, "Paxos", event.getSource(), "Paxos", self, event);

            /*
                45: upon event < pp2p;Deliver | q; [AcceptAck; pts'; t'] > do
                46:     t := max(t; t') + 1;
                47:     if pts' = pts then
                48:         acks := acks + 1;
                49:         if acks > N/2 then
                50:             pts := 0;
                51:             trigger < ac;Return | pv >;
            */

            if (t < event.getT()) {
                t = event.getT();
            }
            t++;

            Integer ts = event.getTs();
            if (ts.equals(pts)) {
                acks += 1;
                if (acks > N/2) {
                    pts = 0;

                    AcDecide acDecide = new AcDecide(pv);
                    MessageLogger.logMessageOut(logger, "Paxos", self, "Client", self, acDecide);

                    trigger(acDecide, ac);
                }
            }
            logLocalVariables();
        }
    };
}
