package se.kth.ict.id2203.components.erb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.erb.RbBroadcast;
import se.kth.ict.id2203.ports.erb.EagerReliableBroadcast;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

import java.util.HashSet;
import java.util.Set;

public class EagerRb extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(EagerRb.class);

	private Negative<EagerReliableBroadcast> rb = provides(EagerReliableBroadcast.class);
	private Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);

    private final Address selfAddress;
    private final Set<Address> allAddresses;
    private int seqnum = 0;
    private HashSet<RbDataMessage> delivered;

    public EagerRb(EagerRbInit init) {
        selfAddress = init.getSelfAddress();
        allAddresses = init.getAllAddresses();
        delivered = new HashSet<>();

        subscribe(handleRbBroadcast, rb);
        subscribe(handleRbDataMessage, beb);
	}


    private Handler<RbBroadcast> handleRbBroadcast = new Handler<RbBroadcast>() {
        @Override
        public void handle(RbBroadcast event) {
            // Only turn on below log if test RB. Because RbMessage is not common for CRB test.
//            logger.debug("EagerRb, handleRbBroadcast " + ((RbMessage)(event.getDeliverEvent())).getMessage() + " " + seqnum);
            seqnum++;
            trigger(new BebBroadcast(new RbDataMessage(selfAddress, event.getDeliverEvent(), seqnum)), beb);
        }
    };

    private Handler<RbDataMessage> handleRbDataMessage = new Handler<RbDataMessage>() {
        @Override
        public void handle(RbDataMessage event) {
            // Only turn on below log if test RB. Because RbMessage is not common for CRB test.
//            logger.debug("EagerRb, handleRbDataMessage " + event.getSource() + " " + ((RbMessage)(event.getDeliverEvent())).getMessage() + " " + seqnum);
            if (!delivered.contains(event)) {
                delivered.add(event);
                logger.debug("EagerRb, handleRbDataMessage, deliver&re-broadcast");
                trigger(event.getDeliverEvent(), rb);  // RbMessage is the deliverEvent()
                trigger(new BebBroadcast(event), beb); // Re-broadcast RbDataMessage via Beb
            }
        }
    };
}
