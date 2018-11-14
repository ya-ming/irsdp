package se.kth.ict.id2203.components.frb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.pa.broadcast.FRbMessage;
import se.kth.ict.id2203.ports.erb.EagerReliableBroadcast;
import se.kth.ict.id2203.ports.erb.RbBroadcast;
import se.kth.ict.id2203.ports.frb.FIFOReliableBroadcast;
import se.kth.ict.id2203.ports.frb.FRbBroadcast;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

//Algorithm 3.12: Broadcast with Sequence Number
//        Implements:
//            FIFOReliableBroadcast, instance frb.
//
//        Uses:
//            ReliableBroadcast, instance rb.
//
//        upon event < frb, Init > do
//            lsn := 0;
//            pending := ∅;
//            next := [1]N;
//
//        upon event < frb, Broadcast | m > do
//            lsn := lsn + 1;
//            trigger < rb, Broadcast | [DATA, self,m, lsn] >;
//
//        upon event < rb, Deliver | p, [DATA, s,m, sn] > do
//            pending := pending ∪ {(s,m, sn)};
//            while exists (s,m, sn) ∈ pending such that sn = next[s] do
//                next[s] := next[s] + 1;
//                pending := pending \ {(s,m, sn)};
//                trigger < frb, Deliver | s, m >;

public class FIFORb extends ComponentDefinition {

    private static final Logger logger = LoggerFactory.getLogger(FIFORb.class);
    private final Address selfAddress;
    private final Set<Address> allAddresses;
    Vector<Integer> next;
    private Negative<FIFOReliableBroadcast> frb = provides(FIFOReliableBroadcast.class);
    private Positive<EagerReliableBroadcast> erb = requires(EagerReliableBroadcast.class);
    private int lsn = 0;
    private HashSet<FRbDataMessage> pending;
    private Handler<FRbBroadcast> handleFRbBroadcast = new Handler<FRbBroadcast>() {
        @Override
        public void handle(FRbBroadcast event) {
            // Only turn on below log if test RB. Because FRbDataMessage is not common for FRB test.
            logger.debug("FIFORb, handleRbBroadcast " + ((FRbMessage) (event.getDeliverEvent())).getMessage() + " " + lsn);
            lsn++;
            trigger(new RbBroadcast(new FRbDataMessage(selfAddress, event.getDeliverEvent(), lsn)), erb);
        }
    };
    private Handler<FRbDataMessage> handleFRbDataMessage = new Handler<FRbDataMessage>() {
        @Override
        public void handle(FRbDataMessage event) {
            // Only turn on below log if test RB. Because FRbMessage is not common for FRB test.
            logger.debug("FIFORb, handleFRbDataMessage " + event.getSource() + " " + ((FRbMessage) (event.getDeliverEvent())).getMessage() + " " + lsn);

            // add the message in the pending set
            pending.add(event);

            logger.debug("FIFORb, handleFRbDataMessage, pending: " + pending);
            logger.debug("FIFORb, handleFRbDataMessage, next: " + next);

            // walk through the pending set
            // deliver message if possible
            // increasing the sequence number in the 'next'
            // walk through the pending set again and try to deliver the next message if possible
            boolean retry;
            do {
                retry = false;
                for (FRbDataMessage frbDataMessage : pending
                        ) {
                    int sPrime = frbDataMessage.getSource().getId();
                    int snPrime = frbDataMessage.getSeqnum();
                    logger.debug("FIFORb, handleFRbDataMessage, processing: " + frbDataMessage +
                            " sPrime: " + sPrime + " snPrime: " + snPrime);
                    if (frbDataMessage.getSeqnum() == next.get(sPrime)) {
                        next.set(sPrime, snPrime + 1);
                        pending.remove(frbDataMessage);

                        logger.debug("FIFORb, handleFRbDataMessage, deliver");
                        trigger(frbDataMessage.getDeliverEvent(), frb);  // FRbMessage is the deliverEvent()
                        retry = true;
                        break;
                    }
                }
            } while (retry == true);
        }
    };

    public FIFORb(FIFORbInit init) {
        selfAddress = init.getSelfAddress();
        allAddresses = init.getAllAddresses();
        pending = new HashSet<>();
        next = new Vector<>();
        for (int i = 0; i < allAddresses.size() + 1; ++i) {
            next.add(i, 1);
        }

        subscribe(handleFRbBroadcast, frb);
        subscribe(handleFRbDataMessage, erb);
    }
}
