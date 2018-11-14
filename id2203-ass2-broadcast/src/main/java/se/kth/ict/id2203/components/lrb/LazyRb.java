package se.kth.ict.id2203.components.lrb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.pa.broadcast.LRbMessage;
import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.lrb.LRbBroadcast;
import se.kth.ict.id2203.ports.lrb.LazyReliableBroadcast;
import se.kth.ict.id2203.ports.pfd.Crash;
import se.kth.ict.id2203.ports.pfd.PerfectFailureDetector;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class LazyRb extends ComponentDefinition {

    private static final Logger logger = LoggerFactory.getLogger(LazyRb.class);

    private Negative<LazyReliableBroadcast> lrb = provides(LazyReliableBroadcast.class);
    private Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
    private Positive<PerfectFailureDetector> pfd = requires(PerfectFailureDetector.class);

    private final Address selfAddress;
    private final Set<Address> allAddresses;
    private int seqnum = 0;
    private HashSet<Address> correct;
    private HashMap<Address, HashSet<LRbDataMessage>> from;

    public LazyRb(LazyRbInit init) {
        selfAddress = init.getSelfAddress();
        allAddresses = init.getAllAddresses();
        correct = new HashSet<>();
        correct.addAll(allAddresses);

        from = new HashMap<>();

        for (Address address: allAddresses
             ) {
            from.put(address, new HashSet<>());
        }

        subscribe(handleLRbBroadcast, lrb);
        subscribe(handleLRbDataMessage, beb);
        subscribe(handleCrash, pfd);
    }
    private Handler<Crash> handleCrash = new Handler<Crash>() {
        @Override
        public void handle(Crash event) {
            logger.info("LazyRb, handleCrash Process {} crashed", event.getSource());
            correct.remove(event.getSource());

            for (LRbDataMessage m:from.get(event.getSource())
                 ) {
                logger.debug("LazyRb, handleLRbDataMessage, re-broadcast all messages because source crashed");
                trigger(new BebBroadcast(m), beb); // Re-broadcast RbDataMessage via Beb because source crashed
            }
        }
    };

    private Handler<LRbBroadcast> handleLRbBroadcast = new Handler<LRbBroadcast>() {
        @Override
        public void handle(LRbBroadcast event) {
            // Only turn on below log if test RB. Because LRbMessage is not common for CRB test.
            logger.debug("LazyRb, handleLRbBroadcast " + ((LRbMessage)(event.getDeliverEvent())).getMessage() + " " + seqnum);
            seqnum++;
            trigger(new BebBroadcast(new LRbDataMessage(selfAddress, event.getDeliverEvent(), seqnum)), beb);
        }
    };

    private Handler<LRbDataMessage> handleLRbDataMessage = new Handler<LRbDataMessage>() {
        @Override
        public void handle(LRbDataMessage event) {
            // Only turn on below log if test RB. Because LRbMessage is not common for CRB test.
            logger.debug("LazyRb, handleLRbDataMessage " + event.getSource() + " " + ((LRbMessage)(event.getDeliverEvent())).getMessage() + " " + seqnum);
            if (from.get(event.getSource()).contains(event) == false) {
                logger.debug("LazyRb, handleLRbDataMessage, deliver");
                trigger(event.getDeliverEvent(), lrb);  // LRbMessage is the deliverEvent()

                from.get(event.getSource()).add(event);
                if (correct.contains(event.getSource()) == false) {
                    logger.debug("LazyRb, handleLRbDataMessage, re-broadcast because source crashed");
                    trigger(new BebBroadcast(event), beb); // Re-broadcast RbDataMessage via Beb because source crashed
                }
            }
        }
    };
}
