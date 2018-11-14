package se.kth.ict.id2203.components.riwmonar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.kth.ict.id2203.ports.riwmonar.ReadImposeWriteMajorityOneNAtomicRegister;
import se.kth.ict.id2203.ports.rowaonrr.ReadRequest;
import se.kth.ict.id2203.ports.rowaonrr.ReadResponse;
import se.kth.ict.id2203.ports.rowaonrr.WriteRequest;
import se.kth.ict.id2203.ports.rowaonrr.WriteResponse;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

import java.util.*;

public class ReadImposeWriteMajority extends ComponentDefinition {

    private static final Logger logger = LoggerFactory.getLogger(ReadImposeWriteMajority.class);

    private Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
    private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
    private Negative<ReadImposeWriteMajorityOneNAtomicRegister> riwmonar = provides(ReadImposeWriteMajorityOneNAtomicRegister.class);

    private Address self;
    private Set<Address> all;

    private Object val;
    private Integer ts, wts, acks, rid;
    private List<ReadInfo> readlist;
    private Object readval;
    private boolean reading;


    public ReadImposeWriteMajority(ReadImposeWriteMajorityInit event) {
        this.self = event.getSelfAddress();
        this.all = event.getAllAddresses();

        this.val = null;
        this.ts = 0;

        this.wts = 0;
        this.acks = 0;
        this.rid = 0;
        this.readlist = new ArrayList<>();
        readval = null;
        reading = false;

        subscribe(handleReadRequest, riwmonar);
        subscribe(handleWriteRequest, riwmonar);

        subscribe(handleWriteRiwmonarMessage, beb);
        subscribe(handleReadRiwmonarMessage, beb);
        subscribe(handleAckRiwmonarMessage, pp2p);
        subscribe(handleValueRiwmonarMessage, pp2p);

    }

    private Handler<WriteRequest> handleWriteRequest = new Handler<WriteRequest>() {
        @Override
        public void handle(WriteRequest event) {
            logger.debug("ReadImposeWriteMajority, handleWriteRequest " + event +
                    " rid " + rid +
                    " wts " + wts + " v " + event.getValue()
            );
            rid++;
            wts++;
            acks = 0;
            trigger(new BebBroadcast(new WriteRiwmonarMessage(self, rid, wts, event.getValue())), beb);
        }
    };

    private Handler<WriteRiwmonarMessage> handleWriteRiwmonarMessage = new Handler<WriteRiwmonarMessage>() {
        @Override
        public void handle(WriteRiwmonarMessage event) {
            logger.debug("ReadImposeWriteMajority, handleWriteRiwmonarMessage" +
                    " source " + event.getSource() +
                    " ts " + ts + " ts' " + event.getWts() +
                    " val " + val + " v' " + event.getVal()
            );
            if (event.getWts() > ts) {
                ts = event.getWts();
                val = event.getVal();
            }

            trigger(new Pp2pSend(event.getSource(), new AckRiwmonarMessage(self, event.getRid())), pp2p);
        }
    };

    private Handler<AckRiwmonarMessage> handleAckRiwmonarMessage = new Handler<AckRiwmonarMessage>() {
        @Override
        public void handle(AckRiwmonarMessage event) {
            logger.debug("ReadImposeWriteMajority, handleAckRiwmonarMessage " + event.getSource() +
                    " wts " + wts +
                    " ts' " + event.getRid()
            );

            if (rid.equals(event.getRid())) {
                acks++;
                logger.debug("ReadImposeWriteMajority, handleAckRiwmonarMessage ts' == wts");
                // check if Majority processes acknowledged
                if (acks > (all.size() / 2)) {
                    acks = 0;
                    logger.debug("ReadImposeWriteMajority, handleAckRiwmonarMessage Majority acked");

                    if (reading) {
                        reading = false;
                        trigger(new ReadResponse(readval), riwmonar);
                    }
                    else {
                        trigger(new WriteResponse(), riwmonar);
                    }
                }
            }

        }
    };

    private Handler<ReadRequest> handleReadRequest = new Handler<ReadRequest>() {
        @Override
        public void handle(ReadRequest event) {
            logger.debug("ReadImposeWriteMajority, ReadRequest " + event +
                    " rid " + rid);

            rid++;
            acks = 0;
            readlist.clear();
            reading = true;

            trigger(new BebBroadcast(new ReadRiwmonarMessage(self, rid)), beb);
        }
    };

    private Handler<ReadRiwmonarMessage> handleReadRiwmonarMessage = new Handler<ReadRiwmonarMessage>() {
        @Override
        public void handle(ReadRiwmonarMessage event) {
            logger.debug("ReadImposeWriteMajority, handleReadRiwmonarMessage" +
                    " source " + event.getSource() +
                    " rid " + rid + " rid' " + event.getRid() +
                    " ts " + ts + " val " + val
            );

            trigger(new Pp2pSend(event.getSource(), new ValueRiwmonarMessage(self, event.getRid(), ts, val)), pp2p);
        }
    };

    private Handler<ValueRiwmonarMessage> handleValueRiwmonarMessage = new Handler<ValueRiwmonarMessage>() {
        @Override
        public void handle(ValueRiwmonarMessage event) {
            logger.debug("ReadImposeWriteMajority, handleValueRiwmonarMessage" +
                    " source " + event.getSource() +
                    " val " + val + " v' " + event.getVal() +
                    " rid " + rid + " rid' " + event.getR() +
                    " ts " + ts + " ts' " + event.getTs()
            );

            if (rid.equals(event.getR())) {
                logger.debug("ReadImposeWriteMajority, handleValueRiwmonarMessage, rid equals rid'");
                readlist.add(new ReadInfo(event.getTs(), event.getVal(), event.getSource().getId()));

                if (readlist.size() > (all.size() / 2)) {
                    logger.debug("ReadImposeWriteMajority, handleValueRiwmonarMessage, rid equals rid', majority acked");
                    ReadInfo temp = Highest();
                    readval = temp.getV();
                    Integer maxts = temp.getTs();
                    readlist.clear();

                    trigger(new BebBroadcast(new WriteRiwmonarMessage(self, rid, maxts, readval)), beb);
                }
            }

        }
    };

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
}
