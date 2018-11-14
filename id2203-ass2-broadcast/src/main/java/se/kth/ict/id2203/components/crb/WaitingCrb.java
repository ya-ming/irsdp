package se.kth.ict.id2203.components.crb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.pa.broadcast.CrbMessage;
import se.kth.ict.id2203.ports.crb.CausalOrderReliableBroadcast;
import se.kth.ict.id2203.ports.crb.CrbBroadcast;
import se.kth.ict.id2203.ports.erb.RbBroadcast;
import se.kth.ict.id2203.ports.erb.EagerReliableBroadcast;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;

import java.util.*;

public class WaitingCrb extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(WaitingCrb.class);

	Negative<CausalOrderReliableBroadcast> crb = provides(CausalOrderReliableBroadcast.class);
	Positive<EagerReliableBroadcast> rb = requires(EagerReliableBroadcast.class);

	private final Address selfAddress;
	private final Set<Address> allAddresses;

	private Vector<Integer> v;
    private Integer lsn = 0;
	private HashSet<CrbDataMessage> pending;

	public WaitingCrb(WaitingCrbInit init) {
        selfAddress = init.getSelfAddress();
        allAddresses = init.getAllAddresses();
        v = new Vector<>();

        for (int i = 0; i < allAddresses.size(); i++) {
            v.addElement(0);
        }

        pending = new HashSet<CrbDataMessage>();

        subscribe(handleCrbBroadcast, crb);
        subscribe(handleCrbDataMessage, rb);
	}

	int rank(Address addr) {
//        logger.debug("WaitingCrb, rank " + addr.getId());
	    return addr.getId() - 1;
    }

    private Handler<CrbBroadcast> handleCrbBroadcast = new Handler<CrbBroadcast>() {
        @Override
        public void handle(CrbBroadcast event) {
            logger.debug("WaitingCrb, handleCrbBroadcast " +
                    ((CrbMessage)(event.getDeliverEvent())).getMessage() + " " + v.toString());

            Vector<Integer> w = new Vector<>(v);
//            logger.debug("WaitingCrb, w " + w.size());
            w.set(rank(selfAddress), lsn);
            lsn++;

            trigger(new RbBroadcast(new CrbDataMessage(selfAddress, event.getDeliverEvent(), w)), rb);
        }
    };

    private Handler<CrbDataMessage> handleCrbDataMessage = new Handler<CrbDataMessage>() {
        @Override
        public void handle(CrbDataMessage event) {
            logger.debug("WaitingCrb, handleCrbDataMessage " +
                    event.getSource() + " " + ((CrbMessage)(event.getDeliverEvent())).getMessage() + " " +
                    event.getV().toString() + " " + v);

            pending.add(event);

            boolean doWhile = true;

            while (doWhile) {
                doWhile = false;

                ArrayList<CrbDataMessage> messagesToDelete = new ArrayList<>();
                for (CrbDataMessage crbDataMessage : pending) {
                    if (crbDataMessage.lessOrEqualto(v)) {
                        messagesToDelete.add(crbDataMessage);

                        v.set(rank(crbDataMessage.getSource()), v.get(rank(crbDataMessage.getSource())) + 1);
                        doWhile = true; // modifed V, need another round of checking to see whether exists W' <= V
                        trigger(crbDataMessage.getDeliverEvent(), crb);
                    }
                }

                for (CrbDataMessage crbDataMessage : messagesToDelete) {
                    pending.remove(crbDataMessage);
                }
            }
        }
    };
}
