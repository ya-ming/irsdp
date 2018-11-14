package se.kth.ict.id2203.components.riwcmnnar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.ports.riwcmnnar.*;
import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

import java.util.*;

public class ReadImposeWriteConsultMajority extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(ReadImposeWriteConsultMajority.class);

    private Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
    private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
    private Negative<AtomicRegister> ar = provides(AtomicRegister.class);

    private Address self;
    private Set<Address> all;

    private Integer ts;
    private Integer wr;
    private Object val;
    private Integer acks;
    private Integer rid;
    private Object writeval;
    private Object readval;
    private boolean reading;
    private ArrayList<ReadInfo> readlist;

    private ReadInfo Highest() {
        Collections.sort(readlist, new SortByTs());

        return readlist.get(readlist.size() - 1);
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

	public ReadImposeWriteConsultMajority(ReadImposeWriteConsultMajorityInit event) {
        this.self = event.getSelfAddress();
        this.all = event.getAllAddresses();


        /*
        1: upon event h nnar; Init i do
        2:  (ts;wr; val) := (0; 0; 0);
        3:  acks := 0;
        4:  writeval := ?;
        5:  rid := 0;
        6:  readlist := [?]N;
        7:  readval := ?;
        8:  reading := False;
         */
        this.ts = 0;    // timestamp
        this.wr = 0;    // writer identifier, if TSs are equal, use this to break the tie
        this.val = 0;   // value of the register
        this.acks = 0;  // number of acks received
        this.rid = 0;   // relative id, sequence number per process
        this.writeval = 0;  // value to write
        this.readval = 0;   // value read
        this.reading = false;   // in reading or not
        this.readlist = new ArrayList<>();  // list contains all the read results

        subscribe(handleArReadRequest, ar);
        subscribe(handleReadArMessage, beb);

        subscribe(handleArDataMessage, pp2p);

        subscribe(handleArWriteRequest, ar);
        subscribe(handleWriteArMessage, beb);

        subscribe(handleAckMessage, pp2p);
	}

    private Handler<ArReadRequest> handleArReadRequest = new Handler<ArReadRequest>() {
        @Override
        public void handle(ArReadRequest event) {
            logger.debug("ReadImposeWriteConsultMajority, ArReadRequest ");
            /*
            upon event ⟨ nnar, Read ⟩ do
                rid := rid + 1;
                acks := 0;
                readlist := [⊥]N ;
                reading := TRUE;
                trigger ⟨ beb, Broadcast | [READ, rid] ⟩;
            */
            rid++;
            acks = 0;
            readlist.clear();
            reading = true;

            trigger(new BebBroadcast(new ReadArMessage(self, rid)), beb);
        }
    };

    private Handler<ReadArMessage> handleReadArMessage = new Handler<ReadArMessage>() {
        @Override
        public void handle(ReadArMessage event) {
            logger.debug("ReadImposeWriteConsultMajority, handleReadArMessage" +
                            " rid " + rid + " " + event.getRid()  +
                            " ts " + ts +
                            " wr " + wr +
                            " val " + val
            );
            /*
            upon event ⟨ beb, Deliver | p, [READ, r] ⟩ do
                trigger ⟨ pl, Send | p, [VALUE, r, ts, wr, val] ⟩;
            */

            trigger(new Pp2pSend(event.getSource(), new ArDataMessage(event.getSource(), event.getRid(), ts, wr, val)), pp2p);
        }
    };

    private Handler<ArDataMessage> handleArDataMessage = new Handler<ArDataMessage>() {
        @Override
        public void handle(ArDataMessage event) {
            logger.debug("ReadImposeWriteConsultMajority, handleArDataMessage" +
                    " rid " + rid + " " + event.getRid()  +
                    " ts " + ts + " e.ts " + event.getTs() +
                    " wr " + wr + " e.wr " + event.getWr() +
                    " val " + val + " e.val " + event.getVal()
            );

            /*
            upon event ⟨ pl, Deliver | q, [VALUE, r, ts′, wr′, v′] ⟩ such that r = rid do
                readlist[q] := (ts′, wr′, v′);
                if #(readlist) > N/2 then
                    (maxts, rr, readval) := highest(readlist);
                    readlist := [⊥]N ;
                    if reading = TRUE then
                        trigger ⟨ beb, Broadcast | [WRITE, rid, maxts, rr, readval] ⟩;
                    else
                        trigger ⟨ beb, Broadcast | [WRITE, rid, maxts + 1, rank(self), writeval] ⟩;
            */

            if (event.getRid().equals(rid)) {
                readlist.add(new ReadInfo(event.getTs(), event.getWr(), event.getVal(), event.getSource().hashCode()));

                if (readlist.size() > (all.size() / 2)) {
//                    logger.debug("ReadImposeWriteConsultMajority, handleArDataMessage size " + readlist.size() + " " + all.size() / 2);

                    ReadInfo highist = Highest();

                    readval = highist.getV();

                    WriteArMessage wrmsg;
                    if(reading) {
                        wrmsg = new WriteArMessage(self, rid, highist.getTs(), highist.getWr(), readval);
                        trigger(new BebBroadcast(wrmsg), beb);
                    }
                    else {
                        wrmsg = new WriteArMessage(self, rid, highist.getTs() + 1, self.hashCode(), writeval);
                        trigger(new BebBroadcast(wrmsg), beb);
                    }

                    readlist.clear();
                }

            }


        }
    };

    private Handler<ArWriteRequest> handleArWriteRequest = new Handler<ArWriteRequest>() {
        @Override
        public void handle(ArWriteRequest event) {
            logger.debug("ReadImposeWriteConsultMajority, handleArWriteRequest ");

            /*
            upon event ⟨ nnar, Write | v ⟩ do
                rid := rid + 1;
                writeval := v;
                acks := 0;
                readlist := [⊥]N ;
                trigger ⟨ beb, Broadcast | [READ, rid] ⟩;
            */

            rid++;
            acks = 0;
            readlist.clear();
            writeval = event.getValue();

            trigger(new BebBroadcast(new ReadArMessage(self, rid)), beb);
        }
    };

    private Handler<WriteArMessage> handleWriteArMessage = new Handler<WriteArMessage>() {
        @Override
        public void handle(WriteArMessage event) {
            logger.debug("ReadImposeWriteConsultMajority, handleWriteArMessage" +
                    " ts " + ts + " e.ts " + event.getTs() +
                    " wr " + wr + " e.wr " + event.getWr() +
                    " val " + val + " e.val " + event.getVal()
            );

            /*
            upon event ⟨ beb, Deliver | p, [WRITE, r, ts′, wr′, v′] ⟩ do
                if (ts′, wr′) is larger than (ts, wr) then
                    (ts, wr, val) := (ts′, wr′, v′);
                    trigger ⟨ pl, Send | p, [ACK, r] ⟩;
             */

            if ((event.getTs() > ts) || ((event.getTs().equals(ts)) && event.getWr() > wr)) {
                ts = event.getTs();
                wr = event.getWr();
                val = event.getVal();
            }

            trigger(new Pp2pSend(event.getSource(), new AckMessage(self, rid)), pp2p);
        }
    };

    private Handler<AckMessage> handleAckMessage = new Handler<AckMessage>() {
        @Override
        public void handle(AckMessage event) {
            logger.debug("ReadImposeWriteConsultMajority, handleAckMessage ");

            /*
            upon event ⟨ pl, Deliver | q, [ACK, r] ⟩ such that r = rid do
                acks := acks + 1;
                if acks > N/2 then
                    acks := 0;
                    if reading = TRUE then
                        reading := FALSE;
                        trigger ⟨ nnar, ReadReturn | readval ⟩;
                    else
                        trigger ⟨ nnar, WriteReturn ⟩;
             */

            acks++;
            if (acks > (all.size() / 2)) {
                acks = 0;

                if (reading) {
                    reading = false;
                    trigger(new ArReadResponse(readval), ar);
                } else {
                    logger.debug("ReadImposeWriteConsultMajority, handleAckMessage, value written: " + val);
                    trigger(new ArWriteResponse(), ar);
                }
            }
        }
    };
}
