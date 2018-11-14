package se.kth.ict.id2203.components.mvrr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.mvrr.MajorityVotingRegularRegister;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.kth.ict.id2203.ports.rowaonrr.*;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

import java.util.*;

public class MajorityVoting extends ComponentDefinition {

    private static final Logger logger = LoggerFactory.getLogger(MajorityVoting.class);

    private Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
    private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
    private Negative<MajorityVotingRegularRegister> mvrr = provides(MajorityVotingRegularRegister.class);

    private Address self;
    private Set<Address> all;

    private Object val;
    private Integer ts, wts, acks, rid;
    private List<ReadInfo> readlist;


    public MajorityVoting(MajorityVotingInit event) {
        this.self = event.getSelfAddress();
        this.all = event.getAllAddresses();

        this.val = null;
        this.ts = 0;

        this.wts = 0;
        this.acks = 0;
        this.rid = 0;
        this.readlist = new ArrayList<>();

        subscribe(handleReadRequest, mvrr);
        subscribe(handleWriteRequest, mvrr);

        subscribe(handleWriteMvrrMessage, beb);
        subscribe(handleReadMvrrMessage, beb);
        subscribe(handleAckMvrrMessage, pp2p);
        subscribe(handleValueMvrrMessage, pp2p);

    }

    private Handler<WriteRequest> handleWriteRequest = new Handler<WriteRequest>() {
        @Override
        public void handle(WriteRequest event) {
            logger.debug("MajorityVoting, handleWriteRequest " + event + " wts " + wts + " val " + event.getValue());
            wts++;
            acks = 0;
            trigger(new BebBroadcast(new WriteMvrrMessage(self, wts, event.getValue())), beb);
        }
    };

    private Handler<WriteMvrrMessage> handleWriteMvrrMessage = new Handler<WriteMvrrMessage>() {
        @Override
        public void handle(WriteMvrrMessage event) {
            logger.debug("MajorityVoting, handleWriteMvrrMessage" +
                    " source " + event.getSource() +
                    " ts " + ts + " ts' " + event.getWts() +
                    " val " + val + " v' " + event.getVal()
            );
            if (event.getWts() > ts) {
                ts = event.getWts();
                val = event.getVal();
            }

            trigger(new Pp2pSend(event.getSource(), new AckMvrrMessage(self, event.getWts())), pp2p);
        }
    };

    private Handler<AckMvrrMessage> handleAckMvrrMessage = new Handler<AckMvrrMessage>() {
        @Override
        public void handle(AckMvrrMessage event) {
            logger.debug("MajorityVoting, handleAckMvrrMessage " + event.getSource() +
                    " wts " + wts +
                    " ts' " + event.getTs()
            );

            if (wts.equals(event.getTs())) {
                acks++;
                logger.debug("MajorityVoting, handleAckMvrrMessage ts' == wts");
                // check if Majority processes acknowledged
                if (acks > (all.size() / 2)) {
                    acks = 0;
                    logger.debug("MajorityVoting, handleAckMvrrMessage Majority acked");
                    trigger(new WriteResponse(), mvrr);
                }
            }

        }
    };

    private Handler<ReadRequest> handleReadRequest = new Handler<ReadRequest>() {
        @Override
        public void handle(ReadRequest event) {
            logger.debug("MajorityVoting, ReadRequest " + event +
                    " rid " + rid);

            rid++;
            readlist.clear();

            trigger(new BebBroadcast(new ReadMvrrMessage(self, rid)), beb);
        }
    };

    private Handler<ReadMvrrMessage> handleReadMvrrMessage = new Handler<ReadMvrrMessage>() {
        @Override
        public void handle(ReadMvrrMessage event) {
            logger.debug("MajorityVoting, handleReadMvrrMessage" +
                    " source " + event.getSource() +
                    " rid " + rid + " rid' " + event.getRid()
            );

            trigger(new Pp2pSend(event.getSource(), new ValueMvrrMessage(self, event.getRid(), ts, val)), pp2p);
        }
    };

    private Handler<ValueMvrrMessage> handleValueMvrrMessage = new Handler<ValueMvrrMessage>() {
        @Override
        public void handle(ValueMvrrMessage event) {
            logger.debug("MajorityVoting, handleValueMvrrMessage" +
                    " source " + event.getSource() +
                    " val " + val + " v' " + event.getVal() +
                    " rid " + rid + " rid' " + event.getR() +
                    " ts " + ts + " ts' " + event.getTs()
            );

            if (rid.equals(event.getR())) {
                logger.debug("MajorityVoting, handleValueMvrrMessage, rid equals rid'");
                readlist.add(new ReadInfo(event.getTs(), event.getVal(), event.getSource().getId()));

                if (readlist.size() > (all.size() / 2)) {
                    logger.debug("MajorityVoting, handleValueMvrrMessage, rid equals rid', majority acked");
                    Object v = Highest().getV();
                    readlist.clear();

                    trigger(new ReadResponse(v), mvrr);
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
