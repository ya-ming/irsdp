package se.kth.ict.id2203.components.nwcrb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.pa.broadcast.NWCrbMessage;
import se.kth.ict.id2203.ports.erb.EagerReliableBroadcast;
import se.kth.ict.id2203.ports.erb.RbBroadcast;
import se.kth.ict.id2203.ports.nwcrb.NWCrbBroadcast;
import se.kth.ict.id2203.ports.nwcrb.NWCrbDeliver;
import se.kth.ict.id2203.ports.nwcrb.NoWaitingCausalOrderReliableBroadcast;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

//Algorithm 3.13: No-Waiting Causal Broadcast
//        Implements:
//            CausalOrderReliableBroadcast, instance crb.
//        Uses:
//            ReliableBroadcast, instance rb.
//
//        upon event < crb, Init > do
//            delivered := ∅;
//            past := [];
//
//        upon event < crb, Broadcast | m > do
//            trigger < rb, Broadcast | [DATA, past,m] >;
//            append(past, (self,m));
//
//        upon event < rb, Deliver | p, [DATA, mpast,m] > do
//            if m ∉ delivered then
//                forall (s, n) ∈ mpast do // by the order in the list
//                if n ∉ delivered then
//                    trigger < crb, Deliver | s, n >;
//                    delivered := delivered ∪ {n};
//                    if (s, n) ∉ past then
//                        append(past, (s, n));
//            trigger < crb, Deliver | p, m >;
//            delivered := delivered ∪ {m};
//            if (p,m) ∉ past then
//                append(past, (p,m));

public class NoWaitingCrb extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(NoWaitingCrb.class);

	Negative<NoWaitingCausalOrderReliableBroadcast> nwcrb = provides(NoWaitingCausalOrderReliableBroadcast.class);
	Positive<EagerReliableBroadcast> rb = requires(EagerReliableBroadcast.class);

	private final Address selfAddress;
	private final Set<Address> allAddresses;

	private Vector<NWCrbDeliver> past;
	private HashSet<NWCrbDeliver> delivered;

	public NoWaitingCrb(NoWaitingCrbInit init) {
        selfAddress = init.getSelfAddress();
        allAddresses = init.getAllAddresses();
        past = new Vector<>();
        delivered = new HashSet<>();

        subscribe(handleNWCrbBroadcast, nwcrb);
        subscribe(handleNWCrbDataMessage, rb);
	}

    private Handler<NWCrbBroadcast> handleNWCrbBroadcast = new Handler<NWCrbBroadcast>() {
        @Override
        public void handle(NWCrbBroadcast event) {
            logger.debug("NoWaitingCrb, handleNWCrbBroadcast " +
                    ((NWCrbMessage)(event.getDeliverEvent())).getMessage() + " " + "past: " + past);
            trigger(new RbBroadcast(new NWCrbDataMessage(selfAddress, event.getDeliverEvent(), new Vector<>(past))), rb);
            past.addElement(event.getDeliverEvent());
        }
    };

    private Handler<NWCrbDataMessage> handleNWCrbDataMessage = new Handler<NWCrbDataMessage>() {
        @Override
        public void handle(NWCrbDataMessage event) {
            logger.debug("NoWaitingCrb, handleNWCrbDataMessage " +
                    event.getSource() + " " + ((NWCrbMessage)(event.getDeliverEvent())).getMessage() + " " +
                    " mpast: " + event.getMpast() + " past: " + past);

            logger.trace("NoWaitingCrb, handleNWCrbDataMessage received message: " + event.getDeliverEvent());

            logger.trace("NoWaitingCrb, handleNWCrbDataMessage delivered: " + delivered);
            for (NWCrbDeliver d: delivered
                 ) {
                logger.trace("NoWaitingCrb, handleNWCrbDataMessage hashcode: " + d.hashCode());
            }

            if (delivered.contains(event.getDeliverEvent()) == false) {
                logger.trace("NoWaitingCrb, handleNWCrbDataMessage not delivered yet");
                for (NWCrbDeliver nwCrbDeliver: event.getMpast()
                     ) {
                    logger.trace("NoWaitingCrb, handleNWCrbDataMessage processing one message in mpast: " + nwCrbDeliver);
                    if (delivered.contains(nwCrbDeliver) == false) {
                        logger.trace("NoWaitingCrb, handleNWCrbDataMessage not delivered yet in mpast");
                        trigger(nwCrbDeliver, nwcrb);
                        delivered.add(nwCrbDeliver);
                        if (past.contains(nwCrbDeliver) == false) {
                            logger.trace("NoWaitingCrb, handleNWCrbDataMessage add into past");
                            past.addElement(nwCrbDeliver);
                        }
                    }
                }
                trigger(event.getDeliverEvent(), nwcrb);
                delivered.add(event.getDeliverEvent());
                if (past.contains(event.getDeliverEvent()) == false) {
                    past.addElement(event.getDeliverEvent());
                }
            }
        }
    };
}
