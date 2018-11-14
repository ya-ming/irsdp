package se.kth.ict.id2203.components.multipaxos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.ports.asc.AbortableSequenceConsensus;
import se.kth.ict.id2203.ports.asc.AscAbort;
import se.kth.ict.id2203.ports.asc.AscDecide;
import se.kth.ict.id2203.ports.asc.AscPropose;
import se.kth.ict.id2203.ports.fpl.FIFOPerfectPointToPointLink;
import se.kth.ict.id2203.ports.fpl.FplSend;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

import java.util.*;

public class MultiPaxos extends ComponentDefinition {

    private static final Logger logger = LoggerFactory.getLogger(MultiPaxos.class);

    private Negative<AbortableSequenceConsensus> asc = provides(AbortableSequenceConsensus.class);
    private Positive<FIFOPerfectPointToPointLink> fpl = requires(FIFOPerfectPointToPointLink.class);

    private Integer t;                      // logic clock
    private Integer prepts;                 // prepared timestamp

    // Acceptor
    private Integer ats;                    // timestamp
    private ArrayList<Object> av;          // value accepted
    private Integer al;                     // length of decided seq

    // Proposer
    private Integer pts;                    // proposer's timestamp
    private ArrayList<Object> pv;          // proposer's value
    private Integer pl;                     // length of learned seq

    private ArrayList<Object> proposedValues;
    private HashMap<Integer, ReadInfo> readlist;
    private HashMap<Integer, Integer> accepted;
    private HashMap<Integer, Integer> decided;
    private final Integer N;

    private Address self;
    private Set<Address> all;

    public MultiPaxos(MultiPaxosInit event) {
        logger.info("Constructing MultiPaxos component.");
/*
        1: upon event < asc, Init > do
        2:  t := 0, . logical clock
        3:  prepts := 0, . acceptor: prepared timestamp
        4:  (ats, av, al ) := (0, <>, 0), . acceptor: timestamp, accepted seq, length of decided seq
        5:  (pts, pv, pl ) := (0, <>, 0), . proposer: timestamp, proposed seq, length of learned seq
        6:  proposedValues := <>, . proposer: values proposed while preparing
        7:  readlist := [_|_]N;
        8:  accepted := [0]N, . proposer's knowledge about length of acceptor's longest accepted seq
        9:  decided := [0]N, . proposer's knowledge about length of acceptor's longest decided seq
*/
        this.t = 0;
        this.prepts = 0;
        this.ats = 0;
        this.av = new ArrayList<>();
        this.al = 0;
        this.pts = 0;
        this.pv = new ArrayList<>();
        this.pl = 0;

        this.proposedValues = new ArrayList<>();

        N = event.getAllAddresses().size();
        all = event.getAllAddresses();
        self = event.getSelfAddress();

        this.readlist = new HashMap<>();
//		ResetReadInfoArrayList(this.readlist);

        this.accepted = new HashMap<>();
        this.decided = new HashMap<>();
//        for (int i = 0; i < N; i++)
//        {
//            decided.add(0);
//            accepted.add(0);
//        }

        subscribe(handleAscPropose, asc);
        subscribe(handlePrepareMessage, fpl);
        subscribe(handlePrepareAckMessage, fpl);
        subscribe(handleAcceptMessage, fpl);
        subscribe(handleAcceptAckMessage, fpl);
        subscribe(handleNackMessage, fpl);
        subscribe(handleDecideMessage, fpl);
    }

    private void ResetIntegerArrayList(ArrayList<Integer> list) {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, 0);
        }
    }

    private void ResetReadInfoArrayList(ArrayList<ReadInfo> list) {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, null);
        }
    }

    private Integer rank(Address s) {
        return s.getId();
    }

//    private ReadInfo Highest() {
//        Collections.sort(readlist, new SortByTs());
//
//        return readlist.get(readlist.size() - 1);
//    }
//
//    class SortByTs implements Comparator {
//        public int compare(Object o1, Object o2) {
//            ReadInfo s1 = (ReadInfo) o1;
//            ReadInfo s2 = (ReadInfo) o2;
//
//            if (s1.getTs() > s2.getTs()) {
//                return 1;
//            } else if (s1.getTs() < s2.getTs()) {
//                return -1;
//            } else {
//                return s1.getNodeId() > s2.getNodeId() ? 1 : -1;
//            }
//        }
//    }


    private Handler<AscPropose> handleAscPropose = new Handler<AscPropose>() {
        @Override
        public void handle(AscPropose event) {
            logger.debug("Paxos, AscPropose ");

            logger.info(String.format("    AscPropose, value:%s",
                    event.getValue()));
            /*
            10: upon event < asc, Propose | v > do
            11:     t := t + 1;
            12:     if pts = 0 then
            13:         pts := t * N + rank(self );
            14:         pv := prefix(av, al );
            15:         pl := 0;
            16:         proposedValues := <v>;
            17:         readlist := [┴]N;
            18:         accepted := [0]N;
            19:         decided := [0]N;
            20:         for all p∈∏ do
            21:             trigger < fpl , Send | p, [Prepare, pts, al , t] >;
            22:     else if #(readlist) <= └N/2┘ then
            23:         proposedValues := proposedValues + <v>, . append to sequence
            24:     else if v !∈ pv then
            25:         pv := pv + <v>;
            26:         for all p∈π such that readlist [p] != ┴ do
            27:             trigger < fpl , Send | p, [Accept, pts, <v>, #(pv) - 1, t] >;
            */

            // proposer选择一个提案编号n并将prepare请求发送给acceptors中的一个多数派
            t = t + 1;
            Object v = event.getValue();

            if (pts == 0) {
                pts = t * N + rank(self);
                pv = new ArrayList<>(av.subList(0, al));

                pl = 0;
                proposedValues.add(v);
//                ResetReadInfoArrayList(readlist);
                readlist.clear();
//                ResetIntegerArrayList(accepted);
//                ResetIntegerArrayList(decided);
                accepted.clear();
                decided.clear();

                for (Address address : all
                        ) {
                    trigger(new FplSend(address, new PrepareMessage(self, pts, al, t)), fpl);
                }

            } else if (readlist.size() <= (N / 2)) {
                proposedValues.add(v);
            } else if (!(pv.contains(v))) {
                pv.add(v);
                for (Address address : all
                        ) {
                    if (readlist.get(address.getId()) != null) {
                        ArrayList<Object> arrayListV = new ArrayList<>();
                        arrayListV.add(v);
                        trigger(new FplSend(address, new AcceptMessage(self, pts, arrayListV, pv.size() - 1, t)), fpl);
                    }
                }
            }
        }
    };

    private Handler<PrepareMessage> handlePrepareMessage = new Handler<PrepareMessage>() {
        @Override
        public void handle(PrepareMessage event) {
            logger.debug("Paxos, PrepareMessage ");

            logger.info(String.format("    PrepareMessage, %s, ts:%d, l:%d, t':%d",
                    event.getSource(), event.getTs(), event.getL(), event.getTPrime()));
            /*
            28: upon event < fpl , Deliver | q, [Prepare, ts, l, t'] > do
            29:     t := max(t, t') + 1;
            30:     if ts < prepts then
            31:         trigger < fpl , Send | q, [Nack, ts, t] >;
            32:     else
            33:         prepts := ts;
            34:         trigger < fpl , Send | q, [PrepareAck, ts, ats, suffix(av, l), al , t] >;
            */

            Integer ts = event.getTs();
            if (t < event.getTPrime()) {
                t = event.getTPrime();
            }
            t = t + 1;

            if (ts < prepts) {
                trigger(new FplSend(event.getSource(), new NackMessage(self, ts, t)), fpl);
            } else {
                prepts = ts;
                trigger(new FplSend(event.getSource(), new PrepareAckMessage(self, ts, ats, new ArrayList<Object>(av.subList(event.getL(), av.size())), al, t)), fpl);
            }


        }
    };

    private Handler<NackMessage> handleNackMessage = new Handler<NackMessage>() {
        @Override
        public void handle(NackMessage event) {
            logger.debug("Paxos, NackMessage ");

            logger.info(String.format("    NackMessage, %s, pts':%d, t':%d",
                    event.getSource(), event.getPtsPrime(), event.getTPrime()));
            /*
            35: upon event < fpl ;Deliver | q, [Nack, pts', t'] > do
            36:     t := max(t, t') + 1;
            37:     if pts' = pts then
            38:         pts := 0;
            39:         trigger < asc, Abort >
            */

            Integer tPrime = event.getTPrime();
            Integer ptsPrime = event.getPtsPrime();
            if (t < tPrime) {
                t = tPrime;
            }
            t = t + 1;

            if (ptsPrime.equals(pts)) {
                pts = 0;
                trigger(new AscAbort(), asc);
            }
        }
    };

    private Handler<PrepareAckMessage> handlePrepareAckMessage = new Handler<PrepareAckMessage>() {
        @Override
        public void handle(PrepareAckMessage event) {
            logger.debug("Paxos, PrepareAckMessage ");

            // Address source, Integer ptsPrime, Integer ts, ArrayList<Object> vsuf, Integer l, Integer tPrime
            logger.info(String.format("    PrepareAckMessage, %s, pts':%d, ts:%d, l:%d, t':%d",
                    event.getSource(), event.getPtsPrime(), event.getTs(), event.getL(), event.getTPrime()));
            logger.info(String.format("     vsuf, size:%d", event.getVsuf().size()));
            for (Object o : event.getVsuf()
                    ) {
                logger.info(String.format("     vsuf, %s", o));
            }
            /*
            40: upon event < fpl , Deliver | q, [PrepareAck, pts', ts, vsuf , l, t'] > do
            41:     t := max(t, t') + 1;
            42:     if pts' = pts then
            43:         readlist[q] := (ts, vsuf );
            44:         decided[q] := l;
            45:         if #(readlist) = └N/2┘ + 1 then
            46:             (ts', vsuf') := (0, <>);
            47:             for all (ts'', vsuf'')∈readlist do
            48:                 if ts' < ts'' || (ts' = ts'' && #(vsuf') < #(vsuf'')) then
            49:                     (ts', vsuf') := (ts'', vsuf'');
            50:             pv := pv + vsuf';
            51:             for all v∈proposedValues such that v !∈ pv do
            52:                 pv := pv + <v>;
            53:             for all p∈π such that readlist [p] != ┴ do
            54:                 l0 := decided[p];
            55:                 trigger < fpl , Send | p, [Accept, pts, suffix(pv, l0), l0, t] >;
            56:         else if #(readlist) > └N/2┘ + 1 then
            57:             trigger < fpl , Send | q, [Accept, pts, suffix(pv, l), l, t] >;
            58:             if pl != 0 then
            59:                 trigger < fpl , Send | q, [Decide, pts, pl , t] >;
            */

            Integer ts = event.getTs();
            Integer ptsPrime = event.getPtsPrime();
            Integer q = event.getSource().getId();
            ArrayList<Object> vsuf = event.getVsuf();
            Integer l = event.getL();

            if (t < event.getTPrime()) {
                t = event.getTPrime();
            }
            t = t + 1;

            if (ptsPrime.equals(pts)) {

                readlist.put(q, new ReadInfo(ts, vsuf));
                decided.put(q, l);

                if (readlist.size() == N / 2 + 1) {
                    ReadInfo riPrime = new ReadInfo(0, new ArrayList<Object>());
                    Iterator iter = readlist.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        Object key = entry.getKey();
                        Object val = entry.getValue();
                        ReadInfo riPrimePrime = (ReadInfo) val;
                        if (
                                riPrime.getTs() < riPrimePrime.getTs() ||
                                        ((riPrime.getTs().equals(riPrimePrime.getTs())) && (riPrime.getVsuf().size() < riPrimePrime.getVsuf().size()))
                                ) {
                            riPrime = riPrimePrime;
                        }
                    }
                    pv.addAll(vsuf);

                    for (Object v : proposedValues
                            ) {
                        if (!(pv.contains(v))) {
                            pv.add(v);
                        }
                    }

                    for (Address address : all
                            ) {
                        if ((readlist.get(address.getId()) != null)) {
                            Integer lPrime = decided.get(address.getId());
                            trigger(new FplSend(address, new AcceptMessage(self, pts, new ArrayList<Object>(pv.subList(lPrime, pv.size())), lPrime, t)), fpl);
                        }
                    }
                } else if (readlist.size() > (N / 2 + 1)) {
                    trigger(new FplSend(event.getSource(), new AcceptMessage(self, pts, new ArrayList<Object>(pv.subList(l, pv.size())), l, t)), fpl);
                    if (!(pl.equals(0))) {
                        trigger(new FplSend(event.getSource(), new DecideMessage(self, pts, pl, t)), fpl);
                    }
                }
            }


        }
    };

    private Handler<AcceptMessage> handleAcceptMessage = new Handler<AcceptMessage>() {
        @Override
        public void handle(AcceptMessage event) {
            logger.debug("Paxos, AcceptMessage ");

//            Address source, Integer ts, ArrayList<Object> vsuf, Integer offs, Integer tPrime
            logger.info(String.format("    AcceptMessage, %s, ts:%d, offs:%d, t':%d",
                    event.getSource(), event.getTs(), event.getOffs(), event.getTPrime()));
            logger.info(String.format("     vsuf, size:%d", event.getVsuf().size()));
            for (Object o : event.getVsuf()
                    ) {
                logger.info(String.format("     vsuf, %s", o));
            }
            /*
            60: upon event < fpl ;Deliver | q, [Accept, ts, vsuf , offs, t'] > do
            61:     t := max(t, t') + 1;
            62:     if ts != prepts then
            63:         trigger < fpl , Send | q, [Nack, ts, t] >;
            64:     else
            65:         ats := ts;
            66:         if offs < #(av) then
            67:             av := prefix(av, offs), . truncate sequence
            68:         av := av + vsuf ;
            69:         trigger < fpl , Send | q, [AcceptAck, ts, #(av), t] >;
            */

            Integer ts = event.getTs();
            Integer tPrime = event.getTPrime();
            Integer offs = event.getOffs();
            ArrayList<Object> vsuf = event.getVsuf();
            if (t < tPrime) {
                t = tPrime;
            }
            t = t + 1;

            if (!(ts.equals(prepts))) {
                trigger(new FplSend(event.getSource(), new NackMessage(self, ts, t)), fpl);
            } else {
                ats = ts;
                if (offs < av.size()) {
                    av = new ArrayList<Object>(av.subList(0, offs));
                }
                av.addAll(vsuf);
                trigger(new FplSend(event.getSource(), new AcceptAckMessage(self, ts, av.size(), t)), fpl);
            }
        }
    };

    private Handler<AcceptAckMessage> handleAcceptAckMessage = new Handler<AcceptAckMessage>() {
        @Override
        public void handle(AcceptAckMessage event) {
            logger.debug("Paxos, AcceptAckMessage ");

//            Address source, Integer ptsPrime, Integer l, Integer tPrime
            logger.info(String.format("    AcceptAckMessage, %s, pts':%d, l:%d, t':%d",
                    event.getSource(), event.getPtsPrime(), event.getL(), event.getTPrime()));
            /*
            70: upon event < fpl ;Deliver | q, [AcceptAck, pts', l, t'] > do
            71:     t := max(t, t') + 1;
            72:     if pts' = pts then
            73:         accepted[q] := l;
            74:         if pl < l ^ #({p∈π | accepted[p] >= l}) > └N/2┘ then
            75:             pl := l;
            76:             for all p∈π such that readlist [p] != _|_ do
            77:                 trigger < fpl , Send | p, [Decide, pts, pl , t] >;
            */

            Integer ptsPrime = event.getPtsPrime();
            Integer tPrime = event.getTPrime();
            Integer l = event.getL();
            if (t < tPrime) {
                t = tPrime;
            }
            t = t + 1;

            if (ptsPrime.equals(pts)) {
                accepted.put(event.getSource().getId(), l);
                if (pl < l) {
                    int counter = 0;
                    for (Address address : all
                            ) {
                        if ((accepted.get(address.getId()) != null) && accepted.get(address.getId()) >= l) {
                            counter++;
                        }
                    }
                    if (counter > (N / 2)) {
                        pl = l;
                        for (Address address : all
                                ) {
                            if (readlist.get(address.getId()) != null) {
                                trigger(new FplSend(address, new DecideMessage(self, pts, pl, t)), fpl);
                            }
                        }
                    }
                }
            }
        }
    };

    private Handler<DecideMessage> handleDecideMessage = new Handler<DecideMessage>() {
        @Override
        public void handle(DecideMessage event) {
            logger.debug("Paxos, DecideMessage ");

//            Address source, Integer ts, Integer l, Integer tPrime
            logger.info(String.format("    DecideMessage, %s, ts:%d, l:%d, t':%d",
                    event.getSource(), event.getTs(), event.getL(), event.getTPrime()));
            /*
            78: upon event < fpl ;Deliver | q, [Decide, ts, l, t'] > do
            79:     t := max(t, t') + 1;
            80:     if ts = prepts then
            81:         while al < l do
            82:             trigger < asc;Decide | av[al] >, . zero-based indexing
            83:             al := al + 1;
            */

            Integer ts = event.getTs();
            Integer tPrime = event.getTPrime();
            Integer l = event.getL();
            if (t < tPrime) {
                t = tPrime;
            }
            t = t + 1;

            if (ts.equals(prepts)) {
                while (al < l) {
                    trigger(new AscDecide(av.get(al)), asc);
                    al = al + 1;
                }
            }
        }
    };
}
